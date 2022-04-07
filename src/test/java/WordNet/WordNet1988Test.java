package WordNet;

import DataStructure.CounterHashMap;
import Dictionary.Pos;
import Dictionary.TurkishWordComparator;
import Dictionary.TxtDictionary;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class WordNet1988Test extends PreviousWordNetTest{

    @Before
    public void setUp() {
        previuosWordNet = new WordNet("turkish1988_wordnet.xml", new Locale("tr"));
    }

    @Test
    public void testExample() {
        previousDictionary = new TxtDictionary("turkish1988_dictionary.txt", new TurkishWordComparator());
        super.testExample();
    }

    @Test
    public void testSize() {
        assertEquals(57902, previuosWordNet.size());
    }

    @Test
    public void testSynSetIdCounts() {
        CounterHashMap<String> counts = synSetIdCounts();
        assertEquals(1749, (int) counts.get("TUR01"));
        assertEquals(1158, (int) counts.get("TUR02"));
        assertEquals(249, (int) counts.get("TUR03"));
        assertEquals(121, (int) counts.get("TUR04"));
        assertEquals(496, (int) counts.get("TUR05"));
        assertEquals(687, (int) counts.get("TUR06"));
        assertEquals(1466, (int) counts.get("TUR07"));
        assertEquals(717, (int) counts.get("TUR08"));
        assertEquals(51259, (int) counts.get("TUR10"));
    }

    @Test
    public void generateDictionary() {
        generateDictionary("1988");
    }

    @Test
    public void testSynSetList() {
        int literalCount = 0;
        for (SynSet synSet : previuosWordNet.synSetList()){
            literalCount += synSet.getSynonym().literalSize();
        }
        assertEquals(75786, literalCount);
    }

    @Test
    public void testGetSynSetsWithPartOfSpeech() {
        assertEquals(30112, previuosWordNet.getSynSetsWithPartOfSpeech(Pos.NOUN).size());
        assertEquals(14193, previuosWordNet.getSynSetsWithPartOfSpeech(Pos.VERB).size());
        assertEquals(9697, previuosWordNet.getSynSetsWithPartOfSpeech(Pos.ADJECTIVE).size());
        assertEquals(1952, previuosWordNet.getSynSetsWithPartOfSpeech(Pos.ADVERB).size());
        assertEquals(1743, previuosWordNet.getSynSetsWithPartOfSpeech(Pos.INTERJECTION).size());
        assertEquals(79, previuosWordNet.getSynSetsWithPartOfSpeech(Pos.CONJUNCTION).size());
        assertEquals(68, previuosWordNet.getSynSetsWithPartOfSpeech(Pos.PRONOUN).size());
        assertEquals(58, previuosWordNet.getSynSetsWithPartOfSpeech(Pos.PREPOSITION).size());
    }

    @Test
    public void testLiteralSortedList() {
        assertTrue(previuosWordNet.literalCorrectOrderCheck());
    }

    @Test
    public void testLiteralList() {
        assertEquals(53957, previuosWordNet.literalList().size());
    }

    @Test
    public void testLiteralWordCounts() {
        CounterHashMap<Integer> counts = literalWordCounts();
        assertEquals(33643, (int) counts.get(1));
        assertEquals(16454, (int) counts.get(2));
        assertEquals(2463, (int) counts.get(3));
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

    public void findMatchingLiteralsInPreviousWordNets(){
        WordNet turkish44 = new WordNet("turkish1944_wordnet.xml", new Locale("tr"));
        for (SynSet synSet1 : previuosWordNet.synSetList()){
            if (synSet1.getId().startsWith("TUR08-")){
                for (SynSet synSet2: turkish44.synSetList()){
                    if (synSet2.getId().startsWith("TUR01-") && synSet1.getPos().equals(synSet2.getPos())){
                        for (int i = 0; i < synSet1.getSynonym().literalSize(); i++) {
                            String literal1 = synSet1.getSynonym().getLiteral(i).getName();
                            for (int j = 0; j < synSet2.getSynonym().literalSize(); j++) {
                                String literal2 = synSet2.getSynonym().getLiteral(j).getName();
                                if (literal1.equalsIgnoreCase(literal2)) {
                                    System.out.println(literal1 + "\t" + synSet1.getId() + "\t" + synSet1.getPos() + "\t" + synSet1.getLongDefinition());
                                    System.out.println(literal1 + "\t" + synSet2.getId() + "\t" + synSet2.getPos() + "\t" + synSet2.getLongDefinition());
                                }
                            }
                        }
                    }
                }
            }
        }
        WordNet turkish55 = new WordNet("turkish1955_wordnet.xml", new Locale("tr"));
        for (SynSet synSet1 : previuosWordNet.synSetList()){
            if (synSet1.getId().startsWith("TUR08-")){
                for (SynSet synSet2: turkish55.synSetList()){
                    if (synSet2.getId().startsWith("TUR02-") && synSet1.getPos().equals(synSet2.getPos())){
                        for (int i = 0; i < synSet1.getSynonym().literalSize(); i++) {
                            String literal1 = synSet1.getSynonym().getLiteral(i).getName();
                            for (int j = 0; j < synSet2.getSynonym().literalSize(); j++) {
                                String literal2 = synSet2.getSynonym().getLiteral(j).getName();
                                if (literal1.equalsIgnoreCase(literal2)) {
                                    System.out.println(literal1 + "\t" + synSet1.getId() + "\t" + synSet1.getPos() + "\t" + synSet1.getLongDefinition());
                                    System.out.println(literal1 + "\t" + synSet2.getId() + "\t" + synSet2.getPos() + "\t" + synSet2.getLongDefinition());
                                }
                            }
                        }
                    }
                }
            }
        }
        WordNet turkish59 = new WordNet("turkish1959_wordnet.xml", new Locale("tr"));
        for (SynSet synSet1 : previuosWordNet.synSetList()){
            if (synSet1.getId().startsWith("TUR08-")){
                for (SynSet synSet2: turkish59.synSetList()){
                    if (synSet2.getId().startsWith("TUR03-") && synSet1.getPos().equals(synSet2.getPos())){
                        for (int i = 0; i < synSet1.getSynonym().literalSize(); i++) {
                            String literal1 = synSet1.getSynonym().getLiteral(i).getName();
                            for (int j = 0; j < synSet2.getSynonym().literalSize(); j++) {
                                String literal2 = synSet2.getSynonym().getLiteral(j).getName();
                                if (literal1.equalsIgnoreCase(literal2)) {
                                    System.out.println(literal1 + "\t" + synSet1.getId() + "\t" + synSet1.getPos() + "\t" + synSet1.getLongDefinition());
                                    System.out.println(literal1 + "\t" + synSet2.getId() + "\t" + synSet2.getPos() + "\t" + synSet2.getLongDefinition());
                                }
                            }
                        }
                    }
                }
            }
        }
        WordNet turkish66 = new WordNet("turkish1966_wordnet.xml", new Locale("tr"));
        for (SynSet synSet1 : previuosWordNet.synSetList()){
            if (synSet1.getId().startsWith("TUR08-")){
                for (SynSet synSet2: turkish66.synSetList()){
                    if (synSet2.getId().startsWith("TUR04-") && synSet1.getPos().equals(synSet2.getPos())){
                        for (int i = 0; i < synSet1.getSynonym().literalSize(); i++) {
                            String literal1 = synSet1.getSynonym().getLiteral(i).getName();
                            for (int j = 0; j < synSet2.getSynonym().literalSize(); j++) {
                                String literal2 = synSet2.getSynonym().getLiteral(j).getName();
                                if (literal1.equalsIgnoreCase(literal2)) {
                                    System.out.println(literal1 + "\t" + synSet1.getId() + "\t" + synSet1.getPos() + "\t" + synSet1.getLongDefinition());
                                    System.out.println(literal1 + "\t" + synSet2.getId() + "\t" + synSet2.getPos() + "\t" + synSet2.getLongDefinition());
                                }
                            }
                        }
                    }
                }
            }
        }
        WordNet turkish69 = new WordNet("turkish1969_wordnet.xml", new Locale("tr"));
        for (SynSet synSet1 : previuosWordNet.synSetList()){
            if (synSet1.getId().startsWith("TUR08-")){
                for (SynSet synSet2: turkish69.synSetList()){
                    if (synSet2.getId().startsWith("TUR05-") && synSet1.getPos().equals(synSet2.getPos())){
                        for (int i = 0; i < synSet1.getSynonym().literalSize(); i++) {
                            String literal1 = synSet1.getSynonym().getLiteral(i).getName();
                            for (int j = 0; j < synSet2.getSynonym().literalSize(); j++) {
                                String literal2 = synSet2.getSynonym().getLiteral(j).getName();
                                if (literal1.equalsIgnoreCase(literal2)) {
                                    System.out.println(literal1 + "\t" + synSet1.getId() + "\t" + synSet1.getPos() + "\t" + synSet1.getLongDefinition());
                                    System.out.println(literal1 + "\t" + synSet2.getId() + "\t" + synSet2.getPos() + "\t" + synSet2.getLongDefinition());
                                }
                            }
                        }
                    }
                }
            }
        }
        WordNet turkish74 = new WordNet("turkish1974_wordnet.xml", new Locale("tr"));
        for (SynSet synSet1 : previuosWordNet.synSetList()){
            if (synSet1.getId().startsWith("TUR08-")){
                for (SynSet synSet2: turkish74.synSetList()){
                    if (synSet2.getId().startsWith("TUR06-") && synSet1.getPos().equals(synSet2.getPos())){
                        for (int i = 0; i < synSet1.getSynonym().literalSize(); i++) {
                            String literal1 = synSet1.getSynonym().getLiteral(i).getName();
                            for (int j = 0; j < synSet2.getSynonym().literalSize(); j++) {
                                String literal2 = synSet2.getSynonym().getLiteral(j).getName();
                                if (literal1.equalsIgnoreCase(literal2)) {
                                    System.out.println(literal1 + "\t" + synSet1.getId() + "\t" + synSet1.getPos() + "\t" + synSet1.getLongDefinition());
                                    System.out.println(literal1 + "\t" + synSet2.getId() + "\t" + synSet2.getPos() + "\t" + synSet2.getLongDefinition());
                                }
                            }
                        }
                    }
                }
            }
        }
        WordNet turkish83 = new WordNet("turkish1983_wordnet.xml", new Locale("tr"));
        for (SynSet synSet1 : previuosWordNet.synSetList()){
            if (synSet1.getId().startsWith("TUR08-")){
                for (SynSet synSet2: turkish74.synSetList()){
                    if (synSet2.getId().startsWith("TUR07-") && synSet1.getPos().equals(synSet2.getPos())){
                        for (int i = 0; i < synSet1.getSynonym().literalSize(); i++) {
                            String literal1 = synSet1.getSynonym().getLiteral(i).getName();
                            for (int j = 0; j < synSet2.getSynonym().literalSize(); j++) {
                                String literal2 = synSet2.getSynonym().getLiteral(j).getName();
                                if (literal1.equalsIgnoreCase(literal2)) {
                                    System.out.println(literal1 + "\t" + synSet1.getId() + "\t" + synSet1.getPos() + "\t" + synSet1.getLongDefinition());
                                    System.out.println(literal1 + "\t" + synSet2.getId() + "\t" + synSet2.getPos() + "\t" + synSet2.getLongDefinition());
                                }
                            }
                        }
                    }
                }
            }
        }
        WordNet turkish = new WordNet();
        for (SynSet synSet1 : previuosWordNet.synSetList()){
            if (synSet1.getId().startsWith("TUR08-")){
                for (SynSet synSet2: turkish.synSetList()){
                    if (synSet1.getPos().equals(synSet2.getPos())){
                        for (int i = 0; i < synSet1.getSynonym().literalSize(); i++) {
                            String literal1 = synSet1.getSynonym().getLiteral(i).getName();
                            for (int j = 0; j < synSet2.getSynonym().literalSize(); j++) {
                                String literal2 = synSet2.getSynonym().getLiteral(j).getName();
                                if (literal1.equalsIgnoreCase(literal2)) {
                                    System.out.println(literal1 + "\t" + synSet1.getId() + "\t" + synSet1.getPos() + "\t" + synSet1.getLongDefinition());
                                    System.out.println(literal1 + "\t" + synSet2.getId() + "\t" + synSet2.getPos() + "\t" + synSet2.getLongDefinition());
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void findMatchingSynSetsInPreviousWordNets(){
        WordNet turkish44 = new WordNet("turkish1944_wordnet.xml", new Locale("tr"));
        for (SynSet synSet1 : previuosWordNet.synSetList()){
            if (synSet1.getId().startsWith("TUR08-")){
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
            if (synSet1.getId().startsWith("TUR08-")){
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
        WordNet turkish59 = new WordNet("turkish1959_wordnet.xml", new Locale("tr"));
        for (SynSet synSet1 : previuosWordNet.synSetList()){
            if (synSet1.getId().startsWith("TUR08-")){
                for (SynSet synSet2: turkish59.synSetList()){
                    if (synSet2.getId().startsWith("TUR03-") && synSet1.getPos().equals(synSet2.getPos())){
                        double matchRatio = numberOfMatches(synSet1.getLongDefinition(), synSet2.getLongDefinition());
                        if (matchRatio >= 0.8){
                            System.out.println(matchRatio + "\t" + synSet1.getId() + "\t" + synSet2.getId() + "\t" + synSet1.getSynonym().toString() + "\t" + synSet2.getSynonym().toString() + "\t" + synSet1.getLongDefinition() + "\t" + synSet2.getLongDefinition());
                        }
                    }
                }
            }
        }
        WordNet turkish66 = new WordNet("turkish1966_wordnet.xml", new Locale("tr"));
        for (SynSet synSet1 : previuosWordNet.synSetList()){
            if (synSet1.getId().startsWith("TUR08-")){
                for (SynSet synSet2: turkish66.synSetList()){
                    if (synSet2.getId().startsWith("TUR04-") && synSet1.getPos().equals(synSet2.getPos())){
                        double matchRatio = numberOfMatches(synSet1.getLongDefinition(), synSet2.getLongDefinition());
                        if (matchRatio >= 0.8){
                            System.out.println(matchRatio + "\t" + synSet1.getId() + "\t" + synSet2.getId() + "\t" + synSet1.getSynonym().toString() + "\t" + synSet2.getSynonym().toString() + "\t" + synSet1.getLongDefinition() + "\t" + synSet2.getLongDefinition());
                        }
                    }
                }
            }
        }
        WordNet turkish69 = new WordNet("turkish1969_wordnet.xml", new Locale("tr"));
        for (SynSet synSet1 : previuosWordNet.synSetList()){
            if (synSet1.getId().startsWith("TUR08-")){
                for (SynSet synSet2: turkish69.synSetList()){
                    if (synSet2.getId().startsWith("TUR05-") && synSet1.getPos().equals(synSet2.getPos())){
                        double matchRatio = numberOfMatches(synSet1.getLongDefinition(), synSet2.getLongDefinition());
                        if (matchRatio >= 0.8){
                            System.out.println(matchRatio + "\t" + synSet1.getId() + "\t" + synSet2.getId() + "\t" + synSet1.getSynonym().toString() + "\t" + synSet2.getSynonym().toString() + "\t" + synSet1.getLongDefinition() + "\t" + synSet2.getLongDefinition());
                        }
                    }
                }
            }
        }
        WordNet turkish74 = new WordNet("turkish1974_wordnet.xml", new Locale("tr"));
        for (SynSet synSet1 : previuosWordNet.synSetList()){
            if (synSet1.getId().startsWith("TUR08-")){
                for (SynSet synSet2: turkish74.synSetList()){
                    if (synSet2.getId().startsWith("TUR06-") && synSet1.getPos().equals(synSet2.getPos())){
                        double matchRatio = numberOfMatches(synSet1.getLongDefinition(), synSet2.getLongDefinition());
                        if (matchRatio >= 0.8){
                            System.out.println(matchRatio + "\t" + synSet1.getId() + "\t" + synSet2.getId() + "\t" + synSet1.getSynonym().toString() + "\t" + synSet2.getSynonym().toString() + "\t" + synSet1.getLongDefinition() + "\t" + synSet2.getLongDefinition());
                        }
                    }
                }
            }
        }
        WordNet turkish83 = new WordNet("turkish1983_wordnet.xml", new Locale("tr"));
        for (SynSet synSet1 : previuosWordNet.synSetList()){
            if (synSet1.getId().startsWith("TUR08-")){
                for (SynSet synSet2: turkish83.synSetList()){
                    if (synSet2.getId().startsWith("TUR07-") && synSet1.getPos().equals(synSet2.getPos())){
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
            if (synSet1.getId().startsWith("TUR08-")){
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

    public void generate2020Data(){
        WordNet turkish = new WordNet();
        for (String literal : turkish.literalList()){
            ArrayList<SynSet> synSets1 = turkish.getSynSetsWithLiteral(literal);
            ArrayList<SynSet> synSets2 = previuosWordNet.getSynSetsWithLiteral(literal);
            for (SynSet synSet : synSets1){
                if (!synSets2.contains(synSet)){
                    System.out.println(literal + "\t" + synSet.getId() + "\t" + synSet.getPos() + "\t" + synSet.getLongDefinition() + "\t" + synSet.getSynonym() + "\t" + synSet.getExample());
                }
            }
        }
    }

}
