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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.limy.eclipse.common.LimyLineIterator;
import org.limy.eclipse.qalab.umlgraph.javadoc.Doc;
import org.limy.eclipse.qalab.umlgraph.javadoc.SeeTag;
import org.limy.eclipse.qalab.umlgraph.javadoc.SourcePosition;
import org.limy.eclipse.qalab.umlgraph.javadoc.Tag;

/**
 *
 * @author Naoki Iwami
 */
public class DocImpl implements Doc {

    /** Javadocパターン */
    private static final Pattern PATTERN_JAVADOC = Pattern.compile(".*@(\\S*)\\s*(.*)");

    /** RootDoc */
    private final RootDocImpl root;

    /** JDT要素 */
    private final IJavaElement element;
    
    // ------------------------ Constructors

    /**
     * DocImplインスタンスを構築します。
     * @param root RootDoc
     * @param element JDT要素
     */
    public DocImpl(RootDocImpl root, IJavaElement element) {
        super();
        this.root = root;
        this.element = element;
    }

    // ------------------------ Implement Methods

    public String name() {
        return element.getElementName();
    }
    
    public Tag[] tags(String tagname) {
        Collection<Tag> results = new ArrayList<Tag>();
        if (element instanceof IMember) {
            IMember member = (IMember)element;
            try {
                ISourceRange range = member.getJavadocRange();
                if (range != null) {
                    
                    String source = member.getCompilationUnit().getSource();
                    String subSource = source.substring(
                            range.getOffset(), range.getOffset() + range.getLength());
                    
                    Iterator<String> it = new LimyLineIterator(subSource);
                    while (it.hasNext()) {
                        String line = it.next();
                        Matcher matcher = PATTERN_JAVADOC.matcher(line);
                        if (matcher.matches()) {
                            String name = matcher.group(1);
                            String text = matcher.group(2);
                            if (tagname.equals(name)) {
                                results.add(new TagImpl(name, text));
                            }
                        }
                    }
                }
                
            } catch (JavaModelException e) {
                throw new JavaRuntimeException(e);
            }
        }
        return results.toArray(new Tag[results.size()]);
    }

    public boolean isEnum() {
        if (element.getElementType() == IJavaElement.TYPE) {
            try {
                return ((IType)element).isEnum();
            } catch (JavaModelException e) {
                throw new JavaRuntimeException(e);
            }
        }
        return false;
    }

    public boolean isInterface() {
        if (element.getElementType() == IJavaElement.TYPE) {
            try {
                return ((IType)element).isInterface();
            } catch (JavaModelException e) {
                throw new JavaRuntimeException(e);
            }
        }
        return false;
    }
    
    
    // ------------------------ Not Supported Methods

    public String commentText() {
        // not support
        return null;
    }

    public int compareTo(Object obj) {
        // not support
        return 0;
    }

    public Tag[] firstSentenceTags() {
        // not support
        return null;
    }

    public String getRawCommentText() {
        // not support
        return null;
    }

    public Tag[] inlineTags() {
        // not support
        return null;
    }

    public boolean isAnnotationType() {
        // not support
        return false;
    }

    public boolean isAnnotationTypeElement() {
        // not support
        return false;
    }

    public boolean isClass() {
        // not support
        return false;
    }

    public boolean isConstructor() {
        // not support
        return false;
    }
    
    public boolean isEnumConstant() {
        // not support
        return false;
    }

    public boolean isError() {
        // not support
        return false;
    }

    public boolean isException() {
        // not support
        return false;
    }

    public boolean isField() {
        // not support
        return false;
    }

    public boolean isIncluded() {
        // not support
        return false;
    }


    public boolean isMethod() {
        // not support
        return false;
    }

    public boolean isOrdinaryClass() {
        // not support
        return false;
    }

    public SourcePosition position() {
        // not support
        return null;
    }

    public SeeTag[] seeTags() {
        // not support
        return null;
    }

    public void setRawCommentText(String arg0) {
        // not support
        
    }

    public Tag[] tags() {
        // not support
        return null;
    }

    // ------------------------ Protected Methods

    /**
     * rootを取得します。
     * @return root
     */
    protected RootDocImpl getRoot() {
        return root;
    }
    
    /**
     * elementを取得します。
     * @return element
     */
    protected IJavaElement getElement() {
        return element;
    }
    
}
