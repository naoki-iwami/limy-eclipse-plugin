/*
 * Created 2007/02/26
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
package org.limy.eclipse.qalab.outline.asm;

import java.util.ArrayList;
import java.util.Collection;

import org.objectweb.asm.Label;

/**
 * インストラクションポインタの情報を表します。
 * @author Naoki Iwami
 */
public class PointInfo {

    /** カウンタ */
    private final int count;
    
    /** ジャンプのみ定義 */
    private boolean jumpOnly;
    
    /** 自身のラベル */
    private Label self;
    
    /** 行番号 */
    private int lineNumber;
    
    /** info文字列 */
    private String info;

    /** 遷移先のラベル */
    private Collection<Label> jumpTos = new ArrayList<Label>();

    // ------------------------ Constructors

    /**
     * PointInfoインスタンスを構築します。
     * @param count カウンタ
     * @param self 自身のラベル
     * @param lineNumber 行番号
     * @param info info文字列
     */
    public PointInfo(int count, Label self, int lineNumber, String info) {
        super();
        this.count = count;
        this.self = self;
        this.info = info;
        this.lineNumber = lineNumber;
    }

    /**
     * PointInfoインスタンスを構築します。
     * @param count カウンタ
     * @param self 自身のラベル
     * @param jumpTo 遷移先のラベル
     * @param jumpOnly 
     * @param lineNumber 行番号
     * @param info info文字列
     */
    public PointInfo(int count, Label self, Label jumpTo, boolean jumpOnly,
            int lineNumber, String info) {
        super();
        this.count = count;
        this.self = self;
        this.jumpOnly = jumpOnly;
        this.info = info;
        this.lineNumber = lineNumber;
        jumpTos.add(jumpTo);
    }
    
    /**
     * PointInfoインスタンスを構築します。
     * @param count カウンタ
     * @param self 自身のラベル
     * @param jumpTos 遷移先のラベル一覧
     * @param lineNumber 行番号
     * @param info info文字列
     */
    public PointInfo(int count, Label self, Collection<Label> jumpTos,
            int lineNumber, String info) {
        super();
        this.count = count;
        this.self = self;
        this.jumpTos = jumpTos;
        this.jumpOnly = true;
        this.info = info;
        this.lineNumber = lineNumber;
    }

    // ------------------------ Getter/Setter Methods

    /**
     * カウンタを取得します。
     * @return カウンタ
     */
    public int getCount() {
        return count;
    }
    
    /**
     * 自身のラベルを取得します。
     * @return 自身のラベル
     */
    public Label getSelf() {
        return self;
    }

    /**
     * ジャンプのみ定義を取得します。
     * @return ジャンプのみ定義
     */
    public boolean isJumpOnly() {
        return jumpOnly;
    }
    
    /**
     * 遷移先のラベル一覧を取得します。
     * @return 遷移先のラベル一覧
     */
    public Collection<Label> getJumpTos() {
        return jumpTos;
    }

    /**
     * info文字列を取得します。
     * @return info文字列
     */
    public String getInfo() {
        return info;
    }

    /**
     * 行番号を取得します。
     * @return 行番号
     */
    public int getLineNumber() {
        return lineNumber;
    }

}
