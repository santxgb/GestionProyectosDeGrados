package co.edu.uptc.service;

import java.lang.reflect.Type;
import java.util.List;

import co.edu.uptc.model.Document;
import co.edu.uptc.repository.JsonRepository;
import co.edu.uptc.repository.Repository;
import com.google.gson.reflect.TypeToken;

public class DocumentService {

    private final Repository<Document> repository;

    public DocumentService() {
        Type type = new TypeToken<List<Document>>() {}.getType();
        repository = new JsonRepository<>("document.json", type);
    }

    public List<Document> listar() {
        return repository.findAll();
    }

    public Document buscarPorId(String id) {
        for (Document document : repository.findAll()) {
            if (id != null && id.equals(document.getId())) {
                return document;
            }
        }
        return null;
    }
}
