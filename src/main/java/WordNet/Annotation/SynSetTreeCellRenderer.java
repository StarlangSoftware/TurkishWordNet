package WordNet.Annotation;

import WordNet.*;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;

public class SynSetTreeCellRenderer extends LiteralTreeCellRenderer {
    private final WordNet currentWordNet;
    private final WordNet foreignWordNet;

    /**
     * Constructor for cell renderer which sets the tooltip according to the two old wordnets, current wordnet and
     * a foreign language wordnet.
     * @param oldWordNet1 First wordnet
     * @param oldWordNet2 Second wordnet
     * @param currentWordNet Current wordnet we are working on.
     * @param foreignWordNet Foreign language wordnet for which interlingual relations are worked on
     */
    public SynSetTreeCellRenderer(WordNet oldWordNet1, WordNet oldWordNet2, WordNet currentWordNet, WordNet foreignWordNet){
        super(oldWordNet1, oldWordNet2);
        this.currentWordNet = currentWordNet;
        this.foreignWordNet = foreignWordNet;
    }

    /**
     * Constructs a tooltip for different type of objects in the tree node. If the object is
     * <ul>
     *     <li>Interlingual relation: The tooltip will show the representative and definition of the interlingual
     *     synset </li>
     *     <li>Semantic relation: The tooltip will show representative and definition of the related synset</li>
     *     <li>Synset: The tooltip will show the long definition of the synset</li>
     * </ul>
     * @param tree .
     * @param value Tree node.
     * @param sel .
     * @param exp .
     * @param leaf .
     * @param row .
     * @param hasFocus .
     * @return
     */
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean exp, boolean leaf, int row, boolean hasFocus) {
        SynSet current;
        Relation relation;
        Component cell = super.getTreeCellRendererComponent(tree, value, sel, exp, leaf, row, hasFocus);
        DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) value;
        if (currentNode.getUserObject() instanceof InterlingualRelation){
            relation = (InterlingualRelation)currentNode.getUserObject();
            current = foreignWordNet.getSynSetWithId(relation.getName());
            if (current != null){
                ((JComponent) cell).setToolTipText(current.representative() + ":" + current.getDefinition());
            }
        } else {
            if (currentNode.getUserObject() instanceof SemanticRelation){
                relation = (SemanticRelation) currentNode.getUserObject();
                current = currentWordNet.getSynSetWithId(relation.getName());
                if (current != null){
                    ((JComponent) cell).setToolTipText(current.representative() + ":" + current.getDefinition());
                }
            } else {
                if (currentNode.getUserObject() instanceof SynSet){
                    current = (SynSet) currentNode.getUserObject();
                    ((JComponent) cell).setToolTipText(current.getLongDefinition());
                }
            }
        }
        return this;
    }

}
