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

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import tech.ravon.model.iviep.Test;
import tech.ravon.service.iviep.TestService;
import tech.ravon.mapper.TestDao;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
* @author Anubis
* @description 针对表【test】的数据库操作Service实现
* @createDate 2024-11-03 21:56:02
*/
@Service
public class TestServiceImpl extends ServiceImpl<TestDao, Test>
    implements TestService{

    @Override
    public boolean saveBatch(Collection<Test> entityList, int batchSize) {
        return false;
    }

    @Override
    public boolean saveOrUpdateBatch(Collection<Test> entityList, int batchSize) {
        return false;
    }

    @Override
    public boolean updateBatchById(Collection<Test> entityList, int batchSize) {
        return false;
    }

    @Override
    public boolean saveOrUpdate(Test entity) {
        return false;
    }

    @Override
    public Test getOne(Wrapper<Test> queryWrapper, boolean throwEx) {
        return null;
    }

    @Override
    public Optional<Test> getOneOpt(Wrapper<Test> queryWrapper, boolean throwEx) {
        return Optional.empty();
    }

    @Override
    public Map<String, Object> getMap(Wrapper<Test> queryWrapper) {
        return Map.of();
    }

    @Override
    public <V> V getObj(Wrapper<Test> queryWrapper, Function<? super Object, V> mapper) {
        return null;
    }
}




