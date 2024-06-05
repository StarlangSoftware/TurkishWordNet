package WordNet.Similarity;

import WordNet.SynSet;
import WordNet.WordNet;

import java.util.ArrayList;

public class SimilarityPath extends Similarity{

    /**
     * Class constructor that sets the wordnet and the information content hash map.
     * @param wordNet WordNet for which similarity metrics will be calculated.
     */
    public SimilarityPath(WordNet wordNet){
        super(wordNet);
    }

    /**
     * Computes wordnet similarity metric based on similarity path between two synsets.
     * @param synSet1 First synset
     * @param synSet2 Second synset
     * @return Resnik wordnet similarity metric based on similarity path between two synsets.
     */
    @Override
    public double computeSimilarity(SynSet synSet1, SynSet synSet2) {
        // Find path to root of both elements. Percolating up until root is necessary since depth is necessary to compute the score.
        ArrayList<String> pathToRootOfSynSet1 = wordNet.findPathToRoot(synSet1);
        ArrayList<String> pathToRootOfSynSet2 = wordNet.findPathToRoot(synSet2);
        // Find path length
        int pathLength = wordNet.findPathLength(pathToRootOfSynSet1, pathToRootOfSynSet2);
        int maxDepth = Math.max(pathToRootOfSynSet1.size(), pathToRootOfSynSet2.size());
        return 2 * maxDepth - pathLength;
    }
}
