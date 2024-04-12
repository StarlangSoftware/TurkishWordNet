package WordNet.Annotation;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

public class MatchedSynSetTreeCellRenderer extends DefaultTreeCellRenderer {

    /**
     * Sets the tooltip for a tree node that contains a interlingual matched synset object. If there is no matching
     * interlingual synset, the color of the object will be black, otherwise green, and the tooltip will show the
     * representative of the original synset. If there are one or more matching interlingual synsets, the tooltip will
     * first show the representative of the original synset, then '=', then show the representatives of those synsets
     * separated via '?'. For example, for kırmızı, it will display  'kırmızı=red'.
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
        if (currentNode.getUserObject() instanceof InterlingualMatchedSynSets){
            InterlingualMatchedSynSets matchedSynSets = (InterlingualMatchedSynSets)currentNode.getUserObject();
            if (matchedSynSets.getSecond() != null && !matchedSynSets.getSecond().isEmpty()){
                if (matchedSynSets.getFirst() != null){
                    StringBuilder label = new StringBuilder(matchedSynSets.getFirst().representative());
                    for (int i = 0; i < matchedSynSets.getSecond().size(); i++){
                        if (i == 0){
                            label.append("=").append(matchedSynSets.getSecond().get(i).representative());
                        } else {
                            label.append("?").append(matchedSynSets.getSecond().get(i).representative());
                        }
                    }
                    ((JComponent) cell).setToolTipText(label.toString());
                } else {
                    ((JComponent) cell).setToolTipText(matchedSynSets.getSecond().get(0).representative());
                }
                setForeground(new Color(0, 128, 0));
            } else {
                ((JComponent) cell).setToolTipText(matchedSynSets.getFirst().representative());
                setForeground(Color.BLACK);
            }
        } else {
            ((JComponent) cell).setToolTipText("");
        }
        setBackgroundSelectionColor(Color.ORANGE);
        return this;
    }
}
