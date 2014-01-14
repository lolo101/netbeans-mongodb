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

import org.netbeans.modules.nbmongo.ui.CollectionViewTopComponent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 *
 * @author Yann D'Isanto
 */
public class TopComponentUtils {

    public static TopComponent find(Object data) {
        final Set<TopComponent> openTopComponents = WindowManager.getDefault().getRegistry().getOpened();
        for (TopComponent tc : openTopComponents) {
            if (tc instanceof CollectionViewTopComponent) {
                if (tc.getLookup().lookup(data.getClass()) == data) {
                    return tc;
                }
            }
        }
        return null;
    }

    public static Collection<TopComponent> findAll(Object data) {
        final List<TopComponent> result = new ArrayList<>();
        final Set<TopComponent> openTopComponents = WindowManager.getDefault().getRegistry().getOpened();
        for (TopComponent tc : openTopComponents) {
            if (tc instanceof CollectionViewTopComponent) {
                if (tc.getLookup().lookup(data.getClass()) == data) {
                    result.add(tc);
                }
            }
        }
        return result;
    }

    private TopComponentUtils() {
    }

}