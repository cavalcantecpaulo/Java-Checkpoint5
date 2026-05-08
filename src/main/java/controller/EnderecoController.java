package controller;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import model.*;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import service.EnderecoService;
import service.GoogleMapsService;
import service.ViaCepService;

import java.util.List;

@RequestScoped
@Tag(name = "Endereco", description = "Disponibiliza funcionalidades sobre endereços.")
@Path("/endereco")
public class EnderecoController {
    @Inject
    ViaCepService viaCepService;

    @Inject
    EnderecoService enderecoService;

    @Inject
    GoogleMapsService googleMapsService;

    @GET
    @Operation(summary = "Endpoint de busca de endereço na Api do Viacep, utilizando o CEP como parâmetro.")
    @Path("/{cep}")
    public ViaCepDTO buscarEnderecoViaCep(@PathParam("cep") String cep){
        return enderecoService.buscarEndereco(cep);
    }

    @GET
    @Operation(summary = "Endpoint que mostra endereço formatado pré busca no google maps.")
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/{cep}/enderecoPreparado")
    public Response enderecoPreConsulta(@PathParam("cep") String cep, @QueryParam("numero") String numero){
        String endereco = viaCepService.preparaRequestEndereco(cep, numero);

        if(endereco != null && !endereco.contains("não encontrado") && !endereco.contains("inválido.")) {
            return Response.ok(endereco).build();
        }

        return Response.status(Response.Status.BAD_REQUEST).entity("Endereço inválido").build();
    }

    @POST
    @Operation(summary = "Endpoint que calcula a menor rota entre as disponíveis.")
    @Path("/calculaRotas")
    public Response calcularMenorRota(BuscaRotaRequestDTO request) {
        try {
            ResponseMelhorRotaDTO melhorRota = googleMapsService.retornoEndereco(request.getCepOrigem(), request.getNumeroOrigem(), request.getDestinos());
            if(melhorRota != null)
                return Response.ok(melhorRota).build();
        } catch (Exception e) {
            return Response.status(400).entity("Erro ao calcular rota: " + e.getMessage()).build();
        }
        return Response.status(404).entity("Melhor rota não foi encontrada.").build();
    }

    @POST
    @Operation(summary = "Endpoint que salva a menor rota no banco de dados.")
    @Path("/salvarRota")
    public Response salvarRota(BuscaRotaRequestDTO request){
        try{
            ResponseMelhorRotaDTO objetoSalvo = googleMapsService.persistenciaBanco(request.getCepOrigem(), request.getNumeroOrigem(), request.getDestinos());
            if(objetoSalvo != null)
                return Response.status(Response.Status.CREATED).entity(objetoSalvo).build();

        } catch (Exception e) {
            return Response.status(400).entity("Erro ao salvar rota: " + e.getMessage()).build();
        }
        return Response.status(404).entity("Não foi possível salvar rota no banco de dados!").build();
    }

    @GET
    @Operation(summary = "Endpoint que retorna a lista de rotas do banco de dados.")
    @Path("/listarRotas")
    public Response listarTodas(){
        List<ResponseMelhorRotaDTO> listaRotas = googleMapsService.listarRotas();
        if (!listaRotas.isEmpty())
            return Response.ok(listaRotas).build();

        return Response.status(204).build();
    }
}
