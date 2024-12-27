package com.wenqi.tech.excel;

import com.alibaba.excel.write.handler.SheetWriteHandler;
import com.alibaba.excel.write.handler.context.SheetWriteHandlerContext;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteWorkbookHolder;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.usermodel.Header;
import org.apache.poi.ss.util.CellRangeAddressList;

/**
 * @author liangwenqi
 * @date 2024/12/27
 */
public class CustomSheetWriteHandler implements SheetWriteHandler {
    @Override
    public void afterSheetCreate(SheetWriteHandlerContext context) {
        DataValidationHelper helper = context.getWriteSheetHolder().getSheet().getDataValidationHelper();

        CellRangeAddressList cellRangeAddressList = new CellRangeAddressList(2, 4, 1, 1);
        // 定义正数且只允许两位小数的数据验证约束条件
        // 使用自定义公式来限制小数位数
        DataValidationConstraint decimalConstraint = helper.createCustomConstraint(
                "AND(ISNUMBER(B1), B1>=0, B1-INT(B1)<0.01)"
        );

        DataValidation dataValidation = helper.createValidation(decimalConstraint, cellRangeAddressList);
        // 设置允许两位小数
        dataValidation.setSuppressDropDownArrow(true);
        dataValidation.setShowErrorBox(true);
        // 设置当用户输入无效数据时显示的提示信息
        dataValidation.createErrorBox("错误", "请输入正数，并且最多保留两位小数！");

        context.getWriteSheetHolder().getSheet().addValidationData(dataValidation);


    }

}
