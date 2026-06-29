package com.ks.mcp.server.sse.service;

import com.ks.mcp.server.sse.pojo.R;
import com.ks.mcp.server.sse.pojo.WzryMusicVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StdioToolService {

    @Tool(description = "根据王者荣耀英雄名称搜索英雄语录")
    public R<List<WzryMusicVO>> wzryMusic(@ToolParam(description = "英雄名称") String name) {
        log.info("正在搜索王者荣耀英雄语录，英雄名称：{}", name);
        String url = "https://api.key5.site/API/king/wzry_music/index.php?apikey=50e7099a396e7cefcba6a5f1170f01a152334cd1bb36610d916c31cc1abb5819&msg=" + name;
        log.info("请求url：{}", url);
        RestClient restClient = RestClient.create();
        return restClient.get()
                .uri(url)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }

}
