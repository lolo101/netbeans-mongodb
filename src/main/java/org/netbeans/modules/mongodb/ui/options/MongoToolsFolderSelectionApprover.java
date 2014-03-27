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
package org.netbeans.modules.mongodb.ui.options;

import java.io.File;
import org.netbeans.modules.mongodb.options.MongoNativeToolsFolderPredicate;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileChooserBuilder;

/**
 *
 * @author Yann D'Isanto
 */
public final class MongoToolsFolderSelectionApprover implements FileChooserBuilder.SelectionApprover {

    @Override
    public boolean approve(File[] selection) {
        if (selection.length == 0) {
            return false;
        }
        final File selectedFolder = selection[0];
        if (selectedFolder.isDirectory()) {
            if (new MongoNativeToolsFolderPredicate().eval(selectedFolder.toPath())) {
                return true;
            }
        }
        DialogDisplayer.getDefault().notify(
            new NotifyDescriptor.Message("The selected folder doesn't contain the mongo tools executables",
                NotifyDescriptor.ERROR_MESSAGE));
        return false;
    }
}
