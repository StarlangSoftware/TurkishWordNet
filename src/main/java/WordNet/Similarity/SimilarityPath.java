package WordNet.Similarity;

import WordNet.SynSet;
import WordNet.WordNet;

import java.util.ArrayList;

public class SimilarityPath extends Similarity{

    public SimilarityPath(WordNet wordNet){
        super(wordNet);
    }

    @Override
    public double computeSimilarity(SynSet synSet1, SynSet synSet2) {
        // Find path to root of both elements. Percolating up until root is necessary since depth is necessary to compute the score.
        ArrayList<String> pathToRootOfSynSet1 = wordNet.findPathToRoot(synSet1);
        ArrayList<String> pathToRootOfSynSet2 = wordNet.findPathToRoot(synSet2);
        // Find path length
        int pathLength = wordNet.findPathLength(pathToRootOfSynSet1, pathToRootOfSynSet2);
        // Find max depth. The length of path roots
        System.out.println(pathLength);
        int maxDepth = Math.max(pathToRootOfSynSet1.size(), pathToRootOfSynSet2.size());
        return 2 * maxDepth - pathLength;
    }
}
