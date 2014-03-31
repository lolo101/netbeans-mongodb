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

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.prefs.Preferences;
import javax.swing.JPanel;
import javax.swing.text.PlainDocument;
import org.netbeans.modules.mongodb.native_tools.MongoDumpOptions;
import org.netbeans.modules.mongodb.options.MongoNativeToolsOptions;
import org.netbeans.modules.mongodb.ui.util.IntegerDocumentFilter;
import org.netbeans.modules.mongodb.ui.windows.CollectionView;
import org.netbeans.modules.mongodb.util.Version;
import org.openide.filesystems.FileChooserBuilder;
import org.openide.util.NbBundle.Messages;
import org.openide.util.NbPreferences;

/**
 *
 * @author Yann D'Isanto
 */
@Messages({
    "# {0} - version",
    "dumpDialogTitle=mongodump {0}"
})
public final class MongoDumpOptionsPanel extends javax.swing.JPanel implements NativeToolOptionsDialog.OptionsPanel {

    private final char defaultPasswordEchoChar;

    /**
     * Creates new form MongoDumpOptionsOptionsPanel
     */
    public MongoDumpOptionsPanel() {
        initComponents();
        disableOptionsAccordingToVersion();
        final PlainDocument document = (PlainDocument) portField.getDocument();
        document.setDocumentFilter(new IntegerDocumentFilter());
        defaultPasswordEchoChar = passwordField.getEchoChar();
        final String defaultPath = prefs().get("dump-restore-path", Paths.get("dump").toAbsolutePath().toString());
        outputField.setText(defaultPath);
    }

    public Preferences prefs() {
        return NbPreferences.forModule(MongoDumpOptionsPanel.class).node("native_tools");
    }

    private void disableOptionsAccordingToVersion() {
        final Version version = MongoNativeToolsOptions.INSTANCE.getToolsVersion();
        final Version v2_4 = new Version("2.4");
        if (version.compareTo(v2_4) < 0) {
            sslCheckBox.setEnabled(false);
            sslCheckBox.setToolTipText(Bundle.requiresVersion(v2_4));
        }
    }

    @Override
    public JPanel getPanel() {
        return this;
    }

    @Override
    public Map<String, String> getOptions() {
        final Map<String, String> options = new HashMap<>();
        final String host = hostField.getText().trim();
        if (host.isEmpty() == false) {
            options.put(MongoDumpOptions.HOST, host);
        }
        final String port = portField.getText().trim();
        if (port.isEmpty() == false) {
            options.put(MongoDumpOptions.PORT, port);
        }
        final String username = usernameField.getText().trim();
        if (username.isEmpty() == false) {
            options.put(MongoDumpOptions.USERNAME, username);
        }
        final String password = new String(passwordField.getPassword());
        if (password.isEmpty() == false) {
            options.put(MongoDumpOptions.PASSWORD, password);
        }
        final String db = dbField.getText().trim();
        if (db.isEmpty() == false) {
            options.put(MongoDumpOptions.DB, db);
        }
        final String collection = collectionField.getText().trim();
        if (collection.isEmpty() == false) {
            options.put(MongoDumpOptions.COLLECTION, collection);
        }
        if (ipv6CheckBox.isSelected()) {
            options.put(MongoDumpOptions.IPV6, "");
        }
        if (sslCheckBox.isSelected()) {
            options.put(MongoDumpOptions.SSL, "");
        }
        if (directoryPerDbCheckBox.isSelected()) {
            options.put(MongoDumpOptions.DIRECTORY_PER_DB, "");
        }
        if (journalCheckBox.isSelected()) {
            options.put(MongoDumpOptions.JOURNAL, "");
        }
        if (oplogCheckBox.isSelected()) {
            options.put(MongoDumpOptions.OPLOG, "");
        }
        if (repairCheckBox.isSelected()) {
            options.put(MongoDumpOptions.REPAIR, "");
        }
        if (forceTableScanCheckBox.isSelected()) {
            options.put(MongoDumpOptions.FORCE_TABLE_SCAN, "");
        }
        final String output = outputField.getText().trim();
        if (output.isEmpty() == false) {
            options.put(MongoDumpOptions.OUTPUT, output);
            prefs().put("dump-restore-path", output);
        }
        return options;
    }

    @Override
    public void setOptions(Map<String, String> options) {
        final String host = options.get(MongoDumpOptions.HOST);
        if (host != null) {
            hostField.setText(host);
        }
        final String port = options.get(MongoDumpOptions.PORT);
        if (port != null) {
            portField.setText(port);
        }
        final String username = options.get(MongoDumpOptions.USERNAME);
        if (username != null) {
            usernameField.setText(username);
        }
        final String password = options.get(MongoDumpOptions.PASSWORD);
        if (password != null) {
            passwordField.setText(password);
        }
        final String db = options.get(MongoDumpOptions.DB);
        if (db != null) {
            dbField.setText(db);
        }
        final String collection = options.get(MongoDumpOptions.COLLECTION);
        if (collection != null) {
            collectionField.setText(collection);
        }
        ipv6CheckBox.setSelected(options.containsKey(MongoDumpOptions.IPV6));
        sslCheckBox.setSelected(options.containsKey(MongoDumpOptions.SSL));
        directoryPerDbCheckBox.setSelected(options.containsKey(MongoDumpOptions.DIRECTORY_PER_DB));
        journalCheckBox.setSelected(options.containsKey(MongoDumpOptions.JOURNAL));
        oplogCheckBox.setSelected(options.containsKey(MongoDumpOptions.OPLOG));
        repairCheckBox.setSelected(options.containsKey(MongoDumpOptions.REPAIR));
        forceTableScanCheckBox.setSelected(options.containsKey(MongoDumpOptions.FORCE_TABLE_SCAN));
        final String output = options.get(MongoDumpOptions.OUTPUT);
        if (output != null) {
            outputField.setText(output);
        }
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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(passwordLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(passwordField, javax.swing.GroupLayout.DEFAULT_SIZE, 313, Short.MAX_VALUE)
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
                        .addComponent(outputField, javax.swing.GroupLayout.DEFAULT_SIZE, 303, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(browseOutputButton))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(verbosityEditor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {collectionLabel, dbLabel, hostLabel, passwordLabel, portLabel, usernameLabel});

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
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(dbLabel)
                    .addComponent(dbField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(collectionLabel)
                    .addComponent(collectionField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton browseOutputButton;
    private javax.swing.JTextField collectionField;
    private javax.swing.JLabel collectionLabel;
    private javax.swing.JTextField dbField;
    private javax.swing.JLabel dbLabel;
    private javax.swing.JCheckBox directoryPerDbCheckBox;
    private javax.swing.JCheckBox displayPasswordCheckBox;
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
    private javax.swing.JCheckBox repairCheckBox;
    private javax.swing.JCheckBox sslCheckBox;
    private javax.swing.JTextField usernameField;
    private javax.swing.JLabel usernameLabel;
    private org.netbeans.modules.mongodb.ui.native_tools.VerbosityEditor verbosityEditor;
    // End of variables declaration//GEN-END:variables

}
