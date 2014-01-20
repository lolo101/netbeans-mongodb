/*
 * The MIT License
 *
 * Copyright 2014 Yann D'Isanto.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.netbeans.modules.mongodb.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.plaf.UIResource;
import javax.swing.tree.TreeCellRenderer;
import org.bson.types.ObjectId;
import org.netbeans.modules.mongodb.util.JsonProperty;

/**
 *
 * @author Yann D'Isanto
 */
public final class JsonTreeCellRenderer extends JPanel implements TreeCellRenderer {

    private static final Map<Class<?>, Color> COLORS = new HashMap<>();

    static {
        final Color caramel = new Color(0xC68E17);
        final Color purple = new Color(0x4B088A);
        COLORS.put(String.class, purple);
        COLORS.put(Integer.class, caramel);
        COLORS.put(Double.class, caramel);
        COLORS.put(Boolean.class, Color.BLUE);
        COLORS.put(ObjectId.class, Color.GRAY);
    }

    private final JLabel keyLabel = new JLabel();

    private final JLabel valueLabel = new JLabel();

    /**
     * Color to use for the foreground for selected nodes.
     */
    protected Color textSelectionColor;

    /**
     * Color to use for the foreground for non-selected nodes.
     */
    protected Color textNonSelectionColor;

    /**
     * Color to use for the background when a node is selected.
     */
    protected Color backgroundSelectionColor;

    /**
     * Color to use for the background when the node isn't selected.
     */
    protected Color backgroundNonSelectionColor;

    /**
     * Set to true after the constructor has run.
     */
    private boolean inited;

    public JsonTreeCellRenderer() {
        super(new BorderLayout(3, 0));
        add(keyLabel, BorderLayout.WEST);
        add(valueLabel, BorderLayout.CENTER);
        setOpaque(true);
        keyLabel.setFont(
            keyLabel.getFont().deriveFont(Font.BOLD));
        inited = true;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        keyLabel.setForeground(selected ? getTextSelectionColor() : getTextNonSelectionColor());
        setBackground(selected ? getBackgroundSelectionColor() : getBackgroundNonSelectionColor());
        if(value instanceof DBObjectTreeNode) {
            keyLabel.setText("document root");
            valueLabel.setText("");
        } else if(value instanceof JsonPropertyNode) {
            computRendererForJsonPropertyNode((JsonPropertyNode) value, selected);
        }
        return this;
    }
    
    private void computRendererForJsonPropertyNode(JsonPropertyNode node, boolean selected) {
        final JsonProperty property = node.getUserObject();
        if(node.isLeaf()) {
            keyLabel.setText(buildJsonKey(property.getName()));
            final Object value = property.getValue();
            valueLabel.setText(value instanceof String ? buildJsonString(value): value.toString());
            final Color foreground = COLORS.get(value.getClass());
            if (selected) {
                valueLabel.setForeground(getTextSelectionColor());
            } else {
                valueLabel.setForeground(foreground != null ? foreground : getTextNonSelectionColor());
            }
            if(value instanceof ObjectId) {
                keyLabel.setForeground(selected ? getTextSelectionColor() : COLORS.get(ObjectId.class));
            }
        } else {
            keyLabel.setText(property.getName());
            valueLabel.setText("");
        }
    }

    private String buildJsonKey(Object value) {
        return new StringBuilder().append(value).append(":").toString();
    }
    
    private String buildJsonString(Object value) {
        return new StringBuilder().append('"').append(value).append('"').toString();
    }
    
    /**
     * {@inheritDoc}
     *
     * @since 1.7
     */
    @Override
    public void updateUI() {
        super.updateUI();
        // To avoid invoking new methods from the constructor, the
        // inited field is first checked. If inited is false, the constructor
        // has not run and there is no point in checking the value. As
        // all look and feels have a non-null value for these properties,
        // a null value means the developer has specifically set it to
        // null. As such, if the value is null, this does not reset the
        // value.
        if (!inited || (getTextSelectionColor() instanceof UIResource)) {
            setTextSelectionColor(
                UIManager.getColor("Tree.selectionForeground"));
        }
        if (!inited || (getTextNonSelectionColor() instanceof UIResource)) {
            setTextNonSelectionColor(
                UIManager.getColor("Tree.textForeground"));
        }
        if (!inited || (getBackgroundSelectionColor() instanceof UIResource)) {
            setBackgroundSelectionColor(
                    UIManager.getColor("Tree.selectionBackground"));
        }
        if (!inited ||
                (getBackgroundNonSelectionColor() instanceof UIResource)) {
            setBackgroundNonSelectionColor(
                    UIManager.getColor("Tree.textBackground"));
        }
    }

    /**
     * Sets the color the text is drawn with when the node is selected.
     */
    public void setTextSelectionColor(Color newColor) {
        textSelectionColor = newColor;
    }

    /**
     * Returns the color the text is drawn with when the node is selected.
     */
    public Color getTextSelectionColor() {
        return textSelectionColor;
    }

    /**
     * Sets the color the text is drawn with when the node isn't selected.
     */
    public void setTextNonSelectionColor(Color newColor) {
        textNonSelectionColor = newColor;
    }

    /**
     * Returns the color the text is drawn with when the node isn't selected.
     */
    public Color getTextNonSelectionColor() {
        return textNonSelectionColor;
    }

    /**
     * Sets the color to use for the background if node is selected.
     */
    public void setBackgroundSelectionColor(Color newColor) {
        backgroundSelectionColor = newColor;
    }

    /**
     * Returns the color to use for the background if node is selected.
     */
    public Color getBackgroundSelectionColor() {
        return backgroundSelectionColor;
    }

    /**
     * Sets the background color to be used for non selected nodes.
     */
    public void setBackgroundNonSelectionColor(Color newColor) {
        backgroundNonSelectionColor = newColor;
    }

    /**
     * Returns the background color to be used for non selected nodes.
     */
    public Color getBackgroundNonSelectionColor() {
        return backgroundNonSelectionColor;
    }

}
