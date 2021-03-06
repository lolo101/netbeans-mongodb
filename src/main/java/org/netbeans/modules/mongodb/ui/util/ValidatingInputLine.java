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
import org.openide.util.NbBundle.Messages;

/**
 *
 * @author Yann D'Isanto
 */
@Messages({
    "VALIDATION_empty=can't be empty",
    "# {0} - type",
    "# {1} - name",
    "VALIDATION_exists={0} \"{1}\" already exists",
    "# {0} - prefix",
    "VALIDATION_invalid_prefix=can't start with \"{0}\"",
    "# {0} - forbidden character",
    "VALIDATION_forbidden_character=can't contains \'{0}\'",
    "VALIDATION_invalid_character=invalid character",
    "# {0} - max length",
    "VALIDATION_maxLength=max length is {0} characters"})
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
