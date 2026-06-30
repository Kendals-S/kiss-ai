package com.ks.mcp.server.streamable.pojo;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public record WzryMusicVO(@JsonPropertyDescription("语音id") Integer id,
                          @JsonPropertyDescription("语言文本") String lines,
                          @JsonPropertyDescription("语音") String voice) {
}
