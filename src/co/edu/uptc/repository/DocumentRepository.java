package co.edu.uptc.repository;

import co.edu.uptc.model.Document;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class DocumentRepository {

    private List<Document> documents;
    private String filePath;

    public DocumentRepository(String filePath) {
        this.filePath = filePath;
        this.documents = new ArrayList<>();
        load();
    }

    public void add(Document item) {
        documents.add(item);
        save();
    }

    public List<Document> getAll() {
        return new ArrayList<>(documents);
    }

    public Document findById(String id) {
        for (Document document : documents) {
            if (document.getId().equals(id)) {
                return document;
            }
        }
        return null;
    }

    public void update(Document item) {
        for (int i = 0; i < documents.size(); i++) {
            if (documents.get(i).getId().equals(item.getId())) {
                documents.set(i, item);
                save();
                return;
            }
        }
    }

    public void delete(String id) {
        Document document = findById(id);
        if (document != null) {
            documents.remove(document);
            save();
        }
    }

    private void save() {
        File file = new File(filePath);
        File parent = file.getParentFile();
        if (parent != null) {
            parent.mkdirs();
        }
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
            out.writeObject(documents);
        } catch (IOException e) {
            System.out.println("Error al guardar los expedientes: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void load() {
        File file = new File(filePath);
        if (!file.exists()) {
            return;
        }
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            documents = (List<Document>) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error al cargar los expedientes: " + e.getMessage());
        }
    }
}