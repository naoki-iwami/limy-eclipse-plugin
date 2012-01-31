/*
 * Created 2007/08/29
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
package org.limy.eclipse.qalab.common;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

/**
 * クラスローダ作成を担当します。
 * @author Naoki Iwami
 */
public final class ClassLoaderCreator {
    
    /**
     * private constructor
     */
    private ClassLoaderCreator() { }
    
    /**
     * Javaプロジェクトの全ライブラリを格納したクラスローダを作成して返します。
     * @param env 
     * @param parentLoader 親クラスローダ
     * @return Javaプロジェクト用クラスローダ
     * @throws CoreException コア例外
     * @throws IOException I/O例外
     */
    public static ClassLoader createProjectClassLoader(LimyQalabEnvironment env,
            final ClassLoader parentLoader)
            throws CoreException, IOException {
        
        IJavaProject project = env.getJavaProject();
        
        Collection<URI> uris = new ArrayList<URI>();
        for (String libraryPath : LimyQalabJavaUtils.getJavaLibraries(project)) {
            uris.add(new File(libraryPath).toURI());
        }
        // 参照プロジェクトのJavaライブラリ（Exportされていないもの）も追加
        for (IProject refProject : project.getProject().getReferencedProjects()) {
            IJavaProject refJavaProject = JavaCore.create(refProject);
            for (String libraryPath : LimyQalabJavaUtils.getJavaLibraries(refJavaProject)) {
                uris.add(new File(libraryPath).toURI());
            }
        }
    
        // プロジェクト用のクラスローダを作成
        final URL[] urls = new URL[uris.size()];
        Iterator<URI> it = uris.iterator();
        for (int i = 0; i < uris.size(); i++) {
            urls[i] = it.next().toURL();
        }
        
        return AccessController.doPrivileged(
                new PrivilegedAction<URLClassLoader>() {
                    public URLClassLoader run() {
                        return new URLClassLoader(urls, parentLoader);
                    }
                });
    }


}
