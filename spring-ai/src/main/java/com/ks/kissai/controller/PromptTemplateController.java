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

/**
 * PromptTemplate 资源模板示例。
 *
 * <p>方式：/resource 从 classpath 模板文件读取提示词，注入 topic 和 language 变量后流式调用模型。</p>
 * <p>注意：变量名要和模板占位符一致，缺失变量会导致提示词生成不完整或失败。</p>
 */
@Slf4j
@RestController
@RequestMapping("/promptTemplate")
public class PromptTemplateController implements InitializingBean {

    @Autowired
    private DashScopeChatModel dashScopeChatModel;

    private ChatClient chatClient;

    @Value("classpath:template/open_source_system_prompt.st")
    private Resource resource;


    /**
     * 从资源模板生成 Prompt，注入 topic 和 language 变量后流式返回内容。
     */
    @GetMapping("resource")
    public Flux<String> resource(String topic) {
        log.info("resource topic: {}", topic);
        HashMap<String, Object> variables = new HashMap<>();
        variables.put("topic", topic);
        variables.put("language", "Java");
        PromptTemplate promptTemplate = PromptTemplate.builder().resource(resource).variables(variables).build();
        return chatClient.prompt(promptTemplate.create()).system("你是一个专业的github项目收集人员").stream().content();
    }

    /**
     * 初始化 ChatClient；模板只负责生成 Prompt，实际调用仍由 ChatClient 完成。
     */
    @Override
    public void afterPropertiesSet() {
        chatClient = ChatClient.builder(dashScopeChatModel)
                .build();
    }
}
