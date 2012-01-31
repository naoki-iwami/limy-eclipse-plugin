/*
 * Created 2006/11/22
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
package org.limy.eclipse.qalab.parser;

import net.objectlab.qalab.parser.BaseStatMerge;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * JavaNCSS結果集計用クラスです。
 * @author Naoki Iwami
 */
public class JavaNCSSStatMerge extends BaseStatMerge {

    // ------------------------ Fields

    /** name属性処理中フラグ */
    private boolean processName;

    /** ccn属性処理中フラグ */
    private boolean processCcn;

    /** 現在処理しているFunction名 */
    private String currentName;

//    /** 現在処理しているccn値 */
//    private String currentCcn;

    // ------------------------ Implement Methods

    public String getType() {
        return "javancss";
    }

    // ------------------------ Override Methods

    public final void startElement(final String ignoreNamespace,
            final String localname, final String qualifiedname,
            final Attributes attrs)
            throws SAXException {
        
        String local = localname;
        if ("".equals(local)) {
            local = qualifiedname;
        }
        
        if ("name".equals(local)) {
            processName = true;
        }
        if ("ccn".equals(local)) {
            processCcn = true;
        }
        
    }
    
    public void characters(char[] ch, int start, int length)
            throws SAXException {
        
        if (processName) {
            currentName = new String(ch, start, length);
        }
        if (processCcn) {
            int ccn = Integer.parseInt(new String(ch, start, length));
            int index = currentName.lastIndexOf('.');
            if (ccn >= 10 && index >= 0) {
                String name = currentName.substring(0, index);
                setCurrentFile(name.replace('.', '_') + ".java");
                incrementFileCount(1);
                resetFileStatistics();
                addFileStatistics(1);
                addNewResults();
            }
        }
        
    }

    public final void endElement(final String ignoreNamespace,
            final String ignoreSimplename, final String qualifiedname)
            throws SAXException {
        
        if ("name".equals(qualifiedname)) {
            processName = false;
        }
        if ("ccn".equals(qualifiedname)) {
            processCcn = false;
        }
    }


}
