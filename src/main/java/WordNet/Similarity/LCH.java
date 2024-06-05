package WordNet.Similarity;

import WordNet.SynSet;
import WordNet.WordNet;

import java.util.ArrayList;

public class LCH extends Similarity{

    /**
     * Class constructor that sets the wordnet.
     * @param wordNet WordNet for which similarity metrics will be calculated.
     */
    public LCH(WordNet wordNet){
        super(wordNet);
    }

    /**
     * Computes LCH wordnet similarity metric between two synsets.
     * @param synSet1 First synset
     * @param synSet2 Second synset
     * @return LCH wordnet similarity metric between two synsets
     */
    public double computeSimilarity(SynSet synSet1, SynSet synSet2) {
        ArrayList<String> pathToRootOfSynSet1 = wordNet.findPathToRoot(synSet1);
        ArrayList<String> pathToRootOfSynSet2 = wordNet.findPathToRoot(synSet2);
        int pathLength = wordNet.findPathLength(pathToRootOfSynSet1, pathToRootOfSynSet2);
        float maxDepth = Math.max(pathToRootOfSynSet1.size(), pathToRootOfSynSet2.size());
        return -Math.log(pathLength / (2 * maxDepth));
    }
}
