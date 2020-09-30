package WordNet.Similarity;

import WordNet.WordNet;
import org.junit.Test;

import static org.junit.Assert.*;

public class WuPalmerTest {

    @Test
    public void testComputeSimilarity() {
        WordNet turkish = new WordNet();
        WuPalmer wuPalmer = new WuPalmer(turkish);
        assertEquals(0.9697, wuPalmer.computeSimilarity(turkish.getSynSetWithId("TUR10-0656390"), turkish.getSynSetWithId("TUR10-0600460")), 0.0001);
        assertEquals(0.25, wuPalmer.computeSimilarity(turkish.getSynSetWithId("TUR10-0412120"), turkish.getSynSetWithId("TUR10-0755370")), 0.0001);
        assertEquals(0.3636, wuPalmer.computeSimilarity(turkish.getSynSetWithId("TUR10-0195110"), turkish.getSynSetWithId("TUR10-0822980")), 0.0001);
    }
}