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
package org.netbeans.modules.nbmongo.ui;

import java.io.File;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.NotifyDescriptor.Confirmation;
import org.openide.filesystems.FileChooserBuilder.SelectionApprover;
import org.openide.util.NbBundle;

/**
 *
 * @author Yann D'Isanto
 */
@NbBundle.Messages({
    "overwrite_file_confirmation_title=Overwrite confirmation",
    "# {0} - file name",
    "overwrite-file-confirmation=Overwrite \"{0}\" file?"})
public class FileChooserConfirmOverrideSelectionApprover implements SelectionApprover {

    @Override
    public boolean approve(File[] selection) {
        for (File file : selection) {
            final String title = Bundle.overwrite_file_confirmation_title();
            final String message = Bundle.overwrite_file_confirmation(file.getName());
            final Confirmation confirmation = new Confirmation(message, title, NotifyDescriptor.YES_NO_OPTION);
            if (DialogDisplayer.getDefault().notify(confirmation) == NotifyDescriptor.NO_OPTION) {
                return false;
            }
        }
        return true;
    }

}
