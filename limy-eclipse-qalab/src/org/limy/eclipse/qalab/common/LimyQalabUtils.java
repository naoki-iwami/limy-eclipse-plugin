/*
 * Created 2006/08/19
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
import java.util.Collection;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.limy.eclipse.common.resource.LimyResourceUtils;

/**
 * Limy Qalab用のユーティリティクラスです。
 * @author Naoki Iwami
 */
public final class LimyQalabUtils {

    /**
     * private constructor
     */
    private LimyQalabUtils() { }

    /**
     * 選択されたアイテムの親Javaプロジェクトを返します。
     * @param selection 選択内容
     * @return 親Javaプロジェクト（存在しなければnull）
     */
    public static IJavaProject getJavaProject(IStructuredSelection selection) {
        
        // 選択された最初のアイテムを取得
        Object element = selection.getFirstElement();
        if (element == null) {
            return null;
        }
        
        // Javaプロジェクトを取得
        IJavaProject project = null;
        if (element instanceof IResource) {
            IResource resource = (IResource)element;
            IProject normalProject = resource.getProject();
            project = JavaCore.create(normalProject);
        }
        if (element instanceof IJavaElement) {
            IJavaElement javaEl = (IJavaElement)element;
            project = javaEl.getJavaProject();
        }
        return project;

    }

    /**
     * JavaファイルがQA対象外かどうかを判定します。
     * @param env 
     * @param path Javaファイル
     * @param store プロジェクトStore
     * @return QA対象外ソースディレクトリならば真
     */
    public static boolean isIgnoreSource(LimyQalabEnvironment env, IPath path) {
        
        String packages = env.getStore().getString(LimyQalabConstants.IGNORE_PACKAGES);
        String[] ignorePackages = packages.split("\n");
        
        IResource resource = LimyResourceUtils.newFile(path);
        try {
            String className = getQualifiedClassName(env, resource);
            if (className == null) {
                return false;
            }
            for (String ignorePackage : ignorePackages) {
                if (className.startsWith(ignorePackage + ".")) {
                    return true;
                }
            }
        } catch (CoreException e) {
            // do nothing
        }
        return false;
    }

    /**
     * JavaプロジェクトのJDKバージョンを取得します。
     * @param project Javaプロジェクト
     * @return JDKバージョン
     */
    public static String getJdkVersion(IJavaProject project) {
        return project.getOption(JavaCore.COMPILER_SOURCE, true);
    }

    /**
     * IPath相対形式のパスを、絶対パスに変換して返します。
     * @param javaProject Javaプロジェクト（null可）
     * @param path 相対パス
     * @return 絶対パス
     * @throws CoreException コア例外
     */
    public static String createFullPath(IJavaProject javaProject, IPath path)
            throws CoreException {
        
        if (javaProject != null
                && javaProject.getProject().getName().equals(path.segment(0))) {
            
            // プロジェクトが同一の場合
            return javaProject.getProject().getLocation()
            .append(path.removeFirstSegments(1)).toString();
        }
        
        IResource[] projects = ResourcesPlugin.getWorkspace().getRoot().members();
        for (IResource resource : projects) {
            IProject targetProject = (IProject)resource;
            if (targetProject.getName().equals(path.segment(0))) {
                return targetProject.getLocation()
                        .append(path.removeFirstSegments(1)).toString();
            }
        }
        return null;
    }
    
    /**
     * Javaリソースのクラス名（完全限定名）を取得します。
     * @param env 
     * @param resource リソース
     * @return クラス名
     * @throws CoreException 
     */
    public static String getQualifiedClassName(LimyQalabEnvironment env,
            IResource resource) throws CoreException {
        
        // ex. /project/src/pack1/ClassA.java
        String pathStr = resource.getFullPath().toString();
    
        Collection<IPath> paths = env.getSourcePaths(true);
        for (IPath path : paths) {
            
            if (pathStr.equals(path.toString())) {
                return null;
            }
            // ex. "/project/src/pack1/ClassA.java".startsWith("/project/src")
            if (pathStr.startsWith(path.toString())) {
                
                // ex. pack1/ClassA.java
                String relativeName = pathStr.substring(path.toString().length() + 1);
                
                if (relativeName.lastIndexOf('.') <= 0) {
                    return null;
                }
                
                // ex. pack1.ClassA
                return relativeName.substring(0, relativeName.lastIndexOf('.'))
                    .replace('\\', '/').replace('/', '.');
            }
        }
        return null;
    }

    /**
     * ".limy"上にテンポラリファイルを作成します。
     * @param project プロジェクト
     * @param relativePath ファイル名
     * @return
     */
    public static File createTempFile(IProject project, String relativePath) {
        File baseDir = new File(project.getLocation().toFile(), ".limy");
        File file = new File(baseDir, relativePath);
        file.getParentFile().mkdirs();
        return file;
    }

    /**
     * プロジェクトの全出力先パスを results に追加します。
     * @param project 
     * @param results 結果格納先
     */
    public static void appendProjectBinPaths(IJavaProject project,
            Collection<IPath> results) {
        
        results.add(project.readOutputLocation());
        // プロジェクトのソースディレクトリ一覧をループ
        for (IClasspathEntry entry : project.readRawClasspath()) {
            IPath location = entry.getOutputLocation();
            if (location != null) {
                // ソースディレクトリ特有の出力ディレクトリが指定されている場合はここを通る
                results.add(location);
            }
        }
    }

}
