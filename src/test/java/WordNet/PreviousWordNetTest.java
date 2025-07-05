package WordNet;

import DataStructure.CounterHashMap;
import Dictionary.Pos;
import Dictionary.TurkishWordComparator;
import Dictionary.TxtDictionary;
import Dictionary.TxtWord;
import MorphologicalAnalysis.FsmMorphologicalAnalyzer;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.Collator;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PreviousWordNetTest {

    WordNet previousWordNet;
    TxtDictionary previousDictionary;
    WordNet currentWordNet;

    public void printDictionaryToCheck(){
        ArrayList<String> output = new ArrayList<>();
        WordNet wordNet = new WordNet();
        for (SynSet synSet : wordNet.synSetList()){
            for (int i = 0; i < synSet.getSynonym().literalSize(); i++){
                Literal literal = synSet.getSynonym().getLiteral(i);
                output.add(literal.getName() + "\t" + synSet.getId() + "\t" + synSet.getPos() + "\t" + synSet.getLongDefinition() + "\t" + synSet.getExample());
            }
        }
        for (int i = 0; i < 10; i++) {
            WordNet old = getWordNet("0" + i);
            for (SynSet synSet : old.synSetList()){
                if (wordNet.getSynSetWithId(synSet.getId()) == null){
                    for (int j = 0; j < synSet.getSynonym().literalSize(); j++){
                        Literal literal = synSet.getSynonym().getLiteral(j);
                        output.add(literal.getName() + "\t" + synSet.getId() + "\t" + synSet.getPos() + "\t" + synSet.getLongDefinition() + "\t" + synSet.getExample());
                    }
                }
            }
        }
        output.sort(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                Locale locale = new Locale("tr");
                Collator collator = Collator.getInstance(locale);
                return collator.compare(o1, o2);
            }
        });
        for (String s : output){
            System.out.println(s);
        }
    }

    public void remove() throws FileNotFoundException {
        Scanner input = new Scanner(new File("tobedeleted.txt"));
        WordNet[] w = new WordNet[10];
        for (int i = 0; i < 10; i++) {
            w[i] = getWordNet("0" + i);
        }
        while (input.hasNextLine()) {
            String id = input.nextLine();
            for (int i = 0; i < 10; i++) {
                SynSet synSet = w[i].getSynSetWithId(id);
                if (synSet != null) {
                    if (synSet.getSynonym().literalSize() == 1){
                        w[i].removeSynSet(synSet);
                    } else {
                        System.out.println(id);
                    }
                }
            }
        }
        for (int i = 0; i < 10; i++) {
            w[i].saveAsXml(i + ".txt");
        }
    }

    public void createNewWordNet() throws FileNotFoundException {
        WordNet turkish = new WordNet();
        WordNet newWordNet = new WordNet("", new Locale("tr"));
        HashMap<String, String> data = new HashMap<>();
        Scanner sc = new Scanner(new File("wordnet.txt"));
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            String[] items = line.split("\t");
            SynSet synSet;
            Pos pos = null;
            switch (items[3]) {
                case "NOUN":
                    pos = Pos.NOUN;
                    break;
                case "VERB":
                    pos = Pos.VERB;
                    break;
                case "ADJECTIVE":
                    pos = Pos.ADJECTIVE;
                    break;
                case "ADVERB":
                    pos = Pos.ADVERB;
                    break;
                case "PREPOSITION":
                    pos = Pos.PREPOSITION;
                    break;
                case "INTERJECTION":
                    pos = Pos.INTERJECTION;
                    break;
                case "CONJUNCTION":
                    pos = Pos.CONJUNCTION;
                    break;
                case "PRONOUN":
                    pos = Pos.PRONOUN;
                    break;
            }
            if (newWordNet.getSynSetWithId(items[2]) != null) {
                synSet = newWordNet.getSynSetWithId(items[2]);
                if (!synSet.getPos().equals(pos)) {
                    System.out.println("Pos mismatch for " + line);
                }
                if (!items[0].isEmpty() && !synSet.getDefinition().equals(items[0])) {
                    System.out.println("Error: SynSet Definition is different for id " + items[2]);
                }
            } else {
                synSet = new SynSet(items[2]);
                synSet.setPos(pos);
                if (items[0].isEmpty()) {
                    synSet.setDefinition("NO DEFINITION");
                } else {
                    synSet.setDefinition(items[0]);
                }
                SynSet old = turkish.getSynSetWithId(items[2]);
                if (old != null) {
                    synSet.setExample(old.getExample());
                    if (!old.getPos().equals(synSet.getPos())) {
                        System.out.println("Pos not equal for " + line + " " + old.getPos());
                    }
                }
                newWordNet.addSynSet(synSet);
            }
            ArrayList result = newWordNet.getLiteralsWithName(items[1]);
            Literal literal = new Literal(items[1], result.size() + 1, items[2]);
            newWordNet.addLiteralToLiteralList(literal);
            synSet.addLiteral(literal);
        }
        newWordNet.saveAsXml("new_wordnet.xml");
    }

    protected double numberOfMatches(String definition1, String definition2) {
        String[] items1, items2;
        double count = 0;
        items1 = definition1.split(" ");
        items2 = definition2.split(" ");
        for (int i = 0; i < items1.length; i++) {
            for (int j = 0; j < items2.length; j++) {
                if (items1[i].toLowerCase(new Locale("tr")).equals(items2[j].toLowerCase(new Locale("tr")))) {
                    count += 2;
                    break;
                }
            }
        }
        return count / (items1.length + items2.length);
    }

    protected CounterHashMap<String> synSetIdCounts() {
        CounterHashMap<String> counts = new CounterHashMap<>();
        for (SynSet synSet : previousWordNet.synSetList()) {
            counts.put(synSet.getId().substring(0, 5));
        }
        return counts;
    }

    protected CounterHashMap<Integer> literalWordCounts() {
        CounterHashMap<Integer> counts = new CounterHashMap<>();
        for (String literal : previousWordNet.literalList()) {
            int count = 1;
            for (int i = 0; i < literal.length(); i++) {
                if (literal.charAt(i) == ' ') {
                    count++;
                }
            }
            counts.put(count);
        }
        return counts;
    }

    public void testExistenceOfKeNetSynSets() {
        boolean found = true;
        for (SynSet synSet : previousWordNet.synSetList()) {
            if (synSet.getId().startsWith("TUR10") && currentWordNet.getSynSetWithId(synSet.getId()) == null) {
                System.out.println("SynSet with id " + synSet.getId() + " does not exist");
                found = false;
            }
        }
        assertTrue(found);
    }

    public void testExistenceOfPreviousSynSets(String year, String id) {
        WordNet compared = getWordNet(id.substring(3));
        boolean found = true;
        for (SynSet synSet : previousWordNet.synSetList()) {
            if (synSet.getId().startsWith(id) && compared.getSynSetWithId(synSet.getId()) == null) {
                System.out.println("SynSet with id " + synSet.getId() + " does not exist");
                found = false;
            }
        }
        assertTrue(found);
    }



    private WordNet getWordNet(String id) {
        switch (id) {
            case "00":
                return new WordNet("turkish1901_wordnet.xml", new Locale("tr"));
            case "01":
                return new WordNet("turkish1944_wordnet.xml", new Locale("tr"));
            case "02":
                return new WordNet("turkish1955_wordnet.xml", new Locale("tr"));
            case "03":
                return new WordNet("turkish1959_wordnet.xml", new Locale("tr"));
            case "04":
                return new WordNet("turkish1966_wordnet.xml", new Locale("tr"));
            case "05":
                return new WordNet("turkish1969_wordnet.xml", new Locale("tr"));
            case "06":
                return new WordNet("turkish1974_wordnet.xml", new Locale("tr"));
            case "07":
                return new WordNet("turkish1983_wordnet.xml", new Locale("tr"));
            case "08":
                return new WordNet("turkish1988_wordnet.xml", new Locale("tr"));
            case "09":
                return new WordNet("turkish1998_wordnet.xml", new Locale("tr"));
            case "10":
            default:
                return new WordNet();
        }
    }

    public void findMatchingLiteralsInPreviousWordNets(String first, String second) {
        WordNet secondWordNet = getWordNet(second);
        for (SynSet synSet1 : previousWordNet.synSetList()) {
            if (synSet1.getId().startsWith("TUR" + first + "-")) {
                for (SynSet synSet2 : secondWordNet.synSetList()) {
                    if (synSet2.getId().startsWith("TUR" + second + "-") && synSet1.getPos().equals(synSet2.getPos()) && previousWordNet.getSynSetWithId(synSet2.getId()) == null) {
                        for (int i = 0; i < synSet1.getSynonym().literalSize(); i++) {
                            String literal1 = synSet1.getSynonym().getLiteral(i).getName();
                            for (int j = 0; j < synSet2.getSynonym().literalSize(); j++) {
                                String literal2 = synSet2.getSynonym().getLiteral(j).getName();
                                if (literal1.equalsIgnoreCase(literal2)) {
                                    System.out.println(literal1 + "\t" + synSet1.getId() + "\t" + synSet1.getPos() + "\t" + synSet1.getLongDefinition());
                                    System.out.println(literal1 + "\t" + synSet2.getId() + "\t" + synSet2.getPos() + "\t" + synSet2.getLongDefinition());
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void findMatchingSynSetsInPreviousWordNets(String first, String second) {
        WordNet secondWordNet = getWordNet(second);
        for (SynSet synSet1 : previousWordNet.synSetList()) {
            if (synSet1.getId().startsWith("TUR" + first + "-")) {
                for (SynSet synSet2 : secondWordNet.synSetList()) {
                    if (synSet2.getId().startsWith("TUR" + second + "-") && synSet1.getPos().equals(synSet2.getPos())) {
                        double matchRatio = numberOfMatches(synSet1.getLongDefinition(), synSet2.getLongDefinition());
                        if (matchRatio >= 0.8) {
                            System.out.println(matchRatio + "\t" + synSet1.getId() + "\t" + synSet2.getId() + "\t" + synSet1.getSynonym().toString() + "\t" + synSet2.getSynonym().toString() + "\t" + synSet1.getLongDefinition() + "\t" + synSet2.getLongDefinition());
                        }
                    }
                }
            }
        }
    }

    public void comparePosWithPosOfCorrespondingKeNetSynSets() {
        boolean found = true;
        for (SynSet synSet : previousWordNet.synSetList()) {
            if (synSet.getId().startsWith("TUR10")) {
                SynSet synSet2 = currentWordNet.getSynSetWithId(synSet.getId());
                if (!synSet.getPos().equals(synSet2.getPos())) {
                    System.out.println("SynSet " + synSet.getId() + " " + synSet.getSynonym() + " is " + synSet.getPos()
                            + " whereas in Kenet it is " + synSet2.getPos());
                    found = false;
                }
            }
        }
        assertTrue(found);
    }

    private String candidate(String word, FsmMorphologicalAnalyzer analyzer) {
        String result = "";
        String tmp;
        for (int i = 0; i < word.length(); i++) {
            switch (word.charAt(i)) {
                case 'ı':
                case 'i':
                    tmp = result + "î";
                    if (analyzer.morphologicalAnalysis(tmp + word.substring(i + 1)).size() > 0) {
                        return tmp + word.substring(i + 1);
                    }
                    break;
                case 'î':
                    tmp = result + "i";
                    if (analyzer.morphologicalAnalysis(tmp + word.substring(i + 1)).size() > 0) {
                        return tmp + word.substring(i + 1);
                    }
                    break;
                case 'a':
                    tmp = result + "â";
                    if (analyzer.morphologicalAnalysis(tmp + word.substring(i + 1)).size() > 0) {
                        return tmp + word.substring(i + 1);
                    }
                    break;
                case 'A':
                    tmp = result + "Â";
                    if (analyzer.morphologicalAnalysis(tmp + word.substring(i + 1)).size() > 0) {
                        return tmp + word.substring(i + 1);
                    }
                    break;
                case 'â':
                    tmp = result + "a";
                    if (analyzer.morphologicalAnalysis(tmp + word.substring(i + 1)).size() > 0) {
                        return tmp + word.substring(i + 1);
                    }
                    break;
                case 'u':
                case 'ü':
                    tmp = result + "û";
                    if (analyzer.morphologicalAnalysis(tmp + word.substring(i + 1)).size() > 0) {
                        return tmp + word.substring(i + 1);
                    }
                    break;
                case 'û':
                    tmp = result + "ü";
                    if (analyzer.morphologicalAnalysis(tmp + word.substring(i + 1)).size() > 0) {
                        return tmp + word.substring(i + 1);
                    }
                    break;
            }
            result = result + word.charAt(i);
        }
        for (int i = 3; i < word.length() - 2; i++) {
            if (analyzer.morphologicalAnalysis(word.substring(0, i)).size() > 0 && analyzer.morphologicalAnalysis(word.substring(i)).size() > 0) {
                return word.substring(0, i) + " " + word.substring(i);
            }
        }
        return "";
    }

    public void testDefinition() {
        FsmMorphologicalAnalyzer analyzer = new FsmMorphologicalAnalyzer(previousDictionary);
        for (SynSet synSet : previousWordNet.synSetList()) {
            if (!synSet.getLongDefinition().contains("DEFINITION")) {
                String definition = synSet.getLongDefinition();
                String[] words = definition.split(" ");
                String notAnalyzed = "";
                String candidates = "";
                for (String word : words) {
                    String newWord = word.replaceAll("`", "").replaceAll("!", "").replaceAll("\\?", "").replaceAll(",", "").replaceAll("\\(", "").replaceAll("\\)", "").replaceAll("\"", "").replaceAll("\\.", "").replaceAll(";", "").replaceAll(":", "");
                    if (!word.startsWith("-") && newWord.length() > 0 && analyzer.morphologicalAnalysis(newWord).size() == 0) {
                        notAnalyzed += newWord + " ";
                        String candidate = candidate(newWord, analyzer);
                        if (!candidate.isEmpty()) {
                            candidates += candidate + " ";
                            definition = definition.replaceAll(newWord, candidate);
                        }
                    }
                }
                if (notAnalyzed.length() > 0) {
                    System.out.println(synSet.getId() + "\t" + synSet.getSynonym().toString() + "\t" + definition + "\t" + notAnalyzed + "\t" + candidates);
                }
            }
        }
    }

    public void testExample() {
        int count = 0;
        FsmMorphologicalAnalyzer analyzer = new FsmMorphologicalAnalyzer(previousDictionary);
        for (SynSet synSet : previousWordNet.synSetList()) {
            if (synSet.getExample() != null) {
                String example = synSet.getExample();
                String[] words = example.split(" ");
                String notAnalyzed = "";
                String candidates = "";
                for (String word : words) {
                    String newWord = word.replaceAll("`", "").replaceAll("!", "").replaceAll("\\?", "").replaceAll(",", "").replaceAll("\\(", "").replaceAll("\\)", "").replaceAll("\"", "").replaceAll("\\.", "").replaceAll(";", "").replaceAll(":", "");
                    if (!word.startsWith("-") && newWord.length() > 0 && analyzer.morphologicalAnalysis(newWord).size() == 0) {
                        notAnalyzed += newWord + " ";
                        String candidate = candidate(newWord, analyzer);
                        if (!candidate.isEmpty()) {
                            candidates += candidate + " ";
                            example = example.replaceAll(newWord, candidate);
                        }
                    }
                }
                if (notAnalyzed.length() > 0) {
                    count++;
                    System.out.println(synSet.getId() + "\t" + synSet.getSynonym().toString() + "\t" + example + "\t" + notAnalyzed + "\t" + candidates);
                }
            }
        }
        assertEquals(0, count);
    }

    public void generateExampleListForWordsHavingMultipleMeanings() {
        for (String name : previousWordNet.literalList()) {
            ArrayList<SynSet> synSets = previousWordNet.getSynSetsWithLiteral(name);
            if (synSets.size() > 1) {
                boolean example = true;
                for (SynSet synSet : synSets) {
                    if (synSet.getExample() == null) {
                        example = false;
                        break;
                    }
                }
                if (!example) {
                    for (SynSet synSet : synSets) {
                        System.out.println(synSet.getId() + "\t" + name + "\t" + synSet.getPos() + "\t" + synSet.getDefinition() + "\t" + synSet.getExample());
                    }
                }
            }
        }
    }

    public void testLiterals() {
        FsmMorphologicalAnalyzer analyzer = new FsmMorphologicalAnalyzer(previousDictionary);
        for (SynSet synSet : previousWordNet.synSetList()) {
            for (int i = 0; i < synSet.getSynonym().literalSize(); i++) {
                String literal = synSet.getSynonym().getLiteral(i).getName();
                String[] words = literal.split(" ");
                String notAnalyzed = "";
                for (String word : words) {
                    String newWord = word.replaceAll("`", "").replaceAll("!", "").replaceAll("\\?", "").replaceAll(",", "").replaceAll("\\(", "").replaceAll("\\)", "").replaceAll("\"", "").replaceAll("\\.", "").replaceAll(";", "").replaceAll(":", "");
                    if (newWord.length() > 0 && analyzer.morphologicalAnalysis(newWord).size() == 0) {
                        notAnalyzed += newWord + " ";
                    }
                }
                if (notAnalyzed.length() > 0) {
                    System.out.println(synSet.getId() + "\t" + synSet.getSynonym().toString() + "\t" + notAnalyzed);
                }
            }
        }
    }

    public void possibleConversionErrorsForLiteralReplace() {
        FsmMorphologicalAnalyzer fsm = new FsmMorphologicalAnalyzer(previousDictionary);
        for (SynSet synSet : previousWordNet.synSetList()) {
            if (synSet.getExample() != null && synSet.getSynonym().literalSize() > 1) {
                int count = 0;
                for (int i = 0; i < synSet.getSynonym().literalSize(); i++) {
                    String newExample = synSet.getModifiedExample(synSet.getSynonym().getLiteral(i).getName(), fsm);
                    if (synSet.getExample().equals(newExample)) {
                        count++;
                    }
                }
                if (count > 1) {
                    System.out.println(synSet.getId() + "\t" + synSet.getDefinition() + "\t" + synSet.getSynonym() + "\t" + synSet.getExample());
                }
            }
        }
    }

    public void generateDictionary(String year) {
        String[] flags = {"IS_SD", "IS_KG", "IS_UD", "IS_UU", "IS_UUU",
                "IS_SU", "IS_ST", "F_SD", "F_GUD", "F_GUDO", "IS_SDD",
                "F1P1", "F2P1", "F2PL", "F2P1-NO-REF", "F3P1-NO-REF",
                "F4P1-NO-REF", "F4PR-NO-REF", "F4PL-NO-REF", "F4PW-NO-REF", "F5PL-NO-REF",
                "F5PR-NO-REF", "F5PW-NO-REF", "F2P1", "F3P1", "F4P1",
                "F4PR", "F4PL", "F4PW", "F5P1", "F5PL",
                "F5PR", "F5PW", "F6P1", "IS_KU", "IS_BILEŞ",
                "IS_B_SD", "IS_KI", "IS_STT", "IS_UDD", "IS_CA", "IS_KIS",
                "IS_EX", "CL_NONE", "IS_B_SI", "IS_SAYI"};
        TxtDictionary turkish = new TxtDictionary();
        TxtDictionary dictionary = new TxtDictionary(new TurkishWordComparator());
        for (int i = 0; i < turkish.size(); i++) {
            TxtWord txtWord = (TxtWord) turkish.getWord(i);
            if (txtWord.containsFlag("IS_OA")) {
                dictionary.addProperNoun(txtWord.getName());
            }
            if (txtWord.containsFlag("IS_QUES")) {
                dictionary.addWithFlag(txtWord.getName(), "IS_QUES");
            }
        }
        for (String literal : previousWordNet.literalList()) {
            if (!literal.contains(" ")) {
                TxtWord txtWord = (TxtWord) turkish.getWord(literal);
                if (txtWord != null) {
                    for (String flag : flags) {
                        if (txtWord.containsFlag(flag)) {
                            dictionary.addWithFlag(literal, flag);
                        }
                    }
                }
                if (literal.length() > 3 && literal.endsWith("mek") || literal.endsWith("mak")) {
                    txtWord = (TxtWord) turkish.getWord(literal.substring(0, literal.length() - 3));
                    if (txtWord != null) {
                        for (String flag : flags) {
                            if (txtWord.containsFlag(flag)) {
                                dictionary.addWithFlag(literal.substring(0, literal.length() - 3), flag);
                            }
                        }
                    }
                }
                ArrayList<SynSet> synSets = previousWordNet.getSynSetsWithLiteral(literal);
                for (SynSet synSet : synSets) {
                    switch (synSet.getPos()) {
                        case NOUN:
                            dictionary.addNoun(literal);
                            break;
                        case VERB:
                            if (!literal.endsWith("mek") && !literal.endsWith("mak")) {
                                System.out.println("Error in verb " + literal);
                            }
                            dictionary.addVerb(literal.substring(0, literal.length() - 3));
                            break;
                        case ADJECTIVE:
                            dictionary.addAdjective(literal);
                            break;
                        case ADVERB:
                            dictionary.addAdverb(literal);
                            break;
                        case PRONOUN:
                            dictionary.addPronoun(literal);
                            break;
                        case CONJUNCTION:
                            dictionary.addWithFlag(literal, "IS_CONJ");
                            break;
                        case INTERJECTION:
                            dictionary.addWithFlag(literal, "IS_INTERJ");
                            break;
                        case PREPOSITION:
                            dictionary.addWithFlag(literal, "IS_POSTP");
                            break;
                    }
                }
            } else {
                String[] words = literal.split(" ");
                if (words.length == 2) {
                    if (words[0].equals(words[1])) {
                        dictionary.addWithFlag(words[0], "IS_DUP");
                    } else {
                        if (!words[0].endsWith("mek") && !words[1].endsWith("mak") &&
                                words[0].length() > 3 && words[1].length() > 3 &&
                                words[0].substring(words[0].length() - 3).equals(words[1].substring(words[1].length() - 3))) {
                            dictionary.addWithFlag(words[0], "IS_DUP");
                            dictionary.addWithFlag(words[1], "IS_DUP");
                        } else {
                            if (words[0].length() == words[1].length()) {
                                int count = 0;
                                for (int j = 0; j < words[0].length(); j++) {
                                    if (words[0].charAt(j) != words[1].charAt(j)) {
                                        count++;
                                    }
                                }
                                if (count == 1) {
                                    dictionary.addWithFlag(words[0], "IS_DUP");
                                    dictionary.addWithFlag(words[1], "IS_DUP");
                                }
                            }
                        }
                    }
                }
            }
        }
        dictionary.saveAsTxt("turkish" + year + "_dictionary.txt");
    }

    public void generateOfflineDictionary() {
        previousWordNet.generateDictionary("sozluk.tex", new FsmMorphologicalAnalyzer(previousDictionary));
    }

}
