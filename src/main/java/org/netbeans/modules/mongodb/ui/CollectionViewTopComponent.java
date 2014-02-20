/*
 * The MIT License
 *
 * Copyright 2013 Yann.
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

import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoException;
import com.mongodb.util.JSON;
import com.mongodb.util.JSONParseException;
import org.netbeans.modules.mongodb.CollectionInfo;
import org.netbeans.modules.mongodb.util.Json;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.Action;
import static javax.swing.Action.SHORT_DESCRIPTION;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.EditorKit;
import org.netbeans.api.editor.mimelookup.MimeLookup;
import org.netbeans.modules.mongodb.Images;
import org.netbeans.modules.mongodb.ui.wizards.ExportWizardAction;
import org.netbeans.modules.mongodb.util.SystemCollectionPredicate;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.TopComponent;

/**
 * Top component which displays something.
 */
@TopComponent.Description(
    preferredID = "CollectionViewTopComponent",
    persistenceType = TopComponent.PERSISTENCE_ALWAYS)
@Messages({
    "addDocumentTitle=Add new document",
    "editDocumentTitle=Edit document",
    "editCriteriaTitle=Enter criteria",
    "editProjectionTitle=Enter projection",
    "editSortTitle=Enter sort",
    "invalidJson=invalid json",
    "confirmDocumentDeletionText=Edit document",
    "ACTION_refreshDocuments=Refresh",
    "ACTION_refreshDocuments_tooltip=Refresh Documents",
    "ACTION_navFirst=First Page",
    "ACTION_navFirst_tooltip=First Page",
    "ACTION_navLeft=Previous Page",
    "ACTION_navLeft_tooltip=Previous Page",
    "ACTION_navRight=Next Page",
    "ACTION_navRight_tooltip=Next Page",
    "ACTION_navLast=Last Page",
    "ACTION_navLast_tooltip=Last Page"})
public final class CollectionViewTopComponent extends TopComponent {

    private static final Integer[] ITEMS_PER_PAGE_VALUES = {10, 20, 50, 100};

    private final CollectionInfo collectionInfo;

    private final boolean isSystemCollection;

    private final DocumentsTableModel tableModel;

    private final EditorKit jsonEditorKit = MimeLookup.getLookup("text/x-json").lookup(EditorKit.class);

    private final QueryEditor queryEditor = new QueryEditor();

    private final Action refreshDocumentsAction = new RefredhDocumentsAction();
    
    private final Action navFirstAction = new NavFirstAction();
    
    private final Action navLeftAction = new NavLeftAction();
    
    private final Action navRightAction = new NavRightAction();
    
    private final Action navLastAction = new NavLastAction();
    
        
    public CollectionViewTopComponent(CollectionInfo collectionInfo, Lookup lookup) {
        super(lookup);
        this.collectionInfo = collectionInfo;
        isSystemCollection = SystemCollectionPredicate.get().eval(collectionInfo.getName());
        initComponents();
        setName(collectionInfo.getName());
        setIcon(isSystemCollection
            ? Images.SYSTEM_COLLECTION_ICON
            : Images.COLLECTION_ICON);

        final DBCollection dbCollection = lookup.lookup(DBCollection.class);
        tableModel = new DocumentsTableModel(dbCollection);
        documentsTable.setModel(tableModel);
        documentsTable.setRowHeight(100);
        documentsTable.setDefaultEditor(DBObject.class, new MongoDocumentExpendableTableCell());
        documentsTable.setDefaultRenderer(DBObject.class, new MongoDocumentExpendableTableCell());
        documentsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        documentsTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent evt) {
                if (!evt.getValueIsAdjusting()) {
                    updateDocumentButtonsState();
                }
            }
        });
        reload();
    }

    public Action getRefreshDocumentsAction() {
        return refreshDocumentsAction;
    }

    public Action getNavFirstAction() {
        return navFirstAction;
    }

    public Action getNavLeftAction() {
        return navLeftAction;
    }

    public Action getNavRightAction() {
        return navRightAction;
    }

    public Action getNavLastAction() {
        return navLastAction;
    }
    
    private void reload() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                tableModel.setPage(1);
                tableModel.update();
                updatePagination();
                updateDocumentButtonsState();
            }
        }).start();
    }

    private void updatePagination() {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                int page = tableModel.getPage();
                int pageCount = tableModel.getPageCount();
                pageLabel.setText(String.valueOf(page));
                pageCountLabel.setText(String.valueOf(pageCount));
                boolean leftButtonsEnabled = page > 1;
                navFirstButton.setEnabled(leftButtonsEnabled);
                navLeftButton.setEnabled(leftButtonsEnabled);
                boolean rightButtonsEnabled = page < pageCount;
                navRightButton.setEnabled(rightButtonsEnabled);
                navLastButton.setEnabled(rightButtonsEnabled);
            }
        });
    }

    private void updateDocumentButtonsState() {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                boolean itemSelected = documentsTable.getSelectedRow() > -1;
                addButton.setEnabled(isSystemCollection == false);
                deleteButton.setEnabled(itemSelected && isSystemCollection == false);
                editButton.setEnabled(itemSelected && isSystemCollection == false);
            }
        });
    }

    private void updateQueryFieldsFromEditor() {
        final DBObject criteria = queryEditor.getCriteria();
        final DBObject projection = queryEditor.getProjection();
        final DBObject sort = queryEditor.getSort();
        criteriaField.setText(criteria != null ? JSON.serialize(criteria) : "");
        projectionField.setText(projection != null ? JSON.serialize(projection) : "");
        sortField.setText(sort != null ? JSON.serialize(sort) : "");
        tableModel.setCriteria(criteria);
        tableModel.setProjection(projection);
        tableModel.setSort(sort);
        reload();
    }

    private DBObject showJsonEditor(String title, String defaultJson) {
        final JEditorPane editor = new JEditorPane();
        if (jsonEditorKit != null) {
            editor.setEditorKit(jsonEditorKit);
        }
        editor.setPreferredSize(new Dimension(450, 300));
        String json = defaultJson.trim().isEmpty() ? "{}" : Json.prettify(defaultJson);
        boolean doLoop = true;
        while (doLoop) {
            doLoop = false;
            editor.setText(json);
            final DialogDescriptor desc = new DialogDescriptor(editor, title);
            final Object dlgResult = DialogDisplayer.getDefault().notify(desc);
            if (dlgResult.equals(NotifyDescriptor.OK_OPTION)) {
                try {
                    json = editor.getText().trim();
                    return (DBObject) JSON.parse(json);
                } catch (JSONParseException ex) {
                    DialogDisplayer.getDefault().notify(
                        new NotifyDescriptor.Message(Bundle.invalidJson(), NotifyDescriptor.ERROR_MESSAGE));
                    doLoop = true;
                }
            }
        }
        return null;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pageSizeLabel = new javax.swing.JLabel();
        pageSizeComboBox = new JComboBox<>(ITEMS_PER_PAGE_VALUES);
        pageCountLabel = new javax.swing.JLabel();
        pageLabel = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        addButton = new javax.swing.JButton();
        editButton = new javax.swing.JButton();
        deleteButton = new javax.swing.JButton();
        queryPanel = new javax.swing.JPanel();
        criteriaLabel = new javax.swing.JLabel();
        criteriaField = new javax.swing.JTextField();
        projectionLabel = new javax.swing.JLabel();
        projectionField = new javax.swing.JTextField();
        sortLabel = new javax.swing.JLabel();
        sortField = new javax.swing.JTextField();
        editQueryButton = new javax.swing.JButton();
        clearQueryButton = new javax.swing.JButton();
        exportButton = new javax.swing.JButton();
        tableScrollPane = new javax.swing.JScrollPane();
        documentsTable = new javax.swing.JTable();
        jToolBar1 = new javax.swing.JToolBar();
        refreshDocumentsButton = new javax.swing.JButton();
        navFirstButton = new javax.swing.JButton();
        navLeftButton = new javax.swing.JButton();
        navRightButton = new javax.swing.JButton();
        navLastButton = new javax.swing.JButton();

        org.openide.awt.Mnemonics.setLocalizedText(pageSizeLabel, org.openide.util.NbBundle.getMessage(CollectionViewTopComponent.class, "CollectionViewTopComponent.pageSizeLabel.text_1")); // NOI18N

        pageSizeComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pageSizeComboBoxActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(pageCountLabel, org.openide.util.NbBundle.getMessage(CollectionViewTopComponent.class, "CollectionViewTopComponent.pageCountLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(pageLabel, org.openide.util.NbBundle.getMessage(CollectionViewTopComponent.class, "CollectionViewTopComponent.pageLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(CollectionViewTopComponent.class, "CollectionViewTopComponent.jLabel3.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(addButton, org.openide.util.NbBundle.getMessage(CollectionViewTopComponent.class, "CollectionViewTopComponent.addButton.text")); // NOI18N
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addDocumentButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(editButton, org.openide.util.NbBundle.getMessage(CollectionViewTopComponent.class, "CollectionViewTopComponent.editButton.text")); // NOI18N
        editButton.setEnabled(false);
        editButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editDocumentButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(deleteButton, org.openide.util.NbBundle.getMessage(CollectionViewTopComponent.class, "CollectionViewTopComponent.deleteButton.text")); // NOI18N
        deleteButton.setEnabled(false);
        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteDocumentButtonActionPerformed(evt);
            }
        });

        queryPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(CollectionViewTopComponent.class, "CollectionViewTopComponent.queryPanel.border.title"))); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(criteriaLabel, org.openide.util.NbBundle.getMessage(CollectionViewTopComponent.class, "CollectionViewTopComponent.criteriaLabel.text")); // NOI18N

        criteriaField.setEditable(false);
        criteriaField.setText(org.openide.util.NbBundle.getMessage(CollectionViewTopComponent.class, "CollectionViewTopComponent.criteriaField.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(projectionLabel, org.openide.util.NbBundle.getMessage(CollectionViewTopComponent.class, "CollectionViewTopComponent.projectionLabel.text")); // NOI18N

        projectionField.setEditable(false);
        projectionField.setText(org.openide.util.NbBundle.getMessage(CollectionViewTopComponent.class, "CollectionViewTopComponent.projectionField.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(sortLabel, org.openide.util.NbBundle.getMessage(CollectionViewTopComponent.class, "CollectionViewTopComponent.sortLabel.text")); // NOI18N

        sortField.setEditable(false);
        sortField.setText(org.openide.util.NbBundle.getMessage(CollectionViewTopComponent.class, "CollectionViewTopComponent.sortField.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(editQueryButton, org.openide.util.NbBundle.getMessage(CollectionViewTopComponent.class, "CollectionViewTopComponent.editQueryButton.text")); // NOI18N
        editQueryButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editQueryButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(clearQueryButton, org.openide.util.NbBundle.getMessage(CollectionViewTopComponent.class, "CollectionViewTopComponent.clearQueryButton.text")); // NOI18N
        clearQueryButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearQueryButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout queryPanelLayout = new javax.swing.GroupLayout(queryPanel);
        queryPanel.setLayout(queryPanelLayout);
        queryPanelLayout.setHorizontalGroup(
            queryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(queryPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(queryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(queryPanelLayout.createSequentialGroup()
                        .addComponent(editQueryButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(clearQueryButton)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, queryPanelLayout.createSequentialGroup()
                        .addComponent(criteriaLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(criteriaField))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, queryPanelLayout.createSequentialGroup()
                        .addGroup(queryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(projectionLabel)
                            .addComponent(sortLabel))
                        .addGap(6, 6, 6)
                        .addGroup(queryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(projectionField)
                            .addComponent(sortField)))))
        );

        queryPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {criteriaLabel, projectionLabel, sortLabel});

        queryPanelLayout.setVerticalGroup(
            queryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(queryPanelLayout.createSequentialGroup()
                .addGroup(queryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(criteriaField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(criteriaLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(queryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(projectionField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(projectionLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(queryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(sortField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(sortLabel))
                .addGap(12, 12, 12)
                .addGroup(queryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(editQueryButton)
                    .addComponent(clearQueryButton)))
        );

        org.openide.awt.Mnemonics.setLocalizedText(exportButton, org.openide.util.NbBundle.getMessage(CollectionViewTopComponent.class, "CollectionViewTopComponent.exportButton.text")); // NOI18N
        exportButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportButtonActionPerformed(evt);
            }
        });

        documentsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tableScrollPane.setViewportView(documentsTable);

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        refreshDocumentsButton.setAction(getRefreshDocumentsAction());
        refreshDocumentsButton.setFocusable(false);
        refreshDocumentsButton.setHideActionText(true);
        refreshDocumentsButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        refreshDocumentsButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(refreshDocumentsButton);

        navFirstButton.setAction(getNavFirstAction());
        navFirstButton.setEnabled(false);
        navFirstButton.setHideActionText(true);
        jToolBar1.add(navFirstButton);

        navLeftButton.setAction(getNavLeftAction());
        navLeftButton.setEnabled(false);
        navLeftButton.setHideActionText(true);
        jToolBar1.add(navLeftButton);

        navRightButton.setAction(getNavRightAction());
        navRightButton.setEnabled(false);
        navRightButton.setFocusable(false);
        navRightButton.setHideActionText(true);
        navRightButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        navRightButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(navRightButton);

        navLastButton.setAction(getNavLastAction());
        navLastButton.setEnabled(false);
        navLastButton.setFocusable(false);
        navLastButton.setHideActionText(true);
        navLastButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        navLastButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(navLastButton);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(queryPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tableScrollPane)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(exportButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 1125, Short.MAX_VALUE)
                        .addComponent(pageLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pageCountLabel)
                        .addGap(106, 106, 106))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pageSizeLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pageSizeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(addButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(editButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(deleteButton)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(queryPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(pageSizeLabel)
                        .addComponent(pageSizeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(addButton)
                        .addComponent(deleteButton)
                        .addComponent(editButton))
                    .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tableScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(pageCountLabel)
                    .addComponent(pageLabel)
                    .addComponent(jLabel3)
                    .addComponent(exportButton))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void pageSizeComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pageSizeComboBoxActionPerformed
        tableModel.setItemsPerPage((Integer) pageSizeComboBox.getSelectedItem());
        reload();
    }//GEN-LAST:event_pageSizeComboBoxActionPerformed

    private void addDocumentButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addDocumentButtonActionPerformed
        final DBObject document = showJsonEditor(
            Bundle.addDocumentTitle(),
            "{}");
        if (document != null) {
            try {
                final DBCollection dbCollection = getLookup().lookup(DBCollection.class);
                dbCollection.insert(document);
                reload();
            } catch (MongoException ex) {
                DialogDisplayer.getDefault().notify(
                    new NotifyDescriptor.Message(ex.getLocalizedMessage(), NotifyDescriptor.ERROR_MESSAGE));
            }
        }
    }//GEN-LAST:event_addDocumentButtonActionPerformed

    private void deleteDocumentButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteDocumentButtonActionPerformed
        final DBObject document = tableModel.getRowValue(documentsTable.getSelectedRow());
        final Object dlgResult = DialogDisplayer.getDefault().notify(
            new NotifyDescriptor.Confirmation(Bundle.confirmDocumentDeletionText(), NotifyDescriptor.YES_NO_OPTION));
        if (dlgResult.equals(NotifyDescriptor.OK_OPTION)) {
            try {
                final DBCollection dbCollection = getLookup().lookup(DBCollection.class);
                dbCollection.remove(document);
                reload();
            } catch (MongoException ex) {
                DialogDisplayer.getDefault().notify(
                    new NotifyDescriptor.Message(ex.getLocalizedMessage(), NotifyDescriptor.ERROR_MESSAGE));
            }
        }
    }//GEN-LAST:event_deleteDocumentButtonActionPerformed

    private void editDocumentButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editDocumentButtonActionPerformed
        final DBObject document = tableModel.getRowValue(documentsTable.getSelectedRow());
        final DBObject modifiedDocument = showJsonEditor(
            Bundle.editDocumentTitle(),
            JSON.serialize(document));
        if (modifiedDocument != null) {
            try {
                final DBCollection dbCollection = getLookup().lookup(DBCollection.class);
                dbCollection.save(modifiedDocument);
                reload();
            } catch (MongoException ex) {
                DialogDisplayer.getDefault().notify(
                    new NotifyDescriptor.Message(ex.getLocalizedMessage(), NotifyDescriptor.ERROR_MESSAGE));
            }
        }
    }//GEN-LAST:event_editDocumentButtonActionPerformed

    private void editQueryButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editQueryButtonActionPerformed
        if (queryEditor.showDialog()) {
            updateQueryFieldsFromEditor();
        }
    }//GEN-LAST:event_editQueryButtonActionPerformed

    private void clearQueryButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearQueryButtonActionPerformed
        queryEditor.setCriteria(null);
        queryEditor.setProjection(null);
        queryEditor.setSort(null);
        updateQueryFieldsFromEditor();
    }//GEN-LAST:event_clearQueryButtonActionPerformed

    private void exportButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportButtonActionPerformed
        final Map<String, Object> properties = new HashMap<>();
        properties.put(ExportWizardAction.PROP_COLLECTION, collectionInfo.getName());
        properties.put("criteria", queryEditor.getCriteria());
        properties.put("projection", queryEditor.getProjection());
        properties.put("sort", queryEditor.getSort());
        new ExportWizardAction(getLookup(), properties).actionPerformed(evt);
    }//GEN-LAST:event_exportButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JButton clearQueryButton;
    private javax.swing.JTextField criteriaField;
    private javax.swing.JLabel criteriaLabel;
    private javax.swing.JButton deleteButton;
    private javax.swing.JTable documentsTable;
    private javax.swing.JButton editButton;
    private javax.swing.JButton editQueryButton;
    private javax.swing.JButton exportButton;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JButton navFirstButton;
    private javax.swing.JButton navLastButton;
    private javax.swing.JButton navLeftButton;
    private javax.swing.JButton navRightButton;
    private javax.swing.JLabel pageCountLabel;
    private javax.swing.JLabel pageLabel;
    private javax.swing.JComboBox<Integer> pageSizeComboBox;
    private javax.swing.JLabel pageSizeLabel;
    private javax.swing.JTextField projectionField;
    private javax.swing.JLabel projectionLabel;
    private javax.swing.JPanel queryPanel;
    private javax.swing.JButton refreshDocumentsButton;
    private javax.swing.JTextField sortField;
    private javax.swing.JLabel sortLabel;
    private javax.swing.JScrollPane tableScrollPane;
    // End of variables declaration//GEN-END:variables

    void writeProperties(java.util.Properties p) {
        // better to version settings since initial version as advocated at
        // http://wiki.apidesign.org/wiki/PropertyFiles
        p.setProperty("version", "1.0");
        // TODO store your settings
    }

    void readProperties(java.util.Properties p) {
        String version = p.getProperty("version");
        // TODO read your settings according to their version
    }

    private final class RefredhDocumentsAction extends AbstractAction {

        public RefredhDocumentsAction() {
            super(Bundle.ACTION_refreshDocuments(), new ImageIcon(Images.REFRESH_ICON));
            putValue(SHORT_DESCRIPTION, Bundle.ACTION_refreshDocuments_tooltip());
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            reload();
        }
    }

    private final class NavFirstAction extends AbstractAction {

        public NavFirstAction() {
            super(Bundle.ACTION_navFirst(), new ImageIcon(Images.NAV_FIRST_ICON));
            putValue(SHORT_DESCRIPTION, Bundle.ACTION_navFirst_tooltip());
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            reload();
        }
    }

    private final class NavLeftAction extends AbstractAction {

        public NavLeftAction() {
            super(Bundle.ACTION_navLeft(), new ImageIcon(Images.NAV_LEFT_ICON));
            putValue(SHORT_DESCRIPTION, Bundle.ACTION_navLeft_tooltip());
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            new Thread(new Runnable() {

                @Override
                public void run() {
                    int page = tableModel.getPage();
                    if (page > 1) {
                        tableModel.setPage(page - 1);
                        tableModel.update();
                        updatePagination();
                    }
                }
            }).start();
        }
    }

    private final class NavRightAction extends AbstractAction {

        public NavRightAction() {
            super(Bundle.ACTION_navRight(), new ImageIcon(Images.NAV_RIGHT_ICON));
            putValue(SHORT_DESCRIPTION, Bundle.ACTION_navRight_tooltip());
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            new Thread(new Runnable() {

                @Override
                public void run() {
                    int page = tableModel.getPage();
                    if (page < tableModel.getPageCount()) {
                        tableModel.setPage(page + 1);
                        tableModel.update();
                        updatePagination();
                    }
                }
            }).start();
        }
    }

    private final class NavLastAction extends AbstractAction {

        public NavLastAction() {
            super(Bundle.ACTION_navLast(), new ImageIcon(Images.NAV_LAST_ICON));
            putValue(SHORT_DESCRIPTION, Bundle.ACTION_navLast_tooltip());
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            new Thread(new Runnable() {

                @Override
                public void run() {
                    tableModel.setPage(tableModel.getPageCount());
                    tableModel.update();
                    updatePagination();
                }
            }).start();
        }
    }
}
