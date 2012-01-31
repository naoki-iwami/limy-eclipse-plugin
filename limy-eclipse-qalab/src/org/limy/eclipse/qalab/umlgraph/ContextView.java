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

import java.io.IOException;
import java.util.regex.Pattern;

import org.limy.eclipse.qalab.umlgraph.javadoc.ClassDoc;
import org.limy.eclipse.qalab.umlgraph.javadoc.RootDoc;

/**
 * A view designed for UMLDoc, filters out everything that it's not directly
 * connected to the center class of the context.
 * <p>
 * As such, can be viewed as a simplified version of a {@linkplain View} using a
 * single {@linkplain ContextMatcher}, but provides some extra configuration
 * such as context highlighting and output path configuration (and it is
 * specified in code rather than in javadoc comments).
 * @author wolf
 * 
 */
public class ContextView implements OptionProvider {

    private ClassDoc cd;
    private ContextMatcher matcher;
    private Options globalOptions;
    private Options myGlobalOptions;
    private Options hideOptions;
    private Options centerOptions;
    private Options packageOptions;
    private static final String[] HIDE_OPTIONS = new String[] { "-hide" };

    public ContextView(String outputFolder, ClassDoc cd, RootDoc root, Options parent)
            throws IOException {
        this.cd = cd;
        String outputPath = cd.containingPackage().name().replace('.', '/') + "/" + cd.name()
                + ".dot";

        // setup options statically, so that we won't need to change them so
        // often
        this.globalOptions = parent.getGlobalOptions();
        
        this.packageOptions = parent.getGlobalOptions();  
        this.packageOptions.showQualified = false;

        this.myGlobalOptions = parent.getGlobalOptions();
        this.myGlobalOptions.setOption(new String[] { "-output", outputPath });
        this.myGlobalOptions.setOption(HIDE_OPTIONS);

        this.hideOptions = parent.getGlobalOptions();
        this.hideOptions.setOption(HIDE_OPTIONS);

        this.centerOptions = parent.getGlobalOptions();
        this.centerOptions.nodeFillColor = "lemonChiffon";
        this.centerOptions.showQualified = false;

        this.matcher = new ContextMatcher(root, Pattern.compile(cd.qualifiedName()),
                myGlobalOptions, true);

    }

    public void setContextCenter(ClassDoc contextCenter) {
        this.cd = contextCenter;
        String outputPath = cd.containingPackage().name().replace('.', '/') + "/" + cd.name()
                + ".dot";
        this.myGlobalOptions.setOption(new String[] { "-output", outputPath });
        matcher.setContextCenter(Pattern.compile(cd.toString()));
    }

    public String getDisplayName() {
        return "Context view for class " + cd;
    }

    public Options getGlobalOptions() {
        return myGlobalOptions;
    }

    public Options getOptionsFor(ClassDoc cd) {
        if (globalOptions.matchesHideExpression(cd.toString()) || !matcher.matches(cd)) {
            return hideOptions;
        } else if (cd.equals(this.cd)) {
            return centerOptions;
        } else if(cd.containingPackage().equals(this.cd.containingPackage())){
            return packageOptions;
        } else {
            return globalOptions;
        }
    }

    public Options getOptionsFor(String name) {
        if (!matcher.matches(name))
            return hideOptions;
        else if (name.equals(cd.name()))
            return centerOptions;
        else
            return globalOptions;
    }

    public void overrideForClass(Options opt, ClassDoc cd) {
        if (opt.matchesHideExpression(cd.toString()) || !matcher.matches(cd))
            opt.setOption(HIDE_OPTIONS);
        if (cd.equals(this.cd))
            opt.nodeFillColor = "lemonChiffon";
    }

    public void overrideForClass(Options opt, String className) {
        if (!matcher.matches(className))
            opt.setOption(HIDE_OPTIONS);
    }

}
