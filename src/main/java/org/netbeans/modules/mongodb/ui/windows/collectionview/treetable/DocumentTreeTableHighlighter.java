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

import com.mongodb.DBObject;
import java.awt.Color;
import java.awt.Component;
import org.jdesktop.swingx.decorator.AbstractHighlighter;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.netbeans.modules.mongodb.options.JsonTreeCellRendererOptions;
import org.netbeans.modules.mongodb.options.LabelCategory;

/**
 * Highlighter for document node row.
 * 
 * @author Yann D'Isanto
 */
public final class DocumentTreeTableHighlighter extends AbstractHighlighter {

    public DocumentTreeTableHighlighter() {
        super(new HighlightPredicate() {

            @Override
            public boolean isHighlighted(Component renderer, ComponentAdapter adapter) {
                final Object value = adapter.getValue();
                if (value instanceof DBObject) {
                    final DBObject dbObject = (DBObject) value;
                    return dbObject.get("_id") != null;
                }
                return false;
            }
        });
    }

    @Override
    protected Component doHighlight(Component component, ComponentAdapter adapter) {
        final Color background = JsonTreeCellRendererOptions.INSTANCE
            .getLabelFontConf(LabelCategory.DOCUMENT)
            .getBackground();
        component.setBackground(background);
        return component;
    }

}
