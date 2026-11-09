package co.edu.uptc.repository;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JsonRepository<T> implements Repository<T> {

    private final String fileName;
    private final Gson gson;
    private final Type type;

    public JsonRepository(String fileName, Type type) {
        this.fileName = fileName;
        this.type = type;
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .create();
    }

    @Override
    public void save(T entity) {
        List<T> data = findAll();
        data.add(entity);
        saveAll(data);
    }

    @Override
    public void saveAll(List<T> data) {
        File file = new File(fileName);
        File parent = file.getParentFile();
        if (parent != null) {
            parent.mkdirs();
        }
        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(data, writer);
        } catch (IOException e) {
            throw new IllegalStateException("Error writing " + fileName + ": " + e.getMessage(), e);
        }
    }

    @Override
    public List<T> findAll() {
        File file = new File(fileName);
        if (!file.exists()) {
            return new ArrayList<>();
        }
        try (FileReader reader = new FileReader(file)) {
            List<T> data = gson.fromJson(reader, type);
            if (data == null) {
                return new ArrayList<>();
            }
            return data;
        } catch (IOException e) {
            throw new IllegalStateException("Error reading " + fileName + ": " + e.getMessage(), e);
        }
    }
}