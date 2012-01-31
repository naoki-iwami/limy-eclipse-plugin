/*
 * Created 2006/08/11
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
package org.limy.eclipse.qalab.ant;

import java.io.File;
import java.io.FileNotFoundException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaProject;
import org.limy.eclipse.core.LimyEclipsePlugin;
import org.limy.eclipse.qalab.LimyQalabPluginUtils;
import org.limy.eclipse.qalab.QalabJarFileFinder;
import org.limy.eclipse.qalab.common.AntCreator;
import org.limy.eclipse.qalab.common.LimyQalabEnvironment;
import org.limy.eclipse.qalab.common.LimyQalabUtils;
import org.limy.xml.XmlElement;
import org.limy.xml.XmlUtils;


/**
 * Ant要素を生成する抽象基底クラスです。
 * @author Naoki Iwami
 * @version 1.0.0
 */
public abstract class AbstractAntCreator implements AntCreator {
    
    // ------------------------ Protected Methods

    /**
     * クラスパスに指定のファイルを追加します。
     * @param classEl classpath要素
     * @param fileName ファイル名
     * @throws FileNotFoundException jarファイルが見つからない場合
     */
    protected void addPathElement(XmlElement classEl, final String fileName)
            throws FileNotFoundException {
        
        createPathelement(classEl, getFileLocation(fileName));
    }

    
    /**
     * クラスパスに指定のjarファイルを追加します。
     * @param classEl classpath要素
     * @param prefix jarファイル名プレフィックス
     * @throws FileNotFoundException jarファイルが見つからない場合
     */
    protected void addPathElementPrefix(XmlElement classEl, final String prefix)
            throws FileNotFoundException {
        
        createPathelement(classEl, getPrefixFileLocation(prefix));
    }

    /**
     * クラスパスにPlugin内クラスへのパスを追加します。
     * @param classEl classpath要素
     */
    protected void addPathElementMyPackage(XmlElement classEl) {
        // 配布用
        createPathelement(classEl, LimyQalabPluginUtils.getPath(""));
        // 開発用
        createPathelement(classEl, LimyQalabPluginUtils.getPath("bin"));
    }

    /**
     * クラスパスにCoreパッケージへのパスを追加します。
     * @param classEl classpath要素
     */
    protected void addPathElementCorePackage(XmlElement classEl) {
        File root = LimyEclipsePlugin.getDefault().getPluginRoot();
        // 配布用
        createPathelement(classEl, root.getAbsolutePath());
        // 開発用
        createPathelement(classEl, new File(root, "bin").getAbsolutePath());
        // velocity-dep.jar
        createPathelement(classEl,
                new File(root, "resource/lib/velocity-dep-1.5.jar").getAbsolutePath());
//        createPathelement(classEl,
//                new File(root, "resource/lib/limy-core.jar").getAbsolutePath());
        
    }

    /**
     * pathelement要素を作成します。
     * @param parent 親要素
     * @param location ロケーション
     */
    protected void createPathelement(XmlElement parent, String location) {
        XmlElement pathEl = XmlUtils.createElement(parent, "pathelement");
        pathEl.setAttribute("location", location);
    }
    
    /**
     * VM用param要素を追加します。
     * @param parent 親要素（null可）
     * @param name param名
     * @param value param値
     */
    protected void addVmParam(XmlElement parent, String name, String value) {
        if (parent != null) {
            XmlElement paramEl = XmlUtils.createElement(parent, "param");
            paramEl.setAttribute("name", name);
            if (value != null) {
                paramEl.setAttribute("expression", value);
            }
        }
    }

    /**
     * VM用param要素（固定値"on"）を追加します。
     * @param parent 親要素（null可）
     * @param name param名
     */
    protected void addVmParam(XmlElement parent, String name) {
        if (parent != null) {
            XmlElement paramEl = XmlUtils.createElement(parent, "param");
            paramEl.setAttribute("name", name);
            paramEl.setAttribute("expression", "on");
        }
    }

    /**
     * param要素を追加します。
     * @param parent 親要素（null可）
     * @param name param名
     * @param value param値
     */
    protected void addParam(XmlElement parent, String name, String value) {
        if (parent != null) {
            XmlElement paramEl = XmlUtils.createElement(parent, "param");
            paramEl.setAttribute("name", name);
            if (value != null) {
                paramEl.setAttribute("value", value);
            }
        }
    }

    /**
     * target要素を生成します。
     * @param parent 親要素
     * @param name ターゲット名
     * @param depends 依存ターゲット
     * @return 生成したターゲット要素
     */
    protected XmlElement createTargetElement(
            XmlElement parent, String name, String depends) {
        
        XmlElement targetEl = XmlUtils.createElement(parent, "target");
        targetEl.setAttribute("name", name);
        targetEl.setAttribute("depends", depends);
        return targetEl;
    }

    /**
     * sysproperty要素を生成します。
     * @param parent 親要素
     * @param key key値
     * @param value value値
     */
    protected void createSyspropertyElement(
            XmlElement parent, String key, String value) {
        
        XmlElement el = XmlUtils.createElement(parent, "sysproperty");
        el.setAttribute("key", key);
        el.setAttribute("value", value);
    }

//    /**
//     * classpath要素を生成します。
//     * @param antVersion Antバージョン
//     * @param parent 親要素
//     * @param paths パス文字列一覧
//     */
//    protected void createClasspathElement(int antVersion,
//            XmlElement parent, Collection<String> paths) {
//        
//        switch (antVersion) {
//        case LimyQalabConstants.ANT_VERSION_16:
//            for (String path : paths) {
//                XmlElement classEl = XmlUtils.createElement(parent, "classpath");
//                classEl.addAttribute("location", path);
//            }
//            break;
//        case LimyQalabConstants.ANT_VERSION_17:
//            XmlElement classEl = XmlUtils.createElement(parent, "classpath");
//            for (String path : paths) {
//                createPathelement(classEl, path);
//            }
//            break;
//        default:
//            break;
//        }
//    }
    
    /**
     * classpath要素を生成します。
     * @param parent 親要素
     * @param refid 参照ID
     */
    protected void createClasspathElement(XmlElement parent, String refid) {
        XmlElement el = XmlUtils.createElement(parent, "classpath");
        el.setAttribute("refid", refid);

    }

    /**
     * taskdef要素を生成します。
     * @param root 親要素
     * @param name taskdef名
     * @param className Taskクラス名
     * @param classpathRef クラスパスID
     * @return 生成した要素
     */
    protected XmlElement createTaskdefElement(XmlElement root, String name,
            String className, String classpathRef) {
        
        XmlElement taskEl = XmlUtils.createElement(root, "taskdef");
        taskEl.setAttribute("name", name);
        taskEl.setAttribute("classname", className);
        if (classpathRef != null) {
            taskEl.setAttribute("classpathref", classpathRef);
        }
        return taskEl;
    }

    /**
     * vmstyle要素を生成します。
     * @param root 親要素
     * @param inFiles 入力XMLファイル一覧
     * @param out 出力ファイル
     * @param style パースするvmファイル（resourceからの相対パス）
     * @param toolClass ツールクラス名
     * @return 生成した要素
     */
    protected XmlElement createVmstyleElement(XmlElement root,
            String[] inFiles, String out, String style, String toolClass) {
        
        XmlElement styleEl = XmlUtils.createElement(root, "vmstyle");
        for (String file : inFiles) {
            createElement(styleEl, "infile", "value", file);
//            styleEl.setAttribute("in", file);
        }
        styleEl.setAttribute("out", out);
        styleEl.setAttribute("style", LimyQalabPluginUtils.getResourcePath(style));
        if (toolClass != null) {
            styleEl.setAttribute("toolClass", toolClass);
        }
        return styleEl;
    }

    /**
     * vmstyle要素を生成します。
     * @param root 親要素
     * @param in 入力XMLファイル
     * @param out 出力ファイル
     * @param style パースするvmファイル（resourceからの相対パス）
     * @return 生成した要素
     */
    protected XmlElement createVmstyleElement(XmlElement root,
            String in, String out, String style) {
        return createVmstyleElement(root, new String[] { in }, out, style, null);
    }

    /**
     * vmstyle要素を生成します。
     * @param root 親要素
     * @param out 出力ファイル
     * @param style パースするvmファイル（resourceからの相対パス）
     * @return 生成した要素
     */
    protected XmlElement createVmstyleElement(XmlElement root,
            String out, String style) {
        return createVmstyleElement(root, new String[0], out, style, null);
    }

    /**
     * vmstyle要素を生成します。
     * @param root 親要素
     * @param inFiles 入力XMLファイル一覧
     * @param out 出力ファイル
     * @param style パースするvmファイル（resourceからの相対パス）
     * @return 生成した要素
     */
    protected XmlElement createVmstyleElement(XmlElement root,
            String[] inFiles, String out, String style) {
        return createVmstyleElement(root, inFiles, out, style, null);
    }

    /**
     * @param parent
     * @param elementName
     * @return 
     */
    protected XmlElement createElement(XmlElement parent, String elementName) {
        
        return XmlUtils.createElement(parent, elementName);
    }
    
    /**
     * @param parent
     * @param elementName
     * @param attrName
     * @param attrValue
     * @return 
     */
    protected XmlElement createElement(XmlElement parent, String elementName,
            String attrName, String attrValue) {
        
        XmlElement element = XmlUtils.createElement(parent, elementName);
        element.setAttribute(attrName, attrValue);
        return element;
    }
    
    /**
     * 全Javaファイルを持つfilset要素を作成します。
     * @param parent 親要素
     */
    protected void createFilesetAllSrc(XmlElement parent) {
        XmlElement filesetEl = XmlUtils.createElement(parent, "fileset");
        filesetEl.setAttribute("dir", "${all.src.dir}");
        filesetEl.setAttribute("includes", "**/*.java");
    }

    /**
     * 全Javaファイルを持つfilset要素を作成します。QA対象外ソースも含めます。
     * @param env 環境
     * @param parent 親要素
     * @throws CoreException コア例外
     */
    protected void createFilesetFullSrc(LimyQalabEnvironment env, XmlElement parent)
            throws CoreException {
        
        IJavaProject project = env.getJavaProject();
        for (IPath path : env.getMainSourcePaths()) {
            XmlElement filesetEl = XmlUtils.createElement(parent, "fileset");
            filesetEl.setAttribute("dir", LimyQalabUtils.createFullPath(project, path));
            filesetEl.setAttribute("includes", "**/*.java");
        }
    }

    protected String getPrefixFileLocation(String prefix) throws FileNotFoundException {
        return new QalabJarFileFinder().getPrefixFileLocation(prefix);
    }

    protected String getFileLocation(String fileName) throws FileNotFoundException {
        return new QalabJarFileFinder().getFileLocation(fileName);
    }

    // ------------------------ Private Methods


}
