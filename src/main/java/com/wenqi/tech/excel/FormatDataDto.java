package com.wenqi.tech.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.alibaba.excel.annotation.format.NumberFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author liangwenqi
 * @date 2024/12/27
 */
@Data
public class FormatDataDto {

    /**
     * 我想所有的 字符串起前面加上"自定义："三个字
     */
    @ExcelProperty({Constant.HEAD_A, "标题"})
    private String title;

    /**
     * 我想写到excel 用百分比表示
     */
    @ExcelProperty({Constant.HEAD_A, "价格(正数两位小数)"})
    private BigDecimal price;

    /**
     * 我想写到excel 用年月日的格式
     */
    @ExcelProperty({Constant.HEAD_A, "日期"})
    private Date date;

}
