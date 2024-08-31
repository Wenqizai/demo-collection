package com.wenqi.example.string;

import cn.hutool.http.HtmlUtil;

/**
 * @author liangwenqi
 * @date 2022/10/27
 */
public class HtmlToString {
    public static void main(String[] args) {
        String str = "<p>尊敬的店长：<br/>您好！<font color=\"#c24f4a\">骏景一店</font>经核实门店存在虚假退货情况，相关凭证已同步给区域运营负责人及门店运营督导<br/>门店行为符合2022年2月15日正式执行的<b>《<span style=\"background-color: rgb(249, 150, 59);\">全国门店退货制度补充说明 V1.0</span>》</b>标准虚假退货规定。如门店需申诉请提供相关凭证给督导，如未收到申诉，则默认门店无异议，将按制度执行，谢谢！<br/>按照《全国门店退货制度补充说明 V1.0》标准虚假退货规定，111111111111111<span style=\"color: rgb(96, 98, 102); font-size: 14px; text-align: initial;\">尊敬的店长：</span></p><font face=\"楷体\">您好！骏景一店经核实门店存在虚假退货情况，相关凭证已同步给区域运营负责人及门店运营督导<br/>门店行为符合2022年2月15日正式执行的<b>《全国门店退货制度补充说明 V1.0》</b>标准虚假退货规定。如门店需申诉请提供相关凭证给督导，如未收到申诉，则默认门店无异议，将按制度执行，谢谢！<br/>按照《全国门店退货制度补充说明 V1.0》标准虚假退货规定，111111111111111</font><span style=\"color: rgb(51, 51, 51); font-size: 16px; text-align: initial;\">尊敬的店长：</span><font face=\"楷体\"><br/></font>您好！骏景一店经核实门店存在虚假退货情况，相关凭证已同步给区域运营负责人及门店运营督导<br/>门店行为符合2022年2月15日正式执行的<b>《全国门店退货制度补充说明 V1.0》</b>标准虚假退货规定。如门店需申诉请提供相关凭证给督导，如未收到申诉，则默认门店无异议，将按制度执行，谢谢！<br/>按照《全国门店退货制度补充说明 V1.0》标准虚假退货规定，111111111111111";
        String result = HtmlUtil.cleanHtmlTag(str);
        System.out.println(result);
    }
}
