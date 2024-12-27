package com.wenqi.tech.excel;

import com.alibaba.excel.EasyExcel;
import org.apache.commons.compress.utils.Lists;

/**
 * @author liangwenqi
 * @date 2024/12/27
 */
public class EasyExcelDataFormatDemo {

    public static void main(String[] args) {
        String fileName = "C://temp//test" + System.currentTimeMillis() + ".xlsx";
        EasyExcel.write(fileName, FormatDataDto.class)
                .registerWriteHandler(new CustomSheetWriteHandler())
                .registerWriteHandler(new CustomSheetCellWriteHandler())
                .sheet("模板")
                .doWrite(Lists.newArrayList());

    }


}
