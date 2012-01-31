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
 * The possibile directions of a relation given a reference class (used in
 * context diagrams)
 */
public enum RelationDirection {
    NONE, IN, OUT, BOTH;

    /**
     * Adds the current direction
     * @param d
     * @return
     */
    public RelationDirection sum(RelationDirection d) {
        if (this == NONE)
            return d;

        if ((this == IN && d == OUT) || (this == OUT && d == IN) || this == BOTH || d == BOTH)
            return BOTH;
        return this;
    }

    /**
     * Returns true if this direction "contains" the specified one, that is,
     * either it's equal to it, or this direction is {@link #BOTH}
     * @param d
     * @return
     */
    public boolean contains(RelationDirection d) {
        if (this == BOTH)
            return true;
        else
            return d == this;
    }

    /**
     * Inverts the direction of the relation. Turns IN into OUT and vice-versa, NONE and BOTH
     * are not changed
     * @return
     */
    public RelationDirection inverse() {
        if (this == IN)
            return OUT;
        else if (this == OUT)
            return IN;
        else
            return this;
    }

};
