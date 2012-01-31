package org.limy.eclipse.code.accessor;

import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.RefactoringParticipant;
import org.eclipse.ltk.core.refactoring.participants.RefactoringProcessor;
import org.eclipse.ltk.core.refactoring.participants.SharableParticipants;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;

public class ModifyProcessor extends RefactoringProcessor {

    private Collection<ICompilationUnit> units;

    public ModifyProcessor(Collection<ICompilationUnit> units) {
        this.units = units;
    }

    @Override
    public RefactoringStatus checkFinalConditions(IProgressMonitor pm,
            CheckConditionsContext context) {
        return null;
    }

    @Override
    public RefactoringStatus checkInitialConditions(IProgressMonitor pm) {
        return null;
    }

    @Override
    public Change createChange(IProgressMonitor pm)
            throws CoreException, OperationCanceledException {
        
        CompositeChange rootChange = new CompositeChange("Modify accessor comment");
        
        for (ICompilationUnit unit : units) {
            IFile file = (IFile)unit.getResource();
            TextFileChange change = new TextFileChange(file.getName(), file);
            change.setEdit(new MultiTextEdit());
            int size = change.getCurrentDocument(null).getLength();
            change.addEdit(new ReplaceEdit(0, size, unit.getSource()));
            rootChange.add(change);
        }
        return rootChange;
    }

    @Override
    public Object[] getElements() {
        return null;
    }

    @Override
    public String getIdentifier() {
        return null;
    }

    @Override
    public String getProcessorName() {
        return "Modify accessor comment";
    }

    @Override
    public boolean isApplicable() {
        return false;
    }

    @Override
    public RefactoringParticipant[] loadParticipants(RefactoringStatus status,
            SharableParticipants sharedParticipants) {
        return null;
    }

}
