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

import java.awt.datatransfer.StringSelection;
import javax.swing.Icon;

/**
 * A copy to cliboard action for a given object.
 *
 * @author Yann D'Isanto
 */
public abstract class CopyObjectToClipboardAction<T> extends AbstractCopyToClipboardAction {

    /**
     * The object to copy to the clipboard.
     */
    private final T object;

    /**
     * Creates a new instance.
     *
     * @param name the action name.
     * @param object the object to copy to the clipboard.
     */
    public CopyObjectToClipboardAction(String name, T object) {
        super(name);
        this.object = object;
    }

    /**
     * Creates a new instance.
     *
     * @param name the action name.
     * @param icon the action icon
     * @param object the object to copy to the clipboard.
     */
    public CopyObjectToClipboardAction(String name, Icon icon, T object) {
        super(name, icon);
        this.object = object;
    }

    @Override
    public final StringSelection getStringSelection() {
        return convertToStringSelection(object);
    }

    /**
     * Converts the specified object into a string to be copied in the
     * clipboard. The default implementation rely on @{code String.valueOf()}
     * method. Overides this method to provide a different implementation.
     *
     * @param object the object to convert in String selection.
     * @return a String selection instance.
     */
    public StringSelection convertToStringSelection(T object) {
        return new StringSelection(String.valueOf(object));
    }
}
