/*
 * Created 2006/08/19
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

import org.eclipse.jface.preference.IPreferenceStore;
import org.limy.eclipse.qalab.LimyQalabPluginUtils;
import org.limy.eclipse.qalab.common.AntCreator;
import org.limy.eclipse.qalab.common.LimyQalabConstants;
import org.limy.eclipse.qalab.common.LimyQalabEnvironment;
import org.limy.xml.XmlElement;


/**
 * Javadoc用のAnt要素を生成するクラスです。
 * @author Naoki Iwami
 * @version 1.0.0
 */
public class JavadocCreator extends AbstractAntCreator {

    // ------------------------ Implement Methods

    public void exec(XmlElement root, LimyQalabEnvironment env) {
        createMainTarget(root, env);
    }
    
    public String[] getQalabClassNames() {
        return new String[0];
    }

    public String[] getReportXmlNames() {
        return new String[0];
    }

    public int getSummaryGroup() {
        return 3;
    }

    public String[] getSummaryTypes() {
        return new String[0];
    }

    public String getTargetName() {
        return "javadoc";
    }

    public boolean isEnable(IPreferenceStore store) {
        return true;
    }

    public AntCreator newInstance() {
        return new JavadocCreator();
    }

    // ------------------------ Private Methods
    
    /**
     * Javadoc生成ターゲットを作成します。
     * @param root ルート要素
     * @param env 
     */
    private void createMainTarget(XmlElement root, LimyQalabEnvironment env) {
        XmlElement targetEl = createTargetElement(root, "javadoc", "init");
        
        XmlElement mainEl = createElement(targetEl, "javadoc");
        mainEl.setAttribute("destdir", "${dest.dir}/javadoc");
        mainEl.setAttribute("encoding", "${src.encoding}");
        mainEl.setAttribute("charset", "${src.encoding}");
        mainEl.setAttribute("docencoding", "${src.encoding}");
        mainEl.setAttribute("classpathref", "all.classpath");
        mainEl.setAttribute("useexternalfile", "true");

        XmlElement filesetEl = createElement(mainEl, "fileset");
        filesetEl.setAttribute("dir", "${all.src.dir}");
        filesetEl.setAttribute("includes", "**/*.java");
        
        IPreferenceStore store = env.getStore();
        if (store.getBoolean(LimyQalabConstants.UMLGRAPH_JAVADOC)
                && store.getBoolean(LimyQalabConstants.ENABLE_UMLGRAPH)) {
            
            XmlElement docletEl = createElement(mainEl, "doclet");
            docletEl.setAttribute("name", "gr.spinellis.umlgraph.doclet.UmlGraphDoc");
            docletEl.setAttribute("path",
                    LimyQalabPluginUtils.getResourcePath("lib/UmlGraph.jar"));
            
            addParam(docletEl, "-collpackages", "java.util.*");
            if (store.getBoolean(LimyQalabConstants.UMLGRAPH_INFERREL)) {
                addParam(docletEl, "-inferrel", null);
            }
//            if (store.getBoolean(LimyQalabConstants.UMLGRAPH_HORIZONTAL)) {
//                addParamDoclet(docletEl, "-horizontal", null);
//            }
            
            String resources = store.getString(LimyQalabConstants.EXCLUDE_JDEPENDS);
            String[] excludes = resources.split("\n");
            for (String exclude : excludes) {
                addParam(docletEl, "-hide", exclude);
            }
            
        }
    }

}
