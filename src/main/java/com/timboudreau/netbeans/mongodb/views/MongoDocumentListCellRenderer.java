/*
 * The MIT License
 *
 * Copyright 2013 Yann D'Isanto.
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
package com.timboudreau.netbeans.mongodb.views;

import com.mongodb.DBObject;
import com.timboudreau.netbeans.mongodb.util.Json;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListCellRenderer;
import javax.swing.ScrollPaneConstants;

/**
 *
 * @author Yann D'Isanto
 */
public class MongoDocumentListCellRenderer extends JPanel implements ListCellRenderer<DBObject> {

    private final JTextArea textArea = new JTextArea();
    private final JScrollPane scrollPane = new JScrollPane(textArea);
    
    private final StringBuilder toolTipBuilder = new StringBuilder();
    
    public MongoDocumentListCellRenderer() {
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        setLayout(new BorderLayout());
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        add(scrollPane, BorderLayout.CENTER);
        setPreferredSize(new Dimension(0, 70));
    }

    
    @Override
    public Component getListCellRendererComponent(JList<? extends DBObject> list, DBObject value, int index, boolean isSelected, boolean cellHasFocus) {
        if(isSelected) {
            textArea.setForeground(list.getSelectionForeground());
            textArea.setBackground(list.getSelectionBackground());
        } else {
            textArea.setForeground(list.getForeground());
            textArea.setBackground(list.getBackground());
        }
        textArea.setText(Json.prettify(value));
        toolTipBuilder.setLength(0);
        toolTipBuilder
                .append("<html><pre>")
                .append(textArea.getText())
                .append("</pre></html>");
        setToolTipText(toolTipBuilder.toString());
        return this;
    }
}
