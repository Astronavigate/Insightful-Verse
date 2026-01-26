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
import java.time.Instant;

@Data
public class GeoAccess {
    private Long geoAccId;
    private Long userId;
    private Instant accessTime;
    private String ip;

    private Integer locationId;
    private Long geonameId;
    private Double latitude;
    private Double longitude;
    private Integer accRadius;
    private String network;
    private Long asn;
    private String aso;

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

    public GeoAccess() {
        this.accessTime = Instant.now();
    }

    public void setLocation(GeoLocation location) {
        this.locationId = location.getLocationId();
        this.contCode = location.getContCode();
        this.contName = location.getContName();
        this.ctryCode = location.getCtryCode();
        this.ctryName = location.getCtryName();
        this.sub1Code = location.getSub1Code();
        this.sub1Name = location.getSub1Name();
        this.sub2Code = location.getSub2Code();
        this.sub2Name = location.getSub2Name();
        this.cityName = location.getCityName();
        this.timeZone = location.getTimeZone();
        this.localeCode = location.getLocaleCode();
        this.postcode = location.getPostcode();
    }

    public GeoLocation getLocation() {
        GeoLocation geoLocation = new GeoLocation();

        geoLocation.setLocationId(locationId);
        geoLocation.setContCode(contCode);
        geoLocation.setContName(contName);
        geoLocation.setCtryCode(ctryCode);
        geoLocation.setCtryName(ctryName);
        geoLocation.setSub1Code(sub1Code);
        geoLocation.setSub1Name(sub1Name);
        geoLocation.setSub2Code(sub2Code);
        geoLocation.setSub2Name(sub2Name);
        geoLocation.setCityName(cityName);
        geoLocation.setTimeZone(timeZone);
        geoLocation.setLocaleCode(localeCode);
        geoLocation.setPostcode(postcode);
        return geoLocation;
    }

    public void initTime() {
        setAccessTime(Instant.now());
    }

    public void setGlobal(String contCode, String contName, String ctryCode, String ctryName) {
        setContCode(contCode);
        setContName(contName);
        setCtryCode(ctryCode);
        setCtryName(ctryName);
    }

    public void setRegion(String sub1Code, String sub1Name, String sub2Code, String sub2Name) {
        setSub1Code(sub1Code);
        setSub1Name(sub1Name);
        setSub2Code(sub2Code);
        setSub2Name(sub2Name);
    }

    public void setLocality(String cityName, String postcode, String timeZone, String localeCode) {
        setCityName(cityName);
        setPostcode(postcode);
        setTimeZone(timeZone);
        setLocaleCode(localeCode);
    }

    public void setAsnBlock(Long asn, String aso) {
        setAsn(asn);
        setAso(aso);
    }

    public void setCoords(Double latitude, Double longitude, Integer accRadius) {
        setLatitude(latitude);
        setLongitude(longitude);
        setAccRadius(accRadius);
    }

    public void setBase(Long geoAccId, Long userId, String ip) {
        setGeoAccId(geoAccId);
        setUserId(userId);
        setIp(ip);
    }

    public void setBase(Long geoAccId, Long userId, Instant time, String ip) {
        setBase(geoAccId, userId, ip);
        if (time == null) {
            setAccessTime(Instant.now());
        } else{
            setAccessTime(time);
        }
    }

    public void local() {
        String code = "LOC";
        String value = "Local";
        setGlobal(code, value, code, value);
        setRegion(code, value, code, value);
        setLocality(value, value, value, "en");
        setAsnBlock(4294967295L, "N/A");
        setCoords(728.66, 324.53, 0);
        setBase(null, null, "127.0.0.1");
        setNetwork("0.0.0.0/0");
    }

    public void unknown(String ip) {
        String code = "UNK";
        String value = "Unknown";
        setGlobal(code, value, code, value);
        setRegion(code, value, code, value);
        setLocality(value, value, value, "en");
        setAsnBlock(0L, "N/A");
        setCoords(0.0, 0.0, 0);
        setBase(null, null, ip);
        setNetwork("0.0.0.0/0");
    }

    public void blank() {
        setGlobal(null, null, null, null);
        setRegion(null, null, null, null);
        setLocality(null, null, null, null);
        setAsnBlock(null, null);
        setCoords(null, null, null);
        setBase(null, null, null);
        setNetwork(null);
    }

    public String getBaseInfo() {
        return String.format(
                "IP: %s, Locate: %s, %s%s ASN: %s%s",
                getIp(), getCityName() == null ? getLatitude() : getCtryName(),
                getSub1Name() == null ? getLongitude() : getSub1Code(),
                getCtryName() == null ? "" : ", " + getCtryCode(),
                getAsn() == null ? "N/A" : "AS" + getAsn(),
                getAso() == null ? "" : ", " + getAso()
        );
    }
}
