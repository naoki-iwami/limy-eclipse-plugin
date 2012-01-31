/*
 * Created 2007/01/06
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
package org.limy.eclipse.qalab;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.widgets.Display;
import org.limy.eclipse.common.LimyEclipsePluginUtils;

/**
 * QALab Plugin内で使用するイメージを定義したクラスです。
 * @author Naoki Iwami
 */
public final class LimyQalabPluginImages {

    // ------------------------ Constants

    /** failureイメージ */
    public static final String FAILURE = "failure";
    
    /** errorイメージ */
    public static final String ERROR = "error";

    /**
     * イメージレジストリのルートパス
     */
    private static final String CROOT = "";

    // ------------------------ Static Fields

    /** 
     * The image registry containing <code>Image</code>s.
     */
    private static ImageRegistry imageRegistry;
    
    /**
     * A table of all the <code>ImageDescriptor</code>s.
     */
    private static Map<String, ImageDescriptor> imageDescriptors;

    /**
     * イメージ格納ルートディレクトリ
     */
    private static URL iconBaseUrl;

    // ------------------------ Constructors
    
    /**
     * private constructor
     */
    private LimyQalabPluginImages() { }

    static {
        try {
            iconBaseUrl = new URL(LimyQalabPlugin.getDefault().getBundle().getEntry("/"),
                    "icons/");
        } catch (MalformedURLException e) {
            LimyEclipsePluginUtils.log(e);
        }
    }
    
    // ------------------------ Public Methods

    /**
     * イメージレジストリを初期化します。
     * @return 初期化されたイメージレジストリ
     */
    public static ImageRegistry initializeImageRegistry() {
        imageRegistry = new ImageRegistry(getStandardDisplay());
        imageDescriptors = new HashMap<String, ImageDescriptor>(30);
        declareImages();
        return imageRegistry;
    }
    
    // ------------------------ Private Methods

    /**
     * Displayを取得します。
     * @return Display
     */
    private static Display getStandardDisplay() {
        Display display = Display.getCurrent();
        if (display == null) {
            display = Display.getDefault();
        }
        return display;     
    }   

    /**
     * 全てのイメージをレジストリに登録します。
     */
    private static void declareImages() {
        declareRegistryImage(FAILURE, CROOT + "failure.png");
        declareRegistryImage(ERROR, CROOT + "error.png");
        declareRegistryImage("icons/checkstyle.png", CROOT + "checkstyle.png");
        declareRegistryImage("icons/pmd.png", CROOT + "pmd.png");
        declareRegistryImage("icons/pmd-cpd.png", CROOT + "pmd-cpd.png");
        declareRegistryImage("icons/findbugs.png", CROOT + "findbugs.png");
        declareRegistryImage("icons/cobertura.png", CROOT + "cobertura.png");
        declareRegistryImage("icons/javancss.png", CROOT + "javancss.png");
        declareRegistryImage("icons/jdepend.png", CROOT + "jdepend.png");
        declareRegistryImage("icons/umlgraph.png", CROOT + "umlgraph.png");
        declareRegistryImage("icons/java2html.png", CROOT + "java2html.png");
        declareRegistryImage("icons/todo.png", CROOT + "todo.png");
    }

    /**
     * イメージをレジストリに登録します。
     * @param key 登録キー
     * @param path 登録パス
     */
    private static void declareRegistryImage(String key, String path) {
        ImageDescriptor desc = ImageDescriptor.getMissingImageDescriptor();
        try {
            desc = ImageDescriptor.createFromURL(makeIconFileURL(path));
        } catch (MalformedURLException e) {
            LimyEclipsePluginUtils.log(e);
        }
        imageRegistry.put(key, desc);
        imageDescriptors.put(key, desc);
    }

    /**
     * イメージのURLを取得します。
     * @param iconPath イメージのパス
     * @return イメージのURL
     * @throws MalformedURLException
     */
    private static URL makeIconFileURL(String iconPath) throws MalformedURLException {
        if (iconBaseUrl == null) {
            throw new MalformedURLException();
        }
        return new URL(iconBaseUrl, iconPath);
    }

}
