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

public class WordNet1988Test extends PreviousWordNetTest{

    @Before
    public void setUp() {
        previousWordNet = new WordNet("turkish1988_wordnet.xml", new Locale("tr"));
    }

    @Test
    public void testExample() {
        previousDictionary = new TxtDictionary("turkish1988_dictionary.txt", new TurkishWordComparator());
        super.testExample();
    }

    @Test
    public void testSize() {
        assertEquals(58772, previousWordNet.size());
    }

    @Test
    public void testSynSetIdCounts() {
        CounterHashMap<String> counts = synSetIdCounts();
        assertEquals(1548, (int) counts.get("TUR01"));
        assertEquals(686, (int) counts.get("TUR02"));
        assertEquals(200, (int) counts.get("TUR03"));
        assertEquals(100, (int) counts.get("TUR04"));
        assertEquals(479, (int) counts.get("TUR05"));
        assertEquals(601, (int) counts.get("TUR06"));
        assertEquals(1288, (int) counts.get("TUR07"));
        assertEquals(567, (int) counts.get("TUR08"));
        assertEquals(53303, (int) counts.get("TUR10"));
    }

    @Test
    public void generateDictionary() {
        generateDictionary("1988");
    }

    @Test
    public void testSynSetList() {
        int literalCount = 0;
        for (SynSet synSet : previousWordNet.synSetList()){
            literalCount += synSet.getSynonym().literalSize();
        }
        assertEquals(76695, literalCount);
    }

    @Test
    public void testGetSynSetsWithPartOfSpeech() {
        assertEquals(30980, previousWordNet.getSynSetsWithPartOfSpeech(Pos.NOUN).size());
        assertEquals(14184, previousWordNet.getSynSetsWithPartOfSpeech(Pos.VERB).size());
        assertEquals(9700, previousWordNet.getSynSetsWithPartOfSpeech(Pos.ADJECTIVE).size());
        assertEquals(1952, previousWordNet.getSynSetsWithPartOfSpeech(Pos.ADVERB).size());
        assertEquals(1751, previousWordNet.getSynSetsWithPartOfSpeech(Pos.INTERJECTION).size());
        assertEquals(79, previousWordNet.getSynSetsWithPartOfSpeech(Pos.CONJUNCTION).size());
        assertEquals(68, previousWordNet.getSynSetsWithPartOfSpeech(Pos.PRONOUN).size());
        assertEquals(58, previousWordNet.getSynSetsWithPartOfSpeech(Pos.PREPOSITION).size());
    }

    @Test
    public void testLiteralSortedList() {
        assertTrue(previousWordNet.literalCorrectOrderCheck());
    }

    @Test
    public void testLiteralList() {
        assertEquals(54840, previousWordNet.literalList().size());
    }

    @Test
    public void testLiteralWordCounts() {
        CounterHashMap<Integer> counts = literalWordCounts();
        assertEquals(34505, (int) counts.get(1));
        assertEquals(16472, (int) counts.get(2));
        assertEquals(2466, (int) counts.get(3));
        assertEquals(853, (int) counts.get(4));
        assertEquals(284, (int) counts.get(5));
        assertEquals(155, (int) counts.get(6));
        assertEquals(56, (int) counts.get(7));
        assertEquals(34, (int) counts.get(8));
        assertEquals(8, (int) counts.get(9));
        assertEquals(4, (int) counts.get(10));
        assertEquals(2, (int) counts.get(11));
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
    }

    @Test
    public void comparePosWithPosOfCorrespondingKeNetSynSets(){
        currentWordNet = new WordNet();
        super.comparePosWithPosOfCorrespondingKeNetSynSets();
    }

}
