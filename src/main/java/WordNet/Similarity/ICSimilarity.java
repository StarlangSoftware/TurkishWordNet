package WordNet.Similarity;

import WordNet.WordNet;

import java.util.HashMap;

public abstract class ICSimilarity extends Similarity{
    protected HashMap<String, Double> informationContents;

    /**
     * Abstract class constructor to set the wordnet and the information content hash map.
     * @param wordNet WordNet for which similarity metrics will be calculated.
     * @param informationContents Information content hash map.
     */
    public ICSimilarity(WordNet wordNet, HashMap<String, Double> informationContents){
        super(wordNet);
        this.informationContents = informationContents;
    }
}
