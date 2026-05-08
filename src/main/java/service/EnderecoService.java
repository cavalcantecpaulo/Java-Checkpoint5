package service;

import client.EnderecoClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import model.ViaCepDTO;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@ApplicationScoped
public class EnderecoService {
    @Inject
    @RestClient
    EnderecoClient client;

    public ViaCepDTO buscarEndereco(String cep){
        return client.buscarEndereco(cep);
    }
}
