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

import com.mongodb.DB;
import org.openide.util.Lookup;

/**
 *
 * @author Yann D'Isanto
 */
public final class CollectionNameValidator implements ValidatingInputLine.InputValidator {

    private static final String[] forbiddenCharacters = {
        "$", "\u0000"
    };

    private static final String SYSTEM_PREFIX = "system.";

    private final Lookup lookup;

    public CollectionNameValidator(Lookup lookup) {
        this.lookup = lookup;
    }

    @Override
    public void validate(String inputText) throws IllegalArgumentException {
        final String value = inputText.trim();
        if (value.isEmpty()) {
            throw new IllegalArgumentException(
                Bundle.VALIDATION_empty());
        }
        if (value.startsWith(SYSTEM_PREFIX)) {
            throw new IllegalArgumentException(
                Bundle.VALIDATION_invalid_prefix(SYSTEM_PREFIX));
        }
        for (String character : forbiddenCharacters) {
            if (value.contains(character)) {
                throw new IllegalArgumentException(
                    Bundle.VALIDATION_forbidden_character(character));
            }
        }
        if (lookup.lookup(DB.class).getCollectionNames().contains(value)) {
            throw new IllegalArgumentException(
                Bundle.VALIDATION_exists("collection", value));
        }
    }
}
