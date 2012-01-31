/*
 * Created 2006/11/22
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
package org.limy.eclipse.qalab.task;

/**
 * todo用Beanクラスです。
 * @author Naoki Iwami
 */
public class TodoError {
    
    /** 行番号 */
    private final int line;
    
    /** メッセージ */
    private final String message;

    /**
     * TodoErrorインスタンスを構築します。
     * @param line
     * @param message
     */
    public TodoError(int line, String message) {
        super();
        this.line = line;
        this.message = message;
    }

    /**
     * 行番号を取得します。
     * @return 行番号
     */
    public int getLine() {
        return line;
    }

    /**
     * メッセージを取得します。
     * @return メッセージ
     */
    public String getMessage() {
        return message;
    }
    
}