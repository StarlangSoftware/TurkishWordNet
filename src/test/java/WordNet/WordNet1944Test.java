package WordNet;

import Dictionary.Pos;
import org.junit.Before;
import org.junit.Test;

import java.util.Locale;

import static org.junit.Assert.assertEquals;

public class WordNet1944Test extends PreviousWordNetTest{

    @Before
    public void setUp() {
        previuosWordNet = new WordNet("turkish1944_wordnet.xml", new Locale("tr"));
    }

    @Test
    public void testSize() {
        assertEquals(31956, previuosWordNet.size());
    }

    @Test
    public void testSynSetList() {
        int literalCount = 0;
        for (SynSet synSet : previuosWordNet.synSetList()){
            literalCount += synSet.getSynonym().literalSize();
        }
        assertEquals(41868, literalCount);
    }

    @Test
    public void testGetSynSetsWithPartOfSpeech() {
        assertEquals(17139, previuosWordNet.getSynSetsWithPartOfSpeech(Pos.NOUN).size());
        assertEquals(7408, previuosWordNet.getSynSetsWithPartOfSpeech(Pos.VERB).size());
        assertEquals(5747, previuosWordNet.getSynSetsWithPartOfSpeech(Pos.ADJECTIVE).size());
        assertEquals(981, previuosWordNet.getSynSetsWithPartOfSpeech(Pos.ADVERB).size());
        assertEquals(534, previuosWordNet.getSynSetsWithPartOfSpeech(Pos.INTERJECTION).size());
        assertEquals(75, previuosWordNet.getSynSetsWithPartOfSpeech(Pos.CONJUNCTION).size());
        assertEquals(39, previuosWordNet.getSynSetsWithPartOfSpeech(Pos.PREPOSITION).size());
        assertEquals(33, previuosWordNet.getSynSetsWithPartOfSpeech(Pos.PRONOUN).size());
    }

    @Test
    public void testLiteralList() {
        assertEquals(31377, previuosWordNet.literalList().size());
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

}
