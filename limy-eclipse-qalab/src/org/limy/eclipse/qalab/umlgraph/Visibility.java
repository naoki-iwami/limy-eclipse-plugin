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

import org.limy.eclipse.qalab.umlgraph.javadoc.ProgramElementDoc;

/**
 * Enumerates the possible visibilities in a Java program. For brevity, package
 * private visibility is referred as PACKAGE.
 * @author wolf
 * 
 */
public enum Visibility {
    PRIVATE, PACKAGE, PROTECTED, PUBLIC;

    public static Visibility get(ProgramElementDoc doc) {
        if (doc.isPrivate())
            return PRIVATE;
        else if (doc.isPackagePrivate())
            return PACKAGE;
        else if (doc.isProtected())
            return PROTECTED;
        else
            return PUBLIC;

    }
}