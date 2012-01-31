/*
 * Created 2007/01/06
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
package org.limy.eclipse.qalab.tester;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.limy.eclipse.common.jface.ITableObjectImage;
import org.limy.eclipse.qalab.LimyQalabPlugin;
import org.limy.eclipse.qalab.LimyQalabPluginImages;

/**
 * テスト失敗結果を表すBeanクラスです。
 * @author Naoki Iwami
 */
public class FailureItem implements ITableObjectImage {

    // ------------------------ Fields

    /** テストクラス名 */
    private String testClassName;
    
    /** テストメソッド名 */
    private String testName;
    
    /** テストリソース */
    private IResource resource;
    
    /** エラーフラグ（Failure : false, Error : true） */
    private boolean isError;
    
    /** 行番号 */
    private int lineNumber;
    
    /** メッセージ */
    private String message;
    
    // ------------------------ Constructors

    /**
     * FailureItemインスタンスを構築します。
     * @param testClassName テストクラス名
     * @param testName テストメソッド名
     * @param resource テストリソース
     * @param isError エラーフラグ（Failure : false, Error : true）
     * @param lineNumber 行番号
     * @param message メッセージ
     */
    public FailureItem(String testClassName,
            String testName,
            IResource resource,
            boolean isError,
            int lineNumber, String message) {
        
        this.testClassName = testClassName;
        this.testName = testName;
        this.resource = resource;
        this.isError = isError;
        this.lineNumber = lineNumber;
        this.message = message;
    }
    
    // ------------------------ Implement Methods

    public int getColumnSize() {
        return 100;
    }

    public String getViewString(int index) {
        
        String result;
        switch (index) {
        case 1:
            result = message.replaceAll("[\\n\\r\\t]+", " ");
            break;
        case 2:
            int pos = testClassName.lastIndexOf('.');
            if (pos >= 0) {
                result = testClassName.substring(pos + 1) + "." + testName;
            } else {
                result = testClassName + "." + testName;
            }
            break;
        case 3:
            result = resource.getFullPath().removeLastSegments(1).toString().substring(1);
            break;
        case 4:
            result = "line " + Integer.toString(lineNumber);
            break;
        default:
            result = "";
            break;
        }
        return result;
    }

    public Image getImage(int index) {
        
        if (index == 0) {
            
            ImageRegistry registry = LimyQalabPlugin.getDefault().getImageRegistry();
            if (isError) {
                return registry.get(LimyQalabPluginImages.ERROR);
            } else {
                return registry.get(LimyQalabPluginImages.FAILURE);
            }
        }
        return null;
    }
    
    public Object getValue(int index) {
        return Integer.valueOf(index);
    }

    public void setValue(int index, Object value) {
        // do nothing
    }

    // ------------------------ Public Methods

    /**
     * テストクラス名を取得します。
     * @return テストクラス名
     */
    public String getQualifiedClassName() {
        return testClassName;
    }
    
    /**
     * テストリソースを取得します。
     * @return テストリソース
     */
    public IResource getResource() {
        return resource;
    }

    /**
     * 行番号を取得します。
     * @return 行番号
     */
    public int getLineNumber() {
        return lineNumber;
    }

}
