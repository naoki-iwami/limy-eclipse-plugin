/*
 * Created 2006/01/14
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
package org.limy.eclipse.web;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.graphics.RGB;
import org.limy.eclipse.common.LimyStoreColorProvider;

/**
 * Webモジュールで使用する色提供プロバイダクラスです。
 * @author Naoki Iwami
 */
public class LimyWebColorProvider extends LimyStoreColorProvider {

    /**
     * LimyWebColorProviderインスタンスを構築します。
     * @param store 
     */
    public LimyWebColorProvider(IPreferenceStore store) {
        super(store);
        addDefaultColor(LimyWebConstants.P_COLOR_COMMENT, new RGB(74, 175, 187));
        addDefaultColor(LimyWebConstants.P_COLOR_KEYWORD, new RGB(0, 0, 255));
        addDefaultColor(LimyWebConstants.P_COLOR_INNER, new RGB(216, 153, 39));
        addDefaultColor(LimyWebConstants.P_COLOR_PROPERTY, new RGB(216, 48, 15));
        addDefaultColor(LimyWebConstants.P_COLOR_TAG, new RGB(56, 155, 240));
    }
    
}
