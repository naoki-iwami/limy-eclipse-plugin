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

/**
 * グラフに使用する線の情報を表します。
 * @author Naoki Iwami
 */
public class LineInfo {
    
    /** from位置 */
    private int from;
    
    /** to位置 */
    private int to;

    /** 比重 */
    private int weight;

    /** 行番号 */
    private int lineNumber;
    
    /** ラベル文字 */
    private String text;

    // ------------------------ Constructors

    /**
     * LineInfoインスタンスを構築します。
     * @param from from位置
     * @param to to位置
     * @param weight 比重
     * @param lineNumber 行番号
     * @param text ラベル文字
     */
    public LineInfo(int from, int to, int weight, int lineNumber, String text) {
        super();
        this.from = from;
        this.to = to;
        this.weight = weight;
        this.lineNumber = lineNumber;
        this.text = text;
    }

    // ------------------------ Override Methods

    @Override
    public String toString() {
        return "[" + from + " -> " + to + "] " + text; 
    }
    
    // ------------------------ Getter/Setter Methods

    /**
     * from位置を取得します。
     * @return from位置
     */
    public int getFrom() {
        return from;
    }

    /**
     * from位置を設定します。
     * @param from from位置
     */
    public void setFrom(int from) {
        this.from = from;
    }

    /**
     * to位置を取得します。
     * @return to位置
     */
    public int getTo() {
        return to;
    }

    /**
     * to位置を設定します。
     * @param to to位置
     */
    public void setTo(int to) {
        this.to = to;
    }

    /**
     * 比重を取得します。
     * @return 比重
     */
    public int getWeight() {
        return weight;
    }

    /**
     * 比重を設定します。
     * @param weight 比重
     */
    public void setWeight(int weight) {
        this.weight = weight;
    }

    /**
     * ラベル文字を取得します。
     * @return ラベル文字
     */
    public String getText() {
        if (text == null) {
            return ".";
//            if (lineNumber == 0) {
//                return "";
//            }
//            return "L." + Integer.toString(lineNumber);
        }
        return text;
    }

    /**
     * ラベル文字を設定します。
     * @param text ラベル文字
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * 行番号を取得します。
     * @return 行番号
     */
    public int getLineNumber() {
        return lineNumber;
    }
}
