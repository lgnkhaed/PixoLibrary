package com.pixo.bib.pixolibrary.Model.metaData;
import java.util.ArrayList;
import java.util.List;

public class MetaData {
    private String imagePath;
    private List<String> tags = new ArrayList<>();
    private List<String> transformations = new ArrayList<>();

    //constructor by default {obligé de l'avoir pour Gson sinon ça ne  marche pas }
    public MetaData() {}


    public MetaData(String imagePath) {
        this.imagePath = imagePath;
    }

    //getters and setters
    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }

    public List<String> getTransformations() { return transformations; }
    public void setTransformations(List<String> transformations) { this.transformations = transformations; }
}