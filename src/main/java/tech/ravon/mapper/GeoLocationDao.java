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

package tech.ravon.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import tech.ravon.model.GeoLocation;

@Mapper
public interface GeoLocationDao extends BaseMapper<GeoLocation> {

    GeoLocation selectById(Integer locationId);

    GeoLocation selectByUnique( String contCode, String ctryCode, String sub1Code, String sub2Code, String cityName, String postcode);

    int insert(GeoLocation geoLocation);

}
