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

import java.util.Map;
import java.util.prefs.Preferences;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.netbeans.modules.mongodb.native_tools.MongoNativeTool;
import org.netbeans.modules.mongodb.ui.native_tools.NativeToolOptionsDialog.OptionsAndArgsPanel;
import org.openide.util.NbPreferences;

/**
 *
 * @author Yann D'Isanto
 */
public abstract class AbstractOptionsAndArgsPanel extends JPanel implements OptionsAndArgsPanel {

    private final MongoNativeTool nativeTool;

    public AbstractOptionsAndArgsPanel(MongoNativeTool nativeTool) {
        this.nativeTool = nativeTool;
    }

    @Override
    public final MongoNativeTool getNativeTool() {
        return nativeTool;
    }

    @Override
    public final JPanel getPanel() {
        return this;
    }

    protected final Preferences prefs() {
        return NbPreferences.forModule(MongoDumpOptionsPanel.class).node("native_tools");
    }

    protected final void readOptionFromUI(Map<String, String> options, String optionKey, JTextField textField) {
        final String value = textField.getText().trim();
        if (value.isEmpty() == false) {
            options.put(optionKey, value);
        }
    }

    protected final void readOptionFromUI(Map<String, String> options, String optionKey, JCheckBox checkbox) {
        if (checkbox.isSelected()) {
            options.put(optionKey, "");
        }
    }

    protected final void populateUIWithOption(Map<String, String> options, String optionKey, JTextField textField, String defaultValue) {
        final String optionValue = options.get(optionKey);
        textField.setText(optionValue != null ? optionValue : defaultValue);
    }

    protected final void populateUIWithOption(Map<String, String> options, String optionKey, JTextField textField) {
        AbstractOptionsAndArgsPanel.this.populateUIWithOption(options, optionKey, textField, "");
    }

    protected final void populateUIWithOption(Map<String, String> options, String optionKey, JCheckBox checkbox) {
        checkbox.setSelected(options.containsKey(optionKey));
    }
}
