/*
 * Created 2007/02/24
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

import org.eclipse.debug.ui.actions.ToggleBreakpointAction;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.source.IVerticalRulerInfo;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.IActionDelegate2;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.texteditor.AbstractRulerActionDelegate;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * エディタ内のルーラー（左側のバー）をダブルクリックしたときのアクションクラスです。
 * @author Naoki Iwami
 */
public class DoubleClickRulerAction extends AbstractRulerActionDelegate
        implements IActionDelegate2 {
    
    /** editor */
    private IEditorPart fEditor;
    
    /** action */
    private ToggleBreakpointAction fDelegate;

    protected IAction createAction(ITextEditor editor, IVerticalRulerInfo rulerInfo) {
        fDelegate = new ToggleBreakpointAction(editor, null, rulerInfo);
        return fDelegate;
    }

    public void setActiveEditor(IAction callerAction, IEditorPart targetEditor) {
        if (fEditor != null && fDelegate != null) {
            fDelegate.dispose();
            fDelegate = null;
        }
        fEditor = targetEditor;
        super.setActiveEditor(callerAction, targetEditor);
    }

    public void init(IAction action) {
        // do nothing
    }

    public void dispose() {
        if (fDelegate != null) {
            fDelegate.dispose();
        }
        fDelegate = null;
        fEditor = null;
    }

    public void runWithEvent(IAction action, Event event) {
        run(action);
    }
    
}
