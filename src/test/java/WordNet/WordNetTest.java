package WordNet;

import Dictionary.Pos;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;


public class WordNetTest {
    WordNet turkish;

    @Before
    public void setUp() {
        turkish = new WordNet();
    }

    @Test
    public void testSynSetList() {
        int literalCount = 0;
        for (SynSet synSet : turkish.synSetList()){
            literalCount += synSet.getSynonym().literalSize();
        }
        assertEquals(108906, literalCount);
    }

    @Test
    public void testLiteralList() {
        assertEquals(80870, turkish.literalList().size());
    }

    @Test
    public void testGetSynSetWithId() {
        assertNotNull(turkish.getSynSetWithId("TUR10-0000040"));
        assertNotNull(turkish.getSynSetWithId("TUR10-0648550"));
        assertNotNull(turkish.getSynSetWithId("TUR10-1034170"));
        assertNotNull(turkish.getSynSetWithId("TUR10-1047180"));
        assertNotNull(turkish.getSynSetWithId("TUR10-1196250"));
    }

    @Test
    public void testGetSynSetWithLiteral() {
        assertNotNull(turkish.getSynSetWithLiteral("sıradaki", 1));
        assertNotNull(turkish.getSynSetWithLiteral("Türkçesi", 2));
        assertNotNull(turkish.getSynSetWithLiteral("tropikal orman", 1));
        assertNotNull(turkish.getSynSetWithLiteral("mesut olmak", 1));
        assertNotNull(turkish.getSynSetWithLiteral("acı badem kurabiyesi", 1));
        assertNotNull(turkish.getSynSetWithLiteral("açık kapı siyaseti", 1));
        assertNotNull(turkish.getSynSetWithLiteral("bir baştan bir başa", 1));
        assertNotNull(turkish.getSynSetWithLiteral("eş zamanlı dil bilimi", 1));
        assertNotNull(turkish.getSynSetWithLiteral("bir iğne bir iplik olmak", 1));
        assertNotNull(turkish.getSynSetWithLiteral("yedi kat yerin dibine geçmek", 2));
        assertNotNull(turkish.getSynSetWithLiteral("kedi gibi dört ayak üzerine düşmek", 1));
        assertNotNull(turkish.getSynSetWithLiteral("bir kulağından girip öbür kulağından çıkmak", 1));
        assertNotNull(turkish.getSynSetWithLiteral("anasından emdiği süt burnundan fitil fitil gelmek", 1));
        assertNotNull(turkish.getSynSetWithLiteral("bir ayak üstünde kırk yalanın belini bükmek", 1));
    }

    @Test
    public void testNumberOfSynSetsWithLiteral() {
        assertEquals(1, turkish.numberOfSynSetsWithLiteral("yolcu etmek"));
        assertEquals(2, turkish.numberOfSynSetsWithLiteral("açık pembe"));
        assertEquals(3, turkish.numberOfSynSetsWithLiteral("bürokrasi"));
        assertEquals(4, turkish.numberOfSynSetsWithLiteral("bordür"));
        assertEquals(5, turkish.numberOfSynSetsWithLiteral("duygulanım"));
        assertEquals(6, turkish.numberOfSynSetsWithLiteral("sarsıntı"));
        assertEquals(7, turkish.numberOfSynSetsWithLiteral("kuvvetli"));
        assertEquals(8, turkish.numberOfSynSetsWithLiteral("merkez"));
        assertEquals(9, turkish.numberOfSynSetsWithLiteral("yüksek"));
        assertEquals(10, turkish.numberOfSynSetsWithLiteral("biçim"));
        assertEquals(11, turkish.numberOfSynSetsWithLiteral("yurt"));
        assertEquals(12, turkish.numberOfSynSetsWithLiteral("iğne"));
        assertEquals(13, turkish.numberOfSynSetsWithLiteral("kol"));
        assertEquals(14, turkish.numberOfSynSetsWithLiteral("alem"));
        assertEquals(15, turkish.numberOfSynSetsWithLiteral("taban"));
        assertEquals(16, turkish.numberOfSynSetsWithLiteral("yer"));
        assertEquals(17, turkish.numberOfSynSetsWithLiteral("ağır"));
        assertEquals(18, turkish.numberOfSynSetsWithLiteral("iş"));
        assertEquals(19, turkish.numberOfSynSetsWithLiteral("dökmek"));
        assertEquals(20, turkish.numberOfSynSetsWithLiteral("kaldırmak"));
        assertEquals(21, turkish.numberOfSynSetsWithLiteral("girmek"));
        assertEquals(22, turkish.numberOfSynSetsWithLiteral("gitmek"));
        assertEquals(23, turkish.numberOfSynSetsWithLiteral("vermek"));
        assertEquals(24, turkish.numberOfSynSetsWithLiteral("olmak"));
        assertEquals(25, turkish.numberOfSynSetsWithLiteral("bırakmak"));
        assertEquals(26, turkish.numberOfSynSetsWithLiteral("çıkarmak"));
        assertEquals(27, turkish.numberOfSynSetsWithLiteral("kesmek"));
        assertEquals(28, turkish.numberOfSynSetsWithLiteral("açmak"));
        assertEquals(33, turkish.numberOfSynSetsWithLiteral("düşmek"));
        assertEquals(38, turkish.numberOfSynSetsWithLiteral("atmak"));
        assertEquals(39, turkish.numberOfSynSetsWithLiteral("geçmek"));
        assertEquals(44, turkish.numberOfSynSetsWithLiteral("çekmek"));
        assertEquals(51, turkish.numberOfSynSetsWithLiteral("tutmak"));
        assertEquals(59, turkish.numberOfSynSetsWithLiteral("çıkmak"));
    }

    @Test
    public void testGetSynSetsWithPartOfSpeech() {
        assertEquals(43829, turkish.getSynSetsWithPartOfSpeech(Pos.NOUN).size());
        assertEquals(17595, turkish.getSynSetsWithPartOfSpeech(Pos.VERB).size());
        assertEquals(12319, turkish.getSynSetsWithPartOfSpeech(Pos.ADJECTIVE).size());
        assertEquals(2516, turkish.getSynSetsWithPartOfSpeech(Pos.ADVERB).size());
        assertEquals(339, turkish.getSynSetsWithPartOfSpeech(Pos.INTERJECTION).size());
        assertEquals(68, turkish.getSynSetsWithPartOfSpeech(Pos.PRONOUN).size());
        assertEquals(60, turkish.getSynSetsWithPartOfSpeech(Pos.CONJUNCTION).size());
        assertEquals(29, turkish.getSynSetsWithPartOfSpeech(Pos.PREPOSITION).size());
    }

    @Test
    public void testGetLiteralsWithPossibleModifiedLiteral() {
        WordNet english = new WordNet("english_wordnet_version_31.xml");
        assertTrue(english.getLiteralsWithPossibleModifiedLiteral("went").contains("go"));
        assertTrue(english.getLiteralsWithPossibleModifiedLiteral("going").contains("go"));
        assertTrue(english.getLiteralsWithPossibleModifiedLiteral("gone").contains("go"));
        assertTrue(english.getLiteralsWithPossibleModifiedLiteral("was").contains("be"));
        assertTrue(english.getLiteralsWithPossibleModifiedLiteral("were").contains("be"));
        assertTrue(english.getLiteralsWithPossibleModifiedLiteral("been").contains("be"));
        assertTrue(english.getLiteralsWithPossibleModifiedLiteral("had").contains("have"));
        assertTrue(english.getLiteralsWithPossibleModifiedLiteral("played").contains("play"));
        assertTrue(english.getLiteralsWithPossibleModifiedLiteral("plays").contains("play"));
        assertTrue(english.getLiteralsWithPossibleModifiedLiteral("oranges").contains("orange"));
        assertTrue(english.getLiteralsWithPossibleModifiedLiteral("better").contains("good"));
        assertTrue(english.getLiteralsWithPossibleModifiedLiteral("better").contains("well"));
        assertTrue(english.getLiteralsWithPossibleModifiedLiteral("best").contains("good"));
        assertTrue(english.getLiteralsWithPossibleModifiedLiteral("best").contains("well"));
        assertTrue(english.getLiteralsWithPossibleModifiedLiteral("worse").contains("bad"));
        assertTrue(english.getLiteralsWithPossibleModifiedLiteral("worst").contains("bad"));
        assertTrue(english.getLiteralsWithPossibleModifiedLiteral("uglier").contains("ugly"));
        assertTrue(english.getLiteralsWithPossibleModifiedLiteral("ugliest").contains("ugly"));
        assertTrue(english.getLiteralsWithPossibleModifiedLiteral("buses").contains("bus"));
        assertTrue(english.getLiteralsWithPossibleModifiedLiteral("flies").contains("fly"));
        assertTrue(english.getLiteralsWithPossibleModifiedLiteral("leaves").contains("leaf"));
    }

    @Test
    public void testGetInterlingual() {
        assertEquals(1, turkish.getInterlingual("ENG31-05674544-n").size());
        assertEquals(2, turkish.getInterlingual("ENG31-00220161-r").size());
        assertEquals(3, turkish.getInterlingual("ENG31-02294200-v").size());
        assertEquals(4, turkish.getInterlingual("ENG31-06205574-n").size());
        assertEquals(5, turkish.getInterlingual("ENG31-02687605-v").size());
        assertEquals(6, turkish.getInterlingual("ENG31-01099197-n").size());
        assertEquals(7, turkish.getInterlingual("ENG31-00587299-n").size());
        assertEquals(9, turkish.getInterlingual("ENG31-02214901-v").size());
        assertEquals(10, turkish.getInterlingual("ENG31-02733337-v").size());
        assertEquals(19, turkish.getInterlingual("ENG31-00149403-v").size());
    }

    @Test
    public void testSameLiteralSameSenseCheck() {
        assertEquals(0, turkish.sameLiteralSameSenseCheck().size());
    }

    @Test
    public void testSameLiteralSameSynSetCheck() {
        assertEquals(0, turkish.sameLiteralSameSynSetCheck().size());
    }

    @Test
    public void testNoPosCheck() {
        assertEquals(0, turkish.noPosCheck().size());
    }

    @Test
    public void testNoDefinitionCheck() {
        assertEquals(0, turkish.noDefinitionCheck().size());
    }

    @Test
    public void testSemanticRelationRelatedToNonExistingSynSetCheck() {
        assertEquals(0, turkish.semanticRelationRelatedToNonExistingSynSetCheck(false).size());
    }

    @Test
    public void testSameSemanticRelationCheck() {
        assertEquals(0, turkish.sameSemanticRelationCheck(false).size());
    }

    @Test
    public void testInbreeedingRelationCheck() {
        assertEquals(0, turkish.inbreeedingRelationCheck(false).size());
    }

    @Test
    public void testNoReverseRelationCheck() {
        assertEquals(0, turkish.noReverseRelationCheck(false).size());
    }

    @Test
    public void testSize() {
        assertEquals(76755, turkish.size());
    }

    @Test
    public void testFindPathToRoot() {
        assertEquals(1, turkish.findPathToRoot(turkish.getSynSetWithId("TUR10-0814560")).size());
        assertEquals(2, turkish.findPathToRoot(turkish.getSynSetWithId("TUR10-0755370")).size());
        assertEquals(3, turkish.findPathToRoot(turkish.getSynSetWithId("TUR10-0516010")).size());
        assertEquals(4, turkish.findPathToRoot(turkish.getSynSetWithId("TUR10-0012910")).size());
        assertEquals(5, turkish.findPathToRoot(turkish.getSynSetWithId("TUR10-0046370")).size());
        assertEquals(6, turkish.findPathToRoot(turkish.getSynSetWithId("TUR10-0186560")).size());
        assertEquals(7, turkish.findPathToRoot(turkish.getSynSetWithId("TUR10-0172740")).size());
        assertEquals(8, turkish.findPathToRoot(turkish.getSynSetWithId("TUR10-0195110")).size());
        assertEquals(9, turkish.findPathToRoot(turkish.getSynSetWithId("TUR10-0285060")).size());
        assertEquals(10, turkish.findPathToRoot(turkish.getSynSetWithId("TUR10-0066050")).size());
        assertEquals(11, turkish.findPathToRoot(turkish.getSynSetWithId("TUR10-0226380")).size());
        assertEquals(12, turkish.findPathToRoot(turkish.getSynSetWithId("TUR10-0490230")).size());
        assertEquals(13, turkish.findPathToRoot(turkish.getSynSetWithId("TUR10-1198750")).size());
        assertEquals(14, turkish.findPathToRoot(turkish.getSynSetWithId("TUR10-0412120")).size());
        assertEquals(15, turkish.findPathToRoot(turkish.getSynSetWithId("TUR10-1116690")).size());
        assertEquals(13, turkish.findPathToRoot(turkish.getSynSetWithId("TUR10-0621870")).size());
        assertEquals(14, turkish.findPathToRoot(turkish.getSynSetWithId("TUR10-0822980")).size());
        assertEquals(15, turkish.findPathToRoot(turkish.getSynSetWithId("TUR10-0178450")).size());
        assertEquals(16, turkish.findPathToRoot(turkish.getSynSetWithId("TUR10-0600460")).size());
        assertEquals(17, turkish.findPathToRoot(turkish.getSynSetWithId("TUR10-0656390")).size());
    }
}