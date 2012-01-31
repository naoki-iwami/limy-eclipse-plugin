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
package org.limy.eclipse.qalab.mark;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.tools.ant.filters.StringInputStream;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.limy.eclipse.common.LimyEclipsePluginUtils;
import org.limy.eclipse.qalab.LimyQalabPluginUtils;
import org.limy.eclipse.qalab.common.LimyQalabConstants;
import org.limy.eclipse.qalab.common.LimyQalabEnvironment;

import com.atlassw.tools.eclipse.checkstyle.builder.PackageNamesLoader;
import com.atlassw.tools.eclipse.checkstyle.builder.PackageObjectFactory;
import com.atlassw.tools.eclipse.checkstyle.util.CheckstylePluginException;
import com.puppycrawl.tools.checkstyle.Checker;
import com.puppycrawl.tools.checkstyle.ConfigurationLoader;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import com.puppycrawl.tools.checkstyle.api.Configuration;

/**
 * チェッカ作成を担当します。
 * @author Naoki Iwami
 */
public class CheckCreator {
    
    /** Checkstyleエンジンのキャッシュ */
    private Map<IProject, Checker> checkers = new HashMap<IProject, Checker>();

    /** AuditListenerのキャッシュ */
    private Map<Checker, LimyAuditListener> listeners = new HashMap<Checker, LimyAuditListener>();

    // ------------------------ Public Methods

    /**
     * @param env 
     * @param monitor 
     * @return 
     * @throws CheckstyleException 
     * @throws CheckstylePluginException 
     */
    public Checker getChecker(LimyQalabEnvironment env, IProgressMonitor monitor)
            throws CheckstyleException, CheckstylePluginException {
        
        IProject project = env.getProject();
        
        Checker checker = checkers.get(project);
        if (checker != null) {
            return checker;
        }

        IProgressMonitor subMonitor = new SubProgressMonitor(monitor, 1);
        subMonitor.subTask("Creating Checkstyle-checker for "
                + project.getName());

        checker = new Checker();

        List<String> packages = PackageNamesLoader
                .getPackageNames(Thread.currentThread().getContextClassLoader());
        checker.setModuleFactory(new PackageObjectFactory(packages));

        try {
            File configFile = LimyQalabPluginUtils.getConfigFile(env,
                    "sun_checks.xml",
                    LimyQalabConstants.KEY_CHK_TYPE, LimyQalabConstants.KEY_CHK_CFG);
            
            Configuration configuration = ConfigurationLoader.loadConfiguration(
                    supportEncoding(env, configFile), null, true);
            
            checker.configure(configuration);
        } catch (IOException e) {
            CheckstylePluginException.rethrow(e);
        }

        LimyAuditListener listener = new LimyAuditListener(project);
        checker.addListener(listener);
        
        checkers.put(project, checker);
        listeners.put(checker, listener);
        
        subMonitor.done();
        return checker;
    }
    
    public void clearCache() {
        checkers.clear();
    }

    // ------------------------ Private Methods

    private InputStream supportEncoding(
            LimyQalabEnvironment env, File configFile) throws IOException {
        String lines = FileUtils.readFileToString(configFile, "UTF-8");
        
        Matcher matcherTree = Pattern.compile(".*<module name=\"TreeWalker\">(.*)",
                Pattern.MULTILINE | Pattern.DOTALL).matcher(lines);
        if (matcherTree.matches()) {
            int pos = matcherTree.start(1);
            StringBuilder buff = new StringBuilder(lines);
            try {
                buff.insert(pos, "<property name=\"charset\" value=\""
                        + env.getProject().getDefaultCharset() + "\"/>");
                lines = buff.toString();
            } catch (CoreException e) {
                LimyEclipsePluginUtils.log(e);
            }
        }
        
        return new StringInputStream(lines);
    }

}
