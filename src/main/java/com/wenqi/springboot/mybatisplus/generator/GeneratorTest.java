package com.wenqi.springboot.mybatisplus.generator;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.po.LikeTable;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.fill.Column;
import com.baomidou.mybatisplus.generator.fill.Property;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author liangwenqi
 * @date 2024/4/7
 */
public class GeneratorTest {
        private static final String url = "jdbc:mysql://10.0.88.8:3306/mbp_test_db?useUnicode=true&characterEncoding=utf-8&useSSL=false&allowMultiQueries=true&rewriteBatchedStatements=true";
    //private static final String url = "jdbc:mysql://192.168.1.116:3306/mbp_test_db?useUnicode=true&characterEncoding=utf-8&useSSL=false&allowMultiQueries=true&rewriteBatchedStatements=true";
    private static final String username = "root";
    private static final String password = "root";
    private static final String finalProjectPath = System.getProperty("user.dir");


    public static void main(String[] args) {
        testGenerator();
    }

    public static void testGenerator() {
        FastAutoGenerator.create(url, username, password)
                .globalConfig(builder -> {
                    builder.author("abc") // 设置作者
                            .enableSwagger() // 开启 swagger 模式
                            .fileOverride() // 覆盖已生成文件
                            .disableOpenDir() //禁止打开输出目录
                            .outputDir(finalProjectPath + "/src/main/java"); // 指定输出目录
                })
                .packageConfig(builder -> {
                    builder.parent("com.wenqi.springboot.mybatisplus") // 设置父包名
                            .moduleName("generator") // 设置父包模块名
                            .entity("model.entity") //设置entity包名
                            .other("model.dto") // 设置dto包名
                            .pathInfo(Collections.singletonMap(OutputFile.xml, finalProjectPath + "/src/main/resources/mapper")); // 设置mapperXml生成路径

                })
                //.injectionConfig(consumer -> {
                //    Map<String, String> customFile = new HashMap<>();
                //    // DTO
                //    customFile.put("DTO.java", "/templates/entityDTO.java.ftl");
                //    consumer.customFile(customFile);
                //})
                .strategyConfig(builder -> {
                    builder
                            .addTablePrefix("t_")
                            .addInclude("t_simple")
                            .build();


                    builder
                            .controllerBuilder()
                            //.superClass(BaseController.class)
                            .enableHyphenStyle()
                            .enableRestStyle()
                            .formatFileName("%sController")
                            .build();

                    builder
                            .serviceBuilder()
                            //.superServiceClass(BaseService.class)
                            //.superServiceImplClass(BaseServiceImpl.class)
                            .formatServiceFileName("%sService")
                            .formatServiceImplFileName("%sServiceImpl")
                            .build();

                    builder
                            .entityBuilder()
                            //.superClass(BaseEntity.class)
                            .disableSerialVersionUID()
                            .enableChainModel()
                            .enableLombok()
                            .enableRemoveIsPrefix()
                            .enableTableFieldAnnotation()
                            .enableActiveRecord()
                            .versionColumnName("version")
                            //.versionPropertyName("version")
                            .logicDeleteColumnName("deleted")
                            //.logicDeletePropertyName("deleteFlag")
                            .naming(NamingStrategy.no_change)
                            .columnNaming(NamingStrategy.underline_to_camel)
                            .addSuperEntityColumns("id", "created_by", "created_time", "updated_by", "updated_time")
                            .addIgnoreColumns("age")
                            .addTableFills(new Column("create_time", FieldFill.INSERT))
                            .addTableFills(new Property("updateTime", FieldFill.INSERT_UPDATE))
                            .idType(IdType.AUTO)
                            .formatFileName("%sEntity")
                            .build();

                    builder
                            .mapperBuilder()
                            .superClass(BaseMapper.class)
                            .enableMapperAnnotation()
                            .enableBaseResultMap()
                            .enableBaseColumnList()
                            .cache(MyMapperCache.class)
                            .formatMapperFileName("%sMapper")
                            .formatXmlFileName("%sMapper")
                            .build();
                })
                .execute();

    }


    public static void testDataSourceConfig() {
        new DataSourceConfig.Builder(url, username, password).build();
    }

    public static void testGlobalConfig() {
        new GlobalConfig.Builder()
                .outputDir("./")
                .author("baomidou")
                .enableSwagger()
                .dateType(DateType.TIME_PACK)
                .commentDate("yyyy-MM-dd")
                .build();
    }

    public static void testPackageConfig() {
        new PackageConfig.Builder()
                .parent("com.baomidou.mybatisplus.samples.generator")
                .moduleName("sys")
                .entity("po")
                .service("service")
                .serviceImpl("service.impl")
                .mapper("mapper")
                .xml("mapper.xml")
                .controller("controller")
                .pathInfo(Collections.singletonMap(OutputFile.xml, "D://"))
                .build();
    }

    public static void testInjectionConfig() {
        new InjectionConfig.Builder()
                .beforeOutputFile((tableInfo, objectMap) -> {
                    System.out.println("tableInfo: " + tableInfo.getEntityName() + " objectMap: " + objectMap.size());
                })
                .customMap(Collections.singletonMap("test", "baomidou"))
                .customFile(Collections.singletonMap("test.txt", "/templates/test.vm"))
                .build();
    }

    public static void testStrategyConfig() {
        new StrategyConfig.Builder()
                .enableCapitalMode()
                .enableSkipView()
                .disableSqlFilter()
                .likeTable(new LikeTable("USER"))
                .addInclude("t_simple")
                .addTablePrefix("t_", "c_")
                .addFieldSuffix("_flag")
                .build();
    }


    public static void testEntityStrategyConfig() {
        new StrategyConfig.Builder()
                .entityBuilder()
                .superClass(BaseEntity.class)
                .disableSerialVersionUID()
                .enableChainModel()
                .enableLombok()
                .enableRemoveIsPrefix()
                .enableTableFieldAnnotation()
                .enableActiveRecord()
                .versionColumnName("version")
                //.versionPropertyName("version")
                .logicDeleteColumnName("deleted")
                //.logicDeletePropertyName("deleteFlag")
                .naming(NamingStrategy.no_change)
                .columnNaming(NamingStrategy.underline_to_camel)
                .addSuperEntityColumns("id", "created_by", "created_time", "updated_by", "updated_time")
                .addIgnoreColumns("age")
                .addTableFills(new Column("create_time", FieldFill.INSERT))
                .addTableFills(new Property("updateTime", FieldFill.INSERT_UPDATE))
                .idType(IdType.AUTO)
                .formatFileName("%sEntity")
                .build();
    }

    public static void testControllerStrategyConfig() {
        new StrategyConfig.Builder()
                .controllerBuilder()
                .superClass(BaseController.class)
                .enableHyphenStyle()
                .enableRestStyle()
                .formatFileName("%sAction")
                .build();
    }

    public static void testServiceStrategyConfig() {
        new StrategyConfig.Builder()
                .serviceBuilder()
                .superServiceClass(BaseService.class)
                .superServiceImplClass(BaseServiceImpl.class)
                .formatServiceFileName("%sService")
                .formatServiceImplFileName("%sServiceImp")
                .build();
    }

    public static void testMapperStrategyConfig() {
        new StrategyConfig.Builder()
                .mapperBuilder()
                .superClass(BaseMapper.class)
                .enableMapperAnnotation()
                .enableBaseResultMap()
                .enableBaseColumnList()
                .cache(MyMapperCache.class)
                .formatMapperFileName("%sDao")
                .formatXmlFileName("%sXml")
                .build();
    }
}
