package WordNet.Similarity;

import WordNet.WordNet;

import java.util.HashMap;

public abstract class ICSimilarity extends Similarity{
    protected HashMap<String, Double> informationContents;

    public ICSimilarity(WordNet wordNet, HashMap<String, Double> informationContents){
        super(wordNet);
        this.informationContents = informationContents;
    }
}
