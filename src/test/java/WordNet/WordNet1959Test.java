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

public class WordNet1959Test extends PreviousWordNetTest{

    @Before
    public void setUp() {
        previuosWordNet = new WordNet("turkish1959_wordnet.xml", new Locale("tr"));
    }

    @Test
    public void testExample() {
        previousDictionary = new TxtDictionary("turkish1959_dictionary.txt", new TurkishWordComparator());
        super.testExample();
    }

    @Test
    public void testSize() {
        assertEquals(35802, previuosWordNet.size());
    }

    @Test
    public void testSynSetIdCounts() {
        CounterHashMap<String> counts = synSetIdCounts();
        assertEquals(3548, (int) counts.get("TUR01"));
        assertEquals(1903, (int) counts.get("TUR02"));
        assertEquals(501, (int) counts.get("TUR03"));
        assertEquals(29849, (int) counts.get("TUR10"));
    }

    @Test
    public void generateDictionary() {
        generateDictionary("1959");
    }

    @Test
    public void testSynSetList() {
        int literalCount = 0;
        for (SynSet synSet : previuosWordNet.synSetList()){
            literalCount += synSet.getSynonym().literalSize();
        }
        assertEquals(46591, literalCount);
    }

    @Test
    public void testGetSynSetsWithPartOfSpeech() {
        assertEquals(19017, previuosWordNet.getSynSetsWithPartOfSpeech(Pos.NOUN).size());
        assertEquals(8162, previuosWordNet.getSynSetsWithPartOfSpeech(Pos.VERB).size());
        assertEquals(6051, previuosWordNet.getSynSetsWithPartOfSpeech(Pos.ADJECTIVE).size());
        assertEquals(1239, previuosWordNet.getSynSetsWithPartOfSpeech(Pos.INTERJECTION).size());
        assertEquals(1147, previuosWordNet.getSynSetsWithPartOfSpeech(Pos.ADVERB).size());
        assertEquals(71, previuosWordNet.getSynSetsWithPartOfSpeech(Pos.PREPOSITION).size());
        assertEquals(69, previuosWordNet.getSynSetsWithPartOfSpeech(Pos.CONJUNCTION).size());
        assertEquals(46, previuosWordNet.getSynSetsWithPartOfSpeech(Pos.PRONOUN).size());
    }

    @Test
    public void testLiteralSortedList() {
        assertTrue(previuosWordNet.literalCorrectOrderCheck());
    }

    @Test
    public void testLiteralList() {
        assertEquals(35670, previuosWordNet.literalList().size());
    }

    @Test
    public void testLiteralWordCounts() {
        CounterHashMap<Integer> counts = literalWordCounts();
        assertEquals(25553, (int) counts.get(1));
        assertEquals(7816, (int) counts.get(2));
        assertEquals(1375, (int) counts.get(3));
        assertEquals(577, (int) counts.get(4));
        assertEquals(201, (int) counts.get(5));
        assertEquals(89, (int) counts.get(6));
        assertEquals(33, (int) counts.get(7));
        assertEquals(14, (int) counts.get(8));
        assertEquals(7, (int) counts.get(9));
        assertEquals(3, (int) counts.get(10));
        assertEquals(2, (int) counts.get(11));
    }

    @Test
    public void testSameLiteralSameSenseCheck() {
        for (Literal literal : previuosWordNet.sameLiteralSameSenseCheck()){
            System.out.println(literal.getName());
        }
        assertEquals(0, previuosWordNet.sameLiteralSameSenseCheck().size());
    }

    @Test
    public void testNoPosCheck() {
        assertEquals(0, previuosWordNet.noPosCheck().size());
    }

    @Test
    public void testNoDefinitionCheck() {
        assertEquals(0, previuosWordNet.noDefinitionCheck().size());
    }

    @Test
    public void testSameLiteralSameSynSetCheck() {
        assertEquals(0, previuosWordNet.sameLiteralSameSynSetCheck().size());
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
    }

    @Test
    public void comparePosWithPosOfCorrespondingKeNetSynSets(){
        currentWordNet = new WordNet();
        super.comparePosWithPosOfCorrespondingKeNetSynSets();
    }
}
