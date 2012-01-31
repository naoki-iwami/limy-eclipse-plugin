/*
 * Created 2008/08/23
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
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;

import antlr.collections.AST;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * AST関連のユーティリティクラスです。
 * @author Naoki Iwami
 */
public final class ASTUtils {

    // ------------------------ Constants

    /** プリミティブ型 => シグニチャ文字への変換マップ */
    private static final Map<Integer, Character> PRIMITIVE_MAP;
    
    static {
        PRIMITIVE_MAP = new HashMap<Integer, Character>();
        PRIMITIVE_MAP.put(Integer.valueOf(TokenTypes.LITERAL_BOOLEAN), Character.valueOf('Z'));
        PRIMITIVE_MAP.put(Integer.valueOf(TokenTypes.LITERAL_BYTE), Character.valueOf('B'));
        PRIMITIVE_MAP.put(Integer.valueOf(TokenTypes.LITERAL_CHAR), Character.valueOf('C'));
        PRIMITIVE_MAP.put(Integer.valueOf(TokenTypes.LITERAL_DOUBLE), Character.valueOf('D'));
        PRIMITIVE_MAP.put(Integer.valueOf(TokenTypes.LITERAL_FLOAT), Character.valueOf('F'));
        PRIMITIVE_MAP.put(Integer.valueOf(TokenTypes.LITERAL_INT), Character.valueOf('I'));
        PRIMITIVE_MAP.put(Integer.valueOf(TokenTypes.LITERAL_SHORT), Character.valueOf('S'));
        PRIMITIVE_MAP.put(Integer.valueOf(TokenTypes.LITERAL_LONG), Character.valueOf('J'));
        PRIMITIVE_MAP.put(Integer.valueOf(TokenTypes.ARRAY_DECLARATOR), Character.valueOf('['));
    }
    
    /**
     * private constructor
     */
    private ASTUtils() { }

    /**
     * ソースルート要素からメソッド要素を検索して返します。
     * @param sourceAst ソースルート要素
     * @param method メソッド
     * @return　メソッド要素
     * @throws JavaModelException 
     */
    public static AST searchMethodFromSource(AST sourceAst, IMethod method)
            throws JavaModelException {
        
        String methodName = method.getElementName();
        String methodSignature = method.getSignature();
        
        // ルート要素から対象メソッドを検索。見つかればそれを返す
        AST result = ASTUtils.searchTargetMethod(sourceAst, methodName, methodSignature);
        if (result != null) {
            return result;
        }
        
        // 全ての子要素から対象メソッドを検索。見つかればそれを返す
        AST sibling = sourceAst.getNextSibling();
        while (sibling != null && result == null) {
            result = ASTUtils.searchTargetMethod(sibling, methodName, methodSignature);
            sibling = sibling.getNextSibling();
        }
        return result;
    }

    /**
     * ソースルート要素からメソッド要素を検索して返します。
     * @param sourceAst ソースルート要素
     * @param methodName 検索するメソッド名
     * @param methodSignature 検索するメソッドシグニチャ
     * @return　メソッド要素
     */
    public static AST searchMethodFromSource(AST sourceAst,
            String methodName, String methodSignature) {
        
        // ルート要素から対象メソッドを検索。見つかればそれを返す
        AST result = ASTUtils.searchTargetMethod(sourceAst, methodName, methodSignature);
        if (result != null) {
            return result;
        }
        
        // 全ての子要素から対象メソッドを検索。見つかればそれを返す
        AST sibling = sourceAst.getNextSibling();
        while (sibling != null && result == null) {
            result = ASTUtils.searchTargetMethod(sibling, methodName, methodSignature);
            sibling = sibling.getNextSibling();
        }
        return result;
    }

    /**
     * ソースルート要素からメソッド要素を検索して返します。
     * @param sourceAst ソースルート要素
     * @param methodName 検索するメソッド名
     * @param methodSignature 検索するメソッドシグニチャ
     * @return メソッド要素
     */
    private static AST searchTargetMethod(AST sourceAst,
            String methodName, String methodSignature) {
        
        // OBJBLOCK要素を探す。これがクラス要素
        AST mainAst = ASTUtils.search(sourceAst, TokenTypes.OBJBLOCK);
        if (mainAst == null) {
            return null;
        }
        
        // METHOD_DEF要素（複数）を探す。これがクラスに含まれるメソッド要素
        AST[] targetAsts = ASTUtils.searchMultiAllSibling(mainAst,
                TokenTypes.METHOD_DEF, TokenTypes.CTOR_DEF);
        for (AST targetAst : targetAsts) {
            String name = ASTUtils.getIdent(targetAst); // メソッド名称を取得
            if (!methodName.equals(name)) {
                continue;
            }
            
            // メソッド名称およびシグニチャが、フィールドに格納した値と等しければそれを返す。
            String paramSignature = ASTUtils.getMethodParamSignature(targetAst);
            if (methodSignature.startsWith(paramSignature)) {
                return targetAst;
            }
        }
        return null;
    }

    /**
     * ルート要素（および子・孫）の中から、指定したタイプの要素を探して返します。
     * @param ast ルート要素
     * @param type 検索タイプ。TokenTypes
     * @return 見つかった要素
     */
    public static AST search(AST ast, int type) {
        if (ast.getType() == type) {
            return ast; // ルート要素そのものが指定したタイプだった場合
        }
        
        // 全ての子（および孫）要素を検索
        AST child = ast.getFirstChild();
        while (child != null) {
            AST result = search(child, type);
            if (result != null) {
                return result;
            }
            child = child.getNextSibling();
        }
        return null;
    }

    /**
     * 自身およびその兄弟要素の中から、指定したタイプの要素を探して返します。
     * @param ast ルート要素
     * @param type 検索タイプ。TokenTypes
     * @return 見つかった要素
     */
    public static AST searchWithSibling(AST ast, int type) {
        if (ast.getType() == type) {
            return ast; // ルート要素そのものが指定したタイプだった場合
        }
        
        // 全ての兄弟要素を検索
        AST child = ast.getNextSibling();
        while (child != null) {
            AST result = search(child, type);
            if (result != null) {
                return result;
            }
            child = child.getNextSibling();
        }
        return null;
    }

    /**
     * 直属の子要素の中から、指定したタイプの要素を探して返します。
     * @param ast ルート要素
     * @param type 検索タイプ。TokenTypes
     * @return
     */
    public static AST searchOnlySibling(AST ast, int type) {
        AST sibling = ast.getFirstChild();
        while (sibling != null) {
            if (sibling.getType() == type) {
                return sibling;
            }
            sibling = sibling.getNextSibling();
        }
        return null;
    }

    /**
     * 直属の子要素の中から、指定したタイプの要素を探して返します。
     * @param ast ルート要素
     * @param type 検索タイプ。TokenTypes
     * @return
     */
    public static AST[] searchOnlySiblings(AST ast, int type) {
        Collection<AST> results = new ArrayList<AST>();
        AST sibling = ast.getFirstChild();
        while (sibling != null) {
            if (sibling.getType() == type) {
                results.add(sibling);
            }
            sibling = sibling.getNextSibling();
        }
        return results.toArray(new AST[results.size()]);
    }

    /**
     * 自身とその兄弟要素の中から、指定したタイプの要素を探して返します。
     * @param ast ルート要素
     * @param type 検索タイプ。TokenTypes
     * @return
     */
    public static AST searchSelfAndSiblings(AST ast, int type) {
        if (ast.getType() == type) {
            return ast;
        }
        AST sibling = ast.getNextSibling();
        while (sibling != null) {
            if (sibling.getType() == type) {
                return sibling;
            }
            sibling = sibling.getNextSibling();
        }
        return null;
    }

    /**
     * ルート要素から、指定したタイプ（ただし除外タイプでないもの）を全て探します。
     * @param ast ルート要素
     * @param excludeType 除外タイプ（ルート直属の子要素にのみ適用）
     * @param types 対象タイプ
     * @return 見つかった要素一覧
     */
    public static AST[] searchMultiWithSiblingWithEx(AST ast, int excludeType, int... types) {
        Collection<AST> results = new ArrayList<AST>();
        
        // ルート要素から探す
        results.addAll(Arrays.asList(searchMultiAllSibling(ast, types)));
        
        // 子要素から探す
        AST sibling = ast.getNextSibling();
        while (sibling != null) {
            if (sibling.getType() == excludeType) {
                break;
            }
            results.addAll(Arrays.asList(searchMultiAllSibling(sibling, types)));
            sibling = sibling.getNextSibling();
        }
        
        return results.toArray(new AST[results.size()]);
    }

    /**
     * ルート要素の中から、指定したタイプの要素（複数）を探して返します。
     * @param ast ルート要素
     * @param types タイプ（複数）。TokenTypes
     * @return 見つかった要素
     */
    public static AST[] searchMultiAllSibling(AST ast, int... types) {
        Collection<AST> results = new ArrayList<AST>();
        for (int type : types) {
            // ルート要素が指定したタイプのうちのいずれかだったら、ルート要素をそのまま返す
            if (ast.getType() == type) {
                return new AST[] { ast };
            }
        }
        
        // 全ての子（および孫）要素を検索
        AST child = ast.getFirstChild();
        while (child != null) {
            searchMultiAndStore(results, child, types);
            child = child.getNextSibling();
        }
        return results.toArray(new AST[results.size()]);
    }

    /**
     * ルート要素の中から、指定したタイプの要素（複数）を探して返します。ただし除外タイプを除く
     * @param ast ルート要素
     * @param excludeTypes 除外タイプ一覧。必ずソート済であること
     * @param types タイプ（複数）。TokenTypes
     * @return 見つかった要素
     */
    public static AST[] searchMultiAllSiblingEx(AST ast,
            int[] excludeTypes, int... types) {
        Collection<AST> results = new ArrayList<AST>();
        for (int type : types) {
            // ルート要素が指定したタイプのうちのいずれかだったら、ルート要素をそのまま返す
            if (ast.getType() == type) {
                return new AST[] { ast };
            }
        }
        
        // 全ての子（および孫）要素を検索
        AST child = ast.getFirstChild();
        while (child != null) {
            if (Arrays.binarySearch(excludeTypes, child.getType()) < 0) {
                searchMultiAndStoreEx(results, child, excludeTypes, types);
            }
            child = child.getNextSibling();
        }
        return results.toArray(new AST[results.size()]);
    }

    /**
     * ルート要素の中から、指定したタイプの要素（複数）を探して results に追加します。
     * @param results 結果格納先
     * @param ast ルート要素
     * @param types タイプ（複数）。TokenTypes
     */
    private static void searchMultiAndStore(Collection<AST> results, AST ast, int... types) {
        for (int type : types) {
            // ルート要素が指定したタイプのうちのいずれかだったら、ルート要素を results に追加
            if (ast.getType() == type) {
                results.add(ast);
                return;
            }
        }
        
        // 全ての子（および孫）要素を検索
        AST child = ast.getFirstChild();
        while (child != null) {
            searchMultiAndStore(results, child, types);
            child = child.getNextSibling();
        }
    }

    /**
     * ルート要素の中から、指定したタイプの要素（複数）を探して results に追加します。
     * @param results 結果格納先
     * @param ast ルート要素
     * @param excludeTypes 除外タイプ一覧。必ずソート済であること
     * @param types タイプ（複数）。TokenTypes
     */
    private static void searchMultiAndStoreEx(Collection<AST> results, AST ast,
            int[] excludeTypes, int... types) {
        
        if (Arrays.binarySearch(excludeTypes, ast.getType()) >= 0) {
            return;
        }
        for (int type : types) {
            // ルート要素が指定したタイプのうちのいずれかだったら、ルート要素を results に追加
            if (ast.getType() == type) {
                results.add(ast);
                return;
            }
        }
        
        // 全ての子（および孫）要素を検索
        AST child = ast.getFirstChild();
        while (child != null) {
            if (Arrays.binarySearch(excludeTypes, child.getType()) < 0) {
                searchMultiAndStoreEx(results, child, excludeTypes, types);
            }
            child = child.getNextSibling();
        }
    }

    /**
     * ルート要素のIDを返します。
     * @param ast ルート要素
     * @return ID
     */
    public static String getIdent(AST ast) {
        // 子要素全てをループして、TokenTypes.IDENT のものを探す
        AST child = ast.getFirstChild();
        while (child != null) {
            if (child.getType() == TokenTypes.IDENT) {
                return child.getText();
            }
            child = child.getNextSibling();
        }
        return null;
    }

    /**
     * メソッド要素の引数のシグニチャを返します。
     * @param targetAst ルート要素（メソッド）
     * @return シグニチャ
     */
    public static String getMethodParamSignature(AST targetAst) {
        AST paramsAst = search(targetAst, TokenTypes.PARAMETERS);
        AST[] params = searchMultiAllSibling(paramsAst, TokenTypes.PARAMETER_DEF);
        
        StringBuilder buff = new StringBuilder();
        buff.append('(');
        for (AST paramAst : params) {
            AST typeAst = search(paramAst, TokenTypes.TYPE);
            AST child = typeAst.getFirstChild();
            while (child != null) {
                if (child.getType() == TokenTypes.IDENT) {
                    buff.append('Q');
                    buff.append(child.getText());
                    buff.append(';');
                } else {
                    buff.append(PRIMITIVE_MAP.get(Integer.valueOf(child.getType())).charValue());
                }
                child = child.getFirstChild();
            }
        }
        buff.append(')');
        return buff.toString();
    }
    
    /**
     * ソース要素の全ての子をデバッグ出力します。
     * @param sourceAst ソース要素
     */
    public static void debugSourceAst(AST sourceAst) {
        debugAst(sourceAst, 0); // メソッドのルート情報を全て書き出し
        // メソッドの兄弟情報を全て書き出し
        AST sibling = sourceAst.getNextSibling();
        while (sibling != null) {
            debugAst(sibling, 0);
            sibling = sibling.getNextSibling();
        }
    }
    
    /**
     * ソース要素の全ての子をデバッグ出力します。
     * @param ast ソース要素
     * @param index 深さ
     */
    private static void debugAst(AST ast, int index) {
        AST child = ast.getFirstChild();
        while (child != null) {
            for (int i = 0; i < index; i++) {
                System.out.print("  ");
            }
            int lineNo = 0;
            if (child instanceof DetailAST) {
                lineNo = ((DetailAST)child).getLineNo();
            } 
            System.out.println(child + " " + child.getType()
                    + " " + TokenTypes.getTokenName(child.getType())
                    + " <" + lineNo + ">");
            debugAst(child, index + 1);
            child = child.getNextSibling();
        }
    }


}
