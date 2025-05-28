package tech.eagloxis.service.iviep.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import tech.eagloxis.model.iviep.IVVersion;
import tech.eagloxis.service.iviep.IVVersionService;
import tech.eagloxis.mapper.IVVersionDao;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author Anubis
* @description 针对表【ivversion】的数据库操作Service实现
* @createDate 2024-11-03 21:56:02
*/
@Service
public class IvversionServiceImpl extends ServiceImpl<IVVersionDao, IVVersion> implements IVVersionService{

    @Autowired
    private IVVersionDao ivVersionDao;

    @Override
    public IVVersion getFileById(String fileId) {
        return ivVersionDao.getFileById(fileId);
    }

    @Override
    public IVVersion getLatestVersion() {
        return ivVersionDao.getLatestVersion();
    }

    @Override
    public List<IVVersion> getAllVersion() {
        return ivVersionDao.getAllVersion();
    }

}




