/*
 * Created 2008/08/23
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
package org.limy.eclipse.code.etc;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.context.Context;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Display;
import org.limy.eclipse.code.LimyCodePlugin;
import org.limy.eclipse.code.accessor.AccessorUtils;
import org.limy.eclipse.code.accessor.LimyClassObject;
import org.limy.eclipse.code.common.LimyFieldObject;
import org.limy.eclipse.common.LimyEclipsePluginUtils;
import org.limy.eclipse.common.jdt.LimyJavaUtils;
import org.limy.eclipse.common.ui.AbstractJavaElementAction;
import org.limy.velocity.VelocitySupport;

public class CreateIbatisXmlAction extends AbstractJavaElementAction {

    @Override
    protected void doAction(IJavaElement javaElement, IProgressMonitor monitor)
            throws CoreException {
        
        IType type = LimyJavaUtils.getPrimaryType(javaElement);
        LimyClassObject limyType = AccessorUtils.createLimyClass(type);

        Context context = new VelocityContext();

//        StringBuilder buff = new StringBuilder();
        context.put("fullQualifiedName", type.getFullyQualifiedName());
        String typeName = type.getElementName();
        context.put("aliasName", typeName.substring(0, 1).toLowerCase() + typeName.substring(1));
        
        Collection<Map<String, String>> fields = new ArrayList<Map<String, String>>();
        for (LimyFieldObject field : limyType.getFields()) {
            String name = field.getField().getElementName();
            Map<String, String> fieldMap = new HashMap<String, String>();
            fieldMap.put("name", name);
            
            StringBuilder dbNameBuff = new StringBuilder();
            for (int i = 0; i < name.length(); i++) {
                char c = name.charAt(i);
                if (Character.isUpperCase(c)) {
                    dbNameBuff.append('_');
                    dbNameBuff.append(c);
                } else {
                    dbNameBuff.append(Character.toUpperCase(c));
                }
            }
            fieldMap.put("dbName", dbNameBuff.toString());
            fields.add(fieldMap);
        }
        context.put("columns", fields);
        
        StringWriter out = new StringWriter();
        try {
            VelocitySupport.write(
                    new File(LimyCodePlugin.getDefault().getPluginRoot(),
                            "resource/ibatis.xml.vm").getAbsolutePath(),
                    context, out);
        } catch (IOException e) {
            LimyEclipsePluginUtils.log(e);
        }
        
//        System.out.println(out.toString());
        Clipboard clipboard = new Clipboard(new Display());
        clipboard.setContents(
                new Object[] { out.toString() },
                new Transfer[] { TextTransfer.getInstance() }
        );
        clipboard.dispose();
        
    }

}
