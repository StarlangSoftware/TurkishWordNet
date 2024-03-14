package WordNet.Annotation;

import WordNet.*;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;

public class SynSetTreeCellRenderer extends LiteralTreeCellRenderer {
    private final WordNet currentWordNet;
    private final WordNet foreignWordNet;

    public SynSetTreeCellRenderer(WordNet oldWordNet1, WordNet oldWordNet2, WordNet currentWordNet, WordNet foreignWordNet){
        super(oldWordNet1, oldWordNet2);
        this.currentWordNet = currentWordNet;
        this.foreignWordNet = foreignWordNet;
    }

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
