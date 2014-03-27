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

package org.netbeans.modules.mongodb.native_tools;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import org.netbeans.api.extexecution.ExecutionDescriptor;
import org.netbeans.modules.mongodb.ui.actions.ExecutionAction;
import org.netbeans.modules.mongodb.util.ProcessCreator;
import org.openide.util.Lookup;

/**
 *
 * @author Yann D'Isanto
 */
public final class MongoRestoreExecAction extends ExecutionAction {

    private final Map<String, String> options = new HashMap<>();

    public MongoRestoreExecAction(Map<String, String> options) {
        super("MongoRestore", Lookup.EMPTY);
        this.options.putAll(options);
    }
    
    @Override
    protected String getDisplayName() {
        return "mongo restore";
    }

    @Override
    protected ExecutionDescriptor getExecutionDescriptor() {
        return super.getExecutionDescriptor().frontWindow(true);
    }

    @Override
    protected Callable<Process> getProcessCreator() {
        final String mongoRestoreExec = MongoNativeTools.MONGO_RESTORE.getExecFullPath().toString();
        return new ProcessCreator.Builder(mongoRestoreExec)
            .options(options)
            .build();
    }
    
}
