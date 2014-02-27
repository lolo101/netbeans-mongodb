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
package org.netbeans.modules.mongodb.options;

import java.awt.Color;
import java.awt.Font;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.prefs.Preferences;
import javax.swing.JLabel;
import javax.swing.UIManager;
import org.openide.util.NbPreferences;

/**
 *
 * @author Yann D'Isanto
 */
public enum JsonTreeCellRendererOptions {

    INSTANCE;
    
    private final Map<LabelCategory, LabelFontConf> labelConfs = new HashMap<>();

    private JsonTreeCellRendererOptions() {
        load();
    }

    private Preferences getPreferences() {
        return NbPreferences.forModule(JsonTreeCellRendererOptions.class);
    }

    public LabelFontConf getLabelFontConf(LabelCategory category) {
        final LabelFontConf labelFontConf = labelConfs.get(category);
        return labelFontConf != null ? labelFontConf : Default.LABEL_CONFS.get(category);
    }

    public void setLabelFontConf(LabelCategory category, LabelFontConf labelFontConf) {
        labelConfs.put(category, labelFontConf);
    }

    public void load() {
        final Preferences prefs = getPreferences();
        final PropertyEditor fontEditor = PropertyEditorManager.findEditor(Font.class);
        final StringBuilder sb = new StringBuilder();
        for (LabelCategory category : LabelCategory.values()) {
            final LabelFontConf labelFontConf = getLabelFontConf(category);
            sb.setLength(0);
            sb.append("CATEGORY_").append(category.name());
            final String categoryKey = sb.toString();
            sb.append(".font");
            fontEditor.setValue(labelFontConf.getFont());
            final String fontStr = prefs.get(sb.toString(), fontEditor.getAsText());
            fontEditor.setAsText(fontStr);
            final Font font = (Font) fontEditor.getValue();
            sb.setLength(categoryKey.length());
            sb.append(".foreground");
            final int foregroundRGB = prefs.getInt(sb.toString(), labelFontConf.getForeground().getRGB());
            final Color foreground = new Color(foregroundRGB);
            sb.setLength(categoryKey.length());
            sb.append(".background");
            final int backgroundRGB = prefs.getInt(sb.toString(), labelFontConf.getBackground().getRGB());
            final Color background = new Color(backgroundRGB);
            labelConfs.put(category, new LabelFontConf(font, foreground, background));
        }
    }

    public void store() {
        final Preferences prefs = getPreferences();
        final PropertyEditor fontEditor = PropertyEditorManager.findEditor(Font.class);
        final StringBuilder sb = new StringBuilder();
        for (LabelCategory category : LabelCategory.values()) {
            final LabelFontConf labelFontConf = getLabelFontConf(category);
            sb.setLength(0);
            sb.append("CATEGORY_").append(category.name());
            final String categoryKey = sb.toString();
            sb.append(".font");
            fontEditor.setValue(labelFontConf.getFont());
            prefs.put(sb.toString(), fontEditor.getAsText());
            sb.setLength(categoryKey.length());
            sb.append(".foreground");
            prefs.putInt(sb.toString(), labelFontConf.getForeground().getRGB());
            sb.setLength(categoryKey.length());
            sb.append(".background");
            prefs.putInt(sb.toString(), labelFontConf.getBackground().getRGB());
        }
    }

    public static final class Default {

        private static final Map<LabelCategory, LabelFontConf> LABEL_CONFS;

        static {
            final Font font = new JLabel().getFont();
            final Color textForeground = UIManager.getColor("Tree.textForeground");
            final Color textBackground = UIManager.getColor("Tree.textBackground");
            final Color brown = new Color(0xCC3300);
            final Color purple = new Color(0x990099);
            final Map<LabelCategory, LabelFontConf> map = new HashMap<>();
            map.put(LabelCategory.KEY, new LabelFontConf(
                font.deriveFont(Font.BOLD),
                textForeground,
                textBackground));
            map.put(LabelCategory.ID, new LabelFontConf(
                font,
                Color.LIGHT_GRAY,
                textBackground));
            map.put(LabelCategory.STRING_VALUE, new LabelFontConf(
                font,
                Color.BLUE,
                textBackground));
            map.put(LabelCategory.INT_VALUE, new LabelFontConf(
                font,
                Color.RED,
                textBackground));
            map.put(LabelCategory.DECIMAL_VALUE, new LabelFontConf(
                font,
                brown,
                textBackground));
            map.put(LabelCategory.BOOLEAN_VALUE, new LabelFontConf(
                font,
                purple,
                textBackground));
            LABEL_CONFS = Collections.unmodifiableMap(map);
        }
        
        public static Map<LabelCategory, LabelFontConf> labelConfs() {
            return LABEL_CONFS;
        }

    }
}
