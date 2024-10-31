package ${domain.packageName};

<#list tableClass.importList as fieldType>${"\n"}import ${fieldType};</#list>
import com.wenqi.test.mybatis.AInterface;
import lombok.Data;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

/**
* ${tableClass.remark!}
* @TableName ${tableClass.tableName}
*/
@Data
@Table(name= "${tableClass.tableName}")
public class ${tableClass.shortClassName} implements AInterface {

<#list tableClass.allFields as field>

    /**
    * ${field.remark!}
    */
    <#if !field.nullable><#if field.fieldName == "id">@Id</#if></#if>
    @Column(name = "${field.columnName}")
    private ${field.shortTypeName} ${field.fieldName};

</#list>
}
