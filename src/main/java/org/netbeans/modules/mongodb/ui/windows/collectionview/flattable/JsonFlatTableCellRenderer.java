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
package org.netbeans.modules.mongodb.ui.windows.collectionview.flattable;

import org.netbeans.modules.mongodb.options.JsonTreeCellRendererOptions;
import java.awt.Component;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import org.bson.types.ObjectId;
import org.netbeans.modules.mongodb.options.LabelCategory;
import org.netbeans.modules.mongodb.options.LabelFontConf;

/**
 *
 * @author Yann D'Isanto
 */
public final class JsonFlatTableCellRenderer extends DefaultTableCellRenderer {

    private static final Map<Class<?>, LabelCategory> LABEL_CATEGORIES = new HashMap<>();

    static {
        LABEL_CATEGORIES.put(String.class, LabelCategory.STRING_VALUE);
        LABEL_CATEGORIES.put(Integer.class, LabelCategory.INT_VALUE);
        LABEL_CATEGORIES.put(Double.class, LabelCategory.DECIMAL_VALUE);
        LABEL_CATEGORIES.put(Boolean.class, LabelCategory.BOOLEAN_VALUE);
        LABEL_CATEGORIES.put(ObjectId.class, LabelCategory.DOCUMENT);

    }

    private final JsonTreeCellRendererOptions options = JsonTreeCellRendererOptions.INSTANCE;

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        setToolTipText(getText());
        if (value != null && isSelected == false) {
            final LabelCategory valueLabelCategory = LABEL_CATEGORIES.get(value.getClass());
            if (valueLabelCategory != null) {
                final LabelFontConf valueFontConf = options.getLabelFontConf(valueLabelCategory);
                setFont(valueFontConf.getFont());
                setForeground(valueFontConf.getForeground());
                setBackground(valueFontConf.getBackground());
            } else {
                setFont(table.getFont());
                setForeground(table.getForeground());
                setBackground(table.getBackground());
            }
        }
        return this;
    }
}
