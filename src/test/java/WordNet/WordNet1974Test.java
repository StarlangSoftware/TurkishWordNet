package WordNet;

import DataStructure.CounterHashMap;
import Dictionary.Pos;
import Dictionary.TurkishWordComparator;
import Dictionary.TxtDictionary;
import org.junit.Before;
import org.junit.Test;

import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class WordNet1974Test extends PreviousWordNetTest{

    @Before
    public void setUp() {
        previousWordNet = new WordNet("turkish1974_wordnet.xml", new Locale("tr"));
    }

    @Test
    public void testExample() {
        previousDictionary = new TxtDictionary("turkish1974_dictionary.txt", new TurkishWordComparator());
        super.testExample();
    }

    @Test
    public void testSize() {
        assertEquals(43747, previousWordNet.size());
    }

    @Test
    public void testSynSetIdCounts() {
        CounterHashMap<String> counts = synSetIdCounts();
        assertEquals(2483, (int) counts.get("TUR01"));
        assertEquals(1068, (int) counts.get("TUR02"));
        assertEquals(326, (int) counts.get("TUR03"));
        assertEquals(146, (int) counts.get("TUR04"));
        assertEquals(652, (int) counts.get("TUR05"));
        assertEquals(1055, (int) counts.get("TUR06"));
        assertEquals(38017, (int) counts.get("TUR10"));
    }

    @Test
    public void generateDictionary() {
        generateDictionary("1974");
    }

    @Test
    public void testSynSetList() {
        int literalCount = 0;
        for (SynSet synSet : previousWordNet.synSetList()){
            literalCount += synSet.getSynonym().literalSize();
        }
        assertEquals(55707, literalCount);
    }

    @Test
    public void testGetSynSetsWithPartOfSpeech() {
        assertEquals(23571, previousWordNet.getSynSetsWithPartOfSpeech(Pos.NOUN).size());
        assertEquals(10466, previousWordNet.getSynSetsWithPartOfSpeech(Pos.VERB).size());
        assertEquals(6790, previousWordNet.getSynSetsWithPartOfSpeech(Pos.ADJECTIVE).size());
        assertEquals(1390, previousWordNet.getSynSetsWithPartOfSpeech(Pos.INTERJECTION).size());
        assertEquals(1322, previousWordNet.getSynSetsWithPartOfSpeech(Pos.ADVERB).size());
        assertEquals(74, previousWordNet.getSynSetsWithPartOfSpeech(Pos.PREPOSITION).size());
        assertEquals(72, previousWordNet.getSynSetsWithPartOfSpeech(Pos.CONJUNCTION).size());
        assertEquals(62, previousWordNet.getSynSetsWithPartOfSpeech(Pos.PRONOUN).size());
    }

    @Test
    public void testLiteralSortedList() {
        assertTrue(previousWordNet.literalCorrectOrderCheck());
    }

    @Test
    public void testLiteralList() {
        assertEquals(42491, previousWordNet.literalList().size());
    }

    @Test
    public void testLiteralWordCounts() {
        CounterHashMap<Integer> counts = literalWordCounts();
        assertEquals(29417, (int) counts.get(1));
        assertEquals(10290, (int) counts.get(2));
        assertEquals(1739, (int) counts.get(3));
        assertEquals(657, (int) counts.get(4));
        assertEquals(227, (int) counts.get(5));
        assertEquals(98, (int) counts.get(6));
        assertEquals(33, (int) counts.get(7));
        assertEquals(16, (int) counts.get(8));
        assertEquals(9, (int) counts.get(9));
        assertEquals(4, (int) counts.get(10));
        assertEquals(1, (int) counts.get(11));
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
    }

    @Test
    public void comparePosWithPosOfCorrespondingKeNetSynSets(){
        currentWordNet = new WordNet();
        super.comparePosWithPosOfCorrespondingKeNetSynSets();
    }

}
