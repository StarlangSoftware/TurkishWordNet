package WordNet;

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

    public void findMatchingSynSetsInPreviousWordNets(String prefix){
        WordNet turkish = new WordNet();
        for (SynSet synSet1 : previuosWordNet.synSetList()){
            if (synSet1.getId().startsWith(prefix)){
                for (SynSet synSet2: turkish.synSetList()){
                    if (synSet1.getPos().equals(synSet2.getPos())){
                        double matchRatio = numberOfMatches(synSet1.getLongDefinition(), synSet2.getLongDefinition());
                        if (matchRatio >= 0.8){
                            System.out.println(matchRatio + "\t" + synSet1.getId() + "\t" + synSet2.getId() + "\t" + synSet1.getSynonym().toString() + "\t" + synSet2.getSynonym().toString() + "\t" + synSet1.getLongDefinition() + "\t" + synSet2.getLongDefinition());
                        }
                    }
                }
            }
        }
    }

    public void findSameLiteralsInPreviousWordNets(String prefix){
        WordNet turkish = new WordNet();
        for (SynSet synSet1 : previuosWordNet.synSetList()){
            if (synSet1.getId().startsWith(prefix)){
                for (int i = 0; i < synSet1.getSynonym().literalSize(); i++){
                    String literal = synSet1.getSynonym().getLiteral(i).getName();
                    ArrayList<SynSet> synSets = turkish.getSynSetsWithLiteral(literal);
                    boolean found = false;
                    for (SynSet synSet : synSets){
                        if (synSet.getPos().equals(synSet1.getPos())){
                            found = true;
                            break;
                        }
                    }
                    if (found){
                        System.out.println(literal + "\t" + synSet1.getPos() + "\t-----\t------");
                        if (synSets.size() > 0){
                            System.out.println(literal + "\t" + synSet1.getPos() + "\t" + synSet1.getId() + "\t" + synSet1.getLongDefinition());
                            for (SynSet synSet : synSets){
                                if (synSet.getPos().equals(synSet1.getPos())){
                                    System.out.println(literal + "\t" + synSet1.getPos() + "\t" + synSet.getId() + "\t" + synSet.getLongDefinition());
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void generateOfflineDictionary(){
        previuosWordNet.generateDictionary("sozluk.tex");
    }

}
