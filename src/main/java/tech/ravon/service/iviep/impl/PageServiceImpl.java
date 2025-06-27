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

import org.springframework.stereotype.Service;
import tech.ravon.service.iviep.PageService;

import java.util.List;

@Service
public class PageServiceImpl implements PageService {
    @Override
    public <T> List<T> pageList(List<T> list, int page, int max) {
        int startIndex = (page - 1) * max;
        if (startIndex >= list.size()) {
            startIndex = list.size() - 1;
        } else if (startIndex < 0) {
            startIndex = 0;
        }
        int endIndex = Math.min(startIndex + max, list.size());
        if (endIndex > list.size()) {
            endIndex = list.size() - 1;
        } else if (endIndex < 0) {
            endIndex = 0;
        } else if (endIndex < startIndex) {
            endIndex = startIndex;
        }
        return list.subList(startIndex, endIndex);
    }
}
