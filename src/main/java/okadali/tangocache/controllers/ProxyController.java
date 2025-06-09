package okadali.tangocache.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.servlet.http.HttpServletRequest;
import okadali.tangocache.dto.ProxyRedisDTO;
import okadali.tangocache.services.ProxyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static okadali.tangocache.constants.SystemConstants.*;

@RestController
public class ProxyController {

    private final ProxyService proxyService;

    public ProxyController(ProxyService proxyService) {
        this.proxyService = proxyService;
    }

    @RequestMapping("/**")
    public ResponseEntity<JsonNode> proxyRequest(HttpServletRequest request) {
        final ProxyRedisDTO response = proxyService.handleProxyRequest(request);

        return ResponseEntity
                .status(response.getStatus())
                .header(X_CACHE, response.isFromCache() ? HIT : MISS)
                .body(response.getBody());
    }
}
