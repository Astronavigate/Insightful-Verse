package tech.ravon.vo.inver;

import lombok.Data;
import java.util.HashMap;
import java.util.Map;

@Data
public class AudioInfoVO {
    private Integer duration;
    private String bitrate;
    private Integer sampleRate;
    private String channels;
    private String encodingType;

    private String cover;

    private Map<String, String> tags = new HashMap<>();

    public void addTag(String key, String value) {
        if (value != null && !value.trim().isEmpty()) {
            this.tags.put(key.toLowerCase(), value);
        }
    }
}