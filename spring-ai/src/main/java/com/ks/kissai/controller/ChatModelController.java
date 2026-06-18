package com.ks.kissai.controller;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@Slf4j
@RestController
@RequestMapping("/model")
public class ChatModelController {

    @Autowired
    private DashScopeChatModel dashScopeChatModel;

    @GetMapping("/call")
    public String callString(@RequestParam String message) {
        log.info("call message: {}", message);
        return dashScopeChatModel.call(message);
    }

    @GetMapping("/callMessage")
    public String callMessage(@RequestParam String message) {
        log.info("callMessage message: {}", message);
        SystemMessage systemMessage = new SystemMessage("你是一个暴脾气，请用极致的臭嘴回答问题");
        return dashScopeChatModel.call(systemMessage, new UserMessage(message));
    }

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



    @GetMapping("/stream")
    public Flux<String> callStreamString(String message, HttpServletResponse response) {
        log.info("stream message: {}", message);
        response.setCharacterEncoding("UTF-8");
        return dashScopeChatModel.stream(message);
    }
}