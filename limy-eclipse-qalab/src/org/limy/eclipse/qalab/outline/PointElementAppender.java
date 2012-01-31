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

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;

import org.w3c.dom.Element;

/**
 * svgのポイント要素をコレクションに追加する実装クラスです。
 * @author Naoki Iwami
 */
public class PointElementAppender implements SvgElementAppender {

    /** ポイント情報作成担当 */
    private final PointInfoCreator creator;
    
    /** 結果格納先 */
    private final Collection<ClickablePointInfo> results = new ArrayList<ClickablePointInfo>();;

    public PointElementAppender(PointInfoCreator creator) {
        this.creator = creator;
    }
    
    public void append(Element el, Rectangle2D.Double rect) {
        
        results.add(creator.create(el, rect));
    }

    public Collection<ClickablePointInfo> getResults() {
        return results;
    }
}
