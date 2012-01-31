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

import java.io.FileNotFoundException;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.preference.IPreferenceStore;
import org.limy.eclipse.qalab.LimyQalabPluginUtils;
import org.limy.eclipse.qalab.common.AntCreator;
import org.limy.eclipse.qalab.common.LimyQalabConstants;
import org.limy.eclipse.qalab.common.LimyQalabEnvironment;
import org.limy.xml.XmlElement;
import org.limy.xml.XmlUtils;


/**
 * Qalab用のAnt要素を生成するクラスです。
 * @author Naoki Iwami
 */
public class QalabCreator extends AbstractAntCreator {

    // ------------------------ Implement Methods
    
    public void exec(XmlElement root, LimyQalabEnvironment env)
            throws FileNotFoundException, JavaModelException {
        
        throw new IllegalStateException("Don't support this method!!");
    }
    
    public String[] getQalabClassNames() {
        return new String[0];
    }

    public String[] getReportXmlNames() {
        return new String[0];
    }
    
    public int getSummaryGroup() {
        return 4;
    }

    public String[] getSummaryTypes() {
        return new String[0];
    }
    
    public String getTargetName() {
        return "qalab";
    }

    public boolean isEnable(IPreferenceStore store) {
        throw new UnsupportedOperationException();
    }

    public AntCreator newInstance() {
        throw new UnsupportedOperationException();
    }

    // ------------------------ Public Methods

    /**
     * @param root XMLルート要素
     * @param env 
     * @param enableCreators 有効なQAツール一覧
     * @throws FileNotFoundException 必要なファイルが見つからない場合
     */
    public void exec(XmlElement root, LimyQalabEnvironment env,
            AntCreator[] enableCreators) throws FileNotFoundException {
        
        createTaskdef(root);
        createMainTarget(root, enableCreators);
        createChart0Target(root, env, enableCreators);
        createChart1Target(root, env, enableCreators);

        createReportTarget(root, env, enableCreators);
        
    }
    
    // ------------------------ Private Methods

    /**
     * taskdef要素を作成します。
     * @param root ルート要素
     * @throws FileNotFoundException 必要なファイルが見つからない場合
     */
    private void createTaskdef(XmlElement root) throws FileNotFoundException {
        
        XmlElement pathEl = XmlUtils.createElement(root, "path");
        pathEl.setAttribute("id", "qalab.classpath");

        addPathElementMyPackage(pathEl);
        addPathElementPrefix(pathEl, "jcommon");
        addPathElementPrefix(pathEl, "jfreechart");
        addPathElement(pathEl, "qalab.jar");
//        addPathElement(pathEl, "xercesImpl.jar");
        
        createTaskdefElement(root, "mergestat",
                "net.objectlab.qalab.ant.BuildStatMergeTask", "qalab.classpath");
        createTaskdefElement(root, "buildchart",
                "net.objectlab.qalab.ant.BuildStatChartTask", "qalab.classpath");
        createTaskdefElement(root, "QALabMover",
                "net.objectlab.qalab.ant.BuildStatMoverTask", "qalab.classpath");
        
    }

    /**
     * Qalab計測ターゲットを作成します。
     * @param root ルート要素
     * @param enableCreators 有効なQAツール一覧
     */
    private void createMainTarget(XmlElement root, AntCreator[] enableCreators) {
        XmlElement targetEl = createTargetElement(root, "qalab", "init");
        
        XmlElement stampEl = XmlUtils.createElement(targetEl, "tstamp");
        XmlElement formatEl = XmlUtils.createElement(stampEl, "format");
        formatEl.setAttribute("property", "TIME");
        formatEl.setAttribute("pattern", "yyyy-MM-dd HH:mm:ss");

        for (AntCreator creator : enableCreators) {
            int size = creator.getQalabClassNames().length;
            for (int i = 0; i < size; i++) {
                String name = creator.getQalabClassNames()[i];
                XmlElement statEl = XmlUtils.createElement(targetEl, "mergestat");
                statEl.setAttribute("inputFile", "${dest.dir}/" + creator.getReportXmlNames()[i]);
                statEl.setAttribute("outputFile", "${qalab.xml}");
                statEl.setAttribute("srcDir", "${all.src.dir}");
                statEl.setAttribute("handler", name);
                statEl.setAttribute("mergerTimeStamp", "${TIME}");
                statEl.setAttribute("propertiesFile",
                        LimyQalabPluginUtils.getResourcePath("qalab.properties"));
                statEl.setAttribute("quiet", "true");
            }
            
        }
    }

    /**
     * Chart出力ターゲットを作成します。
     * @param root ルート要素
     * @param env 
     * @param enableCreators 有効なQAツール一覧
     */
    private void createChart0Target(XmlElement root,
            LimyQalabEnvironment env, AntCreator[] enableCreators) {

        IPreferenceStore store = env.getStore();

        XmlElement targetEl = createTargetElement(root, "qalab-chart", "init");
        
        XmlElement mkdirEl = XmlUtils.createElement(targetEl, "mkdir");
        mkdirEl.setAttribute("dir", "${dest.dir}/qalab");
        
        XmlElement chartEl = XmlUtils.createElement(targetEl, "buildchart");
        chartEl.setAttribute("inputFile", "${qalab.xml}");
        chartEl.setAttribute("toDir", "${dest.dir}/qalab");
        chartEl.setAttribute("width", "680");
        chartEl.setAttribute("height", "453");
        chartEl.setAttribute("quiet", "true");
        
        chartEl.setAttribute("summaryOnly", "true");
//        if (store.getBoolean(LimyQalabConstants.KEY_ENABLE_INDIVISUAL)) {
//            chartEl.addAttribute("summaryOnly", "false");
//        } else {
//            chartEl.addAttribute("summaryOnly", "true");
//        }
        
        String type = LimyCreatorUtils.createTypeString(enableCreators, 0);
        setQalabAttributes(root, store, targetEl, chartEl, type);
    }

    /**
     * Chart出力（Cobertura用）ターゲットを作成します。
     * @param root ルート要素
     * @param env 
     * @param enableCreators 有効なQAツール一覧
     */
    private void createChart1Target(XmlElement root,
            LimyQalabEnvironment env, AntCreator[] enableCreators) {
        
        IPreferenceStore store = env.getStore();

        XmlElement targetEl = createTargetElement(root, "qalab-chart-coverage", "init");
        
        XmlElement mkdirEl = XmlUtils.createElement(targetEl, "mkdir");
        mkdirEl.setAttribute("dir", "${dest.dir}/qalab/coverage");

        XmlElement chartEl = XmlUtils.createElement(targetEl, "buildchart");
        chartEl.setAttribute("inputFile", "${qalab.xml}");
        chartEl.setAttribute("toDir", "${dest.dir}/qalab/coverage");
        chartEl.setAttribute("width", "680");
        chartEl.setAttribute("height", "453");
        chartEl.setAttribute("summaryOnly", "true");
        
//        if (store.getBoolean(LimyQalabConstants.KEY_ENABLE_INDIVISUAL)) {
//            chartEl.addAttribute("summaryOnly", "false");
//        } else {
//            chartEl.addAttribute("summaryOnly", "true");
//        }
        
        String type = LimyCreatorUtils.createTypeString(enableCreators, 1);
        setQalabAttributes(root, store, targetEl, chartEl, type);

    }

    /**
     * buildchart要素に各種属性を設定します。
     * @param root 親要素
     * @param store ストア
     * @param targetEl ターゲット要素
     * @param chartEl chart要素
     * @param type type文字列
     */
    private void setQalabAttributes(XmlElement root, IPreferenceStore store,
            XmlElement targetEl, XmlElement chartEl, String type) {
        if (type.length() == 0) {
            root.removeChild(targetEl);
        } else {
            chartEl.setAttribute("type", type);
            chartEl.setAttribute("summaryType", type);
        }
        
        if (store.getBoolean(LimyQalabConstants.ENABLE_INDIVISUAL)) {
            XmlElement chartEl2 = chartEl.copyBeforeSelf();
            chartEl2.setAttribute("summaryOnly", "false");
            chartEl2.setAttribute("width", "480");
            chartEl2.setAttribute("height", "340");
        }
    }
    
    /**
     * レポート出力ターゲットを作成します。
     * @param root ルート要素
     * @param env 
     * @param enableCreators 有効なQAツール一覧
     * @throws FileNotFoundException 必要なファイルが見つからない場合
     */
    private void createReportTarget(XmlElement root,
            LimyQalabEnvironment env,
            AntCreator[] enableCreators) throws FileNotFoundException {

        IJavaProject project = env.getJavaProject();
        XmlElement targetEl = createTargetElement(root, "qalab-report-only", "init");

        XmlElement mkdirEl = XmlUtils.createElement(targetEl, "mkdir");
        mkdirEl.setAttribute("dir", "${dest.dir}/qalab");

        XmlElement styleEl1 = createVmstyleElement(targetEl,
                "${dest.dir}/qalab/index.html",
                "qalab/index.vm");

        addVmParam(styleEl1, "projectName", project.getElementName());

        XmlElement styleEl2 = createVmstyleElement(targetEl,
                "${dest.dir}/qalab/menu.html",
                "qalab/menu.vm");

        String target = LimyCreatorUtils.createTargetString(enableCreators, null, 0, 1);
        XmlElement styleEl3 = null;
        XmlElement styleEl5 = null;
        if (target.length() > 0) {
            styleEl3 = createVmstyleElement(targetEl,
                    "${qalab.xml}", "${dest.dir}/qalab/qalab.html",
                    "qalab/qalab.vm");
            addVmParam(styleEl3, "pluginVersion", "${plugin.version}");
            
            IPreferenceStore store = env.getStore();
            if (store.getBoolean(LimyQalabConstants.ENABLE_INDIVISUAL)) {
                
                addVmParam(styleEl3, "enable_indivisual");

                createVmstyleElement(targetEl,
                        "${qalab.xml}", "${dest.dir}/qalab/all-packages.html",
                        "qalab/all_packages.vm");

                styleEl5 = createVmstyleElement(targetEl,
                        "${qalab.xml}", "${dest.dir}/qalab/dummy.html",
                        "qalab/per_file_main.vm");
            }

        }

        boolean enableQalab = false;
        boolean summaryGraph = false;
        for (AntCreator creator : enableCreators) {
            int group = creator.getSummaryGroup();
            if (group >= 0) {
                addVmParam(styleEl2, creator.getTargetName());
                addVmParam(styleEl3, creator.getTargetName());
                addVmParam(styleEl5, creator.getTargetName());
            }
            if (group == 0) {
                summaryGraph = true;
            }
            if (group == 0 || group == 1) {
                enableQalab = true;
            }
        }
        if (enableQalab) {
            addVmParam(styleEl1, "qalab");
            addVmParam(styleEl2, "qalab");
        }
        if (summaryGraph) {
            addVmParam(styleEl3, "enable_qalab");
        }

        createMoverReport(targetEl, env, enableCreators);
    }

    /**
     * QALab Moverレポート出力要素を生成します。
     * @param root 親要素
     * @param env 
     * @param enableCreators 有効なQAツール一覧
     */
    private void createMoverReport(XmlElement root,
            LimyQalabEnvironment env, AntCreator[] enableCreators) {
        
        XmlElement moverEl = XmlUtils.createElement(root, "QALabMover");
        moverEl.setAttribute("inputFile", "${qalab.xml}");
        moverEl.setAttribute("types",
                LimyCreatorUtils.createTypeString(enableCreators, 0, 1));
        moverEl.setAttribute("startTimeHoursOffset", "720"); // 720時間 = 30日
        moverEl.setAttribute("weekendAdjustment", "true");
        moverEl.setAttribute("quiet", "true");
        moverEl.setAttribute("outputXMLfile", "${dest.dir}/qalab-mover.xml");
        
        IPreferenceStore store = env.getStore();

        XmlElement styleEl = createVmstyleElement(root, "${dest.dir}/qalab-mover.xml",
                "${dest.dir}/qalab-mover.html", "qalab-mover/index.vm");
        if (store.getBoolean(LimyQalabConstants.ENABLE_INDIVISUAL)) {
            addVmParam(styleEl, "enable_indivisual");
        }

    }


}
