/*
 * Created 2007/08/14
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
package org.limy.velocity;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.context.Context;
import org.apache.velocity.exception.VelocityException;
import org.limy.velocity.xml.XmlContextUtils;
import org.limy.xml.XmlUtils;
import org.w3c.dom.Element;

/**
 * XML -> HTML の変換実装クラスです。
 * @author Naoki Iwami
 * @depend - - - LocalFileResourceLoader
 */
public class XmlToHtmlImpl implements XmlToHtml {

    
    // ------------------------ Override Methods

    public void createHtml(Collection<File> inXmls, File vmTemplate, File out,
            Collection<VmParam> params) throws IOException {
        
        // XMLファイルおよびパラメータからContextを生成
        Context context = new VelocityContext();
        for (File xmlFile : inXmls) {
            Element root = XmlUtils.parse(xmlFile);
            XmlContextUtils.addValue(context, root);
        }
        
        createHtml(context, vmTemplate, out, params);
    }

    public void createHtml(Context context, File vmTemplate, File out,
            Collection<VmParam> params) throws IOException {
        
        if (params != null) {
            for (VmParam param : params) {
                context.put(param.getName(), param.getExpression());
            }
        }
        
        FileWriter writer = new FileWriter(out);
        try {
            VelocitySupport.write(vmTemplate.getAbsolutePath(), context, writer);
//            write(vmTemplate, context, writer);
        } finally {
            writer.close();
        }
    }
    
    public void createHtml(String vmTemplateName, File out,
            Map<String, Object> innerValues, Map<String, Object> additionalValues)
            throws IOException {

        Map<String, Object> values = new HashMap<String, Object>();
        values.putAll(innerValues);
        values.putAll(additionalValues);
        Context context = new VelocityContext(values);
        
        FileWriter writer = new FileWriter(out);
        try {
            VelocitySupport.write(vmTemplateName, context, writer);
//            Template template = getEngine().getTemplate(vmTemplateName);
//            template.merge(context, writer);
        } catch (VelocityException e) {
            throw new IOException(e.getMessage());
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        } finally {
            writer.close();
        }

    }


}
