/*
 * Created 2007/01/05
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
package org.limy.eclipse.qalab.mark;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.limy.eclipse.common.LimyEclipsePluginUtils;
import org.limy.eclipse.common.resource.LimyMarkerUtils;
import org.limy.eclipse.qalab.LimyQalabMarker;
import org.limy.eclipse.qalab.common.LimyQalabEnvironment;
import org.limy.eclipse.qalab.common.LimyQalabUtils;
import org.limy.eclipse.qalab.common.QalabResourceUtils;

import edu.umd.cs.findbugs.AbstractBugReporter;
import edu.umd.cs.findbugs.AnalysisError;
import edu.umd.cs.findbugs.BugAnnotation;
import edu.umd.cs.findbugs.BugInstance;
import edu.umd.cs.findbugs.BugReporter;
import edu.umd.cs.findbugs.ClassAnnotation;
import edu.umd.cs.findbugs.SourceLineAnnotation;
import edu.umd.cs.findbugs.classfile.ClassDescriptor;

/**
 * Findbugs用のチェック結果リスナークラスです。
 * @author Naoki Iwami
 */
public class LimyBugReporter extends AbstractBugReporter {

    // ------------------------ Fields

    /** 環境設定 */
    private LimyQalabEnvironment env;
    
    // ------------------------ Constructors

    /**
     * LimyBugReporterインスタンスを構築します。
     * @param env 
     */
    public LimyBugReporter(LimyQalabEnvironment env) {
        super();
        this.env = env;
    }
    
    // ------------------------ Override Methods

    @Override
    protected void doReportBug(BugInstance bugInstance) {
        int lineNumber = -1;
        String className = null;
        for (Iterator<BugAnnotation> it = bugInstance.annotationIterator(); it.hasNext();) {
            BugAnnotation annotation = it.next();
            if (annotation instanceof SourceLineAnnotation) {
                SourceLineAnnotation line = (SourceLineAnnotation)annotation;
                lineNumber = line.getStartLine();
            }
            if (annotation instanceof ClassAnnotation) {
                ClassAnnotation clazz = (ClassAnnotation)annotation;
                className = clazz.getClassName();
            }
        }
        
        try {
            IResource resource = QalabResourceUtils.getJavaResource(env, className, false);
            if (resource == null
                    || LimyQalabUtils.isIgnoreSource(env, resource.getFullPath())) {
                
                return;
            }
            String markerId = LimyQalabMarker.PROBLEM_ID;
            
            Map<String, Object> attrs = new HashMap<String, Object>();
            attrs.put(LimyQalabMarker.URL, getUrl(bugInstance));
            
            LimyMarkerUtils.addMarker(markerId,
                    resource, lineNumber,
                    bugInstance.getBugPattern().getShortDescription(), attrs);
        } catch (CoreException e) {
            LimyEclipsePluginUtils.log(e);
        }
        
    }

    @Override
    public void reportAnalysisError(AnalysisError error) {
//        System.out.println("reportAnalysisError");
        
    }

    @Override
    public void reportMissingClass(String string) {
//        System.out.println("reportMissingClass");
        
    }

    public void finish() {
//        System.out.println("finish");
//        ClassStats stats = getProjectStats().getClassStats("org.limy.eclipse.code.LimyCodePlugin");
//        if (stats != null) {
//            System.out.println(stats.getTotalBugs());
//        }
        
    }

    public BugReporter getRealBugReporter() {
        return this;
    }

    public void observeClass(ClassDescriptor classDescriptor) {
//        System.out.println("observeClass " + classDescriptor.getClassName());
        
    }

    // ------------------------ Private Methods

    /**
     * バグインスタンスに対応するURLを返します。
     * @param bugInstance バグインスタンス
     * @return URL
     */
    private String getUrl(BugInstance bugInstance) {
        
        return "http://findbugs.sourceforge.net/bugDescriptions.html#"
                + bugInstance.getBugPattern().getType();
    }

}
