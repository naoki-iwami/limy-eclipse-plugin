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

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.limy.eclipse.qalab.umlgraph.javadoc.ClassDoc;
import org.limy.eclipse.qalab.umlgraph.javadoc.RootDoc;

/**
 * Matches classes that are directly connected to one of the classes matched by
 * the regual expression specified. The context center is computed by regex
 * lookup. Depending on the specified Options, inferred relations and
 * dependencies will be used as well.
 * <p>
 * This class needs to perform quite a bit of computations in order to gather
 * the network of class releationships, so you are allowed to reuse it should
 * you
 * @author wolf
 * 
 * @depend - - - DevNullWriter
 */
public class ContextMatcher implements ClassMatcher {
    ClassGraphHack cg;
    Pattern pattern;
    List<ClassDoc> matched;
    Set<String> visited = new HashSet<String>();
    Options opt;
    RootDoc root;
    boolean keepParentHide;

    /**
     * Builds the context matcher
     * @param root The root doc returned by JavaDoc
     * @param pattern The pattern that will match the "center" of this
     *                context
     * @param opt The options will be used to decide on inference
     * @param keepParentHide If true, parent option hide patterns will be
     *                preserved, so that classes hidden by the options won't
     *                be shown in the context
     * @param fullContext If true, all the classes related to the context
     *                center will be included, otherwise it will match only
     *                the classes referred with an outgoing relation from
     *                the context center
     * @throws IOException
     */
    public ContextMatcher(RootDoc root, Pattern pattern, Options options, boolean keepParentHide) throws IOException {
        this.pattern = pattern;
        this.root = root;
        this.keepParentHide = keepParentHide;
        opt = (Options) options.clone();
        opt.setOption(new String[] { "-!hide" });
        opt.setOption(new String[] { "-!attributes" });
        opt.setOption(new String[] { "-!operations" });
        this.cg = new ClassGraphHack(root, opt);

        setContextCenter(pattern);
    }

    /**
     * Can be used to setup a different pattern for this context matcher.
     * <p>
     * This can be used to speed up subsequent matching with the same global
     * options, since the class network informations will be reused.
     * @param pattern
     */
    public void setContextCenter(Pattern pattern) {
        // build up the classgraph printing the relations for all of the
        // classes that make up the "center" of this context
        this.pattern = pattern;
        matched = new ArrayList<ClassDoc>();
        for (ClassDoc cd : root.classes()) {
            if (pattern.matcher(cd.toString()).matches()) {
                matched.add(cd);
                addToGraph(cd);
            }
        }
    }

    /**
     * Adds the specified class to the internal class graph along with its
     * relations and depencies, eventually inferring them, according to the
     * Options specified for this matcher
     * @param cd
     */
    private void addToGraph(ClassDoc cd) {
        // avoid adding twice the same class, but don't rely on cg.getClassInfo
        // since there
        // are other ways to add a classInfor than printing the class
        if (visited.contains(cd.toString()))
            return;

        visited.add(cd.toString());
        cg.printClass(cd, false);
        cg.printRelations(cd);
        if (opt.inferRelationships) {
            cg.printInferredRelations(cd);
        }
        if (opt.inferDependencies) {
            cg.printInferredDependencies(cd);
        }
    }

    public boolean matches(ClassDoc cd) {
        if (keepParentHide && opt.matchesHideExpression(cd.toString()))
            return false;

        // if the class is matched, it's in by default.
        if (matched.contains(cd))
            return true;

        // otherwise, add the class to the graph and see if it's associated
        // with any of the matched classes using the classgraph hack
        addToGraph(cd);
        return matches(cd.toString());
    }

    public boolean matches(String name) {
        if (pattern.matcher(name).matches())
            return true;

        for (ClassDoc mcd : matched) {
            String mcName = mcd.toString();
            ClassInfo ciMatched = cg.getClassInfo(mcName);
            RelationPattern rp = ciMatched.getRelation(name);
            if (ciMatched != null && rp != null && opt.contextRelationPattern.matchesOne(rp))
                return true;
        }
        return false;
    }

    /**
     * A quick hack to compute class dependencies reusing ClassGraph but
     * without generating output. Will be removed once the ClassGraph class
     * will be split into two classes for graph computation and output
     * generation.
     * @author wolf
     * 
     */
    private static class ClassGraphHack extends ClassGraph {

        public ClassGraphHack(RootDoc root, OptionProvider optionProvider) throws IOException {
            super(root, optionProvider, null);
            prologue();
        }

        public void prologue() throws IOException {
            w = new PrintWriter(new DevNullWriter());
        }

    }

    /**
     * Simple dev/null imitation
     * @author wolf
     */
    private static class DevNullWriter extends Writer {

        public void write(char[] cbuf, int off, int len) throws IOException {
            // nothing to do
        }

        public void flush() throws IOException {
            // nothing to do
        }

        public void close() throws IOException {
            // nothing to do
        }

    }

}
