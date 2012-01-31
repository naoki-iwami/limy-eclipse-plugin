/*
 * Created 2005/09/17
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
package org.limy.eclipse.code.accessor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.participants.ProcessorBasedRefactoring;
import org.eclipse.ltk.core.refactoring.participants.RefactoringProcessor;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.eclipse.ltk.ui.refactoring.RefactoringWizardOpenOperation;
import org.eclipse.text.edits.TextEdit;
import org.limy.eclipse.common.jdt.LimyJavaUtils;
import org.limy.eclipse.common.ui.AbstractJavaElementAction;

/**
 * 既存Getter/Setterに対するJavadocコメントを生成するアクションクラスです。
 * @author Naoki Iwami
 */
public class ModifyAccessorJavadocAction extends AbstractJavaElementAction {

    private Collection<ICompilationUnit> units = new ArrayList<ICompilationUnit>();

    @Override
    protected void init() {
        units.clear();
    }

    public static class ModifyAccessorWizard extends RefactoringWizard {
        public ModifyAccessorWizard(Refactoring refactoring) {
            super(refactoring, RefactoringWizard.DIALOG_BASED_USER_INTERFACE);
        }

        /* (non-Javadoc)
         * @see org.eclipse.ltk.ui.refactoring.RefactoringWizard#addUserInputPages()
         */
        protected void addUserInputPages() {
            addPage(new AccessorCommentConfigurationPage());
        }
    }

    // ------------------------ Override Methods

    @Override
    protected void doAction(IJavaElement javaElement, IProgressMonitor monitor)
            throws CoreException {

        IType[] types = LimyJavaUtils.getAllTypes(javaElement);
        for (IType type : types) {
            ICompilationUnit workingCopy = type.getCompilationUnit().getWorkingCopy(monitor);
            
            Map<ICompilationUnit, Collection<TextEdit>> edits
                    = new HashMap<ICompilationUnit, Collection<TextEdit>>();

            AccessorUtils.modifyAccessorJavadoc(workingCopy.findPrimaryType(), monitor, edits);
            
            if (edits.isEmpty()) {
                workingCopy.discardWorkingCopy();
            } else {
                units.add(workingCopy);
            }
            
        }
    }

    @Override
    protected void execAfter(IProgressMonitor monitor) throws CoreException {
        
        try {
            boolean run;
            if (units.size() > 1) {
                RefactoringProcessor processor = new ModifyProcessor(units);
                Refactoring refactoring = new ProcessorBasedRefactoring(processor);
                ModifyAccessorWizard refactoringWizard = new ModifyAccessorWizard(refactoring);
                RefactoringWizardOpenOperation op = new RefactoringWizardOpenOperation(
                        refactoringWizard);
                run = op.run(getWindow().getShell(), "Modify accessor comment") == 0;
            } else if (units.isEmpty()) {
                MessageDialog.openInformation(getWindow().getShell(),
                        "Modify accessor comment",
                        "コメント生成が必要な Accessor が一つもありません。");
                return;
            } else {
                run = true;
            }
            
            for (ICompilationUnit unit : units) {
                if (run) {
                    unit.commitWorkingCopy(true, monitor);
                }
                unit.discardWorkingCopy();
            }
        } catch (InterruptedException e) {
            // refactoring got cancelled
        }
        
    }

}
