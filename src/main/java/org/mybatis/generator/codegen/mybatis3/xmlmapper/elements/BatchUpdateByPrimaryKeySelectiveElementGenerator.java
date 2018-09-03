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
package org.mybatis.generator.codegen.mybatis3.xmlmapper.elements;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.ListUtilities;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;

/**
 * 
 * @author Jeff Butler
 * 
 */
public class BatchUpdateByPrimaryKeySelectiveElementGenerator extends
        AbstractXmlElementGenerator {

    public BatchUpdateByPrimaryKeySelectiveElementGenerator() {
        super();
    }

    @Override
    public void addElements(XmlElement parentElement) {
    	String prefix = "item";
        XmlElement answer = new XmlElement("update"); //$NON-NLS-1$

        answer.addAttribute(new Attribute(
                "id", introspectedTable.getBatchUpdateByPrimaryKeySelectiveStatementId())); //$NON-NLS-1$

//        String parameterType;
//
//        if (introspectedTable.getRules().generateRecordWithBLOBsClass()) {
//            parameterType = introspectedTable.getRecordWithBLOBsType();
//        } else {
//            parameterType = introspectedTable.getBaseRecordType();
//        }
//
//        answer.addAttribute(new Attribute("parameterType", //$NON-NLS-1$
//                parameterType));

        context.getCommentGenerator().addComment(answer);
        
        XmlElement ifElement = new XmlElement("if"); //$NON-NLS-1$
        ifElement.addAttribute(new Attribute("test", "list.size() != 0")); //$NON-NLS-1$ //$NON-NLS-2$
        
        answer.addElement(ifElement);
        XmlElement forEeachElement = new XmlElement("foreach"); //$NON-NLS-1$
        forEeachElement.addAttribute(new Attribute("collection", "list")); //$NON-NLS-1$ //$NON-NLS-2$
        forEeachElement.addAttribute(new Attribute("index", "index")); //$NON-NLS-1$ //$NON-NLS-2$
        forEeachElement.addAttribute(new Attribute("item", prefix)); //$NON-NLS-1$ //$NON-NLS-2$
        forEeachElement.addAttribute(new Attribute("separator", ";")); //$NON-NLS-1$ //$NON-NLS-2$
        ifElement.addElement(forEeachElement);

        StringBuilder sb = new StringBuilder();

        sb.append("update "); //$NON-NLS-1$
        sb.append(introspectedTable.getFullyQualifiedTableNameAtRuntime());
        forEeachElement.addElement(new TextElement(sb.toString()));

        XmlElement dynamicElement = new XmlElement("set"); //$NON-NLS-1$
        forEeachElement.addElement(dynamicElement);

        for (IntrospectedColumn introspectedColumn : ListUtilities.removeGeneratedAlwaysColumns(introspectedTable
                .getNonPrimaryKeyColumns())) {
            sb.setLength(0);
            sb.append(prefix+"."+introspectedColumn.getJavaProperty());
            sb.append(" != null"); //$NON-NLS-1$
            XmlElement isNotNullElement = new XmlElement("if"); //$NON-NLS-1$
            isNotNullElement.addAttribute(new Attribute("test", sb.toString())); //$NON-NLS-1$
            dynamicElement.addElement(isNotNullElement);

            sb.setLength(0);
            sb.append(MyBatis3FormattingUtilities
                    .getEscapedColumnName(introspectedColumn));
            sb.append(" = "); //$NON-NLS-1$
            sb.append(MyBatis3FormattingUtilities
                    .getParameterClause(introspectedColumn,prefix+"."));
            sb.append(',');

            isNotNullElement.addElement(new TextElement(sb.toString()));
        }

        boolean and = false;
        for (IntrospectedColumn introspectedColumn : introspectedTable
                .getPrimaryKeyColumns()) {
            sb.setLength(0);
            if (and) {
                sb.append("  and "); //$NON-NLS-1$
            } else {
                sb.append("where "); //$NON-NLS-1$
                and = true;
            }

            sb.append(MyBatis3FormattingUtilities
                    .getEscapedColumnName(introspectedColumn));
            sb.append(" = "); //$NON-NLS-1$
            sb.append(MyBatis3FormattingUtilities
                    .getParameterClause(introspectedColumn,prefix+"."));
            answer.addElement(new TextElement(sb.toString()));
        }

        if (context.getPlugins()
                .sqlMapUpdateByPrimaryKeySelectiveElementGenerated(answer,
                        introspectedTable)) {
            parentElement.addElement(answer);
        }
    }
}
