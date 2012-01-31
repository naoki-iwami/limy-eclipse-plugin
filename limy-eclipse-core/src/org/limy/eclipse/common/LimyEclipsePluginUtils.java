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
package org.limy.eclipse.common;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.preference.IPreferenceStore;
import org.limy.eclipse.common.io.LimyIOUtils;

/**
 * Limy Eclipse Plugin共通のユーティリティクラスです。
 * @author Naoki Iwami
 * @depend - - - LimyEclipseConstants
 */
public final class LimyEclipsePluginUtils {
    
    /**
     * private constructor
     */
    private LimyEclipsePluginUtils() { }
    
    /**
     * プラグインのルートディレクトリを取得します。
     * @param plugin プラグイン
     * @return プラグインのルートディレクトリ
     */
    public static File getPluginRoot(Plugin plugin) {
        try {
            // 3.2
            URL url = FileLocator.toFileURL(plugin.getBundle().getEntry("/"));
            // 3.1
            // URL url = Platform.asLocalURL(plugin.getBundle().getEntry("/"));
            return new File(url.getFile());
        } catch (IOException e) {
            log(e);
        }
        return null;
    }

    /**
     * エラーログを出力します。
     * @param e 例外
     */
    public static void log(Throwable e) {
        log(LimyEclipseConstants.PLUGIN_ID, LimyEclipseConstants.INTERNAL_ERROR, e);
    }

    /**
     * エラーメッセージログを出力します。
     * @param message ログメッセージ
     */
    public static void log(String message) {
        log(LimyEclipseConstants.PLUGIN_ID, LimyEclipseConstants.INTERNAL_ERROR, message);
    }

    /**
     * Preferenceの値（Boolean型）を取得します。
     * @param store 
     * @param key Preference項目のキー文字列
     * @param defaultValue デフォルト値
     * @return Preference値
     */
    public static boolean getPreferenceBoolean(IPreferenceStore store,
            String key, boolean defaultValue) {
        String value = store.getString(key);
        if (value.length() == 0) {
            return defaultValue;
        }
        return Boolean.getBoolean(value);
    }

    /**
     * ファイルを読み込んで文字列を取得します。
     * @param plugin 
     * @param contentFilePath
     * @return ファイルの内容文字列
     */
    public static String loadContent(Plugin plugin, String contentFilePath) {
        try {
            // 3.2
            URL url = FileLocator.toFileURL(plugin.getBundle().getEntry("/"));
            return LimyIOUtils.getContent(new URL(url, contentFilePath).openStream());
        } catch (IOException e) {
            log(e);
            return null;
        }
    }
    // ------------------------ Private Methods

    /**
     * ログを出力します。
     * @param pluginId プラグインID
     * @param statusCode ステータスコード
     * @param e 出力する例外
     */
    private static void log(String pluginId, int statusCode, Throwable e) {
    
        ILog log = Platform.getLog(Platform.getBundle(pluginId));
        Status status = new Status(IStatus.ERROR, pluginId, statusCode, e.getMessage(), e);
        log.log(status);
    }

    /**
     * ログを出力します。
     * @param pluginId プラグインID
     * @param statusCode ステータスコード
     * @param message 出力するメッセージ
     */
    private static void log(String pluginId, int statusCode, String message) {
    
        ILog log = Platform.getLog(Platform.getBundle(pluginId));
        Status status = new Status(IStatus.ERROR, pluginId, statusCode, message, null);
        log.log(status);
    }

}
