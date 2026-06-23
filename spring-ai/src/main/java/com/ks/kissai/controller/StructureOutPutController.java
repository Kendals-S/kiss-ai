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

/**
 * 结构化输出示例。
 *
 * <p>方式：/outPut 使用 BeanOutputConverter，/outPutEntity 使用 entity(Class)，
 * /outPutList 使用 ParameterizedTypeReference，/outPutMap 使用自定义 MapOutputConverter。</p>
 * <p>注意：模型输出必须符合目标结构，否则转换可能失败；Map 场景最好在提示词中明确 key 的含义。</p>
 */
@RestController
@RequestMapping("structure")
public class StructureOutPutController implements InitializingBean {

    @Autowired
    private DashScopeChatModel dashScopeChatModel;

    private ChatClient chatClient;


    /**
     * 使用 BeanOutputConverter 生成格式要求，再把模型文本转换为 Airplane。
     */
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


    /**
     * 使用 entity(Class) 直接获取单个 Airplane 对象，适合目标类型明确的场景。
     */
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


    /**
     * 使用 ParameterizedTypeReference 保留 List<Airplane> 的泛型类型信息。
     */
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

    /**
     * 使用自定义 MapOutputConverter 转换 Map 结构，提示词中最好明确 key 的含义。
     */
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


    /**
     * 初始化带日志 Advisor 的 ChatClient，便于学习观察请求和响应。
     */
    @Override
    public void afterPropertiesSet() {
        chatClient = ChatClient.builder(dashScopeChatModel)
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .build();
    }
}
