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

import org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitEditorActionContributor;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;
import org.eclipse.ui.texteditor.ITextEditorExtension;
import org.eclipse.ui.texteditor.StatusLineContributionItem;
import org.limy.eclipse.qalab.common.LimyQalabConstants;

/**
 *
 * @author Naoki Iwami
 */
public class QalabEditorContributor extends CompilationUnitEditorActionContributor {

    /** QALab用ステータスバーアイテム(Cc) */
    private StatusLineContributionItem statusFieldCc;

    /** QALab用ステータスバーアイテム(Coverage) */
    private StatusLineContributionItem statusFieldCoverage;

    @Override
    public void setActiveEditor(IEditorPart part) {
        
        super.setActiveEditor(part);
        if (part instanceof ITextEditorExtension) {
            ITextEditorExtension extension = (ITextEditorExtension)part;
            extension.setStatusField(statusFieldCc,
                    LimyQalabConstants.QALAB_CATEGORY_CC);
            extension.setStatusField(statusFieldCoverage,
                    LimyQalabConstants.QALAB_CATEGORY_COVERAGE);
        }
        
    }

    @Override
    public void contributeToStatusLine(IStatusLineManager statusLineManager) {
        
        statusFieldCoverage = new StatusLineContributionItem(
                LimyQalabConstants.QALAB_CATEGORY_COVERAGE, true, 26);
        statusLineManager.add(statusFieldCoverage);

        statusFieldCc = new StatusLineContributionItem(
                LimyQalabConstants.QALAB_CATEGORY_CC, true, 10);
        statusLineManager.add(statusFieldCc);

        // super を最初に呼ぶと、statusFieldは一番最後（右）に追加される
        super.contributeToStatusLine(statusLineManager);
        
        statusLineManager.remove(ITextEditorActionConstants.STATUS_CATEGORY_ELEMENT_STATE);
        statusLineManager.remove(ITextEditorActionConstants.STATUS_CATEGORY_INPUT_MODE);
    }
    
}
