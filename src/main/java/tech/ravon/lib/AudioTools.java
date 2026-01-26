package tech.ravon.lib;

import lombok.extern.slf4j.Slf4j;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.images.Artwork;
import tech.ravon.vo.inver.AudioInfoVO;

import java.io.File;
import java.util.Base64;
import java.util.List;

@Slf4j
public class AudioTools {
    public static AudioInfoVO parseAudioMetadata(String filePath) {
        String absolutePath = System.getProperty("user.dir") + "/data" + filePath;
        File iofile = new File(absolutePath);

        if (!iofile.exists()) {
            log.warn("Target source not found on disk | Path: {}", absolutePath);
            return null;
        }

        try {
            AudioFile audioFile = AudioFileIO.read(iofile);
            AudioInfoVO audioInfoVO = new AudioInfoVO();

            Tag tag = audioFile.getTag();
            AudioHeader header = audioFile.getAudioHeader();

            if (header != null) {
                audioInfoVO.setDuration(header.getTrackLength());
                audioInfoVO.setBitrate(header.getBitRate());
                audioInfoVO.setSampleRate(header.getSampleRateAsNumber());
                audioInfoVO.setChannels(header.getChannels());
                audioInfoVO.setEncodingType(header.getEncodingType());

                log.info("Audio profile established: {}s, {}kbps, {}Hz",
                        audioInfoVO.getDuration(), audioInfoVO.getBitrate(), audioInfoVO.getSampleRate());
            }

            if (tag != null) {
                for (FieldKey key : FieldKey.values()) {
                    try {
                        String value = tag.getFirst(key);
                        if (value != null && !value.trim().isEmpty()) {
                            audioInfoVO.addTag(key.name(), value);
                        }
                    } catch (Exception ignored) {
                        // 静默处理不支持的键
                    }
                }

                List<Artwork> artworkList = tag.getArtworkList();
                if (!artworkList.isEmpty()) {
                    Artwork artwork = artworkList.get(0);
                    String base64Cover = Base64.getEncoder().encodeToString(artwork.getBinaryData());
                    audioInfoVO.setCover("data:" + artwork.getMimeType() + ";base64," + base64Cover);

                    log.info("Cover detected (Size: {} bytes)", artwork.getBinaryData().length);
                }
            }

            return audioInfoVO;

        } catch (Exception e) {
            log.error("Failed to parse audio infrastructure | Cause: {}", e.getMessage());
            return null;
        }
    }
}