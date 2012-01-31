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

import java.util.Arrays;
import java.util.Collection;
import java.util.List;


/**
 * メソッド情報を表します。
 * @author Naoki Iwami
 */
public class MethodInfo {

    // ------------------------ Fields

    /** メソッド名 */
    private final String name;
    
    /** 全ポイント情報 */
    private Collection<PointInfo> pointInfos;
    
    /** 全ライン情報 */
    private List<LineInfo> lineInfos;

    // ------------------------ Constructors

    /**
     * MethodInfoインスタンスを構築します。
     * @param name メソッド名
     * @param pointInfos 全ポイント情報
     * @param lineInfos 全ライン情報
     */
    public MethodInfo(String name, Collection<PointInfo> pointInfos,
            List<LineInfo> lineInfos) {
        super();
        this.name = name;
        this.pointInfos = pointInfos;
        this.lineInfos = lineInfos;
    }

    // ------------------------ Override Methods

    @Override
    public String toString() {
        return Arrays.toString(lineInfos.toArray(new LineInfo[lineInfos.size()]));
    }

    // ------------------------ Getter/Setter Methods

    /**
     * メソッド名を取得します。
     * @return メソッド名
     */
    public String getName() {
        return name;
    }

    /**
     * 全ポイント情報を取得します。
     * @return 全ポイント情報
     */
    public Collection<PointInfo> getPointInfos() {
        return pointInfos;
    }

    /**
     * 全ライン情報を取得します。
     * @return 全ライン情報
     */
    public List<LineInfo> getLineInfos() {
        return lineInfos;
    }

}
