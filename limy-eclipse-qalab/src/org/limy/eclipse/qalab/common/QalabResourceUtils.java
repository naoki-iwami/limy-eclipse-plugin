/*
 * Created 2007/01/06
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

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.limy.eclipse.common.LimyEclipsePluginUtils;
import org.limy.eclipse.common.io.LimyIOUtils;
import org.limy.eclipse.common.resource.LimyResourceUtils;
import org.limy.eclipse.common.resource.ResourceWithBasedir;
import org.limy.qalab.AutoCreated;

/**
 * リソース関連のユーティリティクラスです。
 * @author Naoki Iwami
 */
public final class QalabResourceUtils {

    /**
     * private constructor
     */
    private QalabResourceUtils() { }
    
    /**
     * Javaファイルに対応するclassファイルを返します。
     * @param env 
     * @param javaResource Javaファイル
     * @return 対応するclassファイル
     * @throws CoreException 
     */
    public static ResourceWithBasedir getClassResource(LimyQalabEnvironment env,
            IResource javaResource)
            throws CoreException {
        
        // Javaファイルのパス（/project/src/... 形式）
        String pathStr = javaResource.getFullPath().toString();
    
        Collection<IPath> sourcePaths = env.getSourcePaths(false);
        Collection<IPath> binPaths = env.getBinPaths(false);
        
        // 全ソースパスをループ
        for (IPath path : sourcePaths) {
            if (pathStr.startsWith(path.toString())) {
                // Javaファイルがこのソースパス配下に存在する場合
                
                // ex. pack1/ClassA.java
                String relativeName = pathStr.substring(path.toString().length());
                
                // ex. pack1/ClassA.class
                Path classfilePath = new Path(
                        relativeName.substring(0, relativeName.lastIndexOf('.'))
                        + ".class");
    
                // 全binパスをループして対応するclassファイルを検索
                for (IPath binPath : binPaths) {
                    IResource searchResource = LimyResourceUtils.newFile(
                            binPath.append(classfilePath));
                    if (searchResource.exists()) {
                        return new ResourceWithBasedir(binPath, searchResource);
                    }
                }
            }
        }
        
        return null; // Java Initializer が完了していない場合、ここに来る
    }

    /**
     * Javaファイルの基準バス付リソースを返します。
     * @param env 
     * @param javaResource Javaファイル
     * @return 基準バス付リソース
     * @throws CoreException 
     */
    public static ResourceWithBasedir getResourceWithBasedir(LimyQalabEnvironment env,
            IResource javaResource)
            throws CoreException {
        
        // Javaファイルのパス（/project/src/... 形式）
        String pathStr = javaResource.getFullPath().toString();
        
        Collection<IPath> sourcePaths = env.getSourcePaths(false);
        
        // 全ソースパスをループ
        for (IPath path : sourcePaths) {
            if (pathStr.startsWith(path.toString())) {
                // Javaファイルがこのソースパス配下に存在する場合
                return new ResourceWithBasedir(path, javaResource);
            }
        }
        
        LimyEclipsePluginUtils.log("Resource " + javaResource + " is not found.");
        return null;
    }

    /**
     * @param resource
     * @return
     */
    public static boolean isTestResource(IResource resource) {
        String name = resource.getName();
        if (name.endsWith("Test.java")) {
            return true;
        }
        return false;
    }

    /**
     * リソースがQA対象外かどうかを判定します。
     * @param env QALab環境設定内容
     * @param resource リソース
     * @return リソースがQA対象外ならば true
     */
    public static boolean isIgnoreResource(LimyQalabEnvironment env, IResource resource) {
        if (LimyQalabUtils.isIgnoreSource(env, resource.getFullPath())) {
            return true;
        }
        
        // テストクラスは、QA対象外とする（今後変更の余地あり）
        if (isTestResource(resource)) {
            return true;
        }
        
        return false;
    }

    /**
     * クラス名からJavaリソースを検索して返します。
     * @param env 
     * @param project プロジェクト
     * @param className クラス名（完全限定名）
     * @param enableRefProject 参照プロジェクトも検索対象に含めるかどうか
     * @return Javaリソース（見つからなかったらnull）
     * @throws CoreException 
     */
    public static IResource getJavaResource(LimyQalabEnvironment env,
            String className,
            boolean enableRefProject)
            throws CoreException {
        
        Collection<IPath> sourcePaths;
        sourcePaths = env.getSourcePaths(enableRefProject);
    
        // クラス名が空の場合、どこのソースかはわからないのでソースパスの先頭を返す
        if (className.length() == 0) {
            if (sourcePaths.isEmpty()) {
                // ソースパスが一つも存在しない場合、仕方無いのでプロジェクトを返す
                return env.getProject();
            }
            IPath firstPath = sourcePaths.iterator().next();
            // 先頭のソースパスを返す
            return LimyResourceUtils.newFolder(firstPath);
        }
        
        // クラス名が指定されている場合、全ソースパスから検索
        String javaFileName = className.replace('.', '/') + ".java";
        if (className.indexOf('$') >= 0) {
            javaFileName = className.substring(0, className.indexOf('$'))
                    .replace('.', '/') + ".java";
        }
            
        for (IPath path : sourcePaths) {
            IResource resource = LimyResourceUtils.newFile(
                    path.append(javaFileName));
            if (resource.exists()) {
                return resource;
            }
        }
        return null;
    }

    /**
     * 基準リソース以下の全Javaファイルを results に追加します（再帰）。
     * @param results Javaリソース格納先
     * @param resource 基準リソース（フォルダまたはファイル）
     * @throws CoreException 
     */
    public static void appendJavaResources(
            Collection<IResource> results, IResource resource)
            throws CoreException {
        
        if (resource.getType() == IResource.FOLDER) {
            IResource[] children = ((IContainer)resource).members();
            for (IResource child : children) {
                appendJavaResources(results, child);
            }
        }
        if (resource.getType() == IResource.FILE) {
            String fileName = resource.getName().replace('\\', '/');
            if (fileName.endsWith(".java")) {
                results.add(resource);
            }
        }
    }

    /**
     * プロジェクトの全ソースファイル一覧を返します。
     * @param env 
     * @param enableRef 参照プロジェクトも有効にするか
     * @param ignoreSource falseにすると、QA対象外ディレクトリも一覧として返す
     * @return プロジェクトの全ソースファイル一覧
     * @throws CoreException コア例外
     */
    public static List<IResource> getAllSourceFiles(
            LimyQalabEnvironment env, boolean enableRef, boolean ignoreSource)
            
            throws CoreException {
        
        Collection<IPath> sourcePaths = env.getSourcePaths(enableRef);
        List<IResource> results = new ArrayList<IResource>();
        for (IPath path : sourcePaths) {
            IResource resource = LimyResourceUtils.newFolder(path);
            appendJavaResources(results, resource);
        }
        
        if (!ignoreSource) {
            return results;
        }
        
        for (ListIterator<IResource> it = results.listIterator(); it.hasNext();) {
            IResource resource = it.next();
            if (LimyQalabUtils.isIgnoreSource(env, resource.getFullPath())) {
                it.remove();
            }
        }
        return results;
    }

    /**
     * Javaメインクラスに対応するテストケース名を取得します。
     * @param env 
     * @param javaResource Javaリソース（メインクラス）
     * @return テストケース名
     * @throws CoreException 
     */
    public static String getQualifiedTestClassName(LimyQalabEnvironment env,
            IResource javaResource)
            throws CoreException {
        return LimyQalabUtils.getQualifiedClassName(env, javaResource) + "Test";
    }

    /**
     * Javaテストクラスに対応するメインクラス名を取得します。
     * @param env 
     * @param javaTestResource Javaリソース（テストクラス）
     * @return メインクラス名
     * @throws CoreException 
     */
    public static String getQualifiedMainClassName(LimyQalabEnvironment env,
            IResource javaTestResource) throws CoreException {
        
        String testName = LimyQalabUtils.getQualifiedClassName(env, javaTestResource);
        if (testName.endsWith("Test")) {
            return testName.substring(0, testName.length() - 4);
        }
        return testName;
    }

    /**
     * JavaファイルをパースしてDocumentを生成します。
     * @param resource 
     * @return 
     * @throws CoreException 
     * @throws IOException 
     */
    public static IDocument parseDocument(IResource resource) throws IOException, CoreException {
        
        IProject project = resource.getProject();
        String source = LimyIOUtils.getContent(resource.getLocation().toFile(),
                project.getDefaultCharset());
        return new Document(source);
    }

    /**
     * AutoCreated注釈をつけたクラス一覧を取得します。
     * @param env QALab環境設定内容
     * @param project Javaプロジェクト
     * @return AutoCreated注釈をつけたクラス一覧を取得
     * @throws CoreException コア例外
     * @throws IOException I/O例外
     */
    public static Collection<IFile> getAutoCreatedFiles(
            LimyQalabEnvironment env)
            throws CoreException, IOException {
        
        Collection<IFile> results = new ArrayList<IFile>();
        
        List<IResource> souceFiles = QalabResourceUtils.getAllSourceFiles(
                env, true, true);
        for (IResource file : souceFiles) {
            if (isAutoCreated(env, file)) {
                results.add((IFile)file);
            }
        }
        
        return results;
    }
    
    /**
     * Javaリソースが自動生成されたものかどうかを判定します。
     * @param env QALab環境設定内容
     * @param file Javaリソース
     * @return Javaリソースが自動生成されていたら true
     * @throws CoreException 
     */
    public static boolean isAutoCreated(LimyQalabEnvironment env, IResource file)
            throws CoreException {
        
        String qualifiedClassName = LimyQalabUtils.getQualifiedClassName(env, file);
        try {
            Annotation[] annotations = env.getProjectClassLoader()
                    .loadClass(qualifiedClassName).getAnnotations();
            for (Annotation annotation : annotations) {
                if (AutoCreated.class.getName().equals(annotation.annotationType().getName())) {
                    return true;
                }
            }
        } catch (ClassNotFoundException e) {
            // do nothing
        } catch (UnsupportedClassVersionError e) {
            LimyEclipsePluginUtils.log("UnsupportedClassVersionError : " + qualifiedClassName);
        } catch (NoClassDefFoundError e) {
            LimyEclipsePluginUtils.log("NoClassDefFoundError : " + qualifiedClassName);
            throw e;
        }
        return false;
    }

    public static Collection<IResource> getResources(Collection<IJavaElement> elements) {
        Collection<IResource> results = new ArrayList<IResource>();
        for (IJavaElement el : elements) {
            results.add(el.getResource());
        }
        return results;
    }


}
