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
package com.timboudreau.netbeans.mongodb.views;

import com.mongodb.DB;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import com.timboudreau.netbeans.mongodb.util.ExportProperties;
import com.timboudreau.netbeans.mongodb.util.Exporter;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileChooserBuilder;
import org.openide.util.Exceptions;

/**
 *
 * @author Yann D'Isanto
 */
public final class ExportPanel extends javax.swing.JPanel {

    private final QueryEditor queryEditor = new QueryEditor();

    private final DB db;

    /**
     * Creates new form ExportPanel
     */
    public ExportPanel(DB db) {
        this.db = db;
        initComponents();
        collectionComboBox.removeAllItems();
        for (String collection : db.getCollectionNames()) {
            collectionComboBox.addItem(collection);
        }
        if(collectionComboBox.getItemCount() == 0) {
            throw new IllegalArgumentException("no collection available");
        }
        collectionComboBox.setSelectedIndex(0);
    }

    private void updateQueryFieldsFromEditor() {
        final DBObject criteria = queryEditor.getCriteria();
        final DBObject projection = queryEditor.getProjection();
        final DBObject sort = queryEditor.getSort();
        criteriaField.setText(criteria != null ? JSON.serialize(criteria) : "");
        projectionField.setText(projection != null ? JSON.serialize(projection) : "");
        sortField.setText(sort != null ? JSON.serialize(sort) : "");
    }

    public static void showDialog(DB db, String collection, DBObject criteria, DBObject projection, DBObject sort) {
        final ExportPanel exportPanel = new ExportPanel(db);
        if (collection != null) {
            exportPanel.collectionComboBox.setSelectedItem(collection);
        }
        exportPanel.queryEditor.setCriteria(criteria);
        exportPanel.queryEditor.setProjection(projection);
        exportPanel.queryEditor.setSort(sort);
        exportPanel.updateQueryFieldsFromEditor();

        final DialogDescriptor desc = new DialogDescriptor(exportPanel, "Export");
        final Object dlgResult = DialogDisplayer.getDefault().notify(desc);
        if (dlgResult.equals(NotifyDescriptor.OK_OPTION)) {
            export(exportPanel);
        }

    }

    private static void export(ExportPanel exportPanel) {
        final String collection = (String) exportPanel.collectionComboBox.getSelectedItem();
        final ExportProperties properties = new ExportProperties(collection)
            .criteria(exportPanel.queryEditor.getCriteria())
            .projection(exportPanel.queryEditor.getProjection())
            .sort(exportPanel.queryEditor.getSort())
            .jsonArray(exportPanel.jsonArrayCheckBox.isSelected());
        final File home = new File(System.getProperty("user.home"));
        final File file = new FileChooserBuilder("export-collection-documents")
            .setTitle("Export documents")
            .setDefaultWorkingDirectory(home)
            .setApproveText("Export")
            .showSaveDialog();
        if (file != null) {
            // TODO: confirm if file already exists
            try (Writer writer = new PrintWriter(file, "UTF-8")) {
                new Exporter(exportPanel.db, properties, writer).run();
            } catch (UnsupportedEncodingException ex) {
                throw new AssertionError();
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        collectionLabel = new javax.swing.JLabel();
        collectionComboBox = new javax.swing.JComboBox<String>();
        jsonArrayCheckBox = new javax.swing.JCheckBox();
        queryPanel = new javax.swing.JPanel();
        editQueryButton = new javax.swing.JButton();
        clearQueryButton = new javax.swing.JButton();
        sortField = new javax.swing.JTextField();
        sortLabel = new javax.swing.JLabel();
        projectionLabel = new javax.swing.JLabel();
        criteriaLabel = new javax.swing.JLabel();
        criteriaField = new javax.swing.JTextField();
        projectionField = new javax.swing.JTextField();

        org.openide.awt.Mnemonics.setLocalizedText(collectionLabel, org.openide.util.NbBundle.getMessage(ExportPanel.class, "ExportPanel.collectionLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jsonArrayCheckBox, org.openide.util.NbBundle.getMessage(ExportPanel.class, "ExportPanel.jsonArrayCheckBox.text")); // NOI18N
        jsonArrayCheckBox.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        queryPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(ExportPanel.class, "ExportPanel.queryPanel.border.title"))); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(editQueryButton, org.openide.util.NbBundle.getMessage(ExportPanel.class, "ExportPanel.editQueryButton.text")); // NOI18N
        editQueryButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editQueryButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(clearQueryButton, org.openide.util.NbBundle.getMessage(ExportPanel.class, "ExportPanel.clearQueryButton.text")); // NOI18N
        clearQueryButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearQueryButtonActionPerformed(evt);
            }
        });

        sortField.setEditable(false);

        org.openide.awt.Mnemonics.setLocalizedText(sortLabel, org.openide.util.NbBundle.getMessage(ExportPanel.class, "ExportPanel.sortLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(projectionLabel, org.openide.util.NbBundle.getMessage(ExportPanel.class, "ExportPanel.projectionLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(criteriaLabel, org.openide.util.NbBundle.getMessage(ExportPanel.class, "ExportPanel.criteriaLabel.text")); // NOI18N

        criteriaField.setEditable(false);

        projectionField.setEditable(false);

        javax.swing.GroupLayout queryPanelLayout = new javax.swing.GroupLayout(queryPanel);
        queryPanel.setLayout(queryPanelLayout);
        queryPanelLayout.setHorizontalGroup(
            queryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(queryPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(queryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(queryPanelLayout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(editQueryButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(clearQueryButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 355, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(queryPanelLayout.createSequentialGroup()
                        .addComponent(criteriaLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(criteriaField))
                    .addGroup(queryPanelLayout.createSequentialGroup()
                        .addComponent(projectionLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(projectionField))
                    .addGroup(queryPanelLayout.createSequentialGroup()
                        .addComponent(sortLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(sortField)))
                .addContainerGap())
        );

        queryPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {criteriaLabel, projectionLabel, sortLabel});

        queryPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {clearQueryButton, editQueryButton});

        queryPanelLayout.setVerticalGroup(
            queryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(queryPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(queryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(criteriaLabel)
                    .addComponent(criteriaField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(queryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(projectionLabel)
                    .addComponent(projectionField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(queryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(sortLabel)
                    .addComponent(sortField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(queryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(editQueryButton)
                    .addComponent(clearQueryButton)))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jsonArrayCheckBox)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(collectionLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(collectionComboBox, 0, 414, Short.MAX_VALUE))
                            .addComponent(queryPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(collectionLabel)
                    .addComponent(collectionComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(queryPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jsonArrayCheckBox)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton clearQueryButton;
    private javax.swing.JComboBox<String> collectionComboBox;
    private javax.swing.JLabel collectionLabel;
    private javax.swing.JTextField criteriaField;
    private javax.swing.JLabel criteriaLabel;
    private javax.swing.JButton editQueryButton;
    private javax.swing.JCheckBox jsonArrayCheckBox;
    private javax.swing.JTextField projectionField;
    private javax.swing.JLabel projectionLabel;
    private javax.swing.JPanel queryPanel;
    private javax.swing.JTextField sortField;
    private javax.swing.JLabel sortLabel;
    // End of variables declaration//GEN-END:variables
}
