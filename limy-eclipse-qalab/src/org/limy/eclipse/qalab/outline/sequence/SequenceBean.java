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
package org.limy.eclipse.qalab.outline.sequence;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;

/**
 * シーケンス図を表します。
 * @author Naoki Iwami
 */
public class SequenceBean {
    
    // ------------------------ Fields
    
    /** コンテナ名 */
    private String containerName;

    /** 呼び出し名 */
    private String callName;

    /** インスタンス名 */
    private String instanceName;

    /** Java要素 */
    private IJavaElement javaElement;

    /** 子要素 */
    private Collection<SequenceBean> children = new ArrayList<SequenceBean>();
    
    // ------------------------ Constructors

    /**
     * SequenceBean インスタンスを構築します。
     * @param instanceName インスタンス名
     * @param javaElement Java要素
     */
    public SequenceBean(String instanceName, IJavaElement javaElement) {
        super();
        this.javaElement = javaElement;
        this.callName = javaElement.getElementName();
        this.instanceName = instanceName;
        if (javaElement instanceof IMember) {
            IMember member = (IMember)javaElement;
            this.containerName = member.getDeclaringType().getElementName();
        }
    }

    /**
     * SequenceBean インスタンスを構築します。
     * @param instanceName インスタンス名
     * @param javaElement Java要素
     * @param containerName コンテナ名
     */
    public SequenceBean(String instanceName, IJavaElement javaElement, String containerName) {
        super();
        this.javaElement = javaElement;
        this.callName = javaElement.getElementName();
        this.instanceName = instanceName;
        this.containerName = containerName;
    }

    /**
     * SequenceBean インスタンスを構築します。
     * @param containerName コンテナ名
     * @param callName 呼び出し名
     */
    public SequenceBean(String containerName, String callName) {
        super();
        this.callName = callName;
        this.containerName = containerName;
    }
    
    // ------------------------ Public Methods
    
    public void addChild(SequenceBean bean) {
        children.add(bean);
    }
    
    public String getResultValue() {
        if (callName.startsWith("get")) {
            return callName.substring(3, 4).toLowerCase() + callName.substring(4);
        }
        return null;
    }
    
    /**
     * 指定した名前のJava要素を返します。
     * @param lastBean 最後に検索したBean
     * @param name 名称
     * @return Java要素
     */
    public IJavaElement searchElement(SequenceBean lastBean, String name) {
        for (SequenceBean child : getChildren()) {
            IJavaElement result = child.searchElement(lastBean, name);
            if (result != null) {
                return result;
            }
        }
        if (getCallName().equals(name)) {
            return javaElement;
        }
        return null;
    }

    // ------------------------ Getter/Setter Methods

    /**
     * コンテナ名を取得します。
     * @return コンテナ名
     */
    public String getContainerName() {
        return containerName;
    }
    
    /**
     * コンテナ名を設定します。
     * @param containerName コンテナ名
     */
    public void setContainerName(String containerName) {
        this.containerName = containerName;
    }

    /**
     * 呼び出し名を取得します。
     * @return 呼び出し名
     */
    public String getCallName() {
        return callName;
    }

    /**
     * 呼び出し名を設定します。
     * @param callName 呼び出し名
     */
    public void setCallName(String callName) {
        this.callName = callName;
    }

    /**
     * インスタンス名を取得します。
     * @return インスタンス名
     */
    public String getInstanceName() {
        return instanceName;
    }

    /**
     * Java要素を取得します。
     * @return Java要素
     */
    public IJavaElement getJavaElement() {
        return javaElement;
    }

    /**
     * 子要素を取得します。
     * @return 子要素
     */
    public Collection<SequenceBean> getChildren() {
        return children;
    }

    /**
     * 子要素を設定します。
     * @param children 子要素
     */
    public void setChildren(Collection<SequenceBean> children) {
        this.children = children;
    }

}
