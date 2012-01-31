/*
 * Created 2007/08/29
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
package org.limy.eclipse.qalab.common;


import jdepend.framework.PackageFilter;

/**
 * Findbugs用のパッケージフィルタークラスです。
 * @author Naoki Iwami
 */
public class QalabPackageFilter extends PackageFilter {

    /** 表示対象とするパッケージ名Prefix */
    private String prefix;

    public QalabPackageFilter(LimyQalabEnvironment env) {
        super();
        prefix = env.getStore().getString(LimyQalabConstants.KEY_JDEPEND_BASE);
    }

    @Override
    public boolean accept(String packageName) {
        boolean result = packageName.startsWith(prefix);
        if (result) {
            // パッケージが適合した場合、excludeによるフィルタリングを掛ける
            return super.accept(packageName);
        }
        return false; // パッケージが適合しない場合は常にfalse
    }
    
    

}
