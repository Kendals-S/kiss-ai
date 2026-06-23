package com.ks.kissai.tool;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Component
@Slf4j
public class TimeTools {

    @Tool(description = "根据用户输入的时区获取该时区当前时间")
    public static String getTimeByZoneId(@ToolParam(description = "时区，比如 Asia/Shanghai") String zoneId) {
        ZoneId zone = ZoneId.of(zoneId);
        log.info("getTimeByZoneId zoneId: {}", zone);
        ZonedDateTime zonedDateTime = ZonedDateTime.now(zone);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return zonedDateTime.format(dateTimeFormatter);
    }
}
