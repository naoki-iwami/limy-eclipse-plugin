/*
 * Created 2007/02/15
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
package org.limy.eclipse.qalab.umlgraph.javadoc.impl;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;
import org.limy.eclipse.qalab.umlgraph.javadoc.MethodDoc;
import org.limy.eclipse.qalab.umlgraph.javadoc.Parameter;
import org.limy.eclipse.qalab.umlgraph.javadoc.Type;

/**
 *
 * @author Naoki Iwami
 */
public class MethodDocImpl extends MemberDocImpl implements MethodDoc {

    // ------------------------ Fields

    /** ASM解析情報 */
    private ClassAsmInfo info;

    // ------------------------ Constructors

    /**
     * MethodDocImplインスタンスを構築します。
     * @param root RootDoc
     * @param method JDT要素
     * @param info ASM解析情報
     */
    public MethodDocImpl(RootDocImpl root, IMethod method, ClassAsmInfo info) {
        super(root, method);
        this.info = info;
    }

    // ------------------------ Implement Methods

    public Type returnType() {
        IMethod method = (IMethod)getJdtElement();
        try {
            return new ClassType(getRoot(), method.getReturnType(), createReturnType(method));
        } catch (JavaModelException e) {
            throw new JavaRuntimeException(e);
        }
    }

    public Parameter[] parameters() {
        IMethod method = (IMethod)getJdtElement();
        String[] params = method.getParameterTypes();
        
        MethodExt methodExt = getMethodExt(method);
        
        String[] names = methodExt.getParamQualifiedNames();
        Parameter[] results = new Parameter[names.length];
        for (int i = 0; i < names.length; i++) {
            results[i] = new ParameterImpl(new ClassType(getRoot(), params[i], names[i]), names[i]);
        }
        return results;
    }

    // ------------------------ Private Methods

    /**
     * @param method
     * @return
     */
    private String createReturnType(IMethod method) {
        MethodExt ext = getMethodExt(method);
        if (ext != null) {
            return ext.getQualifiedTypeName();
        }
        return null;
    }

    /**
     * @param method
     * @return
     */
    private MethodExt getMethodExt(IMethod method) {
        for (MethodExt ext : info.getMethods()) {
            if (method.getElementName().equals(ext.getName())) {
                return ext;
            }
        }
        return null;
    }

}
