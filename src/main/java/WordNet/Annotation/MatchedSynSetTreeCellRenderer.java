package WordNet.Annotation;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

public class MatchedSynSetTreeCellRenderer extends DefaultTreeCellRenderer {
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean exp, boolean leaf, int row, boolean hasFocus) {
        Component cell = super.getTreeCellRendererComponent(tree, value, sel, exp, leaf, row, hasFocus);
        DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) value;
        if (currentNode.getUserObject() instanceof InterlingualMatchedSynSets){
            InterlingualMatchedSynSets matchedSynSets = (InterlingualMatchedSynSets)currentNode.getUserObject();
            if (matchedSynSets.getSecond() != null && matchedSynSets.getSecond().size() > 0){
                if (matchedSynSets.getFirst() != null){
                    String label = matchedSynSets.getFirst().representative();
                    for (int i = 0; i < matchedSynSets.getSecond().size(); i++){
                        if (i == 0){
                            label = label + "=" + matchedSynSets.getSecond().get(i).representative();
                        } else {
                            label = label + "?" + matchedSynSets.getSecond().get(i).representative();
                        }
                    }
                    ((JComponent) cell).setToolTipText(label);
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
