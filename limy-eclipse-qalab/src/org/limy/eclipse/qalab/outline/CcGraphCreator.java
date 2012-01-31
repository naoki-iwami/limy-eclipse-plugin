/*
 * Created 2007/02/26
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
package org.limy.eclipse.qalab.outline;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.context.Context;
import org.limy.common.ProcessUtils;
import org.limy.eclipse.qalab.LimyQalabPluginUtils;
import org.limy.eclipse.qalab.common.LimyQalabConstants;
import org.limy.eclipse.qalab.common.LimyQalabEnvironment;
import org.limy.eclipse.qalab.common.LimyQalabUtils;
import org.limy.eclipse.qalab.outline.asm.LineInfo;
import org.limy.eclipse.qalab.outline.asm.MethodInfo;
import org.limy.velocity.XmlToHtml;
import org.limy.velocity.XmlToHtmlImpl;

/**
 *
 * @author Naoki Iwami
 */
public final class CcGraphCreator {
    
    private static XmlToHtml xmlToHtml = new XmlToHtmlImpl();

    /**
     * private constructor
     */
    private CcGraphCreator() { }

    /**
     * @param env 
     * @param methodInfo
     * @return 
     * @throws IOException 
     */
    public static BasePopupImage make(LimyQalabEnvironment env, MethodInfo methodInfo)
            throws IOException {
        
        String styleFile = LimyQalabPluginUtils.getResourcePath("cc/index.vm");
        File output = LimyQalabUtils.createTempFile(env.getProject(), "cc.dot");
        
        List<LineInfo> lines = methodInfo.getLineInfos();  //createLines(methodInfo);

        BasePopupImage image = new BasePopupImage();
        int edges = lines.size();
        if (lines.size() == 1) {
            edges = 0;
        }
        Set<Integer> points = new HashSet<Integer>();
        for (LineInfo lineInfo : lines) {
            points.add(Integer.valueOf(lineInfo.getFrom()));
            points.add(Integer.valueOf(lineInfo.getTo()));
        }
        int nodes = points.size();
        image.setParam("edge", Integer.valueOf(edges));
        image.setParam("node", Integer.valueOf(nodes));
        image.setParam("cc", Integer.valueOf(edges - nodes + 2));

        Context context = new VelocityContext();
        context.put("lines", lines);
        context.put("enableSource", Boolean.TRUE);
//        context.put("enableSource", Boolean.valueOf(enableSource));
        
        xmlToHtml.createHtml(context, new File(styleFile), output, null);
        
        Writer out = new StringWriter();
        
        File pngFile = LimyQalabUtils.createTempFile(env.getProject(), "cc.png");
        File svgFile = LimyQalabUtils.createTempFile(env.getProject(), "cc.svg");

        ProcessUtils.execProgram(new File("."), out,
                env.getStore().getString(LimyQalabConstants.KEY_DOT_EXE),
                "-Tpng", "-o" + pngFile.getAbsolutePath(),
                output.getAbsolutePath());

        ProcessUtils.execProgram(new File("."), out,
                env.getStore().getString(LimyQalabConstants.KEY_DOT_EXE),
                "-Tsvg", "-o" + svgFile.getAbsolutePath(),
                output.getAbsolutePath());

        image.setImageFile(pngFile);
        image.setSvgImageFile(svgFile);
        
        return image;
    }
    
}
