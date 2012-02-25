package org.limy.eclipse.code.etc;

import java.util.Iterator;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.ISourceReference;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.javaeditor.EditorUtility;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.projection.ProjectionAnnotation;
import org.eclipse.jface.text.source.projection.ProjectionAnnotationModel;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.ui.progress.UIJob;
import org.limy.eclipse.code.LimyCodePlugin;
import org.limy.eclipse.common.LimyEclipsePluginUtils;
import org.limy.eclipse.common.LimyLineIterator;
import org.limy.eclipse.common.ui.AbstractJavaElementAction;
import org.limy.eclipse.core.LimyEclipsePlugin;

public class FoldingAction extends AbstractJavaElementAction {

	@SuppressWarnings("restriction")
	@Override
	protected void doAction(final IJavaElement javaElement, IProgressMonitor monitor)
			throws CoreException {
		final JavaEditor editor = (JavaEditor) getWindow().getActivePage().getActiveEditor();

		ProjectionAnnotationModel model = (ProjectionAnnotationModel) editor.getAdapter(ProjectionAnnotationModel.class);
		String targetString = javaElement.toString();

		Iterator<?> iterator = model.getAnnotationIterator();
		while (iterator.hasNext()) {
			Object next = iterator.next();
			final Annotation annotation = (Annotation) next;
			
			if (annotation instanceof ProjectionAnnotation) {
				ProjectionAnnotation projection = (ProjectionAnnotation) annotation;
				if (projection.toString().indexOf(targetString) >= 0) {
					final ProjectionAnnotationModel projectionAnnotationModel = (ProjectionAnnotationModel)model;

					new UIJob("reveal") {
						@Override
						public IStatus runInUIThread(IProgressMonitor monitor) {
							if (javaElement instanceof ISourceReference) {
								ISourceReference ref = (ISourceReference) javaElement;
						        IDocument doc = editor.getDocumentProvider().getDocument(editor.getEditorInput());
								try {
									IRegion region = doc.getLineInformationOfOffset(ref.getNameRange().getOffset());
									ISelectionProvider provider = editor.getSelectionProvider();
									int pos = region.getOffset() + region.getLength();
									boolean expended = !((ProjectionAnnotation) annotation).isCollapsed();
									if (expended) {
										provider.setSelection(new TextSelection(doc, pos, 0));
									}
									projectionAnnotationModel.toggleExpansionState(annotation);
								} catch (JavaModelException e) {
									LimyEclipsePluginUtils.log(e);
								} catch (BadLocationException e) {
									LimyEclipsePluginUtils.log(e);
								}
							}
							return Status.OK_STATUS;
						}
					}.schedule();
				}
			}
		}
	}

}