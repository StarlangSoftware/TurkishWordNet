package WordNet.Annotation;

import WordNet.SynSet;
import WordNet.WordNet;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;

public class SynSetMatcherPanel extends WordNetEditorPanel{

    private SynSet[][] synSets;
    private boolean[][] toBeRemoved;
    private final WordNet wordNet;

    public SynSetMatcherPanel(WordNet wordNet, String fileName){
        super(fileName);
        this.fileName = fileName;
        this.wordNet = wordNet;
        try {
            java.util.List<String> lines = Files.readAllLines(new File(fileName).toPath(), StandardCharsets.UTF_8);
            synSets = new SynSet[lines.size()][];
            toBeRemoved = new boolean[lines.size()][];
            int i = 0;
            for (String line : lines){
                String[] candidates = line.split("->");
                toBeRemoved[i] = new boolean[candidates.length];
                synSets[i] = new SynSet[candidates.length];
                int j = 0;
                for (String candidate: candidates){
                    synSets[i][j] = wordNet.getSynSetWithId(candidate);
                    toBeRemoved[i][j] = false;
                    j++;
                }
                i++;
            }
            displaySynSetCandidate();
        } catch (IOException ignored) {
        }
    }

    protected JTree createSynSetTree(final SynSet synSet, final int synSetIndex){
        TreeNode[] path;
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(synSet.representative() + " (" + synSet.getId() + ")");
        DefaultMutableTreeNode child = new DefaultMutableTreeNode(synSet);
        root.add(child);
        ArrayList<SynSet> synSetsList = wordNet.getSynSetsWithLiteral(synSet.representative());
        if (synSetsList.size() > 1){
            for (SynSet set : synSetsList) {
                DefaultMutableTreeNode grandChild = new DefaultMutableTreeNode(set);
                child.add(grandChild);
            }
        }
        final JTree tree;
        tree = new JTree(root);
        if (toBeRemoved[itemIndex][synSetIndex]){
            path = new TreeNode[1];
            path[0] = root;
        } else {
            path = new TreeNode[2];
            path[0] = root;
            path[1] = child;
        }
        tree.setSelectionPath(new TreePath(path));
        tree.addTreeSelectionListener(e -> {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            if (node.getLevel() == 0){
                toBeRemoved[itemIndex][synSetIndex] = true;
            } else {
                toBeRemoved[itemIndex][synSetIndex] = false;
                if (node.getLevel() == 2){
                    synSets[itemIndex][synSetIndex] = (SynSet) node.getUserObject();
                }
            }
        });
        tree.setCellRenderer(new ExampleTreeCellRenderer());
        ToolTipManager.sharedInstance().registerComponent(tree);
        return tree;
    }

    private void displaySynSetCandidate(){
        removeAll();
        setLayout(new GridLayout(1, synSets[itemIndex].length));
        for (int i = 0; i < synSets[itemIndex].length; i++){
            add(createSynSetTree(synSets[itemIndex][i], i));
        }
        validate();
    }

    public void save(){
        PrintWriter writer;
        try {
            writer = new PrintWriter(fileName, "UTF-8");
            for (int i = 0; i < synSets.length; i++) {
                int k = 0;
                for (int j = 0; j < synSets[i].length; j++){
                    if (!toBeRemoved[i][j]){
                        if (k == 0) {
                            writer.print(synSets[i][j].getId());
                        } else {
                            writer.print("->" + synSets[i][j].getId());
                        }
                        k++;
                    }
                }
                if (k > 0){
                    writer.println();
                }
            }
            writer.close();
        } catch (FileNotFoundException | UnsupportedEncodingException ignored) {
        }
    }

    public String nextSynSetCandidate(){
        if (itemIndex < synSets.length - 1){
            itemIndex++;
            displaySynSetCandidate();
        }
        return itemIndex + 1 + "/" + synSets.length;
    }

    public String previousSynSetCandidate(){
        if (itemIndex > 0){
            itemIndex--;
            displaySynSetCandidate();
        }
        return itemIndex + 1 + "/" + synSets.length;
    }

    public String randomSynSetCandidate(){
        itemIndex = random.nextInt(synSets.length);
        displaySynSetCandidate();
        return itemIndex + 1 + "/" + synSets.length;
    }

}
