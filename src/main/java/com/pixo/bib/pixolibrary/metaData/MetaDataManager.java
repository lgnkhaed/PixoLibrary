package com.pixo.bib.pixolibrary.metaData;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pixo.bib.pixolibrary.metaData.MetaData;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MetaDataManager {
    private static final String METADATA_FILE = "metadata.json";
    private List<MetaData> metadataList = new ArrayList<>();
    private final Gson gson = new Gson();

    // Charge les métadonnées depuis le fichier JSON
    public void loadMetadata() {
        try (Reader reader = new FileReader(METADATA_FILE)) {
            Type type = new TypeToken<List<MetaData>>(){}.getType();
            metadataList = gson.fromJson(reader, type);
            if (metadataList == null) metadataList = new ArrayList<>();
        } catch (IOException e) {
            System.err.println("Aucun fichier de métadonnées trouvé, création d'une nouvelle liste.");
            metadataList = new ArrayList<>();
        }
    }

    // Sauvegarde les métadonnées dans le fichier JSON
    public void saveMetadata() {
        try (Writer writer = new FileWriter(METADATA_FILE)) {
            gson.toJson(metadataList, writer);
        } catch (IOException e) {
            System.err.println("Erreur lors de la sauvegarde des métadonnées : " + e.getMessage());
        }
    }

    // Ajoute un tag à une image
    public void addTag(String imagePath, String tag) {
        MetaData metadata = getOrCreateMetadata(imagePath);
        if (!metadata.getTags().contains(tag)) {
            metadata.getTags().add(tag);
            saveMetadata();
        }
    }

    // Supprime un tag d'une image
    public void removeTag(String imagePath, String tag) {
        getMetadataForImage(imagePath).ifPresent(metadata -> {
            metadata.getTags().remove(tag);
            saveMetadata();
        });
    }

    // Récupère les images associées à un tag
    public List<String> getImagesByTag(String tag) {
        return metadataList.stream()
                .filter(m -> m.getTags().contains(tag))
                .map(MetaData::getImagePath)
                .collect(Collectors.toList());
    }

    // Récupère les tags d'une image
    public List<String> getTagsForImage(String imagePath) {
        return getMetadataForImage(imagePath)
                .map(MetaData::getTags)
                .orElse(new ArrayList<>());
    }

    // Méthodes utilitaires
    private MetaData getOrCreateMetadata(String imagePath) {
        return getMetadataForImage(imagePath)
                .orElseGet(() -> {
                    MetaData newMetadata = new MetaData(imagePath);
                    metadataList.add(newMetadata);
                    return newMetadata;
                });
    }

    private java.util.Optional<MetaData> getMetadataForImage(String imagePath) {
        return metadataList.stream()
                .filter(m -> m.getImagePath().equals(imagePath))
                .findFirst();
    }
}