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
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.limy.eclipse.common.LimyEclipsePluginUtils;
import org.limy.eclipse.common.resource.LimyMarkerUtils;
import org.limy.eclipse.common.resource.LimyResourceUtils;
import org.limy.eclipse.qalab.LimyQalabMarker;
import org.limy.eclipse.qalab.common.LimyQalabWebUtils;

import com.puppycrawl.tools.checkstyle.api.AuditEvent;
import com.puppycrawl.tools.checkstyle.api.AuditListener;
import com.puppycrawl.tools.checkstyle.api.SeverityLevel;

/**
 * Checkstyle用のチェック結果リスナークラスです。
 * @author Naoki Iwami
 */
public class LimyAuditListener implements AuditListener {

    // ------------------------ Fields

    /** プロジェクト */
    private IProject project;

    // ------------------------ Constructors

    public LimyAuditListener(IProject project) {
        this.project = project;
    }
    
    // ------------------------ Override Methods
    
    public void addError(AuditEvent evt) {
        
        if (evt.getSeverityLevel() == SeverityLevel.IGNORE) {
            return;
        }
        IResource resource = getResource(evt);
        if (!resource.exists()) {
            resource = resource.getParent();
            try {
                resource.deleteMarkers(LimyQalabMarker.DEFAULT_ID, false, IResource.DEPTH_ZERO);
            } catch (CoreException e) {
                LimyEclipsePluginUtils.log(e);
            }
        }
        try {
            String merkerId = LimyQalabMarker.PROBLEM_ID;
            Map<String, Object> attrs = new HashMap<String, Object>();
            attrs.put(LimyQalabMarker.URL, LimyQalabWebUtils.getCheckstyleUrl(evt.getSourceName()));
            LimyMarkerUtils.addMarker(merkerId,
                    resource, evt.getLine(), evt.getMessage(), attrs);
        } catch (CoreException e) {
            LimyEclipsePluginUtils.log(e);
        }

    }

    public void addException(AuditEvent evt, Throwable throwable) {
        // do nothing
    }

    public void auditFinished(AuditEvent evt) {
        // do nothing
    }

    public void auditStarted(AuditEvent evt) {
        // do nothing
    }

    public void fileFinished(AuditEvent evt) {
        // do nothing
    }

    public void fileStarted(AuditEvent evt) {
        // do nothing
    }

    // ------------------------ Private Methods

    /**
     * @param evt
     * @return 
     */
    private IResource getResource(AuditEvent evt) {
        String rootDir = project.getLocation().toString();
        String pathStr = evt.getFileName().substring(rootDir.length()).replace('\\', '/');
        
        IPath path = new Path("/" + project.getName() + pathStr);
        return LimyResourceUtils.newFile(path);
    }

}
