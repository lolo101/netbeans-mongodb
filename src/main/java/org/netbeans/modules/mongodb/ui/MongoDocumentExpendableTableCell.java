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

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;
import org.netbeans.modules.mongodb.util.JsonProperty;
import org.openide.util.NbBundle.Messages;

/**
 * Documents expendable table cell renderer and editor.
 *
 * @author Yann D'Isanto
 */
@Messages({
    "ACTION_copyDocumentToClipboard=Copy document",
    "ACTION_copyKeyToClipboard=Copy key",
    "ACTION_copyValueToClipboard=Copy value"})
public final class MongoDocumentExpendableTableCell extends AbstractCellEditor implements TableCellEditor, TableCellRenderer {

    private final JTree tree = new JTree();

    private final JPanel panel = new JPanel(new BorderLayout());

    private boolean popup = false;

    public MongoDocumentExpendableTableCell() {
        tree.setRootVisible(false);
        tree.setShowsRootHandles(true);
        final TreeCellRenderer treeRenderer = new JsonTreeCellRenderer();
        tree.setCellRenderer(treeRenderer);
        panel.add(new JScrollPane(tree), BorderLayout.CENTER);
        panel.setPreferredSize(new Dimension(0, 100));
        panel.setBorder(BorderFactory.createLineBorder(Color.BLUE));
        tree.addFocusListener(new FocusAdapter() {

            @Override
            public void focusLost(FocusEvent e) {
                if (popup == false) {
                    stopCellEditing();
                }
            }

        });
        tree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    final TreePath path = tree.getClosestPathForLocation(e.getX(), e.getY());
                    if (path != null) {
                        tree.setSelectionPath(path);
                    }
                    final JsonPropertyNode node = path != null
                        ? (JsonPropertyNode) path.getLastPathComponent()
                        : null;
                    final JPopupMenu menu = createContextMenu(node);
                    menu.addPopupMenuListener(new PopupMenuListener() {

                        @Override
                        public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                        }

                        @Override
                        public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                            popup = false;
                        }

                        @Override
                        public void popupMenuCanceled(PopupMenuEvent e) {
                        }
                    });
                    popup = true;
                    menu.show(e.getComponent(), e.getX(), e.getY());
                }
            }

        });
    }

    @Override
    public Object getCellEditorValue() {
        return ((ImmutableTreeNode) tree.getModel().getRoot()).getUserObject();
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        tree.setModel(new DefaultTreeModel(new DBObjectTreeNode((DBObject) value)));
        return panel;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        tree.setModel(new DefaultTreeModel(new DBObjectTreeNode((DBObject) value)));
        panel.setBorder(hasFocus
            ? BorderFactory.createLineBorder(Color.BLUE)
            : BorderFactory.createEmptyBorder());
        if(hasFocus) {
            table.editCellAt(row, column);
        }
        return panel;
    }

    public JPopupMenu createContextMenu(JsonPropertyNode node) {
        final JPopupMenu menu = new JPopupMenu();
        menu.add(new JMenuItem(new CopyDocumentToClipboardAction()));
        if (node != null) {
            menu.addSeparator();
            menu.add(new JMenuItem(new CopyKeyToClipboardAction(node.getUserObject())));
            menu.add(new JMenuItem(new CopyValueToClipboardAction(node.getUserObject())));
        }
        return menu;
    }

    private final class CopyDocumentToClipboardAction extends AbstractAction {

        public CopyDocumentToClipboardAction() {
            super(Bundle.ACTION_copyDocumentToClipboard());
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            final DBObjectTreeNode rootNode = (DBObjectTreeNode) tree.getModel().getRoot();
            final StringSelection stringSelection = new StringSelection(
                JSON.serialize(rootNode.getUserObject()));
            final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(stringSelection, null);
        }

    }

    private static final class CopyKeyToClipboardAction extends AbstractAction {

        private final JsonProperty property;

        public CopyKeyToClipboardAction(JsonProperty property) {
            super(Bundle.ACTION_copyKeyToClipboard());
            this.property = property;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            final StringSelection stringSelection = new StringSelection(property.getName());
            final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(stringSelection, null);
        }

    }

    private static final class CopyValueToClipboardAction extends AbstractAction {

        private final JsonProperty property;

        public CopyValueToClipboardAction(JsonProperty property) {
            super(Bundle.ACTION_copyValueToClipboard());
            this.property = property;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            final StringSelection stringSelection = new StringSelection(getValueAsString());
            final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(stringSelection, null);
        }

        private String getValueAsString() {
            if (property.getValue() instanceof Map) {
                return JSON.serialize(new BasicDBObject((Map) property.getValue()));
            }
            return property.getValue().toString();
        }
    }
}
