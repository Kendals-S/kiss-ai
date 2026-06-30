package com.ks.mcp.server.client.controller;

import com.ks.mcp.server.client.pojo.R;
import com.ks.mcp.server.client.service.McpClientService;
import io.modelcontextprotocol.spec.McpSchema;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mcp/client")
@RequiredArgsConstructor
public class McpClientController {

    private final McpClientService mcpClientService;

    /**
     * 聊天
     *
     * @param message 消息
     * @return String
     */
    @RequestMapping("/chat")
    public R<String> chat(String message) {
        String chat = mcpClientService.chat(message);
        return R.success(chat);
    }

    /**
     * 调用mcp服务
     *
     * @param type 调用的mcp服务类型
     * @return McpSchema.CallToolResult
     */
    @RequestMapping("/callToolResult")
    public R<McpSchema.CallToolResult> callToolResult(String type) {
        McpSchema.CallToolResult callToolResult = mcpClientService.callToolResult(type);
        return R.success(callToolResult);
    }
}
