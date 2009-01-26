/*
 * $Id: NameSpace.java 4564 2008-09-18 22:01:10Z fduminy $
 *
 * JNode.org
 * Copyright (C) 2003-2006 JNode.org
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public 
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; If not, write to the Free Software Foundation, Inc., 
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.jnode.test.shell.harness;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.jnode.naming.InitialNaming;
import org.jnode.plugin.PluginManager;
import org.jnode.shell.CommandShell;
import org.jnode.shell.ShellException;

public abstract class JNodeTestRunnerBase implements TestRunnable {
    protected ByteArrayOutputStream outBucket;
    protected ByteArrayOutputStream errBucket;
    
    protected final TestSpecification spec;
    protected final TestHarness harness;
    
    protected final boolean usingEmu;

    public JNodeTestRunnerBase(TestSpecification spec, TestHarness harness) {
        super();
        this.spec = spec;
        this.harness = harness;
        this.usingEmu = TestEmu.initEmu(harness.getRoot());
    }
    
    public CommandShell getShell() throws ShellException {
        CommandShell shell = TestEmu.getShell(); 
        if (shell == null) {
            shell = new TestCommandShell(System.in, System.out, System.err);
            shell.configureShell();
        }
        return shell;
    }
    
    @Override
    public void cleanup() {
    }

    @Override
    public void setup() {
        if (spec.getTestSet() != null) {
            for (PluginSpecification plugin : spec.getTestSet().getPlugins()) {
                ensurePluginLoaded(plugin);
            }
        }
        for (PluginSpecification plugin : spec.getPlugins()) {
            ensurePluginLoaded(plugin);
        }
        System.setIn(new ByteArrayInputStream(spec.getInputContent().toString().getBytes()));
        outBucket = new ByteArrayOutputStream();
        errBucket = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outBucket));
        System.setErr(new PrintStream(errBucket));
    }
    
    protected void ensurePluginLoaded(PluginSpecification plugin) {
        if (usingEmu) {
            TestEmu.loadPseudoPlugin(plugin.getPluginId(), plugin.getClassName());
        } else {
            // TODO - I'm not sure about this.  A simpler alternative is to assume
            // that all required plugins have already been loaded by JNode.
            String ver = (plugin.getPluginVersion().length() == 0) ? 
                    System.getProperty("os.version") : plugin.getPluginVersion();
            try {
                PluginManager mgr = InitialNaming.lookup(PluginManager.NAME);
                mgr.getRegistry().loadPlugin(mgr.getLoaderManager(), plugin.getPluginId(), ver);
            } catch (Exception ex) {
                throw new RuntimeException(
                        "Cannot load plugin '" + plugin.getPluginId() + "/" + ver + "'");
            }
        }
    }
}