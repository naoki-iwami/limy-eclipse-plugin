/*
 * Created 2007/02/15
 * Copyright (C) 2003-2009  Naoki Iwami (naoki@limy.org)
 *
 * This file is part of Limy Eclipse Plugin.
 *
 * Limy Eclipse Plugin is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Limy Eclipse Plugin is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Limy Eclipse Plugin.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.limy.eclipse.qalab.umlgraph.javadoc.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IType;
import org.limy.eclipse.common.LimyEclipsePluginUtils;
import org.limy.eclipse.qalab.common.LimyQalabEnvironment;
import org.limy.eclipse.qalab.common.QalabResourceUtils;
import org.limy.eclipse.qalab.umlgraph.javadoc.ClassDoc;
import org.limy.eclipse.qalab.umlgraph.javadoc.PackageDoc;
import org.limy.eclipse.qalab.umlgraph.javadoc.RootDoc;
import org.limy.eclipse.qalab.umlgraph.javadoc.SourcePosition;

/**
 *
 * @author Naoki Iwami
 */
public class RootDocImpl extends DocImpl implements RootDoc {

    // ------------------------ Fields

    private final LimyQalabEnvironment env;
    
    private String[][] options;
    
    private ClassDoc[] classes;

    private ClassDoc[] refClasses;

    private Writer log;
    
    // ------------------------ Constructors

    public RootDocImpl(LimyQalabEnvironment env, IType[] types,
            IType[] refTypes,
            String[][] options) {
        super(null, null);
        this.env = env;
        this.options = options;
        init(types, refTypes);
    }
    
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        if (log != null) {
            log.flush();
            log.close();
        }
    }

    /**
     * @param types 
     * @param refTypes 
     * 
     */
    private void init(IType[] types, IType[] refTypes) {

        classes = new ClassDoc[types.length];
        refClasses = new ClassDoc[refTypes.length];
        
        try {
            
            File logFile = env.getTempFile("umlgraph.log");
            logFile.getParentFile().mkdirs();
            log = new FileWriter(logFile);
            
            for (int i = 0; i < types.length; i++) {
                IResource classResource = QalabResourceUtils.getClassResource(
                        env, types[i].getResource()).getResource();
                ClassAsmInfo info = new ClassAsmInfo(classResource.getLocation().toFile());
                classes[i] = new ClassDocImpl(this, types[i], info);
            }

            for (int i = 0; i < refTypes.length; i++) {
                IResource classResource = QalabResourceUtils.getClassResource(
                        env, refTypes[i].getResource()).getResource();
                ClassAsmInfo info = new ClassAsmInfo(classResource.getLocation().toFile());
                refClasses[i] = new ClassDocImpl(this, refTypes[i], info);
            }

        } catch (CoreException e) {
            LimyEclipsePluginUtils.log(e);
        } catch (IOException e) {
            LimyEclipsePluginUtils.log(e);
        }
    }

    public String[][] options() {
        
        return options;
    }
    
    public ClassDoc[] classes() {
        return classes;
    }

    public ClassDoc classNamed(String name) {
        for (ClassDoc classDoc : classes) {
            if (name.equals(classDoc.qualifiedName())) {
                return classDoc;
            }
        }
        for (ClassDoc classDoc : refClasses) {
            if (name.equals(classDoc.qualifiedName())) {
                return classDoc;
            }
        }
        
        // Dependなどでは完全限定名で指定しない。その場合は単一クラス名で検索
        for (ClassDoc classDoc : classes) {
            if (classDoc.qualifiedName().endsWith(name)) {
                return classDoc;
            }
        }
        for (ClassDoc classDoc : refClasses) {
            if (classDoc.qualifiedName().endsWith(name)) {
                return classDoc;
            }
        }
        
        return new RefClassDocImpl(this, name);
    }
    
    public void printWarning(String string) {
        try {
            log.write(string);
            log.write('\n');
            log.flush();
        } catch (IOException e) {
            // do nothing
        }
    }

    public void printNotice(String msg) {
        try {
            log.write(msg);
            log.write('\n');
            log.flush();
        } catch (IOException e) {
            // do nothing
        }
    }
    
    
    
    
    

    public PackageDoc packageNamed(String arg0) {
        return null; // not support
    }

    public ClassDoc[] specifiedClasses() {
        return null; // not support
    }

    public PackageDoc[] specifiedPackages() {
        return null; // not support
    }

    public void printError(SourcePosition arg0, String arg1) {
        // not support
    }

    public void printError(String arg0) {
        // not support
    }

    public void printNotice(SourcePosition arg0, String arg1) {
        // not support
    }

    public void printWarning(SourcePosition arg0, String arg1) {
        // not support
    }

    
    public LimyQalabEnvironment getEnv() {
        return env;
    }

}
