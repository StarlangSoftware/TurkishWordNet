package WordNet.Similarity;

import WordNet.SynSet;
import WordNet.WordNet;

import java.util.ArrayList;

public class WuPalmer extends Similarity{

    /**
     * Class constructor that sets the wordnet and the information content hash map.
     * @param wordNet WordNet for which similarity metrics will be calculated.
     */
    public WuPalmer(WordNet wordNet){
        super(wordNet);
    }

    /**
     * Computes Wu-Palmer wordnet similarity metric between two synsets.
     * @param synSet1 First synset
     * @param synSet2 Second synset
     * @return Wu-Palmer wordnet similarity metric between two synsets
     */
    public double computeSimilarity(SynSet synSet1, SynSet synSet2) {
        ArrayList<String> pathToRootOfSynSet1 = wordNet.findPathToRoot(synSet1);
        ArrayList<String> pathToRootOfSynSet2 = wordNet.findPathToRoot(synSet2);
        float LCSdepth = wordNet.findLCSdepth(pathToRootOfSynSet1, pathToRootOfSynSet2);
        return 2 * LCSdepth / (pathToRootOfSynSet1.size() + pathToRootOfSynSet2.size());
    }
}
