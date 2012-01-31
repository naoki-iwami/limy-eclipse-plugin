/*
 * Created 2006/06/11
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
import java.util.ArrayList;
import java.util.List;

import org.limy.eclipse.qalab.umlgraph.javadoc.ClassDoc;
import org.limy.eclipse.qalab.umlgraph.javadoc.Doc;
import org.limy.eclipse.qalab.umlgraph.javadoc.LanguageVersion;
import org.limy.eclipse.qalab.umlgraph.javadoc.RootDoc;

/**
 * Doclet API implementation
 * @depend - - - OptionProvider
 * @depend - - - Options
 * @depend - - - View
 * @depend - - - ClassGraph
 * @depend - - - Version
 *
 * @version $Revision: 1.75 $
 * @author <a href="http://www.spinellis.gr">Diomidis Spinellis</a>
 */
public class UmlGraph {
    /** Entry point */
    public static boolean start(RootDoc root) throws IOException {
        Options opt = buildOptions(root);
        root.printNotice("UMLGraph doclet version " + Version.VERSION + " started");

        View[] views = buildViews(opt, root, root);
        if(views == null)
            return false;
        if (views.length == 0) {
            buildGraph(root, opt, null);
        } else {
            for (int i = 0; i < views.length; i++) {
                buildGraph(root, views[i], null);
            }
        }

        return true;
    }

    /**
     * Creates the base Options object, that contains both the options specified on the command 
     * line and the ones specified in the UMLOptions class, if available.
     */
    public static Options buildOptions(RootDoc root) {
        Options opt = new Options();
        opt.setOptions(root.options());
        opt.setOptions(findUMLOptions(root));
        return opt;
    }
    
    private static ClassDoc findUMLOptions(RootDoc root) {
        ClassDoc[] classes = root.classes();
        for (ClassDoc cd : classes) {
            if(cd.name().equals("UMLOptions"))
                    return cd;
        }
        return null;
    }

    /**
     * Builds and outputs a single graph according to the view overrides
     */
    public static void buildGraph(RootDoc root, OptionProvider op, Doc contextDoc) throws IOException {
        Options opt = op.getGlobalOptions();
        root.printNotice("Building " + op.getDisplayName());
        ClassDoc[] classes = root.classes();

        ClassGraph c = new ClassGraph(root, op, contextDoc);
        c.prologue();
        for (int i = 0; i < classes.length; i++) {
            c.printClass(classes[i], true);
        }
        for (int i = 0; i < classes.length; i++) {
            c.printRelations(classes[i]);
        }
        if(opt.inferRelationships)
            c.printInferredRelations(classes);
        if(opt.inferDependencies)
            c.printInferredDependencies(classes);

        c.printExtraClasses(root);
        c.epilogue();
    }

    
    
    /**
     * Builds the views according to the parameters on the command line
     * @param opt The options
     * @param srcRootDoc The RootDoc for the source classes
     * @param viewRootDoc The RootDoc for the view classes (may be
     *                different, or may be the same as the srcRootDoc)
     */
    public static View[] buildViews(Options opt, RootDoc srcRootDoc, RootDoc viewRootDoc) {
        if (opt.viewName != null) {
            ClassDoc viewClass = viewRootDoc.classNamed(opt.viewName);
            if(viewClass == null) {
                System.out.println("View " + opt.viewName + " not found! Exiting without generating any output.");
                return null;
            }
            if(viewClass.tags("view").length == 0) {
                System.out.println(viewClass + " is not a view!");
                return null;
            }
            if(viewClass.isAbstract()) {
                System.out.println(viewClass + " is an abstract view, no output will be generated!");
                return null;
            }
            return new View[] { buildView(srcRootDoc, viewClass, opt) };
        } else if (opt.findViews) {
            List<View> views = new ArrayList<View>();
            ClassDoc[] classes = viewRootDoc.classes();
            
            // find view classes
            for (int i = 0; i < classes.length; i++) {
                if (classes[i].tags("view").length > 0 && !classes[i].isAbstract()) {
                    views.add(buildView(srcRootDoc, classes[i], opt));
                }
            }
            
            return views.toArray(new View[views.size()]);
        } else {
            return new View[0];
        }
    }

    /**
     * Builds a view along with its parent views, recursively 
     */
    private static View buildView(RootDoc root, ClassDoc viewClass, OptionProvider provider) {
        ClassDoc superClass = viewClass.superclass();
        if(superClass == null || superClass.tags("view").length == 0)
            return new View(root, viewClass, provider);
        
        return new View(root, viewClass, buildView(root, superClass, provider));
    }

    /** Option checking */
    public static int optionLength(String option) {
        return Options.optionLength(option);
    }

    /** Indicate the language version we support */
    public static LanguageVersion languageVersion() {
        return LanguageVersion.JAVA_1_5;
    }

    
}
