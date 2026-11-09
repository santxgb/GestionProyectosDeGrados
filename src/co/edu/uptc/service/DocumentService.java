package co.edu.uptc.service;

import java.util.List;

import co.edu.uptc.model.Document;
import co.edu.uptc.repository.Repository;

public class DocumentService {

    private final Repository<Document> repository;

    public DocumentService(Repository<Document> repository) {
        this.repository = repository;
    }

    public List<Document> findAll() {
        return repository.findAll();
    }

    public Document findById(String id) {
        return repository.findAll()
                .stream()
                .filter(d -> d.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public boolean create(Document document) {
        if (findById(document.getId()) != null) {
            return false;
        }
        repository.save(document);
        return true;
    }

    public boolean update(Document document) {
        List<Document> documents = repository.findAll();
        for (int i = 0; i < documents.size(); i++) {
            if (documents.get(i).getId().equals(document.getId())) {
                documents.set(i, document);
                repository.saveAll(documents);
                return true;
            }
        }
        return false;
    }

    public boolean delete(String id) {
        List<Document> documents = repository.findAll();
        boolean removed = documents.removeIf(d -> d.getId().equals(id));
        if (removed) {
            repository.saveAll(documents);
        }
        return removed;
    }

    // Pendiente: alertas de plazo (2 meses, 1 mes, 15 días) y reporte TXT
}