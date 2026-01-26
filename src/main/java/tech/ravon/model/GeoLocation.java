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

package tech.ravon.model;

import lombok.Data;

@Data
public class GeoLocation {
    private Integer locationId;
    private String contCode;
    private String contName;
    private String ctryCode;
    private String ctryName;
    private String sub1Code;
    private String sub1Name;
    private String sub2Code;
    private String sub2Name;
    private String cityName;
    private String timeZone;
    private String localeCode;
    private String postcode;
}
