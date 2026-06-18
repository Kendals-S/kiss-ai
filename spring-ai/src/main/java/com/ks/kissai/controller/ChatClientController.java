package com.ks.kissai.controller;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/client")
public class ChatClientController implements InitializingBean {

    @Autowired
    private DashScopeChatModel dashScopeChatModel;

    private ChatClient chatClient;

    @RequestMapping("/call")
    public String call(String message) {
        log.info("call message: {}", message);
        return chatClient.prompt(message).call().content();
    }

    @Override
    public void afterPropertiesSet() {
        chatClient = ChatClient.builder(dashScopeChatModel)
                .build();
    }
}
