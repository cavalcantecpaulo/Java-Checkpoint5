package model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ViaCepDTO {

    @JsonProperty("cep")
    private String cep;

    @JsonProperty("logradouro")
    private String logradouro;

    @JsonProperty("unidade")
    private String numero;

    @JsonProperty("bairro")
    private String bairro;

    @JsonProperty("localidade")
    private String cidade;

    @JsonProperty("uf")
    private String uf;

    @JsonProperty("estado")
    private String estado;

    @JsonProperty("regiao")
    private String regiao;

    @JsonProperty("ddd")
    private String ddd;

    @JsonProperty("erro")
    private String erro;
}
