package com.wenqi.springboot.elasticsearch.controller;

import com.wenqi.springboot.elasticsearch.model.BulkOperation;
import com.wenqi.springboot.elasticsearch.model.ResponseResult;
import com.wenqi.springboot.elasticsearch.service.IWebsiteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 批量操作控制器
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/website/bulk")
public class BulkOperationController {

    private final IWebsiteService websiteService;

    /**
     * 执行批量脚本操作
     *
     * @param operations 批量操作列表
     * @return 操作结果
     */
    @PostMapping("/script")
    public ResponseResult<Map<String, Object>> bulkScriptOperations(@RequestBody List<BulkOperation> operations) {
        return ResponseResult.success(websiteService.bulkScriptOperations(operations));
    }

    /**
     * 执行批量REST操作（create、update、delete、index）
     *
     * @param operations 批量操作列表
     * @return 操作结果
     */
    @PostMapping("/rest")
    public ResponseResult<Map<String, Object>> bulkRestOperations(@RequestBody List<BulkOperation> operations) {
        return ResponseResult.success(websiteService.bulkRestOperations(operations));
    }
}