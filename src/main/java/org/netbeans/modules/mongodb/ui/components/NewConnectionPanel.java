/* 
 * The MIT License
 *
 * Copyright 2013 Tim Boudreau.
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
package org.netbeans.modules.mongodb.ui.components;

import com.mongodb.MongoClientURI;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotificationLineSupport;
import org.openide.NotifyDescriptor;
import org.openide.util.ChangeSupport;
import org.openide.util.NbBundle.Messages;

/**
 *
 * @author Tim Boudreau
 * @author Yann D'Isanto
 */
@Messages("ConnectionNameNotSet=Specify the connection name")
public class NewConnectionPanel extends javax.swing.JPanel implements DocumentListener, FocusListener {

    private final ChangeSupport supp = new ChangeSupport(this);

    private NotificationLineSupport notificationLineSupport;
    
    private MongoClientURI lastValidURI;

    /**
     * Creates new form NewConnectionPanel
     */
    public NewConnectionPanel() {
        initComponents();
        nameField.addFocusListener(this);
        uriField.addFocusListener(this);
        uriField.getDocument().addDocumentListener(this);
        performValidation();
    }

    public void addChangeListener(ChangeListener listener) {
        supp.addChangeListener(listener);
    }

    public void removeChangeListener(ChangeListener listener) {
        supp.removeChangeListener(listener);
    }

    public String getConnectionName() {
        return nameField.getText().trim();
    }

    public MongoClientURI getMongoURI() {
        return new MongoClientURI(uriField.getText());
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        performValidation();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        performValidation();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
    }

    private void performValidation() {
        final String name = getConnectionName();
        if (name.isEmpty()) {
            setProblem(Bundle.ConnectionNameNotSet());
            return;
        }
        try {
            lastValidURI = new MongoClientURI(uriField.getText());
        } catch (IllegalArgumentException ex) {
            setProblem(ex.getLocalizedMessage());
            return;
        }
        setProblem(null);
    }

    private boolean ok = true;

    private void setOk(boolean ok) {
        if (ok != this.ok) {
            this.ok = ok;
            supp.fireChange();
        }
    }

    public boolean isOk() {
        return ok;
    }

    private void setProblem(String problem) {
        if (problem == null) {
            clearNotificationLineSupport();
            setOk(true);
        } else {
            error(problem);
            setOk(false);
        }
    }

    @Override
    public void focusGained(FocusEvent e) {
        ((JTextComponent) e.getComponent()).selectAll();
    }

    @Override
    public void focusLost(FocusEvent e) {
        // do nothing
    }

    public NotificationLineSupport getNotificationLineSupport() {
        return notificationLineSupport;
    }

    public void setNotificationLineSupport(NotificationLineSupport notificationLineSupport) {
        this.notificationLineSupport = notificationLineSupport;
    }

    private void clearNotificationLineSupport() {
        if (notificationLineSupport != null) {
            notificationLineSupport.clearMessages();
        }
    }

    private void info(String message) {
        if (notificationLineSupport != null) {
            notificationLineSupport.setInformationMessage(message);
        }
    }

    private void error(String message) {
        if (notificationLineSupport != null) {
            notificationLineSupport.setErrorMessage(message);
        }
    }

    private void warn(String message) {
        if (notificationLineSupport != null) {
            notificationLineSupport.setWarningMessage(message);
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

        uriLabel = new javax.swing.JLabel();
        uriField = new javax.swing.JTextField();
        nameLabel = new javax.swing.JLabel();
        nameField = new javax.swing.JTextField();
        openURIEditorButton = new javax.swing.JButton();

        uriLabel.setLabelFor(uriField);
        org.openide.awt.Mnemonics.setLocalizedText(uriLabel, org.openide.util.NbBundle.getMessage(NewConnectionPanel.class, "NewConnectionPanel.uriLabel.text")); // NOI18N

        uriField.setText(org.openide.util.NbBundle.getMessage(NewConnectionPanel.class, "NewConnectionPanel.uriField.text")); // NOI18N
        uriField.setToolTipText(org.openide.util.NbBundle.getMessage(NewConnectionPanel.class, "NewConnectionPanel.uriField.toolTipText")); // NOI18N

        nameLabel.setLabelFor(nameField);
        org.openide.awt.Mnemonics.setLocalizedText(nameLabel, org.openide.util.NbBundle.getMessage(NewConnectionPanel.class, "NewConnectionPanel.nameLabel.text")); // NOI18N

        nameField.setText(org.openide.util.NbBundle.getMessage(NewConnectionPanel.class, "NewConnectionPanel.nameField.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(openURIEditorButton, org.openide.util.NbBundle.getMessage(NewConnectionPanel.class, "NewConnectionPanel.openURIEditorButton.text")); // NOI18N
        openURIEditorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openURIEditorButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(nameLabel)
                    .addComponent(uriLabel))
                .addGap(14, 14, 14)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(nameField, javax.swing.GroupLayout.DEFAULT_SIZE, 332, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(uriField)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(openURIEditorButton)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nameLabel)
                    .addComponent(nameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(uriLabel)
                    .addComponent(uriField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(openURIEditorButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void openURIEditorButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openURIEditorButtonActionPerformed
        
        final MongoURIEditorPanel editor = new MongoURIEditorPanel(lastValidURI);
        final DialogDescriptor desc = new DialogDescriptor(editor, "Mongo URI Editor");
        editor.setNotificationLineSupport(desc.createNotificationLineSupport());
        editor.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                desc.setValid(editor.valid());
            }
        });
        if(NotifyDescriptor.OK_OPTION.equals(DialogDisplayer.getDefault().notify(desc))) {
            uriField.setText(editor.getMongoURI().getURI());
        }
    }//GEN-LAST:event_openURIEditorButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField nameField;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JButton openURIEditorButton;
    private javax.swing.JTextField uriField;
    private javax.swing.JLabel uriLabel;
    // End of variables declaration//GEN-END:variables

}
