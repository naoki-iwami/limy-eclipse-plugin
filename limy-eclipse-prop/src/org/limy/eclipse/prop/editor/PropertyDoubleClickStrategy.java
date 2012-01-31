/*
 * Created 2005/07/21
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
package org.limy.eclipse.prop.editor;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.ITextViewer;
import org.limy.eclipse.common.LimyEclipsePluginUtils;
import org.limy.eclipse.prop.editor.PropertyDoubleClickStrategy.PosAndType.SelectType;

/**
 * プロパティエディタ内でダブルクリックしたときの処理を定義したクラスです。
 * @author Naoki Iwami
 */
public class PropertyDoubleClickStrategy implements ITextDoubleClickStrategy {

    /**
     * 選択開始位置および選択種別を表します。
     * @author Naoki Iwami
     */
    static class PosAndType {
        
        /**
         * 選択種別を表します。
         * @author Naoki Iwami
         */
        enum SelectType {
            /** name選択 */
            NAME,
            /** value選択 */
            VALUE;
        }
        
        /** 対象文字位置 */
        private int targetPos;
        
        /** 選択種別 */
        private SelectType type;

        protected int getTargetPos() {
            return targetPos;
        }

        protected SelectType getType() {
            return type;
        }
        
        protected void setTypeAnd(SelectType type, int targetPos) {
            this.type = type;
            this.targetPos = targetPos;
        }
        
    }
    
    // ------------------------ Fields

    /** 現在処理中のテキストビューア */
    private ITextViewer viewer;
    
    // ------------------------ Implement Methods

    public void doubleClicked(ITextViewer viewer) {

        this.viewer = viewer;

        try {
            
            if (initCheck(viewer)) {
                return;
            }
            
            PosAndType posType = decideSelectType();
            
            if (posType.getType() == SelectType.VALUE) {
                // value選択
                selectTextValue(posType.getTargetPos());
            } else {
                // name選択
                selectTextName(posType.getTargetPos());
            }
        
        } catch (BadLocationException e) {
            LimyEclipsePluginUtils.log(e);
        }
    }


    // ------------------------ Private Methods

    /**
     * 現在処理中のドキュメントを取得します。
     * @return 現在処理中のドキュメント
     */
    private IDocument getDocument() {
        return viewer.getDocument();
    }
    
    /**
     * クリックしたカーソル位置を返します。
     * @return クリックしたカーソル位置
     */
    private int getSelectedPos() {
        return viewer.getSelectedRange().x; // クリックしたカーソル位置
    }

    /**
     * 初期チェックを行います。
     * @param viewer ITextViewer
     * @return その後の処理を行わない場合は true
     * @throws BadLocationException
     */
    private boolean initCheck(ITextViewer viewer) throws BadLocationException {
        
        int pos = getSelectedPos();
        if (pos >= getDocument().getLength()) {
            return true; // アサーションも兼ねて範囲チェック
        }
        
        if (getDocument().getChar(pos) == '=') {
            // '=' 文字をクリックした場合、'=' のみを選択して終了
            viewer.setSelectedRange(pos, 1);
            return true;
        }
        
        return false;
    }

    /**
     * カーソルより前方向に文字を解析して、選択開始位置と選択種別を決定します。
     * @return 選択開始位置と選択種別
     * @throws BadLocationException 
     */
    private PosAndType decideSelectType() throws BadLocationException {
        
        IDocument doc = getDocument();

        PosAndType posType = new PosAndType();

        int posMark = -1; // 「=」記号が見つかった位置
        boolean flagPrevLine = false; // 解析が前行に突入した直後に true にする

        // カーソル位置から前方向に検索
        for (int offset = getSelectedPos(); offset >= 0; offset--) {
            char c = doc.getChar(offset);
            int line = doc.getLineOfOffset(offset); // 行番号を取得

            if (flagPrevLine) {
                // 前行に解析が突入した直後、ここに来る
                if (c == '\\') {
                    // 前行の行末が '\' で終了していた場合、さらに解析を続ける
                    flagPrevLine = false;

                    // もし以前の解析で '=' が見つかっていたとしたら、それをvalueの一部と見なす
                    posMark = -1;

                } else if (!Character.isWhitespace(c)) { // スペース文字は無視
                    // 前行の行末が '\' やスペース文字以外だった場合、もう解析をする必要は無いので終了
                    if (posMark >= 0) {
                        // '=' が見つかっている場合、value選択
                        posType.setTypeAnd(SelectType.VALUE, posMark + 1);
                    } else {
                        // '=' が見つかっていない場合、name選択
                        posType.setTypeAnd(SelectType.NAME, doc.getLineOffset(line + 1));
                    }
                    break;
                }
            } else {
                if (c == '=') {
                    posMark = offset; // 「=」記号が見つかった。この場合、現在選択中の位置は「=」以降
                }
            }

            if (doc.getLineOffset(line) == offset) {
                // 行頭に到達した場合、newLineフラグを立てる。さらに前の行まで解析は続ける
                flagPrevLine = true;
            }

            checkFirstDocument(offset, posMark, posType);
        }
        return posType;
    }

    /**
     * ドキュメントの先頭をチェックします。
     * @param offset 現在解析中の文字位置
     * @param posMark '='文字が見つかった位置
     * @param posType チェック内容格納インスタンス
     */
    private void checkFirstDocument(int offset, int posMark, PosAndType posType) {
        if (offset == 0) {
            // ドキュメントの先頭まで解析が達した場合
            if (posMark >= 0) {
                posType.setTypeAnd(SelectType.VALUE, posMark + 1);
            } else {
                posType.setTypeAnd(SelectType.NAME, 0);
            }
        }
    }
    
    /**
     * value文字列を選択します。
     * @param start '='文字の直後
     * @throws BadLocationException 
     */
    private void selectTextValue(int start) throws BadLocationException {
        
        IDocument doc = getDocument();

        boolean isCont = false;
        // カーソル位置から後方向に検索
        for (int offset = getSelectedPos(); offset <= doc.getLength(); offset++) {
            
            if (offset == doc.getLength()) {
                // ドキュメントの最後まで解析が達した場合、最後までを選択して完了
                selectText(start, doc.getLength());
                break;
            }

            char c = doc.getChar(offset);
            int line = doc.getLineOfOffset(offset); // 行番号を取得
            
            if (c == '\\') {
                // '\'文字が見つかった場合、isContフラグを立てる
                isCont = true;
            } else if (doc.getLineOffset(line) == offset) {
                // 行頭の場合、ここに来る
                if (isCont) {
                    // 直前の文字が '\' だった場合、複数行 value として解析を続ける
                    isCont = false;
                } else {
                    // 直前の文字が '\' 以外だった場合、解析終了。valueを選択
                    selectText(start, offset);
                    return;
                }
            } else if (!Character.isWhitespace(c)) {
                // 行末以外の '\' は isCont フラグを元に戻す
                isCont = false;
            }
        }
    }

    /**
     * name文字列を選択します。
     * @param start 行頭文字位置
     * @throws BadLocationException 
     */
    private void selectTextName(int start) throws BadLocationException {
        
        IDocument doc = getDocument();

        // カーソル位置から後方向に検索
        for (int offset = getSelectedPos(); offset <= doc.getLength(); offset++) {
            
            char c = doc.getChar(offset);
            int line = doc.getLineOfOffset(offset); // 行番号を取得
            
            if (c == '=') {
                // '=' が見つかったら解析終了。nameを選択
                selectText(start, offset);
                return;
            } else if (doc.getLineOffset(line) == offset) {
                return;
            }
        }
    }

    /**
     * テキストを選択します。
     * @param start 選択開始位置
     * @param end 選択終了位置
     * @throws BadLocationException ポジション例外
     */
    private void selectText(int start, int end) throws BadLocationException {
        IDocument doc = getDocument();
        int startPos = start;
        for (; startPos <= end; startPos++) {
            if (!Character.isWhitespace(doc.getChar(startPos))) {
                break;
            }
        }
        int endPos = end;
        for (; endPos > startPos; endPos--) {
            if (!Character.isWhitespace(doc.getChar(endPos - 1))) {
                break;
            }
        }
        viewer.setSelectedRange(startPos, endPos - startPos);
    }

}
