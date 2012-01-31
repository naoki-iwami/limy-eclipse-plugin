/*
 * Created 2007/02/05
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
package org.limy.eclipse.qalab.editor;

import java.io.IOException;
import java.util.List;

import javancss.Javancss;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.text.ITextSelection;
import org.limy.eclipse.common.LimyEclipsePluginUtils;

/**
 * CCステータスバーに表示する内容を作成するクラスです。
 * @author Naoki Iwami
 */
public class CcStatusInfoCreator implements StatusInfoCreator {

    // ------------------------ Constants

    /** プリミティブ型のシグネチャ表現 */
    private static final char[] PRIM_SIGNATURES = "ZBCDFIJS".toCharArray();
    
    /** プリミティブ型のJava表現 */
    private static final String[] PRIM_TYPES = new String[] {
            "boolean", "byte", "char", "double", "float", "int", "long", "short",
    };

    // ------------------------ Fields

    /** javancssインスタンス */
    private final Javancss javancss;
    
    // ------------------------ Constructors
    
    /**
     * CcStatusInfoCreatorインスタンスを構築します。
     * @param javancss
     */
    public CcStatusInfoCreator(Javancss javancss) {
        super();
        this.javancss = javancss;
    }
    
    // ------------------------ Implement Methods

    public String create(ICompilationUnit cunit, ITextSelection textSelection) {
        try {
            // 現在選択中のフィールドを取得
            IJavaElement selectedElement = cunit.getElementAt(textSelection.getOffset());
            
            if (selectedElement != null) {
                return /*new MetricCalculator().*/getCcInfo(javancss, selectedElement);
            }
        } catch (JavaModelException e) {
            LimyEclipsePluginUtils.log(e);
        } catch (IOException e) {
            LimyEclipsePluginUtils.log(e);
        }
        return null;
    }
    
    // ------------------------ Private Methods

    public String getCcInfo(Javancss javancss,
            IJavaElement selectedElement) throws JavaModelException, IOException {
        
        if (!(selectedElement instanceof IMethod)) {
            return null;
        }
        
        IMethod selectedMethod = (IMethod)selectedElement;

        List<List<? extends Object>> functions = javancss.getFunctions();
        for (List<? extends Object> function : functions) {
            // org.limy.blog.BlogModelImpl.getRecentDiaryDatas(UserKey,int)
            String result = searchCcInfo(selectedMethod, function);
            if (result != null) {
                return result;
            }
        }
        return null;
    }
    
    // ------------------------ Private Methods

    private String searchCcInfo(IMethod selectedMethod, List<? extends Object> function) {
        
        String methodSignature = (String)function.get(0);
        String methodName;
        try {
            methodName = methodSignature.substring(
                    methodSignature.lastIndexOf('.') + 1, methodSignature.lastIndexOf('('));
        } catch (IndexOutOfBoundsException e) {
            LimyEclipsePluginUtils.log(
                    "IndexOutOfBoundsException : methodName = " + methodSignature);
            return null;
        }
        
        if (methodName.equals(selectedMethod.getElementName())) {
            
            String[] types = selectedMethod.getParameterTypes();
            String[] params = methodSignature.substring(
                    methodSignature.lastIndexOf('(') + 1, methodSignature.length() - 1)
                    .split(",");
            
            if (isTypesEquals(types, params)) {
                int cc = ((Integer)function.get(2)).intValue();
                if (cc >= 10) {
                    return "CC : " + cc + " !";
                } else {
                    return "cc : " + cc;
                }
            }
        }
        return null;
    }
    
    private boolean isTypesEquals(String[] signatureTypes, String[] javaTypes) {
        
        if (signatureTypes.length != javaTypes.length) {
            return false;
        }
        
        for (int i = 0; i < signatureTypes.length; i++) {
            if (!convJavaType(signatureTypes[i]).equals(javaTypes[i])) {
                return false;
            }
        }
        
        return true;
    }

    private String convJavaType(String signatureType) {
        
        StringBuilder dimension = new StringBuilder();
        int pos;
        for (pos = 0; pos < signatureType.length(); pos++) {
            if (signatureType.charAt(pos) == '[') {
                dimension.append("[]");
            } else {
                break;
            }
        }
        String dimensionStr = dimension.toString();
        
        for (int i = 0; i < PRIM_SIGNATURES.length; i++) {
            if (signatureType.charAt(pos) == PRIM_SIGNATURES[i]) {
                return PRIM_TYPES[i] + dimensionStr;
            }
        }
        
        if (signatureType.charAt(pos) == 'Q') {
            return signatureType.substring(pos + 1, signatureType.length() - 1) + dimensionStr;
        }
        
        return "";
    }

}
