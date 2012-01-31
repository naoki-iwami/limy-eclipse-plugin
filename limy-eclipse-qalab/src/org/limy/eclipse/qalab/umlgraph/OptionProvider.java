/*
 * Created 2006/02/04
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
 * A factory class that builds Options object for general use or for a
 * specific class
 */
public interface OptionProvider {
    /**
     * Returns the options for the specified class.
     */
    public Options getOptionsFor(ClassDoc cd);

    /**
     * Returns the options for the specified class.
     */
    public Options getOptionsFor(String name);

    /**
     * Returns the global options (the class independent definition)
     */
    public Options getGlobalOptions();

    /**
     * Gets a base Options and applies the overrides for the specified class
     */
    public void overrideForClass(Options opt, ClassDoc cd);

    /**
     * Gets a base Options and applies the overrides for the specified class
     */
    public void overrideForClass(Options opt, String className);

    /**
     * Returns user displayable name for this option provider.
     * <p>Will be used to provide progress feedback on the console
     */
    public String getDisplayName();
}
