package WordNet;

import java.util.Comparator;

public class SynSetSizeComparator implements Comparator<SynSet> {

    public int compare(SynSet synSetA, SynSet synSetB) {
        return synSetB.getSynonym().literalSize() - synSetA.getSynonym().literalSize();
    }

}
