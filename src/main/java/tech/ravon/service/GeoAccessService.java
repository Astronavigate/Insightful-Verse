package tech.ravon.service;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.AsnResponse;
import com.maxmind.geoip2.model.CityResponse;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import tech.ravon.mapper.GeoAccessDao;
import tech.ravon.mapper.GeoLocationDao;
import tech.ravon.model.GeoAccess;
import tech.ravon.model.GeoLocation;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GeoAccessService {

    private DatabaseReader cityReader;
    private DatabaseReader asnReader;

    private final GeoAccessDao geoAccessDao;
    private final GeoLocationDao geoLocationDao;

    @PostConstruct
    public void init() throws IOException {
        ClassPathResource cityRes = new ClassPathResource("static/geoip/GeoLite2-City-20260119.mmdb");
        try (InputStream is = cityRes.getInputStream()) {
            cityReader = new DatabaseReader.Builder(is).build();
        }

        ClassPathResource asnRes = new ClassPathResource("static/geoip/GeoLite2-ASN-20260119.mmdb");
        try (InputStream is = asnRes.getInputStream()) {
            asnReader = new DatabaseReader.Builder(is).build();
        }
    }

    public String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            return ip.split(",")[0].trim();
        }

        ip = request.getHeader("X-Real-IP");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }

        return request.getRemoteAddr();
    }

    public GeoAccess setGeoAccess(String ip, Long userId) {
        GeoAccess geo = new GeoAccess();

        if (ip == null || ip.isBlank()) {
            geo.unknown(null);
            return geo;
        }

        try {
            InetAddress ipAddress = InetAddress.getByName(ip);

            if (ipAddress.isAnyLocalAddress() || ipAddress.isLoopbackAddress() || ipAddress.isSiteLocalAddress()) {
                geo.local();
            } else {
                CityResponse cityResp = cityReader.city(ipAddress);
                AsnResponse asnResp = asnReader.asn(ipAddress);

                geo.setNetwork(asnResp.network().toString());
                geo.setGeonameId(cityResp.city().geonameId());

                geo.setLatitude(cityResp.location().latitude());
                geo.setLongitude(cityResp.location().longitude());
                geo.setAccRadius(cityResp.location().accuracyRadius());

                geo.setContCode(safe(cityResp.continent().code()));
                geo.setContName(safe(cityResp.continent().name()));
                geo.setCtryCode(safe(cityResp.country().isoCode()));
                geo.setCtryName(safe(cityResp.country().name()));

                geo.setSub1Code(safe(cityResp.mostSpecificSubdivision().isoCode()));
                geo.setSub1Name(safe(cityResp.mostSpecificSubdivision().name()));
                geo.setSub2Code(safe(cityResp.leastSpecificSubdivision().isoCode()));
                geo.setSub2Name(safe(cityResp.leastSpecificSubdivision().name()));

                geo.setCityName(safe(cityResp.city().name()));
                geo.setPostcode(safe(cityResp.postal().code()));
                geo.setTimeZone(safe(cityResp.location().timeZone()));
                geo.setLocaleCode(cityResp.city().locales().get(0));

                geo.setAsn(asnResp.autonomousSystemNumber());
                geo.setAso(safe(asnResp.autonomousSystemOrganization()));
            }

            geo.setBase(null, userId, null, ip);

            GeoLocation existing = geoLocationDao.selectByUnique(
                    geo.getContCode(),
                    geo.getCtryCode(),
                    geo.getSub1Code(),
                    geo.getSub2Code(),
                    geo.getCityName(),
                    geo.getPostcode()
            );

            if (existing != null) {
                geo.setLocationId(existing.getLocationId());
            } else {
                geoLocationDao.insert(geo.getLocation());

                existing = geoLocationDao.selectByUnique(
                        geo.getContCode(),
                        geo.getCtryCode(),
                        geo.getSub1Code(),
                        geo.getSub2Code(),
                        geo.getCityName(),
                        geo.getPostcode()
                );
                geo.setLocationId(existing.getLocationId());
            }

            geoAccessDao.insert(geo);

            return geo;
        } catch (IOException | GeoIp2Exception e) {
            geo.unknown(ip);
            geo.setBase(null, userId, null, ip);
            return geo;
        }
    }

    public List<GeoAccess> getGeoAccessByUser(Long userId) {
        return geoAccessDao.selectByUser(userId);
    }

    private String safe(String s) {
        return s == null || s.isBlank() ? "Unknown" : s;
    }
}
