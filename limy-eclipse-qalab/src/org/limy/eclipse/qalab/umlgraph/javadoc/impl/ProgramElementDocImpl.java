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

import org.eclipse.jdt.core.IMember;
import org.limy.eclipse.qalab.umlgraph.javadoc.AnnotationDesc;
import org.limy.eclipse.qalab.umlgraph.javadoc.ClassDoc;
import org.limy.eclipse.qalab.umlgraph.javadoc.PackageDoc;
import org.limy.eclipse.qalab.umlgraph.javadoc.ProgramElementDoc;

/**
 * メンバを表すElementDocです。
 * @author Naoki Iwami
 */
public class ProgramElementDocImpl extends DocImpl implements ProgramElementDoc {
    
    // ------------------------ Fields

    /** JDT要素 */
    private final IMember type;
    
    // ------------------------ Constructors

    /**
     * ProgramElementDocImplインスタンスを構築します。
     * @param root RootDoc
     * @param element JDT要素
     */
    public ProgramElementDocImpl(RootDocImpl root, IMember element) {
        super(root, element);
        this.type = element;
    }

    // ------------------------ Implement Methods

    public PackageDoc containingPackage() {
        
        return new PackageDocImpl(getRoot(),
                type.getTypeRoot().findPrimaryType().getPackageFragment());
//        try {
//            IType[] types = type.getCompilationUnit().getTypes();
//            if (types.length > 0) {
//                return new PackageDocImpl(getRoot(), types[0].getPackageFragment());
//                
//            }
//        } catch (JavaModelException e) {
//            LimyEclipsePluginUtils.log(e);
//        }
//        LimyEclipsePluginUtils.log(type.getElementName() + " is illegal type!");
//        return null;
    }

    public boolean isAbstract() {
        return false; // not support
    }
    
    public AnnotationDesc[] annotations() {
        // not support
        return null;
    }

    public ClassDoc containingClass() {
        // not support
        return null;
    }

    public boolean isFinal() {
        // not support
        return false;
    }

    public boolean isPackagePrivate() {
        // not support
        return false;
    }

    public boolean isPrivate() {
        // not support
        return false;
    }

    public boolean isProtected() {
        // not support
        return false;
    }

    public boolean isPublic() {
        // not support
        return false;
    }

    public boolean isStatic() {
        // not support
        return false;
    }

    public int modifierSpecifier() {
        // not support
        return 0;
    }

    public String modifiers() {
        // not support
        return null;
    }

    // ------------------------ Protected Methods

    /**
     * typeを取得します。
     * @return type
     */
    protected IMember getJdtElement() {
        return type;
    }

}
