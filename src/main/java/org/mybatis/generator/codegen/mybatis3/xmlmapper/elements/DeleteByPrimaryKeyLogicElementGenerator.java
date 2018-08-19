/**
 * 朱敬辉
 */
package org.mybatis.generator.codegen.mybatis3.xmlmapper.elements;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;

/**
 * 
 * @author Jeff Butler
 * 
 */
public class DeleteByPrimaryKeyLogicElementGenerator extends
        AbstractXmlElementGenerator {

    private boolean isSimple;

    public DeleteByPrimaryKeyLogicElementGenerator(boolean isSimple) {
        super();
        this.isSimple = isSimple;
    }

    @Override
    public void addElements(XmlElement parentElement) {
        XmlElement answer = new XmlElement("update"); //$NON-NLS-1$

        answer.addAttribute(new Attribute(
                "id", introspectedTable.getDeleteByPrimaryKeyLogicStatementId())); //$NON-NLS-1$
        String parameterClass;
        if (!isSimple && introspectedTable.getRules().generatePrimaryKeyClass()) {
            parameterClass = introspectedTable.getPrimaryKeyType();
        } else {
            // PK fields are in the base class. If more than on PK
            // field, then they are coming in a map.
            if (introspectedTable.getPrimaryKeyColumns().size() > 1) {
                parameterClass = "map"; //$NON-NLS-1$
            } else {
                parameterClass = introspectedTable.getPrimaryKeyColumns()
                        .get(0).getFullyQualifiedJavaType().toString();
            }
        }
        answer.addAttribute(new Attribute("parameterType", //$NON-NLS-1$
                parameterClass));

        context.getCommentGenerator().addComment(answer);

        StringBuilder sb = new StringBuilder();
        sb.append("update "); //$NON-NLS-1$
        sb.append(introspectedTable.getFullyQualifiedTableNameAtRuntime());
        answer.addElement(new TextElement(sb.toString()));
        sb.setLength(0);
        sb.append("set is_deleted = 1 "); //$NON-NLS-1$
        answer.addElement(new TextElement(sb.toString()));

        for (IntrospectedColumn introspectedColumn : introspectedTable
                .getPrimaryKeyColumns()) {
            sb.setLength(0);
            
            sb.append("where ");

            sb.append(MyBatis3FormattingUtilities
                    .getEscapedColumnName(introspectedColumn));
            sb.append(" = "); //$NON-NLS-1$
            sb.append(MyBatis3FormattingUtilities
                    .getParameterClause(introspectedColumn));
            answer.addElement(new TextElement(sb.toString()));
        }

        if (context.getPlugins()
                .sqlMapDeleteByPrimaryKeyElementGenerated(answer,
                        introspectedTable)) {
            parentElement.addElement(answer);
        }
    }
}
