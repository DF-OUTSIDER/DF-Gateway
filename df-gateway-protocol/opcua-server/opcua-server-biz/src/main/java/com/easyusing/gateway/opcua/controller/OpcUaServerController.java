package com.easyusing.gateway.opcua.controller;

import com.easyusing.gateway.opcua.service.OpcUaServerService;
import com.easyusing.gateway.common.pojo.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author outsider
 * @date 2023/7/28
 */
@RestController
@Tag(name = "服务 - OPCUA SERVER")
@RequestMapping("/gateway/opcua-server")
public class OpcUaServerController {

    @Resource
    private OpcUaServerService opcUaServerService;

    @Operation(summary = "启动")
    @GetMapping("/start")
    public CommonResult start() {
        opcUaServerService.doStart();
        return CommonResult.success(true);
    }

    @Operation(summary = "关闭")
    @GetMapping("/shutdown")
    public CommonResult shutdown() {
        opcUaServerService.shutdown();
        return CommonResult.success(true);
    }

}
