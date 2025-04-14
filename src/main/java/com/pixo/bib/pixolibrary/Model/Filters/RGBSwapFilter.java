package com.pixo.bib.pixolibrary.Model.Filters;
import javafx.scene.image.*;

public class RGBSwapFilter implements ImageFilter {

    @Override
    public Image apply(Image inputImage) {
        int width = (int) inputImage.getWidth();
        int height = (int) inputImage.getHeight();

        WritableImage outputImage = new WritableImage(width, height);
        PixelReader reader = inputImage.getPixelReader();
        PixelWriter writer = outputImage.getPixelWriter();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int argb = reader.getArgb(x, y);

                int a = (argb >> 24) & 0xFF;
                int r = (argb >> 16) & 0xFF;
                int g = (argb >> 8) & 0xFF;
                int b = argb & 0xFF;

                // Inverser RGB -> BGR devient -> BRG
                int newArgb = (a << 24) | (b << 16) | (r << 8) | g;
                writer.setArgb(x, y, newArgb);
            }
        }

        return outputImage;
    }
}
