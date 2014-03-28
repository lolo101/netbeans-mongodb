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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 *
 * @author Yann D'Isanto
 */
public final class ProcessCreator implements Callable<Process> {

    private final List<String> commandLine;

    public ProcessCreator(List<String> commandLine) {
        this.commandLine = commandLine;
    }

    @Override
    public Process call() throws Exception {
        return new ProcessBuilder(commandLine).start();
    }

    public final static class Builder {

        private String command;

        private final Map<String, String> options = new HashMap<>();
        
        private final List<String> args = new ArrayList<>();

        public Builder() {
        }

        public Builder(String command) {
            this.command = command;
        }

        public Builder command(String command) {
            this.command = command;
            return this;
        }

        public Builder option(String optionName) {
            options.put(optionName, "");
            return this;
        }

        public Builder option(String optionName, String optionValue) {
            options.put(optionName, optionValue);
            return this;
        }

        public Builder options(Map<String, String> options) {
            this.options.putAll(options);
            return this;
        }

        public Builder arg(String arg) {
            args.add(arg);
            return this;
        }

        public Builder args(String... args) {
            this.args.addAll(Arrays.asList(args));
            return this;
        }

        public ProcessCreator build() {
            final List<String> commandLine = new ArrayList<>();
            commandLine.add(command);
            for (Map.Entry<String, String> option : options.entrySet()) {
                commandLine.add(option.getKey());
                final String optionValue = option.getValue();
                if (optionValue.isEmpty() == false) {
                    commandLine.add(optionValue);
                }
            }
            commandLine.addAll(args);
            return new ProcessCreator(commandLine);
        }
    }

}
