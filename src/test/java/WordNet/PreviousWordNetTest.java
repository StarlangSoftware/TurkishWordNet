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
                    String newWord = word.replaceAll("`", "").replaceAll("!", "").replaceAll("\\?", "").replaceAll(",", "").replaceAll("'", "").replaceAll("\\(", "").replaceAll("\\)", "").replaceAll("\"", "").replaceAll("\\.", "").replaceAll(";", "");
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

    public void generateDictionary(String year){
        TxtDictionary turkish = new TxtDictionary();
        TxtDictionary dictionary = new TxtDictionary(new TurkishWordComparator());
        for (String literal : previuosWordNet.literalList()){
            if (!literal.contains(" ")){
                TxtWord txtWord = (TxtWord) turkish.getWord(literal);
                if (txtWord != null){
                    if (txtWord.containsFlag("IS_SD")){
                        dictionary.addWithFlag(literal, "IS_SD");
                    }
                    if (txtWord.containsFlag("IS_KG")){
                        dictionary.addWithFlag(literal, "IS_KG");
                    }
                    if (txtWord.containsFlag("IS_UD")){
                        dictionary.addWithFlag(literal, "IS_UD");
                    }
                    if (txtWord.containsFlag("IS_UU")){
                        dictionary.addWithFlag(literal, "IS_UU");
                    }
                    if (txtWord.containsFlag("IS_SU")){
                        dictionary.addWithFlag(literal, "IS_SU");
                    }
                    if (txtWord.containsFlag("F_SD")){
                        dictionary.addWithFlag(literal, "F_SD");
                    }
                    if (txtWord.containsFlag("F_GUD")){
                        dictionary.addWithFlag(literal, "F_GUD");
                    }
                    if (txtWord.containsFlag("F_GUDO")){
                        dictionary.addWithFlag(literal, "F_GUDO");
                    }
                }
                ArrayList<SynSet> synSets = previuosWordNet.getSynSetsWithLiteral(literal);
                for (SynSet synSet : synSets){
                    switch (synSet.getPos()){
                        case NOUN:
                            dictionary.addWithFlag(literal, "CL_ISIM");
                            break;
                        case VERB:
                            dictionary.addWithFlag(literal.substring(0, literal.length() - 3), "CL_FIIL");
                            break;
                        case ADJECTIVE:
                            dictionary.addWithFlag(literal, "IS_ADJ");
                            break;
                        case ADVERB:
                            dictionary.addWithFlag(literal, "IS_ADVERB");
                            break;
                        case CONJUNCTION:
                            dictionary.addWithFlag(literal, "IS_CONJ");
                            break;
                        case PRONOUN:
                            dictionary.addWithFlag(literal, "IS_ZM");
                            break;
                        case INTERJECTION:
                            dictionary.addWithFlag(literal, "IS_INTERJ");
                            break;
                        case PREPOSITION:
                            dictionary.addWithFlag(literal, "IS_POSTP");
                            break;
                    }
                }
            }
        }
        dictionary.saveAsTxt("turkish" + year + "_dictionary.txt");
    }

    public void generateOfflineDictionary(){
        previuosWordNet.generateDictionary("sozluk.tex");
    }

}
