package com.ks.kissai.controller;


import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.HashMap;

@Slf4j
@RestController
@RequestMapping("/promptTemplate")
public class PromptTemplateController implements InitializingBean {

    @Autowired
    private DashScopeChatModel dashScopeChatModel;

    private ChatClient chatClient;

    @Value("classpath:template/open_source_system_prompt.st")
    private Resource resource;


    @GetMapping("resource")
    public Flux<String> resource(String topic) {
        log.info("resource topic: {}", topic);
        HashMap<String, Object> variables = new HashMap<>();
        variables.put("topic", topic);
        variables.put("language", "Java");
        PromptTemplate promptTemplate = PromptTemplate.builder().resource(resource).variables(variables).build();
        return chatClient.prompt(promptTemplate.create()).system("你是一个专业的github项目收集人员").stream().content();
    }

    @Override
    public void afterPropertiesSet() {
        chatClient = ChatClient.builder(dashScopeChatModel)
                .build();
    }
}
