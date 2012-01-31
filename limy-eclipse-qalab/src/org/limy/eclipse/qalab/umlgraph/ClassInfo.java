/*
 * Created 2006/07/02
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

import java.util.HashMap;
import java.util.Map;

/**
 * Class's dot-comaptible alias name (for fully qualified class names)
 * and printed information
 * @version $Revision: 1.60 $
 * @author <a href="http://www.spinellis.gr">Diomidis Spinellis</a>
 */
class ClassInfo {
    private static int classNumber;
    /** Alias name for the class */
    String name;
    /** True if the class class node has been printed */
    boolean nodePrinted;
    /** True if the class class node is hidden */
    boolean hidden;
    /** 
     * The list of classes that share a relation with this one. Contains
     * all the classes linked with a bi-directional relation , and the ones 
     * referred by a directed relation 
     */
    Map<String, RelationPattern> relatedClasses = new HashMap<String, RelationPattern>();

    ClassInfo(boolean p, boolean h) {
        nodePrinted = p;
        hidden = h;
        name = "c" + (new Integer(classNumber)).toString();
        classNumber++;
    }
    
    public void addRelation(String dest, RelationType rt, RelationDirection d) {
        RelationPattern ri = relatedClasses.get(dest);
        if(ri == null) {
            ri = new RelationPattern(RelationDirection.NONE);
            relatedClasses.put(dest, ri);
        }
        ri.addRelation(rt, d);
    }
    
    public RelationPattern getRelation(String dest) {
        return relatedClasses.get(dest);
    }

    /** Start numbering from zero. */
    public static void reset() {
        classNumber = 0;
    }

    
}

