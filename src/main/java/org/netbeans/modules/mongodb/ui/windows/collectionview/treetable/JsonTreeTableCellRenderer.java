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
package org.netbeans.modules.mongodb.ui.windows.collectionview.treetable;

import org.netbeans.modules.mongodb.options.JsonTreeCellRendererOptions;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.UIResource;
import javax.swing.tree.TreeCellRenderer;
import org.bson.types.ObjectId;
import org.netbeans.modules.mongodb.options.LabelCategory;
import org.netbeans.modules.mongodb.options.LabelFontConf;
import org.netbeans.modules.mongodb.util.JsonProperty;

/**
 *
 * @author Yann D'Isanto
 */
public final class JsonTreeTableCellRenderer extends JPanel implements TreeCellRenderer {

    private static final Map<Class<?>, LabelCategory> LABEL_CATEGORIES = new HashMap<>();

    static {
        LABEL_CATEGORIES.put(String.class, LabelCategory.STRING_VALUE);
        LABEL_CATEGORIES.put(Integer.class, LabelCategory.INT_VALUE);
        LABEL_CATEGORIES.put(Double.class, LabelCategory.DECIMAL_VALUE);
        LABEL_CATEGORIES.put(Boolean.class, LabelCategory.BOOLEAN_VALUE);
        LABEL_CATEGORIES.put(ObjectId.class, LabelCategory.ID);

    }

    private final JsonTreeCellRendererOptions options = JsonTreeCellRendererOptions.INSTANCE;

    private final JLabel keyLabel = new JLabel();

    private final JLabel valueLabel = new JLabel();

    private final Border selectionBorder = BorderFactory.createLineBorder(Color.BLACK);

    private final Border nonSelectionBorder = BorderFactory.createEmptyBorder();

    /**
     * Color to use for the foreground for selected nodes.
     */
    private Color textSelectionColor;

    /**
     * Color to use for the background when a node is selected.
     */
    private Color backgroundSelectionColor;

    /**
     * Color to use for the background when the node isn't selected.
     */
    private Color backgroundNonSelectionColor;

    /**
     * Set to true after the constructor has run.
     */
    private boolean inited;

    public JsonTreeTableCellRenderer() {
        super(new GridBagLayout());
        add(keyLabel, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0,
            GridBagConstraints.WEST,
            GridBagConstraints.NONE,
            new Insets(0, 0, 0, 2), 0, 0));
        add(valueLabel, new GridBagConstraints(1, 0, 1, 1, 10.0, 1.0,
            GridBagConstraints.WEST,
            GridBagConstraints.HORIZONTAL,
            new Insets(0, 0, 0, 1), 2, 0));
        setOpaque(true);
        keyLabel.setOpaque(true);
        valueLabel.setOpaque(true);
        inited = true;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        setBackground(selected ? getBackgroundSelectionColor() : getBackgroundNonSelectionColor());
        setBorder(selected ? selectionBorder : nonSelectionBorder);
        if (value instanceof DBObjectNode) {
            final LabelFontConf keyFontConf = options.getLabelFontConf(LabelCategory.KEY);
            keyLabel.setFont(keyFontConf.getFont());
            if (value instanceof DocumentNode) {
                final DocumentNode node = (DocumentNode) value;
                final Object id = node.getUserObject().get("_id");
                keyLabel.setText(String.valueOf(id));
            } else {
                keyLabel.setText("-");
            }
            valueLabel.setText("");
            if (selected) {
                keyLabel.setForeground(getTextSelectionColor());
                keyLabel.setBackground(getBackgroundSelectionColor());
            } else {
                keyLabel.setForeground(keyFontConf.getForeground());
                keyLabel.setBackground(keyFontConf.getBackground());
            }
        } else if (value instanceof DBObjectNode) {
        } else if (value instanceof JsonPropertyNode) {
            computRendererForJsonPropertyNode((JsonPropertyNode) value, selected);
        } else if (value instanceof JsonValueNode) {
            computRendererForJsonValuePropertyNode((JsonValueNode) value, selected);
        }
        return this;
    }

    private void computRendererForJsonPropertyNode(JsonPropertyNode node, boolean selected) {
        final JsonProperty property = node.getUserObject();
        if (node.isLeaf() && (property.getValue() instanceof List) == false) {
            final Object value = property.getValue();
            final LabelCategory valueLabelCategory = LABEL_CATEGORIES.get(value.getClass());
            final LabelFontConf keyFontConf = options.getLabelFontConf((value instanceof ObjectId) ? LabelCategory.ID : LabelCategory.KEY);
            final LabelFontConf valueFontConf = options.getLabelFontConf(valueLabelCategory);

            keyLabel.setText(buildJsonKey(property.getName()));
            keyLabel.setFont(keyFontConf.getFont());
            valueLabel.setText(value instanceof String ? buildJsonString(value) : value.toString());
            valueLabel.setFont(valueFontConf.getFont());
            if (selected) {
                keyLabel.setForeground(getTextSelectionColor());
                keyLabel.setBackground(getBackgroundSelectionColor());
                valueLabel.setForeground(getTextSelectionColor());
                valueLabel.setBackground(getBackgroundSelectionColor());
            } else {
                keyLabel.setForeground(keyFontConf.getForeground());
                keyLabel.setBackground(keyFontConf.getBackground());
                valueLabel.setForeground(valueFontConf.getForeground());
                valueLabel.setBackground(valueFontConf.getBackground());
            }
        } else {
            final LabelFontConf keyFontConf = options.getLabelFontConf(LabelCategory.KEY);
            keyLabel.setFont(keyFontConf.getFont());
            if (selected) {
                keyLabel.setForeground(getTextSelectionColor());
                keyLabel.setBackground(getBackgroundSelectionColor());
            } else {
                keyLabel.setForeground(keyFontConf.getForeground());
                keyLabel.setBackground(keyFontConf.getBackground());
            }
            keyLabel.setText(property.getName());
            valueLabel.setText("");
        }
    }

    private void computRendererForJsonValuePropertyNode(JsonValueNode node, boolean selected) {
        final Object value = node.getUserObject();
        final LabelCategory valueLabelCategory = LABEL_CATEGORIES.get(value.getClass());
        final LabelFontConf keyFontConf = options.getLabelFontConf((value instanceof ObjectId) ? LabelCategory.ID : LabelCategory.KEY);
        final LabelFontConf valueFontConf = options.getLabelFontConf(valueLabelCategory);

        keyLabel.setText("");
        valueLabel.setText(value instanceof String ? buildJsonString(value) : value.toString());
        valueLabel.setFont(valueFontConf.getFont());
        if (selected) {
            keyLabel.setForeground(getTextSelectionColor());
            keyLabel.setBackground(getBackgroundSelectionColor());
            valueLabel.setForeground(getTextSelectionColor());
            valueLabel.setBackground(getBackgroundSelectionColor());
        } else {
            keyLabel.setForeground(keyFontConf.getForeground());
            keyLabel.setBackground(keyFontConf.getBackground());
            valueLabel.setForeground(valueFontConf.getForeground());
            valueLabel.setBackground(valueFontConf.getBackground());
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
        if (!inited || (getBackgroundSelectionColor() instanceof UIResource)) {
            setBackgroundSelectionColor(
                UIManager.getColor("Tree.selectionBackground"));
        }
        if (!inited
            || (getBackgroundNonSelectionColor() instanceof UIResource)) {
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
