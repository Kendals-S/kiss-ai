package com.ks.mcp.server.stdio.config;

import com.ks.mcp.server.stdio.service.StdioToolService;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ToolCallbackProviderConfiguration {

    /**
     * 创建工具回调提供者
     *
     * @param stdioToolService 工具服务
     * @return 工具回调提供者
     */
    @Bean
    public ToolCallbackProvider toolCallbackProvider(StdioToolService stdioToolService) {
        return MethodToolCallbackProvider
                .builder()
                .toolObjects(stdioToolService)
                .build();
    }
}
