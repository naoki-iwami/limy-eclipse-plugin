/*
 * Created 2007/01/05
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
package org.limy.eclipse.qalab.mark;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;

import net.sourceforge.cobertura.instrument.CoberturaInstrument;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.limy.eclipse.common.LimyEclipsePluginUtils;
import org.limy.eclipse.common.jdt.LimyJavaUtils;
import org.limy.eclipse.common.resource.LimyResourceUtils;
import org.limy.eclipse.qalab.common.LimyQalabEnvironment;
import org.limy.eclipse.qalab.common.LimyQalabUtils;
import org.limy.eclipse.qalab.common.QalabResourceUtils;

/**
 * Cobertura用のマーカー作成クラスです。
 * @author Naoki Iwami
 */
public final class CoberturaMarkCreator implements MarkCreator {
    
    /** 唯一のインスタンス */
    private static CoberturaMarkCreator instance = new CoberturaMarkCreator();
    
    /**
     * private constructor
     */
    private CoberturaMarkCreator() {
    }
    
    public static CoberturaMarkCreator getInstance() {
        return instance;
    }
    
    // ------------------------ Implement Methods

    public String getName() {
        return "cobertura";
    }

    public boolean markJavaElement(LimyQalabEnvironment env,
            Collection<IJavaElement> elements, IProgressMonitor monitor) {
        
        File dataFile = LimyQalabUtils.createTempFile(env.getProject(), "cobertura.ser");
        System.setProperty("net.sourceforge.cobertura.datafile",
                dataFile.getAbsolutePath());
        dataFile.delete();
        
        try {
            
            IProject project = env.getProject();
            CoberturaInstrument obj = new CoberturaInstrument(dataFile);
            
            // Instrument対象となるJavaファイル一覧を取得
            IResource[] resources = getTargetJavaFiles(env, elements);
            
            // Classファイル用のリソースに変換
            IResource[] classResources = new IResource[resources.length];
            for (int i = 0; i < resources.length; i++) {
                classResources[i] = QalabResourceUtils.getClassResource(env,
                        resources[i]).getResource();
            }

            // Instrumentファイル（およびcobertura.ser）を作成
            obj.makeInstrument(project, classResources);
            
            // テストの実行
            new ExecuteUIJob(env, resources).schedule();
            
            return true;
        } catch (CoreException e) {
            LimyEclipsePluginUtils.log(e);
        }
        return false;
    }
    
    public boolean markResource(LimyQalabEnvironment env,
            IResource resource, IProgressMonitor monitor) {

        throw new UnsupportedOperationException("markResource");
//        return executeTest(env, resource);
    }

    public boolean markResourceTemporary(LimyQalabEnvironment env,
            IResource resource,
            IProgressMonitor monitor) {

        throw new UnsupportedOperationException("markResourceTemporary");
//        return executeTest(env, resource);
    }

    public boolean markResources(LimyQalabEnvironment env,
            Collection<IResource> allResources, IProgressMonitor monitor) {
        
        throw new UnsupportedOperationException("markResourceTemporary");
//        File dataFile = LimyQalabUtils.createTempFile(env.getProject(), "cobertura.ser");
//        System.setProperty("net.sourceforge.cobertura.datafile",
//                dataFile.getAbsolutePath());
//        dataFile.delete();
//        
//        try {
//            
//            IProject project = env.getProject();
//            CoberturaInstrument obj = new CoberturaInstrument(dataFile);
//            
//            // Instrument対象となるJavaファイル一覧を取得
//            IResource[] resources = getTargetJavaFiles(env, allResources);
//            
//            // Classファイル用のリソースに変換
//            IResource[] classResources = new IResource[resources.length];
//            for (int i = 0; i < resources.length; i++) {
//                classResources[i] = QalabResourceUtils.getClassResource(env,
//                        resources[i]).getResource();
//            }
//
//            // Instrumentファイル（およびcobertura.ser）を作成
//            obj.makeInstrument(project, classResources);
//            
//            // テストの実行
//            new ExecuteUIJob(env, resources).schedule();
//            
//            return true;
//        } catch (CoreException e) {
//            LimyEclipsePluginUtils.log(e);
//        }
//        return false;
    }
    
    // ------------------------ Private Methods

//    /**
//     * リソースに対応するテストを実行してマーカーを作成します。
//     * @param env 
//     * @param resource リソース
//     * @return 処理に成功したら true
//     */
//    private boolean executeTest(LimyQalabEnvironment env,
//            IResource resource) {
//        
//        File rootDir = LimyQalabPlugin.getDefault().getPluginRoot();
//        File dataFile = new File(rootDir, "cobertura.ser");
//        System.setProperty("net.sourceforge.cobertura.datafile",
//                dataFile.getAbsolutePath());
//        dataFile.delete();
//
//        if (QalabResourceUtils.isTestResource(resource)) {
//            // resource がテストファイルだった場合、それを実行する
//            try {
//                String className = LimyQalabUtils.getQualifiedClassName(env, resource);
//                // テストファイルに対応するメインJavaファイルを取得
//                IResource mainJavaResource = QalabResourceUtils.getJavaResource(
//                        env, className.substring(0, className.length() - 4),
//                        false);
//                
//                ResourceWithBasedir classResource = QalabResourceUtils.getClassResource(
//                        env, mainJavaResource);
//                if (classResource != null) {
//                    // 単一Javaファイルのinstrumentおよびcobertura.serを作成
//                    CoberturaInstrument obj = new CoberturaInstrument(dataFile);
//                    obj.makeInstrument(classResource.getResource());
//                }
//                
//                // メインJavaファイルのマーカーを削除する
//                mainJavaResource.deleteMarkers(
//                        LimyQalabMarker.DEFAULT_ID, true, IResource.DEPTH_ZERO);
//                addMarker(env, mainJavaResource);
//                return true;
//
//            } catch (CoreException e) {
//                LimyEclipsePluginUtils.log(e);
//            }
//            return false;
//        }
//
//        try {
//            ResourceWithBasedir classResource = QalabResourceUtils.getClassResource(
//                    env, resource);
//            if (classResource != null) {
//                // 単一Javaファイルのinstrumentおよびcobertura.serを作成
//                CoberturaInstrument obj = new CoberturaInstrument(dataFile);
//                obj.makeInstrument(classResource.getResource());
//            }
//
//            addMarker(env, resource);
//            return true;
//            
//        } catch (CoreException e) {
//            LimyEclipsePluginUtils.log(e);
//        }
//        return false;
//    }
//
//    /**
//     * Javaクラスのテストケースを実行してマーカーを作成します。
//     * @param env 
//     * @param mainJavaResource Javaクラス（メインクラス）
//     */
//    private void addMarker(LimyQalabEnvironment env,
//            IResource mainJavaResource) {
//        
//        new ExecuteUIJob(env, mainJavaResource).schedule();
//    }
    
//    /**
//     * Instrument対象となるJavaファイル一覧を返します。
//     * @param env 
//     * @param allResources リソース一覧
//     * @return Instrument対象となるJavaファイル一覧
//     * @throws CoreException コア例外
//     */
//    private IResource[] getTargetJavaFiles(LimyQalabEnvironment env,
//            Collection<IResource> allResources)
//            throws CoreException {
//        
//        Collection<IResource> results = new HashSet<IResource>();
//        
//        for (IResource resource : allResources) {
//            if (QalabResourceUtils.isTestResource(resource)) {
//                // テストファイルの場合、対応するメインファイルを取得
//                if (resource.getType() == IResource.FILE) {
//                    String name = QalabResourceUtils.getQualifiedMainClassName(env, resource);
//                    IType type = env.getJavaProject().findType(name);
//                    if (type != null) {
//                        results.add(type.getResource());
//                    }
//                } else {
//                    env.getJavaProject().findPackageFragment(resource.getFullPath());
////                    LimyQalabJavaUtils.getBinDirPath(el)
//                }
//            } else if (LimyQalabUtils.isIgnoreSource(env, resource.getFullPath())) {
//                // do nothing
//            } else {
//                results.add(resource);
//            }
//        }
//        
//        return results.toArray(new IResource[results.size()]);
//    }

    /**
     * Instrument対象となるJavaファイル一覧を返します。
     * @param env 
     * @param javaElements Java要素一覧
     * @return Instrument対象となるJavaファイル一覧
     * @throws CoreException コア例外
     */
    private IResource[] getTargetJavaFiles(LimyQalabEnvironment env,
            Collection<IJavaElement> javaElements)
            throws CoreException {
        
        Collection<IResource> results = new HashSet<IResource>();
        
        for (IJavaElement javaElement : javaElements) {
            IResource resource = javaElement.getResource();
            if (QalabResourceUtils.isTestResource(resource)) {
                // テストファイルの場合、対応するメインファイルを取得
                if (resource.getType() == IResource.FILE) {
                    String name = QalabResourceUtils.getQualifiedMainClassName(env, resource);
                    IType type = env.getJavaProject().findType(name);
                    if (type != null) {
                        results.add(type.getResource());
                    }
                } else if (javaElement.getElementType() == IJavaElement.PACKAGE_FRAGMENT) {
                    Collection<IPath> sourcePaths = env.getSourcePaths(true);
                    for (IPath path : sourcePaths) {
                        IFolder folder = LimyResourceUtils.newFolder(
                                path.append(javaElement.getElementName().replace('.', '/'))
                        );
                        if (folder.exists()) {
                            results.add(folder);
                        }
                    }
                }
            } else if (LimyQalabUtils.isIgnoreSource(env, resource.getFullPath())) {
                // QA対象外リソース
            } else {
                IType[] types = LimyJavaUtils.getAllTypes(javaElement);
                for (IType type : types) {
                    results.add(type.getResource());
                }
            }
        }
        
        return results.toArray(new IResource[results.size()]);
    }

}
