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

/**
 * A map from relation types to directions
 * @author wolf
 * 
 */
public class RelationPattern {
    /**
     * A map from RelationType (indexes) to Direction objects
     */
    RelationDirection[] directions;

    /**
     * Creates a new pattern using the same direction for every relation kind
     * @param defaultDirection The direction used to initialize this pattern
     */
    public RelationPattern(RelationDirection defaultDirection) {
        directions = new RelationDirection[RelationType.values().length];
        for (int i = 0; i < directions.length; i++) {
            directions[i] = defaultDirection;
        }
    }

    /**
     * Adds, eventually merging, a direction for the specified relation type
     * @param relationType
     * @param direction
     */
    public void addRelation(RelationType relationType, RelationDirection direction) {
        int idx = relationType.ordinal();
        directions[idx] = directions[idx].sum(direction);
    }

    /**
     * Returns true if this patterns matches at least the direction of one
     * of the relations in the other relation patterns. Matching is defined
     * by {@linkplain RelationDirection#contains(RelationDirection)}
     * @param relationPattern
     * @return
     */
    public boolean matchesOne(RelationPattern relationPattern) {
        for (int i = 0; i < directions.length; i++) {
            if (directions[i].contains(relationPattern.directions[i]))
                return true;
        }
        return false;
    }

}
