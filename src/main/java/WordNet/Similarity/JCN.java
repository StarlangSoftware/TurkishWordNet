package WordNet.Similarity;

import WordNet.*;

import java.util.ArrayList;
import java.util.HashMap;

public class JCN extends ICSimilarity{

    public JCN(WordNet wordNet, HashMap<String, Double> informationContents){
        super(wordNet, informationContents);
    }

    public double computeSimilarity(SynSet synSet1, SynSet synSet2) {
        ArrayList<String> pathToRootOfSynSet1 = wordNet.findPathToRoot(synSet1);
        ArrayList<String> pathToRootOfSynSet2 = wordNet.findPathToRoot(synSet2);
        String LCSid = wordNet.findLCSid(pathToRootOfSynSet1, pathToRootOfSynSet2);
        return 1 / (informationContents.get(synSet1.getId()) + informationContents.get(synSet2.getId()) - 2 * informationContents.get(LCSid));
    }
}
