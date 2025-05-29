package tech.ravon.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import tech.ravon.model.iviep.Test;

@Mapper
public interface TestDao extends BaseMapper<Test> {

}
