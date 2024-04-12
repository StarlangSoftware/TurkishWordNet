package WordNet.Annotation;

import WordNet.Literal;
import WordNet.SynSet;
import WordNet.WordNet;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

public class LiteralTreeCellRenderer extends DefaultTreeCellRenderer {
    private final WordNet wordNet1;
    private final WordNet wordNet2;

    /**
     * Constructor for cell renderer which sets the tooltip according to the two wordnets.
     * @param wordNet1 First wordnet
     * @param wordNet2 Second wordnet
     */
    public LiteralTreeCellRenderer(WordNet wordNet1, WordNet wordNet2){
        this.wordNet1 = wordNet1;
        this.wordNet2 = wordNet2;
    }

    /**
     * Sets the tooltip for a tree node that contains a literal in a JTree. If the literal exists in the first
     * wordnet, the tooltip will show the definition of the synset for that literal. Otherwise, the second wordnet is
     * checked for existence of the literal. If the literal exists in that wordnet, the tooltip will show the definition
     * of the synset in the second wordnet.
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
        SynSet current;
        Component cell = super.getTreeCellRendererComponent(tree, value, sel, exp, leaf, row, hasFocus);
        DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) value;
        if (currentNode.getUserObject() instanceof Literal){
            Literal literal = (Literal) currentNode.getUserObject();
            current = wordNet1.getSynSetWithLiteral(literal.getName(), literal.getSense());
            if (current != null){
                ((JComponent) cell).setToolTipText(current.getDefinition());
            } else {
                current = wordNet2.getSynSetWithLiteral(literal.getName(), literal.getSense());
                if (current != null){
                    ((JComponent) cell).setToolTipText(current.getDefinition());
                }
            }
        }
        return this;
    }

}
