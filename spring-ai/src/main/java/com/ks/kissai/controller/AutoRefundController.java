package com.ks.kissai.controller;


import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.ks.kissai.tool.OrderTools;
import com.ks.kissai.util.WebUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@Slf4j
@RestController
@RequestMapping("/autoRefund")
@RequiredArgsConstructor
public class AutoRefundController implements InitializingBean {

    private final DashScopeChatModel dashScopeChatModel;

    private final ChatMemory chatMemory;

    private final WebUtils webUtils;

    private final OrderTools orderTools;

    @Value("classpath:template/auto_refund_system_prompt.st")
    private Resource systemPrompt;

    private ChatClient chatClient;

    @RequestMapping("/call")
    public Flux<String> call(String message) {
        String remoteAddr = webUtils.getClientIp();
        return chatClient
                .prompt().user(message).tools(orderTools)
                .advisors(id -> id.param(ChatMemory.CONVERSATION_ID, remoteAddr))
                .stream()
                .content();
    }


    @Override
    public void afterPropertiesSet() {
        chatClient = ChatClient.builder(dashScopeChatModel)
                .defaultAdvisors(new SimpleLoggerAdvisor(),
                        MessageChatMemoryAdvisor.builder(chatMemory).build())
                .defaultSystem(systemPrompt)
                .build();
    }
}
