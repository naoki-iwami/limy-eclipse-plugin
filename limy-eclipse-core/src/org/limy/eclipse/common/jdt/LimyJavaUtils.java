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
package org.limy.eclipse.common.jdt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IParent;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.corext.dom.TokenScanner;
import org.eclipse.jdt.internal.corext.util.CodeFormatterUtil;
import org.eclipse.jdt.internal.corext.util.MethodOverrideTester;
import org.eclipse.jdt.internal.corext.util.SuperTypeHierarchyCache;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * Java要素関連のユーティリティクラスです。
 * @author Naoki Iwami
 */
public final class LimyJavaUtils {
    
    /**
     * private constructor
     */
    private LimyJavaUtils() { }
    
    /**
     * javaElement に属する全てのJavaファイルに対して処理を実行します。
     * @param javaElement ルートJava要素
     * @param visitor IJavaResourceVisitor
     * @throws CoreException コア例外
     */
    public static void executeAllJavas(IJavaElement javaElement,
            IJavaResourceVisitor visitor)
            throws CoreException {
        
        Set<IJavaElement> results = new HashSet<IJavaElement>();
        appendAllJavas(results, javaElement);
        for (IJavaElement el : results) {
            visitor.executeJavaElement(el);
        }
    }

    /**
     * 選択された全てのJava要素を返します。
     * <p>
     * エディタ内 : IType, IMethod, IField のインスタンス一つ<br>
     * エレメント : IJavaProject, IPackageFragmentRoot, IPackageFragment,
     * ICompilationUnit のインスタンスを選択した分だけ<br>
     * </p>
     * @param window WorkbenchWindow
     * @param selection 選択範囲
     * @return 選択された全てのJava要素
     * @throws JavaModelException Javaモデル例外
     */
    public static List<IJavaElement> getSelectedJavaElements(
            IWorkbenchWindow window, ISelection selection) throws JavaModelException {

        List<IJavaElement> results = new ArrayList<IJavaElement>();

        if (selection instanceof ITextSelection) {
            // エディタ内に選択範囲があった場合、selection は空なので渡しても意味が無い
            
            // エディタで開いているファイルが表現するJavaクラスエレメント
            IEditorPart editor = window.getActivePage().getActiveEditor();
            IJavaElement javaElement = JavaUI.getEditorInputJavaElement(editor.getEditorInput());

            // エディタ内の選択範囲を取得
            ITextSelection textSelection = (ITextSelection)
                    ((ITextEditor)editor).getSelectionProvider().getSelection();

            if (javaElement instanceof ICompilationUnit) {
                ICompilationUnit cunit = (ICompilationUnit)javaElement;
                // 現在選択中のフィールドを取得
                IJavaElement selectedElement = cunit.getElementAt(textSelection.getOffset());
                if (selectedElement == null) {
                    // どこも選択されていない場合（class定義外のコメント部分など）
                    results.add(cunit); // Javaクラスそのものを選択したものと見なす
                } else {
                    results.add(selectedElement);
                }
            }
        }

        if (selection instanceof IStructuredSelection) {
            Object[] elements = ((IStructuredSelection)selection).toArray();
            for (Object element : elements) {
                if (element instanceof IJavaElement) {
                    results.add((IJavaElement)element);
                }
            }
        }
        
        return results;
    }

    /**
     * メンバの宣言位置を返します。
     * @param member メンバ
     * @param document ドキュメント
     * @return メンバの宣言位置
     * @throws CoreException コア例外
     */
    public static int getMemberStartOffset(IMember member, IDocument document)
            throws CoreException {
        
        int offset = member.getSourceRange().getOffset();
//        TokenScanner scanner = new TokenScanner(document);
        TokenScanner scanner = new TokenScanner(document, member.getJavaProject());
        return scanner.getNextStartOffset(offset, true);
    }
    
    /**
     * Javaクラスに含まれる全てのメンバを取得します。
     * @param cunit Javaクラス
     * @return Javaクラスに含まれる全てのメンバ
     * @throws JavaModelException Javaモデル例外
     */
    public static Collection<IJavaElement> getAllMembers(ICompilationUnit cunit)
            throws JavaModelException {
        
        Collection<IJavaElement> results = new ArrayList<IJavaElement>();
        for (IType type : cunit.getTypes()) {
            for (IJavaElement element : type.getChildren()) {
                results.add(element);
            }
        }
        return results;
    }
    
    /**
     * エディタのタブ幅を取得します。
     * @param project プロジェクト
     * @return タブ幅
     */
    public static int getTabWidth(IJavaProject project) {
        return CodeFormatterUtil.getTabWidth(project);
    }


    /**
     * @param method
     * @param declaringType
     * @return
     * @throws JavaModelException
     */
    public static IMethod isOverridden(
            IMethod method, IType declaringType) throws JavaModelException {
        
        ITypeHierarchy hierarchy = SuperTypeHierarchyCache.getTypeHierarchy(declaringType);
        MethodOverrideTester tester = new MethodOverrideTester(declaringType, hierarchy);
        return tester.findOverriddenMethod(method, true);
    }

    /**
     * javaElement に属する全てのJavaファイルを検索して results に格納します。
     * @param results 結果格納先
     * @param javaElement ルートJava要素
     * @param visitor IJavaResourceVisitor
     * @throws CoreException コア例外
     */
    public static void appendAllJavas(Collection<IJavaElement> results,
            IJavaElement javaElement) throws CoreException {
        
        if (javaElement == null || javaElement.getResource() == null) {
            // Jarエレメントの場合、resource = null となる
            return;
        }
        
        // Javaプロジェクト、ソースパス、Javaパッケージ
        appendForIParent(results, javaElement/*, visitor*/);
        
        // Javaパッケージのサブパッケージはここで取得
        if (javaElement instanceof IPackageFragment) {
            appendForIPackageFragment(results, (IPackageFragment)javaElement/*, visitor*/);
        }

        // Javaクラス、メソッド定義、フィールド定義
        int type = javaElement.getElementType();
        if (type == IJavaElement.IMPORT_DECLARATION
                || type == IJavaElement.PACKAGE_DECLARATION
                || type == IJavaElement.COMPILATION_UNIT
                || type == IJavaElement.TYPE
                || type == IJavaElement.METHOD
                || type == IJavaElement.FIELD) {
            
            results.add(javaElement);
        }
    }

    /**
     * 対象要素に含まれる全ITypeを返します。
     * @param targetElement 対象要素
     * @return 対象要素に含まれる全IType
     * @throws JavaModelException Javaモデル例外
     */
    public static IType[] getAllTypes(IJavaElement targetElement)
            throws JavaModelException {

        Collection<ICompilationUnit> units = new ArrayList<ICompilationUnit>();

        if (targetElement instanceof IPackageFragmentRoot) {
            IJavaElement[] elements = ((IPackageFragmentRoot)targetElement).getChildren();
            for (IJavaElement element : elements) {
                units.addAll(Arrays.asList(
                        ((IPackageFragment)element).getCompilationUnits()));
            }
        } else if (targetElement instanceof IPackageFragment) {
            units.addAll(Arrays.asList(
                    ((IPackageFragment)targetElement).getCompilationUnits()));
        } else if (targetElement instanceof ICompilationUnit) {
            units.add((ICompilationUnit)targetElement);
        } else if (targetElement instanceof IMember) {
            units.add(((IMember)targetElement).getCompilationUnit());
        } else if (targetElement instanceof IType) {
            units.add(((IType)targetElement).getCompilationUnit());
        }

        Collection<IType> types = new ArrayList<IType>();
        for (ICompilationUnit unit : units) {
            for (IType type : unit.getTypes()) {
                types.add(type);
            }
        }
        return types.toArray(new IType[types.size()]);
    }
    
    /**
     * Java要素内で最初に見つかったクラス（IType）を返します。
     * @param javaElement 
     * @return
     * @throws JavaModelException 
     */
    public static IType getPrimaryType(IJavaElement javaElement) throws JavaModelException {
        IType[] types = getAllTypes(javaElement);
        if (types.length > 0) {
            return types[0];
        }
        return null;
    }

    // ------------------------ Private Methods

    /**
     * IPackageFragmentに属する全てのJavaファイルを検索して results に格納します。
     * @param results 結果格納先
     * @param packageFragment IPackageFragment要素
     * @param visitor IJavaResourceVisitor
     * @throws CoreException コア例外
     */
    private static void appendForIPackageFragment(Collection<IJavaElement> results,
            IPackageFragment packageFragment/*, IJavaResourceVisitor visitor*/)
            throws CoreException {
        
        IPackageFragmentRoot parent = (IPackageFragmentRoot)packageFragment.getParent();
        
        String parentName = packageFragment.getElementName();
        if (parentName.length() > 0) {
            for (IJavaElement child : parent.getChildren()) {
                String childName = child.getElementName();
                
                if (childName.startsWith(parentName)
                        && childName.lastIndexOf('.') == parentName.length()) {
                    // 直下のサブパッケージだけ探す
                    appendAllJavas(results, child/*, visitor*/);
                }
            }
        }
    }

    /**
     * Java要素に属する全てのJavaファイルを検索して results に格納します。
     * @param results 結果格納先
     * @param javaElement Java要素
     * @throws CoreException コア例外
     */
    private static void appendForIParent(Collection<IJavaElement> results,
            IJavaElement javaElement) throws CoreException {
        
        int type = javaElement.getElementType();
        if (type == IJavaElement.JAVA_PROJECT
                || type == IJavaElement.PACKAGE_FRAGMENT_ROOT
                || type == IJavaElement.PACKAGE_FRAGMENT) {

            IParent parent = (IParent)javaElement;
            for (IJavaElement child : parent.getChildren()) {
                // Javaパッケージの場合、サブパッケージまでは取得できないので後に別途取得している
                appendAllJavas(results, child);
            }
        }
    }

}
