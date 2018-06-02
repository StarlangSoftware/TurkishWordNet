package WordNet;

import java.util.Comparator;

public class SynSetComparator implements Comparator<SynSet> {

    public int compare(SynSet synSetA, SynSet synSetB) {
        return synSetA.getId().compareTo(synSetB.getId());
    }

}
