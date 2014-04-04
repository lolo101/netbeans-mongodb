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
package org.netbeans.modules.mongodb.ui.native_tools;

import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.text.PlainDocument;
import org.netbeans.modules.mongodb.native_tools.MongoDumpOptions;
import org.netbeans.modules.mongodb.native_tools.MongoNativeTool;
import org.netbeans.modules.mongodb.options.MongoNativeToolsOptions;
import org.netbeans.modules.mongodb.ui.util.IntegerDocumentFilter;
import org.netbeans.modules.mongodb.ui.util.JsonUI;
import org.netbeans.modules.mongodb.util.Version;
import org.openide.filesystems.FileChooserBuilder;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Yann D'Isanto
 */
@ServiceProvider(service = NativeToolOptionsDialog.OptionsAndArgsPanel.class)
public final class MongoDumpOptionsPanel extends AbstractOptionsAndArgsPanel implements NativeToolOptionsDialog.OptionsAndArgsPanel {

    private final char defaultPasswordEchoChar;

    /**
     * Creates new form MongoDumpOptionsOptionsPanel
     */
    public MongoDumpOptionsPanel() {
        super(MongoNativeTool.MONGO_DUMP);
        initComponents();
        disableOptionsAccordingToVersion();
        final PlainDocument document = (PlainDocument) portField.getDocument();
        document.setDocumentFilter(new IntegerDocumentFilter());
        defaultPasswordEchoChar = passwordField.getEchoChar();
        final String defaultPath = prefs().get("dump-restore-path", Paths.get("dump").toAbsolutePath().toString());
        outputField.setText(defaultPath);
    }

    private void disableOptionsAccordingToVersion() {
        final Version version = MongoNativeToolsOptions.INSTANCE.getToolsVersion();
        final Version v2_4 = new Version("2.4");
        if (version.compareTo(v2_4) < 0) {
            final String toolTipText = Bundle.requiresVersion(v2_4);
            sslCheckBox.setEnabled(false);
            sslCheckBox.setToolTipText(toolTipText);
            authDatabaseLabel.setEnabled(false);
            authDatabaseField.setEnabled(false);
            authDatabaseField.setToolTipText(toolTipText);
            authMechanismLabel.setEnabled(false);
            authMechanismField.setEnabled(false);
            authMechanismField.setToolTipText(toolTipText);
        }
    }

    @Override
    public Map<String, String> getOptions() {
        final Map<String, String> options = new HashMap<>();
        readOptionFromUI(options, MongoDumpOptions.HOST, hostField);
        readOptionFromUI(options, MongoDumpOptions.PORT, portField);
        readOptionFromUI(options, MongoDumpOptions.USERNAME, usernameField);
        readOptionFromUI(options, MongoDumpOptions.PASSWORD, passwordField);
        readOptionFromUI(options, MongoDumpOptions.AUTH_DATABASE, authDatabaseField);
        readOptionFromUI(options, MongoDumpOptions.AUTH_MECHANISM, authMechanismField);
        readOptionFromUI(options, MongoDumpOptions.DB, dbField);
        readOptionFromUI(options, MongoDumpOptions.COLLECTION, collectionField);
        readOptionFromUI(options, MongoDumpOptions.QUERY, queryField);
        readOptionFromUI(options, MongoDumpOptions.DB_PATH, dbPathField);
        readOptionFromUI(options, MongoDumpOptions.IPV6, ipv6CheckBox);
        readOptionFromUI(options, MongoDumpOptions.SSL, sslCheckBox);
        readOptionFromUI(options, MongoDumpOptions.DIRECTORY_PER_DB, directoryPerDbCheckBox);
        readOptionFromUI(options, MongoDumpOptions.JOURNAL, journalCheckBox);
        readOptionFromUI(options, MongoDumpOptions.OPLOG, oplogCheckBox);
        readOptionFromUI(options, MongoDumpOptions.REPAIR, repairCheckBox);
        readOptionFromUI(options, MongoDumpOptions.FORCE_TABLE_SCAN, forceTableScanCheckBox);
        final String output = outputField.getText().trim();
        if (output.isEmpty() == false) {
            options.put(MongoDumpOptions.OUTPUT, output);
            prefs().put("dump-restore-path", output);
        }
        return options;
    }

    @Override
    public void setOptions(Map<String, String> options) {
        populateUIWithOption(options, MongoDumpOptions.HOST, hostField);
        populateUIWithOption(options, MongoDumpOptions.PORT, portField);
        populateUIWithOption(options, MongoDumpOptions.USERNAME, usernameField);
        populateUIWithOption(options, MongoDumpOptions.PASSWORD, passwordField);
        populateUIWithOption(options, MongoDumpOptions.AUTH_DATABASE, authDatabaseField);
        populateUIWithOption(options, MongoDumpOptions.AUTH_MECHANISM, authMechanismField);
        populateUIWithOption(options, MongoDumpOptions.DB, dbField);
        populateUIWithOption(options, MongoDumpOptions.COLLECTION, collectionField);
        populateUIWithOption(options, MongoDumpOptions.QUERY, queryField);
        populateUIWithOption(options, MongoDumpOptions.DB_PATH, dbPathField);
        populateUIWithOption(options, MongoDumpOptions.IPV6, ipv6CheckBox);
        populateUIWithOption(options, MongoDumpOptions.SSL, sslCheckBox);
        populateUIWithOption(options, MongoDumpOptions.DIRECTORY_PER_DB, directoryPerDbCheckBox);
        populateUIWithOption(options, MongoDumpOptions.JOURNAL, journalCheckBox);
        populateUIWithOption(options, MongoDumpOptions.OPLOG, oplogCheckBox);
        populateUIWithOption(options, MongoDumpOptions.REPAIR, repairCheckBox);
        populateUIWithOption(options, MongoDumpOptions.FORCE_TABLE_SCAN, forceTableScanCheckBox);
        final String defaultPath = prefs().get("dump-restore-path", Paths.get("dump").toAbsolutePath().toString());
        populateUIWithOption(options, MongoDumpOptions.OUTPUT, outputField, defaultPath);
    }

    @Override
    public List<String> getArgs() {
        final List<String> args = new ArrayList<>();
        if (verbosityEditor.isVerboseSelected()) {
            args.add(verbosityEditor.getVerboseArg());
        }
        return args;
    }

    @Override
    public void setArgs(List<String> args) {
        verbosityEditor.setVerboseSelected(false);
        for (String arg : args) {
            if (arg.matches("-v{1,5}")) {
                verbosityEditor.setVerboseArg(arg);
                verbosityEditor.setVerboseSelected(true);
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

        hostLabel = new javax.swing.JLabel();
        portLabel = new javax.swing.JLabel();
        usernameLabel = new javax.swing.JLabel();
        passwordLabel = new javax.swing.JLabel();
        hostField = new javax.swing.JTextField();
        portField = new javax.swing.JTextField();
        usernameField = new javax.swing.JTextField();
        passwordField = new javax.swing.JPasswordField();
        displayPasswordCheckBox = new javax.swing.JCheckBox();
        ipv6CheckBox = new javax.swing.JCheckBox();
        sslCheckBox = new javax.swing.JCheckBox();
        directoryPerDbCheckBox = new javax.swing.JCheckBox();
        journalCheckBox = new javax.swing.JCheckBox();
        oplogCheckBox = new javax.swing.JCheckBox();
        repairCheckBox = new javax.swing.JCheckBox();
        forceTableScanCheckBox = new javax.swing.JCheckBox();
        outputLabel = new javax.swing.JLabel();
        outputField = new javax.swing.JTextField();
        browseOutputButton = new javax.swing.JButton();
        dbLabel = new javax.swing.JLabel();
        collectionLabel = new javax.swing.JLabel();
        dbField = new javax.swing.JTextField();
        collectionField = new javax.swing.JTextField();
        verbosityEditor = new org.netbeans.modules.mongodb.ui.native_tools.VerbosityEditor();
        authDatabaseLabel = new javax.swing.JLabel();
        authMechanismLabel = new javax.swing.JLabel();
        authDatabaseField = new javax.swing.JTextField();
        authMechanismField = new javax.swing.JTextField();
        authLabel = new javax.swing.JLabel();
        dbPathLabel = new javax.swing.JLabel();
        dbPathField = new javax.swing.JTextField();
        browseDBPathButton = new javax.swing.JButton();
        queryLabel = new javax.swing.JLabel();
        queryField = new javax.swing.JTextField();
        editQueryButton = new javax.swing.JButton();

        org.openide.awt.Mnemonics.setLocalizedText(hostLabel, org.openide.util.NbBundle.getMessage(MongoDumpOptionsPanel.class, "MongoDumpOptionsPanel.hostLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(portLabel, org.openide.util.NbBundle.getMessage(MongoDumpOptionsPanel.class, "MongoDumpOptionsPanel.portLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(usernameLabel, org.openide.util.NbBundle.getMessage(MongoDumpOptionsPanel.class, "MongoDumpOptionsPanel.usernameLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(passwordLabel, org.openide.util.NbBundle.getMessage(MongoDumpOptionsPanel.class, "MongoDumpOptionsPanel.passwordLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(displayPasswordCheckBox, org.openide.util.NbBundle.getMessage(MongoDumpOptionsPanel.class, "MongoDumpOptionsPanel.displayPasswordCheckBox.text")); // NOI18N
        displayPasswordCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                displayPasswordCheckBoxActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(ipv6CheckBox, org.openide.util.NbBundle.getMessage(MongoDumpOptionsPanel.class, "MongoDumpOptionsPanel.ipv6CheckBox.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(sslCheckBox, org.openide.util.NbBundle.getMessage(MongoDumpOptionsPanel.class, "MongoDumpOptionsPanel.sslCheckBox.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(directoryPerDbCheckBox, org.openide.util.NbBundle.getMessage(MongoDumpOptionsPanel.class, "MongoDumpOptionsPanel.directoryPerDbCheckBox.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(journalCheckBox, org.openide.util.NbBundle.getMessage(MongoDumpOptionsPanel.class, "MongoDumpOptionsPanel.journalCheckBox.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(oplogCheckBox, org.openide.util.NbBundle.getMessage(MongoDumpOptionsPanel.class, "MongoDumpOptionsPanel.oplogCheckBox.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(repairCheckBox, org.openide.util.NbBundle.getMessage(MongoDumpOptionsPanel.class, "MongoDumpOptionsPanel.repairCheckBox.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(forceTableScanCheckBox, org.openide.util.NbBundle.getMessage(MongoDumpOptionsPanel.class, "MongoDumpOptionsPanel.forceTableScanCheckBox.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(outputLabel, org.openide.util.NbBundle.getMessage(MongoDumpOptionsPanel.class, "MongoDumpOptionsPanel.outputLabel.text")); // NOI18N

        outputField.setEditable(false);

        org.openide.awt.Mnemonics.setLocalizedText(browseOutputButton, org.openide.util.NbBundle.getMessage(MongoDumpOptionsPanel.class, "MongoDumpOptionsPanel.browseOutputButton.text")); // NOI18N
        browseOutputButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseOutputButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(dbLabel, org.openide.util.NbBundle.getMessage(MongoDumpOptionsPanel.class, "MongoDumpOptionsPanel.dbLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(collectionLabel, org.openide.util.NbBundle.getMessage(MongoDumpOptionsPanel.class, "MongoDumpOptionsPanel.collectionLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(authDatabaseLabel, org.openide.util.NbBundle.getMessage(MongoDumpOptionsPanel.class, "MongoDumpOptionsPanel.authDatabaseLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(authMechanismLabel, org.openide.util.NbBundle.getMessage(MongoDumpOptionsPanel.class, "MongoDumpOptionsPanel.authMechanismLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(authLabel, org.openide.util.NbBundle.getMessage(MongoDumpOptionsPanel.class, "MongoDumpOptionsPanel.authLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(dbPathLabel, org.openide.util.NbBundle.getMessage(MongoDumpOptionsPanel.class, "MongoDumpOptionsPanel.dbPathLabel.text")); // NOI18N

        dbPathField.setEditable(false);

        org.openide.awt.Mnemonics.setLocalizedText(browseDBPathButton, org.openide.util.NbBundle.getMessage(MongoDumpOptionsPanel.class, "MongoDumpOptionsPanel.browseDBPathButton.text")); // NOI18N
        browseDBPathButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseDBPathButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(queryLabel, org.openide.util.NbBundle.getMessage(MongoDumpOptionsPanel.class, "MongoDumpOptionsPanel.queryLabel.text")); // NOI18N

        queryField.setEditable(false);

        org.openide.awt.Mnemonics.setLocalizedText(editQueryButton, org.openide.util.NbBundle.getMessage(MongoDumpOptionsPanel.class, "MongoDumpOptionsPanel.editQueryButton.text")); // NOI18N
        editQueryButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editQueryButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(authDatabaseLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(authDatabaseField))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(authMechanismLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(authMechanismField))))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(passwordLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(passwordField, javax.swing.GroupLayout.DEFAULT_SIZE, 303, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(displayPasswordCheckBox))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(usernameLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(usernameField))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(hostLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(hostField))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(portLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(portField))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(dbLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(dbField))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(collectionLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(collectionField))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(queryLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(queryField)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(editQueryButton))
                    .addComponent(ipv6CheckBox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(sslCheckBox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(directoryPerDbCheckBox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(journalCheckBox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(oplogCheckBox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(repairCheckBox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(forceTableScanCheckBox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(outputLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(outputField, javax.swing.GroupLayout.DEFAULT_SIZE, 293, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(browseOutputButton))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(dbPathLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(dbPathField)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(browseDBPathButton))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(authLabel)
                            .addComponent(verbosityEditor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {authDatabaseLabel, authMechanismLabel});

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {collectionLabel, dbLabel, dbPathLabel, hostLabel, passwordLabel, portLabel, queryLabel, usernameLabel});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(hostLabel)
                    .addComponent(hostField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(portLabel)
                    .addComponent(portField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(usernameLabel)
                    .addComponent(usernameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(passwordLabel)
                    .addComponent(passwordField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(displayPasswordCheckBox))
                .addGap(18, 18, 18)
                .addComponent(authLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(authDatabaseLabel)
                    .addComponent(authDatabaseField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(authMechanismLabel)
                    .addComponent(authMechanismField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(dbLabel)
                    .addComponent(dbField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(collectionLabel)
                    .addComponent(collectionField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(queryLabel)
                    .addComponent(queryField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(editQueryButton))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(dbPathLabel)
                    .addComponent(dbPathField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(browseDBPathButton))
                .addGap(18, 18, 18)
                .addComponent(ipv6CheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(sslCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(directoryPerDbCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(journalCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(oplogCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(repairCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(forceTableScanCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(verbosityEditor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(outputLabel)
                    .addComponent(outputField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(browseOutputButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void displayPasswordCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_displayPasswordCheckBoxActionPerformed
        final char echoChar = displayPasswordCheckBox.isSelected()
            ? 0
            : defaultPasswordEchoChar;
        passwordField.setEchoChar(echoChar);
    }//GEN-LAST:event_displayPasswordCheckBoxActionPerformed

    private void browseOutputButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseOutputButtonActionPerformed
        final FileChooserBuilder fcb = new FileChooserBuilder("dump-restore-path");
        fcb.setDirectoriesOnly(true);
        final String output = outputField.getText().trim();
        if (output.isEmpty() == false) {
            fcb.setDefaultWorkingDirectory(new File(output));
        }
        final File file = fcb.showSaveDialog();
        if (file != null) {
            outputField.setText(file.getAbsolutePath());
        }
    }//GEN-LAST:event_browseOutputButtonActionPerformed

    private void browseDBPathButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseDBPathButtonActionPerformed
        final FileChooserBuilder fcb = new FileChooserBuilder("dump-restore-db-path");
        fcb.setDirectoriesOnly(true);
        final String output = dbPathField.getText().trim();
        if (output.isEmpty() == false) {
            fcb.setDefaultWorkingDirectory(new File(output));
        }
        final File file = fcb.showSaveDialog();
        if (file != null) {
            dbPathField.setText(file.getAbsolutePath());
        }
    }//GEN-LAST:event_browseDBPathButtonActionPerformed

    private void editQueryButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editQueryButtonActionPerformed
        final DBObject dbObject = JsonUI.showEditor("title", queryField.getText());
        if (dbObject != null) {
            queryField.setText(JSON.serialize(dbObject));
        }
    }//GEN-LAST:event_editQueryButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField authDatabaseField;
    private javax.swing.JLabel authDatabaseLabel;
    private javax.swing.JLabel authLabel;
    private javax.swing.JTextField authMechanismField;
    private javax.swing.JLabel authMechanismLabel;
    private javax.swing.JButton browseDBPathButton;
    private javax.swing.JButton browseOutputButton;
    private javax.swing.JTextField collectionField;
    private javax.swing.JLabel collectionLabel;
    private javax.swing.JTextField dbField;
    private javax.swing.JLabel dbLabel;
    private javax.swing.JTextField dbPathField;
    private javax.swing.JLabel dbPathLabel;
    private javax.swing.JCheckBox directoryPerDbCheckBox;
    private javax.swing.JCheckBox displayPasswordCheckBox;
    private javax.swing.JButton editQueryButton;
    private javax.swing.JCheckBox forceTableScanCheckBox;
    private javax.swing.JTextField hostField;
    private javax.swing.JLabel hostLabel;
    private javax.swing.JCheckBox ipv6CheckBox;
    private javax.swing.JCheckBox journalCheckBox;
    private javax.swing.JCheckBox oplogCheckBox;
    private javax.swing.JTextField outputField;
    private javax.swing.JLabel outputLabel;
    private javax.swing.JPasswordField passwordField;
    private javax.swing.JLabel passwordLabel;
    private javax.swing.JTextField portField;
    private javax.swing.JLabel portLabel;
    private javax.swing.JTextField queryField;
    private javax.swing.JLabel queryLabel;
    private javax.swing.JCheckBox repairCheckBox;
    private javax.swing.JCheckBox sslCheckBox;
    private javax.swing.JTextField usernameField;
    private javax.swing.JLabel usernameLabel;
    private org.netbeans.modules.mongodb.ui.native_tools.VerbosityEditor verbosityEditor;
    // End of variables declaration//GEN-END:variables

}
