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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.text.PlainDocument;
import org.netbeans.modules.mongodb.native_tools.MongoNativeTool;
import org.netbeans.modules.mongodb.native_tools.MongoRestoreOptions;
import org.netbeans.modules.mongodb.native_tools.MongoTopOptions;
import org.netbeans.modules.mongodb.options.MongoNativeToolsOptions;
import org.netbeans.modules.mongodb.ui.util.IntegerDocumentFilter;
import org.netbeans.modules.mongodb.util.Version;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Yann D'Isanto
 */
@ServiceProvider(service = NativeToolOptionsDialog.OptionsAndArgsPanel.class)
public final class MongoTopOptionsPanel extends AbstractOptionsAndArgsPanel implements NativeToolOptionsDialog.OptionsAndArgsPanel {

    private final char defaultPasswordEchoChar;

    /**
     * Creates new form MongoTopOptionsOptionsPanel
     */
    public MongoTopOptionsPanel() {
        super(MongoNativeTool.MONGO_TOP);
        initComponents();
        disableOptionsAccordingToVersion();
        PlainDocument document = (PlainDocument) portField.getDocument();
        document.setDocumentFilter(new IntegerDocumentFilter());
        document = (PlainDocument) sleepTimeField.getDocument();
        document.setDocumentFilter(new IntegerDocumentFilter());
        defaultPasswordEchoChar = passwordField.getEchoChar();
    }

    private void disableOptionsAccordingToVersion() {
        final Version version = MongoNativeToolsOptions.INSTANCE.getToolsVersion();
        final Version v2_2 = new Version("2.2");
        final Version v2_4 = new Version("2.4");
        if (version.compareTo(v2_4) < 0) {
            final String toolTipText = Bundle.requiresVersion(v2_4);
            authDatabaseLabel.setEnabled(false);
            authDatabaseField.setEnabled(false);
            authDatabaseField.setToolTipText(toolTipText);
            authMechanismLabel.setEnabled(false);
            authMechanismField.setEnabled(false);
            authMechanismField.setToolTipText(toolTipText);
        }
        if (version.compareTo(v2_2) < 0) {
            final String toolTipText = Bundle.requiresVersion(v2_2);
            locksCheckBox.setEnabled(false);
            locksCheckBox.setToolTipText(toolTipText);
        }
    }

    @Override
    public Map<String, String> getOptions() {
        final Map<String, String> options = new HashMap<>();
        readOptionFromUI(options, MongoTopOptions.HOST, hostField);
        readOptionFromUI(options, MongoTopOptions.PORT, portField);
        readOptionFromUI(options, MongoTopOptions.USERNAME, usernameField);
        readOptionFromUI(options, MongoTopOptions.PASSWORD, passwordField);
        readOptionFromUI(options, MongoTopOptions.AUTH_DATABASE, authDatabaseField);
        readOptionFromUI(options, MongoTopOptions.AUTH_MECHANISM, authMechanismField);
        readOptionFromUI(options, MongoTopOptions.IPV6, ipv6CheckBox);
        readOptionFromUI(options, MongoTopOptions.LOCKS, locksCheckBox);
        return options;
    }

    @Override
    public void setOptions(Map<String, String> options) {
        populateUIWithOption(options, MongoTopOptions.HOST, hostField);
        populateUIWithOption(options, MongoTopOptions.PORT, portField);
        populateUIWithOption(options, MongoTopOptions.USERNAME, usernameField);
        populateUIWithOption(options, MongoTopOptions.PASSWORD, passwordField);
        populateUIWithOption(options, MongoTopOptions.AUTH_DATABASE, authDatabaseField);
        populateUIWithOption(options, MongoTopOptions.AUTH_MECHANISM, authMechanismField);
        populateUIWithOption(options, MongoTopOptions.IPV6, ipv6CheckBox);
        populateUIWithOption(options, MongoTopOptions.LOCKS, locksCheckBox);
    }

    @Override
    public List<String> getArgs() {
        final List<String> args = new ArrayList<>();
        if (verbosityEditor.isVerboseSelected()) {
            args.add(verbosityEditor.getVerboseArg());
        }
        final String sleepTime = sleepTimeField.getText().trim();
        if(sleepTime.isEmpty() == false) {
            args.add(sleepTime);
        }
        return args;
    }

    @Override
    public void setArgs(List<String> args) {
        verbosityEditor.setVerboseSelected(false);
        sleepTimeField.setText("");
        for (String arg : args) {
            if (arg.matches("-v{1,5}")) {
                verbosityEditor.setVerboseArg(arg);
                verbosityEditor.setVerboseSelected(true);
            } else if (arg.matches("\\d+")) {
                sleepTimeField.setText(arg);
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
        locksCheckBox = new javax.swing.JCheckBox();
        sleepTimeLabel = new javax.swing.JLabel();
        sleepTimeField = new javax.swing.JTextField();
        verbosityEditor = new org.netbeans.modules.mongodb.ui.native_tools.VerbosityEditor();
        authDatabaseLabel = new javax.swing.JLabel();
        authMechanismLabel = new javax.swing.JLabel();
        authDatabaseField = new javax.swing.JTextField();
        authMechanismField = new javax.swing.JTextField();
        authLabel = new javax.swing.JLabel();
        defaultSleepTimeLabel = new javax.swing.JLabel();

        org.openide.awt.Mnemonics.setLocalizedText(hostLabel, org.openide.util.NbBundle.getMessage(MongoTopOptionsPanel.class, "MongoDumpOptionsPanel.hostLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(portLabel, org.openide.util.NbBundle.getMessage(MongoTopOptionsPanel.class, "MongoDumpOptionsPanel.portLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(usernameLabel, org.openide.util.NbBundle.getMessage(MongoTopOptionsPanel.class, "MongoDumpOptionsPanel.usernameLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(passwordLabel, org.openide.util.NbBundle.getMessage(MongoTopOptionsPanel.class, "MongoDumpOptionsPanel.passwordLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(displayPasswordCheckBox, org.openide.util.NbBundle.getMessage(MongoTopOptionsPanel.class, "MongoDumpOptionsPanel.displayPasswordCheckBox.text")); // NOI18N
        displayPasswordCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                displayPasswordCheckBoxActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(ipv6CheckBox, org.openide.util.NbBundle.getMessage(MongoTopOptionsPanel.class, "MongoDumpOptionsPanel.ipv6CheckBox.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(locksCheckBox, org.openide.util.NbBundle.getMessage(MongoTopOptionsPanel.class, "MongoDumpOptionsPanel.sslCheckBox.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(sleepTimeLabel, org.openide.util.NbBundle.getMessage(MongoTopOptionsPanel.class, "MongoDumpOptionsPanel.outputLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(authDatabaseLabel, org.openide.util.NbBundle.getMessage(MongoTopOptionsPanel.class, "MongoDumpOptionsPanel.authDatabaseLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(authMechanismLabel, org.openide.util.NbBundle.getMessage(MongoTopOptionsPanel.class, "MongoDumpOptionsPanel.authMechanismLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(authLabel, org.openide.util.NbBundle.getMessage(MongoTopOptionsPanel.class, "MongoDumpOptionsPanel.authLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(defaultSleepTimeLabel, org.openide.util.NbBundle.getMessage(MongoTopOptionsPanel.class, "MongoTopOptionsPanel.defaultSleepTimeLabel.text")); // NOI18N

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
                        .addComponent(passwordField, javax.swing.GroupLayout.DEFAULT_SIZE, 331, Short.MAX_VALUE)
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
                    .addComponent(ipv6CheckBox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(locksCheckBox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(authLabel)
                            .addComponent(verbosityEditor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(sleepTimeLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(sleepTimeField)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(defaultSleepTimeLabel)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {authDatabaseLabel, authMechanismLabel});

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {hostLabel, passwordLabel, portLabel, usernameLabel});

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
                .addComponent(ipv6CheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(locksCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(verbosityEditor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(19, 19, 19)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(sleepTimeLabel)
                    .addComponent(sleepTimeField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(defaultSleepTimeLabel))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void displayPasswordCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_displayPasswordCheckBoxActionPerformed
        final char echoChar = displayPasswordCheckBox.isSelected()
            ? 0
            : defaultPasswordEchoChar;
        passwordField.setEchoChar(echoChar);
    }//GEN-LAST:event_displayPasswordCheckBoxActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField authDatabaseField;
    private javax.swing.JLabel authDatabaseLabel;
    private javax.swing.JLabel authLabel;
    private javax.swing.JTextField authMechanismField;
    private javax.swing.JLabel authMechanismLabel;
    private javax.swing.JLabel defaultSleepTimeLabel;
    private javax.swing.JCheckBox displayPasswordCheckBox;
    private javax.swing.JTextField hostField;
    private javax.swing.JLabel hostLabel;
    private javax.swing.JCheckBox ipv6CheckBox;
    private javax.swing.JCheckBox locksCheckBox;
    private javax.swing.JPasswordField passwordField;
    private javax.swing.JLabel passwordLabel;
    private javax.swing.JTextField portField;
    private javax.swing.JLabel portLabel;
    private javax.swing.JTextField sleepTimeField;
    private javax.swing.JLabel sleepTimeLabel;
    private javax.swing.JTextField usernameField;
    private javax.swing.JLabel usernameLabel;
    private org.netbeans.modules.mongodb.ui.native_tools.VerbosityEditor verbosityEditor;
    // End of variables declaration//GEN-END:variables

}
