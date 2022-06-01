package WordNet;


import DataStructure.CounterHashMap;
import Dictionary.Pos;
import org.junit.Before;
import org.junit.Test;

import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class WordNet1998Test extends PreviousWordNetTest{

    @Before
    public void setUp() {
        previousWordNet = new WordNet("turkish1998_wordnet.xml", new Locale("tr"));
    }

    @Test
    public void testExample() {
        //previousDictionary = new TxtDictionary("turkish1998_dictionary.txt", new TurkishWordComparator());
        //super.testExample();
    }

    @Test
    public void testSize() {
        assertEquals(67286, previousWordNet.size());
    }

    @Test
    public void testSynSetIdCounts() {
        CounterHashMap<String> counts = synSetIdCounts();
        assertEquals(1398, (int) counts.get("TUR01"));
        assertEquals(625, (int) counts.get("TUR02"));
        assertEquals(193, (int) counts.get("TUR03"));
        assertEquals(94, (int) counts.get("TUR04"));
        assertEquals(443, (int) counts.get("TUR05"));
        assertEquals(560, (int) counts.get("TUR06"));
        assertEquals(1196, (int) counts.get("TUR07"));
        assertEquals(520, (int) counts.get("TUR08"));
        assertEquals(61213, (int) counts.get("TUR10"));
    }

    @Test
    public void generateDictionary() {
        generateDictionary("1998");
    }

    @Test
    public void testSynSetList() {
        int literalCount = 0;
        for (SynSet synSet : previousWordNet.synSetList()){
            literalCount += synSet.getSynonym().literalSize();
        }
        assertEquals(87531, literalCount);
    }

    @Test
    public void testGetSynSetsWithPartOfSpeech() {
        assertEquals(36132, previousWordNet.getSynSetsWithPartOfSpeech(Pos.NOUN).size());
        assertEquals(15927, previousWordNet.getSynSetsWithPartOfSpeech(Pos.VERB).size());
        assertEquals(10818, previousWordNet.getSynSetsWithPartOfSpeech(Pos.ADJECTIVE).size());
        assertEquals(2346, previousWordNet.getSynSetsWithPartOfSpeech(Pos.ADVERB).size());
        assertEquals(1848, previousWordNet.getSynSetsWithPartOfSpeech(Pos.INTERJECTION).size());
        assertEquals(79, previousWordNet.getSynSetsWithPartOfSpeech(Pos.CONJUNCTION).size());
        assertEquals(76, previousWordNet.getSynSetsWithPartOfSpeech(Pos.PRONOUN).size());
        assertEquals(60, previousWordNet.getSynSetsWithPartOfSpeech(Pos.PREPOSITION).size());
    }

    @Test
    public void testLiteralSortedList() {
        assertTrue(previousWordNet.literalCorrectOrderCheck());
    }

    @Test
    public void testLiteralList() {
        assertEquals(63052, previousWordNet.literalList().size());
    }

    @Test
    public void testLiteralWordCounts() {
        CounterHashMap<Integer> counts = literalWordCounts();
        assertEquals(37047, (int) counts.get(1));
        assertEquals(21647, (int) counts.get(2));
        assertEquals(2806, (int) counts.get(3));
        assertEquals(939, (int) counts.get(4));
        assertEquals(326, (int) counts.get(5));
        assertEquals(169, (int) counts.get(6));
        assertEquals(61, (int) counts.get(7));
        assertEquals(40, (int) counts.get(8));
        assertEquals(8, (int) counts.get(9));
        assertEquals(5, (int) counts.get(10));
        assertEquals(3, (int) counts.get(11));
    }

    @Test
    public void testSameLiteralSameSenseCheck() {
        for (Literal literal : previousWordNet.sameLiteralSameSenseCheck()){
            System.out.println(literal.getName());
        }
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
        for (SynSet synSet : previousWordNet.sameLiteralSameSynSetCheck()){
            System.out.println(synSet.getId());
        }
        assertEquals(0, previousWordNet.sameLiteralSameSynSetCheck().size());
    }

    @Test
    public void testExistenceOfKeNetSynSets(){
        currentWordNet = new WordNet();
        super.testExistenceOfKeNetSynSets();
    }

    @Test
    public void testExistenceOfPreviousSynSets(){
        super.testExistenceOfPreviousSynSets("1944", "TUR01");
        super.testExistenceOfPreviousSynSets("1955", "TUR02");
        super.testExistenceOfPreviousSynSets("1959", "TUR03");
        super.testExistenceOfPreviousSynSets("1966", "TUR04");
        super.testExistenceOfPreviousSynSets("1969", "TUR05");
        super.testExistenceOfPreviousSynSets("1974", "TUR06");
        super.testExistenceOfPreviousSynSets("1983", "TUR07");
        super.testExistenceOfPreviousSynSets("1988", "TUR08");
    }

    @Test
    public void comparePosWithPosOfCorrespondingKeNetSynSets(){
        currentWordNet = new WordNet();
        super.comparePosWithPosOfCorrespondingKeNetSynSets();
    }
}
