/*
 * Created 2007/01/09
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
package org.limy.eclipse.qalab.ui;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.limy.eclipse.qalab.LimyQalabMarker;

/**
 * テスト結果表示用のSelectionListener実装クラスです。
 * @author Naoki Iwami
 */
public class TestResultListener implements ISelectionListener {

    // ------------------------ Fields (Constants)

    /** ターゲット表示ラベル */
    private final Label targetLabel;
    
    /** カバレッジ表示ラベル */
    private final Label coverageLabel;
    
    // ------------------------ Fields (Variable)

    /** 全Branch数 */
    private int allBranches;
    
    /** 全Line数 */
    private int allLines;
    
    /** カバレッジされたBranch数 */
    private int coverageBranches;
    
    /** カバレッジされたLine数 */
    private int coverageLines;
    
    // ------------------------ Constructors

    /**
     * TestResultListenerインスタンスを構築します。
     * @param targetLabel
     * @param coverageLabel 
     */
    public TestResultListener(Label targetLabel, Label coverageLabel) {
        this.targetLabel = targetLabel;
        this.coverageLabel = coverageLabel;
    }

    // ------------------------ Implement Methods

    public void selectionChanged(IWorkbenchPart part, ISelection selection) {
        
        if (!(selection instanceof IStructuredSelection)) {
            return;
        }
        Object element = ((IStructuredSelection)selection).getFirstElement();
        if (!(element instanceof IJavaElement)) {
            return;
        }
        IResource resource = ((IJavaElement)element).getResource();
        targetLabel.setText(resource.getFullPath().toString());
        
        allBranches = 0;
        allLines = 0;
        coverageBranches = 0;
        coverageLines = 0;
        try {
            totalNumbers(resource);
            
            double ratioLines = 100 * coverageLines / (double)allLines;
            double ratioBranches = 100 * coverageBranches / (double)allBranches;
            String line = String.format("%2d %%", Integer.valueOf((int)ratioLines));
            String branch = String.format("%2d %%", Integer.valueOf((int)ratioBranches));
            if (Double.isNaN(ratioLines)) {
                line = "N/A";
            }
            if (Double.isNaN(ratioBranches)) {
                branch = "N/A";
            }
            
            String message = "Line : " + line + ", Branch : " + branch;
            coverageLabel.setText(message);
            
        } catch (CoreException e) {
            coverageLabel.setText(e.toString());
        }
        
    }

    // ------------------------ Private Methods

    /**
     * @param resource
     * @throws CoreException
     */
    private void totalNumbers(IResource resource) throws CoreException {
        IMarker[] markers = resource.findMarkers(LimyQalabMarker.COVERAGE_RESULT,
                false, IResource.DEPTH_INFINITE);
        for (IMarker marker : markers) {
            allBranches += Integer.parseInt(
                    (String)marker.getAttribute(LimyQalabMarker.ALL_BRANCH_NUMBER));
            allLines += Integer.parseInt(
                    (String)marker.getAttribute(LimyQalabMarker.ALL_LINE_NUMBER));
            coverageBranches += Integer.parseInt(
                    (String)marker.getAttribute(LimyQalabMarker.COVERAGE_BRANCH));
            coverageLines += Integer.parseInt(
                    (String)marker.getAttribute(LimyQalabMarker.COVERAGE_LINE));
        }
    }

}
