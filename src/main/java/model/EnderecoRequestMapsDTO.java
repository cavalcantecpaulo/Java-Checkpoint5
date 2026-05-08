package model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EnderecoRequestMapsDTO {
    @JsonProperty("waypoint")
    private PontoParadaDTO pontoParada;
}