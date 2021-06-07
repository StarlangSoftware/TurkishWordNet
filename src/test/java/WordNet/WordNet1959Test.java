package WordNet;

import Dictionary.Pos;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Locale;

import static org.junit.Assert.assertEquals;

public class WordNet1959Test extends PreviousWordNetTest{

    @Before
    public void setUp() {
        previuosWordNet = new WordNet("turkish1959_wordnet.xml", new Locale("tr"));
    }

    public void findMatchingSynSetsInPreviousWordNets(){
        WordNet turkish44 = new WordNet("turkish1944_wordnet.xml", new Locale("tr"));
        for (SynSet synSet1 : previuosWordNet.synSetList()){
            if (synSet1.getId().startsWith("TUR03-")){
                for (SynSet synSet2: turkish44.synSetList()){
                    if (synSet2.getId().startsWith("TUR01-") && synSet1.getPos().equals(synSet2.getPos())){
                        double matchRatio = numberOfMatches(synSet1.getLongDefinition(), synSet2.getLongDefinition());
                        if (matchRatio >= 0.8){
                            System.out.println(matchRatio + "\t" + synSet1.getId() + "\t" + synSet2.getId() + "\t" + synSet1.getSynonym().toString() + "\t" + synSet2.getSynonym().toString() + "\t" + synSet1.getLongDefinition() + "\t" + synSet2.getLongDefinition());
                        }
                    }
                }
            }
        }
        WordNet turkish55 = new WordNet("turkish1955_wordnet.xml", new Locale("tr"));
        for (SynSet synSet1 : previuosWordNet.synSetList()){
            if (synSet1.getId().startsWith("TUR03-")){
                for (SynSet synSet2: turkish55.synSetList()){
                    if (synSet2.getId().startsWith("TUR02-") && synSet1.getPos().equals(synSet2.getPos())){
                        double matchRatio = numberOfMatches(synSet1.getLongDefinition(), synSet2.getLongDefinition());
                        if (matchRatio >= 0.8){
                            System.out.println(matchRatio + "\t" + synSet1.getId() + "\t" + synSet2.getId() + "\t" + synSet1.getSynonym().toString() + "\t" + synSet2.getSynonym().toString() + "\t" + synSet1.getLongDefinition() + "\t" + synSet2.getLongDefinition());
                        }
                    }
                }
            }
        }
        WordNet turkish = new WordNet();
        for (SynSet synSet1 : previuosWordNet.synSetList()){
            if (synSet1.getId().startsWith("TUR03-")){
                for (SynSet synSet2: turkish.synSetList()){
                    if (synSet1.getPos().equals(synSet2.getPos())){
                        double matchRatio = numberOfMatches(synSet1.getLongDefinition(), synSet2.getLongDefinition());
                        if (matchRatio >= 0.8){
                            System.out.println(matchRatio + "\t" + synSet1.getId() + "\t" + synSet2.getId() + "\t" + synSet1.getSynonym().toString() + "\t" + synSet2.getSynonym().toString() + "\t" + synSet1.getLongDefinition() + "\t" + synSet2.getLongDefinition());
                        }
                    }
                }
            }
        }
    }

    public void generateNextData(){
        for (String literal : previuosWordNet.literalList()){
            ArrayList<SynSet> synSets = previuosWordNet.getSynSetsWithLiteral(literal);
            for (SynSet synSet : synSets){
                System.out.println(literal + "\t" + synSet.getId() + "\t" + synSet.getPos() + "\t" + synSet.getLongDefinition() + "\t" + synSet.getSynonym() + "\t" + synSet.getExample());
            }
        }
    }

    @Test
    public void testSize() {
        assertEquals(36069, previuosWordNet.size());
    }

    @Test
    public void testSynSetList() {
        int literalCount = 0;
        for (SynSet synSet : previuosWordNet.synSetList()){
            literalCount += synSet.getSynonym().literalSize();
        }
        assertEquals(46620, literalCount);
    }

    @Test
    public void testGetSynSetsWithPartOfSpeech() {
        assertEquals(19166, previuosWordNet.getSynSetsWithPartOfSpeech(Pos.NOUN).size());
        assertEquals(8232, previuosWordNet.getSynSetsWithPartOfSpeech(Pos.VERB).size());
        assertEquals(6073, previuosWordNet.getSynSetsWithPartOfSpeech(Pos.ADJECTIVE).size());
        assertEquals(1260, previuosWordNet.getSynSetsWithPartOfSpeech(Pos.INTERJECTION).size());
        assertEquals(1151, previuosWordNet.getSynSetsWithPartOfSpeech(Pos.ADVERB).size());
        assertEquals(72, previuosWordNet.getSynSetsWithPartOfSpeech(Pos.PREPOSITION).size());
        assertEquals(70, previuosWordNet.getSynSetsWithPartOfSpeech(Pos.CONJUNCTION).size());
        assertEquals(45, previuosWordNet.getSynSetsWithPartOfSpeech(Pos.PRONOUN).size());
    }

    @Test
    public void testLiteralList() {
        assertEquals(35637, previuosWordNet.literalList().size());
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
