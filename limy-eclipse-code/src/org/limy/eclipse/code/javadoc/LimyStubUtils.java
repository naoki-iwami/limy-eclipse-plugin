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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.internal.corext.template.java.CodeTemplateContext;
import org.eclipse.jdt.internal.corext.template.java.CodeTemplateContextType;
import org.eclipse.jdt.internal.corext.util.JavaModelUtil;
import org.eclipse.jdt.internal.corext.util.Strings;
import org.eclipse.jdt.internal.ui.JavaUIStatus;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.TemplateBuffer;
import org.eclipse.jface.text.templates.TemplateException;
import org.eclipse.jface.text.templates.TemplateVariable;
import org.limy.eclipse.code.LimyCodeConstants;
import org.limy.eclipse.code.LimyCodePlugin;
import org.limy.eclipse.code.accessor.LimyClassObject;
import org.limy.eclipse.code.common.LimyFieldObject;
import org.limy.eclipse.common.LimyEclipsePluginUtils;

/**
 * スタブコード生成用のユーティリティクラスです。
 * @author Naoki Iwami
 * @see org.eclipse.jdt.internal.corext.codemanipulation.StubUtility
 */
public final class LimyStubUtils {
    
    //--------------------------------------------------------------- Constants

    /**
     * コードテンプレート用プレフィックス文字列
     */
    private static final String CODETEMPLATE_ID = "org.eclipse.jdt.ui.text.codetemplates.";

    /**
     * overrideコメントID
     */
    private static final String OVERRIDE_ID = CODETEMPLATE_ID + "overridecomment";
    
    /**
     * コンストラクタコメントID
     */
    private static final String CONSTRUCTOR_ID = CODETEMPLATE_ID
            + "constructorcomment";
    
    /**
     * メソッドコメントID
     */
    private static final String METHOD_ID = CODETEMPLATE_ID + "methodcomment";

    // -------------------------------- Static Fields
    
    /**
     * 設定ファイルの最終修正時刻
     */
    private static long lastModified;
    
    /**
     * 設定ファイルの内容
     */
    private static Map<String, String> paramDefaultTexts = new HashMap<String, String>();

    // ------------------------ Constructors
    
    /**
     * private constructor
     */
    private LimyStubUtils() { }
    
    // ------------------------ Public Methods
    
    /**
     * メソッドコメントを取得します。
     * @param method メソッド
     * @param overridden オーバーライドメソッド
     * @param lineDelimiter 行デリミタ
     * @param classObj クラスオブジェクト
     * @return メソッドコメント
     * @throws CoreException
     */
    public static String getMethodComment(IMethod method, IMethod overridden,
            String lineDelimiter, LimyClassObject classObj) throws CoreException {
        
        String retType = null;
        if (!method.isConstructor()) {
            retType = method.getReturnType();
        }

        return getMethodComment(method.getCompilationUnit(),
                method.getDeclaringType().getElementName(),
                method.getElementName(),
                method.getParameterTypes(), method.getParameterNames(),
                method.getExceptionTypes(),
                retType, overridden, lineDelimiter, classObj);
    }

    /**
     * @param cu
     * @param typeName
     * @param methodName
     * @param paramTypes
     * @param paramNames
     * @param excTypeSig
     * @param retTypeSig
     * @param overridden
     * @param lineDelimiter
     * @param classObj 
     * @return メソッドコメント
     * @throws CoreException
     */
    private static String getMethodComment(ICompilationUnit cu, String typeName,
            String methodName, 
            String[] paramTypes, String[] paramNames, 
            String[] excTypeSig,
            String retTypeSig, IMethod overridden, String lineDelimiter,
            LimyClassObject classObj) throws CoreException {
        String templateName = METHOD_ID;
        if (retTypeSig == null) {
            templateName = CONSTRUCTOR_ID;
        } else if (overridden != null) {
            templateName = OVERRIDE_ID;
        }
        Template template = JavaTemplateUtils.getCodeTemplate(templateName, cu.getJavaProject());
        if (template == null) {
            return null;
        }        
        CodeTemplateContext context = new CodeTemplateContext(
                template.getContextTypeId(), cu.getJavaProject(), lineDelimiter);
        context.setCompilationUnitVariables(cu);
        context.setVariable(CodeTemplateContextType.ENCLOSING_TYPE, typeName);
        context.setVariable(CodeTemplateContextType.ENCLOSING_METHOD, methodName);
                
        if (retTypeSig != null) {
            context.setVariable(CodeTemplateContextType.RETURN_TYPE,
                    Signature.toString(retTypeSig));
        }
        if (overridden != null) {
            // context.setVariable(CodeTemplateContextType.SEE_TAG, getSeeTag(overridden));
            // 3.1M6
            context.setVariable(CodeTemplateContextType.SEE_TO_OVERRIDDEN_TAG,
                    getSeeTag(overridden));
        }
        TemplateBuffer buffer;
        try {
            buffer = context.evaluate(template);
        } catch (BadLocationException e) {
            throw new CoreException(Status.CANCEL_STATUS);
        } catch (TemplateException e) {
            throw new CoreException(Status.CANCEL_STATUS);
        }
        if (buffer == null) {
            return null;
        }
        
        // 展開後のテンプレート文字列を取得
        String str = buffer.getString();
        
        if (containsOnlyWhitespaces(str)) {
            return null;
        }
        TemplateVariable position = findTagVariable(buffer);
        if (position == null) {
            return str;
        }
            
        IDocument textBuffer = new Document(str);
        String[] exceptionNames = new String[excTypeSig.length];
        for (int i = 0; i < excTypeSig.length; i++) {
            exceptionNames[i] = Signature.toString(excTypeSig[i]);
        }
        String returnType = retTypeSig != null ? Signature.toString(retTypeSig) : null;
        int[] tagOffsets = position.getOffsets();
        for (int i = tagOffsets.length - 1; i >= 0; i--) { // from last to first
            try {
                insertTag(textBuffer, tagOffsets[i], position.getLength(),
                        paramTypes, paramNames,
                        exceptionNames,
                        returnType, false, lineDelimiter, classObj);
            } catch (BadLocationException e) {
                throw new CoreException(JavaUIStatus.createError(IStatus.ERROR, e));
            }
        }
        return textBuffer.get();
    }
    
    // private methods

    /**
     * seeタグを生成します。
     * @param overridden オーバーライドメソッド
     * @return seeタグ文字列
     * @throws JavaModelException
     */
    private static String getSeeTag(IMethod overridden) throws JavaModelException {
        IType declaringType = overridden.getDeclaringType();
        StringBuffer buf = new StringBuffer();
        buf.append("@see "); //$NON-NLS-1$
        buf.append(declaringType.getFullyQualifiedName('.'));
        buf.append('#'); 
        buf.append(overridden.getElementName());
        buf.append('(');
        String[] paramTypes = overridden.getParameterTypes();
        for (int i = 0; i < paramTypes.length; i++) {
            if (i > 0) {
                buf.append(", "); //$NON-NLS-1$
            }
            String curr = paramTypes[i];
            buf.append(JavaModelUtil.getResolvedTypeName(curr, declaringType));
            int arrayCount = Signature.getArrayCount(curr);
            while (arrayCount > 0) {
                buf.append("[]"); //$NON-NLS-1$
                arrayCount--;
            }
        }
        buf.append(')');
        return buf.toString();
    }

    /**
     * タグ文字列を検索します。
     * @param buffer テンプレートバッファ
     * @return タグ文字列
     */
    private static TemplateVariable findTagVariable(TemplateBuffer buffer) {
        TemplateVariable[] positions = buffer.getVariables();
        for (int i = 0; i < positions.length; i++) {
            TemplateVariable curr = positions[i];
            if (CodeTemplateContextType.TAGS.equals(curr.getType())) {
                return curr;
            }
        }
        return null;        
    }

    /**
     * タグを挿入します。
     * @param textBuffer
     * @param offset
     * @param length
     * @param paramTypes
     * @param paramNames
     * @param exceptionNames
     * @param returnType
     * @param isDeprecated
     * @param lineDelimiter
     * @param classObj 
     * @throws BadLocationException
     */
    private static void insertTag(IDocument textBuffer, int offset, int length,
            String[] paramTypes, String[] paramNames,
            String[] exceptionNames,
            String returnType, boolean isDeprecated,
            String lineDelimiter, LimyClassObject classObj)
            throws BadLocationException {
        IRegion region = textBuffer.getLineInformationOfOffset(offset);
        if (region == null) {
            return;
        }
        String lineStart = textBuffer.get(region.getOffset(), offset - region.getOffset());
        
        StringBuffer buf = new StringBuffer(128);
        for (int i = 0; i < paramNames.length; i++) {
            if (buf.length() > 0) {
                buf.append(lineDelimiter); buf.append(lineStart);
            }
            buf.append("@param "); buf.append(paramNames[i]); //$NON-NLS-1$
            buf.append(' ');
            buf.append(getParamDefaultText(paramTypes[i], paramNames[i], classObj));
        }
        if (returnType != null && !returnType.equals("void")) { //$NON-NLS-1$
            if (buf.length() > 0) {
                buf.append(lineDelimiter); buf.append(lineStart);
            }
            buf.append("@return"); //$NON-NLS-1$
        }
        if (exceptionNames != null) {
            for (int i = 0; i < exceptionNames.length; i++) {
                if (buf.length() > 0) {
                    buf.append(lineDelimiter); buf.append(lineStart);
                }
                buf.append("@throws "); buf.append(exceptionNames[i]); //$NON-NLS-1$
                buf.append(' ');
                buf.append(getParamDefaultText(exceptionNames[i], null, classObj));
            }
        }        
        if (isDeprecated) {
            if (buf.length() > 0) {
                buf.append(lineDelimiter); buf.append(lineStart);
            }
            buf.append("@deprecated"); //$NON-NLS-1$
        }
        textBuffer.replace(offset, length, buf.toString());
    }

    /**
     * パラメータに関連したjavadoc文字列を返します。
     * @param paramType パラメータクラス（特殊書式）
     * @param paramName パラメータ名
     * @param classObj クラス情報
     * @return パラメータに関連したjavadoc文字列または空文字
     */
    private static String getParamDefaultText(String paramType, String paramName,
            LimyClassObject classObj) {
        initParamDefaultTexts();
        
//        for (Iterator it = paramDefaultTexts.keySet().iterator(); it.hasNext();) {

        for (String key : paramDefaultTexts.keySet()) {
            
            int index = key.indexOf('/');
            if (index >= 0) {
                if (key.substring(0, index).equals(paramType)
                        && key.substring(index + 1).equals(paramName)) {
                    return paramDefaultTexts.get(key);
                }
            } else {
                if (paramType.equals(key)) {
                    return paramDefaultTexts.get(key);
                }
            }
        }
        
        if (classObj != null) {
            for (LimyFieldObject field : classObj.getFields()) {
                if (paramName.equals(field.getField().getElementName())) {
                    return field.getComment();
                }
            }
        }
        return "";
        /**
         * パラメータクラスの書式
         * QPssParam;/ep=パラメータコンテナ
         * QMap;/params=画面設定パラメータ
         * QMap;/values=値マップ
         * QConnection;=DB接続
         * QFile;/outputDir=出力ディレクトリ
         * MessageHandlingException=予期せぬ例外
         */
    }
    
    /**
     * Javadoc生成支援情報を初期化します。
     * <p>
     * Preferenceで指定されたJavadoc生成支援XMLファイルを読み込み、
     * 初期時もしくはXMLファイルが更新されていたら情報を更新します。
     * </p>
     */
    private static void initParamDefaultTexts() {
        
        String path = LimyCodePlugin.getDefault().getPreferenceStore().getString(
                LimyCodeConstants.PREF_PROP_PATH);
        if (path == null) {
            return;
        }
        
        File file = new File(path);
//        File file = new File("C:/var/mylib/src/setting.properties");
        if (file.lastModified() > lastModified) {
            lastModified = file.lastModified();
            paramDefaultTexts.clear();
            
            FileInputStream in = null;
            try {
                in = new FileInputStream(file);
                ResourceBundle bundle = new PropertyResourceBundle(in);
                for (Enumeration<String> en = bundle.getKeys(); en.hasMoreElements();) {
                    String key = en.nextElement();
                    String value = bundle.getString(key);
                    paramDefaultTexts.put(key, value);
                }
                in.close();
            } catch (IOException e) {
                LimyEclipsePluginUtils.log(e);
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        LimyEclipsePluginUtils.log(e);
                    }
                }
            }
        }
    }
    
    private static boolean containsOnlyWhitespaces(String s) {
        int size = s.length();
        for(int i = 0; i < size; ++i) {
            if(!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

}
