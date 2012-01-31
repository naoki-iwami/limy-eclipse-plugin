/*
 * Created 2007/01/31
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * QALab用の環境設定情報を格納するクラスです。
 * @author Naoki Iwami
 */
public class LimyQalabEnvironment {

    // ------------------------ Fields

    /** プロジェクト用ストア */
    private final IPreferenceStore store;
    
    /** プロジェクト一覧 */
    private IProject[] projects;
    
    /** プロジェクト用クラスローダ */
    private ClassLoader projectClassLoader;

    /** ソースパス一覧（参照プロジェクトを含む） */
    private Collection<IPath> sourcePathsWithRef;

    /** ソースパス一覧（参照プロジェクトを除く） */
    private Collection<IPath> sourcePathsWithoutRef;

    /** メインJavaプロジェクト */
    private IJavaProject javaProject;

    /** binパス一覧 */
    private Collection<IPath> binPaths;
    
    // ------------------------ Constructors

    /**
     * LimyQalabEnvironmentインスタンスを構築します。
     * @param projects プロジェクト一覧
     * @param store プロジェクト用ストア
     */
    public LimyQalabEnvironment(IProject[] projects, IPreferenceStore store) {
        super();
        this.projects = projects;
        this.store = store;
    }

    // ------------------------ Public Methods

    /**
     * @return
     */
    public IPreferenceStore getStore() {
        return store;
//        return LimyQalabPluginUtils.createQalabStore(getMainProject());
    }
    
    /**
     * メインプロジェクトを取得します。
     * @return メインプロジェクト
     */
    public IProject getProject() {
        return projects[0];
    }

    /**
     * メインJavaプロジェクトを取得します。
     * @return メインJavaプロジェクト
     */
    public IJavaProject getJavaProject() {
        if (javaProject == null) {
            javaProject = JavaCore.create(projects[0]);
        }
        return javaProject;
    }

    /**
     * Javaプロジェクトのソースディレクトリ一覧を返します。
     * @param enableRef 参照プロジェクトも対象に含めるか
     * @return
     * @throws CoreException 
     */
    public Collection<IPath> getSourcePaths(boolean enableRef) throws CoreException {
        if (sourcePathsWithRef == null) {
            sourcePathsWithRef = privateGetSourcePaths(true);
            sourcePathsWithoutRef = privateGetSourcePaths(false);
        }
        return enableRef ? sourcePathsWithRef : sourcePathsWithoutRef;
    }
    
    /**
     * Javaプロジェクトのソースディレクトリ一覧（テストディレクトリは除く）を返します。
     * @return Javaプロジェクトのソースディレクトリ一覧（テストディレクトリは除く）
     * @throws CoreException
     */
    public Collection<IPath> getMainSourcePaths() throws CoreException {

        return getAllSourcePaths(false);
    }

    /**
     * Javaプロジェクトのテストソースディレクトリ一覧を返します。
     * @return Javaプロジェクトのソースディレクトリ一覧
     * @throws CoreException
     */
    public Collection<IPath> getTestSourcePaths() throws CoreException {
        
        return getAllSourcePaths(true);
    }

    /**
     * binディレクトリ一覧を返します。
     * @param enableRef 参照プロジェクトも対象に含めるか
     * @return binディレクトリ一覧
     * @throws JavaModelException 
     */
    public Collection<IPath> getBinPaths(boolean enableRef) throws JavaModelException {
        
        if (binPaths == null) {
            Collection<IPath> paths = privateGetBinPaths(this, enableRef);
            String enableProject = store.getString(LimyQalabConstants.SUB_PROJECT_NAMES);

            binPaths = new ArrayList<IPath>();
            for (IPath path : paths) {
                String projectName = path.segment(0);
                
                // 自プロジェクトもしくは対象のサブプロジェクトのみtargetとする
                if (projectName.equals(getMainProject().getName())
                        || enableProject.indexOf(projectName + "\n") >= 0) {
                    binPaths.add(path);
                }
            }
        }
        return binPaths;
    }
    
    /**
     * 有効な参照プロジェクト一覧を返します。
     * @return 有効な参照プロジェクト一覧
     * @throws CoreException 
     */
    public Collection<IProject> getEnableReferencedProjects() throws CoreException {
        Collection<IProject> results = new ArrayList<IProject>();
        
        String enableProject = store.getString(LimyQalabConstants.SUB_PROJECT_NAMES);

        for (IProject refProject : getMainProject().getReferencedProjects()) {
            if (enableProject.indexOf(refProject.getName() + "\n") >= 0) {
                results.add(refProject);
            }
        }
        return results;
    }

    // ------------------------ Getter/Setter Methods

    /**
     * プロジェクト用クラスローダを取得します。
     * @return プロジェクト用クラスローダ
     */
    public ClassLoader getProjectClassLoader() {
        return projectClassLoader;
    }

    /**
     * プロジェクト用クラスローダを設定します。
     * @param projectClassLoader プロジェクト用クラスローダ
     */
    public void setProjectClassLoader(ClassLoader projectClassLoader) {
        this.projectClassLoader = projectClassLoader;
    }
    
    public File getTempFile(String relativePath) {
        return LimyQalabUtils.createTempFile(getMainProject(), relativePath);
    }

    // ------------------------ Private Methods

    /**
     * メインプロジェクトを返します。
     * @return メインプロジェクト
     */
    private IProject getMainProject() {
        return projects[0];
    }
   
    /**
     * ソースディレクトリ一覧を取得します。
     * @param enableRef 参照プロジェクトも対象に含めるか
     * @return ソースディレクトリ一覧
     * @throws JavaModelException Javaモデル例外
     */
    private Collection<IPath> privateGetSourcePaths(boolean enableRef)
            throws JavaModelException {
        
        Collection<IPath> results = new ArrayList<IPath>();
        IJavaProject project = getJavaProject();
        
        for (IJavaElement element : getAllJavaElements(project)) {
            if (enableRef
                    && getStore().getBoolean(LimyQalabConstants.ENABLE_REFPROJECT)) {
                results.add(element.getPath());
            } else {
                // 自プロジェクトのみ有効な場合
                if (element.getJavaProject().equals(project)) {
                    results.add(element.getPath());
                }
            }
        }
        return results;
    }

    /**
     * Javaプロジェクトの全ソースパス（参照プロジェクト含）を返します。
     * @param project Javaプロジェクト
     * @return 全ソースパス
     * @throws JavaModelException Javaモデル例外
     */
    private Collection<IJavaElement> getAllJavaElements(IJavaProject project)
            throws JavaModelException {
        
        Collection<IJavaElement> results = new ArrayList<IJavaElement>();
        
        IPackageFragmentRoot[] roots = project.getAllPackageFragmentRoots();
        
        // プロジェクトの全ソースパスおよび参照プロジェクトをループ
        for (IPackageFragmentRoot root : roots) {
            if (root.getKind() == IPackageFragmentRoot.K_SOURCE) {
                results.add(root);
            }
        }
        return results;
    }

    /**
     * パスがテスト用ディレクトリなのかどうかを判定します。
     * @param location パス
     * @return テスト用ディレクトリならば真
     */
    private boolean isTestPath(IPath location) {
        for (String segment : location.removeFirstSegments(1).segments()) {
            if ("test".equals(segment)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Javaプロジェクトの出力先ディレクトリ一覧を返します。
     * @param env 
     * @param enableRef 参照プロジェクトも対象に含めるか
     * @return Javaプロジェクトのソースディレクトリ一覧
     * @throws JavaModelException Javaモデル例外
     */
    private Collection<IPath> privateGetBinPaths(LimyQalabEnvironment env,
            boolean enableRef) throws JavaModelException {
        
        IJavaProject project = env.getJavaProject();

        Set<IPath> results = new HashSet<IPath>();
        
        if (enableRef
                && store.getBoolean(LimyQalabConstants.ENABLE_REFPROJECT)) {
            
            // 参照プロジェクトも有効な場合
            IPackageFragmentRoot[] roots = project.getAllPackageFragmentRoots();
            for (IPackageFragmentRoot refRoot : roots) {
                if (refRoot.getKind() == IPackageFragmentRoot.K_SOURCE) {
                    // 参照プロジェクト毎にループ
                    IJavaProject refProject = refRoot.getJavaProject();
                    LimyQalabUtils.appendProjectBinPaths(refProject, results);
                }
            }
        } else {
            // デフォルトのプロジェクト出力ディレクトリを追加
            LimyQalabUtils.appendProjectBinPaths(project, results);
        }
        
        return results;
    }

    /**
     * Javaプロジェクトのソースディレクトリ一覧を返します。
     * @param enableTestPath trueにするとテストパスのみを取得、falseにするとメインパスのみを取得
     * @return Javaプロジェクトのソースディレクトリ一覧
     * @throws CoreException
     */
    private Collection<IPath> getAllSourcePaths(boolean enableTestPath)
            throws CoreException {
        
        String enableProject = store.getString(LimyQalabConstants.SUB_PROJECT_NAMES);

        Collection<IPath> results = new ArrayList<IPath>();
        Collection<IPath> paths = getSourcePaths(true);
        for (IPath path : paths) {
            if (isTestPath(path) == enableTestPath) {
                String projectName = path.segment(0);
                // 自プロジェクトもしくは対象のサブプロジェクトのみcopyする
                if (projectName.equals(getMainProject().getName())
                        || enableProject.indexOf(projectName + "\n") >= 0) {

                    results.add(path);
                }
            }
        }
        return results;
    }

}
