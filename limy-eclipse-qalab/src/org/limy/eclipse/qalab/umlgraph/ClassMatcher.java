/*
 * Created 2006/01/28
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

import org.limy.eclipse.qalab.umlgraph.javadoc.ClassDoc;

/**
 * A ClassMatcher is used to check if a class definition matches a
 * specific condition. The nature of the condition is dependent on
 * the kind of matcher 
 * @author wolf
 */
public interface ClassMatcher {
    /**
     * Returns the options for the specified class. 
     */
    public boolean matches(ClassDoc cd);
    
    /**
     * Returns the options for the specified class. 
     */
    public boolean matches(String name);
}
