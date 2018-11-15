package org.mybatis.generator.codegen.mybatis3.xmlmapper.elements;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.ListUtilities;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;

public class UpdateByParamsSelectiveElementGenerator extends
			AbstractXmlElementGenerator{

	private boolean isSimple;
	
    public UpdateByParamsSelectiveElementGenerator(boolean isSimple) {
        super();
        this.isSimple = isSimple;
    }
    
    @Override
    public void addElements(XmlElement parentElement) {
    	String params = "params.";
    	String coditions = "coditions.";
        XmlElement answer = new XmlElement("update"); //$NON-NLS-1$

        answer.addAttribute(new Attribute(
                "id", introspectedTable.getUpdateByParamsSelectiveStatementId())); //$NON-NLS-1$

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

        StringBuilder sb = new StringBuilder();

        sb.append("update "); //$NON-NLS-1$
        sb.append(introspectedTable.getFullyQualifiedTableNameAtRuntime());
        answer.addElement(new TextElement(sb.toString()));

        XmlElement dynamicElement = new XmlElement("set"); //$NON-NLS-1$
        answer.addElement(dynamicElement);

        for (IntrospectedColumn introspectedColumn : ListUtilities.removeGeneratedAlwaysColumns(introspectedTable
                .getNonPrimaryKeyColumns())) {
            sb.setLength(0);
            sb.append(params+introspectedColumn.getJavaProperty());
            sb.append(" != null"); //$NON-NLS-1$
            XmlElement isNotNullElement = new XmlElement("if"); //$NON-NLS-1$
            isNotNullElement.addAttribute(new Attribute("test", sb.toString())); //$NON-NLS-1$
            dynamicElement.addElement(isNotNullElement);

            sb.setLength(0);
            sb.append(MyBatis3FormattingUtilities
                    .getEscapedColumnName(introspectedColumn));
            sb.append(" = "); //$NON-NLS-1$
            sb.append(MyBatis3FormattingUtilities
                    .getParameterClause(introspectedColumn,params));
            sb.append(',');

            isNotNullElement.addElement(new TextElement(sb.toString()));
        }

        XmlElement whereElement = new XmlElement("where"); //$NON-NLS-1$
        answer.addElement(whereElement);
        
        boolean and = false;
        for (IntrospectedColumn introspectedColumn : ListUtilities.removeGeneratedAlwaysColumns(introspectedTable
                .getNonPrimaryKeyColumns())) {
            sb.setLength(0);
            sb.append(coditions+introspectedColumn.getJavaProperty());
            sb.append(" != null"); //$NON-NLS-1$
            XmlElement isNotNullElement = new XmlElement("if"); //$NON-NLS-1$
            isNotNullElement.addAttribute(new Attribute("test", sb.toString())); //$NON-NLS-1$
            whereElement.addElement(isNotNullElement);

            sb.setLength(0);
            if(and) {
            	sb.append("and ");
            }
            and = true;
            sb.append(MyBatis3FormattingUtilities
                    .getEscapedColumnName(introspectedColumn));
            sb.append(" = "); //$NON-NLS-1$
            sb.append(MyBatis3FormattingUtilities
                    .getParameterClause(introspectedColumn,coditions));
//            sb.append(',');

            isNotNullElement.addElement(new TextElement(sb.toString()));
        }

        if (context.getPlugins()
                .sqlMapUpdateByPrimaryKeySelectiveElementGenerated(answer,
                        introspectedTable)) {
            parentElement.addElement(answer);
        }
    }
}
