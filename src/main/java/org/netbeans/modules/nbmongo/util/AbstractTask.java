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
package org.netbeans.modules.nbmongo.util;

import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.util.RequestProcessor;
import org.openide.util.TaskListener;

/**
 *
 * @author Yann D'Isanto
 */
public abstract class AbstractTask<T extends Runnable> implements Runnable {

    private final RequestProcessor requestProcessor;

    private final T runnable;

    public AbstractTask(RequestProcessor requestProcessor, T runnable) {
        this.requestProcessor = requestProcessor;
        this.runnable = runnable;
    }

    public abstract String getLabel();

    public final T getRunnable() {
        return runnable;
    }

    @Override
    public void run() {
        final RequestProcessor.Task task = requestProcessor.create(runnable);
        final ProgressHandle progressHandle = ProgressHandleFactory.createHandle(getLabel(), task);
        task.addTaskListener(new TaskListener() {
            @Override
            public void taskFinished(org.openide.util.Task task) {
                //make sure that we get rid of the ProgressHandle
                //when the task is finished
                progressHandle.finish();
            }
        });
        //start the progresshandle the progress UI will show 500s after
        progressHandle.start();
        //this actually start the task
        task.schedule(0);
    }

}
