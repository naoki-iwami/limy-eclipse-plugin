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
package org.limy.eclipse.qalab.outline.umlimage;

import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.limy.eclipse.common.LimyLineIterator;

/**
 * @author Naoki Iwami
 */
public final class UmlImageSourceSupport {

    /** Javadocコメントパターン */
    private static final Pattern PATTERN_JAVADOC = Pattern.compile("\\s*\\*\\s*([^\\s@].*)");

    /**
     * private constructor
     */
    private UmlImageSourceSupport() { }

    /**
     * ツールチップ文字列を生成します。
     * @param type JDTクラスインスタンス
     * @return 
     */
    public static String createTooltipText(IType type) {
        try {
            ISourceRange javadocRange = type.getJavadocRange();
            if (javadocRange != null) {
                String lines = type.getSource().substring(0, type.getJavadocRange().getLength());
                StringBuilder buff = new StringBuilder();
                for (Iterator<String> it = new LimyLineIterator(lines); it.hasNext();) {
                    String line = it.next();
                    Matcher matcher = PATTERN_JAVADOC.matcher(line);
                    if (matcher.matches()) {
                        buff.append(matcher.group(1)).append('\n');
                    }
                }
                return buff.toString();
            }
        } catch (JavaModelException e) {
            // do nothing
        }
        return null;
    }

}
