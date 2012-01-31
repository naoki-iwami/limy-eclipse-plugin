/*
 * Created 2006/01/14
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
package org.limy.eclipse.web.velocityeditor;

import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.internal.ui.actions.FoldingActionGroup;
import org.eclipse.jdt.internal.ui.text.java.hover.SourceViewerInformationControl;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.text.source.projection.ProjectionAnnotation;
import org.eclipse.jface.text.source.projection.ProjectionAnnotationModel;
import org.eclipse.jface.text.source.projection.ProjectionSupport;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.editors.text.TextEditor;
import org.limy.eclipse.common.LimyEclipseConstants;
import org.limy.eclipse.common.LimyEclipsePluginUtils;
import org.limy.eclipse.core.LimyEclipsePlugin;
import org.limy.eclipse.web.LimyWebConstants;

/**
 * Velocityエディタクラスです。
 * @author Naoki Iwami
 */
public class VelocityEditor extends TextEditor {

    // ------------------------ Fields

    /** プロジェクション（Folding）サポート */
    private ProjectionSupport projectionSupport;

    /** プロジェクションモデル更新プロバイダ */
    private VelocityFoldingStructureProvider projectionUpdater;

    /** Foldingアクショングループ */
    private FoldingActionGroup foldingGroup;

    // ------------------------ Constructors

    /**
     * VelocityEditorインスタンスを構築します。
     */
    public VelocityEditor() {
        super();
        setSourceViewerConfiguration(new VelocitySourceViewerConfiguration(this));
    }
    
    // ------------------------ Override Methods
    
    public void createPartControl(Composite parent) {
        super.createPartControl(parent);
        // 背景色をセット
        getSourceViewer().getTextWidget().setBackground(
                LimyEclipsePlugin.getDefault().getColorProvider().getColor(
                    LimyEclipseConstants.P_BGCOLOR));

        ProjectionViewer projectionViewer = (ProjectionViewer) getSourceViewer();

        projectionSupport = new ProjectionSupport(
                projectionViewer, getAnnotationAccess(), getSharedColors());
        projectionSupport.addSummarizableAnnotationType(
                "org.eclipse.ui.workbench.texteditor.error"); //$NON-NLS-1$
        projectionSupport.addSummarizableAnnotationType(
                "org.eclipse.ui.workbench.texteditor.warning"); //$NON-NLS-1$
        projectionSupport.setHoverControlCreator(new IInformationControlCreator() {
            public IInformationControl createInformationControl(Shell shell) {
//                return new CustomSourceInformationControl(shell, IDocument.DEFAULT_CONTENT_TYPE);
//                return new SourceViewerInformationControl(shell,
//                        SWT.TOOL | SWT.NO_TRIM | getOrientation(), SWT.NONE);
                return new SourceViewerInformationControl(shell, true,
                        SWT.TOOL | SWT.NO_TRIM | getOrientation(), null);
            }
        });
        
         projectionSupport.install();
        
        projectionUpdater = new VelocityFoldingStructureProvider();
        projectionUpdater.install(this, projectionViewer);

        if (isFoldingEnabled()) {
            projectionViewer.doOperation(ProjectionViewer.TOGGLE);
        }

    }

//    protected void initializeKeyBindingScopes() {
//        setKeyBindingScopes(new String[] { "org.limy.eclipse.web.velocityEditorScope" });
//    }
    
    protected void createActions() {
        super.createActions();
        foldingGroup = new FoldingActionGroup(this, getSourceViewer());
    }
    
    protected void doSetInput(IEditorInput input) throws CoreException {
        super.doSetInput(input);
        setPreferenceStore(LimyEclipsePlugin.getDefault().getPreferenceStore());
    }
    
    protected void handlePreferenceStoreChanged(PropertyChangeEvent event) {
        super.handlePreferenceStoreChanged(event);
        
        ProviderManager.getInstance().updateProviders();
        if (getSourceViewer() instanceof SourceViewer) {
            ((SourceViewer)getSourceViewer()).unconfigure();
            getSourceViewer().configure(new VelocitySourceViewerConfiguration(this));
            getSourceViewer().getTextWidget().setBackground(
                    LimyEclipsePlugin.getDefault().getColorProvider().getColor(
                        LimyEclipseConstants.P_BGCOLOR));
            ((SourceViewer)getSourceViewer()).refresh();
        }
    }

    protected ISourceViewer createSourceViewer(Composite parent,
            IVerticalRuler ruler, int styles) {
        
        ProjectionViewer viewer = new ProjectionViewer(
                parent, ruler, getOverviewRuler(), true, styles);
        getSourceViewerDecorationSupport(viewer);
        return viewer;
    }
    
    public Object getAdapter(Class required) {

        // プロジェクションサポート
        if (projectionSupport != null) {
            Object adapter = projectionSupport.getAdapter(getSourceViewer(), required);
            if (adapter != null) {
                return adapter;
            }
        }

        return super.getAdapter(required);
    }
    
    public void doSave(IProgressMonitor progressMonitor) {
        super.doSave(progressMonitor);

        // Foldingマーカーを再構築する
        createProjectionAnnotation();
        
    }

    // ------------------------ Public Methods

    /**
     * Foldingアクショングループを取得します。
     * @return Foldingアクショングループ
     */
    public FoldingActionGroup getFoldingActionGroup() {
        return foldingGroup;
    }

    /**
     * @return Foldingが有効ならばtrue
     */
    private boolean isFoldingEnabled() {
        return LimyEclipsePluginUtils.getPreferenceBoolean(
                LimyEclipsePlugin.getDefault().getPreferenceStore(),
                LimyWebConstants.P_FOLDING_ENABLE, true);
    }
    
    // getter/setter methods
    
    /**
     * Foldingマーカーを再構築します。
     */
    private void createProjectionAnnotation() {
        ProjectionAnnotationModel model = (ProjectionAnnotationModel)
                getAdapter(ProjectionAnnotationModel.class);
        IDocument doc = getDocumentProvider().getDocument(getEditorInput());
        if (model != null) {
            model.removeAllAnnotations();
            try {
                Map<ProjectionAnnotation, Position> annotations
                        = VelocityUtils.getFoldingAnnotations(doc);
                model.replaceAnnotations(null, annotations);
            } catch (BadLocationException e) {
                LimyEclipsePluginUtils.log(e);
            }
        }
    }
    
}
