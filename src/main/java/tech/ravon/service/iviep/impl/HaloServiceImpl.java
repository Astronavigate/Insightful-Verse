package tech.ravon.service.iviep.impl;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.ravon.dao.iviep.HaloDao;
import tech.ravon.service.iviep.HaloService;

import java.util.List;

@Service
public class HaloServiceImpl implements HaloService {

    @Autowired
    HaloDao haloDao;

    @Transactional
    @Override
    public List<List<String>> runSQL(String sql) {
        return haloDao.doSql(sql);
    }
}
