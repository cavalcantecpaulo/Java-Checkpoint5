package service;

import client.GoogleMapsClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import model.*;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import repository.EnderecoRepository;

import java.util.List;

@ApplicationScoped
public class GoogleMapsService {
    @ConfigProperty(name = "google-api-key")
    String chaveApi;

    @Inject
    @RestClient
    GoogleMapsClient googleMapsClient;

    @Inject
    ViaCepService service;
    @Inject
    EnderecoRepository enderecoRepository;

    @Transactional
    public ResponseMelhorRotaDTO persistenciaBanco(String cep, String numero, List<DestinoDTO> destinos){
        ResponseMelhorRotaDTO endereco = retornoEndereco(cep, numero, destinos);
        enderecoRepository.persist(endereco);
        return endereco;
    }

    public List<ResponseMelhorRotaDTO> listarRotas(){
        return enderecoRepository.listarTodos();
    }

    public List<GoogleMapsResponseDTO> buscarRotas(String cep, String numero, List<DestinoDTO> destinos){
        if(!service.validarCep(cep)){
            return null;
        }
        String origemFormatada = service.preparaRequestEndereco(cep, numero);

        List<String> destinosFormatados = destinos.stream().
                map(destino -> service.preparaRequestEndereco(destino.getCep(), destino.getNumero()))
                .toList();

        GoogleMapsRequestDTO request = preparaRequestApi(origemFormatada, destinosFormatados);

        //FieldMask do Header
        String recursosApi = "originIndex,destinationIndex,distanceMeters,condition";

        return googleMapsClient.buscarRotas(chaveApi, recursosApi, request);
    }

    public GoogleMapsResponseDTO retornaMenorRota(String cep, String numero, List<DestinoDTO> destinos) {
        List<GoogleMapsResponseDTO> rotas = buscarRotas(cep, numero, destinos);

        if(rotas == null){
            return null;
        }

        int contador = 0;
        int menorDistancia = 0;
        GoogleMapsResponseDTO melhorRota = null;
        for (GoogleMapsResponseDTO endereco : rotas) {
            if(endereco.statusRetorno.equals("ROUTE_EXISTS")){
                if (contador == 0) {
                    menorDistancia = endereco.metrosDistancia;
                    melhorRota = endereco;
                }
                if(endereco.metrosDistancia < menorDistancia) {
                    menorDistancia = endereco.metrosDistancia;
                    melhorRota = endereco;
                }
                contador++;
            }
        }
        if(melhorRota == null){
            throw new RuntimeException("Nenhuma rota encontrada!!!");
        }
        return melhorRota;
    }

    public ResponseMelhorRotaDTO retornoEndereco(String cep, String numero, List<DestinoDTO> destinos){
        GoogleMapsResponseDTO responseRota = retornaMenorRota(cep, numero, destinos);
        if(responseRota!= null){
            //Indice do melhor destino
            int indiceDestino = responseRota.elementoDestino;

            DestinoDTO melhorRota = destinos.get(indiceDestino);
            ResponseMelhorRotaDTO rotaFormatada = new ResponseMelhorRotaDTO();

            rotaFormatada.setCepDestino(melhorRota.getCep());
            double distanciaKm = (double) responseRota.metrosDistancia / 1000;

            String distancia = String.format("%.2f", distanciaKm) + "km";
            rotaFormatada.setDistancia(distancia);

            rotaFormatada.setEnderecoDestino(service.preparaRequestEndereco(melhorRota.getCep(), melhorRota.getNumero()));

            //Endereco Origem
            rotaFormatada.setCepOrigem(cep);
            rotaFormatada.setEnderecoOrigem(service.preparaRequestEndereco(cep, numero));

            return rotaFormatada;
        }
        return null;
    }

    public GoogleMapsRequestDTO preparaRequestApi(String origem, List<String> destinos){
        //Objeto para busca dos endereços
        GoogleMapsRequestDTO request = new GoogleMapsRequestDTO();

        //Waypoint de origem, que recebe o endereço formatado
        PontoParadaDTO pontoOrigem = new PontoParadaDTO();
        pontoOrigem.setAddress(origem);

        //Transforma no objeto de origem, que irá na busca
        EnderecoRequestMapsDTO requestOrigemDTO = new EnderecoRequestMapsDTO();
        requestOrigemDTO.setPontoParada(pontoOrigem);

        request.setOrigins(List.of(requestOrigemDTO));

        //Destinos
        request.setDestinations(destinos.stream().map(destino -> {
            EnderecoRequestMapsDTO indiceDestino = new EnderecoRequestMapsDTO();
            PontoParadaDTO pontoParada = new PontoParadaDTO();

            pontoParada.setAddress(destino);
            indiceDestino.setPontoParada(pontoParada);
            return indiceDestino;
        }).toList());

        return request;
    }
}
