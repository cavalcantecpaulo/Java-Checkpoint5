package model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class GoogleMapsRequestDTO {
    @JsonProperty("origins")
    private List<EnderecoRequestMapsDTO> origins;

    @JsonProperty("destinations")
    private List<EnderecoRequestMapsDTO> destinations;

    @JsonProperty("travelMode")
    private String tipoViagem = "DRIVE";

    @JsonProperty("routingPreference")
    private String rotaPreferida = "TRAFFIC_UNAWARE";
}
