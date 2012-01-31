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

import org.limy.eclipse.qalab.umlgraph.javadoc.Parameter;
import org.limy.eclipse.qalab.umlgraph.javadoc.Type;

/**
 *
 * @author Naoki Iwami
 */
public class ParameterImpl implements Parameter {

    private final Type type;

    private final String name;

    /**
     * ParameterImplインスタンスを構築します。
     * @param type
     * @param name
     */
    public ParameterImpl(Type type, String name) {
        super();
        this.type = type;
        this.name = name;
    }

    
    public String name() {
        return name;
    }

    public Type type() {
        return type;
    }

}
