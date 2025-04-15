package com.pixo.bib.pixolibrary.Utilis;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import java.awt.image.BufferedImage;

public class ImageConverter {

    public static BufferedImage toBufferedImage(Image fxImage) {
        return SwingFXUtils.fromFXImage(fxImage, null);
    }

    public static Image toFXImage(BufferedImage bufferedImage) {
        return SwingFXUtils.toFXImage(bufferedImage, null);
    }
}
