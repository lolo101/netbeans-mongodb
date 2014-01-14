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
import com.mongodb.DBObject;
import javax.swing.JComboBox;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.modules.nbmongo.ui.QueryEditor;
import org.openide.WizardDescriptor;
import org.openide.WizardValidationException;
import org.openide.util.ChangeSupport;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle.Messages;

@Messages({
    "validation_no_collection_selected=no collection selected"})
public class ExportWizardPanel1 implements WizardDescriptor.ValidatingPanel<WizardDescriptor>, ChangeListener {

    private ExportVisualPanel1 component;

    private final ChangeSupport changeSupport = new ChangeSupport(this);

    private final DB db;

    public ExportWizardPanel1(DB db) {
        this.db = db;
    }

    @Override
    public ExportVisualPanel1 getComponent() {
        if (component == null) {
            component = new ExportVisualPanel1(db);
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
        final JComboBox collectionField = component.getCollectionComboBox();
        if (collectionField.getSelectedIndex() < 0) {
            throw new WizardValidationException(collectionField, Bundle.validation_no_collection_selected(), null);
        }
    }

    @Override
    public boolean isValid() {
        final JComboBox collectionField = component.getCollectionComboBox();
        if (collectionField.getSelectedIndex() < 0) {
            return false;
        }
        return true;
    }

    @Override
    public void readSettings(WizardDescriptor wiz) {
        final ExportVisualPanel1 panel = getComponent();
        final QueryEditor query = panel.getQueryEditor();
        String collection = (String) wiz.getProperty(ExportWizardAction.PROP_COLLECTION);
        if (collection != null) {
            panel.getCollectionComboBox().setSelectedItem(collection);
        }
        query.setCriteria((DBObject) wiz.getProperty(ExportWizardAction.PROP_CRITERIA));
        query.setProjection((DBObject) wiz.getProperty(ExportWizardAction.PROP_PROJECTION));
        query.setSort((DBObject) wiz.getProperty(ExportWizardAction.PROP_SORT));
        panel.updateQueryFieldsFromEditor();
    }

    @Override
    public void storeSettings(WizardDescriptor wiz) {
        final ExportVisualPanel1 panel = getComponent();
        final QueryEditor query = panel.getQueryEditor();
        wiz.putProperty(ExportWizardAction.PROP_COLLECTION, 
            panel.getCollectionComboBox().getSelectedItem());
        wiz.putProperty(ExportWizardAction.PROP_CRITERIA, 
            panel.getQueryEditor().getCriteria());
        wiz.putProperty(ExportWizardAction.PROP_PROJECTION, 
            panel.getQueryEditor().getProjection());
        wiz.putProperty(ExportWizardAction.PROP_SORT, 
            panel.getQueryEditor().getSort());
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
