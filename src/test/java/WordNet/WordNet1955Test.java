package WordNet;

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
        previuosWordNet = new WordNet("turkish1955_wordnet.xml", new Locale("tr"));
    }

    @Test
    public void testExample() {
        previousDictionary = new TxtDictionary("turkish1955_dictionary.txt", new TurkishWordComparator());
        super.testExample();
    }

    @Test
    public void testSize() {
        assertEquals(34457, previuosWordNet.size());
    }

    @Test
    public void generateDictionary() {
        generateDictionary("1955");
    }

    @Test
    public void testSynSetList() {
        int literalCount = 0;
        for (SynSet synSet : previuosWordNet.synSetList()){
            literalCount += synSet.getSynonym().literalSize();
        }
        assertEquals(44815, literalCount);
    }

    @Test
    public void testGetSynSetsWithPartOfSpeech() {
        assertEquals(18225, previuosWordNet.getSynSetsWithPartOfSpeech(Pos.NOUN).size());
        assertEquals(7998, previuosWordNet.getSynSetsWithPartOfSpeech(Pos.VERB).size());
        assertEquals(5801, previuosWordNet.getSynSetsWithPartOfSpeech(Pos.ADJECTIVE).size());
        assertEquals(1186, previuosWordNet.getSynSetsWithPartOfSpeech(Pos.INTERJECTION).size());
        assertEquals(1072, previuosWordNet.getSynSetsWithPartOfSpeech(Pos.ADVERB).size());
        assertEquals(83, previuosWordNet.getSynSetsWithPartOfSpeech(Pos.CONJUNCTION).size());
        assertEquals(49, previuosWordNet.getSynSetsWithPartOfSpeech(Pos.PREPOSITION).size());
        assertEquals(43, previuosWordNet.getSynSetsWithPartOfSpeech(Pos.PRONOUN).size());
    }

    @Test
    public void testLiteralSortedList() {
        assertTrue(previuosWordNet.literalCorrectOrderCheck());
    }

    @Test
    public void testLiteralList() {
        assertEquals(34220, previuosWordNet.literalList().size());
    }

    @Test
    public void testSameLiteralSameSenseCheck() {
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
        for (SynSet synSet : previuosWordNet.sameLiteralSameSynSetCheck()){
            System.out.println(synSet.getId());
        }
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
    }

    @Test
    public void comparePosWithPosOfCorrespondingKeNetSynSets(){
        currentWordNet = new WordNet();
        super.comparePosWithPosOfCorrespondingKeNetSynSets();
    }
}
