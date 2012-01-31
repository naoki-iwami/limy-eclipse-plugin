/*
 * Created 2006/11/25
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
 * JUnit結果集計用クラスです。
 * @author Naoki Iwami
 */
public class JUnitStatMerge extends BaseStatMerge {

    // ------------------------ Fields

    /** 合計テスト数 */
    private int totalTest;
    
    /** 成功テスト数 */
    private int totalSuccess;
    
    // ------------------------ Implement Methods

    public String getType() {
        return "junit";
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
        
        if ("testsuite".equals(local)) {
            int errorNumber = Integer.parseInt(attrs.getValue("errors"));
            int failureNumber = Integer.parseInt(attrs.getValue("failures"));
            int testNumber = Integer.parseInt(attrs.getValue("tests"));
            int successRate = 0;
            if (testNumber > 0) {
                successRate = 100 * (testNumber - errorNumber - failureNumber) / testNumber;
            }
            
            String name = attrs.getValue("package") + "." + attrs.getValue("name");
            if (attrs.getValue("package").length() == 0) {
                name = attrs.getValue("name");
            }
            setCurrentFile(name.replace('.', '_') + ".java");
            incrementFileCount(1);
            resetFileStatistics();
            addFileStatistics(successRate);
            addNewResults();
            
            totalTest += testNumber;
            totalSuccess += testNumber - errorNumber - failureNumber;
            
        }
        
    }

    public int getTotalStatistics() {
        if (totalTest == 0) {
            return 0;
        }
        return 100 * totalSuccess / totalTest;
    }

}
