package wordle;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class WordleExec {

    Set<String> wordleDict;
    Set<Character> sureChars;
    Set<Character> blacklistChars;

    Map<Character, Set<String>> wordWithAChar;

    Set<String> shortlistedWords;

    final static String ANY = ".";

    WordleExec() {
        wordleDict = new HashSet<>();
        sureChars = new HashSet<>();
        wordWithAChar = new HashMap<>();
        shortlistedWords = new HashSet<>();
        blacklistChars = new HashSet<>();
    }

    private void readFile() throws IOException {
        File file = new File("input.txt");
        FileReader fr=new FileReader(file);   //reads the file
        BufferedReader br=new BufferedReader(fr);  //creates a buffering character input stream
        String line;
        while((line=br.readLine())!=null)
        {
            wordleDict.add(line);
        }
        fr.close();
    }

    private void loadInMemory() {
        for (String s : wordleDict) {
            for (Character c : s.toCharArray()) {
                Set<String> previousList = wordWithAChar.getOrDefault(c, new HashSet<>());
                previousList.add(s);
                wordWithAChar.put(c, previousList);
            }
        }
    }

    private String buildPattern(Character c, int pos) {
        StringBuilder pattern = new StringBuilder();

        for (int i = 1; i <= 5; i++) {
            if (i == pos) {
                pattern.append(c);
            } else {
                pattern.append(ANY);
            }
        }

        return pattern.toString();
    }

    private void loadWordWithFixedPosition(Character c, int pos) {
        sureChars.add(c);
        shortlist();
        shortlistedWords =
                shortlistedWords.stream().filter(e -> e.matches(buildPattern(c, pos))).collect(Collectors.toSet());
    }

    private void loadWordWithBlacklistPosition(Character c, int pos) {
        sureChars.add(c);
        shortlist();
        shortlistedWords =
                shortlistedWords.stream().filter(e -> !e.matches(buildPattern(c, pos))).collect(Collectors.toSet());
    }

    private void optionalChars(Character c) {
        sureChars.add(c);
        shortlist();
    }

    private void shortlist() {
        for (Character c : sureChars) {
            Set<String> candidate = wordWithAChar.get(c);
            if (shortlistedWords.isEmpty()) {
                shortlistedWords.addAll(candidate);
            } else {
                shortlistedWords.retainAll(candidate);
            }
        }

        for (Character c : blacklistChars) {
            Set<String> blackList = wordWithAChar.get(c);
            shortlistedWords.removeAll(blackList);
        }
    }

    private void addBlacklistChars(Character c) {
        Set<String> wordsToRemove = wordWithAChar.get(c);
        wordleDict.removeAll(wordsToRemove);
        blacklistChars.add(c);
        shortlist();
    }

    private void showUsage() {
        System.out.println("Usage :");
        System.out.println("To Whitelist a char: +<char to whitelist followed by position (1 indexed)\nex: +a or +a5");
        System.out.println("To blacklist a char: -<char to whitelist followed by position (1 indexed)\nex: -a or -a5");
        System.out.println("To Show the complete list: *\nex: *");
        System.out.println("To Suggest any random: %, *\nex: %");
        System.out.println("To start a new game: n, *\nex: n");
        System.out.println("This helptext: h, *\nex: h");
    }

    static private WordleExec newGame() throws IOException {
        WordleExec wordleExec = new WordleExec();
        wordleExec.readFile();
        wordleExec.loadInMemory();
        wordleExec.showUsage();

        return wordleExec;
    }

    public static void main(String[] args) throws IOException {

        WordleExec wordleExec = newGame();

        Scanner sc = new Scanner(System.in);

        while(true) {
            char[] userInput = sc.next().toCharArray();
            if (userInput[0] == '+') {
                if (userInput.length >= 3) {
                    wordleExec.loadWordWithFixedPosition(userInput[1], userInput[2] - '0');
                } else {
                    wordleExec.optionalChars(userInput[1]);
                }
            } else if (userInput[0] == '-') {
                if (userInput.length >= 3) {
                    wordleExec.loadWordWithBlacklistPosition(userInput[1], userInput[2] - '0');
                } else {
                    wordleExec.addBlacklistChars(userInput[1]);
                }
            } else if (userInput[0] == '*') {
                for (String s : wordleExec.shortlistedWords) {
                    System.out.println(s);
                }
            } else if (userInput[0] == '%') {
                Set<String> setToAct = wordleExec.shortlistedWords.size() != 0 ?
                        wordleExec.shortlistedWords : wordleExec.wordleDict;
                int random = new Random().nextInt(setToAct.size());
                int i = 0;
                for (String s : setToAct) {
                    if (i++ == random) {
                        System.out.println(s);
                    }
                }
            } else if (userInput[0] == 'n') {
                System.out.println("Starting new game");
                wordleExec = newGame();
            } else if (userInput[0] == 'h') {
                wordleExec.showUsage();
            }
        }

    }
}
