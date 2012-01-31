/*
 * Created 2007/01/30
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

import javancss.Javancss;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitEditor;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.texteditor.IStatusField;
import org.limy.eclipse.qalab.common.LimyQalabConstants;

/**
 * Java Editorです。
 * @depend - - - StatusInfoCreator
 * @depend - - - QalabEditorContributor
 * @author Naoki Iwami
 */
public class QalabJavaEditor extends CompilationUnitEditor {

    // ------------------------ Fields

//    /** ClassNodeキャッシュ */
//    private ClassNode classNode;

//    /** Javancssキャッシュ */
//    private Javancss javancss;

    /** ccステータス作成キャッシュ */
    private StatusInfoCreator ccStatusInfoCreator;

    // ------------------------ Override Methods

    @Override
    protected void handleCursorPositionChanged() {
        super.handleCursorPositionChanged();
        updateStatusField(LimyQalabConstants.QALAB_CATEGORY_CC);
        updateStatusField(LimyQalabConstants.QALAB_CATEGORY_COVERAGE);

    }

    @Override
    protected void updateStatusField(String category) {
        super.updateStatusField(category);
        if (LimyQalabConstants.QALAB_CATEGORY_CC.equals(category)) {
            updateStatus(category, getCcStatusInfoCreator());
        }
        if (LimyQalabConstants.QALAB_CATEGORY_COVERAGE.equals(category)) {
            updateStatus(category, CoverageStatusInfoCreator.getInstance());
        }
    }

    /**
     * @param category
     * @param creator 
     */
    private void updateStatus(String category, StatusInfoCreator creator) {
        IStatusField field = getStatusField(category);
        if (field == null || creator == null) {
            return;
        }
        
        String text = null;
        IJavaElement javaElement = JavaUI.getEditorInputJavaElement(getEditorInput());
        if (javaElement instanceof ICompilationUnit) {
            ICompilationUnit cunit = (ICompilationUnit)javaElement;
            
            // エディタ内の選択範囲を取得
            ITextSelection textSelection = (ITextSelection)getSelectionProvider().getSelection();
            text = creator.create(cunit, textSelection);
        }
        if (text == null) {
            field.setText("");
        } else {
            field.setText(text);
        }
    }

    @Override
    protected void doSetInput(IEditorInput input) throws CoreException {
        super.doSetInput(input);
//        classNode = null;
        ccStatusInfoCreator = null;
    }

    @Override
    public void doSave(IProgressMonitor progressMonitor) {
        super.doSave(progressMonitor);
//        classNode = null;
        ccStatusInfoCreator = null;
        handleCursorPositionChanged();
    }

//    @Override
//    protected void createActions() {
//        super.createActions();
////        IAction action = new JavaSelectRulerAction();
////        setAction("RulerClick", action);
//    }

//    @Override
//    protected void handleEditorInputChanged() {
//        
//        System.out.println("handleEditorInputChanged");
//        super.handleEditorInputChanged();
//    }

//    @Override
//    protected void updateStatusLine() {
//        System.out.println("updateStatusLine");
//        super.updateStatusLine();
////        getStatusLineManager().setMessage("sample");
//    }

//    @Override
//    protected Control createStatusControl(Composite parent, IStatus status) {
//        System.out.println("createStatusControl");
//        return super.createStatusControl(parent, status);
//    }

//    @Override
//    protected IStatusLineManager getStatusLineManager() {
//        System.out.println("getStatusLineManager");
//        return super.getStatusLineManager();
//    }


//    @Override
//    protected void selectionChanged() {
//        System.out.println("selectionChanged");
//        super.selectionChanged();
//    }

//    @Override
//    protected String getStatusMessage(IStatus status) {
//        System.out.println("getStatusMessage");
//        return super.getStatusMessage(status);
//    }

    // ------------------------ Private Methods

//    /**
//     * @return
//     * @throws JavaModelException 
//     * @throws IOException 
//     */
//    private ClassNode getClassNode() throws JavaModelException, IOException {
//        
//        if (classNode != null) {
//            return classNode;
//        }
//        
//        long time = System.currentTimeMillis();
//
//        ResourceWithBasedir classResource = CoberturaMarkerUtils.getClassResource(
//                JavaUI.getEditorInputJavaElement(getEditorInput()).getResource());
//        IResource resource = classResource.getResource();
//
//        FileInputStream in = new FileInputStream(resource.getLocation().toFile());
//        try {
//            ClassReader cr = new ClassReader(in);
//            ClassNode cn = new ClassNode();
//            cr.accept(cn, 0);
//            classNode = cn;
//        } finally {
//            in.close();
//        }
//            
//        System.out.println((System.currentTimeMillis() - time) + "ms.");
//        return classNode;
//    }
    
    private StatusInfoCreator getCcStatusInfoCreator() {

        IJavaElement element = JavaUI.getEditorInputJavaElement(getEditorInput());
        if (element == null || element.getResource() == null) {
            return null;
        }

        if (ccStatusInfoCreator != null) {
            return ccStatusInfoCreator;
        }

        Javancss javancss = new Javancss(element.getResource().getLocation().toString());
        ccStatusInfoCreator = new CcStatusInfoCreator(javancss);
        
        return ccStatusInfoCreator;
    }

}
