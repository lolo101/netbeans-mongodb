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

import com.mongodb.DB;
import java.io.File;
import java.nio.charset.Charset;
import javax.swing.JFileChooser;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.WizardDescriptor;
import org.openide.WizardValidationException;
import org.openide.util.ChangeSupport;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle.Messages;

@Messages({
    "validation_no_collection_specified=no collection specified"})
public class ImportWizardPanel1 implements WizardDescriptor.ValidatingPanel<WizardDescriptor>, ChangeListener {

    private static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

    private final ChangeSupport changeSupport = new ChangeSupport(this);

    private ImportVisualPanel1 component;

    private final DB db;

    public ImportWizardPanel1(DB db) {
        this.db = db;
    }
    
    
    @Override
    public ImportVisualPanel1 getComponent() {
        if (component == null) {
            component = new ImportVisualPanel1(db);
            component.addChangeListener(this);
        }
        return component;
    }

    @Override
    public HelpCtx getHelp() {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    public void validate() throws WizardValidationException {
        final ImportVisualPanel1 panel = getComponent();
        if(panel.getFileChooser().getSelectedFile() == null) {
            throw new WizardValidationException(null, Bundle.validation_file_missing(), null);
        }
        if(panel.getCollectionEditor().getText().trim().isEmpty()) {
            throw new WizardValidationException(null, Bundle.validation_no_collection_specified(), null);
        }
    }

    @Override
    public boolean isValid() {
        final ImportVisualPanel1 panel = getComponent();
        return panel.getFileChooser().getSelectedFile() != null 
            && panel.getCollectionEditor().getText().trim().isEmpty() == false;
    }

    @Override
    public void readSettings(WizardDescriptor wiz) {
        final ImportVisualPanel1 panel = getComponent();
//        panel.setWizard(wiz);
        final JFileChooser fileChooser = panel.getFileChooser();
        final File file = (File) wiz.getProperty(ImportWizardAction.PROP_FILE);
        fileChooser.setSelectedFile(file);
        panel.getFileField().setText(file != null ? file.getAbsolutePath() : "");
        final Charset charset = (Charset) wiz.getProperty(ImportWizardAction.PROP_ENCODING);
        panel.getEncodingComboBox().setSelectedItem(charset != null ? charset : DEFAULT_CHARSET);
        
        String collection = (String) wiz.getProperty(ImportWizardAction.PROP_COLLECTION);
        if(collection == null) {
            collection = "";
        }
        if(collection.isEmpty() && file != null) {
            collection = file.getName().replaceAll("\\.json$", "");
        }
        if(collection != null) {
            panel.getCollectionEditor().setText(collection);
        }
        final Boolean drop = (Boolean) wiz.getProperty(ImportWizardAction.PROP_DROP);
        panel.getDropCheckBox().setSelected(drop != null ? drop : false);
    }

    @Override
    public void storeSettings(WizardDescriptor wiz) {
        final ImportVisualPanel1 panel = getComponent();
        wiz.putProperty(ImportWizardAction.PROP_FILE, 
            panel.getFileChooser().getSelectedFile());
        wiz.putProperty(ImportWizardAction.PROP_ENCODING, 
            panel.getEncodingComboBox().getSelectedItem());
        wiz.putProperty(ImportWizardAction.PROP_COLLECTION, 
            panel.getCollectionEditor().getText().trim());
        wiz.putProperty(ImportWizardAction.PROP_DROP, 
            panel.getDropCheckBox().isSelected());
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
