package WordNet;

import org.junit.Before;

import java.util.ArrayList;
import java.util.Locale;

public class WordNet1983Test extends PreviousWordNetTest{

    @Before
    public void setUp() {
        previuosWordNet = new WordNet("turkish1983_wordnet.xml", new Locale("tr"));
    }

    public void findMatchingLiteralsInPreviousWordNets(){
        WordNet turkish44 = new WordNet("turkish1944_wordnet.xml", new Locale("tr"));
        for (SynSet synSet1 : previuosWordNet.synSetList()){
            if (synSet1.getId().startsWith("TUR07-")){
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
            if (synSet1.getId().startsWith("TUR07-")){
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
            if (synSet1.getId().startsWith("TUR07-")){
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
            if (synSet1.getId().startsWith("TUR07-")){
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
            if (synSet1.getId().startsWith("TUR07-")){
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
            if (synSet1.getId().startsWith("TUR07-")){
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
        WordNet turkish = new WordNet();
        for (SynSet synSet1 : previuosWordNet.synSetList()){
            if (synSet1.getId().startsWith("TUR07-")){
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
            if (synSet1.getId().startsWith("TUR07-")){
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
            if (synSet1.getId().startsWith("TUR07-")){
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
            if (synSet1.getId().startsWith("TUR07-")){
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
            if (synSet1.getId().startsWith("TUR07-")){
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
            if (synSet1.getId().startsWith("TUR07-")){
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
            if (synSet1.getId().startsWith("TUR07-")){
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
        WordNet turkish = new WordNet();
        for (SynSet synSet1 : previuosWordNet.synSetList()){
            if (synSet1.getId().startsWith("TUR07-")){
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
