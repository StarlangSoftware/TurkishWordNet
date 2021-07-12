package WordNet;

import Dictionary.TurkishWordComparator;
import Dictionary.TxtDictionary;

import java.util.ArrayList;
import java.util.Locale;

public class PreviousWordNetTest {

    WordNet previuosWordNet;

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

    public void generateDictionary(String year){
        TxtDictionary dictionary = new TxtDictionary(new TurkishWordComparator());
        for (String literal : previuosWordNet.literalList()){
            if (!literal.contains(" ")){
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
