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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.DataSource;
import net.sourceforge.pmd.FileDataSource;
import net.sourceforge.pmd.IRuleViolation;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.RuleSetNotFoundException;
import net.sourceforge.pmd.RuleSets;
import net.sourceforge.pmd.SimpleRuleSetNameMapper;
import net.sourceforge.pmd.SourceType;
import net.sourceforge.pmd.dfa.report.AbstractReportNode;
import net.sourceforge.pmd.dfa.report.ViolationNode;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.limy.eclipse.common.LimyEclipsePluginUtils;
import org.limy.eclipse.qalab.LimyQalabPluginUtils;
import org.limy.eclipse.qalab.common.LimyQalabConstants;
import org.limy.eclipse.qalab.common.LimyQalabEnvironment;
import org.limy.eclipse.qalab.common.LimyQalabUtils;
import org.limy.eclipse.qalab.common.QalabResourceUtils;

/**
 * PMD用のマーカー作成クラスです。
 * @depend - - - PmdMarkAppender
 * @author Naoki Iwami
 */
public final class PmdMarkCreator implements MarkCreator {
    
    /** 唯一のInstance */
    private static PmdMarkCreator instance = new PmdMarkCreator();
    
    /** PMDパーサのキャッシュ */
    private Map<IProject, PMD> pmds = new HashMap<IProject, PMD>();

    /** ルールセットのキャッシュ */
    private Map<IProject, RuleSets> ruleSetsMap = new HashMap<IProject, RuleSets>();

    /** テンポラリフラグ */
    private boolean temporary;
    
    /**
     * private constructor
     */
    private PmdMarkCreator() {
        // empty
    }
    
    public static PmdMarkCreator getInstance() {
        return instance;
    }
    
    // ------------------------ Implement Methods

    public String getName() {
        return "pmd";
    }
    
    public boolean markJavaElement(LimyQalabEnvironment env,
            Collection<IJavaElement> elements, IProgressMonitor monitor) {
        
        return markResources(env, QalabResourceUtils.getResources(elements), monitor);
    }

    public boolean markResource(LimyQalabEnvironment env,
            IResource resource, IProgressMonitor monitor) {
        
        markAll(env,
                new File[] { resource.getLocation().toFile() }, false, monitor);
                
        Collection<IResource> resources = new ArrayList<IResource>();
        resources.add(resource);
        return PmdCpdMarkCreator.getInstance().markResources(
                env, resources, monitor);

    }


    public boolean markResourceTemporary(LimyQalabEnvironment env,
            IResource resource, IProgressMonitor monitor) {
        
        return markAll(env,
                new File[] { resource.getLocation().toFile() }, true, monitor);
    }

    public boolean markResources(LimyQalabEnvironment env,
            Collection<IResource> resources, IProgressMonitor monitor) {

        List<File> files = new ArrayList<File>();
        for (IResource resource : resources) {
            files.add(resource.getLocation().toFile());
        }
        
        markAll(env, files.toArray(new File[files.size()]), false, monitor);

        // TODO 現在は常にプロジェクト全体に対してCPDを計測している
        return PmdCpdMarkCreator.getInstance().markResources(env, resources, monitor);

    }

    // ------------------------ Private Methods
    
    /**
     * ファイル一覧をチェックしてマーカーを作成します。
     * @param env 
     * @param files ファイル一覧
     * @param b 一時モード
     * @param monitor 遷移モニタ
     * @return 処理に成功したらtrue
     */
    private boolean markAll(LimyQalabEnvironment env, File[] files, boolean b,
            IProgressMonitor monitor) {
        
        try {
            IProject project = env.getProject();
            PMD pmd = getPmd(env, monitor);
            RuleSets ruleSets = ruleSetsMap.get(project);
            
            RuleContext context = new RuleContext();
            context.setSourceCodeFilename(project.getLocation().toString());
            Report report = new Report();
            context.setReport(report);
            
            List<DataSource> sourceFiles = new ArrayList<DataSource>();
            for (File file : files) {
                sourceFiles.add(new FileDataSource(file));
            }
  
            pmd.processFiles(sourceFiles, context, ruleSets,
                    false,
                    true,
                    project.getLocation().toString(),
                    project.getDefaultCharset());

//            InputStreamReader in = new InputStreamReader(
//                    new FileInputStream(resource.getLocation().toFile()),
//                    resource.getProject().getDefaultCharset());
//            pmd.processFile(in, ruleSets, context);
            
            temporary = b;
            markAll(new PmdMarkAppender(),
                    env, context.getReport().getViolationTree().getRootNode());
            return true;
        } catch (RuleSetNotFoundException e) {
            LimyEclipsePluginUtils.log(e);
        } catch (IOException e) {
            LimyEclipsePluginUtils.log(e);
        } catch (CoreException e) {
            LimyEclipsePluginUtils.log(e);
        }
        return false;
    }

    /**
     * PMDチェック結果に応じてマーカーを作成します。
     * @param appender マーカー作成担当インスタンス
     * @param env 
     * @param root PMDチェック結果ルート要素
     * @throws CoreException コア例外
     */
    private void markAll(PmdMarkAppender appender,
            LimyQalabEnvironment env, AbstractReportNode root) throws CoreException {
        
        for (int i = 0; i < root.getChildCount(); i++) {
            AbstractReportNode child = root.getChildAt(i);
            if (child instanceof ViolationNode) {
                ViolationNode node = (ViolationNode)child;
                IRuleViolation ruleViolation = node.getRuleViolation();

                IResource resource = getResource(env, ruleViolation);
                
                if (resource != null && resource.exists()) {
                    appender.addMarker(resource, ruleViolation, temporary);
                }

            }
            markAll(appender, env, child);
        }
    }


    /**
     * @param project 
     * @param ruleViolation
     * @return
     * @throws CoreException 
     */
    private IResource getResource(LimyQalabEnvironment env, IRuleViolation ruleViolation)
            throws CoreException {
                
        String pathStr = ruleViolation.getPackageName();
        if (pathStr.length() > 0) {
            pathStr += ".";
        }
        pathStr += ruleViolation.getClassName();
        
        return QalabResourceUtils.getJavaResource(env, pathStr, false);
    }

    /**
     * @param env 
     * @param monitor 
     * @return
     * @throws RuleSetNotFoundException 
     */
    private PMD getPmd(LimyQalabEnvironment env, IProgressMonitor monitor)
            throws RuleSetNotFoundException {
        
        IProject project = env.getProject();
        PMD pmd = pmds.get(project);
        if (pmd != null) {
            return pmd;
        }
        
        IProgressMonitor subMonitor = new SubProgressMonitor(monitor, 1);
        subMonitor.subTask("Creating PMD-engine for "
                + project.getName());

        pmd = new PMD();
        String jdkVersion = LimyQalabUtils.getJdkVersion(env.getJavaProject());
        if ("1.3".equals(jdkVersion)) {
            pmd.setJavaVersion(SourceType.JAVA_13);
        }
        if ("1.4".equals(jdkVersion)) {
            pmd.setJavaVersion(SourceType.JAVA_14);
        }
        if ("1.5".equals(jdkVersion)) {
            pmd.setJavaVersion(SourceType.JAVA_15);
        }
        if ("1.6".equals(jdkVersion)) {
            pmd.setJavaVersion(SourceType.JAVA_16);
        }
        
        RuleSetFactory ruleSetFactory = new RuleSetFactory();
//        ruleSetFactory.setMinimumPriority(opts.getMinPriority());

        RuleSets ruleSets = ruleSetFactory.createRuleSets(
                new SimpleRuleSetNameMapper(getRulesetFile(env)).getRuleSets());
        ruleSetsMap.put(project, ruleSets);
        
        subMonitor.done();
        pmds.put(project, pmd);
        return pmd;
    }

    /**
     * @param env 
     * @return
     */
    private String getRulesetFile(LimyQalabEnvironment env) {
        
        File file = LimyQalabPluginUtils.getConfigFile(
                env, "pmd-ruleset.xml",
                LimyQalabConstants.KEY_PMD_TYPE, LimyQalabConstants.KEY_PMD_CFG);
        return file.getAbsolutePath();
    }

}
