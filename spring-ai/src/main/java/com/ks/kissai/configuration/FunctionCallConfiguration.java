package com.ks.kissai.configuration;

import com.ks.kissai.pojo.R;
import com.ks.kissai.service.TimeService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;

import java.util.function.Function;

@Configuration
public class FunctionCallConfiguration {


    /**
     * 函数调用 出入参必须是可以被JSON序列化的对象
     *
     * @param timeService 时间服务
     * @return 时间服务
     */
    @Bean
    @Description("根据用户输入的时区获取该时区当前的时间")
    public Function<TimeService.Request, R<String>> getTimeFunction(TimeService timeService) {
        return timeService::getTimeByZoneId;
    }
}
