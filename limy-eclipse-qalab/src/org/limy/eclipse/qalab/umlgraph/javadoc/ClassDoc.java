/*
 * Created 2007/02/15
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
package org.limy.eclipse.qalab.umlgraph.javadoc;






/**
 *
 * @author Naoki Iwami
 */
public interface ClassDoc extends ProgramElementDoc, Type  {

    /**
     * @return
     */
    String qualifiedName();

    /**
     * @return
     */
    FieldDoc[] fields();

    /**
     * @return
     */
    FieldDoc[] enumConstants();

    /**
     * @return
     */
    MethodDoc[] methods();

    /**
     * @return
     */
    ConstructorDoc[] constructors();

    /**
     * @param className
     * @return
     */
    ClassDoc findClass(String className);

    /**
     * @return
     */
    Type superclassType();

    /**
     * @return
     */
    Type[] interfaceTypes();

    /**
     * @param filter
     * @return
     */
    FieldDoc[] fields(boolean filter);

    /**
     * @param filter
     * @return
     */
    MethodDoc[] methods(boolean filter);

    /**
     * @return
     */
    TypeVariable[] typeParameters();

    /**
     * @return
     */
    @Deprecated
    ClassDoc[] importedClasses();

    /**
     * @return
     */
    ClassDoc superclass();

    /**
     * @return
     */
    ClassDoc[] interfaces();

    /**
     * @param filter
     * @return
     */
    ConstructorDoc[] constructors(boolean filter);

    /**
     * @return
     */
    boolean definesSerializableFields();

    /**
     * @return
     */
    @Deprecated
    PackageDoc[] importedPackages();

    /**
     * @return
     */
    ClassDoc[] innerClasses();

    /**
     * @param filter
     * @return
     */
    ClassDoc[] innerClasses(boolean filter);

    /**
     * @return
     */
    boolean isExternalizable();

    /**
     * @return
     */
    boolean isSerializable();

    /**
     * @return
     */
    FieldDoc[] serializableFields();

    /**
     * @return
     */
    MethodDoc[] serializationMethods();

    /**
     * @param arg0
     * @return
     */
    boolean subclassOf(ClassDoc arg0);

    /**
     * @return
     */
    ParamTag[] typeParamTags();


}
