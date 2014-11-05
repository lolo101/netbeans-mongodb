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
package org.netbeans.modules.mongodb.ui.util;

import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import com.mongodb.util.JSONParseException;
import java.awt.Dimension;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.text.EditorKit;
import org.netbeans.api.editor.mimelookup.MimeLookup;
import org.netbeans.modules.mongodb.util.Json;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.NbBundle.Messages;

/**
 *
 * @author Yann D'Isanto
 */
@Messages({
    "invalidJson=invalid json"
})
public final class JsonUI {

    /**
     * Displays a modal dialog to input json.
     *
     * @param title the dialog title
     * @param defaultJson the default json
     * @return a DBObject representing the input json or null if the dialog has
     * been cancelled.
     */
    public static DBObject showEditor(String title, String defaultJson) {
        final JEditorPane editor = new JEditorPane();
        final EditorKit jsonEditorKit = MimeLookup.getLookup("text/x-json").lookup(EditorKit.class);
        if (jsonEditorKit != null) {
            editor.setEditorKit(jsonEditorKit);
        }
		final JScrollPane pane = new JScrollPane(editor);
        pane.setPreferredSize(new Dimension(450, 300));
        String json = defaultJson.trim().isEmpty() ? "{}" : Json.prettify(defaultJson);
        boolean doLoop = true;
        while (doLoop) {
            doLoop = false;
            editor.setText(json);
            final DialogDescriptor desc = new DialogDescriptor(pane, title);
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

    private JsonUI() {
    }
}
