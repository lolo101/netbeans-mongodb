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
import org.netbeans.modules.mongodb.native_tools.MongoRestoreOptions;
import org.netbeans.modules.mongodb.options.MongoNativeToolsOptions;
import org.netbeans.modules.mongodb.ui.util.IntegerDocumentFilter;
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
    "restoreDialogTitle=mongorestore {0}"
})
public final class MongoRestoreOptionsPanel extends javax.swing.JPanel implements NativeToolOptionsDialog.OptionsPanel {

    private final char defaultPasswordEchoChar;

    /**
     * Creates new form MongoDumpOptionsOptionsPanel
     */
    public MongoRestoreOptionsPanel() {
        initComponents();
        disableOptionsAccordingToVersion();
        final PlainDocument document = (PlainDocument) portField.getDocument();
        document.setDocumentFilter(new IntegerDocumentFilter());
        defaultPasswordEchoChar = passwordField.getEchoChar();
        final String defaultPath = prefs().get("dump-restore-path", Paths.get("dump").toAbsolutePath().toString());
        dumpPathField.setText(defaultPath);
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
            options.put(MongoRestoreOptions.HOST, host);
        }
        final String port = portField.getText().trim();
        if (port.isEmpty() == false) {
            options.put(MongoRestoreOptions.PORT, port);
        }
        final String username = usernameField.getText().trim();
        if (username.isEmpty() == false) {
            options.put(MongoRestoreOptions.USERNAME, username);
        }
        final String password = new String(passwordField.getPassword());
        if (password.isEmpty() == false) {
            options.put(MongoRestoreOptions.PASSWORD, password);
        }
        final String db = dbField.getText().trim();
        if (db.isEmpty() == false) {
            options.put(MongoRestoreOptions.DB, db);
        }
        final String collection = collectionField.getText().trim();
        if (collection.isEmpty() == false) {
            options.put(MongoRestoreOptions.COLLECTION, collection);
        }
        if (ipv6CheckBox.isSelected()) {
            options.put(MongoRestoreOptions.IPV6, "");
        }
        if (sslCheckBox.isSelected()) {
            options.put(MongoRestoreOptions.SSL, "");
        }
        if (directoryPerDbCheckBox.isSelected()) {
            options.put(MongoRestoreOptions.DIRECTORY_PER_DB, "");
        }
        if (journalCheckBox.isSelected()) {
            options.put(MongoRestoreOptions.JOURNAL, "");
        }
        if (oplogReplayCheckBox.isSelected()) {
            options.put(MongoRestoreOptions.OPLOG_REPLAY, "");
        }
        return options;
    }

    @Override
    public void setOptions(Map<String, String> options) {
        final String host = options.get(MongoRestoreOptions.HOST);
        if (host != null) {
            hostField.setText(host);
        }
        final String port = options.get(MongoRestoreOptions.PORT);
        if (port != null) {
            portField.setText(port);
        }
        final String username = options.get(MongoRestoreOptions.USERNAME);
        if (username != null) {
            usernameField.setText(username);
        }
        final String password = options.get(MongoRestoreOptions.PASSWORD);
        if (password != null) {
            passwordField.setText(password);
        }
        final String db = options.get(MongoRestoreOptions.DB);
        if (db != null) {
            dbField.setText(db);
        }
        final String collection = options.get(MongoRestoreOptions.COLLECTION);
        if (collection != null) {
            collectionField.setText(collection);
        }
        ipv6CheckBox.setSelected(options.containsKey(MongoRestoreOptions.IPV6));
        sslCheckBox.setSelected(options.containsKey(MongoRestoreOptions.SSL));
        directoryPerDbCheckBox.setSelected(options.containsKey(MongoRestoreOptions.DIRECTORY_PER_DB));
        journalCheckBox.setSelected(options.containsKey(MongoRestoreOptions.JOURNAL));
        oplogReplayCheckBox.setSelected(options.containsKey(MongoRestoreOptions.OPLOG_REPLAY));
    }

    @Override
    public List<String> getArgs() {
        final List<String> args = new ArrayList<>();
        if (verbosityEditor.isVerboseSelected()) {
            args.add(verbosityEditor.getVerboseArg());
        }
        final String dumpPath = dumpPathField.getText().trim();
        if (dumpPath.isEmpty() == false) {
            args.add(dumpPath);
            prefs().put("dump-restore-path", dumpPath);
        }
        return args;
    }

    @Override
    public void setArgs(List<String> args) {
        for (String arg : args) {
            if (arg.matches("-v{1,5}")) {
                verbosityEditor.setVerboseArg(arg);
                verbosityEditor.setVerboseSelected(true);
            } else {
                dumpPathField.setText(arg);
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
        oplogReplayCheckBox = new javax.swing.JCheckBox();
        inputLabel = new javax.swing.JLabel();
        dumpPathField = new javax.swing.JTextField();
        browseOutputButton = new javax.swing.JButton();
        dbLabel = new javax.swing.JLabel();
        collectionLabel = new javax.swing.JLabel();
        dbField = new javax.swing.JTextField();
        collectionField = new javax.swing.JTextField();
        verbosityEditor = new org.netbeans.modules.mongodb.ui.native_tools.VerbosityEditor();

        org.openide.awt.Mnemonics.setLocalizedText(hostLabel, org.openide.util.NbBundle.getMessage(MongoRestoreOptionsPanel.class, "MongoRestoreOptionsPanel.hostLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(portLabel, org.openide.util.NbBundle.getMessage(MongoRestoreOptionsPanel.class, "MongoRestoreOptionsPanel.portLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(usernameLabel, org.openide.util.NbBundle.getMessage(MongoRestoreOptionsPanel.class, "MongoRestoreOptionsPanel.usernameLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(passwordLabel, org.openide.util.NbBundle.getMessage(MongoRestoreOptionsPanel.class, "MongoRestoreOptionsPanel.passwordLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(displayPasswordCheckBox, org.openide.util.NbBundle.getMessage(MongoRestoreOptionsPanel.class, "MongoRestoreOptionsPanel.displayPasswordCheckBox.text")); // NOI18N
        displayPasswordCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                displayPasswordCheckBoxActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(ipv6CheckBox, org.openide.util.NbBundle.getMessage(MongoRestoreOptionsPanel.class, "MongoRestoreOptionsPanel.ipv6CheckBox.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(sslCheckBox, org.openide.util.NbBundle.getMessage(MongoRestoreOptionsPanel.class, "MongoRestoreOptionsPanel.sslCheckBox.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(directoryPerDbCheckBox, org.openide.util.NbBundle.getMessage(MongoRestoreOptionsPanel.class, "MongoRestoreOptionsPanel.directoryPerDbCheckBox.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(journalCheckBox, org.openide.util.NbBundle.getMessage(MongoRestoreOptionsPanel.class, "MongoRestoreOptionsPanel.journalCheckBox.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(oplogReplayCheckBox, org.openide.util.NbBundle.getMessage(MongoRestoreOptionsPanel.class, "MongoRestoreOptionsPanel.oplogReplayCheckBox.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(inputLabel, org.openide.util.NbBundle.getMessage(MongoRestoreOptionsPanel.class, "MongoRestoreOptionsPanel.inputLabel.text")); // NOI18N

        dumpPathField.setEditable(false);

        org.openide.awt.Mnemonics.setLocalizedText(browseOutputButton, org.openide.util.NbBundle.getMessage(MongoRestoreOptionsPanel.class, "MongoRestoreOptionsPanel.browseOutputButton.text")); // NOI18N
        browseOutputButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseOutputButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(dbLabel, org.openide.util.NbBundle.getMessage(MongoRestoreOptionsPanel.class, "MongoRestoreOptionsPanel.dbLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(collectionLabel, org.openide.util.NbBundle.getMessage(MongoRestoreOptionsPanel.class, "MongoRestoreOptionsPanel.collectionLabel.text")); // NOI18N

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
                        .addComponent(passwordField, javax.swing.GroupLayout.DEFAULT_SIZE, 381, Short.MAX_VALUE)
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
                    .addComponent(oplogReplayCheckBox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(inputLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(dumpPathField, javax.swing.GroupLayout.DEFAULT_SIZE, 302, Short.MAX_VALUE)
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
                .addComponent(oplogReplayCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(verbosityEditor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(inputLabel)
                    .addComponent(dumpPathField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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
        final String output = dumpPathField.getText().trim();
        if (output.isEmpty() == false) {
            fcb.setDefaultWorkingDirectory(new File(output));
        }
        final File file = fcb.showOpenDialog();
        if (file != null) {
            dumpPathField.setText(file.getAbsolutePath());
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
    private javax.swing.JTextField dumpPathField;
    private javax.swing.JTextField hostField;
    private javax.swing.JLabel hostLabel;
    private javax.swing.JLabel inputLabel;
    private javax.swing.JCheckBox ipv6CheckBox;
    private javax.swing.JCheckBox journalCheckBox;
    private javax.swing.JCheckBox oplogReplayCheckBox;
    private javax.swing.JPasswordField passwordField;
    private javax.swing.JLabel passwordLabel;
    private javax.swing.JTextField portField;
    private javax.swing.JLabel portLabel;
    private javax.swing.JCheckBox sslCheckBox;
    private javax.swing.JTextField usernameField;
    private javax.swing.JLabel usernameLabel;
    private org.netbeans.modules.mongodb.ui.native_tools.VerbosityEditor verbosityEditor;
    // End of variables declaration//GEN-END:variables

}
