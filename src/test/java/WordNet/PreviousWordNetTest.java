package WordNet;

import Dictionary.TurkishWordComparator;
import Dictionary.TxtDictionary;
import Dictionary.TxtWord;
import MorphologicalAnalysis.FsmMorphologicalAnalyzer;

import java.util.ArrayList;
import java.util.Locale;

public class PreviousWordNetTest {

    WordNet previuosWordNet;
    TxtDictionary previousDictionary;

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

    public void testExample() {
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
                    System.out.println(synSet.getId() + "\t" + synSet.getSynonym().toString() + "\t" + synSet.getExample() + "\t" + notAnalyzed);
                }
            }
        }
    }

    public void generateDictionary(String year){
        String[] flags = {"IS_SD", "IS_KG", "IS_UD", "IS_UU", "IS_UUU",
                "IS_SU", "IS_ST", "F_SD", "F_GUD", "F_GUDO",
                "F1P1", "F2P1", "F2PL", "F2P1-NO-REF", "F3P1-NO-REF",
                "F4P1-NO-REF", "F4PR-NO-REF", "F4PL-NO-REF", "F4PW-NO-REF", "F5PL-NO-REF",
                "F5PR-NO-REF", "F5PW-NO-REF", "F2P1", "F3P1", "F4P1",
                "F4PR", "F4PL", "F4PW", "F5P1", "F5PL",
                "F5PR", "F5PW", "F6P1"};
        TxtDictionary turkish = new TxtDictionary();
        TxtDictionary dictionary = new TxtDictionary(new TurkishWordComparator());
        for (int i = 0; i < turkish.size(); i++){
            TxtWord txtWord = (TxtWord) turkish.getWord(i);
            if (txtWord.containsFlag("IS_OA")){
                dictionary.addProperNoun(txtWord.getName());
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
                if (words.length == 2 && words[0].equals(words[1])){
                    dictionary.addWithFlag(words[0], "IS_DUP");
                }
            }
        }
        dictionary.saveAsTxt("turkish" + year + "_dictionary.txt");
    }

    public void generateOfflineDictionary(){
        previuosWordNet.generateDictionary("sozluk.tex");
    }

}
