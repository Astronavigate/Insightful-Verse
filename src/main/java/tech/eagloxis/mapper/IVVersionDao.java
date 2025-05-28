package tech.eagloxis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import tech.eagloxis.model.iviep.IVVersion;

import java.util.List;

@Mapper
public interface IVVersionDao extends BaseMapper<IVVersion> {

    IVVersion getFileById(String fileId);

    IVVersion getLatestVersion();

    List<IVVersion> getAllVersion();

}
