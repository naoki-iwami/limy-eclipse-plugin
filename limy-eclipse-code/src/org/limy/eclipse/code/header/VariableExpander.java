/*
 * Created 2007/08/21
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
package org.limy.eclipse.code.header;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 変数の式展開を実装するクラスです。
 * @author Naoki Iwami
 */
public final class VariableExpander {

    // ------------------------ Constants

    /**
     * ファイルの先頭コメントを識別するパターン
     */
    // 「/*」から始まって「 */」で終わる部分（最短一致）
    private static final Pattern PATTERN_COMMENT = Pattern.compile("^\\s*(/\\*.*?^ \\*/\\s*)(.*)",
            Pattern.DOTALL | Pattern.MULTILINE);
    
    /**
     * コメント中の日付を識別するパターン
     */
    // yyyy/mm/dd形式を日付として認識
    private static final Pattern PATTERN_DATE = Pattern.compile(".*[^\\d](\\d+/\\d+/\\d+).*",
            Pattern.DOTALL | Pattern.MULTILINE);

    /**
     * コメント中の時刻を識別するパターン
     */
    // yyyy/mm/dd hh:mm:ss形式を日付として認識
    private static final Pattern PATTERN_TIME = Pattern.compile(
            ".*[^\\d](\\d+/\\d+/\\d+\\s+\\d+:\\d+:\\d+).*",
            Pattern.DOTALL | Pattern.MULTILINE);

    // ------------------------ Constructors

    /**
     * private constructor
     */
    private VariableExpander() { }
    
    // ------------------------ Public Methods

    /**
     * Javaソース文字列にヘッダを追加して返します。
     * @param content Javaソース文字列
     * @param header ヘッダ文字列
     * @param options 置き換えマッピング
     * @return ヘッダを追加したJavaソース文字列
     */
    public static String convertContent(
            String content, String header, Map<String, String> options) {
        
        String tmpContent = content;
        Date date = new Date();
        String dateStr = new SimpleDateFormat("yyyy/MM/dd").format(date);
        String timeStr = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(date);
        
        // 先頭の /* ～ */ を検索（最短一致）
        Matcher matcherComment = PATTERN_COMMENT.matcher(content);
        if (matcherComment.matches()) {
            
            String group1 = matcherComment.group(1);
            if (!group1.startsWith("/**")) {
                // いきなり /** で始まるパターン（package-info.javaなど）は除外
                Matcher matcherDate = PATTERN_DATE.matcher(group1);
                if (matcherDate.matches()) {
                    // 日付形式の文字列が見つかったら、それを後の置き換え処理で使う（つまり現状を維持する）
                    dateStr = matcherDate.group(1);
                }

                Matcher matcherTime = PATTERN_TIME.matcher(group1);
                if (matcherTime.matches()) {
                    // 時刻形式の文字列が見つかったら、それを後の置き換え処理で使う（つまり現状を維持する）
                    timeStr = matcherTime.group(1);
                }

                tmpContent = matcherComment.group(2);
            }
        }
        
        String tmpHeader = header;
        for (Entry<String, String> entry : options.entrySet()) {
            tmpHeader = tmpHeader.replaceAll("\\$\\{" + entry.getKey() + "\\}", entry.getValue());
        }
        tmpHeader = tmpHeader.replaceAll("\\$\\$", "\\$");
        tmpHeader = tmpHeader.replaceAll("\\$\\{date\\}", dateStr);
        tmpHeader = tmpHeader.replaceAll("\\$\\{time\\}", timeStr);
        tmpHeader = tmpHeader.replaceAll("\\$\\{year\\}",
                new SimpleDateFormat("yyyy").format(date));
        
        return tmpHeader + tmpContent;
    }

}
