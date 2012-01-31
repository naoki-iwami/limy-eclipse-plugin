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
package org.limy.eclipse.code.javadoc;

import java.io.IOException;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.viewsupport.ProjectTemplateStore;
import org.eclipse.jface.text.templates.Template;

/**
 * @author Naoki Iwami
 */
public final class JavaTemplateUtils {
    
    /**
     * private constructor
     */
    private JavaTemplateUtils() { }

    /**
     * テンプレートを取得します(プロジェクト毎テンプレート対応)。
     * @param id 取得するテンプレートID
     * @param project 現在のJavaプロジェクト
     * @return テンプレート
     */
    public static Template getCodeTemplate(String id, IJavaProject project) {
        if (project == null) {
            return JavaPlugin.getDefault().getCodeTemplateStore().findTemplateById(id);
        }
        ProjectTemplateStore projectStore = new ProjectTemplateStore(project.getProject());
        try {
            projectStore.load();
        } catch (IOException e) {
            JavaPlugin.log(e);
        }
        return projectStore.findTemplateById(id);
    }

}
