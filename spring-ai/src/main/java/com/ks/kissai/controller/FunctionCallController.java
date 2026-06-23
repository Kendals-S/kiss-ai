package com.ks.kissai.controller;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.ks.kissai.tool.TimeTools;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;


/**
 * 函数/工具调用示例。
 *
 * <p>方式：/call 通过 toolNames 调用 FunctionCallConfiguration 中的 Function Bean，
 * /call2 通过 tools(timeTools) 直接暴露工具对象方法并流式返回。</p>
 * <p>注意：toolNames 名称要和 @Bean 方法名一致，工具入参和返回值要便于 JSON 序列化。</p>
 */
@Slf4j
@RestController
@RequestMapping("/function")
public class FunctionCallController implements InitializingBean {

    @Autowired
    private DashScopeChatModel dashScopeChatModel;

    private ChatClient chatClient;

    @Autowired
    private TimeTools timeTools;

    /**
     * 初始化带窗口记忆和日志 Advisor 的 ChatClient，窗口记忆只保留最近 10 条消息。
     */
    @Override
    public void afterPropertiesSet() {
        ChatMemory chatMemory = MessageWindowChatMemory.builder().maxMessages(10).build();
        chatClient = ChatClient.builder(dashScopeChatModel)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build(), new SimpleLoggerAdvisor())
                .build();
    }

    /**
     * 通过 Function Bean 名称触发工具调用，名称需与配置类中的 @Bean 方法名一致。
     */
    @GetMapping("call")
    public String call(String message) {
        log.info("call message: {}", message);
        return chatClient.prompt()
                .user(message)
                .toolNames("getTimeFunction")
                .call().content();
    }

    /**
     * 直接暴露 TimeTools 中的工具方法，并以流式方式返回模型内容。
     */
    @GetMapping("call2")
    public Flux<String> call2(String message) {
        log.info("call2 message: {}", message);
        return chatClient.prompt()
                .user(message)
                .tools(timeTools)
                .stream().content();
    }


}
