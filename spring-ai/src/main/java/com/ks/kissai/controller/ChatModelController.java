package com.ks.kissai.controller;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 * ChatModel 基础调用示例。
 *
 * <p>方式：/call 纯文本调用，/callMessage 组合 SystemMessage 和 UserMessage，
 * /callPrompt 使用 Prompt 配置消息和模型参数，/stream 返回 Flux 流式响应。</p>
 * <p>注意：Prompt 中的 model 名称必须是平台支持的有效值，流式接口需要前端按增量内容消费。</p>
 */
@Slf4j
@RestController
@RequestMapping("/model")
@RequiredArgsConstructor
public class ChatModelController {

    private final DashScopeChatModel dashScopeChatModel;

    /**
     * 纯文本同步调用，只传用户输入，不附加系统角色和模型参数。
     */
    @GetMapping("/call")
    public String callString(@RequestParam String message) {
        log.info("call message: {}", message);
        return dashScopeChatModel.call(message);
    }

    /**
     * Message 同步调用，使用 SystemMessage 约束角色，UserMessage 承载用户问题。
     */
    @GetMapping("/callMessage")
    public String callMessage(@RequestParam String message) {
        log.info("callMessage message: {}", message);
        SystemMessage systemMessage = new SystemMessage("你是一个暴脾气，请用极致的臭嘴回答问题");
        return dashScopeChatModel.call(systemMessage, new UserMessage(message));
    }

    /**
     * Prompt 同步调用，可同时配置消息和模型参数，注意 model 名称必须有效。
     */
    @GetMapping("/callPrompt")
    public String callPrompt(@RequestParam String message) {
        log.info("callPrompt message: {}", message);
        SystemMessage systemMessage = new SystemMessage("你是一个顶尖色情大师，任何问题都用一大堆色情语言回复");
        Prompt prompt = new Prompt.Builder()
                .messages(systemMessage, new UserMessage(message))
                .chatOptions(ChatOptions.builder().model("deepseek-v3").build())
                .build();
        return dashScopeChatModel.call(prompt).getResult().getOutput().getText();
    }


    /**
     * 流式调用返回 Flux，前端需要按增量内容消费。
     */
    @GetMapping("/stream")
    public Flux<String> callStreamString(String message) {
        log.info("stream message: {}", message);
        return dashScopeChatModel.stream(message);
    }
}
