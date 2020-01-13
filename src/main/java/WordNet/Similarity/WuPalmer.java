package WordNet.Similarity;

import WordNet.SynSet;
import WordNet.WordNet;

import java.util.AbstractMap;
import java.util.ArrayList;

public class WuPalmer extends Similarity{

    public WuPalmer(WordNet wordNet){
        super(wordNet);
    }

    public double computeSimilarity(SynSet synSet1, SynSet synSet2) {
        ArrayList<String> path1 = wordNet.findPathToRoot(synSet1);
        ArrayList<String> path2 = wordNet.findPathToRoot(synSet2);
        AbstractMap.SimpleEntry<String, Integer> lcs = findLCS(path1,path2,true);
        float lcsDepth = lcs.getValue();
        if(lcsDepth == -1) return -1;        //TODO: -1 is used for null returns. Should return nullable results
        float c1len = findLength(path1,lcs);
        float c2len = findLength(path2,lcs);

        float num = 2 * lcsDepth;
        float denom = c1len + c2len + num;
        return num / denom;
    }
}
