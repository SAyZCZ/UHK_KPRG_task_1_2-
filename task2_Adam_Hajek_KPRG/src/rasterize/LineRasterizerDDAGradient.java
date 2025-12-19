package rasterize;

import java.awt.Color;

public class LineRasterizerDDAGradient extends LineRasterizer {

    public LineRasterizerDDAGradient(Raster raster) {
        super(raster);
    }

    /**
     * DDA line with per-step color interpolation (startColor -> endColor).
     *
     * @param gap same idea as in your other rasterizers:
     *            gap <= 1 means draw every step, gap=3 draws every 3rd step, etc.
     */
    public void rasterizeGradient(int x1, int y1, int x2, int y2,
                                  Color startColor, Color endColor,
                                  int gap) {
        if (gap <= 0) gap = 1;

        int dx = x2 - x1;
        int dy = y2 - y1;

        int steps = Math.max(Math.abs(dx), Math.abs(dy));
        if (steps == 0) {
            raster.setPixel(x1, y1, startColor.getRGB());
            return;
        }

        float x = x1;
        float y = y1;
        float xInc = dx / (float) steps;
        float yInc = dy / (float) steps;

        int r0 = startColor.getRed();
        int g0 = startColor.getGreen();
        int b0 = startColor.getBlue();

        int r1 = endColor.getRed();
        int g1 = endColor.getGreen();
        int b1 = endColor.getBlue();

        for (int i = 0; i <= steps; i += gap) {
            float t = i / (float) steps; // 0..1

            int r = Math.round(r0 + (r1 - r0) * t);
            int g = Math.round(g0 + (g1 - g0) * t);
            int b = Math.round(b0 + (b1 - b0) * t);

            int rgb = (0xFF << 24) | (r << 16) | (g << 8) | b;

            raster.setPixel(Math.round(x), Math.round(y), rgb);

            x += xInc * gap;
            y += yInc * gap;
        }
    }

    /**
     * Keeps the existing LineRasterizer API usable.
     * If someone calls rasterize(..., color, gap), it draws a solid-color DDA line.
     */
    @Override
    public void rasterize(int x1, int y1, int x2, int y2, Color color, int gap) {
        rasterizeGradient(x1, y1, x2, y2, color, color, gap);
    }
}