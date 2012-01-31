/*
 * Created 2007/01/08
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
 *
 * @author Naoki Iwami
 */
public class PmdCpdStatMerge extends BaseStatMerge {

    public String getType() {
        return "pmd-cpd";
    }

    @Override
    public void startElement(String uri, String localName, String name,
            Attributes attributes) throws SAXException {
        
        String local = localName;

        if ("".equals(local)) {
            local = name;
        }

        if ("duplication".equals(local)) {
            // dupliation一つにつき1カウントとする（本家はlineカウントとするので、カウントが多過ぎる）
            addTotalStatistics(1);
            incrementFileCount(2);
            addNewResults();
        }
    }

}
