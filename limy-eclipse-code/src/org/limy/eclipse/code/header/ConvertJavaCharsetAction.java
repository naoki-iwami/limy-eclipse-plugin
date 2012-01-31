/*
 * Created 2005/11/29
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
import java.util.Arrays;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.limy.eclipse.code.LimyCodeConstants;
import org.limy.eclipse.code.LimyCodePlugin;
import org.limy.eclipse.common.LimyEclipsePluginUtils;
import org.limy.eclipse.common.io.LimyIOUtils;
import org.limy.eclipse.common.ui.AbstractResoueceAction;

/**
 * Javaファイルの文字コードを一括変換するアクションです。
 * @author Naoki Iwami
 */
public class ConvertJavaCharsetAction extends AbstractResoueceAction {

    // ------------------------ Fields

    /** 変換後の文字セット */
    private String charset;
    
    // ------------------------ Override Methods

    @Override
    protected boolean execBefore(IProgressMonitor monitor) {
        charset = inputCharset();
        return charset != null;
    }

    @Override
    protected void doAction(IResource resource, IProgressMonitor monitor)
            throws CoreException {
        
        resource.accept(new IResourceVisitor() {
            public boolean visit(IResource resource) throws CoreException {
                if (resource.getType() == IResource.FILE) {
                    try {
                        convertCharset((IFile)resource);
                    } catch (IOException e) {
                        LimyEclipsePluginUtils.log(e);
                    }
                }
                return true;
            }
            
        });
        resource.refreshLocal(IResource.DEPTH_INFINITE,
                new SubProgressMonitor(monitor, 1));
        
//
//        try {
//            convertAllFiles(monitor);
//            resource.refreshLocal(IResource.DEPTH_INFINITE,
//                    new SubProgressMonitor(monitor, 1));
//        } catch (IOException e) {
//            LimyEclipsePluginUtils.log(e);
//        }
        
    }
    
    // ------------------------ Private Methods

    /**
     * ダイアログを表示して入力された文字セットを返します。
     * @return 入力された文字セット
     */
    private String inputCharset() {
        InputDialog dialog = new InputDialog(getWindow().getShell(),
                LimyCodePlugin.getResourceString(
                        LimyCodeConstants.MES_CONV_TITLE),
                        LimyCodePlugin.getResourceString(
                                LimyCodeConstants.MES_CONV_DIALOG),
                                "UTF-8", null);

        if (dialog.open() != Window.OK) {
            return null;
        }
        return dialog.getValue();
    }

//    /**
//     * 選択された全ファイルの文字セットを変換します。
//     * @param monitor 遷移モニタ
//     * @throws CoreException コア例外
//     * @throws IOException I/O例外
//     */
//    private void convertAllFiles(IProgressMonitor monitor) throws CoreException, IOException {
//        
//        ISelection selection = getSelection();
//        if (selection instanceof IStructuredSelection) {
//            Object[] elements = ((IStructuredSelection)selection).toArray();
//            for (Object element : elements) {
//                convertCharsets(element, monitor);
//                if (element instanceof IResource) {
//                    IResource resource = (IResource)element;
//                    resource.refreshLocal(IResource.DEPTH_INFINITE,
//                            new SubProgressMonitor(monitor, 1));
//                }
//            }
//        }
//
//        // Java要素
//        Collection<IJavaElement> javaElements = getSelectedJavaElements();
//        for (IJavaElement javaElement : javaElements) {
//            IResource resource = javaElement.getResource();
//            if (resource != null) {
//                convertCharsets(javaElement);
//                resource.refreshLocal(IResource.DEPTH_INFINITE,
//                        new SubProgressMonitor(monitor, 1));
//            }
//        }
//        
//        monitor.done();
//
//    }
    
//    private void convertCharsets(Object element, IProgressMonitor monitor)
//            throws CoreException, IOException {
//        if (element instanceof IContainer) {
//            IContainer container = (IContainer)element;
//            IResource[] members = container.members();
//            for (IResource member : members) {
//                convertCharsets(member, monitor);
//            }
//        }
//        if (element instanceof IFile) {
//            IFile file = (IFile)element;
//            convertCharset(file);
//            
//        }
//    }

//    /**
//     * Javaリソース（またはフォルダ）の文字コードを変換します。
//     * @param javaElement Java要素
//     * @throws CoreException コア例外
//     */
//    private void convertCharsets(
//            IJavaElement javaElement)
//            throws CoreException {
//        
//        LimyJavaUtils.executeAllJavas(javaElement, new IJavaResourceVisitor() {
//            public boolean executeJavaElement(IJavaElement el) throws CoreException {
//                // Javaクラス、メソッド定義、フィールド定義
//                try {
//                    IResource resource = el.getResource();
//                    if (resource instanceof IFile) {
//                        convertCharset((IFile)resource);
//                    }
//                    return false;
//                } catch (IOException e) {
//                    LimyEclipsePluginUtils.log(e);
//                }
//                return true;
//            }
//        });
//    }

    /**
     * ファイルの文字コードを変換します。
     * @param file ファイル
     * @throws IOException I/O例外
     * @throws CoreException コア例外
     */
    private void convertCharset(IFile file) throws IOException, CoreException {
        
        String baseCharset = file.getCharset();
        FileInputStream in = new FileInputStream(file.getLocation().toFile());
        String orgContent;
        byte[] content;
        try {
            orgContent = LimyIOUtils.getContent(in, baseCharset);
        } finally {
            in.close();
        }

        content = orgContent.getBytes(charset);
        
        if (!Arrays.equals(content, orgContent.getBytes(baseCharset))) {
            FileOutputStream out = new FileOutputStream(file.getLocation().toFile());
            try {
                out.write(content);
            } finally {
                out.close();
            }
        }

        
    }

}
