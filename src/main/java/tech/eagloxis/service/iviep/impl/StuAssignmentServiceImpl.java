package tech.eagloxis.service.iviep.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import tech.eagloxis.model.iviep.StuAssignment;
import tech.eagloxis.service.iviep.StuAssignmentService;
import tech.eagloxis.mapper.StuAssignmentDao;
import org.springframework.stereotype.Service;

import java.util.Collection;

/**
* @author Anubis
* @description 针对表【stuassignments】的数据库操作Service实现
* @createDate 2024-11-03 21:56:02
*/
@Service
public class StuAssignmentServiceImpl extends ServiceImpl<StuAssignmentDao, StuAssignment>
    implements StuAssignmentService{

    @Override
    public boolean saveBatch(Collection<StuAssignment> entityList) {
        return super.saveBatch(entityList);
    }

    @Override
    public boolean saveOrUpdateBatch(Collection<StuAssignment> entityList) {
        return super.saveOrUpdateBatch(entityList);
    }

    @Override
    public boolean removeBatchByIds(Collection<?> list) {
        return super.removeBatchByIds(list);
    }

    @Override
    public boolean updateBatchById(Collection<StuAssignment> entityList) {
        return super.updateBatchById(entityList);
    }
}




