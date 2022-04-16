package WordNet;

import DataStructure.CounterHashMap;
import Dictionary.TurkishWordComparator;
import Dictionary.TxtDictionary;
import Dictionary.TxtWord;
import MorphologicalAnalysis.FsmMorphologicalAnalyzer;

import java.util.ArrayList;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PreviousWordNetTest {

    WordNet previuosWordNet;
    TxtDictionary previousDictionary;
    WordNet currentWordNet;

    protected double numberOfMatches(String definition1, String definition2){
        String[] items1, items2;
        double count = 0;
        items1 = definition1.split(" ");
        items2 = definition2.split(" ");
        for (int i = 0; i < items1.length; i++){
            for (int j = 0; j < items2.length; j++){
                if (items1[i].toLowerCase(new Locale("tr")).equals(items2[j].toLowerCase(new Locale("tr")))){
                    count += 2;
                    break;
                }
            }
        }
        return count / (items1.length + items2.length);
    }

    protected CounterHashMap<String> synSetIdCounts() {
        CounterHashMap<String> counts = new CounterHashMap<>();
        for (SynSet synSet : previuosWordNet.synSetList()){
            counts.put(synSet.getId().substring(0, 5));
        }
        return counts;
    }

    protected CounterHashMap<Integer> literalWordCounts() {
        CounterHashMap<Integer> counts = new CounterHashMap<>();
        for (String literal : previuosWordNet.literalList()){
            int count = 1;
            for (int i = 0; i < literal.length(); i++){
                if (literal.charAt(i) == ' '){
                    count++;
                }
            }
            counts.put(count);
        }
        return counts;
    }

    public void testExistenceOfKeNetSynSets(){
        boolean found = true;
        for (SynSet synSet : previuosWordNet.synSetList()){
            if (synSet.getId().startsWith("TUR10") && currentWordNet.getSynSetWithId(synSet.getId()) == null){
                System.out.println("SynSet with id " + synSet.getId() + " does not exist");
                found = false;
            }
        }
        assertTrue(found);
    }

    public void testExistenceOfPreviousSynSets(String year, String id){
        WordNet compared = new WordNet("turkish" + year + "_wordnet.xml", new Locale("tr"));
        boolean found = true;
        for (SynSet synSet : previuosWordNet.synSetList()){
            if (synSet.getId().startsWith(id) && compared.getSynSetWithId(synSet.getId()) == null){
                System.out.println("SynSet with id " + synSet.getId() + " does not exist");
                found = false;
            }
        }
        assertTrue(found);
    }

    private WordNet getWordNet(String id){
        switch (id){
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
            case "10":
            default:
                return new WordNet();
        }
    }

    public void findMatchingLiteralsInPreviousWordNets(String first, String second) {
        WordNet secondWordNet = getWordNet(second);
        for (SynSet synSet1 : previuosWordNet.synSetList()) {
            if (synSet1.getId().startsWith("TUR" + first + "-")) {
                for (SynSet synSet2 : secondWordNet.synSetList()) {
                    if (synSet2.getId().startsWith("TUR" + second + "-") && synSet1.getPos().equals(synSet2.getPos()) && previuosWordNet.getSynSetWithId(synSet2.getId()) == null) {
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
        for (SynSet synSet1 : previuosWordNet.synSetList()){
            if (synSet1.getId().startsWith("TUR" + first + "-")){
                for (SynSet synSet2: secondWordNet.synSetList()){
                    if (synSet2.getId().startsWith("TUR" + second + "-") && synSet1.getPos().equals(synSet2.getPos())){
                        double matchRatio = numberOfMatches(synSet1.getLongDefinition(), synSet2.getLongDefinition());
                        if (matchRatio >= 0.8){
                            System.out.println(matchRatio + "\t" + synSet1.getId() + "\t" + synSet2.getId() + "\t" + synSet1.getSynonym().toString() + "\t" + synSet2.getSynonym().toString() + "\t" + synSet1.getLongDefinition() + "\t" + synSet2.getLongDefinition());
                        }
                    }
                }
            }
        }
    }

    public void comparePosWithPosOfCorrespondingKeNetSynSets(){
        boolean found = true;
        for (SynSet synSet : previuosWordNet.synSetList()){
            if (synSet.getId().startsWith("TUR10")){
                SynSet synSet2 = currentWordNet.getSynSetWithId(synSet.getId());
                if (!synSet.getPos().equals(synSet2.getPos())){
                    System.out.println("SynSet " + synSet.getId() + " " + synSet.getSynonym() + " is " + synSet.getPos()
                            + " whereas in Kenet it is " + synSet2.getPos());
                    found = false;
                }
            }
        }
        assertTrue(found);
    }

    public void testDefinition() {
        FsmMorphologicalAnalyzer analyzer = new FsmMorphologicalAnalyzer(previousDictionary);
        for (SynSet synSet : previuosWordNet.synSetList()){
            if (!synSet.getLongDefinition().contains("DEFINITION")){
                String definition = synSet.getLongDefinition();
                String[] words = definition.split(" ");
                String notAnalyzed = "";
                for (String  word : words){
                    String newWord = word.replaceAll("`", "").replaceAll("!", "").replaceAll("\\?", "").replaceAll(",", "").replaceAll("\\(", "").replaceAll("\\)", "").replaceAll("\"", "").replaceAll("\\.", "").replaceAll(";", "").replaceAll(":", "");
                    if (!word.startsWith("-") && newWord.length() > 0 && analyzer.morphologicalAnalysis(newWord).size() == 0){
                        notAnalyzed += newWord + " ";
                    }
                }
                if (notAnalyzed.length() > 0){
                    System.out.println(synSet.getId() + "\t" + synSet.getSynonym().toString() + "\t" + synSet.getLongDefinition() + "\t" + notAnalyzed);
                }
            }
        }
    }

    public void generateExampleListForWordsHavingMultipleMeanings(){
        for (String name : previuosWordNet.literalList()){
            ArrayList<SynSet> synSets = previuosWordNet.getSynSetsWithLiteral(name);
            if (synSets.size() > 1){
                boolean example = true;
                for (SynSet synSet : synSets){
                    if (synSet.getExample() == null){
                        example = false;
                        break;
                    }
                }
                if (!example){
                    for (SynSet synSet : synSets){
                        System.out.println(synSet.getId() + "\t" + name + "\t" + synSet.getPos() + "\t" + synSet.getDefinition() + "\t" + synSet.getExample());
                    }
                }
            }
        }
    }

    public void testLiterals() {
        FsmMorphologicalAnalyzer analyzer = new FsmMorphologicalAnalyzer(previousDictionary);
        for (SynSet synSet : previuosWordNet.synSetList()){
            for (int i = 0; i < synSet.getSynonym().literalSize(); i++){
                String literal = synSet.getSynonym().getLiteral(i).getName();
                String[] words = literal.split(" ");
                String notAnalyzed = "";
                for (String  word : words){
                    String newWord = word.replaceAll("`", "").replaceAll("!", "").replaceAll("\\?", "").replaceAll(",", "").replaceAll("\\(", "").replaceAll("\\)", "").replaceAll("\"", "").replaceAll("\\.", "").replaceAll(";", "").replaceAll(":", "");
                    if (newWord.length() > 0 && analyzer.morphologicalAnalysis(newWord).size() == 0){
                        notAnalyzed += newWord + " ";
                    }
                }
                if (notAnalyzed.length() > 0){
                    System.out.println(synSet.getId() + "\t" + synSet.getSynonym().toString() + "\t" + notAnalyzed);
                }
            }
        }
    }

    public void testExample() {
        int count = 0;
        FsmMorphologicalAnalyzer analyzer = new FsmMorphologicalAnalyzer(previousDictionary);
        for (SynSet synSet : previuosWordNet.synSetList()){
            if (synSet.getExample() != null){
                String example = synSet.getExample();
                String[] words = example.split(" ");
                String notAnalyzed = "";
                for (String  word : words){
                    String newWord = word.replaceAll("`", "").replaceAll("!", "").replaceAll("\\?", "").replaceAll(",", "").replaceAll("\\(", "").replaceAll("\\)", "").replaceAll("\"", "").replaceAll("\\.", "").replaceAll(";", "").replaceAll(":", "");
                    if (!word.startsWith("-") && newWord.length() > 0 && analyzer.morphologicalAnalysis(newWord).size() == 0){
                        notAnalyzed += newWord + " ";
                    }
                }
                if (notAnalyzed.length() > 0){
                    count++;
                    System.out.println(synSet.getId() + "\t" + synSet.getSynonym().toString() + "\t" + synSet.getExample() + "\t" + notAnalyzed);
                }
            }
        }
        assertEquals(0, count);
    }

    public void possibleConversionErrorsForLiteralReplace(){
        FsmMorphologicalAnalyzer fsm = new FsmMorphologicalAnalyzer(previousDictionary);
        for (SynSet synSet : previuosWordNet.synSetList()){
            if (synSet.getExample() != null && synSet.getSynonym().literalSize() > 1){
                int count = 0;
                for (int i = 0; i < synSet.getSynonym().literalSize(); i++){
                    String newExample = synSet.getModifiedExample(synSet.getSynonym().getLiteral(i).getName(), fsm);
                    if (synSet.getExample().equals(newExample)){
                        count++;
                    }
                }
                if (count > 1){
                    System.out.println(synSet.getId() + "\t" + synSet.getDefinition() + "\t" + synSet.getSynonym() + "\t" + synSet.getExample());
                }
            }
        }
    }

    public void generateDictionary(String year){
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
        for (int i = 0; i < turkish.size(); i++){
            TxtWord txtWord = (TxtWord) turkish.getWord(i);
            if (txtWord.containsFlag("IS_OA")){
                dictionary.addProperNoun(txtWord.getName());
            }
            if (txtWord.containsFlag("IS_QUES")){
                dictionary.addWithFlag(txtWord.getName(), "IS_QUES");
            }
        }
        for (String literal : previuosWordNet.literalList()){
            if (!literal.contains(" ")){
                TxtWord txtWord = (TxtWord) turkish.getWord(literal);
                if (txtWord != null){
                    for (String flag: flags){
                        if (txtWord.containsFlag(flag)){
                            dictionary.addWithFlag(literal, flag);
                        }
                    }
                }
                if (literal.endsWith("mek") || literal.endsWith("mak")){
                    txtWord = (TxtWord) turkish.getWord(literal.substring(0, literal.length() - 3));
                    if (txtWord != null){
                        for (String flag: flags){
                            if (txtWord.containsFlag(flag)){
                                dictionary.addWithFlag(literal.substring(0, literal.length() - 3), flag);
                            }
                        }
                    }
                }
                ArrayList<SynSet> synSets = previuosWordNet.getSynSetsWithLiteral(literal);
                for (SynSet synSet : synSets){
                    switch (synSet.getPos()){
                        case NOUN:
                            dictionary.addNoun(literal);
                            break;
                        case VERB:
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
                if (words.length == 2){
                    if (words[0].equals(words[1])){
                        dictionary.addWithFlag(words[0], "IS_DUP");
                    } else {
                        if (!words[0].endsWith("mek") && !words[1].endsWith("mak") &&
                                words[0].length() > 3 && words[1].length() > 3 &&
                                words[0].substring(words[0].length() - 3).equals(words[1].substring(words[1].length() - 3))){
                            dictionary.addWithFlag(words[0], "IS_DUP");
                            dictionary.addWithFlag(words[1], "IS_DUP");
                        } else {
                            if (words[0].length() == words[1].length()){
                                int count = 0;
                                for (int j = 0; j < words[0].length(); j++){
                                    if (words[0].charAt(j) != words[1].charAt(j)){
                                        count++;
                                    }
                                }
                                if (count == 1){
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

    public void generateOfflineDictionary(){
        previuosWordNet.generateDictionary("sozluk.tex", new FsmMorphologicalAnalyzer(previousDictionary));
    }

}
