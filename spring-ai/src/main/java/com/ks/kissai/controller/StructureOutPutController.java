package com.ks.kissai.controller;


import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.ks.kissai.converter.MapOutputConverter;
import com.ks.kissai.pojo.Airplane;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("structure")
public class StructureOutPutController implements InitializingBean {

    @Autowired
    private DashScopeChatModel dashScopeChatModel;

    private ChatClient chatClient;


    @GetMapping("outPut")
    public Airplane structureOutPut() {
        BeanOutputConverter<Airplane> beanOutputConverter = new BeanOutputConverter<>(Airplane.class);
        Map<String, Object> variables = Map.of("format", beanOutputConverter.getFormat());
        Prompt prompt = PromptTemplate.builder()
                .template("请帮我介绍一架国产战斗机{format}")
                .variables(variables)
                .build()
                .create();
        String content = chatClient
                .prompt(prompt)
                .system("你是一个资深的飞行器专家")
                .call()
                .content();
        assert content != null;
        return beanOutputConverter.convert(content);
    }


    @GetMapping("outPutEntity")
    public Airplane structureOutPutEntity() {
        Prompt prompt = PromptTemplate.builder()
                .template("请帮我介绍一架国产战斗机")
                .build()
                .create();
        return chatClient
                .prompt(prompt)
                .system("你是一个资深的飞行器专家")
                .call()
                .entity(Airplane.class);
    }


    @GetMapping("outPutList")
    public List<Airplane> structureOutPutList() {
        Prompt prompt = PromptTemplate.builder()
                .template("请帮我介绍十三个高端战斗机")
                .build()
                .create();
        return chatClient
                .prompt(prompt)
                .system("你是一个资深的飞行器专家")
                .call()
                .entity(new ParameterizedTypeReference<>() {
                });
    }

    @GetMapping("outPutMap")
    public Map<String, Airplane> structureOutPutMap() {
        Prompt prompt = PromptTemplate.builder()
                .template("请帮我介绍三个高端战斗机")
                .build()
                .create();
        return chatClient
                .prompt(prompt)
                .system("你是一个资深的飞行器专家")
                .call()
                .entity(new MapOutputConverter<>(Airplane.class));
    }


    @Override
    public void afterPropertiesSet() {
        chatClient = ChatClient.builder(dashScopeChatModel)
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .build();
    }
}
