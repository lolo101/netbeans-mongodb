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
package org.netbeans.modules.mongodb.ui.util;

import java.awt.Component;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.openide.NotifyDescriptor.InputLine;

/**
 *
 * @author Yann D'Isanto
 */
public final class ValidatingInputLine extends InputLine {

    private final InputValidator validator;

    public ValidatingInputLine(String text, String title, InputValidator validator) {
        super(text, title);
        this.validator = validator;
        createNotificationLineSupport();
        performValidation();
    }

    private void performValidation() {
        boolean valid = true;
        try {
            validator.validate(getInputText());
            getNotificationLineSupport().setErrorMessage("");
        } catch (IllegalArgumentException ex) {
            valid = false;
            getNotificationLineSupport().setErrorMessage(ex.getLocalizedMessage());
        }
        setValid(valid);
    }

    @Override
    protected Component createDesign(String text) {
        final Component design = super.createDesign(text);
        textField.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                performValidation();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                performValidation();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                performValidation();
            }
        });
        return design;
    }

    public static interface InputValidator {

        void validate(String inputText) throws IllegalArgumentException;
    }
}
