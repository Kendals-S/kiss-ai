package com.ks.mcp.server.client.service;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.spec.McpSchema;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class McpClientService implements InitializingBean {

    private final List<McpSyncClient> mcpSyncClients;

    private Map<String, McpSyncClient> mcpSyncClientMap;

    private final SyncMcpToolCallbackProvider toolCallbackProvider;

    private final DashScopeChatModel dashScopeChatModel;

    private ChatClient chatClient;


    /**
     * 手动调用调用mcp服务
     *
     * @param type 调用的mcp服务类型
     * @return McpSchema.CallToolResult
     */
    public McpSchema.CallToolResult callToolResult(String type) {
        McpSyncClient mcpSyncClient = mcpSyncClientMap.get(type);
        if (mcpSyncClient == null) {
            return McpSchema.CallToolResult.builder()
                    .isError(true)
                    .build();
        }
        log.info("开始调用mcp服务");
        HashMap<String, Object> meta = new HashMap<>();
        meta.put("name", "李白");
        McpSchema.CallToolRequest toolRequest = McpSchema.CallToolRequest.builder().arguments(meta).name(type).build();
        return mcpSyncClient.callTool(toolRequest);
    }

    /**
     * 聊天
     *
     * @param message 消息
     * @return String
     */
    public String chat(String message) {
        return chatClient
                .prompt()
                .user(message)
                .call()
                .content();
    }


    @Override
    public void afterPropertiesSet() {
        ToolCallback[] toolCallbacks = toolCallbackProvider.getToolCallbacks();
        ChatMemory chatMemory = MessageWindowChatMemory.builder()
                .maxMessages(100)
                .build();
        this.chatClient = ChatClient.builder(dashScopeChatModel)
                .defaultToolCallbacks(toolCallbacks)
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(chatMemory).build(),
                        new SimpleLoggerAdvisor()
                )
                .build();
        this.mcpSyncClientMap = mcpSyncClients.stream()
                .collect(Collectors.toMap(c -> c.getClientInfo().title(), Function.identity()));
    }
}
