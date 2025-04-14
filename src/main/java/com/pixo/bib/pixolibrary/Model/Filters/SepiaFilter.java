package com.pixo.bib.pixolibrary.Model.Filters;
import javafx.scene.image.*;

public class SepiaFilter implements ImageFilter {

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

                int tr = (int)(0.393 * r + 0.769 * g + 0.189 * b);
                int tg = (int)(0.349 * r + 0.686 * g + 0.168 * b);
                int tb = (int)(0.272 * r + 0.534 * g + 0.131 * b);

                // Clamp
                tr = Math.min(255, tr);
                tg = Math.min(255, tg);
                tb = Math.min(255, tb);

                int sepia = (a << 24) | (tr << 16) | (tg << 8) | tb;
                writer.setArgb(x, y, sepia);
            }
        }

        return outputImage;
    }
}

