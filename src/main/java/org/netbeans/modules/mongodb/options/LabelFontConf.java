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

/**
 *
 * @author Yann D'Isanto
 */
public final class LabelFontConf implements Cloneable {

    private final Font font;

    private final Color foreground;

    private final Color background;

    public static final class Builder {

        private Font font;

        private Color foreground;

        private Color background;

        public Builder() {
        }

        public Builder(LabelFontConf defaultConf) {
            this.font = defaultConf.font;
            this.foreground = defaultConf.foreground;
            this.background = defaultConf.background;
        }

        public Builder font(Font font) {
            this.font = font;
            return this;
        }

        public Builder foreground(Color foreground) {
            this.foreground = foreground;
            return this;
        }

        public Builder background(Color background) {
            this.background = background;
            return this;
        }

        public Font getFont() {
            return font;
        }

        public Color getForeground() {
            return foreground;
        }

        public Color getBackground() {
            return background;
        }

        public LabelFontConf build() {
            return new LabelFontConf(this);
        }
    }

    public LabelFontConf(Font font, Color foreground, Color background) {
        this.font = font;
        this.foreground = foreground;
        this.background = background;
    }

    private LabelFontConf(Builder builder) {
        this.font = builder.font;
        this.foreground = builder.foreground;
        this.background = builder.background;
    }

    public Font getFont() {
        return font;
    }

    public Color getForeground() {
        return foreground;
    }

    public Color getBackground() {
        return background;
    }

    @Override
    public LabelFontConf clone() {
        try {
            return (LabelFontConf) super.clone();
        } catch (CloneNotSupportedException ex) {
            throw new AssertionError();
        }
    }
}
