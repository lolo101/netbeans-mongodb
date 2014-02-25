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
package org.netbeans.modules.mongodb.util;

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

    @SuppressWarnings("unchecked")
    public static <T extends TopComponent> T find(Class<T> topComponentType, Object data) {
        final Set<TopComponent> openTopComponents = WindowManager.getDefault().getRegistry().getOpened();
        for (TopComponent tc : openTopComponents) {
            if(topComponentType.isAssignableFrom(tc.getClass())) {
                if (tc.getLookup().lookup(data.getClass()) == data) {
                    return (T) tc;
                }
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static <T extends TopComponent> Collection<T> findAll(Class<T> topComponentType, Object data) {
        final List<T> result = new ArrayList<>();
        final Set<TopComponent> openTopComponents = WindowManager.getDefault().getRegistry().getOpened();
        for (TopComponent tc : openTopComponents) {
            if(topComponentType.isAssignableFrom(tc.getClass())) {
                if (tc.getLookup().lookup(data.getClass()) == data) {
                    result.add((T) tc);
                }
            }
        }
        return result;
    }

    private TopComponentUtils() {
    }

}
