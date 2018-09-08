package WordNet.Similarity;

import WordNet.SynSet;
import WordNet.WordNet;

public abstract class Similarity {
    protected WordNet wordNet;
    public abstract double computeSimilarity(SynSet synSet1, SynSet synSet2);

    public Similarity(WordNet wordNet){
        this.wordNet = wordNet;
    }
}
