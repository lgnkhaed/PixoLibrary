package com.pixo.bib.pixolibrary.metaData;
import java.util.ArrayList;
import java.util.List;

public class MetaData {

    private String imagePath;  
    private List<String> tags = new ArrayList<>();
    private List<String> transformations = new ArrayList<>();

    public MetaData(String imagePath) {
    }



    // Getters et Setters
    public String getImagePath() { return imagePath; }
    public void setImagePath(String path) { this.imagePath = path; }
    public List<String> getTags() { return tags; }
    public List<String> getTransformations() { return transformations; }
}
