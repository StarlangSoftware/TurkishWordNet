package WordNet.Similarity;

import WordNet.SynSet;
import WordNet.WordNet;

import java.util.AbstractMap;
import java.util.ArrayList;

public abstract class Similarity {
    private static final String ROOT_KEY = "TUR10-0814560";      //Varlık - Hardcoded!

    protected WordNet wordNet;
    public abstract double computeSimilarity(SynSet synSet1, SynSet synSet2);

    public Similarity(WordNet wordNet){
        this.wordNet = wordNet;
    }

    public AbstractMap.SimpleEntry<String, Integer> findLCS(ArrayList<String> path1, ArrayList<String> path2, boolean autoSimulateRoots) {
        if(autoSimulateRoots){
            autoSimulateRoot(path1);
            autoSimulateRoot(path2);
        }
        for (int i = 0; i < path1.size(); i++) {
            String LCSid = path1.get(i);
            if (path2.contains(LCSid)) {
                return new AbstractMap.SimpleEntry<>(LCSid, path1.size() - i + 1);
            }
        }
        return null;
    }
    public void autoSimulateRoot(ArrayList<String> path){
        if(path.size() == 0) return;
        String lastKey = path.get(path.size()-1);
        if(lastKey != ROOT_KEY){
            path.add(ROOT_KEY);
        }
    }

    /*
    Finds the length between the concept and the lcs.
    lso (lowest super ordinate) = most specific common subsumer (lcs)
     */
    protected float findLength(ArrayList<String> conceptPath, AbstractMap.SimpleEntry<String, Integer> lcs){
        int len = 0;
        for (String s : conceptPath) {
            if(s == lcs.getKey()) return Float.valueOf(len);
            len++;
        }
        throw new RuntimeException("Cannot compute the lengths. Given LCS should be extracted from the conceptPath. Two are unrelated. Try autoSimulateRoots.");
    }

}
