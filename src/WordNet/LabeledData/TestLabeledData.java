package WordNet.LabeledData;

import DataStructure.Graph.Graph;
import DataStructure.Graph.GraphList;
import DataStructure.Graph.Node;
import WordNet.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

public class TestLabeledData {

    private static void displayComponents(GraphList<SynSet> components){
        String result = "";
        for (int i = 0; i < components.size(); i++){
            Graph<SynSet> graph = components.getGraph(i);
            String component = "";
            for (Node<SynSet> node : graph.nodeList()){
                component = component + " " + node.getLabel().getId();
            }
            result = result + component + "\n";
        }
        System.out.println(result);
    }

    public static void testSynonym(){
        SynonymLabeledData labeledData = new SynonymLabeledData(new WordNet());
        labeledData.addPath("../WordNet/synonym-students", new Locale("tr"));
        labeledData.addPath("../WordNet/synonym2-students", new Locale("tr"));
        labeledData.addPath("../WordNet/farkli-matches", new Locale("tr"));
        labeledData.addPath("../WordNet/farkli-matches", new Locale("tr"));
        ArrayList<Double> edgeWeights = labeledData.distinctEdgeWeights(0.0001);
        HashSet<String> skipList = new HashSet<>();
        GraphList<SynSet> previousComponents = new GraphList<SynSet>();
        for (int i = edgeWeights.size() - 1; i >= 0; i--){
            double threshold = edgeWeights.get(i);
            Graph<SynSet> synSetGraph = labeledData.createGraph(threshold, 0.0001, skipList);
            GraphList<SynSet> connectedComponents = synSetGraph.connectedComponents();
            HashSet<Node<SynSet>> nodeList = connectedComponents.nodeList();
            skipList.addAll(nodeList.stream().map(node -> node.getLabel().getId()).collect(Collectors.toList()));
            previousComponents = previousComponents.union(connectedComponents);
            previousComponents.sortWrtNodeSize();
        }
        displayComponents(previousComponents);
    }

    public static void extractInterlingualRelations(){
        WordNet turkish = new WordNet();
        WordNet english = new WordNet("Data/Wordnet/english_wordnet_version_31.xml", "Data/Wordnet/english_exception.xml", new Locale("en"));
        InterlinguaLabeledData labeledData = new InterlinguaLabeledData(english, turkish);
        labeledData.addPath("../WordNet/interlingua", new Locale("en"), new Locale("tr"), turkish);
        labeledData.addPath("../WordNet/interlingua2", new Locale("en"), new Locale("tr"), turkish);
        labeledData.addPath("../WordNet/interlingua-farkli", new Locale("en"), new Locale("tr"), turkish);
        HashMap<SynSet, ArrayList<SynSet>> synSetMap = labeledData.extractSynSetMap();
        for (SynSet synSet : synSetMap.keySet()){
            ArrayList<SynSet> list = synSetMap.get(synSet);
            for (SynSet related : list){
                if (!related.getInterlingual().contains(synSet.getId())){
                    InterlingualRelation relation = new InterlingualRelation(synSet.getId(), "SYNONYM");
                    related.addRelation(relation);
                }
            }
        }
        turkish.saveAsXml("turkish_new.xml");
    }

    public static void totalCharacters(String fileName){
        WordNet turkish = new WordNet();
        WordNet english = new WordNet("Data/Wordnet/english_wordnet_version_31.xml", "Data/Wordnet/english_exception.xml", new Locale("en"));
        int total = 0;
        try {
            Scanner scanner = new Scanner(new File(fileName));
            while (scanner.hasNext()){
                String line = scanner.next();
                String[] words = line.split(";");
                if (words.length == 2){
                    ArrayList<SynSet> englishSynSets = english.getSynSetsWithLiteral(words[0]);
                    ArrayList<SynSet> turkishSynSets = turkish.getSynSetsWithLiteral(words[1]);
                    for (SynSet synSet : englishSynSets){
                        if (synSet.getDefinition() != null){
                            total += synSet.getDefinition().trim().length();
                        }
                    }
                    for (SynSet synSet : turkishSynSets){
                        if (synSet.getDefinition() != null){
                            total += synSet.getDefinition().trim().length();
                        }
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        System.out.println(total);
    }

    public static void main(String[] args){
        extractInterlingualRelations();
    }
}
