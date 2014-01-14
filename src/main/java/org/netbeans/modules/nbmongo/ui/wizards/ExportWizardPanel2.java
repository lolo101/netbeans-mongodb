/*
 * The MIT License
 *
 * Copyright 2014 PVVQ7166.
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
package org.netbeans.modules.nbmongo.ui.wizards;

import java.io.File;
import java.nio.charset.Charset;
import javax.swing.JFileChooser;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.WizardDescriptor;
import org.openide.WizardValidationException;
import org.openide.util.ChangeSupport;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle.Messages;

@Messages({
    "validation_file_missing=specify a file for export",
    "overwrite_file_confirmation_title=Overwrite confirmation",
    "# {0} - file name",
    "overwrite_file_confirmation=Overwrite \"{0}\" file?"})
public class ExportWizardPanel2 implements WizardDescriptor.ValidatingPanel<WizardDescriptor>, ChangeListener {

    private static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

    private ExportVisualPanel2 component;

    private final ChangeSupport changeSupport = new ChangeSupport(this);

    @Override
    public ExportVisualPanel2 getComponent() {
        if (component == null) {
            component = new ExportVisualPanel2();
        }
        return component;
    }

    @Override
    public HelpCtx getHelp() {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    public void validate() throws WizardValidationException {
        final JFileChooser fileChooser = getComponent().getFileChooser();
        final File file = fileChooser.getSelectedFile();
        if (file == null) {
            throw new WizardValidationException(fileChooser, Bundle.validation_file_missing(), null);
        }
        if (file.exists()) {
            final String title = Bundle.overwrite_file_confirmation_title();
            final String message = Bundle.overwrite_file_confirmation(file.getName());
            final NotifyDescriptor.Confirmation confirmation = new NotifyDescriptor.Confirmation(message, title, NotifyDescriptor.YES_NO_OPTION);
            if (DialogDisplayer.getDefault().notify(confirmation) == NotifyDescriptor.NO_OPTION) {
                throw new WizardValidationException(null, null, null);
            }
        }
    }

    @Override
    public boolean isValid() {
        final File file = getComponent().getFileChooser().getSelectedFile();
        return file != null;
    }

    @Override
    public void readSettings(WizardDescriptor wiz) {
        final ExportVisualPanel2 panel = getComponent();
        panel.setWizard(wiz);
        final JFileChooser fileChooser = panel.getFileChooser();
        File file = (File) wiz.getProperty(ExportWizardAction.PROP_FILE);
        if (file == null) {
            final String collection = (String) wiz.getProperty(ExportWizardAction.PROP_COLLECTION);
            if (collection != null) {
                file = new File(fileChooser.getCurrentDirectory(), collection + ".json");
            }
        }
        fileChooser.setSelectedFile(file);
        panel.getFileField().setText(file != null ? file.getAbsolutePath() : "");
        final Charset charset = (Charset) wiz.getProperty(ExportWizardAction.PROP_ENCODING);
        panel.getEncodingComboBox().setSelectedItem(charset != null ? charset : DEFAULT_CHARSET);
        final Boolean jsonArray = (Boolean) wiz.getProperty(ExportWizardAction.PROP_JSON_ARRAY);
        panel.getJsonArrayCheckBox().setSelected(jsonArray != null ? jsonArray : false);
    }

    @Override
    public void storeSettings(WizardDescriptor wiz) {
        final ExportVisualPanel2 panel = getComponent();
        wiz.putProperty(ExportWizardAction.PROP_FILE, 
            panel.getFileChooser().getSelectedFile());
        wiz.putProperty(ExportWizardAction.PROP_ENCODING, 
            panel.getEncodingComboBox().getSelectedItem());
        wiz.putProperty(ExportWizardAction.PROP_JSON_ARRAY, 
            panel.getJsonArrayCheckBox().isSelected());
    }

    @Override
    public void addChangeListener(ChangeListener l) {
        changeSupport.addChangeListener(l);
    }

    @Override
    public void removeChangeListener(ChangeListener l) {
        changeSupport.removeChangeListener(l);
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        changeSupport.fireChange();
    }

}
