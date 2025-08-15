package com.njdealsandclicks.searchrequest;


import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.njdealsandclicks.dto.searchrequest.SearchRequestCreateDTO;
import com.njdealsandclicks.util.PublicIdGeneratorService;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class SearchRequestService {

    private static final Pattern URL_REGEX = Pattern.compile("^(https?://).+", Pattern.CASE_INSENSITIVE);
    private static final String PREFIX_PUBLIC_ID = "req_";

    private final SearchRequestRepository searchRequestRepository;
    private final PublicIdGeneratorService publicIdGeneratorService;

    public SearchRequestService(SearchRequestRepository searchRequestRepository, PublicIdGeneratorService publicIdGeneratorService) {
        this.searchRequestRepository = searchRequestRepository;
        this.publicIdGeneratorService = publicIdGeneratorService;
    }

    /*
    4) Perché così è meglio
    Dedup: non riempi il DB con 100 righe uguali; incrementi count.
    Telemetria utile: resultsCount, path, referrer, acceptLanguage, device, ip → capisci cosa manca e dove.
    Robusto a proxy/CDN: prendi X-Forwarded-For quando deployerai dietro reverse proxy.
    Privacy: salvi solo ciò che serve (nessun dato personale). Se aggiungi email in futuro, fallo opt-in e con consenso.
     */

    private String createPublicId() {
        return publicIdGeneratorService.generateSinglePublicId(PREFIX_PUBLIC_ID, searchRequestRepository::filterAvailablePublicIds);
    }

    @Transactional
    public void saveOrBump(SearchRequestCreateDTO dto, HttpServletRequest req) {
        if (dto == null || dto.getInput() == null || dto.getInput().isBlank()) return;

        String normalized = dto.getInput().trim().toLowerCase();
        String hash = sha256(normalized);

        var since = java.time.ZonedDateTime.now(java.time.ZoneId.of("UTC")).minusHours(24);
        var recentOpt = searchRequestRepository.findTopByQueryHashAndCreatedAtGreaterThanEqualOrderByCreatedAtDesc(hash, since);

        if (recentOpt.isPresent()) {
            var last = recentOpt.get();
            last.setCount((last.getCount() == null ? 1 : last.getCount()) + 1);
            last.setUpdatedAt(ZonedDateTime.now(ZoneId.of("UTC")));
            searchRequestRepository.save(last);
            return;
        }

        SearchRequest r = new SearchRequest();
        r.setPublicId(createPublicId());
        r.setInputText(dto.getInput().trim());
        r.setUrl(URL_REGEX.matcher(dto.getInput().trim()).matches());
        r.setUserAgent(req.getHeader("User-Agent"));
        r.setIpAddress(clientIp(req));
        r.setAcceptLanguage(req.getHeader("Accept-Language"));
        r.setDevice(guessDevice(req.getHeader("User-Agent"))); // opzionale, semplice euristica
        r.setResultsCount(dto.getResultsCount());
        r.setPath(dto.getPath());
        r.setReferrer(dto.getRef());
        r.setStatus("NEW");
        r.setQueryHash(hash);
        searchRequestRepository.save(r);
    }

    private String clientIp(HttpServletRequest req) {
        String h = req.getHeader("X-Forwarded-For");
        if (h != null && !h.isBlank()) return h.split(",")[0].trim();
        return req.getRemoteAddr();
    }

    private String sha256(String s) {
        try {
            var md = java.security.MessageDigest.getInstance("SHA-256");
            byte[] d = md.digest(s.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b: d) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) { return s; }
    }

    private String guessDevice(String ua) {
        if (ua == null) return null;
        String x = ua.toLowerCase();
        if (x.contains("mobile")) return "mobile";
        if (x.contains("tablet") || x.contains("ipad")) return "tablet";
        return "desktop";
    }
}