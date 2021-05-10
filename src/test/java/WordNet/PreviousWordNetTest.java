package WordNet;

import Dictionary.Pos;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Locale;

public class PreviousWordNetTest {

    WordNet previuosWordNet;

    public void generateHypernyms(){
        ArrayList<String> sortedList = new ArrayList<String>();
        WordNet turkish = new WordNet();
        for (SynSet synSet : previuosWordNet.synSetList()){
            SynSet synSet1 = turkish.getSynSetWithId(synSet.getId());
            if (synSet1 != null && synSet1.getPos().equals(Pos.NOUN)){
                ArrayList<String> list = turkish.findPathToRoot(synSet1);
                String result = list.get(list.size() - 1);
                for (int i = list.size() - 2; i >= 0; i--){
                    result += " " + list.get(i);
                }
                sortedList.add(result);
            }
        }
        sortedList.sort(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });
        for (int i = 0, k = 1; i < sortedList.size(); i++){
            String[] list = sortedList.get(i).split(" ");
            if (list[0].equals("TUR10-0814560")){
                int j = 1;
                for (String item : list){
                    if (previuosWordNet.getSynSetWithId(item) != null){
                        System.out.println(k + "\t1\t" + j + "\t" + item + "\t" + previuosWordNet.getSynSetWithId(item).getSynonym() + "\t" + previuosWordNet.getSynSetWithId(item).getLongDefinition());
                    } else {
                        System.out.println(k + "\t0\t" + j + "\t" + item + "\t" + turkish.getSynSetWithId(item).getSynonym() + "\t" + turkish.getSynSetWithId(item).getLongDefinition());
                    }
                    j++;
                }
                System.out.println(k + "\t--------\t--------\t--------\t--------\t--------");
                k++;
            }
        }
    }

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
