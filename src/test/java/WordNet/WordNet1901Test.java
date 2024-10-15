package WordNet;

import DataStructure.CounterHashMap;
import Dictionary.Pos;
import org.junit.Before;
import org.junit.Test;

import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class WordNet1901Test extends PreviousWordNetTest{

    @Before
    public void setUp() {
        previousWordNet = new WordNet("turkish1901_wordnet.xml", new Locale("tr"));
    }

    @Test
    public void testSize() {
        assertEquals(41175, previousWordNet.size());
    }

    @Test
    public void testSynSetIdCounts() {
        CounterHashMap<String> counts = synSetIdCounts();
        assertEquals(19461, (int) counts.get("TUR00"));
        assertEquals(1285, (int) counts.get("TUR01"));
        assertEquals(270, (int) counts.get("TUR02"));
        assertEquals(92, (int) counts.get("TUR03"));
        assertEquals(36, (int) counts.get("TUR04"));
        assertEquals(115, (int) counts.get("TUR05"));
        assertEquals(122, (int) counts.get("TUR06"));
        assertEquals(203, (int) counts.get("TUR07"));
        assertEquals(80, (int) counts.get("TUR08"));
        assertEquals(114, (int) counts.get("TUR09"));
        assertEquals(19397, (int) counts.get("TUR10"));
    }

    @Test
    public void generateDictionary() {
        generateDictionary("1901");
    }

    @Test
    public void testSynSetList() {
        int literalCount = 0;
        for (SynSet synSet : previousWordNet.synSetList()){
            literalCount += synSet.getSynonym().literalSize();
        }
        assertEquals(47708, literalCount);
    }

    @Test
    public void testGetSynSetsWithPartOfSpeech() {
        assertEquals(22906, previousWordNet.getSynSetsWithPartOfSpeech(Pos.NOUN).size());
        assertEquals(6751, previousWordNet.getSynSetsWithPartOfSpeech(Pos.VERB).size());
        assertEquals(9372, previousWordNet.getSynSetsWithPartOfSpeech(Pos.ADJECTIVE).size());
        assertEquals(1512, previousWordNet.getSynSetsWithPartOfSpeech(Pos.ADVERB).size());
        assertEquals(467, previousWordNet.getSynSetsWithPartOfSpeech(Pos.INTERJECTION).size());
        assertEquals(57, previousWordNet.getSynSetsWithPartOfSpeech(Pos.CONJUNCTION).size());
        assertEquals(70, previousWordNet.getSynSetsWithPartOfSpeech(Pos.PREPOSITION).size());
        assertEquals(40, previousWordNet.getSynSetsWithPartOfSpeech(Pos.PRONOUN).size());
    }

    @Test
    public void testLiteralSortedList() {
        assertTrue(previousWordNet.literalCorrectOrderCheck());
    }

    @Test
    public void testLiteralList() {
        assertEquals(33143, previousWordNet.literalList().size());
    }

    @Test
    public void testLiteralWordCounts() {
        CounterHashMap<Integer> counts = literalWordCounts();
        assertEquals(26301, (int) counts.get(1));
        assertEquals(6175, (int) counts.get(2));
        assertEquals(544, (int) counts.get(3));
        assertEquals(95, (int) counts.get(4));
        assertEquals(20, (int) counts.get(5));
        assertEquals(7, (int) counts.get(6));
        assertEquals(1, (int) counts.get(8));
    }

    @Test
    public void testSameLiteralSameSenseCheck() {
        assertEquals(0, previousWordNet.sameLiteralSameSenseCheck().size());
    }

    @Test
    public void testNoPosCheck() {
        assertEquals(0, previousWordNet.noPosCheck().size());
    }

    @Test
    public void testNoDefinitionCheck() {
        assertEquals(0, previousWordNet.noDefinitionCheck().size());
    }

    @Test
    public void testSameLiteralSameSynSetCheck() {
        assertEquals(0, previousWordNet.sameLiteralSameSynSetCheck().size());
    }

    @Test
    public void testExistenceOfKeNetSynSets(){
        currentWordNet = new WordNet();
        super.testExistenceOfKeNetSynSets();
    }

    @Test
    public void comparePosWithPosOfCorrespondingKeNetSynSets(){
        currentWordNet = new WordNet();
        super.comparePosWithPosOfCorrespondingKeNetSynSets();
    }

}
