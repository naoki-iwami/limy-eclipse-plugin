/*
 * Created 2007/01/05
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
package org.limy.eclipse.common.resource;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;

/**
 * 基準パスを持ったリソース情報を表します。
 * @author Naoki Iwami
 */
public class ResourceWithBasedir {
    
    /** 基準パス */
    private final IPath baseDir;
    
    /** リソース */
    private final IResource resource;

    // ------------------------ Constructors

    /**
     * ResourceWithBasedirインスタンスを構築します。
     * @param baseDir 基準パス
     * @param resource リソース
     */
    public ResourceWithBasedir(IPath baseDir, IResource resource) {
        super();
        this.baseDir = baseDir;
        this.resource = resource;
    }
    
    // ------------------------ Getter/Setter Methods

    /**
     * 基準パスを取得します。
     * @return 基準パス
     */
    public IPath getBaseDir() {
        return baseDir;
    }

    /**
     * リソースを取得します。
     * @return リソース
     */
    public IResource getResource() {
        return resource;
    }
    

}
