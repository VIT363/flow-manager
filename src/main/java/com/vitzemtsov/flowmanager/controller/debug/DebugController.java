package com.vitzemtsov.flowmanager.controller.debug;

import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/files/debug")
public class DebugController {

    @GetMapping("/headers")
    public Map<String, String> headers(@RequestHeader Map<String, String> h) {
        return h;
    }
}