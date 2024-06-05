package WordNet.Similarity;

import WordNet.*;

import java.util.ArrayList;
import java.util.HashMap;

public class JCN extends ICSimilarity{

    /**
     * Class constructor that sets the wordnet and the information content hash map.
     * @param wordNet WordNet for which similarity metrics will be calculated.
     * @param informationContents Information content hash map.
     */
    public JCN(WordNet wordNet, HashMap<String, Double> informationContents){
        super(wordNet, informationContents);
    }

    /**
     * Computes JCN wordnet similarity metric between two synsets.
     * @param synSet1 First synset
     * @param synSet2 Second synset
     * @return JCN wordnet similarity metric between two synsets
     */
    public double computeSimilarity(SynSet synSet1, SynSet synSet2) {
        ArrayList<String> pathToRootOfSynSet1 = wordNet.findPathToRoot(synSet1);
        ArrayList<String> pathToRootOfSynSet2 = wordNet.findPathToRoot(synSet2);
        String LCSid = wordNet.findLCSid(pathToRootOfSynSet1, pathToRootOfSynSet2);
        return 1 / (informationContents.get(synSet1.getId()) + informationContents.get(synSet2.getId()) - 2 * informationContents.get(LCSid));
    }
}
