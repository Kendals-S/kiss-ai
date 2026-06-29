package com.ks.mcp.server.stdio.controller;

import com.ks.mcp.server.stdio.service.StdioToolService;
import com.ks.mcp.server.stdio.pojo.R;
import com.ks.mcp.server.stdio.pojo.WzryMusicVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestController {

    private final StdioToolService stdioToolService;

    @GetMapping("/wzry")
    public R<List<WzryMusicVO>> wzry(@RequestParam String name) {
        return stdioToolService.wzryMusic(name);

    }
}
