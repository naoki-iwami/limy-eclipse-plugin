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

import org.limy.eclipse.qalab.umlgraph.javadoc.ClassDoc;
import org.limy.eclipse.qalab.umlgraph.javadoc.PackageDoc;
import org.limy.eclipse.qalab.umlgraph.javadoc.RootDoc;


/**
 * A view designed for UMLDoc, filters out everything that it's not contained in
 * the specified package.
 * <p>
 * As such, can be viewed as a simplified version of a {@linkplain View} using a
 * single {@linkplain ClassMatcher}, and provides some extra configuration such
 * as output path configuration (and it is specified in code rather than in
 * javadoc comments).
 * @author wolf
 * 
 */
public class PackageView implements OptionProvider {

    private PackageDoc pd;
    private OptionProvider parent;
    private ClassMatcher matcher;
    private String outputPath;

    public PackageView(String outputFolder, PackageDoc pd, RootDoc root, OptionProvider parent) {
        this.parent = parent;
        this.pd = pd;
        this.matcher = new PackageMatcher(pd);
        this.outputPath = pd.name().replace('.', '/') + "/" + pd.name() + ".dot";
    }

    public String getDisplayName() {
        return "Package view for package " + pd;
    }

    public Options getGlobalOptions() {
        Options go = parent.getGlobalOptions();

        go.setOption(new String[] { "-output", outputPath });
        go.setOption(new String[] { "-hide" });

        return go;
    }

    public Options getOptionsFor(ClassDoc cd) {
        Options go = parent.getGlobalOptions();
        overrideForClass(go, cd);
        return go;
    }

    public Options getOptionsFor(String name) {
        Options go = parent.getGlobalOptions();
        overrideForClass(go, name);
        return go;
    }

    public void overrideForClass(Options opt, ClassDoc cd) {
        opt.showQualified = false;
        if (!matcher.matches(cd) || parent.getGlobalOptions().matchesHideExpression(cd.name()))
            opt.setOption(new String[] { "-hide" });
    }

    public void overrideForClass(Options opt, String className) {
        opt.showQualified = false;
        if (!matcher.matches(className))
            opt.setOption(new String[] { "-hide" });
    }

}
