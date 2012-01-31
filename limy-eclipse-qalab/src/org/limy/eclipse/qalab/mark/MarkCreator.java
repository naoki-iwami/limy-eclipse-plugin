/*
 * Created 2007/01/08
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
package org.limy.eclipse.qalab.mark;

import java.util.Collection;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.limy.eclipse.qalab.common.LimyQalabEnvironment;

/**
 * マーカー作成用インターフェイスです。
 * @author Naoki Iwami
 */
public interface MarkCreator {

    /**
     * Creator名を返します。
     * @return Creator名
     */
    String getName();
    
    /**
     * 単一リソースを計測してマーカーをつけます。
     * @param env 
     * @param resource リソース
     * @param monitor 遷移モニタ
     * @return 処理に成功したら true
     */
    boolean markResource(LimyQalabEnvironment env,
            IResource resource, IProgressMonitor monitor);

    /**
     * 単一リソースを計測して一時マーカーをつけます。
     * @param env 
     * @param resource リソース
     * @param monitor 遷移モニタ
     * @return 処理に成功したら true
     */
    boolean markResourceTemporary(LimyQalabEnvironment env,
            IResource resource, IProgressMonitor monitor);

    /**
     * 複数リソースを計測してマーカーをつけます。
     * @param env 
     * @param resources リソース
     * @param monitor 遷移モニタ
     * @return 処理に成功したら true
     */
    boolean markResources(LimyQalabEnvironment env,
            Collection<IResource> resources, IProgressMonitor monitor);
    
    boolean markJavaElement(LimyQalabEnvironment env,
            Collection<IJavaElement> elements, IProgressMonitor monitor);

}
