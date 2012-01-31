/*
 * Created 2007/09/01
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

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * 編集位置を履歴で管理するクラスです。
 * @author Naoki Iwami
 */
public final class EditPositionHistoryManager {
    
    /** 唯一のインスタンス */
    private static EditPositionHistoryManager instance = new EditPositionHistoryManager();
    
    /** 過去の編集位置履歴 */
    private List<EditPosition> positions = new ArrayList<EditPosition>();
    
    /**
     * private constructor
     */
    private EditPositionHistoryManager() { }
    
    public static EditPositionHistoryManager getInstance() {
        return instance;
    }
    
    public synchronized void pushPosition(EditPosition position) {
        ListIterator<EditPosition> it = positions.listIterator();
        
        while (it.hasNext()) {
            EditPosition pos = it.next();
            if (pos.getEditorInput().equals(position.getEditorInput())) {
                pos.setPosition(position.getPosition());
                it.remove();
            }
        }
//        List<EditPosition> newPositions = Collections.synchronizedList(positions);
//        for (EditPosition pos : newPositions) {
//            if (pos.getEditorInput().equals(position.getEditorInput())) {
//                pos.setPosition(position.getPosition());
//                newPositions.remove(pos);
//            }
//        }
        positions.add(position);
//        positions = newPositions;
    }
    
    public EditPosition popPosition() {
        if (positions.isEmpty()) {
            return null;
        }
        EditPosition result = positions.get(positions.size() - 1);
        if (positions.size() > 1) {
            positions.remove(positions.size() - 1);
        }
        return result;
    }

}
