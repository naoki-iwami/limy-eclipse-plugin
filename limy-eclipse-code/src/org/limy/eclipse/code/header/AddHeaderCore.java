/*
 * Created 2007/08/15
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
package org.limy.eclipse.code.header;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageDeclaration;
import org.eclipse.jdt.core.IType;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.limy.eclipse.code.LimyCodeConstants;
import org.limy.eclipse.code.LimyCodePlugin;
import org.limy.eclipse.common.LimyEclipsePluginUtils;
import org.limy.eclipse.common.io.LimyIOUtils;
import org.limy.eclipse.common.jdt.IJavaResourceVisitor;
import org.limy.eclipse.common.jdt.LimyJavaUtils;

/**
 * ファイルにヘッダ文字列を付加するクラスです。
 * @author Naoki Iwami
 */
public class AddHeaderCore {

    /** ヘッダ文字列テンプレート */
    private final String header;

    /** 可変オプション一覧 */
    private Map<String, String> options = new HashMap<String, String>();

    // ------------------------ Constructors

    /**
     * AddHeaderCore インスタンスを構築します。
     * @param header ヘッダ文字列テンプレート
     */
    public AddHeaderCore(String header) {
        this.header = header;
    }
    
    // ------------------------ Public Methods

    /**
     * Java要素のヘッダ行を書き換えます。
     * <p>パッケージやプロジェクトを指定した場合、それに含まれる全てのJavaファイルが対象になります。</p>
     * @param javaElement Java要素
     * @param monitor 
     * @throws CoreException 
     */
    public void changeResource(IJavaElement javaElement, IProgressMonitor monitor)
            throws CoreException {
        
        LimyJavaUtils.executeAllJavas(javaElement, new IJavaResourceVisitor() {
            public boolean executeJavaElement(IJavaElement el) throws CoreException {
                // Javaクラス
                initOptions(el);
                addHerderLine(el);
                return false;
            }
        });

        IResource resource = javaElement.getResource();
        if (resource != null) {
            resource.refreshLocal(IResource.DEPTH_INFINITE, monitor);
        }
        
    }

    /**
     * リソースのヘッダ行を書き換えます。
     * <p>フォルダやプロジェクトを指定した場合、それに含まれる全てのリソースが対象になります。</p>
     * @param resource リソース
     * @param monitor 
     * @throws CoreException 
     */
    public void changeResource(IResource resource, IProgressMonitor monitor)
            throws CoreException {
        
        resource.accept(new IResourceVisitor() {
            public boolean visit(IResource resource) throws CoreException {
                if (resource.getType() == IResource.FILE) {
                    initOptions(resource);
                    try {
                        addFileHeader((IFile)resource, header, options);
                    } catch (IOException e) {
                        LimyEclipsePluginUtils.log(e);
                    }
                }
                return true;
            }
            
        });
    }

    // ------------------------ Private Methods

    /**
     * 置き換えマッピングを初期化します。
     * @param javaElement Javaプロジェクト
     */
    private void initOptions(IJavaElement javaElement) {
        
        ScopedPreferenceStore store = new ScopedPreferenceStore(
                new ProjectScope(javaElement.getJavaProject().getProject()), 
                LimyCodePlugin.getDefault().getBundle().getSymbolicName());
        String projectName = store.getString(
                LimyCodeConstants.PREF_PROJECT_NAME);
        if (projectName.length() == 0) {
            projectName = javaElement.getJavaProject().getElementName();
        }
        
        options.put("project", projectName);
        options.put("project_name", projectName);
        options.put("file_name", javaElement.getResource().getName());
        if (javaElement instanceof IPackageDeclaration) {
            ICompilationUnit cunit = (ICompilationUnit)
                    ((IPackageDeclaration)javaElement).getParent();
            createTypeOptions(cunit.findPrimaryType());
        }
        if (javaElement instanceof ICompilationUnit) {
            ICompilationUnit cunit = (ICompilationUnit)javaElement;
            createTypeOptions(cunit.findPrimaryType());
        }
        if (javaElement instanceof IType) {
            createTypeOptions((IType)javaElement);
        }
        
        options.put("user", System.getProperty("user.name"));
    }

    /**
     * 置き換えマッピングを初期化します。
     * @param resource リソース
     * @param javaElement Javaプロジェクト
     */
    private void initOptions(IResource resource) {
        
        ScopedPreferenceStore store = new ScopedPreferenceStore(
                new ProjectScope(resource.getProject()), 
                LimyCodePlugin.getDefault().getBundle().getSymbolicName());
        String projectName = store.getString(
                LimyCodeConstants.PREF_PROJECT_NAME);
        if (projectName.length() == 0) {
            projectName = resource.getProject().getName();
        }
        
        options.put("project", projectName);
        options.put("project_name", projectName);
        options.put("file_name", resource.getName());
        
        options.put("user", System.getProperty("user.name"));
    }

    private void createTypeOptions(IType type) {
        if (type != null) {
            options.put("package_name",
                    type.getPackageFragment().getElementName());
            options.put("type_name", type.getElementName());
        }
    }

    /**
     * Javaリソース（またはフォルダ）にヘッダ文字列を追加します。
     * @param javaElement Java要素
     * @throws CoreException コア例外
     */
    private void addHerderLine(IJavaElement javaElement)
            throws CoreException {
        
        IResource resource = javaElement.getResource();
        if (resource instanceof IFile) {
            try {
                addFileHeader((IFile)resource, header, options);
            } catch (IOException e) {
                LimyEclipsePluginUtils.log(e);
            }
        }
    }
    
    /**
     * ファイルにヘッダ記述を追加します。
     * @param file 対象ファイル
     * @param header ヘッダ文字列
     * @param options 置き換えマッピング
     * @throws CoreException コア例外
     * @throws IOException I/O例外
     */
    private void addFileHeader(IFile file, String header,
            Map<String, String> options) throws CoreException, IOException {
        
        String charset = file.getCharset();
        FileInputStream in = new FileInputStream(file.getLocation().toFile());
        String orgContent;
        String content;
        try {
            orgContent = LimyIOUtils.getContent(in, charset);
        } finally {
            in.close();
        }
        
        content = VariableExpander.convertContent(orgContent, header, options);
        
        if (!content.equals(orgContent)) {
            FileOutputStream out = new FileOutputStream(file.getLocation().toFile());
            try {
                out.write(content.getBytes(charset));
            } finally {
                out.close();
            }
        }
    }

}
