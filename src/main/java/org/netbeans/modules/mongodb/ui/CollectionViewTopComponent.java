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
import java.util.prefs.Preferences;
import javax.swing.AbstractAction;
import javax.swing.Action;
import static javax.swing.Action.SHORT_DESCRIPTION;
import javax.swing.ImageIcon;
import javax.swing.JEditorPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.EditorKit;
import javax.swing.text.PlainDocument;
import org.netbeans.api.editor.mimelookup.MimeLookup;
import org.netbeans.modules.mongodb.Images;
import org.netbeans.modules.mongodb.ui.util.IntegerDocumentFilter;
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
    "# {0} - total documents count",
    "totalDocuments=Total Documents: {0}      ",
    "# {0} - current page",
    "# {1} - total page count",
    "pageCountLabel=Page {0} of {1}",
    "confirmDocumentDeletionText=Delete document?",
    "ACTION_addDocument=Add Document",
    "ACTION_addDocument_tooltip=Add Document",
    "ACTION_deleteSelectedDocument=Delete Selected Document",
    "ACTION_deleteSelectedDocument_tooltip=Delete Selected Document",
    "ACTION_editSelectedDocument=Edit Selected Document",
    "ACTION_editSelectedDocument_tooltip=Edit Selected Document",
    "ACTION_refreshDocuments=Refresh",
    "ACTION_refreshDocuments_tooltip=Refresh Documents",
    "ACTION_navFirst=First Page",
    "ACTION_navFirst_tooltip=First Page",
    "ACTION_navLeft=Previous Page",
    "ACTION_navLeft_tooltip=Previous Page",
    "ACTION_navRight=Next Page",
    "ACTION_navRight_tooltip=Next Page",
    "ACTION_navLast=Last Page",
    "ACTION_navLast_tooltip=Last Page",
    "ACTION_exportQueryResult=Export Query Result",
    "ACTION_exportQueryResult_tooltip=Export Query Result"})
public final class CollectionViewTopComponent extends TopComponent {

    private final CollectionInfo collectionInfo;

    private final boolean isSystemCollection;

    private final DocumentsTableModel tableModel;

    private final EditorKit jsonEditorKit = MimeLookup.getLookup("text/x-json").lookup(EditorKit.class);

    private final QueryEditor queryEditor = new QueryEditor();

    private final Action addDocumentAction = new AddDocumentAction();

    private final Action deleteSelectedDocumentAction = new DeleteSelectedDocumentAction();

    private final Action editSelectedDocumentAction = new EditSelectedDocumentAction();

    private final Action refreshDocumentsAction = new RefredhDocumentsAction();

    private final Action navFirstAction = new NavFirstAction();

    private final Action navLeftAction = new NavLeftAction();

    private final Action navRightAction = new NavRightAction();

    private final Action navLastAction = new NavLastAction();

    private final Action exportQueryResultAction = new ExportQueryResultAction();

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
        
        final int pageSize = prefs().getInt("table-page-size", DocumentsTableModel.DEFAULT_PAGE_SIZE);
        tableModel.setPageSize(pageSize);
        final PlainDocument document = (PlainDocument) pageSizeField.getDocument();
        document.setDocumentFilter(new IntegerDocumentFilter());
        pageSizeField.setText(String.valueOf(pageSize));
        reload();
    }

    private Preferences prefs() {
        return Preferences.userNodeForPackage(CollectionViewTopComponent.class);
    }
    
    public Action getAddDocumentAction() {
        return addDocumentAction;
    }

    public Action getDeleteSelectedDocumentAction() {
        return deleteSelectedDocumentAction;
    }

    public Action getEditSelectedDocumentAction() {
        return editSelectedDocumentAction;
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

    public Action getExportQueryResultAction() {
        return exportQueryResultAction;
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
                totalDocumentsLabel.setText(Bundle.totalDocuments(tableModel.getTotalDocumentsCount()));
                int page = tableModel.getPage();
                int pageCount = tableModel.getPageCount();
                pageCountLabel.setText(Bundle.pageCountLabel(page, pageCount));

                boolean leftNavEnabled = page > 1;
                navFirstAction.setEnabled(leftNavEnabled);
                navLeftAction.setEnabled(leftNavEnabled);
                boolean rightNavEnabled = page < pageCount;
                navRightButton.setEnabled(rightNavEnabled);
                navLastAction.setEnabled(rightNavEnabled);
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

        queryPanel = new javax.swing.JPanel();
        criteriaLabel = new javax.swing.JLabel();
        criteriaField = new javax.swing.JTextField();
        projectionLabel = new javax.swing.JLabel();
        projectionField = new javax.swing.JTextField();
        sortLabel = new javax.swing.JLabel();
        sortField = new javax.swing.JTextField();
        editQueryButton = new javax.swing.JButton();
        clearQueryButton = new javax.swing.JButton();
        documentsToolBar = new javax.swing.JToolBar();
        addButton = new javax.swing.JButton();
        deleteButton = new javax.swing.JButton();
        editButton = new javax.swing.JButton();
        exportButton = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        refreshDocumentsButton = new javax.swing.JButton();
        navFirstButton = new javax.swing.JButton();
        navLeftButton = new javax.swing.JButton();
        navRightButton = new javax.swing.JButton();
        navLastButton = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        pageSizeLabel = new javax.swing.JLabel();
        pageSizeField = new javax.swing.JTextField();
        jSeparator3 = new javax.swing.JToolBar.Separator();
        totalDocumentsLabel = new javax.swing.JLabel();
        pageCountLabel = new javax.swing.JLabel();
        tableScrollPane = new javax.swing.JScrollPane();
        documentsTable = new javax.swing.JTable();

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

        documentsToolBar.setFloatable(false);
        documentsToolBar.setRollover(true);

        addButton.setAction(getAddDocumentAction());
        addButton.setFocusable(false);
        addButton.setHideActionText(true);
        addButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        addButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        documentsToolBar.add(addButton);

        deleteButton.setAction(getDeleteSelectedDocumentAction());
        deleteButton.setEnabled(false);
        deleteButton.setFocusable(false);
        deleteButton.setHideActionText(true);
        deleteButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        deleteButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        documentsToolBar.add(deleteButton);

        editButton.setAction(getEditSelectedDocumentAction());
        editButton.setEnabled(false);
        editButton.setFocusable(false);
        editButton.setHideActionText(true);
        editButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        editButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        documentsToolBar.add(editButton);

        exportButton.setAction(getExportQueryResultAction());
        exportButton.setFocusable(false);
        exportButton.setHideActionText(true);
        exportButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        exportButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        documentsToolBar.add(exportButton);
        documentsToolBar.add(jSeparator1);

        refreshDocumentsButton.setAction(getRefreshDocumentsAction());
        refreshDocumentsButton.setFocusable(false);
        refreshDocumentsButton.setHideActionText(true);
        refreshDocumentsButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        refreshDocumentsButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        documentsToolBar.add(refreshDocumentsButton);

        navFirstButton.setAction(getNavFirstAction());
        navFirstButton.setHideActionText(true);
        documentsToolBar.add(navFirstButton);

        navLeftButton.setAction(getNavLeftAction());
        navLeftButton.setHideActionText(true);
        documentsToolBar.add(navLeftButton);

        navRightButton.setAction(getNavRightAction());
        navRightButton.setFocusable(false);
        navRightButton.setHideActionText(true);
        navRightButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        navRightButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        documentsToolBar.add(navRightButton);

        navLastButton.setAction(getNavLastAction());
        navLastButton.setFocusable(false);
        navLastButton.setHideActionText(true);
        navLastButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        navLastButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        documentsToolBar.add(navLastButton);
        documentsToolBar.add(jSeparator2);

        org.openide.awt.Mnemonics.setLocalizedText(pageSizeLabel, org.openide.util.NbBundle.getMessage(CollectionViewTopComponent.class, "CollectionViewTopComponent.pageSizeLabel.text_1")); // NOI18N
        documentsToolBar.add(pageSizeLabel);

        pageSizeField.setMaximumSize(new java.awt.Dimension(40, 2147483647));
        pageSizeField.setMinimumSize(new java.awt.Dimension(40, 20));
        pageSizeField.setPreferredSize(new java.awt.Dimension(40, 20));
        pageSizeField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pageSizeFieldActionPerformed(evt);
            }
        });
        documentsToolBar.add(pageSizeField);
        documentsToolBar.add(jSeparator3);
        documentsToolBar.add(totalDocumentsLabel);
        documentsToolBar.add(pageCountLabel);

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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(queryPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tableScrollPane)
                    .addComponent(documentsToolBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(queryPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(documentsToolBar, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tableScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 138, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

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

    private void pageSizeFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pageSizeFieldActionPerformed
        final int pageSize = Integer.parseInt(pageSizeField.getText());
        tableModel.setPageSize(pageSize);
        reload();
        prefs().putInt("table-page-size", pageSize);
    }//GEN-LAST:event_pageSizeFieldActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JButton clearQueryButton;
    private javax.swing.JTextField criteriaField;
    private javax.swing.JLabel criteriaLabel;
    private javax.swing.JButton deleteButton;
    private javax.swing.JTable documentsTable;
    private javax.swing.JToolBar documentsToolBar;
    private javax.swing.JButton editButton;
    private javax.swing.JButton editQueryButton;
    private javax.swing.JButton exportButton;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToolBar.Separator jSeparator3;
    private javax.swing.JButton navFirstButton;
    private javax.swing.JButton navLastButton;
    private javax.swing.JButton navLeftButton;
    private javax.swing.JButton navRightButton;
    private javax.swing.JLabel pageCountLabel;
    private javax.swing.JTextField pageSizeField;
    private javax.swing.JLabel pageSizeLabel;
    private javax.swing.JTextField projectionField;
    private javax.swing.JLabel projectionLabel;
    private javax.swing.JPanel queryPanel;
    private javax.swing.JButton refreshDocumentsButton;
    private javax.swing.JTextField sortField;
    private javax.swing.JLabel sortLabel;
    private javax.swing.JScrollPane tableScrollPane;
    private javax.swing.JLabel totalDocumentsLabel;
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

    private final class AddDocumentAction extends AbstractAction {

        public AddDocumentAction() {
            super(Bundle.ACTION_addDocument(), new ImageIcon(Images.ADD_DOCUMENT_ICON));
            putValue(SHORT_DESCRIPTION, Bundle.ACTION_addDocument_tooltip());
        }

        @Override
        public void actionPerformed(ActionEvent e) {
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
        }
    }

    private final class DeleteSelectedDocumentAction extends AbstractAction {

        public DeleteSelectedDocumentAction() {
            super(Bundle.ACTION_deleteSelectedDocument(), new ImageIcon(Images.DELETE_DOCUMENT_ICON));
            putValue(SHORT_DESCRIPTION, Bundle.ACTION_deleteSelectedDocument_tooltip());
        }

        @Override
        public void actionPerformed(ActionEvent e) {
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
        }
    }

    private final class EditSelectedDocumentAction extends AbstractAction {

        public EditSelectedDocumentAction() {
            super(Bundle.ACTION_editSelectedDocument(), new ImageIcon(Images.EDIT_DOCUMENT_ICON));
            putValue(SHORT_DESCRIPTION, Bundle.ACTION_editSelectedDocument_tooltip());
        }

        @Override
        public void actionPerformed(ActionEvent e) {
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
        }
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

    private final class ExportQueryResultAction extends AbstractAction {

        public ExportQueryResultAction() {
            super(Bundle.ACTION_exportQueryResult(), new ImageIcon(Images.EXPORT_COLLECTION_ICON));
            putValue(SHORT_DESCRIPTION, Bundle.ACTION_exportQueryResult_tooltip());
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            final Map<String, Object> properties = new HashMap<>();
            properties.put(ExportWizardAction.PROP_COLLECTION, collectionInfo.getName());
            properties.put("criteria", queryEditor.getCriteria());
            properties.put("projection", queryEditor.getProjection());
            properties.put("sort", queryEditor.getSort());
            new ExportWizardAction(getLookup(), properties).actionPerformed(e);
        }
    }
}
