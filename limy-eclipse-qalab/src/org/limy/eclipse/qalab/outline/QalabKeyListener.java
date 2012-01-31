/*
 * Created 2007/08/30
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
package org.limy.eclipse.qalab.outline;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.limy.eclipse.common.LimyEclipsePluginUtils;

/**
 * @author Naoki Iwami
 */
public class QalabKeyListener extends KeyAdapter {

    /** キーに応じてアクションを実行するExecutor */
    private final KeyExecutor keyExecutor;
    
    /**
     * QalabKeyListener インスタンスを構築します。
     * @param keyExecutor キーに応じてアクションを実行するExecutor
     */
    public QalabKeyListener(KeyExecutor keyExecutor) {
        super();
        this.keyExecutor = keyExecutor;
    }
    
    @Override
    public void keyPressed(KeyEvent evt) {
        try {
            keyExecutor.execute(evt.character);
        } catch (CoreException e) {
            LimyEclipsePluginUtils.log(e);
        }
        super.keyPressed(evt);
    }

}
