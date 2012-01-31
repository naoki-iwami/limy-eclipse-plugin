/*
 * Created 2007/02/05
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
package org.limy.eclipse.qalab.editor;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jface.text.ITextSelection;
import org.limy.eclipse.common.LimyEclipsePluginUtils;
import org.limy.eclipse.qalab.LimyQalabMarker;

/**
 * Coverageステータスバーに表示する内容を作成するクラスです。
 * @author Naoki Iwami
 */
public final class CoverageStatusInfoCreator implements StatusInfoCreator {

    /** 唯一のインスタンス */
    private static final CoverageStatusInfoCreator instance = new CoverageStatusInfoCreator();
    
    /**
     * private constructor
     */
    private CoverageStatusInfoCreator() { }
    
    public String create(ICompilationUnit cunit, ITextSelection textSelection) {
        
        try {
            IMarker[] markers = cunit.getResource().findMarkers(
                    LimyQalabMarker.COVERAGE_RESULT, false,
                    IResource.DEPTH_ZERO);
            for (IMarker marker : markers) {
                int allLineNumber = Integer.parseInt(
                        (String)marker.getAttribute(LimyQalabMarker.ALL_LINE_NUMBER));
                int coverageLineNumber = Integer.parseInt(
                        (String)marker.getAttribute(LimyQalabMarker.COVERAGE_LINE));
                int allBranchNumber = Integer.parseInt(
                        (String)marker.getAttribute(LimyQalabMarker.ALL_BRANCH_NUMBER));
                int coverageBranchNumber = Integer.parseInt(
                        (String)marker.getAttribute(LimyQalabMarker.COVERAGE_BRANCH));
                
                String branch = "-";
                if (allBranchNumber > 0) {
                    branch = Integer.toString(coverageBranchNumber * 100 / allBranchNumber);
                }

                String line = "-";
                if (allLineNumber > 0) {
                    line = Integer.toString(coverageLineNumber * 100 / allLineNumber);
                }

                return "Line : " + line + ", Branch : " + branch;
            }
        } catch (CoreException e) {
            LimyEclipsePluginUtils.log(e);
        }
        return null;
    }

    /**
     * @return
     */
    public static StatusInfoCreator getInstance() {
        return instance;
    }
    
}
