/**
 * Copyright 2025 Astronavigate
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tech.ravon.service.iviep.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import tech.ravon.model.iviep.IVVersion;
import tech.ravon.service.iviep.IVVersionService;
import tech.ravon.mapper.IVVersionDao;
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




