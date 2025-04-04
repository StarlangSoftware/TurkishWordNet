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

public class WordNet1955Test extends PreviousWordNetTest{

    @Before
    public void setUp() {
        previousWordNet = new WordNet("turkish1955_wordnet.xml", new Locale("tr"));
    }

    public void testExample() {
        previousDictionary = new TxtDictionary("turkish1955_dictionary.txt", new TurkishWordComparator());
        super.testExample();
    }

    @Test
    public void testSize() {
        assertEquals(35265, previousWordNet.size());
    }

    @Test
    public void testSynSetIdCounts() {
        CounterHashMap<String> counts = synSetIdCounts();
        assertEquals(3421, (int) counts.get("TUR01"));
        assertEquals(1478, (int) counts.get("TUR02"));
        assertEquals(30364, (int) counts.get("TUR10"));
    }

    @Test
    public void generateDictionary() {
        generateDictionary("1955");
    }

    @Test
    public void testSynSetList() {
        int literalCount = 0;
        for (SynSet synSet : previousWordNet.synSetList()){
            literalCount += synSet.getSynonym().literalSize();
        }
        assertEquals(45677, literalCount);
    }

    @Test
    public void testGetSynSetsWithPartOfSpeech() {
        assertEquals(19070, previousWordNet.getSynSetsWithPartOfSpeech(Pos.NOUN).size());
        assertEquals(7972, previousWordNet.getSynSetsWithPartOfSpeech(Pos.VERB).size());
        assertEquals(5801, previousWordNet.getSynSetsWithPartOfSpeech(Pos.ADJECTIVE).size());
        assertEquals(1174, previousWordNet.getSynSetsWithPartOfSpeech(Pos.INTERJECTION).size());
        assertEquals(1072, previousWordNet.getSynSetsWithPartOfSpeech(Pos.ADVERB).size());
        assertEquals(83, previousWordNet.getSynSetsWithPartOfSpeech(Pos.CONJUNCTION).size());
        assertEquals(49, previousWordNet.getSynSetsWithPartOfSpeech(Pos.PREPOSITION).size());
        assertEquals(44, previousWordNet.getSynSetsWithPartOfSpeech(Pos.PRONOUN).size());
    }

    @Test
    public void testLiteralSortedList() {
        assertTrue(previousWordNet.literalCorrectOrderCheck());
    }

    @Test
    public void testLiteralList() {
        assertEquals(35066, previousWordNet.literalList().size());
    }

    @Test
    public void testLiteralWordCounts() {
        CounterHashMap<Integer> counts = literalWordCounts();
        assertEquals(25327, (int) counts.get(1));
        assertEquals(7546, (int) counts.get(2));
        assertEquals(1321, (int) counts.get(3));
        assertEquals(517, (int) counts.get(4));
        assertEquals(200, (int) counts.get(5));
        assertEquals(89, (int) counts.get(6));
        assertEquals(36, (int) counts.get(7));
        assertEquals(16, (int) counts.get(8));
        assertEquals(7, (int) counts.get(9));
        assertEquals(4, (int) counts.get(10));
        assertEquals(3, (int) counts.get(11));
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
    }

    @Test
    public void comparePosWithPosOfCorrespondingKeNetSynSets(){
        currentWordNet = new WordNet();
        super.comparePosWithPosOfCorrespondingKeNetSynSets();
    }
}
