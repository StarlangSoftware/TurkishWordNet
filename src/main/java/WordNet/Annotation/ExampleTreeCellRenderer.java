package WordNet.Annotation;

import WordNet.SynSet;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

public class ExampleTreeCellRenderer extends DefaultTreeCellRenderer {

    /**
     * Sets the tooltip for a synset. The tooltip shows the example sentence for that synset, if it exists. Otherwise,
     * it shows the literals in that synset.
     * @param tree .
     * @param value Tree node.
     * @param sel .
     * @param exp .
     * @param leaf .
     * @param row .
     * @param hasFocus .
     * @return New component with tooltip text.
     */
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean exp, boolean leaf, int row, boolean hasFocus) {
        Component cell = super.getTreeCellRendererComponent(tree, value, sel, exp, leaf, row, hasFocus);
        DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) value;
        if (currentNode.getUserObject() instanceof SynSet){
            SynSet synSet = (SynSet)currentNode.getUserObject();
            if (synSet.getExample() != null){
                ((JComponent) cell).setToolTipText(synSet.getExample());
            } else {
                ((JComponent) cell).setToolTipText(synSet.getSynonym().toString());
            }
        } else {
            ((JComponent) cell).setToolTipText("");
        }
        return this;
    }
}
