/*
 * Created 2007/01/15
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
package org.limy.eclipse.code.accessor;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.ITextFileBufferManager;
import org.eclipse.core.filebuffers.LocationKind;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.texteditor.ITextEditor;
import org.limy.eclipse.common.LimyEclipsePluginUtils;
import org.limy.eclipse.common.jdt.AbstractUIAction;
import org.limy.eclipse.common.jdt.LimyJavaUtils;

/**
 * 選択したフィールドのアクセッサを作成します。
 * @depend - - - LimyJavaElementUtils
 * @author Naoki Iwami
 */
public class CreateAccessorAction extends AbstractUIAction {

    // ------------------------ Override Methods

    @Override
    public void doRun(ISelection selection, IProgressMonitor monitor)
            throws CoreException {
        
        if (selection instanceof ITextSelection) {
            // エディタ内に選択範囲があった場合、selection は空なので渡しても意味が無い
            try {
                createAccessorWithTextSelection(monitor);
            } catch (BadLocationException e) {
                LimyEclipsePluginUtils.log(e);
            }
        }
        // Package Explorer などで実行された場合は何もしない
        
    }

    // ------------------------ Protected Methods

    protected void createAccessors(Collection<IField> targets,
            IProgressMonitor monitor) throws JavaModelException {
        for (IField field : targets) {
            AccessorUtils.createPublicAccessor(field, monitor);
        }
    }

    // ------------------------ Private Methods

    /**
     * エディタ内で選択されているフィールドに対してアクセッサを生成します。
     * @param monitor 遷移モニタ
     * @throws CoreException コア例外
     * @throws BadLocationException 
     */
    private void createAccessorWithTextSelection(
            IProgressMonitor monitor) throws CoreException, BadLocationException {
        
        // エディタで開いているファイルが表現するJavaクラスエレメント
        IEditorPart editor = getWindow().getActivePage().getActiveEditor();
        IJavaElement javaElement = JavaUI.getEditorInputJavaElement(editor.getEditorInput());

        // エディタ内の選択範囲を取得
        ITextSelection textSelection = (ITextSelection)
                ((ITextEditor)editor).getSelectionProvider().getSelection();

        if (javaElement instanceof ICompilationUnit) {
            createAccessors((ICompilationUnit)javaElement, textSelection, monitor);
        }
        
    }

    /**
     * 選択した範囲の全フィールドに対してアクセッサメソッドを生成します。
     * @param cunit Javaクラス
     * @param textSelection エディタ内の選択範囲
     * @param monitor 遷移モニタ
     * @throws BadLocationException
     * @throws CoreException
     */
    private void createAccessors(ICompilationUnit cunit, ITextSelection textSelection,
            IProgressMonitor monitor)
            throws BadLocationException, CoreException {
        
        ITextFileBufferManager manager = FileBuffers.getTextFileBufferManager();
        IDocument document = manager.getTextFileBuffer(cunit.getPath(),
                LocationKind.IFILE).getDocument();
        
        int startPos = document.getLineOffset(textSelection.getStartLine());
        int endPos = document.getLineOffset(textSelection.getEndLine() + 1);

        ICompilationUnit workingCopy = cunit.getWorkingCopy(monitor);

        Collection<IField> targets = new ArrayList<IField>();
        for (IJavaElement element : LimyJavaUtils.getAllMembers(workingCopy)) {
            if (element instanceof IField) {
                IField field = (IField)element;
                int pos = LimyJavaUtils.getMemberStartOffset(field, document);
                if (startPos <= pos && pos <= endPos) {
                    targets.add(field);
                }
            }
        }

        createAccessors(targets, monitor);
        
        workingCopy.commitWorkingCopy(true, monitor);
        workingCopy.discardWorkingCopy();
    }

}
