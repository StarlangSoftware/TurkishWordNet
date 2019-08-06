package WordNet;

import java.util.Comparator;

public class SynSetSizeComparator implements Comparator<SynSet> {

    /**
     * Compares the source SynSet's literal size to the target SynSet's literal size according to the
     * collation rules for this Collator. Returns an integer less than,
     * equal to or greater than zero depending on whether the source SynSet's literal size is
     * less than, equal to or greater than the target SynSet's literal size.
     *
     * @param synSetA the source SynSet
     * @param synSetB the target SynSet
     * @return Returns an integer value. Value is less than zero if source is less than
     * target, value is zero if source and target are equal, value is greater than zero
     * if source is greater than target
     */
    public int compare(SynSet synSetA, SynSet synSetB) {
        return synSetB.getSynonym().literalSize() - synSetA.getSynonym().literalSize();
    }

}
