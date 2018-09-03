/**
 *    Copyright 2006-2017 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.mybatis.generator.codegen.mybatis3.model;

import static org.mybatis.generator.internal.util.JavaBeansUtil.getJavaBeansField;
import static org.mybatis.generator.internal.util.JavaBeansUtil.getJavaBeansGetter;
import static org.mybatis.generator.internal.util.JavaBeansUtil.getJavaBeansSetter;
import static org.mybatis.generator.internal.util.messages.Messages.getString;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.mybatis.generator.api.CommentGenerator;
import org.mybatis.generator.api.FullyQualifiedTable;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.Plugin;
import org.mybatis.generator.api.dom.java.CompilationUnit;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.codegen.AbstractJavaGenerator;
import org.mybatis.generator.codegen.RootClassInfo;

/**
 * 
 * @author Jeff Butler
 * 
 */
public class BaseRecordGenerator extends AbstractJavaGenerator {

    public BaseRecordGenerator() {
        super();
    }

    @Override
    public List<CompilationUnit> getCompilationUnits() {
        FullyQualifiedTable table = introspectedTable.getFullyQualifiedTable();
        progressCallback.startTask(getString(
                "Progress.8", table.toString())); //$NON-NLS-1$
        Plugin plugins = context.getPlugins();
        CommentGenerator commentGenerator = context.getCommentGenerator();
        

        FullyQualifiedJavaType type = new FullyQualifiedJavaType(
                introspectedTable.getBaseRecordType());
        FullyQualifiedJavaType type2 = new FullyQualifiedJavaType(
                introspectedTable.getReturnMainRecordType());
        TopLevelClass topLevelClass = new TopLevelClass(type);
        TopLevelClass topLevelClass2 = new TopLevelClass(type2);
        topLevelClass.setVisibility(JavaVisibility.PUBLIC);
        topLevelClass2.setVisibility(JavaVisibility.PUBLIC);
        commentGenerator.addJavaFileComment(topLevelClass);
        
        topLevelClass2.addAnnotation("@JsonInclude(Include.NON_NULL)");
        topLevelClass2.addImportedType("com.fasterxml.jackson.annotation.JsonInclude");
        topLevelClass2.addImportedType("com.fasterxml.jackson.annotation.JsonInclude.Include");
        
        FullyQualifiedJavaType superClass = getSuperClass();
        if (superClass != null) {
            topLevelClass.setSuperClass(superClass);
            topLevelClass2.setSuperClass(superClass);
            topLevelClass.addImportedType(superClass);
            topLevelClass2.addImportedType(superClass);
        }
        commentGenerator.addModelClassComment(topLevelClass, introspectedTable);
        commentGenerator.addModelClassComment(topLevelClass2, introspectedTable);

        List<IntrospectedColumn> introspectedColumns = getColumnsInThisClass();

        if (introspectedTable.isConstructorBased()) {
            addParameterizedConstructor(topLevelClass, introspectedTable.getNonBLOBColumns());
            addParameterizedConstructor(topLevelClass2, introspectedTable.getNonBLOBColumns());

            if (includeBLOBColumns()) {
                addParameterizedConstructor(topLevelClass, introspectedTable.getAllColumns());
                addParameterizedConstructor(topLevelClass2, introspectedTable.getAllColumns());
            }

            if (!introspectedTable.isImmutable()) {
                addDefaultConstructor(topLevelClass);
                addDefaultConstructor(topLevelClass2);
            }
        }

        String rootClass = getRootClass();
        for (IntrospectedColumn introspectedColumn : introspectedColumns) {
            if (RootClassInfo.getInstance(rootClass, warnings)
                    .containsProperty(introspectedColumn)) {
                continue;
            }

            Field field = getJavaBeansField(introspectedColumn, context, introspectedTable);
            if (plugins.modelFieldGenerated(field, topLevelClass,
                    introspectedColumn, introspectedTable,
                    Plugin.ModelClassType.BASE_RECORD)) {
                topLevelClass.addField(field);
                topLevelClass.addImportedType(field.getType());
                topLevelClass2.addField(field);
                topLevelClass2.addImportedType(field.getType());
            }

            Method method = getJavaBeansGetter(introspectedColumn, context, introspectedTable);
            Method method2 = getJavaBeansGetter(introspectedColumn, context, introspectedTable);
            if (plugins.modelGetterMethodGenerated(method, topLevelClass,
                    introspectedColumn, introspectedTable,
                    Plugin.ModelClassType.BASE_RECORD)) {
                topLevelClass.addMethod(method);
                topLevelClass2.addMethod(method2);
            }

            if (!introspectedTable.isImmutable()) {
                method = getJavaBeansSetter(introspectedColumn, context, introspectedTable);
                method2 = getJavaBeansSetter(introspectedColumn, context, introspectedTable);
                
//                boolean hadImported = false;
//                
//                String methodName_R = method.getName();
//                if(methodName_R.length()>=6) {
//                	String methodName = methodName_R;
//                	 methodName = methodName.substring(methodName.length()-6);
//                	 if(methodName.equals("Status")) {
//                     	method.addAnnotation("@JsonDeserialize(using = ConvertBooleanToInt.class)");
//                     	topLevelClass.addImportedType("com.fasterxml.jackson.databind.annotation.JsonDeserialize");
//                     	topLevelClass.addImportedType("com.eshop.dataSerialize.ConvertBooleanToInt");
//                     	method2.addAnnotation("@JsonSerialize(using = ConvertIntToBoolean.class)");
//                     	topLevelClass2.addImportedType("com.fasterxml.jackson.databind.annotation.JsonSerialize");
//                     	topLevelClass2.addImportedType("com.eshop.dataSerialize.ConvertIntToBoolean");
//                     	hadImported = true;
//                     }
//                }
//              if(methodName_R.length()>=9) {
//                  String methodName2 = methodName_R;
//                	 methodName2 = methodName2.substring(methodName2.length()-9);
//                	 if(methodName2.equals("IsDefault")) {
//                     	method.addAnnotation("@JsonDeserialize(using = ConvertBooleanToInt.class)");
//                     	method2.addAnnotation("@JsonSerialize(using = ConvertIntToBoolean.class)");
//                     	
//                     	if(!hadImported) {
//                     		topLevelClass.addImportedType("com.fasterxml.jackson.databind.annotation.JsonDeserialize");
//                         	topLevelClass.addImportedType("com.eshop.dataSerialize.ConvertBooleanToInt");
//                         	topLevelClass2.addImportedType("com.fasterxml.jackson.databind.annotation.JsonSerialize");
//                         	topLevelClass2.addImportedType("com.eshop.dataSerialize.ConvertIntToBoolean");
//                     	}
//                     }
//              }
                
                if (plugins.modelSetterMethodGenerated(method, topLevelClass,
                        introspectedColumn, introspectedTable,
                        Plugin.ModelClassType.BASE_RECORD)) {
                    topLevelClass.addMethod(method);
                    topLevelClass2.addMethod(method2);
                }
            }
        }

        List<CompilationUnit> answer = new ArrayList<CompilationUnit>();
        if (context.getPlugins().modelBaseRecordClassGenerated(
                topLevelClass, introspectedTable)) {
            answer.add(topLevelClass);
            answer.add(topLevelClass2);
        }
        return answer;
    }

    private FullyQualifiedJavaType getSuperClass() {
        FullyQualifiedJavaType superClass;
        if (introspectedTable.getRules().generatePrimaryKeyClass()) {
            superClass = new FullyQualifiedJavaType(introspectedTable
                    .getPrimaryKeyType());
        } else {
            String rootClass = getRootClass();
            if (rootClass != null) {
                superClass = new FullyQualifiedJavaType(rootClass);
            } else {
                superClass = null;
            }
        }

        return superClass;
    }

    private boolean includePrimaryKeyColumns() {
        return !introspectedTable.getRules().generatePrimaryKeyClass()
                && introspectedTable.hasPrimaryKeyColumns();
    }

    private boolean includeBLOBColumns() {
        return !introspectedTable.getRules().generateRecordWithBLOBsClass()
                && introspectedTable.hasBLOBColumns();
    }

    private void addParameterizedConstructor(TopLevelClass topLevelClass, List<IntrospectedColumn> constructorColumns) {
        Method method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setConstructor(true);
        method.setName(topLevelClass.getType().getShortName());
        context.getCommentGenerator().addGeneralMethodComment(method, introspectedTable);

        for (IntrospectedColumn introspectedColumn : constructorColumns) {
            method.addParameter(new Parameter(introspectedColumn.getFullyQualifiedJavaType(),
                    introspectedColumn.getJavaProperty()));
            topLevelClass.addImportedType(introspectedColumn.getFullyQualifiedJavaType());
        }

        StringBuilder sb = new StringBuilder();
        List<String> superColumns = new LinkedList<String>();
        if (introspectedTable.getRules().generatePrimaryKeyClass()) {
            boolean comma = false;
            sb.append("super("); //$NON-NLS-1$
            for (IntrospectedColumn introspectedColumn : introspectedTable.getPrimaryKeyColumns()) {
                if (comma) {
                    sb.append(", "); //$NON-NLS-1$
                } else {
                    comma = true;
                }
                sb.append(introspectedColumn.getJavaProperty());
                superColumns.add(introspectedColumn.getActualColumnName());
            }
            sb.append(");"); //$NON-NLS-1$
            method.addBodyLine(sb.toString());
        }

        for (IntrospectedColumn introspectedColumn : constructorColumns) {
            if (!superColumns.contains(introspectedColumn.getActualColumnName())) {
                sb.setLength(0);
                sb.append("this."); //$NON-NLS-1$
                sb.append(introspectedColumn.getJavaProperty());
                sb.append(" = "); //$NON-NLS-1$
                sb.append(introspectedColumn.getJavaProperty());
                sb.append(';');
                method.addBodyLine(sb.toString());
            }
        }

        topLevelClass.addMethod(method);
    }

    private List<IntrospectedColumn> getColumnsInThisClass() {
        List<IntrospectedColumn> introspectedColumns;
        if (includePrimaryKeyColumns()) {
            if (includeBLOBColumns()) {
                introspectedColumns = introspectedTable.getAllColumns();
            } else {
                introspectedColumns = introspectedTable.getNonBLOBColumns();
            }
        } else {
            if (includeBLOBColumns()) {
                introspectedColumns = introspectedTable
                        .getNonPrimaryKeyColumns();
            } else {
                introspectedColumns = introspectedTable.getBaseColumns();
            }
        }

        return introspectedColumns;
    }
}
