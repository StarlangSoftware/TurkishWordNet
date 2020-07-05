package WordNet.Similarity;

import WordNet.WordNet;
import org.junit.Test;

import static org.junit.Assert.*;

public class SimilarityPathTest {

    @Test
    public void testComputeSimilarity() {
        WordNet turkish = new WordNet();
        SimilarityPath similarityPath = new SimilarityPath(turkish);
        assertEquals(38.0, similarityPath.computeSimilarity(turkish.getSynSetWithId("TUR10-0656390"), turkish.getSynSetWithId("TUR10-0600460")), 0.0001);
        assertEquals(6.0, similarityPath.computeSimilarity(turkish.getSynSetWithId("TUR10-0066050"), turkish.getSynSetWithId("TUR10-1198750")), 0.0001);
        assertEquals(6.0, similarityPath.computeSimilarity(turkish.getSynSetWithId("TUR10-0012910"), turkish.getSynSetWithId("TUR10-0172740")), 0.0001);
        assertEquals(15.0, similarityPath.computeSimilarity(turkish.getSynSetWithId("TUR10-0412120"), turkish.getSynSetWithId("TUR10-0755370")), 0.0001);
        assertEquals(16.0, similarityPath.computeSimilarity(turkish.getSynSetWithId("TUR10-0195110"), turkish.getSynSetWithId("TUR10-0822980")), 0.0001);
    }
}