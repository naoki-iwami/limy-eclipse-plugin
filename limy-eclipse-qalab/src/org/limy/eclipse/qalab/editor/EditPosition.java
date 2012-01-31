/*
 * Created 2007/09/02
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

import org.eclipse.jface.text.Position;
import org.eclipse.ui.IEditorInput;

/**
 * エディタ内ポジションを表します。
 * @author Naoki Iwami
 */
public class EditPosition {

    /** エディタ */
    private final IEditorInput editorInput;
    
    /** エディタID */
    private final String editorId;
    
    /** テキストポジション */
    private Position position;

    /**
     * EditPosition インスタンスを構築します。
     * @param editorInput エディタ
     * @param editorId エディタID
     * @param position テキストポジション
     */
    public EditPosition(IEditorInput editorInput, String editorId,
            Position position) {
        super();
        this.editorInput = editorInput;
        this.editorId = editorId;
        this.position = position;
    }

    /**
     * エディタを取得します。
     * @return エディタ
     */
    public IEditorInput getEditorInput() {
        return editorInput;
    }

    /**
     * エディタIDを取得します。
     * @return エディタID
     */
    public String getEditorId() {
        return editorId;
    }

    /**
     * テキストポジションを取得します。
     * @return テキストポジション
     */
    public Position getPosition() {
        return position;
    }

    /**
     * テキストポジションを設定します。
     * @param position テキストポジション
     */
    public void setPosition(Position position) {
        this.position = position;
    }

}
