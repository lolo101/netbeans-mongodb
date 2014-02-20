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
package org.netbeans.modules.mongodb.shell;

import com.mongodb.MongoClientURI;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.netbeans.api.extexecution.ExecutionDescriptor;
import org.netbeans.api.extexecution.ExecutionService;
import org.netbeans.modules.mongodb.ConnectionInfo;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;

/**
 *
 * @author Yann D'Isanto
 */
@Messages({
    "ACTION_MongoShell=Mongo shell",
    "# {0} - connection uri",
    "mongoShellOutputTitle=mongo shell - {0}"
})
public final class MongoShellAction extends AbstractAction {

    private final Lookup lookup;

    public MongoShellAction(Lookup lookup) {
        super(Bundle.ACTION_MongoShell());
        this.lookup = lookup;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final ConnectionInfo ci = lookup.lookup(ConnectionInfo.class);
        final MongoClientURI uri = ci.getMongoURI();
        final ExecutionDescriptor descriptor = new ExecutionDescriptor()
            .inputVisible(true)
//            .controllable(true)
            .frontWindow(true);
        final ExecutionService service = ExecutionService.newService(
            new MongoShellProcessCreator(uri),
            descriptor,
            Bundle.mongoShellOutputTitle(uri.toString()));
        service.run();
    }
}
