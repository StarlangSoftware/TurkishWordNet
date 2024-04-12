package WordNet.Annotation;

import WordNet.*;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.util.ArrayList;
import java.util.Random;

public class WordNetEditorPanel extends JPanel {

    protected int itemIndex;
    protected String fileName;
    protected Random random;

    /**
     * Constructor for main parent panel of the WordNet Editor
     * @param fileName Name of the file (can be synset split, merge file or intralingual file)
     */
    public WordNetEditorPanel(String fileName){
        this.fileName = fileName;
        itemIndex = 0;
        random = new Random();
    }

    /**
     * Constructs a tree node for a relation in WordNet. If the relation is a semantic relation, its name is relation
     * name with its type, otherwise only name of the relation.
     * @param relation Relation for which tree node will be created.
     * @return A DefaultMutableTreeNode
     */
    protected DefaultMutableTreeNode createRelationNode(Relation relation){
        DefaultMutableTreeNode relationNode;
        if (relation instanceof SemanticRelation){
            relationNode  = new DefaultMutableTreeNode(relation.getName() + "(" + ((SemanticRelation)relation).getTypeAsString() + ")");
        } else {
            relationNode = new DefaultMutableTreeNode(relation.getName());
        }
        return relationNode;
    }

    /**
     * Constructs a subtree for a literal in WordNet. The literal name and its sense will be the node name, its
     * children will be its relations with other synsets.
     * @param literal Literal for which tree node will be created.
     * @return A subtree rooted at the literal.
     */
    protected DefaultMutableTreeNode createLiteralNode(Literal literal){
        DefaultMutableTreeNode literalNode = new DefaultMutableTreeNode(literal.getName() + "(" + literal.getSense() + ")");
        for (int i = 0; i < literal.relationSize(); i++){
            DefaultMutableTreeNode relationNode = createRelationNode(literal.getRelation(i));
            literalNode.add(relationNode);
        }
        return literalNode;
    }

    /**
     * Constructs a subtree for a synset in WordNet. The synset info will be the node name, its children will be
     * subtrees generated with createLiteralNode.
     * @param synSet SynSet for ehich tree node will be created.
     * @return A subtree rooted at the synset.
     */
    protected DefaultMutableTreeNode createSynSetNode(SynSet synSet){
        DefaultMutableTreeNode synSetNode;
        synSetNode = new DefaultMutableTreeNode(synSet);
        for (int i = 0; i < synSet.getSynonym().literalSize(); i++){
            synSetNode.add(createLiteralNode(synSet.getSynonym().getLiteral(i)));
        }
        return synSetNode;
    }

    /**
     * Creates a synset JTree for a literal in wordnet. First a root node with the name of this literal is created.
     * Then for every synset that contains this literal, a subtree (as a child of the root node) will be created with
     * createSynSetNode.
     * @param literal The literal for which a synset tree will be created.
     * @param wordNet WordNet used to create the JTree.
     * @return JTree for a literal in wordnet.
     */
    protected JTree createSynSetTree(final Literal literal, final WordNet wordNet){
        TreeNode[] path;
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(literal.getName());
        final JTree tree;
        ArrayList<SynSet> synSets = wordNet.getSynSetsWithLiteral(literal.getName());
        for (SynSet synSet : synSets){
            root.add(createSynSetNode(synSet));
        }
        tree = new JTree(root);
        int i = 0;
        for (SynSet synSet : synSets){
            if (synSet.getId().equalsIgnoreCase(literal.getSynSetId())){
                path = new TreeNode[2];
                path[0] = root;
                path[1] = root.getChildAt(i);
                tree.setSelectionPath(new TreePath(path));
            }
            i++;
        }
        tree.addTreeSelectionListener(e -> {
            int childIndex;
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            if (node.getLevel() == 1){
                childIndex = node.getParent().getIndex(node);
                ArrayList<SynSet> synSets1 = wordNet.getSynSetsWithLiteral(literal.getName());
                literal.setSynSetId(synSets1.get(childIndex).getId());
            } else {
                literal.setSynSetId("");
            }
        });
        tree.setCellRenderer(new ExampleTreeCellRenderer());
        ToolTipManager.sharedInstance().registerComponent(tree);
        return tree;
    }

}
