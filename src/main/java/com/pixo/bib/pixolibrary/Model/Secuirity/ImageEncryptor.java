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
        //System.out.println("encrypting with in the imageEncryptor : " + java.util.Arrays.toString(seed));
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();

        PixelReader reader = image.getPixelReader();
        WritableImage encrypted = new WritableImage(width, height);
        PixelWriter writer = encrypted.getPixelWriter();

        //set List {size = number of pixels / h*w } {fill it from 0 to PixelsSize}
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < width * height; i++) indices.add(i);

        try {
            // Cretaing a SecureRandom instance
            // SecureRandom random = SecureRandom(seed)  {didn't work giving wrong tings}

            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            random.setSeed(seed);

            // reorganizing the pixels
            Collections.shuffle(indices, random);

            //
            for (int i = 0; i < indices.size(); i++) {
                // the position in the sourceImage
                int srcX = i % width;
                int srcY = i / width;
                // the position of the (srcX,srcY) after shuffling
                int destIndex = indices.get(i);
                // destIndex has the position in the pixels[] array , we calculate (destX,destY) where to write (srcX,srcY) in the encrypted image
                int destX = destIndex % width;
                int destY = destIndex / width;

                // we put the pixel from the source image at (srcX,srcY) in the dest image at the position (destX, destY)
                writer.setArgb(destX, destY, reader.getArgb(srcX, srcY));
            }

            return encrypted;

        } catch (Exception e) {
            throw new RuntimeException("SecureRandom init failed", e);
        }
    }


    // method to decrypt {imag} with the {seed}
    public static Image decrypt(Image image, byte[] seed) {
        //System.out.println("decrypting with in the imageENcry : " + java.util.Arrays.toString(seed));
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();

        PixelReader reader = image.getPixelReader();
        WritableImage decrypted = new WritableImage(width, height);
        PixelWriter writer = decrypted.getPixelWriter();

        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < width * height; i++) indices.add(i);

        SecureRandom random;
        try {
            random = SecureRandom.getInstance("SHA1PRNG");
            random.setSeed(seed);
        } catch (Exception e) {
            throw new RuntimeException("SecureRandom init failed", e);
        }
        Collections.shuffle(indices, random);

        // from the list we do the inverse
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