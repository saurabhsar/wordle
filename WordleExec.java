package wordle;

import java.io.*;
import java.util.*;

public class WordleExec {

    Set<String> wordleDict;
    Set<Character> sureChars;
    Map<Character, Integer> charWithPos;
    Set<Character> blacklistChars;

    Map<Character, Set<String>> wordWithAChar;

    Set<String> shortlistedChars;

    static class WordWithPos {
        String word;
        int position;
    }

    WordleExec() {
        wordleDict = new HashSet<>();
        sureChars = new HashSet<>();
        wordWithAChar = new HashMap<>();
        shortlistedChars = new HashSet<>();
        blacklistChars = new HashSet<>();
        charWithPos = new HashMap<>();
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

    private void loadWordWithFixedPosition(Character c, int pos) {
        sureChars.add(c);
        charWithPos.put(c, pos);
    }

    private void optionalChars(Character c) {
        sureChars.add(c);
    }

    private void shortListWithPos() {
        Set<String> strToRemove = new HashSet<>();
        for (Map.Entry<Character, Integer> entry : charWithPos.entrySet()) {
            for (String s : shortlistedChars) {
                if (!entry.getKey().equals(s.charAt(entry.getValue()))) {
                    strToRemove.add(s);
                }
            }
        }

        shortlistedChars.removeAll(strToRemove);
    }

    private void shortlist() {
        for (Character c : sureChars) {
            Set<String> candidate = wordWithAChar.get(c);
            if (shortlistedChars.isEmpty()) {
                shortlistedChars.addAll(candidate);
            } else {
                shortlistedChars.retainAll(candidate);
            }
        }

        for (Character c : blacklistChars) {
            Set<String> blackList = wordWithAChar.get(c);
            shortlistedChars.removeAll(blackList);
        }
    }

    private void addBlacklistChars(Character c) {
        blacklistChars.add(c);
    }

    private void view() {
        System.out.println("abc");
    }

    public static void main(String[] args) throws IOException {

        WordleExec wordleExec = new WordleExec();
        wordleExec.readFile();
        wordleExec.loadInMemory();

        Scanner sc = new Scanner(System.in);

        while(true) {
            char[] userInput = sc.next().toCharArray();
            if (userInput[0] == '+') {
                wordleExec.optionalChars(userInput[1]);
            } else if (userInput[0] == '-') {
                wordleExec.addBlacklistChars(userInput[1]);
            } else if (userInput[0] == '=') {
                wordleExec.loadWordWithFixedPosition(userInput[1], userInput[2] - '0');
            } else if (userInput[0] == '*') {
                for (String s : wordleExec.shortlistedChars) {
                    System.out.println(s);
                }
            }

            wordleExec.shortlist();
//            wordleExec.shortListWithPos();

            wordleExec.view();
        }

    }
}
