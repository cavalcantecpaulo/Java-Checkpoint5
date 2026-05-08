package model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class BuscaRotaRequestDTO {
    private String cepOrigem;
    private String numeroOrigem;
    private List<DestinoDTO> destinos;
}
