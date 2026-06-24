package com.ks.kissai.controller;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ChatClient 链式调用示例。
 *
 * <p>方式：启动后用 DashScopeChatModel 构建一次 ChatClient，/call 通过 prompt(message).call() 同步获取内容。</p>
 * <p>注意：需要系统词、记忆或参数时，改用 prompt().system().user() 等链式配置。</p>
 */
@Slf4j
@RestController
@RequestMapping("/client")
@RequiredArgsConstructor
public class ChatClientController implements InitializingBean {

    private final DashScopeChatModel dashScopeChatModel;

    private ChatClient chatClient;

    /**
     * 使用 ChatClient 最简链式写法完成一次同步对话。
     */
    @RequestMapping("/call")
    public String call(String message) {
        log.info("call message: {}", message);
        return chatClient.prompt(message).call().content();
    }

    /**
     * Bean 初始化后构建 ChatClient，后续请求复用同一个实例。
     */
    @Override
    public void afterPropertiesSet() {
        chatClient = ChatClient.builder(dashScopeChatModel)
                .build();
    }
}
