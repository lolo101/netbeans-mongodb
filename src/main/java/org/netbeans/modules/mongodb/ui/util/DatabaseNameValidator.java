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

import com.mongodb.MongoClient;
import org.openide.util.Lookup;

/**
 *
 * @author Yann D'Isanto
 */
//@Messages({
//    "VALIDATION_empty=can't be empty",
//    "# {0} - type",
//    "# {1} - name",
//    "VALIDATION_exists={0} \"{1}\" already exists",
//    "# {0} - prefix",
//    "VALIDATION_invalid_prefix=can't start with \"{0}\"",
//    "# {0} - forbidden character",
//    "VALIDATION_forbidden_character=can't contains \'{0}\'",
//    "VALIDATION_invalid_character=invalid character"})
public final class DatabaseNameValidator implements ValidatingInputLine.InputValidator {

    private final Lookup lookup;

    public DatabaseNameValidator(Lookup lookup) {
        this.lookup = lookup;
    }

    @Override
    public void validate(String inputText) throws IllegalArgumentException {
        final String value = inputText.trim();
        if (value.isEmpty()) {
            throw new IllegalArgumentException(
                Bundle.VALIDATION_empty());
        }
//        if (value.startsWith("system.")) {
//            throw new IllegalArgumentException(
//                Bundle.VALIDATION_invalid_prefix("system."));
//        }
//        if (value.contains("$")) {
//            throw new IllegalArgumentException(
//                Bundle.VALIDATION_forbidden_character('$'));
//        }
//        if (value.contains("\u0000")) {
//            throw new IllegalArgumentException(
//                 Bundle.VALIDATION_invalid_character());
//        }
//        if (lookup.lookup(DB.class).getCollectionNames().contains(value)) {
//            throw new IllegalArgumentException(
//                Bundle.VALIDATION_exists("collection", value));
//        }
        
        if (lookup.lookup(MongoClient.class).getDatabaseNames().contains(value)) {
            throw new IllegalArgumentException(
                Bundle.VALIDATION_exists("database", value));
        }
    }
}
