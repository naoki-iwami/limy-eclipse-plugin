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

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.limy.eclipse.qalab.umlgraph.javadoc.ClassDoc;
import org.limy.eclipse.qalab.umlgraph.javadoc.PackageDoc;

/**
 *
 * @author Naoki Iwami
 */
public class PackageDocImpl extends DocImpl implements PackageDoc {
    
    // ------------------------ Constructors
    
    /**
     * PackgeDocImplインスタンスを構築します。
     * @param fragment
     */
    public PackageDocImpl(RootDocImpl root, IPackageFragment fragment) {
        super(root, fragment);
    }
    
    // ------------------------ Override Methods

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PackageDocImpl) {
            PackageDocImpl other = (PackageDocImpl)obj;
            return getFragment().equals(other.getFragment());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return getFragment().hashCode();
    }

    @Override
    public String name() {
        return getFragment().getElementName();
    }

    // ------------------------ Implement Methods

    public ClassDoc[] allClasses() {
        
        Collection<ClassDoc> results = new ArrayList<ClassDoc>();

        try {
            ICompilationUnit[] units = getFragment().getCompilationUnits();
            for (ICompilationUnit unit : units) {
                for (IType type : unit.getAllTypes()) {
                    results.add(new ClassDocImpl(getRoot(), type, null));
                }
            }
        } catch (JavaModelException e) {
            throw new JavaRuntimeException(e);
        }
        return results.toArray(new ClassDoc[results.size()]);
    }
    
    // ------------------------ Private Methods
    
    /**
     * @return
     */
    private IPackageFragment getFragment() {
        return (IPackageFragment)getElement();
    }


}
