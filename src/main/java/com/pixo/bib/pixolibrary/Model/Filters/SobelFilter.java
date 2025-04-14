package com.pixo.bib.pixolibrary.Model.Filters;

import javafx.scene.image.*;

public class SobelFilter implements ImageFilter {

    private static final int[][] GX = {
            { -1, 0, 1 },
            { -2, 0, 2 },
            { -1, 0, 1 }
    };

    private static final int[][] GY = {
            { -1, -2, -1 },
            {  0,  0,  0 },
            {  1,  2,  1 }
    };

    @Override
    public Image apply(Image inputImage) {
        int width = (int) inputImage.getWidth();
        int height = (int) inputImage.getHeight();

        WritableImage outputImage = new WritableImage(width, height);
        PixelReader reader = inputImage.getPixelReader();
        PixelWriter writer = outputImage.getPixelWriter();

        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                int gx = 0, gy = 0;

                for (int j = -1; j <= 1; j++) {
                    for (int i = -1; i <= 1; i++) {
                        int argb = reader.getArgb(x + i, y + j);
                        int r = (argb >> 16) & 0xFF;
                        int g = (argb >> 8) & 0xFF;
                        int b = argb & 0xFF;

                        // on fait la moyenne pour avoir un niveau de gris
                        int gray = (r + g + b) / 3;

                        gx += gray * GX[j + 1][i + 1];
                        gy += gray * GY[j + 1][i + 1];
                    }
                }

                int magnitude = Math.min(255, (int) Math.sqrt(gx * gx + gy * gy));
                int color = (0xFF << 24) | (magnitude << 16) | (magnitude << 8) | magnitude;
                writer.setArgb(x, y, color);
            }
        }

        return outputImage;
    }
}


