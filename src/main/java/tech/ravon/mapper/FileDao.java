package tech.ravon.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import tech.ravon.model.iviep.File;

import java.util.List;

@Mapper
public interface FileDao extends BaseMapper<File> {

    File getFileById(String fileId);

    List<File> getFilesByCourse(String courseId);

    void addFile(File file, String userId);

    void deleteFile(String fileId);

    List<File> getFileByName(String keyword);

}
