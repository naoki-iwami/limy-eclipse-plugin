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
package org.limy.eclipse.common.resource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;

/**
 * @author Naoki Iwami
 */
public final class LimyResourceUtils {
    
    /**
     * private constructor
     */
    private LimyResourceUtils() { }
    
    /**
     * Fileインスタンスを作成します。
     * @param path ファイルパス
     * @return Fileインスタンス
     */
    public static IFile newFile(IPath path) {
        return ResourcesPlugin.getWorkspace().getRoot().getFile(path);
    }

    /**
     * Folderインスタンスを作成します。
     * @param path ファイルパス
     * @return Folderインスタンス
     */
    public static IFolder newFolder(IPath path) {
        return ResourcesPlugin.getWorkspace().getRoot().getFolder(path);
    }

    /**
     * ファイルを contents の内容で新規作成します。
     * <p>
     * すでにファイルが存在した場合、無条件で内容を上書きします。
     * </p>
     * @param path ファイルパス（内部パス形式）
     * @param contents ファイル内容
     * @param charset 書き込み時の文字セット
     * @throws CoreException 
     * @throws IOException 
     */
    public static void createFile(IPath path, String contents, String charset)
            throws CoreException, IOException {
        
        IFile file = newFile(path);
        InputStream in = new ByteArrayInputStream(contents.getBytes(charset));
        if (file.exists()) {
            file.delete(true, null);
        }
        file.create(in, true, null);
        in.close();

    }

}
