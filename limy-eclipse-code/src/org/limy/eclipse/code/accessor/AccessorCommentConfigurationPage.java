package org.limy.eclipse.code.accessor;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.ltk.ui.refactoring.UserInputWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class AccessorCommentConfigurationPage extends UserInputWizardPage {

    public AccessorCommentConfigurationPage() {
        super("AccessoCommentConfigurationPage");
    }

    public void createControl(Composite parent) {
        Composite result = new Composite(parent, SWT.NONE);
        
        result.setLayout(new FillLayout());
        Label label = new Label(result, SWT.NONE);
        label.setText("àÍäáÇ≈ Accessor ÇÃJavadocÉRÉÅÉìÉgÇê∂ê¨ÇµÇ‹Ç∑ÅB");
        parent.getShell().setText("Modify Accessor Comment");
        
        setControl(result);
        Dialog.applyDialogFont(result);
        
    }

}
