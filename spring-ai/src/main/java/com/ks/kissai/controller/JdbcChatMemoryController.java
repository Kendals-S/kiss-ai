package com.ks.kissai.controller;


import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 * JDBC 会话记忆示例。
 *
 * <p>方式：通过 MessageChatMemoryAdvisor 接入持久化 ChatMemory，/callDb 使用 CONVERSATION_ID 读取同一会话历史。</p>
 * <p>注意：固定会话 ID 只适合演示，真实项目应使用用户 ID、会话 ID 等动态值。</p>
 */
@RestController
@RequestMapping("/jdbc")
@RequiredArgsConstructor
public class JdbcChatMemoryController implements InitializingBean {


    private final DashScopeChatModel dashScopeChatModel;

    private final ChatMemory chatMemory;

    private ChatClient chatClient;

    /**
     * 构建带 JDBC 记忆和日志 Advisor 的 ChatClient，并设置模型 topP 参数。
     */
    @Override
    public void afterPropertiesSet() {
        this.chatClient = ChatClient.builder(dashScopeChatModel)
                // 添加 ChatMemory
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build(), new SimpleLoggerAdvisor())
                // 设置 ChatClient 中 ChatModel 的 Options 参数
                .defaultOptions(DashScopeChatOptions.builder().topP(0.7).build())
                .build();
    }


    /**
     * 使用指定 CONVERSATION_ID 读取同一会话历史；固定 ID 仅适合演示。
     */
    @GetMapping("callDb")
    public Flux<String> callDb(String message) {
        return chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, "889112938484"))
                .stream()
                .content();
    }


}
