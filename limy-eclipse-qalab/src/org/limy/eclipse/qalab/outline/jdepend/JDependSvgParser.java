/*
 * Created 2007/08/31
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
package org.limy.eclipse.qalab.outline.jdepend;

import java.io.IOException;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaElement;
import org.limy.eclipse.common.resource.LimyResourceUtils;
import org.limy.eclipse.qalab.common.ClickableXmlParser;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Naoki Iwami
 */
public class JDependSvgParser implements ClickableXmlParser {
    
    public String getQualifiedName(Element el) {
        String qualifiedName = null;
        NodeList childNodes = el.getParentNode().getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node child = childNodes.item(i);
            if ("title".equals(child.getNodeName())) {
                qualifiedName = child.getFirstChild().getNodeValue();
                break;
            }
        }
        return qualifiedName;
    }

    public String getTooltip(IJavaElement javaElement) {
        if (javaElement != null && javaElement.getResource() != null) {
            IFile file = LimyResourceUtils.newFile(
                    javaElement.getResource().getFullPath().append("package.html"));
            if (file.exists()) {
                // package.html ファイルがあったら、その内容をツールチップにセット
                return javaElement.getElementName() + "\n\n" + getHtmlContents(file);
            }
            return javaElement.getElementName();
        }
        return null;
    }
    
    // ------------------------ Private Methods
    
    private String getHtmlContents(IFile file) {
        try {
            String contents = IOUtils.toString(file.getContents(), file.getCharset());
            return Pattern.compile("<.*>", Pattern.MULTILINE).matcher(contents)
                    .replaceAll("").trim();
        } catch (IOException e) {
            return null;
        } catch (CoreException e) {
            return null;
        }
    }

}