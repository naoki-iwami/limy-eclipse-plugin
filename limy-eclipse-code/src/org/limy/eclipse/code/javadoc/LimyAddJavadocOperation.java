/*
 * Created 2005/07/21
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
package org.limy.eclipse.code.javadoc;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.ITextFileBufferManager;
import org.eclipse.core.filebuffers.LocationKind;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.core.formatter.IndentManipulation;
import org.eclipse.jdt.ui.CodeGeneration;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.ui.progress.UIJob;
import org.limy.eclipse.code.accessor.LimyClassObject;
import org.limy.eclipse.code.common.LimyFieldObject;
import org.limy.eclipse.common.LimyEclipsePluginUtils;
import org.limy.eclipse.common.jdt.LimyJavaUtils;

/**
 * Javadoc生成ロジッククラスです。
 * @depend - - - LimyStubUtils
 * @depend - - - LimyJavaElementUtils
 * @author Naoki Iwami
 * @see org.eclipse.jdt.ui.actions.AddJavaDocStubAction
 * @see org.eclipse.jdt.internal.corext.codemanipulation.AddJavaDocStubOperation
 * @see org.eclipse.jdt.ui.CodeGeneration
 */
public class LimyAddJavadocOperation extends UIJob {

    // ------------------------ Fields

    /**
     * メンバ一覧
     */
    private IMember[] fMembers;
    
    // ------------------------ Constructors
    
    /**
     * LimyAddJavadocOperationインスタンスを構築します。
     * @param members メンバ一覧
     */
    public LimyAddJavadocOperation(IMember[] members) {
        super("");
        fMembers = members;
    }

    // ------------------------ Override Methods

    @Override
    public IStatus runInUIThread(IProgressMonitor monitor) {
        
        IProgressMonitor tmpMonitor = monitor;
        try {
            if (monitor == null) {
                tmpMonitor = new NullProgressMonitor();
            }
            
            if (fMembers.length > 0) {
                tmpMonitor.beginTask("Create Javadoc stub...", fMembers.length + 2);
                addJavadocComments(tmpMonitor);
            }
            
        } catch (CoreException e) {
            LimyEclipsePluginUtils.log(e);
        } finally {
            if (tmpMonitor != null) {
                tmpMonitor.done();
            }
        }
        return Status.OK_STATUS;
    }
    
    // ------------------------ Private Methods

    /**
     * Javadocコメントを追加します。
     * @param monitor 推移モニタ
     * @throws CoreException コア例外
     */
    private void addJavadocComments(IProgressMonitor monitor) throws CoreException {
        ICompilationUnit cu = fMembers[0].getCompilationUnit();
        
        ITextFileBufferManager manager = FileBuffers.getTextFileBufferManager();
        IPath path = cu.getPath();
        
        manager.connect(path, LocationKind.IFILE,
                new SubProgressMonitor(monitor, 1));
        try {
            // ソースドキュメントを取得
            IDocument document = manager.getTextFileBuffer(path, LocationKind.IFILE).getDocument();
            
            // 行デリミタ
            String lineDelim = TextUtilities.getDefaultLineDelimiter(document);
            
            MultiTextEdit edit = new MultiTextEdit();
            
            for (int i = 0; i < fMembers.length; i++) {
                IMember curr = fMembers[i];
                
                String indentedComment = createIndentedComment(cu, curr, document, lineDelim);
                int memberStartOffset = LimyJavaUtils.getMemberStartOffset(curr, document);
                edit.addChild(new InsertEdit(memberStartOffset, indentedComment));

                monitor.worked(1);
            }
            edit.apply(document); // apply all edits
        } catch (BadLocationException e) {
            LimyEclipsePluginUtils.log(e);
        } finally {
            manager.disconnect(path, LocationKind.IFILE, new SubProgressMonitor(monitor, 1));
        }
    }

    /**
     * コメント文字列をインデント化して返します。
     * @param javaElement Javaクラス
     * @param member メンバ
     * @param document ドキュメント
     * @param lineDelim デリミタ文字
     * @param comment 元コメント
     * @return インデント化された文字列
     * @throws BadLocationException ドキュメント例外
     * @throws CoreException コア例外
     */
    private String createIndentedComment(IJavaElement javaElement, IMember member,
            IDocument document, String lineDelim)
            throws BadLocationException, CoreException {
        
        // Javadocコメントを生成
        String comment = createComment(member, lineDelim);

        // プロジェクト固有のタブ幅を取得
        int tabWidth = LimyJavaUtils.getTabWidth(javaElement.getJavaProject());
        
        // メンバが定義された位置の行内容を取得
        IRegion region = document.getLineInformationOfOffset(
                LimyJavaUtils.getMemberStartOffset(member, document));
        String line = document.get(region.getOffset(), region.getLength());
        
        String indentString = IndentManipulation.extractIndentString(
                line, tabWidth, tabWidth);
        
        // コメントをインデント化
        return IndentManipulation.changeIndent(
                comment, 0, tabWidth, tabWidth, indentString, lineDelim);
    }

    /**
     * メンバに対応するJavadocコメントを生成して返します。
     * @param member メンバ
     * @param lineDelim デリミタ文字
     * @return メンバに対応するJavadocコメント
     * @throws CoreException コア例外
     */
    private String createComment(IMember member, String lineDelim) throws CoreException {
        
        String comment = null;
        switch (member.getElementType()) {
            case IJavaElement.TYPE:
                comment = createTypeComment((IType)member, lineDelim);
                break;
            case IJavaElement.FIELD:
                comment = createFieldComment((IField)member);    
                break;
            case IJavaElement.METHOD:
                comment = createMethodComment((IMethod)member, lineDelim);
                break;
            default:
                break;
        }
        
        if (comment == null) {
            // 適切なJavadocが生成できなかった場合、空のJavadoc定義を生成
            StringBuilder buf = new StringBuilder();
            buf.append("/**").append(lineDelim); //$NON-NLS-1$
            buf.append(" *").append(lineDelim); //$NON-NLS-1$
            buf.append(" */").append(lineDelim); //$NON-NLS-1$
            comment = buf.toString();
        } else {
            // 最後がデリミタ文字で終わっていなかったら追加する
            if (!comment.endsWith(lineDelim)) {
                comment = comment + lineDelim;
            }
        }
        return comment;
    }
    
    /**
     * タイプコメントを生成して返します。
     * @param type タイプ（Javaクラス）
     * @param lineDelimiter 行デリミタ
     * @return タイプコメント
     * @throws CoreException コア例外
     */
    private String createTypeComment(IType type, String lineDelimiter) throws CoreException {
        return CodeGeneration.getTypeComment(
                type.getCompilationUnit(), type.getTypeQualifiedName('.'), lineDelimiter);
    }

    /**
     * メソッドコメントを生成して返します。
     * @param method メソッド
     * @param lineDelimiter 行デリミタ
     * @return メソッドコメント
     * @throws CoreException コア例外
     */
    private String createMethodComment(IMethod method, String lineDelimiter) throws CoreException {
        IType declaringType = method.getDeclaringType();
        
        IMethod overridden = null;
        LimyClassObject obj = new LimyClassObject();
        if (method.isConstructor()) {
            IField[] fields = ((IType)method.getParent()).getFields();
            for (IField field : fields) {
                obj.addField(new LimyFieldObject(field));
            }
        } else {
            overridden = LimyJavaUtils.isOverridden(method, declaringType);
        }
        return LimyStubUtils.getMethodComment(method, overridden, lineDelimiter, obj);
    }

    /**
     * フィールドコメントを生成して返します。
     * @param field フィールド情報
     * @return フィールドコメント
     * @throws CoreException コア例外
     */
    private String createFieldComment(IField field) throws CoreException {
        String typeName = Signature.toString(field.getTypeSignature());
        String fieldName = field.getElementName();
        return CodeGeneration.getFieldComment(
                field.getCompilationUnit(), typeName, fieldName, String.valueOf('\n'));
    }
    
}
