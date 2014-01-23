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

import com.mongodb.MongoClientURI;
import java.awt.event.KeyEvent;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.swing.DefaultListModel;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.PlainDocument;
import org.openide.NotificationLineSupport;
import org.openide.util.ChangeSupport;
import org.openide.util.Exceptions;

/**
 *
 * @author Yann D'Isanto
 */
public final class MongoURIEditorPanel extends javax.swing.JPanel {

    private static final String URI_PREFIX = "mongodb://";

    private static final String UTF_8 = "UTF-8";

    private final ChangeSupport changeSupport = new ChangeSupport(this);

    private NotificationLineSupport notificationLineSupport;

    private final DefaultListModel<Host> hostsListModel = new DefaultListModel<>();

    private final DefaultListModel<Option> optionsListModel = new DefaultListModel<>();

    private final Object lock = new Object();

    private boolean uriUserInput = false;

    private boolean uriFieldUpdate = false;

    public MongoURIEditorPanel() {
        this(null);
    }

    public MongoURIEditorPanel(MongoClientURI uri) {
        initComponents();
        final DocumentListener hostValidationListener = new AbstractDocumentListener() {

            @Override
            protected void onChange() {
                addHostButton.setEnabled(hostField.getText().isEmpty() == false);
            }
        };
        hostField.getDocument().addDocumentListener(hostValidationListener);
        final DocumentListener optionValidationListener = new AbstractDocumentListener() {

            @Override
            protected void onChange() {
                addOptionButton.setEnabled(
                    optionNameField.getText().isEmpty() == false
                    && optionValueField.getText().isEmpty() == false);
            }
        };
        optionNameField.getDocument().addDocumentListener(optionValidationListener);
        optionValueField.getDocument().addDocumentListener(optionValidationListener);
        final DocumentListener fireChangeDocumentListener = new AbstractDocumentListener() {

            @Override
            protected void onChange() {
                updateURIField();
            }

        };
        usernameField.getDocument().addDocumentListener(fireChangeDocumentListener);
        passwordField.getDocument().addDocumentListener(fireChangeDocumentListener);
        databaseField.getDocument().addDocumentListener(fireChangeDocumentListener);
        uriField.getDocument().addDocumentListener(new AbstractDocumentListener() {

            @Override
            protected void onChange() {
                synchronized (lock) {
                    if (uriUserInput || uriFieldUpdate) {
                        return;
                    }
                    uriUserInput = true;
                }
                try {
                    setMongoURI(new MongoClientURI(uriField.getText()));
                } catch (IllegalArgumentException ex) {
                    error(ex.getLocalizedMessage());
                }
                uriUserInput = false;

            }
        });
        if (uri != null) {
            setMongoURI(uri);
        }
    }

    private void updateURIField() {
        synchronized (lock) {
            if (uriUserInput || uriFieldUpdate) {
                return;
            }
            uriFieldUpdate = true;
        }
        final String uri = computeMongoURIString();
        try {
            uriField.setText(new MongoClientURI(uri).getURI());
        } catch (IllegalArgumentException ex) {
            error(ex.getLocalizedMessage());
        }
        changeSupport.fireChange();
        uriFieldUpdate = false;
    }

    public boolean valid() {
        try {
            new MongoClientURI(computeMongoURIString());
            return true;
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }

    public void setMongoURI(MongoClientURI uri) {
        hostsListModel.clear();
        optionsListModel.clear();
        for (String string : uri.getHosts()) {
            final String[] rawHost = string.split(":");
            final String hostname = urlDecode(rawHost[0]);
            final Integer port = rawHost.length > 1
                ? Integer.parseInt(rawHost[1])
                : null;
            hostsListModel.addElement(new Host(hostname, port));
        }
        usernameField.setText(uri.getUsername());
        if (uri.getPassword() != null) {
            passwordField.setText(new String(uri.getPassword()));
        }
        databaseField.setText(uri.getDatabase());
        final List<Option> options = decodeOptions(uri);
        optionsListModel.clear();
        for (Option option : options) {
            optionsListModel.addElement(option);
        }
        updateURIField();
    }

    public MongoClientURI getMongoURI() {
        return new MongoClientURI(uriField.getText());
    }

    private String computeMongoURIString() {
        final StringBuilder sb = new StringBuilder(URI_PREFIX)
            .append(encodeCredentials())
            .append(encodeHosts())
            .append(encodeDatabaseWithOptions());
        return sb.toString();
    }

    private String encodeCredentials() {
        final String username = usernameField.getText();
        final String password = new String(passwordField.getPassword());
        if (username.isEmpty() && password.isEmpty()) {
            return "";
        }
        return new StringBuilder()
            .append(urlEncode(username))
            .append(':')
            .append(urlEncode(password))
            .append('@')
            .toString();
    }

    private String encodeHosts() {
        final StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Object item : hostsListModel.toArray()) {
            final Host host = (Host) item;
            if (first) {
                first = false;
            } else {
                sb.append(',');
            }
            sb.append(encodeHost(host));
        }
        return sb.toString();
    }

    private String encodeDatabaseWithOptions() {
        final StringBuilder sb = new StringBuilder()
            .append(urlEncode(databaseField.getText()))
            .append(encodeOptions());
        if (sb.length() > 0) {
            sb.insert(0, "/");
        }
        return sb.toString();
    }

    private String encodeOptions() {
        if (optionsListModel.isEmpty()) {
            return "";
        }
        final StringBuilder sb = new StringBuilder("?");
        boolean first = true;
        for (Object item : optionsListModel.toArray()) {
            Option option = (Option) item;
            if (first) {
                first = false;
            } else {
                sb.append('&');
            }
            sb.append(urlEncode(option.name)).append('=').append(option.value);
        }
        return sb.toString();
    }

    private String encodeHost(Host host) {
        final StringBuilder sb = new StringBuilder(urlEncode(host.hostname));
        if (host.port != null) {
            sb.append(':').append(host.port);
        }
        return sb.toString();
    }

    private String urlDecode(String string) {
        if (string.isEmpty()) {
            return string;
        }
        try {
            return URLDecoder.decode(string, UTF_8);
        } catch (UnsupportedEncodingException ex) {
            Exceptions.printStackTrace(ex);
            throw new AssertionError();
        }

    }

    private String urlEncode(String string) {
        if (string.isEmpty()) {
            return string;
        }
        try {
            return URLEncoder.encode(string, UTF_8);
        } catch (UnsupportedEncodingException ex) {
            Exceptions.printStackTrace(ex);
            throw new AssertionError();
        }

    }

    private Host readHostFromUI() {
        final String hostname = hostField.getText();
        if (hostname.isEmpty()) {
            error("no hostname specified");
            hostField.requestFocusInWindow();
            return null;
        }
        final String port = portField.getText();
        return new Host(hostname, port.isEmpty() ? null : Integer.parseInt(port));
    }

    private List<Option> decodeOptions(MongoClientURI uri) {
        final List<Option> options = new ArrayList<>();
        final String uriString = uri.getURI();
        final int index = uriString.indexOf('?');
        if (index != -1) {
            final String optionsPart = uriString.substring(index + 1);
            for (String option : optionsPart.split("&|;")) {
                int idx = option.indexOf("=");
                if (idx >= 0) {
                    final String key = option.substring(0, idx);
                    final String value = option.substring(idx + 1);
                    options.add(new Option(key, value));
                }
            }
        }
        return options;
    }

    private Option readOptionFromUI() {
        final String optionName = optionNameField.getText();
        // TODO: add option name validation (supported option)
        if (optionName.isEmpty()) {
            error("no option name specified");
            optionNameField.requestFocusInWindow();
            return null;
        }
        final String optionValue = optionValueField.getText();
        // TODO: add option value validation
        if (optionValue.isEmpty()) {
            error("no option value specified");
            optionValueField.requestFocusInWindow();
            return null;
        }
        return new Option(optionName, optionValue);
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
        System.out.println("error: " + message);
        if (notificationLineSupport != null) {
            System.out.println("nls: " + message);
            notificationLineSupport.setErrorMessage(message);
        }
    }

    private void warn(String message) {
        if (notificationLineSupport != null) {
            notificationLineSupport.setWarningMessage(message);
        }
    }

    public void addChangeListener(ChangeListener listener) {
        changeSupport.addChangeListener(listener);
    }

    public void removeChangeListener(ChangeListener listener) {
        changeSupport.removeChangeListener(listener);
    }

    private void focusFullSelectionOnTextComponent(JTextComponent component) {
        component.setSelectionStart(0);
        component.setSelectionEnd(component.getText().length());
        component.requestFocusInWindow();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        hostsPanel = new javax.swing.JPanel();
        hostField = new javax.swing.JTextField();
        hostLabel = new javax.swing.JLabel();
        addHostButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        hostsList = new javax.swing.JList<Host>();
        portField = new javax.swing.JTextField();
        portLabel = new javax.swing.JLabel();
        credentialsPanel = new javax.swing.JPanel();
        usernameLabel = new javax.swing.JLabel();
        passwordLabel = new javax.swing.JLabel();
        usernameField = new javax.swing.JTextField();
        passwordField = new javax.swing.JPasswordField();
        databasePanel = new javax.swing.JPanel();
        databaseLabel = new javax.swing.JLabel();
        databaseField = new javax.swing.JTextField();
        optionsLabel = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        optionsList = new javax.swing.JList<Option>();
        addOptionButton = new javax.swing.JButton();
        optionNameLabel = new javax.swing.JLabel();
        optionNameField = new javax.swing.JTextField();
        optionValueLabel = new javax.swing.JLabel();
        optionValueField = new javax.swing.JTextField();
        uriLabel = new javax.swing.JLabel();
        uriField = new javax.swing.JTextField();

        hostsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(MongoURIEditorPanel.class, "MongoURIEditorPanel.hostsPanel.border.title"))); // NOI18N

        hostField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                hostFieldKeyPressed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(hostLabel, org.openide.util.NbBundle.getMessage(MongoURIEditorPanel.class, "MongoURIEditorPanel.hostLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(addHostButton, org.openide.util.NbBundle.getMessage(MongoURIEditorPanel.class, "MongoURIEditorPanel.addHostButton.text")); // NOI18N
        addHostButton.setEnabled(false);
        addHostButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addHostButtonActionPerformed(evt);
            }
        });

        hostsList.setModel(hostsListModel);
        hostsList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                hostsListMouseClicked(evt);
            }
        });
        hostsList.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                hostsListKeyPressed(evt);
            }
        });
        jScrollPane1.setViewportView(hostsList);

        portField.setDocument(new PortDocument());
        portField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                portFieldKeyPressed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(portLabel, org.openide.util.NbBundle.getMessage(MongoURIEditorPanel.class, "MongoURIEditorPanel.portLabel.text")); // NOI18N

        javax.swing.GroupLayout hostsPanelLayout = new javax.swing.GroupLayout(hostsPanel);
        hostsPanel.setLayout(hostsPanelLayout);
        hostsPanelLayout.setHorizontalGroup(
            hostsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(hostsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(hostsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, hostsPanelLayout.createSequentialGroup()
                        .addComponent(hostLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(hostField)
                        .addGap(18, 18, 18)
                        .addComponent(portLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(portField, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(addHostButton)))
                .addContainerGap())
        );
        hostsPanelLayout.setVerticalGroup(
            hostsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(hostsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(hostsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(addHostButton)
                    .addComponent(hostLabel)
                    .addComponent(hostField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(portLabel)
                    .addComponent(portField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        credentialsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(MongoURIEditorPanel.class, "MongoURIEditorPanel.credentialsPanel.border.title"))); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(usernameLabel, org.openide.util.NbBundle.getMessage(MongoURIEditorPanel.class, "MongoURIEditorPanel.usernameLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(passwordLabel, org.openide.util.NbBundle.getMessage(MongoURIEditorPanel.class, "MongoURIEditorPanel.passwordLabel.text")); // NOI18N

        javax.swing.GroupLayout credentialsPanelLayout = new javax.swing.GroupLayout(credentialsPanel);
        credentialsPanel.setLayout(credentialsPanelLayout);
        credentialsPanelLayout.setHorizontalGroup(
            credentialsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(credentialsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(credentialsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(credentialsPanelLayout.createSequentialGroup()
                        .addComponent(usernameLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(usernameField, javax.swing.GroupLayout.DEFAULT_SIZE, 375, Short.MAX_VALUE))
                    .addGroup(credentialsPanelLayout.createSequentialGroup()
                        .addComponent(passwordLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(passwordField)))
                .addContainerGap())
        );

        credentialsPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {passwordLabel, usernameLabel});

        credentialsPanelLayout.setVerticalGroup(
            credentialsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(credentialsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(credentialsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(usernameLabel)
                    .addComponent(usernameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(credentialsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(passwordLabel)
                    .addComponent(passwordField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        databasePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(MongoURIEditorPanel.class, "MongoURIEditorPanel.databasePanel.border.title"))); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(databaseLabel, org.openide.util.NbBundle.getMessage(MongoURIEditorPanel.class, "MongoURIEditorPanel.databaseLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(optionsLabel, org.openide.util.NbBundle.getMessage(MongoURIEditorPanel.class, "MongoURIEditorPanel.optionsLabel.text")); // NOI18N

        optionsList.setModel(optionsListModel);
        optionsList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                optionsListMouseClicked(evt);
            }
        });
        optionsList.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                optionsListKeyPressed(evt);
            }
        });
        jScrollPane2.setViewportView(optionsList);

        org.openide.awt.Mnemonics.setLocalizedText(addOptionButton, org.openide.util.NbBundle.getMessage(MongoURIEditorPanel.class, "MongoURIEditorPanel.addOptionButton.text")); // NOI18N
        addOptionButton.setEnabled(false);
        addOptionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addOptionButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(optionNameLabel, org.openide.util.NbBundle.getMessage(MongoURIEditorPanel.class, "MongoURIEditorPanel.optionNameLabel.text")); // NOI18N

        optionNameField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                optionNameFieldKeyPressed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(optionValueLabel, org.openide.util.NbBundle.getMessage(MongoURIEditorPanel.class, "MongoURIEditorPanel.optionValueLabel.text")); // NOI18N

        optionValueField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                optionValueFieldKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout databasePanelLayout = new javax.swing.GroupLayout(databasePanel);
        databasePanel.setLayout(databasePanelLayout);
        databasePanelLayout.setHorizontalGroup(
            databasePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(databasePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(databasePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2)
                    .addGroup(databasePanelLayout.createSequentialGroup()
                        .addComponent(databaseLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(databaseField))
                    .addGroup(databasePanelLayout.createSequentialGroup()
                        .addComponent(optionsLabel)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, databasePanelLayout.createSequentialGroup()
                        .addComponent(optionNameLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(optionNameField, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(optionValueLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(optionValueField)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(addOptionButton)))
                .addContainerGap())
        );
        databasePanelLayout.setVerticalGroup(
            databasePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(databasePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(databasePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(databaseLabel)
                    .addComponent(databaseField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(optionsLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(databasePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(databasePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(optionNameLabel)
                        .addComponent(optionNameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(optionValueLabel)
                        .addComponent(optionValueField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(addOptionButton))
                .addContainerGap())
        );

        org.openide.awt.Mnemonics.setLocalizedText(uriLabel, org.openide.util.NbBundle.getMessage(MongoURIEditorPanel.class, "MongoURIEditorPanel.uriLabel.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(hostsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(credentialsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(databasePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(uriLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(uriField)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(hostsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(credentialsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(databasePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(uriLabel)
                    .addComponent(uriField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void hostsListKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_hostsListKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_DELETE) {
            final int index = hostsList.getSelectedIndex();
            if (index != -1) {
                hostsListModel.remove(index);
                updateURIField();
            }
        }
    }//GEN-LAST:event_hostsListKeyPressed

    private void addHostButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addHostButtonActionPerformed
        final Host host = readHostFromUI();
        if (host != null) {
            if (hostsListModel.contains(host)) {
                hostsList.setSelectedValue(host, true);
            } else {
                hostsListModel.addElement(host);
                updateURIField();
                hostField.setText("");
                portField.setText("");
                hostField.requestFocusInWindow();
            }
        }
    }//GEN-LAST:event_addHostButtonActionPerformed

    private void addOptionButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addOptionButtonActionPerformed
        final Option option = readOptionFromUI();
        if (option != null) {
            if (optionsListModel.contains(option)) {
                optionsList.setSelectedValue(option, true);
            } else {
                optionsListModel.addElement(option);
                updateURIField();
                optionNameField.setText("");
                optionValueField.setText("");
                optionNameField.requestFocusInWindow();
            }
        }
    }//GEN-LAST:event_addOptionButtonActionPerformed

    private void optionsListKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_optionsListKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_DELETE) {
            final int index = optionsList.getSelectedIndex();
            if (index != -1) {
                optionsListModel.remove(index);
                updateURIField();
            }
        }
    }//GEN-LAST:event_optionsListKeyPressed

    private void optionNameFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_optionNameFieldKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            focusFullSelectionOnTextComponent(optionValueField);
            evt.consume();
        }
    }//GEN-LAST:event_optionNameFieldKeyPressed

    private void hostFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_hostFieldKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            focusFullSelectionOnTextComponent(portField);
            evt.consume();
        }
    }//GEN-LAST:event_hostFieldKeyPressed

    private void portFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_portFieldKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            if (addHostButton.isEnabled()) {
                addHostButtonActionPerformed(null);
            }
            evt.consume();
        }
    }//GEN-LAST:event_portFieldKeyPressed

    private void optionValueFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_optionValueFieldKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            if (addOptionButton.isEnabled()) {
                addOptionButtonActionPerformed(null);
            }
            evt.consume();
        }
    }//GEN-LAST:event_optionValueFieldKeyPressed

    private void hostsListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_hostsListMouseClicked
        final int index = hostsList.getSelectedIndex();
        if (evt.getClickCount() == 2 && index != -1) {
            final Host host = hostsListModel.remove(index);
            updateURIField();
            hostField.setText(host.hostname);
            portField.setText(host.port != null ? host.port.toString() : "");
            focusFullSelectionOnTextComponent(hostField);
        }
    }//GEN-LAST:event_hostsListMouseClicked

    private void optionsListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_optionsListMouseClicked
        final int index = optionsList.getSelectedIndex();
        if (evt.getClickCount() == 2 && index != -1) {
            final Option option = optionsListModel.remove(index);
            updateURIField();
            optionNameField.setText(option.name);
            optionValueField.setText(option.value);
            focusFullSelectionOnTextComponent(optionNameField);
        }
    }//GEN-LAST:event_optionsListMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addHostButton;
    private javax.swing.JButton addOptionButton;
    private javax.swing.JPanel credentialsPanel;
    private javax.swing.JTextField databaseField;
    private javax.swing.JLabel databaseLabel;
    private javax.swing.JPanel databasePanel;
    private javax.swing.JTextField hostField;
    private javax.swing.JLabel hostLabel;
    private javax.swing.JList<Host> hostsList;
    private javax.swing.JPanel hostsPanel;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextField optionNameField;
    private javax.swing.JLabel optionNameLabel;
    private javax.swing.JTextField optionValueField;
    private javax.swing.JLabel optionValueLabel;
    private javax.swing.JLabel optionsLabel;
    private javax.swing.JList<Option> optionsList;
    private javax.swing.JPasswordField passwordField;
    private javax.swing.JLabel passwordLabel;
    private javax.swing.JTextField portField;
    private javax.swing.JLabel portLabel;
    private javax.swing.JTextField uriField;
    private javax.swing.JLabel uriLabel;
    private javax.swing.JTextField usernameField;
    private javax.swing.JLabel usernameLabel;
    // End of variables declaration//GEN-END:variables

    private final class PortDocument extends PlainDocument {

        @Override
        public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
            boolean valid = true;
            for (char c : str.toCharArray()) {
                if (c < '0' || c > '9') {
                    valid = false;
                    break;
                }
            }
            if (valid && str.length() > 0 && getLength() > 0) {
                final StringBuilder sb = new StringBuilder(getText(0, getLength()));
                sb.insert(offs, str);
                final String value = sb.toString();
                if (value.isEmpty() == false) {
                    try {
                        int port = Integer.parseInt(value);
                        valid = port > 0 && port < 65536;
                    } catch (NumberFormatException ex) {
                        // should never happens
                        Exceptions.printStackTrace(ex);
                        valid = false;
                    }
                }
            }
            if (valid) {
                super.insertString(offs, str, a);
            }
        }

    }

    private static final class Host {

        private final String hostname;

        private final Integer port;

        public Host(String hostname, Integer port) {
            this.hostname = hostname;
            this.port = port;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 17 * hash + Objects.hashCode(this.hostname);
            hash = 17 * hash + Objects.hashCode(this.port);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Host other = (Host) obj;
            if (!Objects.equals(this.hostname, other.hostname)) {
                return false;
            }
            return Objects.equals(this.port, other.port);
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder(hostname);
            if (port != null) {
                sb.append(':').append(port);
            }
            return sb.toString();
        }
    }

    private static final class Option {

        private final String name;

        private final String value;

        public Option(String name, Object value) {
            this(name, String.valueOf(value));
        }

        public Option(String name, String value) {
            this.name = name;
            this.value = value;
        }

        @Override
        public String toString() {
            return new StringBuilder(name).append('=').append(value).toString();
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 83 * hash + Objects.hashCode(this.name);
            hash = 83 * hash + Objects.hashCode(this.value);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Option other = (Option) obj;
            if (!Objects.equals(this.name, other.name)) {
                return false;
            }
            return Objects.equals(this.value, other.value);
        }

    }

    private static abstract class AbstractDocumentListener implements DocumentListener {

        protected abstract void onChange();

        @Override
        public void insertUpdate(DocumentEvent e) {
            onChange();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            onChange();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            onChange();
        }

    }

}
