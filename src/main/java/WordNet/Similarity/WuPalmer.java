package WordNet.Similarity;

import WordNet.SynSet;
import WordNet.WordNet;

import java.util.ArrayList;

public class WuPalmer extends Similarity{

    public WuPalmer(WordNet wordNet){
        super(wordNet);
    }

    public double computeSimilarity(SynSet synSet1, SynSet synSet2) {
        ArrayList<String> pathToRootOfSynSet1 = wordNet.findPathToRoot(synSet1);
        ArrayList<String> pathToRootOfSynSet2 = wordNet.findPathToRoot(synSet2);
        float LCSdepth = wordNet.findLCSdepth(pathToRootOfSynSet1, pathToRootOfSynSet2);
        return 2 * LCSdepth / (pathToRootOfSynSet1.size() + pathToRootOfSynSet2.size());
    }
}
