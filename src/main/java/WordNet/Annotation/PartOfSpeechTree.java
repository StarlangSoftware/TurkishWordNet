package WordNet.Annotation;

import WordNet.SynSet;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.util.HashMap;

public class PartOfSpeechTree {
    final JTree tree;
    final HashMap<SynSet, DefaultMutableTreeNode> nodeList;
    final DefaultTreeModel treeModel;

    /**
     * Constructor for a part of speech tree object.
     * @param tree JTree for displaying the synset tree for that part of speech.
     * @param nodeList HashMap storing all tree nodes in the JTree indexed via synsets.
     * @param treeModel Tree model of the JTree.
     */
    PartOfSpeechTree(JTree tree, HashMap<SynSet, DefaultMutableTreeNode> nodeList, DefaultTreeModel treeModel) {
        this.tree = tree;
        this.nodeList = nodeList;
        this.treeModel = treeModel;
    }
}
