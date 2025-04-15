package com.pixo.bib.pixolibrary.Model.Filters;

import javafx.scene.image.*;

public class RotateRightFilter implements ImageFilter {
    @Override
    public Image apply(Image inputImage) {
        int width = (int) inputImage.getWidth();
        int height = (int) inputImage.getHeight();

        WritableImage outputImage = new WritableImage(height, width);
        PixelReader reader = inputImage.getPixelReader();
        PixelWriter writer = outputImage.getPixelWriter();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                writer.setArgb(height - y - 1, x, reader.getArgb(x, y));
            }
        }

        return outputImage;
    }
}
