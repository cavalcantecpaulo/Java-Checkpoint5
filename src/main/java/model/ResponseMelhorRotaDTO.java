package model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "rota_completa")
public class ResponseMelhorRotaDTO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rota")
    private Long idRota;

    @Column(name = "cep_origem")
    private String cepOrigem;

    @Column(name = "endereco_origem")
    private String enderecoOrigem;

    @Column(name = "cep_destino")
    private String cepDestino;

    @Column(name = "endereco_destino")
    private String enderecoDestino;
    private String distancia;
}
