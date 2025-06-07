package okadali.tangocache.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static okadali.tangocache.constants.SystemConstants.ORIGIN;

@RestController
public class ProxyController {



    @RequestMapping("/**")
    public ResponseEntity<String> proxyRequest(HttpServletRequest request) {
        final String origin = System.getProperty(ORIGIN);
        String path = request.getRequestURI();
        return ResponseEntity.ok(path);
    }
}
