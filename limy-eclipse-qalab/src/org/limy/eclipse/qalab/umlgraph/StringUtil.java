/*
 * Created 2005/11/14
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
package org.limy.eclipse.qalab.umlgraph;

import java.util.ArrayList;

/**
 * String utility functions
 * @version $Revision: 1.58 $
 * @author <a href="http://www.spinellis.gr">Diomidis Spinellis</a>
 */
class StringUtil {
    /** Tokenize string s into an array */
    public static String[] tokenize(String s) {
        ArrayList<String> r = new ArrayList<String>();
        String remain = s;
        int n = 0, pos;

        remain = remain.trim();
        while (remain.length() > 0) {
            if (remain.startsWith("\"")) {
                // Field in quotes
                pos = remain.indexOf('"', 1);
                if (pos == -1)
                    break;
                r.add(remain.substring(1, pos));
                if (pos + 1 < remain.length())
                    pos++;
            } else {
                // Space-separated field
                pos = remain.indexOf(' ', 0);
                if (pos == -1) {
                    r.add(remain);
                    remain = "";
                } else
                    r.add(remain.substring(0, pos));
            }
            remain = remain.substring(pos + 1);
            remain = remain.trim();
            // - is used as a placeholder for empy fields
            if (r.get(n).equals("-"))
                r.set(n, "");
            n++;
        }
        return r.toArray(new String[0]);
    }

}
