package client;

import jakarta.ws.rs.*;
import model.ViaCepDTO;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "viacep-api")
@Path("/")
public interface EnderecoClient {

    @GET
    @Path("/{cep}/json/")
    ViaCepDTO buscarEndereco(@PathParam("cep") String cep);
}
