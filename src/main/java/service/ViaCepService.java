package service;

import client.EnderecoClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import model.ViaCepDTO;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@ApplicationScoped
public class ViaCepService {
    @Inject
    @RestClient
    EnderecoClient enderecoClient;

    public String preparaRequestEndereco(String cep, String numero){
        if(validarCep(cep)){
            ViaCepDTO endereco = enderecoClient.buscarEndereco(cep);
            if("true".equals(endereco.getErro())){
                return "Endereço não encontrado.";
            }
            String logradouro = endereco.getLogradouro();
            String cidade = endereco.getCidade();
            String uf = endereco.getUf();

            if(numero == null){
                return logradouro + ", " + cidade + ", " + uf;
            }
            return logradouro + ", " + numero + ", " + cidade + ", " + uf;
        }
        return "CEP inválido.";
    }

    public boolean validarCep(String cep) {
        return cep != null && cep.matches("\\d{8}");
    }

}
