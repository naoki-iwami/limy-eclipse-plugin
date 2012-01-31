/*
 * Created 2007/06/23
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
package org.limy.eclipse.qalab.common;

import java.io.IOException;
import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;

/**
 * ENV関連のユーティリティクラスです。
 * @author Naoki Iwami
 */
public final class LimyQalabEnvUtils {
    
    /**
     * private constructor
     */
    private LimyQalabEnvUtils() { }

    public static String createIgnoreStr(LimyQalabEnvironment env, boolean isRoot)
            throws CoreException, IOException {
        
        StringBuilder ignoreLines = new StringBuilder();

        String packages = env.getStore().getString(LimyQalabConstants.IGNORE_PACKAGES);
        String[] ignorePackages = packages.split("\n");
        for (String ignorePackage : ignorePackages) {
            if (ignorePackage.length() > 0) {
                ignoreLines.append(ignorePackage.replace('.', '/')).append("/**");
                ignoreLines.append(',');
            }
        }
        
        Collection<IFile> autoCreatedFiles = QalabResourceUtils.getAutoCreatedFiles(env);
        for (IFile file : autoCreatedFiles) {
            String qualifiedClassName = LimyQalabUtils.getQualifiedClassName(env, file);
            ignoreLines.append(qualifiedClassName.replace('.', '/')).append(".java,");
        }

        if (isRoot) {
            // プロジェクトルート直下のソースディレクトリは特殊（destを対象から外す必要がある）
            ignoreLines.append("dest/**");
        }
        return ignoreLines.toString();
    }
}
