package tech.eagloxis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import tech.eagloxis.model.iviep.Test;

@Mapper
public interface TestDao extends BaseMapper<Test> {

}
