package WordNet;

import DataStructure.CounterHashMap;
import Dictionary.Pos;
import Dictionary.TurkishWordComparator;
import Dictionary.TxtDictionary;
import Dictionary.TxtWord;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;


public class WordNetTest {
    WordNet turkish;

    @Before
    public void setUp() {
        turkish = new WordNet();
    }

    @Test
    public void convertToLmf() {
        turkish.saveAsLmf("turkish.lmf");
    }

    @Test
    public void testSize() {
        assertEquals(78327, turkish.size());
    }

    @Test
    public void testSynSetList() {
        int literalCount = 0;
        for (SynSet synSet : turkish.synSetList()){
            literalCount += synSet.getSynonym().literalSize();
        }
        assertEquals(110259, literalCount);
    }

    @Test
    public void testLiteralSortedList() {
        assertTrue(turkish.literalCorrectOrderCheck());
    }

    @Test
    public void testTotalForeignLiterals() {
        int count = 0;
        for (SynSet synSet : turkish.synSetList()){
            for (int i = 0; i < synSet.getSynonym().literalSize(); i++){
                if (synSet.getSynonym().getLiteral(i).getOrigin() != null){
                    count++;
                }
            }
        }
        assertEquals(3981, count);
    }

    @Test
    public void testTotalGroupedLiterals() {
        int count = 0;
        for (SynSet synSet : turkish.synSetList()){
            for (int i = 0; i < synSet.getSynonym().literalSize(); i++){
                if (synSet.getSynonym().getLiteral(i).getGroupNo() != 0){
                    count++;
                }
            }
        }
        assertEquals(5973, count);
    }

    @Test
    public void testGroupSize() {
        CounterHashMap<Integer> groups = new CounterHashMap<>();
        for (SynSet synSet : turkish.synSetList()){
            ArrayList<Synonym> literalGroups = synSet.getSynonym().getUniqueLiterals();
            for (Synonym synonym : literalGroups){
                if (synonym.getLiteral(0).getGroupNo() != 0){
                    groups.put(synonym.literalSize());
                }
            }
        }
        assertEquals(0, groups.count(1));
        assertEquals(2949, groups.count(2));
        assertEquals(21, groups.count(3));
        assertEquals(3, groups.count(4));
    }

    @Test
    public void testNumberOfGroupsInSynSet() {
        CounterHashMap<Integer> groups = new CounterHashMap<>();
        for (SynSet synSet : turkish.synSetList()){
            ArrayList<Synonym> literalGroups = synSet.getSynonym().getUniqueLiterals();
            int groupCount = 0;
            for (Synonym synonym : literalGroups){
                if (synonym.getLiteral(0).getGroupNo() != 0){
                    groupCount++;
                }
            }
            groups.put(groupCount);
        }
        assertEquals(2674, groups.count(1));
        assertEquals(125, groups.count(2));
        assertEquals(12, groups.count(3));
        assertEquals(2, groups.count(4));
        assertEquals(1, groups.count(5));
        assertEquals(0, groups.count(6));
    }

    @Test
    public void testDistinctForeignLiterals() {
        int count = 0;
        for (String literalName : turkish.literalList()){
            ArrayList<Literal> literals = turkish.getLiteralsWithName(literalName);
            int foreignCount = 0;
            int notForeignCount = 0;
            for (Literal literal : literals){
                if (literal.getOrigin() != null){
                    foreignCount++;
                } else {
                    notForeignCount++;
                }
            }
            if (foreignCount * notForeignCount > 0){
                count++;
            }
        }
        assertEquals(count, 5);
    }

    @Test
    public void testLiteralList() {
        assertEquals(82155, turkish.literalList().size());
    }

    public void generateDictionary(){
        String[] flags = {"IS_SD", "IS_KG", "IS_UD", "IS_UU", "IS_UUU",
                "IS_SU", "IS_ST", "F_SD", "F_GUD", "F_GUDO", "IS_SDD",
                "F1P1", "F2P1", "F2PL", "F2P1-NO-REF", "F3P1-NO-REF",
                "F4P1-NO-REF", "F4PR-NO-REF", "F4PL-NO-REF", "F4PW-NO-REF", "F5PL-NO-REF",
                "F5PR-NO-REF", "F5PW-NO-REF", "F2P1", "F3P1", "F4P1",
                "F4PR", "F4PL", "F4PW", "F5P1", "F5PL",
                "F5PR", "F5PW", "F6P1", "IS_KU", "IS_BILEŞ",
                "IS_B_SD", "IS_KI", "IS_STT", "IS_UDD", "IS_CA", "IS_KIS",
                "IS_EX", "CL_NONE", "IS_B_SI", "IS_SAYI"};
        WordNet turkishWordNet = new WordNet();
        TxtDictionary turkish = new TxtDictionary();
        TxtDictionary dictionary = new TxtDictionary(new TurkishWordComparator());
        for (int i = 0; i < turkish.size(); i++){
            TxtWord txtWord = (TxtWord) turkish.getWord(i);
            if (txtWord.containsFlag("IS_OA")){
                dictionary.addProperNoun(txtWord.getName());
            }
            if (txtWord.containsFlag("IS_QUES")){
                dictionary.addWithFlag(txtWord.getName(), "IS_QUES");
            }
        }
        for (String literal : turkishWordNet.literalList()){
            if (!literal.contains(" ")){
                TxtWord txtWord = (TxtWord) turkish.getWord(literal);
                if (txtWord != null){
                    for (String flag: flags){
                        if (txtWord.containsFlag(flag)){
                            dictionary.addWithFlag(literal, flag);
                        }
                    }
                }
                if (literal.endsWith("mek") || literal.endsWith("mak")){
                    txtWord = (TxtWord) turkish.getWord(literal.substring(0, literal.length() - 3));
                    if (txtWord != null){
                        for (String flag: flags){
                            if (txtWord.containsFlag(flag)){
                                dictionary.addWithFlag(literal.substring(0, literal.length() - 3), flag);
                            }
                        }
                    }
                }
                ArrayList<SynSet> synSets = turkishWordNet.getSynSetsWithLiteral(literal);
                for (SynSet synSet : synSets){
                    switch (synSet.getPos()){
                        case NOUN:
                            dictionary.addNoun(literal);
                            break;
                        case VERB:
                            dictionary.addVerb(literal.substring(0, literal.length() - 3));
                            break;
                        case ADJECTIVE:
                            dictionary.addAdjective(literal);
                            break;
                        case ADVERB:
                            dictionary.addAdverb(literal);
                            break;
                        case PRONOUN:
                            dictionary.addPronoun(literal);
                            break;
                        case CONJUNCTION:
                            dictionary.addWithFlag(literal, "IS_CONJ");
                            break;
                        case INTERJECTION:
                            dictionary.addWithFlag(literal, "IS_INTERJ");
                            break;
                        case PREPOSITION:
                            dictionary.addWithFlag(literal, "IS_POSTP");
                            break;
                    }
                }
            } else {
                String[] words = literal.split(" ");
                if (words.length == 2){
                    if (words[0].equals(words[1])){
                        dictionary.addWithFlag(words[0], "IS_DUP");
                    } else {
                        if (!words[0].endsWith("mek") && !words[1].endsWith("mak") &&
                                words[0].length() > 3 && words[1].length() > 3 &&
                                words[0].substring(words[0].length() - 3).equals(words[1].substring(words[1].length() - 3))){
                            dictionary.addWithFlag(words[0], "IS_DUP");
                            dictionary.addWithFlag(words[1], "IS_DUP");
                        } else {
                            if (words[0].length() == words[1].length()){
                                int count = 0;
                                for (int j = 0; j < words[0].length(); j++){
                                    if (words[0].charAt(j) != words[1].charAt(j)){
                                        count++;
                                    }
                                }
                                if (count == 1){
                                    dictionary.addWithFlag(words[0], "IS_DUP");
                                    dictionary.addWithFlag(words[1], "IS_DUP");
                                }
                            }
                        }
                    }
                }
            }
        }
        dictionary.saveAsTxt("turkish_dictionary_new.txt");
    }

    @Test
    public void testLiteralWordCounts() {
        CounterHashMap<Integer> counts = new CounterHashMap<>();
        for (String literal : turkish.literalList()){
            int count = 1;
            for (int i = 0; i < literal.length(); i++){
                if (literal.charAt(i) == ' '){
                    count++;
                }
            }
            counts.put(count);
        }
        assertEquals(48715, (int) counts.get(1));
        assertEquals(28424, (int) counts.get(2));
        assertEquals(3558, (int) counts.get(3));
        assertEquals(910, (int) counts.get(4));
        assertEquals(304, (int) counts.get(5));
        assertEquals(137, (int) counts.get(6));
        assertEquals(57, (int) counts.get(7));
        assertEquals(32, (int) counts.get(8));
        assertEquals(9, (int) counts.get(9));
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
        assertEquals(50, turkish.numberOfSynSetsWithLiteral("tutmak"));
        assertEquals(59, turkish.numberOfSynSetsWithLiteral("çıkmak"));
    }

    @Test
    public void testGetSynSetsWithPartOfSpeech() {
        assertEquals(43882, turkish.getSynSetsWithPartOfSpeech(Pos.NOUN).size());
        assertEquals(17773, turkish.getSynSetsWithPartOfSpeech(Pos.VERB).size());
        assertEquals(12406, turkish.getSynSetsWithPartOfSpeech(Pos.ADJECTIVE).size());
        assertEquals(2549, turkish.getSynSetsWithPartOfSpeech(Pos.ADVERB).size());
        assertEquals(1552, turkish.getSynSetsWithPartOfSpeech(Pos.INTERJECTION).size());
        assertEquals(74, turkish.getSynSetsWithPartOfSpeech(Pos.PRONOUN).size());
        assertEquals(61, turkish.getSynSetsWithPartOfSpeech(Pos.CONJUNCTION).size());
        assertEquals(30, turkish.getSynSetsWithPartOfSpeech(Pos.PREPOSITION).size());
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
        ArrayList<Literal> literals = turkish.sameLiteralSameSenseCheck();
        assertEquals(0, literals.size());
    }

    @Test
    public void testSameLiteralSameSynSetCheck() {
        for (SynSet synSet : turkish.sameLiteralSameSynSetCheck()){
            System.out.println(synSet.getId());
        }
        assertEquals(0, turkish.sameLiteralSameSynSetCheck().size());
    }

    @Test
    public void testNoPosCheck() {
        assertEquals(0, turkish.noPosCheck().size());
    }

    @Test
    public void testWikiPages() {
        int count = 0;
        for (SynSet synSet: turkish.synSetList()){
            if (synSet.getWikiPage() != null){
                count++;
            }
        }
        assertEquals(11001, count);
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
        assertEquals(12, turkish.findPathToRoot(turkish.getSynSetWithId("TUR10-0412120")).size());
        assertEquals(13, turkish.findPathToRoot(turkish.getSynSetWithId("TUR10-1116690")).size());
        assertEquals(13, turkish.findPathToRoot(turkish.getSynSetWithId("TUR10-0621870")).size());
        assertEquals(14, turkish.findPathToRoot(turkish.getSynSetWithId("TUR10-0822980")).size());
        assertEquals(15, turkish.findPathToRoot(turkish.getSynSetWithId("TUR10-0178450")).size());
        assertEquals(16, turkish.findPathToRoot(turkish.getSynSetWithId("TUR10-0600460")).size());
        assertEquals(17, turkish.findPathToRoot(turkish.getSynSetWithId("TUR10-0656390")).size());
    }

}