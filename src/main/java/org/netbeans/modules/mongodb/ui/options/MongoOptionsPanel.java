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
package org.netbeans.modules.mongodb.ui.options;

import org.netbeans.modules.mongodb.options.JsonCellRenderingOptions;
import java.awt.Font;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.io.File;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.netbeans.modules.mongodb.options.MongoNativeToolsOptions;
import org.netbeans.modules.mongodb.options.LabelCategory;
import org.netbeans.modules.mongodb.options.LabelFontConf;
import org.netbeans.modules.mongodb.options.MongoNativeToolsFolderPredicate;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.awt.ColorComboBox;
import org.openide.filesystems.FileChooserBuilder;

/**
 * @author Yann D'Isanto
 */
final class MongoOptionsPanel extends javax.swing.JPanel {

    private final JsonCellRenderingOptions jsonRenderingOptions = JsonCellRenderingOptions.INSTANCE;

    private final MongoNativeToolsOptions mongoToolsOptions = MongoNativeToolsOptions.INSTANCE;

    private final MongoOptionsPanelController controller;

    private final PropertyEditor fontEditor = PropertyEditorManager.findEditor(Font.class);

    private final Map<LabelCategory, LabelFontConf.Builder> labelConfBuilders = new HashMap<>();

    private boolean internalUpdate = false;

    MongoOptionsPanel(MongoOptionsPanelController controller) {
        this.controller = controller;
        initComponents();
        internalUpdate = true;
        categoriesList.setListData(LabelCategory.values());
        categoriesList.setSelectedIndex(0);

        for (LabelCategory labelCategory : LabelCategory.values()) {
            final LabelFontConf conf = jsonRenderingOptions.getLabelFontConf(labelCategory);
            labelConfBuilders.put(labelCategory, new LabelFontConf.Builder(conf));
        }

        updateSelectedLabelFontConfUI();
        internalUpdate = false;
        
        categoriesList.addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                updateSelectedLabelFontConfUI();
            }
        });
        mongoToolsFolderPathField.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                fireChangeEvent();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                fireChangeEvent();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                fireChangeEvent();
            }
        });
    }

    private void updateSelectedLabelFontConfUI() {
        loadLabelFontConfInUI(getSelectedLabelFontConfBuilder());
    }
    
    private LabelFontConf.Builder getSelectedLabelFontConfBuilder() {
        final LabelCategory category = categoriesList.getSelectedValue();
        return labelConfBuilders.get(category);
    }

    private void loadLabelFontConfInUI(LabelFontConf.Builder labelFontConfBuilder) {
        final Font font = labelFontConfBuilder.getFont();
        fontEditor.setValue(font);
        fontField.setFont(font);
        fontField.setText(fontEditor.getAsText());
        ((ColorComboBox) foregroundComboBox).setSelectedColor(labelFontConfBuilder.getForeground());
        ((ColorComboBox) backgroundComboBox).setSelectedColor(labelFontConfBuilder.getBackground());
    }

    private void fireChangeEvent() {
        if (internalUpdate == false) {
            controller.changed();
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jsonTreeRenderingOptionsPanel = new javax.swing.JPanel();
        renderingOptionsPanel = new javax.swing.JPanel();
        fontLabel = new javax.swing.JLabel();
        backgroundComboBox = new ColorComboBox();
        foregroundLabel = new javax.swing.JLabel();
        foregroundComboBox = new ColorComboBox();
        backgroundLabel = new javax.swing.JLabel();
        fontField = new javax.swing.JTextField();
        browseFontButton = new javax.swing.JButton();
        categoriesScrollPane = new javax.swing.JScrollPane();
        categoriesList = new javax.swing.JList<LabelCategory>();
        categoriesLabel = new javax.swing.JLabel();
        restoreDefaultRenderingLabel = new javax.swing.JLabel();
        restoreDefaultRenderingButton = new javax.swing.JButton();
        shellOptionsPanel = new javax.swing.JPanel();
        mongoToolsFolderPathLabel = new javax.swing.JLabel();
        mongoToolsFolderPathField = new javax.swing.JTextField();
        browseMongoToolsFolderPathButton = new javax.swing.JButton();

        jsonTreeRenderingOptionsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(MongoOptionsPanel.class, "MongoOptionsPanel.jsonTreeRenderingOptionsPanel.border.title"))); // NOI18N

        renderingOptionsPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        org.openide.awt.Mnemonics.setLocalizedText(fontLabel, org.openide.util.NbBundle.getMessage(MongoOptionsPanel.class, "MongoOptionsPanel.fontLabel.text")); // NOI18N

        backgroundComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backgroundComboBoxActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(foregroundLabel, org.openide.util.NbBundle.getMessage(MongoOptionsPanel.class, "MongoOptionsPanel.foregroundLabel.text")); // NOI18N

        foregroundComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                foregroundComboBoxActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(backgroundLabel, org.openide.util.NbBundle.getMessage(MongoOptionsPanel.class, "MongoOptionsPanel.backgroundLabel.text")); // NOI18N

        fontField.setEditable(false);
        fontField.setText(org.openide.util.NbBundle.getMessage(MongoOptionsPanel.class, "MongoOptionsPanel.fontField.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(browseFontButton, org.openide.util.NbBundle.getMessage(MongoOptionsPanel.class, "MongoOptionsPanel.browseFontButton.text")); // NOI18N
        browseFontButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseFontButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout renderingOptionsPanelLayout = new javax.swing.GroupLayout(renderingOptionsPanel);
        renderingOptionsPanel.setLayout(renderingOptionsPanelLayout);
        renderingOptionsPanelLayout.setHorizontalGroup(
            renderingOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(renderingOptionsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(renderingOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(renderingOptionsPanelLayout.createSequentialGroup()
                        .addComponent(fontLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(fontField)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(browseFontButton))
                    .addGroup(renderingOptionsPanelLayout.createSequentialGroup()
                        .addComponent(backgroundLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(backgroundComboBox, 0, 214, Short.MAX_VALUE))
                    .addGroup(renderingOptionsPanelLayout.createSequentialGroup()
                        .addComponent(foregroundLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(foregroundComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );

        renderingOptionsPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {backgroundLabel, foregroundLabel});

        renderingOptionsPanelLayout.setVerticalGroup(
            renderingOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(renderingOptionsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(renderingOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(fontLabel)
                    .addComponent(fontField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(browseFontButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(renderingOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(foregroundLabel)
                    .addComponent(foregroundComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(renderingOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(backgroundLabel)
                    .addComponent(backgroundComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        categoriesScrollPane.setViewportView(categoriesList);

        org.openide.awt.Mnemonics.setLocalizedText(categoriesLabel, org.openide.util.NbBundle.getMessage(MongoOptionsPanel.class, "MongoOptionsPanel.categoriesLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(restoreDefaultRenderingLabel, org.openide.util.NbBundle.getMessage(MongoOptionsPanel.class, "MongoOptionsPanel.restoreDefaultRenderingLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(restoreDefaultRenderingButton, org.openide.util.NbBundle.getMessage(MongoOptionsPanel.class, "MongoOptionsPanel.restoreDefaultRenderingButton.text")); // NOI18N
        restoreDefaultRenderingButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                restoreDefaultRenderingButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jsonTreeRenderingOptionsPanelLayout = new javax.swing.GroupLayout(jsonTreeRenderingOptionsPanel);
        jsonTreeRenderingOptionsPanel.setLayout(jsonTreeRenderingOptionsPanelLayout);
        jsonTreeRenderingOptionsPanelLayout.setHorizontalGroup(
            jsonTreeRenderingOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jsonTreeRenderingOptionsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jsonTreeRenderingOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jsonTreeRenderingOptionsPanelLayout.createSequentialGroup()
                        .addComponent(categoriesScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(renderingOptionsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jsonTreeRenderingOptionsPanelLayout.createSequentialGroup()
                        .addComponent(categoriesLabel)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jsonTreeRenderingOptionsPanelLayout.createSequentialGroup()
                        .addComponent(restoreDefaultRenderingLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(restoreDefaultRenderingButton)))
                .addContainerGap())
        );
        jsonTreeRenderingOptionsPanelLayout.setVerticalGroup(
            jsonTreeRenderingOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jsonTreeRenderingOptionsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jsonTreeRenderingOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(restoreDefaultRenderingLabel)
                    .addComponent(restoreDefaultRenderingButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(categoriesLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jsonTreeRenderingOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(renderingOptionsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(categoriesScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );

        shellOptionsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(MongoOptionsPanel.class, "MongoOptionsPanel.shellOptionsPanel.border.title"))); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(mongoToolsFolderPathLabel, org.openide.util.NbBundle.getMessage(MongoOptionsPanel.class, "MongoOptionsPanel.mongoToolsFolderPathLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(browseMongoToolsFolderPathButton, org.openide.util.NbBundle.getMessage(MongoOptionsPanel.class, "MongoOptionsPanel.browseMongoToolsFolderPathButton.text")); // NOI18N
        browseMongoToolsFolderPathButton.setActionCommand(org.openide.util.NbBundle.getMessage(MongoOptionsPanel.class, "MongoOptionsPanel.browseMongoToolsFolderPathButton.actionCommand")); // NOI18N
        browseMongoToolsFolderPathButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseMongoToolsFolderPathButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout shellOptionsPanelLayout = new javax.swing.GroupLayout(shellOptionsPanel);
        shellOptionsPanel.setLayout(shellOptionsPanelLayout);
        shellOptionsPanelLayout.setHorizontalGroup(
            shellOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(shellOptionsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(shellOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(shellOptionsPanelLayout.createSequentialGroup()
                        .addComponent(mongoToolsFolderPathLabel)
                        .addGap(0, 241, Short.MAX_VALUE))
                    .addGroup(shellOptionsPanelLayout.createSequentialGroup()
                        .addComponent(mongoToolsFolderPathField)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(browseMongoToolsFolderPathButton)))
                .addContainerGap())
        );
        shellOptionsPanelLayout.setVerticalGroup(
            shellOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(shellOptionsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(mongoToolsFolderPathLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(shellOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(mongoToolsFolderPathField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(browseMongoToolsFolderPathButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jsonTreeRenderingOptionsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(shellOptionsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jsonTreeRenderingOptionsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(shellOptionsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void browseFontButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseFontButtonActionPerformed
        fontEditor.setValue(fontField.getFont());
        final DialogDescriptor dd = new DialogDescriptor(
            fontEditor.getCustomEditor(),
            "Select Font" // NOI18N
        );

        DialogDisplayer.getDefault().createDialog(dd).setVisible(true);
        if (dd.getValue() == DialogDescriptor.OK_OPTION) {
            final Font font = (Font) fontEditor.getValue();
            fontField.setFont(font);
            fontField.setText(fontEditor.getAsText());
            fireChangeEvent();
            getSelectedLabelFontConfBuilder().font(font);
        }

    }//GEN-LAST:event_browseFontButtonActionPerformed

    private void foregroundComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_foregroundComboBoxActionPerformed
        getSelectedLabelFontConfBuilder().foreground(((ColorComboBox) foregroundComboBox).getSelectedColor());
        fireChangeEvent();
    }//GEN-LAST:event_foregroundComboBoxActionPerformed

    private void backgroundComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backgroundComboBoxActionPerformed
        getSelectedLabelFontConfBuilder().background(((ColorComboBox) backgroundComboBox).getSelectedColor());
        fireChangeEvent();
    }//GEN-LAST:event_backgroundComboBoxActionPerformed

    private void browseMongoToolsFolderPathButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseMongoToolsFolderPathButtonActionPerformed
        final String mongoHomePath = System.getenv("MONGO_HOME");
        final FileChooserBuilder fcb = new FileChooserBuilder(MongoNativeToolsOptions.class);
        fcb.setDirectoriesOnly(true);
        fcb.setSelectionApprover(new MongoToolsFolderSelectionApprover());
        final String mongoToolsFolderPath = mongoToolsFolderPathField.getText().trim();
        if(mongoToolsFolderPath.isEmpty() == false) {
            fcb.setDefaultWorkingDirectory(new File(mongoToolsFolderPath));
        } else if(mongoHomePath != null) {
            final File mongoHome = new File(mongoHomePath);
            if(mongoHome.isDirectory()) {
                fcb.setDefaultWorkingDirectory(mongoHome);
            }
        }
        final File file = fcb.showOpenDialog();
        if(file != null) {
            mongoToolsFolderPathField.setText(file.getAbsolutePath());
        }
    }//GEN-LAST:event_browseMongoToolsFolderPathButtonActionPerformed

    private void restoreDefaultRenderingButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_restoreDefaultRenderingButtonActionPerformed
        for (Map.Entry<LabelCategory, LabelFontConf> entry : JsonCellRenderingOptions.Default.LABEL_CONFS.entrySet()) {
            labelConfBuilders.put(entry.getKey(), new LabelFontConf.Builder(entry.getValue()));
        }
        updateSelectedLabelFontConfUI();
    }//GEN-LAST:event_restoreDefaultRenderingButtonActionPerformed

    void load() {
        internalUpdate = true;
        for (LabelCategory labelCategory : LabelCategory.values()) {
            final LabelFontConf conf = jsonRenderingOptions.getLabelFontConf(labelCategory);
            labelConfBuilders.put(labelCategory, new LabelFontConf.Builder(conf));
        }
        loadLabelFontConfInUI(getSelectedLabelFontConfBuilder());
        final String mongoToolsFolderPath = mongoToolsOptions.getToolsFolder();
        if (mongoToolsFolderPath != null) {
            mongoToolsFolderPathField.setText(mongoToolsFolderPath);
        }
        internalUpdate = false;
    }

    void store() {
        for (LabelCategory labelCategory : LabelCategory.values()) {
            jsonRenderingOptions.setLabelFontConf(labelCategory, labelConfBuilders.get(labelCategory).build());
        }
        jsonRenderingOptions.store();
        final String mongoToolsFolderPath = mongoToolsFolderPathField.getText().trim();
        mongoToolsOptions.setToolsFolder(mongoToolsFolderPath.isEmpty() ? null : mongoToolsFolderPath);
        mongoToolsOptions.store();
    }

    boolean valid() {
        final String mongoExecPath = mongoToolsFolderPathField.getText().trim();
        if (mongoExecPath.isEmpty() == false) {
            return new File(mongoExecPath).isDirectory();
        }
        return true;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox backgroundComboBox;
    private javax.swing.JLabel backgroundLabel;
    private javax.swing.JButton browseFontButton;
    private javax.swing.JButton browseMongoToolsFolderPathButton;
    private javax.swing.JLabel categoriesLabel;
    private javax.swing.JList<LabelCategory> categoriesList;
    private javax.swing.JScrollPane categoriesScrollPane;
    private javax.swing.JTextField fontField;
    private javax.swing.JLabel fontLabel;
    private javax.swing.JComboBox foregroundComboBox;
    private javax.swing.JLabel foregroundLabel;
    private javax.swing.JPanel jsonTreeRenderingOptionsPanel;
    private javax.swing.JTextField mongoToolsFolderPathField;
    private javax.swing.JLabel mongoToolsFolderPathLabel;
    private javax.swing.JPanel renderingOptionsPanel;
    private javax.swing.JButton restoreDefaultRenderingButton;
    private javax.swing.JLabel restoreDefaultRenderingLabel;
    private javax.swing.JPanel shellOptionsPanel;
    // End of variables declaration//GEN-END:variables
}
