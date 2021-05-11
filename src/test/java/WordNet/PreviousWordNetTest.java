package WordNet;

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

    public void generateOfflineDictionary(){
        previuosWordNet.generateDictionary("sozluk.tex");
    }

}
