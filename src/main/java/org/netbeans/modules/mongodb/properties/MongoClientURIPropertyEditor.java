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
package org.netbeans.modules.mongodb.properties;

import com.mongodb.MongoClientURI;
import java.awt.Component;
import java.beans.PropertyEditorSupport;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.modules.mongodb.ui.components.MongoURIEditorPanel;
import org.openide.explorer.propertysheet.ExPropertyEditor;
import org.openide.explorer.propertysheet.PropertyEnv;

/**
 *
 * @author Yann D'Isanto
 */
public final class MongoClientURIPropertyEditor extends PropertyEditorSupport implements ExPropertyEditor {

    private PropertyEnv env;

    @Override
    public String getAsText() {
        final MongoClientURI uri = (MongoClientURI) getValue();
        return uri.getURI();
    }

    @Override
    public void setAsText(String uri) throws IllegalArgumentException {
        setValue(new MongoClientURI(uri));
    }

    @Override
    public Component getCustomEditor() {
        final MongoURIEditorPanel editor = new MongoURIEditorPanel((MongoClientURI) getValue());
        editor.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                if (editor.valid()) {
                    setEnvState(PropertyEnv.STATE_VALID);
                    setValue(editor.getMongoURI());
                } else {
                    setEnvState(PropertyEnv.STATE_INVALID);
                }
            }
        });
        return editor;
    }

    @Override
    public boolean supportsCustomEditor() {
        return true;
    }

    @Override
    public void attachEnv(PropertyEnv env) {
        this.env = env;
    }

    private void setEnvState(Object state) {
        if(env != null) {
            env.setState(state);
        }
    }

}
