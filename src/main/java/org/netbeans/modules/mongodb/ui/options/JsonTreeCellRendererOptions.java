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

import java.awt.Color;
import java.awt.Font;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;
import javax.swing.JLabel;
import javax.swing.UIManager;

/**
 *
 * @author Yann D'Isanto
 */
public final class JsonTreeCellRendererOptions {

    private static final Map<LabelCategory, LabelFontConf> DEFAULT_LABEL_CONFS = new HashMap<>();

    static {
        final Font font = new JLabel().getFont();
        final Color textForeground = UIManager.getColor("Tree.textForeground");
        final Color textBackground = UIManager.getColor("Tree.textBackground");
        final Color caramel = new Color(0xC68E17);
        final Color purple = new Color(0x4B088A);
        DEFAULT_LABEL_CONFS.put(LabelCategory.KEY, new LabelFontConf(
                font.deriveFont(Font.BOLD),
                textForeground,
                textBackground));
        DEFAULT_LABEL_CONFS.put(LabelCategory.ID, new LabelFontConf(
                font,
                Color.LIGHT_GRAY,
                textBackground));
        DEFAULT_LABEL_CONFS.put(LabelCategory.STRING_VALUE, new LabelFontConf(
                font,
                purple,
                textBackground));
        DEFAULT_LABEL_CONFS.put(LabelCategory.INT_VALUE, new LabelFontConf(
                font,
                caramel,
                textBackground));
        DEFAULT_LABEL_CONFS.put(LabelCategory.DECIMAL_VALUE, new LabelFontConf(
                font,
                caramel,
                textBackground));
        DEFAULT_LABEL_CONFS.put(LabelCategory.BOOLEAN_VALUE, new LabelFontConf(
                font,
                Color.BLUE,
                textBackground));
    }
    
    private final Map<LabelCategory, LabelFontConf> labelConfs = new HashMap<>();

    public JsonTreeCellRendererOptions() {
        load();
    }

    private Preferences getPreferences() {
        return Preferences.userNodeForPackage(JsonTreeCellRendererOptions.class);
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
            fontEditor.setValue(labelFontConf.font);
            final String fontStr = prefs.get(sb.toString(), fontEditor.getAsText());
            fontEditor.setAsText(fontStr);
            final Font font = (Font) fontEditor.getValue();
            sb.setLength(categoryKey.length());
            sb.append(".foreground");
            final int foregroundRGB = prefs.getInt(sb.toString(), labelFontConf.foreground.getRGB());
            final Color foreground = new Color(foregroundRGB);
            sb.setLength(categoryKey.length());
            sb.append(".background");
            final int backgroundRGB = prefs.getInt(sb.toString(), labelFontConf.background.getRGB());
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
            fontEditor.setValue(labelFontConf.font);
            prefs.put(sb.toString(), fontEditor.getAsText());
            sb.setLength(categoryKey.length());
            sb.append(".foreground");
            prefs.putInt(sb.toString(), labelFontConf.foreground.getRGB());
            sb.setLength(categoryKey.length());
            sb.append(".background");
            prefs.putInt(sb.toString(), labelFontConf.background.getRGB());
        }
    }

    public LabelFontConf getLabelFontConf(LabelCategory category) {
        final LabelFontConf labelFontConf = labelConfs.get(category);
        return labelFontConf != null ? labelFontConf : DEFAULT_LABEL_CONFS.get(category);
    }

    public void setLabelFontConf(LabelCategory category, LabelFontConf labelFontConf) {
        labelConfs.put(category, labelFontConf);
    }

    public enum LabelCategory {

        KEY,
        ID,
        STRING_VALUE,
        INT_VALUE,
        DECIMAL_VALUE,
        BOOLEAN_VALUE;

        private static final ResourceBundle RESOURCES = ResourceBundle.getBundle(LabelCategory.class.getName());

        @Override
        public String toString() {
            return RESOURCES.getString(name());
        }

    }

    public static final class LabelFontConf {

        private Font font;

        private Color foreground;

        private Color background;

        public LabelFontConf(Font font, Color foreground, Color background) {
            this.font = font;
            this.foreground = foreground;
            this.background = background;
        }

        public Font getFont() {
            return font;
        }

        public void setFont(Font font) {
            this.font = font;
        }

        public Color getForeground() {
            return foreground;
        }

        public void setForeground(Color foreground) {
            this.foreground = foreground;
        }

        public Color getBackground() {
            return background;
        }

        public void setBackground(Color background) {
            this.background = background;
        }

    }
}
