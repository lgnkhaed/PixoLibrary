package com.pixo.bib.pixolibrary.Model.metaData;
import com.google.gson.Gson;
import com.google.gson.Gson.*;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MetaDataManager {
    private static final String METADATA_FILE = "data/metadata.json";
    private List<MetaData> metadataList = new ArrayList<>();
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();;

    //charge metaData from Json file
    public void loadMetadata() {
        try (Reader reader = new FileReader(METADATA_FILE)) {
            Type type = new TypeToken<List<MetaData>>(){}.getType();
            metadataList = gson.fromJson(reader, type);
            if (metadataList == null) metadataList = new ArrayList<>();
        } catch (IOException e) {
            System.err.println("No metaData file found , Creating new List ! ");
            metadataList = new ArrayList<>();
        }
    }

    //sava MetaData to jsonFile
    public void saveMetadata() {
        try (Writer writer = new FileWriter(METADATA_FILE)) {
            gson.toJson(metadataList, writer);
        } catch (IOException e) {
            System.err.println("Error while savin to MetaData : " + e.getMessage());
        }
    }

    //add Tag to Image
    public void addTag(String imagePath, String tag) {
        MetaData metadata = getOrCreateMetadata(imagePath);
        if (!metadata.getTags().contains(tag)) {
            metadata.getTags().add(tag);
            saveMetadata();
        }
    }

    //remove tag from Metadata of the image specified with the Path {imagePath}
    public void removeTag(String imagePath, String tag) {
        getMetadataForImage(imagePath).ifPresent(metadata -> {
            metadata.getTags().remove(tag);
            saveMetadata();
        });
    }



    //return List of tags of the image specified with {imagePath}
    public List<String> getTagsForImage(String imagePath) {
        return getMetadataForImage(imagePath)
                .map(MetaData::getTags)
                .orElse(new ArrayList<>());
    }

    //look for Metadata of the image with {ImagePath} , if the image is Found so the metaData is  found we return the metadata else we create a newMetaData with the imagePath specofoed
    private MetaData getOrCreateMetadata(String imagePath) {
        return getMetadataForImage(imagePath)
                .orElseGet(() -> {
                    MetaData newMetadata = new MetaData(imagePath);
                    metadataList.add(newMetadata);
                    return newMetadata;
                });
    }

    // search in metadataList about the metadata for the picture with {imagePath} and return it
    private java.util.Optional<MetaData> getMetadataForImage(String imagePath) {
        return metadataList.stream()
                .filter(m -> m.getImagePath().equals(imagePath))
                .findFirst();
    }


    // add transformation to the Image {used when we add transformation}
    public void addTransformation(String imagePath, String transformation) {
        MetaData metadata = getOrCreateMetadata(imagePath);
        if (!metadata.getTransformations().contains(transformation)) {
            metadata.getTransformations().add(transformation);
            saveMetadata();
        }
    }

    // return the Transformation applied to an Image
    public List<String> getTransformationsForImage(String imagePath) {
        return getMetadataForImage(imagePath)
                .map(MetaData::getTransformations)
                .orElse(new ArrayList<>());
    }

    // return True if the image wih {imagePath} has {transformation} as tage else return false
    public boolean hasTransformation(String imagePath, String transformation) {
        return getMetadataForImage(imagePath)
                .map(m -> m.getTransformations().contains(transformation))
                .orElse(false);
    }

    // return the paths of images that has the {tag} in the MetaData
    public List<String> getImagesByTag(String tag) {
        loadMetadata();
        return metadataList.stream()
                .filter(m -> m.getTags().contains(tag))
                .map(MetaData::getImagePath)
                .collect(Collectors.toList());
    }


    public List<String> getAllUniqueTags() {
        loadMetadata();
        return metadataList.stream()
                .flatMap(m -> m.getTags().stream())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

}