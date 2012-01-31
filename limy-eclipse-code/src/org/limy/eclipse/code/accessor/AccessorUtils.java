/*
 * Created 2005/09/13
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
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IParent;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.corext.codemanipulation.GetterSetterUtil;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEdit;
import org.limy.eclipse.code.LimyCodeConstants;
import org.limy.eclipse.code.common.LimyFieldObject;
import org.limy.eclipse.common.LimyEclipseConstants;

/**
 * アクセッサ（Getter/Setter）メソッド関連のユーティリティクラスです。
 * @depend - - - LimyClassObject
 * @depend - - - LimyFieldObject
 * @depend - - - AccessorCreater
 * @author Naoki Iwami
 */
public final class AccessorUtils {
    
    /**
     * private constructor
     */
    private AccessorUtils() { }
    
    /**
     * 既存アクセッサメソッドのJavadocを更新します。
     * @param element Javaクラス
     * @param monitor 遷移モニタ
     * @param edits 
     * @throws JavaModelException Javaモデル例外
     */
    public static void modifyAccessorJavadoc(IType element,
            IProgressMonitor monitor,
            Map<ICompilationUnit, Collection<TextEdit>> edits) throws JavaModelException {
        
        for (IField javaField : element.getFields()) {
            LimyFieldObject field = new LimyFieldObject(javaField);
            modifyGetterMethod(element, field, monitor, edits);
            modifySetterMethod(element, field, monitor, edits);
        }
    }
    
    /**
     * 指定したクラスにpublic属性のGetter/Setterメソッドを全生成します。
     * @param element Javaクラス
     * @param monitor 遷移モニタ
     * @throws JavaModelException Javaモデル例外
     */
    public static void createAllPublicMethods(IType element,
            IProgressMonitor monitor) throws JavaModelException {
        
        List<LimyFieldObject> fields = createLimyClass(element).getFields();
        
        for (int idx = 0; idx < fields.size(); idx++) {
            LimyFieldObject field = fields.get(idx);
            
            if (Flags.isStatic(field.getField().getFlags())) {
                continue;
            }

            // Getter/Setterメソッドを追加する位置を決定
            IJavaElement sibling = decideSibling(fields, idx);

            // Getter/Setterメソッドを追加
            appendGetterMethod(element, field, sibling, monitor);
            
            if (!Flags.isFinal(field.getField().getFlags())) {
                appendSetterMethod(element, field, sibling, monitor);
            }

        }
        
    }

    /**
     * 指定したフィールドにpublic属性のGetter/Setterメソッドを全生成します。
     * @param javaField Javaフィールド
     * @param sibling 生成箇所
     * @param monitor 遷移モニタ
     * @throws JavaModelException Javaモデル例外
     */
    public static void createPublicAccessor(IField javaField,
            IProgressMonitor monitor) throws JavaModelException {

        List<LimyFieldObject> fields = createLimyClass(
                javaField.getDeclaringType()).getFields();

        LimyFieldObject field = new LimyFieldObject(javaField);
        IType javaClass = (IType)javaField.getParent();
        
        if (Flags.isStatic(field.getField().getFlags())) {
            return;
        }

        // Getter/Setterメソッドを追加
        for (int i = 0; i < fields.size(); i++) {
            LimyFieldObject nowField = fields.get(i);
            if (nowField.getField().equals(javaField)) {
                IJavaElement sibling = decideSibling(fields, i);
                appendGetterMethod(javaClass, field, sibling, monitor);
                if (!Flags.isFinal(field.getField().getFlags())) {
                    appendSetterMethod(javaClass, field, sibling, monitor);
                }
                continue;
            }
        }
        
    }

    /**
     * 指定したフィールドにpublic属性のSetterメソッドを全生成します。
     * @param javaField Javaフィールド
     * @param monitor 遷移モニタ
     * @throws JavaModelException Javaモデル例外
     */
    public static void createPublicSetter(IField javaField,
            IProgressMonitor monitor) throws JavaModelException {
        
        List<LimyFieldObject> fields = createLimyClass(
                javaField.getDeclaringType()).getFields();

        IType javaClass = (IType)javaField.getParent();

        for (int i = 0; i < fields.size(); i++) {
            LimyFieldObject nowField = fields.get(i);
            if (nowField.getField().equals(javaField)) {
                IJavaElement sibling = decideSibling(fields, i);
                appendSetterMethod(javaClass, nowField, sibling, monitor);
            }
        }

    }

    /**
     * 指定したフィールドにpublic属性のGetterメソッドを全生成します。
     * @param javaField Javaフィールド
     * @param monitor 遷移モニタ
     * @throws JavaModelException Javaモデル例外
     */
    public static void createPublicGetter(IField javaField,
            IProgressMonitor monitor) throws JavaModelException {
        
        List<LimyFieldObject> fields = createLimyClass(
                javaField.getDeclaringType()).getFields();

        IType javaClass = (IType)javaField.getParent();

        for (int i = 0; i < fields.size(); i++) {
            LimyFieldObject nowField = fields.get(i);
            if (nowField.getField().equals(javaField)) {
                IJavaElement sibling = decideSibling(fields, i);
                appendGetterMethod(javaClass, nowField, sibling, monitor);
            }
        }

    }

    /**
     * フラグ値からアクセス識別子文字列を返します。
     * @param flags フラグ値
     * @return アクセス識別子文字列
     */
    public static String getFlagString(int flags) {
        String r = LimyEclipseConstants.EMPTY_STR;
        if ((flags & Flags.AccPrivate) != 0) {
            r = "private";
        }
        if ((flags & Flags.AccProtected) != 0) {
            r = "protected";
        }
        if ((flags & Flags.AccPublic) != 0) {
            r = LimyCodeConstants.FLAGSTR_PUBLIC;
        }
        return r;
    }

    /**
     * JavaクラスからLimyClassObjectを生成して返します。
     * @param element Javaクラス
     * @return 生成したLimyClassObject
     * @throws JavaModelException Javaモデル例外
     */
    public static LimyClassObject createLimyClass(IType element) throws JavaModelException {
        
        LimyClassObject limyClass = new LimyClassObject();
        IField[] fields = element.getFields();
        for (IField field : fields) {
            limyClass.addField(new LimyFieldObject(field));
        }
        return limyClass;
    }

    // ------------------------ Private Methods

    /**
     * 既存のGetterメソッドを修正します。
     * @param javaElement Javaクラス
     * @param field フィールド
     * @param monitor 遷移モニタ
     * @throws JavaModelException Javaモデル例外
     */
    private static void modifyGetterMethod(IType javaElement,
            LimyFieldObject field, IProgressMonitor monitor,
            Map<ICompilationUnit, Collection<TextEdit>> edits)
            throws JavaModelException {
        
        // 既存のGetterメソッドを取得
        IMethod getterMethod = GetterSetterUtil.getGetter(field.getField());
        if (getterMethod != null) {
            // 今回追加するGetterメソッドを作成（アクセス識別子は既存のものに合わせる）
            String getterContents = AccessorCreater.createGetterContents(
                    field.getField(),
                    field.getComment(),
                    field.getAnnotationHint(),
                    AccessorUtils.getFlagString(getterMethod.getFlags()),
                    getterMethod);
            
            // 既存のメソッド定義と今回作成したメソッド定義が異なっていたら、今回作成したメソッド定義に置き換え
            String getterSource = getterMethod.getSource();
            if (!generalizeStr(getterContents).equals(generalizeStr(getterSource))) {
                
                Collection<TextEdit> changes = edits.get(javaElement.getCompilationUnit());
                if (changes == null) {
                    changes = new ArrayList<TextEdit>();
                    edits.put(javaElement.getCompilationUnit(), changes);
                }
                ISourceRange range = getterMethod.getSourceRange();
                changes.add(new ReplaceEdit(range.getOffset(), range.getLength(), getterContents));
                
                javaElement.createMethod(getterContents,
                        getNextSibling(getterMethod), true, monitor);
                getterMethod.delete(true, monitor);
            }
        }
    }

    /**
     * 既存のSetterメソッドを修正します。
     * @param javaElement Javaクラス
     * @param field フィールド
     * @param monitor 遷移モニタ
     * @throws JavaModelException Javaモデル例外
     */
    private static void modifySetterMethod(IType javaElement,
            LimyFieldObject field, IProgressMonitor monitor,
            Map<ICompilationUnit, Collection<TextEdit>> edits)
            throws JavaModelException {
        
        // 既存のSetterメソッドを取得
        IMethod setterMethod = GetterSetterUtil.getSetter(field.getField());
        if (setterMethod != null) {
            // 今回追加するSetterメソッドを作成（アクセス識別子は既存のものに合わせる）
            String setterContents = AccessorCreater.createSetterContents(
                    field.getField(),
                    field.getComment(),
                    AccessorUtils.getFlagString(setterMethod.getFlags()),
                    setterMethod);
            
            // 既存のメソッド定義と今回作成したメソッド定義が異なっていたら、今回作成したメソッド定義に置き換え
            String setterSource = setterMethod.getSource();
            if (!generalizeStr(setterContents).equals(generalizeStr(setterSource))) {
                
                Collection<TextEdit> changes = edits.get(javaElement.getCompilationUnit());
                if (changes == null) {
                    changes = new ArrayList<TextEdit>();
                    edits.put(javaElement.getCompilationUnit(), changes);
                }
                ISourceRange range = setterMethod.getSourceRange();
                changes.add(new ReplaceEdit(range.getOffset(), range.getLength(), setterContents));

                javaElement.createMethod(setterContents,
                        getNextSibling(setterMethod), true, monitor);
                setterMethod.delete(true, monitor);
            }
        }
    }

    /**
     * Getterメソッドをクラスに追加します。
     * @param javaElement Javaクラス
     * @param field フィールド
     * @param sibling メソッドを挿入する場所
     * @param monitor 遷移モニタ
     * @throws JavaModelException Javaモデル例外
     */
    private static void appendGetterMethod(
            IType javaElement, LimyFieldObject field, IJavaElement sibling,
            IProgressMonitor monitor)
            throws JavaModelException {
        
        // 既存のGetterメソッドを取得
        IMethod getterMethod = GetterSetterUtil.getGetter(field.getField());
        
        // 今回追加するGetterメソッドを作成
        String getterContents = AccessorCreater.createGetterContents(
                field.getField(),
                field.getComment(),
                field.getAnnotationHint(),
                LimyCodeConstants.FLAGSTR_PUBLIC,
                getterMethod);
        
        // 既存のメソッド定義と今回作成したメソッド定義が異なっていたら、今回作成したメソッド定義に置き換え
        String getterSource = (getterMethod != null) ? getterMethod.getSource() : null;
        if (!generalizeStr(getterContents).equals(generalizeStr(getterSource))) {
            javaElement.createMethod(getterContents, sibling, true, monitor);
            if (getterMethod != null) {
                getterMethod.delete(true, monitor);
            }
        }
    }
    
    /**
     * Setterメソッドをクラスに追加します。
     * @param javaElement Javaクラス
     * @param field フィールド
     * @param sibling メソッドを挿入する場所
     * @param monitor 遷移モニタ
     * @throws JavaModelException Javaモデル例外
     */
    private static void appendSetterMethod(
            IType javaElement, LimyFieldObject field, IJavaElement sibling,
            IProgressMonitor monitor)
            throws JavaModelException {
        
        // 既存のSetterメソッドを取得
        IMethod setterMethod = GetterSetterUtil.getSetter(field.getField());
        
        // 今回追加するSetterメソッドを作成
        String setterContents = AccessorCreater.createSetterContents(
                field.getField(),
                field.getComment(),
                LimyCodeConstants.FLAGSTR_PUBLIC,
                setterMethod);
        
        // 既存のメソッド定義と今回作成したメソッド定義が異なっていたら、今回作成したメソッド定義に置き換え
        String setterSource = (setterMethod != null) ? setterMethod.getSource() : null;
        if (!generalizeStr(setterContents).equals(generalizeStr(setterSource))) {
            javaElement.createMethod(setterContents, sibling, true, monitor);
            if (setterMethod != null) {
                setterMethod.delete(true, monitor);
            }
        }
    }

    /**
     * 指定したフィールドに対応するGetterまたはSetterメソッドを返します。
     * <p>
     * 対応するメソッドが無い場合、その直後に定義されたGetterまたはSetterメソッドを返します。
     * </p>
     * @param fields フィールド一覧
     * @param idx 対象とするフィールドの一覧リスト内インデックス
     * @return
     * @throws JavaModelException Javaモデル例外
     */
    private static IJavaElement decideSibling(List<LimyFieldObject> fields, int idx)
            throws JavaModelException {
        
        IJavaElement sibling = null;
        
        // fields[(idx + 1) ... last]までループ
        for (int insIdx = idx + 1; insIdx < fields.size(); insIdx++) {
            LimyFieldObject field = fields.get(insIdx);
            
            // フィールドに対応するGetterがあればそれを返す
            IMethod getterMethod = GetterSetterUtil.getGetter(field.getField());
            if (getterMethod != null && getterMethod.exists()) {
                sibling = getterMethod;
                break;
            }
            
            // フィールドに対応するSetterがあればそれを返す
            IMethod setterMethod = GetterSetterUtil.getSetter(field.getField());
            if (setterMethod != null && setterMethod.exists()) {
                sibling = setterMethod;
                break;
            }
        }
        return sibling;
    }

    /**
     * 指定したエレメントの直後のエレメントを返します。
     * @param element エレメント
     * @return 直後のエレメント（指定したエレメントが最後だった場合、null）
     * @throws JavaModelException Javaモデル例外
     */
    private static IJavaElement getNextSibling(IJavaElement element)
            throws JavaModelException {
        IJavaElement parent = element.getParent();
        if (parent instanceof IParent) {
            IJavaElement[] children = ((IParent)parent).getChildren();
            for (int i = 0; i < children.length; i++) {
                if (children[i] == element) {
                    if (i < children.length - 1) {
                        return children[i + 1];
                    }
                    return null;
                }
            }
        }
        return null;
    }
    /**
     * 文字列を一般化して返します。
     * @param contents コンテンツ文字列
     * @return 一般化された文字列
     */
    private static String generalizeStr(String contents) {
        if (contents == null) {
            return null;
        }
        
        String r = contents;
        r = r.replaceAll("//.*", LimyEclipseConstants.EMPTY_STR);
        r = r.trim().replaceAll("\r\n", "\n").replaceAll("\\t", "    ");
        return r;
    }

}
