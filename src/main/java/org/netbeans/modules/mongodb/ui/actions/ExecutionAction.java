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
package org.netbeans.modules.mongodb.ui.actions;

import java.awt.event.ActionEvent;
import java.util.concurrent.Callable;
import javax.swing.AbstractAction;
import org.netbeans.api.extexecution.ExecutionDescriptor;
import org.netbeans.api.extexecution.ExecutionService;
import org.openide.util.Lookup;

/**
 *
 * @author Yann D'Isanto
 */
public abstract class ExecutionAction extends AbstractAction {

    private final Lookup lookup;

    public ExecutionAction(String name, Lookup lookup) {
        super(name);
        this.lookup = lookup;
    }

    
    @Override
    public final void actionPerformed(ActionEvent e) {
        final Callable<Process> processCreator = getProcessCreator();
        if(processCreator == null) {
            return;
        }
        final ExecutionService service = ExecutionService.newService(
            processCreator,
            getExecutionDescriptor(),
            getDisplayName());
        service.run();
    }

    public final Lookup getLookup() {
        return lookup;
    }
    
    protected abstract String getDisplayName();
    
    protected ExecutionDescriptor getExecutionDescriptor() {
        return new ExecutionDescriptor();
    }
    
    /**
     * @return the process creator or null if no process should be executed.
     */
    protected abstract Callable<Process> getProcessCreator();
    
}
