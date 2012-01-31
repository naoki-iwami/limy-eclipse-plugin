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
package org.limy.eclipse.qalab.umlgraph.javadoc;


/**
 *
 * @author Naoki Iwami
 */
public interface Doc extends Comparable<Object> {
    
    String name();

    /**
     * @param tagname
     * @return
     */
    Tag[] tags(String tagname);

    /**
     * @return
     */
    boolean isEnum();
    
    /**
     * @return
     */
    boolean isInterface();
    
    /**
     * @return
     */
    Tag[] tags();
    
    /**
     * @return
     */
    String commentText();

    int compareTo(Object obj);
    
    /**
     * @return
     */
    Tag[] firstSentenceTags();
    
    /**
     * @return
     */
    String getRawCommentText();

    /**
     * @return
     */
    Tag[] inlineTags();

    /**
     * @return
     */
    boolean isAnnotationType();
    
    /**
     * @return
     */
    boolean isAnnotationTypeElement();
    
    /**
     * @return
     */
    boolean isClass();

    /**
     * @return
     */
    boolean isConstructor();
    
    /**
     * @return
     */
    boolean isEnumConstant();
    /**
     * @return
     */
    boolean isError();

    /**
     * @return
     */
    boolean isException();
    
    /**
     * @return
     */
    boolean isField();
    
    /**
     * @return
     */
    boolean isIncluded();
    
    /**
     * @return
     */
    boolean isMethod();

    /**
     * @return
     */
    boolean isOrdinaryClass();
    
    /**
     * @return
     */
    SourcePosition position();
    
    /**
     * @return
     */
    SeeTag[] seeTags();

    /**
     * @param arg0
     */
    void setRawCommentText(String arg0);
    
}
