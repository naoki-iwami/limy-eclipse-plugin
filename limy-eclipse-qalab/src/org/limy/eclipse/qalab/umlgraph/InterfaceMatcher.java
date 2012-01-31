/*
 * Created 2007/08/30
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
package org.limy.eclipse.qalab.umlgraph;

import java.util.regex.Pattern;

import org.limy.eclipse.qalab.umlgraph.javadoc.ClassDoc;
import org.limy.eclipse.qalab.umlgraph.javadoc.RootDoc;

/**
 * Matches every class that implements (directly or indirectly) an
 * interfaces matched by regular expression provided.
 */
public class InterfaceMatcher implements ClassMatcher {

    protected RootDoc root;
    protected Pattern pattern;

    public InterfaceMatcher(RootDoc root, Pattern pattern) {
        this.root = root;
        this.pattern = pattern;
    }

    public boolean matches(ClassDoc cd) {
        // if it's the interface we're looking for, match
        if(cd.isInterface() && pattern.matcher(cd.toString()).matches())
            return true;
        
        // for each interface, recurse, since classes and interfaces 
        // are treated the same in the doclet API
        for (ClassDoc iface : cd.interfaces()) {
            if(matches(iface))
                return true;
        }
        
        // recurse on supeclass, if available
        if(cd.superclass() != null)
            return matches(cd.superclass());
        
        return false;
    }

    public boolean matches(String name) {
        ClassDoc cd = root.classNamed(name);
        if(cd == null)
            return false;
        return matches(cd);
    }

}
