package okadali.tangocache.dto;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
@Builder
public class ProxyResponse {
    private JsonNode body;
    private boolean fromCache;
    private HttpStatus status;
}
