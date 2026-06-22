package com.ks.kissai.controller;


import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/jdbc")
public class JdbcChatMemoryController implements InitializingBean {


    @Autowired
    private DashScopeChatModel dashScopeChatModel;

    @Autowired
    private ChatMemory chatMemory;

    private ChatClient chatClient;

    @Override
    public void afterPropertiesSet() {
        this.chatClient = ChatClient.builder(dashScopeChatModel)
                // 添加 ChatMemory
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build(), new SimpleLoggerAdvisor())
                // 设置 ChatClient 中 ChatModel 的 Options 参数
                .defaultOptions(DashScopeChatOptions.builder().topP(0.7).build())
                .build();
    }


    @GetMapping("callDb")
        public Flux<String> callDb(String message) {
        return chatClient
                .prompt()
                .user( message)
                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, "889112938484"))
                .stream()
                .content();
    }


}
