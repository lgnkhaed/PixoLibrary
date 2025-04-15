package com.pixo.bib.pixolibrary.Model.Filters;

import javafx.scene.image.*;

public class FlipHorizontalFilter implements ImageFilter {

    @Override
    public Image apply(Image inputImage) {
        int width = (int) inputImage.getWidth();
        int height = (int) inputImage.getHeight();

        WritableImage outputImage = new WritableImage(width, height);
        PixelReader reader = inputImage.getPixelReader();
        PixelWriter writer = outputImage.getPixelWriter();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                writer.setArgb(width - x - 1, y, reader.getArgb(x, y));
            }
        }

        return outputImage;
    }
}
