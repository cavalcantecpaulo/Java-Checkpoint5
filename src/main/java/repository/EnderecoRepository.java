package repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import model.ResponseMelhorRotaDTO;

import java.util.List;

@ApplicationScoped
public class EnderecoRepository implements PanacheRepository<ResponseMelhorRotaDTO> {
    public void salvarEndereco(ResponseMelhorRotaDTO rota) {
        persist(rota);
    }

    public List<ResponseMelhorRotaDTO> listarTodos() {
        return listAll();
    }
}
