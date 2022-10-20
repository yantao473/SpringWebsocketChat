package cn.ai4wms.server.controller;

import cn.ai4wms.server.handler.ServerHandler;
import cn.ai4wms.server.utils.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
@Tag(name = "测试")
public class TestController {
    @Autowired
    private ServerHandler serverHandler;

    @GetMapping("/getStream")
    @Operation(description = "获取流地址")
    public Result getStream(@RequestParam("ip") String ip) {
        serverHandler.sendMessage(ip);
        return Result.ok().put("ip", ip);
    }
}
