/*
 * Created 2007/02/06
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
package org.limy.eclipse.web.velocityeditor;

import org.limy.eclipse.common.LimyStoreColorProvider;
import org.limy.eclipse.core.LimyEclipsePlugin;
import org.limy.eclipse.web.LimyWebColorProvider;

/**
 * エディタで使用する各種プロバイダを管理するクラスです。
 * @author Naoki Iwami
 */
public final class ProviderManager {

    /** 唯一のインスタンス */
    private static ProviderManager instance = new ProviderManager();

    /**
     * カラープロバイダ
     */
    private LimyStoreColorProvider colorProvider;

    /**
     * コードスキャナ
     */
    private VelocityCodeScanner codeScanner;

    /**
     * private constructor
     */
    private ProviderManager() { }
    
    public static ProviderManager getInstance() {
        return instance;
    }

    /**
     * カラープロバイダを取得します。
     * @return カラープロバイダ
     */
    public LimyStoreColorProvider getColorProvider() {
        if (colorProvider == null) {
            colorProvider = new LimyWebColorProvider(
                    LimyEclipsePlugin.getDefault().getPreferenceStore());
        }
        return colorProvider;
    }

    /**
     * Velocityコードスキャナを取得します。
     * @return Velocityコードスキャナ
     */
    public VelocityCodeScanner getVelocityCodeScanner() {
        if (codeScanner == null) {
            codeScanner = new VelocityCodeScanner(getColorProvider());
        }
        return codeScanner;
    }
    
    /**
     * プロバイダ群を更新（初期化）します。
     */
    public void updateProviders() {
        colorProvider = new LimyWebColorProvider(
                LimyEclipsePlugin.getDefault().getPreferenceStore());
        codeScanner = new VelocityCodeScanner(getColorProvider());
//        colorProvider = null;
//        codeScanner = null;
    }


}
