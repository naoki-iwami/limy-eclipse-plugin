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
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;

/**
 * Java関連のユーティリティクラスです。
 * @author Naoki Iwami
 */
public final class LimyQalabJavaUtils {
    
    /**
     * private constructor
     */
    private LimyQalabJavaUtils() { }

    /**
     * Javaプロジェクトの全ライブラリ（Java binパスおよび参照プロジェクトのExportライブラリも含む）を取得します。
     * @param project Javaプロジェクト
     * @return ライブラリの絶対パス
     * @throws CoreException 
     */
    public static Collection<String> getJavaLibraries(IJavaProject project)
            throws CoreException {
        
        Collection<String> results = new HashSet<String>();
        
        for (IPackageFragmentRoot fragment : project.getAllPackageFragmentRoots()) {
            
            String location = getExternalLocation(fragment);
            if (location != null) {
                results.add(location);
            }
        }
        
        // TODO 複数の Eclipse Plugin をまとめるようなプロジェクトの場合、
        // 各プロジェクトのEclipse関連jarが取得できない
        // これはexportに含めていない為だが、含めるわけにはいかない（Accessエラーになる）ので
        // それ用のフラグを用意して別途取得する必要あり
        
        for (IProject refProject : project.getProject().getReferencedProjects()) {
            
            boolean isPlugin = Arrays.asList(refProject.getDescription().getNatureIds())
                    .indexOf("org.eclipse.pde.PluginNature") >= 0;
            if (isPlugin) {
                IPackageFragmentRoot[] roots = JavaCore.create(refProject)
                        .getAllPackageFragmentRoots();
                for (IPackageFragmentRoot fragment : roots) {
                    String location = getExternalLocation(fragment);
                    if (location != null) {
                        results.add(location);
                    }
                }
            }
                    
        }
        
        return results;
    }

    /**
     * PackageFragmentRootの絶対パスを返します。
     * @param fragment PackageFragmentRoot
     * @return
     * @throws CoreException 
     */
    public static String getExternalLocation(IPackageFragmentRoot fragment) throws CoreException {
        
        String location = null;
        if (fragment.getKind() == IPackageFragmentRoot.K_SOURCE) {
            // 自プロジェクトおよび参照プロジェクトのソースディレクトリ
            IPath outputLocation = fragment.getRawClasspathEntry().getOutputLocation();
            if (outputLocation != null) {
                // ソースパス固有の出力先が指定されている場合
                location = LimyQalabUtils.createFullPath(
                        fragment.getJavaProject(), outputLocation);
            } else {
                // ソースパス固有の出力先が指定されていない場合、プロジェクトのデフォルト出力先を使用
                location = LimyQalabUtils.createFullPath(fragment.getJavaProject(),
                        fragment.getJavaProject().getOutputLocation());
            }
        } else {
            // 自プロジェクトのclasspath一覧および参照プロジェクトのExportライブラリ
            IResource resource = fragment.getResource();
            if (resource != null) {
                location = resource.getLocation().toString();
            } else {
                // Variable指定のjarファイルはresource = null となる
                IPath path = fragment.getRawClasspathEntry().getPath();
                if (!path.toString().startsWith("org.eclipse.jdt.launching.JRE_CONTAINER")) {
                    // JRE以外のJARファイル（Variable指定）もクラスパスに追加
                    location = fragment.getPath().toString();
                }
            }
        }
        return location;
    }
    
    public static IPackageFragmentRoot getPackageFragmentRoot(IJavaElement el) {
//        if (el.getElementType() == IJavaElement.COMPILATION_UNIT) {
//            return (PackageFragmentRoot)((IPackageFragment)el.getParent()).getParent();
//        }
//        if (el.getElementType() == IJavaElement.PACKAGE_FRAGMENT) {
//            return (PackageFragmentRoot)el.getParent();
//        }
//        if (el.getElementType() == IJavaElement.PACKAGE_FRAGMENT_ROOT) {
//            return (PackageFragmentRoot)el;
//        }
        return el.getJavaProject().getPackageFragmentRoot(el.getResource());
    }

    /**
     * Java要素に対応するbinディレクトリを返します。
     * <p>
     * パッケージ要素 : 対応するbinディレクトリ内のサブディレクトリ<br>
     * ソースディレクトリ : 対応するbinディレクトリ<br>
     * </p>
     * @param el Java要素
     * @return binディレクトリ（絶対パス）
     * @throws CoreException 
     */
    public static String getBinDirPath(IJavaElement el) throws CoreException {

        if (el.getElementType() == IJavaElement.PACKAGE_FRAGMENT) {
            IPackageFragmentRoot root = (IPackageFragmentRoot)el.getParent();
            String path = LimyQalabJavaUtils.getExternalLocation(root);
            return new File(path, el.getElementName().replace('.', '/')).getAbsolutePath();
        }
        if (el.getElementType() == IJavaElement.PACKAGE_FRAGMENT_ROOT) {
            return LimyQalabJavaUtils.getExternalLocation((IPackageFragmentRoot)el);
        }
        return null;
    }


}
