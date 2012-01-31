/*
 * Created 2007/03/03
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

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.limy.eclipse.qalab.outline.asm.LineInfo;
import org.limy.eclipse.qalab.outline.asm.MethodInfo;

import antlr.collections.AST;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * @author Naoki Iwami
 */
public class CcParser {
    
    
    // ------------------------ Fields

    /** 現在のカウント */
    private int count;

    /** 対象メソッド名 */
    private final String methodName;

    /** 対象メソッドシグニチャ */
    private final String methodSignature;
    
    // ------------------------ Constructors

    /**
     * CcParser インスタンスを構築します。
     * @param methodName 対象メソッド名
     * @param methodSignature 対象メソッドシグニチャ
     */
    public CcParser(String methodName, String methodSignature) {
        super();
        this.methodName = methodName;
        this.methodSignature = methodSignature;
    }
    
    // ------------------------ Public Methods

    /**
     * メソッド情報を作成します。
     * @param sourceAst ソース解析情報（CheckstyleのTreeWalker使用）
     * @return メソッド情報
     */
    public MethodInfo createMethodInfo(AST sourceAst) {
        
        // デバッグ出力
        ASTUtils.debugSourceAst(sourceAst);

        // メソッド情報を検索
        AST methodAst = ASTUtils.searchMethodFromSource(sourceAst, 
                methodName, methodSignature);
        
        // 子要素からメソッド情報を検索（これ、必要？）
        AST sibling = sourceAst.getNextSibling();
        while (methodAst == null && sibling != null) {
            methodAst = ASTUtils.searchMethodFromSource(sibling, methodName, methodSignature);
            sibling = sibling.getNextSibling();
        }
        
        // メソッド要素からSLIST要素を検索
        AST mainBlockAst = ASTUtils.search(methodAst, TokenTypes.SLIST);
        
        List<LineInfo> lineInfos = new ArrayList<LineInfo>();
        
        // メインブロックを解析してライン情報を作成
        createInfos(mainBlockAst, lineInfos);
        
        if (lineInfos.size() > 1) {
            // 分岐が一つでもあった場合、同一点へのライン情報は削除
            for (ListIterator<LineInfo> it = lineInfos.listIterator(); it.hasNext();) {
                LineInfo lineInfo = it.next();
                if (lineInfo.getFrom() == lineInfo.getTo()) {
                    it.remove();
                }
            }
        }
        
        return new MethodInfo(methodName, null, lineInfos);
    }

    // ------------------------ Private Methods

    /**
     * ルート要素を解析して lineInfos にライン情報を追加します。
     * @param ast SLISTルート要素
     * @param lineInfos ライン情報格納先
     */
    private void createInfos(AST ast, List<LineInfo> lineInfos) {
        
        // ルート要素の中から、ELSE以外の要素を全てリストアップ
        AST[] targetAsts = ASTUtils.searchMultiWithSiblingWithEx(ast, TokenTypes.LITERAL_ELSE,
                TokenTypes.LITERAL_IF,
                TokenTypes.LITERAL_WHILE, TokenTypes.LITERAL_FOR, TokenTypes.LITERAL_DO,
                TokenTypes.LITERAL_SWITCH,
                TokenTypes.LITERAL_TRY,
                TokenTypes.LAND, TokenTypes.LOR, TokenTypes.QUESTION);
        
        if (targetAsts.length == 0) {
            // 一つも見つからなかったら、ライン情報に自身を追加
            appendLineInfo(lineInfos, count, count, 1);
            ++count;
        } else {
            // 一つ以上見つかったら、ライン情報に見つかった全てを追加
            for (AST targetAst : targetAsts) {
                int fromCount = count++;
                
                int type = targetAst.getType();
                if (type == TokenTypes.LITERAL_TRY) {
                    taskTry(lineInfos, targetAst, fromCount);
                } else if (type == TokenTypes.LAND || type == TokenTypes.LOR
                        || type == TokenTypes.QUESTION) {
                    taskAnd(lineInfos, targetAst, fromCount);
                } else if (type == TokenTypes.LITERAL_SWITCH) {
                    taskSwitch(lineInfos, targetAst, fromCount);
                } else {
                    // if,for,while
                    taskIf(lineInfos, targetAst, fromCount);
                }
            }
        }
        
    }

    /**
     * ANDルート要素を解析してライン情報に追加します。
     * @param lineInfos ライン情報格納先
     * @param ast ANDルート要素
     * @param fromCount FROM位置
     */
    private void taskAnd(List<LineInfo> lineInfos, AST ast, int fromCount) {
        
        // 分岐内ライン情報を作成
        List<LineInfo> childInfos = new ArrayList<LineInfo>();
        createInfos(ast.getFirstChild(), childInfos);
        
        // FROM -> 分岐内先頭要素までの線を作成
        appendLineInfoWithLineNumber(lineInfos, fromCount, childInfos.get(0).getFrom(), 1, ast);
        lineInfos.addAll(childInfos);
        
        // 分岐内最終要素 -> 現在要素までの線を作成
        appendLineInfoWithLineNumber(lineInfos,
                childInfos.get(childInfos.size() - 1).getTo(), count,
                1, ast);
        
        // FROM -> 現在要素までの線を作成。これが、分岐が無い場合のメインルート
        appendLineInfoWithLineNumber(lineInfos, fromCount, count, 1, ast);
    }

    private void taskTry(List<LineInfo> lineInfos, AST ast, int fromCount) {
        
        AST mainAst = ASTUtils.search(ast, TokenTypes.SLIST);
        AST[] catchAsts = ASTUtils.searchMultiAllSibling(ast, TokenTypes.LITERAL_CATCH);
        
        List<Integer> lastLines = new ArrayList<Integer>();
        
        List<LineInfo> mainLineInfos = new ArrayList<LineInfo>();
        createInfos(mainAst, mainLineInfos);
        lastLines.add(Integer.valueOf(mainLineInfos.get(mainLineInfos.size() - 1).getTo()));
        
        appendLineInfoWithLineNumber(lineInfos, fromCount, mainLineInfos.get(0).getFrom(), 1, ast);
        lineInfos.addAll(mainLineInfos);
        
        for (AST catchAst : catchAsts) {
            List<LineInfo> catchLineInfos = new ArrayList<LineInfo>();
            createInfos(catchAst, catchLineInfos);
            appendLineInfoWithLineNumber(
                    lineInfos, fromCount, catchLineInfos.get(0).getFrom(), 1, ast);
            lastLines.add(Integer.valueOf(catchLineInfos.get(catchLineInfos.size() - 1).getTo()));
            lineInfos.addAll(catchLineInfos);
        }
        
        for (Integer lineNumber : lastLines) {
            appendLineInfoWithLineNumber(lineInfos, lineNumber.intValue(), count, 1, ast);
        }

    }

    private void taskSwitch(List<LineInfo> lineInfos, AST ast, int fromCount) {
        
        AST[] caseGroupAsts = ASTUtils.searchMultiAllSibling(ast, TokenTypes.CASE_GROUP);
        
        List<Integer> lastLines = new ArrayList<Integer>();

        boolean isDefault = false;
        for (AST caseGroupAst : caseGroupAsts) {
            List<LineInfo> caseInfos = new ArrayList<LineInfo>();
            isDefault |= taskCase(caseInfos, caseGroupAst, fromCount);
            lastLines.add(Integer.valueOf(caseInfos.get(caseInfos.size() - 1).getTo()));
            lineInfos.addAll(caseInfos);
        }
        
        if (!isDefault) {
            // 明示的なdefault節が存在しない場合
            appendLineInfoWithLineNumber(lineInfos, fromCount, count, 100, ast);
            lastLines.add(Integer.valueOf(count));
        }
        
        for (Integer lineNumber : lastLines) {
            appendLineInfo(lineInfos, lineNumber.intValue(), count, 1);
        }
    
    }

    private boolean taskCase(List<LineInfo> lineInfos, AST ast, int fromCount) {
        AST[] caseAsts = ASTUtils.searchMultiAllSibling(ast, TokenTypes.LITERAL_CASE);
        if (caseAsts.length == 0) {
            caseAsts = ASTUtils.searchMultiAllSibling(ast, TokenTypes.LITERAL_DEFAULT);
        }
        
        int mainCount = count + caseAsts.length;
        for (AST caseAst : caseAsts) {
            appendLineInfoWithLineNumber(lineInfos, fromCount, count, 1, ast);
            appendLineInfoWithLineNumber(lineInfos, count, mainCount, 1, ast);
            ++count;
        }
        
        AST slistAst = ASTUtils.search(ast, TokenTypes.SLIST);
        createInfos(slistAst, lineInfos);
        
        return ASTUtils.search(ast, TokenTypes.LITERAL_DEFAULT) != null;
    }

    private void taskIf(List<LineInfo> lineInfos, AST ast, int fromCount) {
        
        // 条件文の中を調べる（&& や ||）
        List<LineInfo> exprInfos = new ArrayList<LineInfo>();
        AST exprAst = ASTUtils.search(ast, TokenTypes.EXPR);
        createInfos(exprAst.getFirstChild(), exprInfos);
        
        // 自身から条件文の先頭へのライン（A->I0）
        appendLineInfo(lineInfos, fromCount, exprInfos.get(0).getFrom(), 1);
        lineInfos.addAll(exprInfos);
        
        List<LineInfo> mainInfos = new ArrayList<LineInfo>();
        AST innerAst = ASTUtils.search(ast, TokenTypes.RPAREN); // if (...) の右括弧を検出
        if (exprInfos.size() > 1) {
            ++count; // 条件文内で分岐があった場合だけカウント増加
        }
        createInfos(innerAst, mainInfos);
        
        // 条件文の最後からifブロックの先頭へのライン（I1->B0）
        int newFromCount = exprInfos.get(exprInfos.size() - 1).getTo();
        appendLineInfo(lineInfos, newFromCount, mainInfos.get(0).getFrom(), 1);
        lineInfos.addAll(mainInfos);

        // elseブロックを検索
        AST elseAst = ASTUtils.searchSelfAndSiblings(innerAst, TokenTypes.LITERAL_ELSE);
        if (elseAst != null) {
            // elseブロックが存在する場合
            
            ++count;
            List<LineInfo> elseInfos = new ArrayList<LineInfo>();
            createInfos(elseAst.getFirstChild(), elseInfos);

            // 条件文の最後からelseブロックの先頭へのライン（A->E0）
            appendLineInfoWithLineNumber(lineInfos,
                    newFromCount, elseInfos.get(0).getFrom(), 1, ast);
            lineInfos.addAll(elseInfos);
            
            // ifブロックの最後から直後のポイントへのライン（B1->C）
            appendLineInfo(lineInfos, mainInfos.get(mainInfos.size() - 1).getTo(), count,
                    1);

            // elseブロックの最後から直後のポイントへのライン（E1->C）
            appendLineInfo(lineInfos, elseInfos.get(elseInfos.size() - 1).getTo(), count,
                    1);

        } else {
            // ifブロックの最後から直後のポイントへのライン（B1->C）
            appendLineInfo(lineInfos, mainInfos.get(mainInfos.size() - 1).getTo(), count,
                    1);

            // 自身から直後のポイントへの(暗黙else)ライン（A->C）
            appendLineInfoWithLineNumber(lineInfos, newFromCount, count, 100, ast);
        }
    }

    /**
     * ライン情報を追加します。行番号付き
     * @param lineInfos ライン情報追加先
     * @param from FROM位置
     * @param to TO位置
     * @param weight ウェイト
     * @param ast ルート要素
     */
    private void appendLineInfoWithLineNumber(List<LineInfo> lineInfos, int from,
            int to, int weight, AST ast) {
        
        int lineNumber = ((DetailAST)ast).getLineNo();
        String text = "[L." + lineNumber + "]";
        lineInfos.add(new LineInfo(from, to, weight, lineNumber, text));
    }

    /**
     * ライン情報を追加します。
     * @param lineInfos ライン情報追加先
     * @param from FROM位置
     * @param to TO位置
     * @param weight ウェイト
     */
    private void appendLineInfo(List<LineInfo> lineInfos, int from,
            int to, int weight) {
        
        lineInfos.add(new LineInfo(from, to, weight, 0, "."));
    }

}
