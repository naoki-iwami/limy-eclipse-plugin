/*
 * Created 2006/08/20
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
package org.limy.velocity;

/**
 * vmstyleタスクに使用されるパラメータクラスです。
 * @author Naoki Iwami
 */
public class VmParam {
    
    /**
     * パラメータ名
     */
    private String name;
    
    /**
     * パラメータ値
     */
    private Object expression;

    // ------------------------ Constructors

    /**
     * VmParam インスタンスを構築します。
     */
    public VmParam() {
        super();
    }

    /**
     * VmParam インスタンスを構築します。
     * @param name パラメータ名
     * @param expression パラメータ値
     */
    public VmParam(String name, Object expression) {
        super();
        this.name = name;
        this.expression = expression;
    }
    
    /**
     * パラメータ値を設定します。
     * @param expression パラメータ値
     */
    public void setObjectExpression(Object expression) {
        this.expression = expression;
    }

    // ------------------------ Getter/Setter Methods

    /**
     * パラメータ名を取得します。
     * @return パラメータ名
     */
    public String getName() {
        return name;
    }

    /**
     * パラメータ名を設定します。
     * @param name パラメータ名
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * パラメータ値を取得します。
     * @return パラメータ値
     */
    public Object getExpression() {
        return expression;
    }

    /**
     * パラメータ値を設定します。
     * @param expression パラメータ値
     */
    public void setExpression(String expression) {
        this.expression = expression;
    }

}
