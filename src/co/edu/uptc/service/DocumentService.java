package co.edu.uptc.service;

import java.lang.reflect.Type;
import java.util.List;

import co.edu.uptc.model.Document;
import co.edu.uptc.repository.JsonRepository;
import com.google.gson.reflect.TypeToken;

public class DocumentService {

    public DocumentService() {
        Type type = new TypeToken<List<Document>>() {}.getType();

        repository = new JsonRepository<>("document.json", type);
    }

    public List<Document> listar() {
        return repository.findAll();
    }

    public Document buscarPorId(String id) {

        return repository.findAll()
                .stream()
                .filter(m -> m.getId()==id)
                .findFirst()
                .orElse(null);
    }
}
