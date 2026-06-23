package com.ks.kissai.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.ks.kissai.pojo.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
public class TimeService {


    /**
     * 获取指定时区的时间
     *
     * @param zone Id
     * @return 时间
     */
    public R<String> getTimeByZoneId(Request zone) {
        ZoneId zoneId = ZoneId.of(zone.zoneId);
        log.info("getTimeByZoneId zoneId: {}", zoneId);
        ZonedDateTime zonedDateTime = ZonedDateTime.now(zoneId);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return R.success(zonedDateTime.format(dateTimeFormatter));
    }


    public record Request(@JsonProperty(required = true, value = "zoneId")
                          @JsonPropertyDescription("时区，比如 Asia/Shanghai")
                          String zoneId) {
    }
}
