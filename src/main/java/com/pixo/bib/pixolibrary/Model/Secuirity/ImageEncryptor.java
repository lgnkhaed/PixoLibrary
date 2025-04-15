package com.pixo.bib.pixolibrary.Model.Secuirity;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ImageEncryptor {


    // method to encrypt {Image} with the {seed}
    public static Image encrypt(Image image, byte[] seed) {
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();

        PixelReader reader = image.getPixelReader();
        WritableImage encrypted = new WritableImage(width, height);
        PixelWriter writer = encrypted.getPixelWriter();

        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < width * height; i++) indices.add(i);

        SecureRandom random = new SecureRandom(seed);
        Collections.shuffle(indices, random);

        for (int i = 0; i < indices.size(); i++) {
            int srcX = i % width;
            int srcY = i / width;
            int destIndex = indices.get(i);
            int destX = destIndex % width;
            int destY = destIndex / width;
            writer.setArgb(destX, destY, reader.getArgb(srcX, srcY));
        }

        return encrypted;

    }


    // method to decrypt {imag} with the {seed}
    public static Image decrypt(Image image, byte[] seed) {
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();

        PixelReader reader = image.getPixelReader();
        WritableImage decrypted = new WritableImage(width, height);
        PixelWriter writer = decrypted.getPixelWriter();

        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < width * height; i++) indices.add(i);

        SecureRandom random = new SecureRandom(seed);
        Collections.shuffle(indices, random);

        for (int i = 0; i < indices.size(); i++) {
            int destX = i % width;
            int destY = i / width;
            int srcIndex = indices.get(i);
            int srcX = srcIndex % width;
            int srcY = srcIndex / width;
            writer.setArgb(destX, destY, reader.getArgb(srcX, srcY));
        }

        return decrypted;
    }
}