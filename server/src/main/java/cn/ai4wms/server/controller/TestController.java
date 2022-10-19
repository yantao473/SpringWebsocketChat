package cn.ai4wms.server.controller;

import cn.ai4wms.server.handler.ServerHandler;
import cn.ai4wms.server.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {
    @Autowired
    private ServerHandler serverHandler;

    @GetMapping("/getInfo")
    public Result getInfo() {
        return Result.ok();
    }
}
