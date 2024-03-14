package WordNet.LabeledData;

import DataStructure.CounterHashMap;
import DataStructure.Graph.Graph;
import DataStructure.Graph.Node;
import WordNet.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map.*;

public class SynonymLabeledData extends LabeledData{
    private final WordNet wordNet;

    public SynonymLabeledData(WordNet wordNet){
        this.wordNet = wordNet;
    }

    public Graph<SynSet> createGraph(double minConfidence, double alpha, HashSet<String> skipList){
        Graph<SynSet> synSetGraph;
        synSetGraph = new Graph<>(false);
        for (Entry<WordPair, CounterHashMap<WordPair>> entry: data.entrySet()){
            CounterHashMap<WordPair> labeledRow = entry.getValue();
            int sum = labeledRow.sumOfCounts();
            for (WordPair label : labeledRow.keySet()){
                if (label.leftWord != null && label.rightWord != null && !skipList.contains(label.leftWord) && !skipList.contains(label.rightWord)){
                    SynSet left = wordNet.getSynSetWithId(label.leftWord);
                    SynSet right = wordNet.getSynSetWithId(label.rightWord);
                    if (left != null && right != null && !left.equals(right) && left.getPos() == right.getPos()){
                        Node<SynSet> fromNode = new Node<>(left);
                        Node<SynSet> toNode = new Node<>(right);
                        double confidence = labeledRow.count(label) / (sum + alpha);
                        if (confidence >= minConfidence) {
                            synSetGraph.addEdge(fromNode, toNode, confidence);
                        }
                    }
                }
            }
        }
        return synSetGraph;
    }

    public Graph<SynSet> createGraph(double minConfidence, double alpha){
        return createGraph(minConfidence, alpha, new HashSet<>());
    }

    public ArrayList<Double> distinctEdgeWeights(double alpha){
        ArrayList<Double> result = new ArrayList<>();
        for (CounterHashMap<WordPair> labeledRow: data.values()){
            int sum = labeledRow.sumOfCounts();
            for (WordPair label : labeledRow.keySet()){
                if (label.leftWord != null && label.rightWord != null){
                    double confidence = labeledRow.count(label) / (sum + alpha);
                    if (!result.contains(confidence)){
                        result.add(confidence);
                    }
                }
            }
        }
        Collections.sort(result);
        return result;
    }
}
