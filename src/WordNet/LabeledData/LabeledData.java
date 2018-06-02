package WordNet.LabeledData;

import DataStructure.CounterHashMap;
import WordNet.*;

import java.io.*;
import java.util.HashMap;
import java.util.Locale;

public class LabeledData {
    protected HashMap<WordPair, CounterHashMap<WordPair>> data;

    public LabeledData(){
        data = new HashMap<>();
    }

    private void addFile(String fileName, Locale locale){
        CounterHashMap<WordPair> labeledRow;
        WordPair wordPair;
        String leftWord, rightWord, leftId, rightId;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "UTF8"));
            String line = br.readLine();
            while (line != null){
                if (!line.contains("->")){
                    System.out.println("File " + fileName + " does not contain ->");
                } else {
                    String[] words = line.split("\\->");
                    if (words.length != 2){
                        System.out.println("File " + fileName + " does not contain correct labeling");
                    } else {
                        if (words[0].contains("(") && words[0].contains(")")){
                            String[] parts = words[0].split("\\(|\\)");
                            leftWord = parts[0].toLowerCase(locale);
                            leftId = parts[1];
                        } else {
                            leftWord = words[0].toLowerCase(locale);
                            leftId = null;
                        }
                        if (words[1].contains("(") && words[1].contains(")")){
                            String[] parts = words[1].split("\\(|\\)");
                            rightWord = parts[0].toLowerCase(locale);
                            rightId = parts[1];
                        } else {
                            rightWord = words[1].toLowerCase(locale);
                            rightId = null;
                        }
                        if (leftWord.compareTo(rightWord) > 0){
                            String tmp = leftWord;
                            leftWord = rightWord;
                            rightWord = tmp;
                            tmp = leftId;
                            leftId = rightId;
                            rightId = tmp;
                        }
                        wordPair = new WordPair(leftWord, rightWord);
                        if (data.containsKey(wordPair)){
                            labeledRow = data.get(wordPair);
                        } else {
                            labeledRow = new CounterHashMap<>();
                            data.put(wordPair, labeledRow);
                        }
                        if (leftId != null && rightId != null){
                            labeledRow.put(new WordPair(leftId, rightId));
                        } else {
                            labeledRow.put(new WordPair());
                        }
                    }
                }
                line = br.readLine();
            }
            br.close();
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        }
    }

    private void addFile(String fileName, Locale leftLocale, Locale rightLocale, WordNet rightWordNet, IdMapping mapping){
        CounterHashMap<WordPair> labeledRow;
        WordPair wordPair;
        String leftWord, rightWord, leftId, rightId;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "UTF8"));
            String line = br.readLine();
            while (line != null){
                if (!line.contains("->")){
                    System.out.println("File " + fileName + " does not contain ->");
                } else {
                    String[] words = line.split("\\->");
                    if (words.length != 2){
                        System.out.println("File " + fileName + " does not contain correct labeling");
                    } else {
                        if (words[0].contains("(") && words[0].contains(")")){
                            String[] parts = words[0].split("\\(|\\)");
                            leftWord = parts[0].toLowerCase(leftLocale);
                            leftId = parts[1];
                        } else {
                            leftWord = words[0].toLowerCase(leftLocale);
                            leftId = null;
                        }
                        if (words[1].contains("(") && words[1].contains(")")){
                            String[] parts = words[1].split("\\(|\\)");
                            rightWord = parts[0].toLowerCase(rightLocale);
                            rightId = parts[1];
                            if (rightWordNet.getSynSetWithId(rightId) == null){
                                rightId = mapping.map(rightId);
                            }
                        } else {
                            rightWord = words[1].toLowerCase(rightLocale);
                            rightId = null;
                        }
                        wordPair = new WordPair(leftWord, rightWord);
                        if (data.containsKey(wordPair)){
                            labeledRow = data.get(wordPair);
                        } else {
                            labeledRow = new CounterHashMap<>();
                            data.put(wordPair, labeledRow);
                        }
                        if (leftId != null && rightId != null){
                            labeledRow.put(new WordPair(leftId, rightId));
                        } else {
                            labeledRow.put(new WordPair());
                        }
                    }
                }
                line = br.readLine();
            }
            br.close();
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        }
    }

    public void addPath(String path, Locale locale){
        File[] listOfFiles = new File(path).listFiles();
        for (File file : listOfFiles){
            addFile(file.getAbsolutePath(), locale);
        }
    }

    public void addPath(String path, Locale leftLocale, Locale rightLocale, WordNet rightWordNet){
        File[] listOfFiles = new File(path).listFiles();
        IdMapping idMapping = new IdMapping("Data/Wordnet/mapping.txt");
        for (File file : listOfFiles){
            addFile(file.getAbsolutePath(), leftLocale, rightLocale, rightWordNet, idMapping);
        }
    }
}
