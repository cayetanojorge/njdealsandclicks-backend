package com.njdealsandclicks.searchrequest;


import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.njdealsandclicks.dto.searchrequest.SearchRequestCreateDTO;
import com.njdealsandclicks.util.PublicIdGeneratorService;
import com.njdealsandclicks.util.enums.Market;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class SearchRequestService {

    private static final Pattern URL_REGEX = Pattern.compile("^(https?://).+", Pattern.CASE_INSENSITIVE);
    private static final String PREFIX_PUBLIC_ID = "req_";

    private final SearchRequestRepository searchRequestRepository;
    private final PublicIdGeneratorService publicIdGeneratorService;

    public SearchRequestService(SearchRequestRepository searchRequestRepository,
                                 PublicIdGeneratorService publicIdGeneratorService) {
        this.searchRequestRepository = searchRequestRepository;
        this.publicIdGeneratorService = publicIdGeneratorService;
    }

    private String createPublicId() {
        return publicIdGeneratorService.generateSinglePublicId(
                PREFIX_PUBLIC_ID,
                searchRequestRepository::filterAvailablePublicIds
        );
    }

    @Transactional
    public void saveOrBump(SearchRequestCreateDTO dto, HttpServletRequest req) {
        if (dto == null || dto.getInput() == null || dto.getInput().isBlank()) return;

        // Normalizza input e calcola hash
        String normalized = dto.getInput().trim().toLowerCase();
        String hash = sha256(normalized);

        Market marketEnum = dto.getMarket();
        ZonedDateTime since = ZonedDateTime.now(ZoneId.of("UTC")).minusHours(24);

        // Cerca duplicati recenti per (market, queryHash)
        var recentOpt = searchRequestRepository.findLatestByMarketAndQueryHashSince(
                marketEnum, hash, since
        );

        if (recentOpt.isPresent()) {
            var last = recentOpt.get();
            last.setCount((last.getCount() == null ? 1 : last.getCount()) + 1);
            last.setUpdatedAt(ZonedDateTime.now(ZoneId.of("UTC")));
            searchRequestRepository.save(last);
            return;
        }

        int rc = (dto.getResultsCount() == null) ? 0 : Math.max(0, dto.getResultsCount());

        // Nuovo record
        SearchRequest r = new SearchRequest();
        r.setPublicId(createPublicId());
        r.setMarket(marketEnum);
        r.setInputText(cut(dto.getInput(), 2000));
        r.setUrl(URL_REGEX.matcher(dto.getInput().trim()).matches());
        r.setUserAgent(cut(req.getHeader("User-Agent"), 512));
        r.setIpAddress(cut(clientIp(req), 100));
        r.setAcceptLanguage(cut(req.getHeader("Accept-Language"), 100));
        r.setDevice(cut(guessDevice(req.getHeader("User-Agent")), 120));
        r.setResultsCount(rc);
        r.setPath(cut(dto.getPath(), 300));
        r.setReferrer(cut(dto.getRef(), 300));
        // r.setStatus(SearchRequestStatus.NEW);
        r.setQueryHash(hash);

        // se vuoi salvare anche clientTs in entity:
        // r.setClientTs(dto.getClientTs());

        searchRequestRepository.save(r);
    }

    private String clientIp(HttpServletRequest req) {
        String[] headers = {
            "X-Forwarded-For", "CF-Connecting-IP", "X-Real-IP"
        };
        for (String h : headers) {
            String v = req.getHeader(h);
            if (v != null && !v.isBlank()) return v.split(",")[0].trim();
        }
        return req.getRemoteAddr();
    }

    private String sha256(String s) {
        try {
            var md = java.security.MessageDigest.getInstance("SHA-256");
            byte[] d = md.digest(s.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : d) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            return s;
        }
    }

    private String guessDevice(String ua) {
        if (ua == null) return null;
        String x = ua.toLowerCase();
        if (x.contains("mobile")) return "mobile";
        if (x.contains("tablet") || x.contains("ipad")) return "tablet";
        return "desktop";
    }

    private String cut(String s, int max) {
        if (s == null) return null;
        s = s.trim();
        return s.length() <= max ? s : s.substring(0, max);
    }
}
