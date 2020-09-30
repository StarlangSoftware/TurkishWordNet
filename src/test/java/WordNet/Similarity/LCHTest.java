package WordNet.Similarity;

import WordNet.WordNet;
import org.junit.Test;

import static org.junit.Assert.*;

public class LCHTest {

    @Test
    public void testComputeSimilarity() {
        WordNet turkish = new WordNet();
        LCH lch = new LCH(turkish);
        assertEquals(2.8332, lch.computeSimilarity(turkish.getSynSetWithId("TUR10-0656390"), turkish.getSynSetWithId("TUR10-0600460")), 0.0001);
        assertEquals(0.7673, lch.computeSimilarity(turkish.getSynSetWithId("TUR10-0412120"), turkish.getSynSetWithId("TUR10-0755370")), 0.0001);
        assertEquals(0.6241, lch.computeSimilarity(turkish.getSynSetWithId("TUR10-0195110"), turkish.getSynSetWithId("TUR10-0822980")), 0.0001);
    }
}