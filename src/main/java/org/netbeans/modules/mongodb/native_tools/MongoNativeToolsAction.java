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
package org.netbeans.modules.mongodb.native_tools;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import org.netbeans.modules.mongodb.options.MongoNativeToolsOptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;
import org.openide.util.actions.Presenter;

/**
 *
 * @author Yann D'Isanto
 */
@Messages({
    "ACTION_MongoNativeTools=Native Tools",
    "TOOLTIP_configureOptions=Configure native tools path in options"
})
public final class MongoNativeToolsAction extends AbstractAction implements Presenter.Popup {

    private final Lookup lookup;

    public MongoNativeToolsAction(Lookup lookup) {
        super(Bundle.ACTION_MongoNativeTools());
        this.lookup = lookup;
        if(isEnabled() == false) {
            putValue(SHORT_DESCRIPTION, Bundle.TOOLTIP_configureOptions());
        }
    }

    @Override
    public JMenuItem getPopupPresenter() {
        final JMenu menu = new JMenu(this);
        menu.add(new JMenuItem(new MongoShellExecAction(lookup)));
        menu.add(new JMenuItem(new MongoDumpExecAction(lookup)));
        menu.add(new JMenuItem(new MongoRestoreExecAction(lookup)));
        return menu;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // do nothing, only container for native tools action menu items
    }

    @Override
    public boolean isEnabled() {
        return MongoNativeToolsOptions.INSTANCE.isToolsFolderConfigured();
    }

}
