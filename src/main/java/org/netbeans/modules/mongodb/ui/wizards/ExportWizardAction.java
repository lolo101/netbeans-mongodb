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
package org.netbeans.modules.mongodb.ui.wizards;

import com.mongodb.DB;
import com.mongodb.DBObject;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.File;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import org.netbeans.modules.mongodb.util.ExportProperties;
import org.netbeans.modules.mongodb.util.ExportPropertiesBuilder;
import org.netbeans.modules.mongodb.util.ExportTask;
import org.netbeans.modules.mongodb.util.Exporter;
import org.openide.DialogDisplayer;
import org.openide.WizardDescriptor;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;

@Messages({"ACTION_Export=Export"})
public final class ExportWizardAction extends AbstractAction {

    public static final String PROP_COLLECTION = "collection";

    public static final String PROP_CRITERIA = "criteria";

    public static final String PROP_PROJECTION = "projection";

    public static final String PROP_SORT = "sort";

    public static final String PROP_FILE = "file";

    public static final String PROP_ENCODING = "encoding";

    public static final String PROP_JSON_ARRAY = "jsonArray";

    private final Lookup lookup;

    private final Map<String, Object> defaultProperties;

    public ExportWizardAction(Lookup lookup) {
        this(lookup, new HashMap<String, Object>());
    }

    public ExportWizardAction(Lookup lookup, Map<String, Object> defaultProperties) {
        super(Bundle.ACTION_Export());
        this.lookup = lookup;
        this.defaultProperties = defaultProperties;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        List<WizardDescriptor.Panel<WizardDescriptor>> panels = new ArrayList<>();
        panels.add(new ExportWizardPanel1(lookup.lookup(DB.class)));
        panels.add(new ExportWizardPanel2());
        String[] steps = new String[panels.size()];
        for (int i = 0; i < panels.size(); i++) {
            Component c = panels.get(i).getComponent();
            // Default step name to component name of panel.
            steps[i] = c.getName();
            if (c instanceof JComponent) { // assume Swing components
                JComponent jc = (JComponent) c;
                jc.putClientProperty(WizardDescriptor.PROP_CONTENT_SELECTED_INDEX, i);
                jc.putClientProperty(WizardDescriptor.PROP_CONTENT_DATA, steps);
                jc.putClientProperty(WizardDescriptor.PROP_AUTO_WIZARD_STYLE, true);
                jc.putClientProperty(WizardDescriptor.PROP_CONTENT_DISPLAYED, true);
                jc.putClientProperty(WizardDescriptor.PROP_CONTENT_NUMBERED, true);
            }
        }
        final WizardDescriptor wiz = new WizardDescriptor(new WizardDescriptor.ArrayIterator<>(panels));
        for (Map.Entry<String, Object> entry : defaultProperties.entrySet()) {
            wiz.putProperty(entry.getKey(), entry.getValue());
        }

        // {0} will be replaced by WizardDesriptor.Panel.getComponent().getName()
        wiz.setTitleFormat(new MessageFormat("{0}"));
        wiz.setTitle(Bundle.ACTION_Export());
        if (DialogDisplayer.getDefault().notify(wiz) == WizardDescriptor.FINISH_OPTION) {
            final ExportProperties properties = new ExportPropertiesBuilder()
                .collection((String) wiz.getProperty(PROP_COLLECTION))
                .criteria((DBObject) wiz.getProperty(PROP_CRITERIA))
                .projection((DBObject) wiz.getProperty(PROP_PROJECTION))
                .sort((DBObject) wiz.getProperty(PROP_SORT))
                .jsonArray((Boolean) wiz.getProperty(PROP_JSON_ARRAY))
                .file((File) wiz.getProperty(PROP_FILE))
                .encoding((Charset) wiz.getProperty(PROP_ENCODING))
                .build();
            new ExportTask(
                new Exporter(lookup.lookup(DB.class), properties))
                .run();
        }
    }

}
