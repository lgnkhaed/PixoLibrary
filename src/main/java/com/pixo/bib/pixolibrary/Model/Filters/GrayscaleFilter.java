package com.pixo.bib.pixolibrary.Model.Filters;
import javafx.scene.image.*;

public class GrayscaleFilter implements ImageFilter {

    public Image apply(Image inputImage) {
        int width = (int) inputImage.getWidth();
        int height = (int) inputImage.getHeight();

        WritableImage outputImage = new WritableImage(width, height);
        PixelReader reader = inputImage.getPixelReader();
        PixelWriter writer = outputImage.getPixelWriter();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int argb = reader.getArgb(x, y);

                int r = (argb >> 16) & 0xFF;
                int g = (argb >> 8) & 0xFF;
                int b = argb & 0xFF;
                int gray = (r + g + b) / 3;

                int newArgb = (0xFF << 24) | (gray << 16) | (gray << 8) | gray;
                writer.setArgb(x, y, newArgb);
            }
        }

        return outputImage;
    }
}
