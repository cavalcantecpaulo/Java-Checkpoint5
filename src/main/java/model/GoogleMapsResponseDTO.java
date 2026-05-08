package model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GoogleMapsResponseDTO {
    @JsonProperty("originIndex")
    public int elementoOrigem;

    @JsonProperty("destinationIndex")
    public int elementoDestino;

    @JsonProperty("distanceMeters")
    public int metrosDistancia;

    @JsonProperty("condition")
    public String statusRetorno;


}
