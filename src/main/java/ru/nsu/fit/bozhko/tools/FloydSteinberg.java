package ru.nsu.fit.bozhko.tools;

import ru.nsu.fit.bozhko.components.Parameter;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

// Аня
public class FloydSteinberg implements Filter{
    private List<Parameter> parameters = new ArrayList<>();
    private ArrayList<Integer> palette = new ArrayList<>();

    @Override
    public void execute(BufferedImage image) {
        int[] values = {0, 128, 255};
        for(int i : values){
            for(int j : values){
                for(int k : values){
                    palette.add((255 << 24) | (i << 16) | (j << 8) | k );
                }
            }
        }

        for (int x = 0; x < image.getWidth(); ++x) {
            for (int y = 0; y < image.getHeight(); ++y) {
                int oldRGB = image.getRGB(x, y);
                int R = (oldRGB >> 16) & 0xff;
                int G = (oldRGB >> 8) & 0xff;
                int B = oldRGB & 0xff;

                int newRGB = getNearestColor(R,  G,  B);
                image.setRGB(x, y, newRGB);

                double eR = R - ((newRGB >> 16) & 0xff);
                double eG = G - ((newRGB >> 8) & 0xff);
                double eB = B - (newRGB & 0xff);

                if(x < image.getWidth() - 1){
                    int rgb = image.getRGB(x + 1, y);
                    double r = Math.max(0, Math.min(((rgb >> 16) & 0xff) + eR * 7 / 16, 255));
                    double g = Math.max(0, Math.min(((rgb >> 8) & 0xff) + eG * 7 / 16, 255));
                    double b = Math.max(0, Math.min((rgb & 0xff) + eB * 7 / 16, 255));
                    image.setRGB(x + 1, y, (255 << 24) | ((int)r << 16) | ((int)g << 8) | (int)b);
                }

                if(x > 0 && y < image.getHeight() - 1){
                    int rgb = image.getRGB(x - 1, y + 1);
                    double r = Math.max(0, Math.min(((rgb >> 16) & 0xff) + eR * 3 / 16, 255));
                    double g = Math.max(0, Math.min(((rgb >> 8) & 0xff) + eG  * 3 / 16, 255));
                    double b = Math.max(0, Math.min((rgb & 0xff) + eB * 3 / 16, 255));
                    image.setRGB(x - 1, y + 1, (255 << 24) | ((int)r << 16) | ((int)g << 8) | (int)b);
                }

                if(y < image.getHeight() - 1){
                    int rgb = image.getRGB(x, y + 1);
                    double r = Math.max(0, Math.min(((rgb >> 16) & 0xff) + eR * 5 / 16, 255));
                    double g = Math.max(0, Math.min(((rgb >> 8) & 0xff) + eG * 5 / 16, 255));
                    double b = Math.max(0, Math.min((rgb & 0xff) + eB * 5 / 16, 255));
                    image.setRGB(x, y + 1, (255 << 24) | ((int)r << 16) | ((int)g << 8) | (int)b);
                }

                if(x < image.getWidth() - 1 && y < image.getHeight() - 1){
                    int rgb = image.getRGB(x + 1, y + 1);
                    double r = Math.max(0, Math.min(((rgb >> 16) & 0xff) + eR / 16, 255));
                    double g = Math.max(0, Math.min(((rgb >> 8) & 0xff) + eG / 16, 255));
                    double b = Math.max(0, Math.min((rgb & 0xff) + eB / 16, 255));
                    image.setRGB(x + 1, y + 1, (255 << 24) | ((int)r << 16) | ((int)g << 8) | (int)b);
                }
            }
        }
    }

    public double diff(double R1, double G1, double B1, double R2, double G2, double B2) {
        double rdiff = R1 - R2;
        double gdiff = G1 - G2;
        double bdiff = B1 - B2;
        return rdiff * rdiff + gdiff * gdiff + bdiff * bdiff;
    }

    private int getNearestColor(double R, double G, double B){
        int resultColor = palette.get(0);
        double minDiff = diff(R, G, B, (resultColor >> 16) & 0xff, (resultColor >> 8) & 0xff,
                resultColor & 0xff);

        for (int i = 1; i < palette.size(); ++i){
            double newDiff = diff(R, G, B, (palette.get(i) >> 16) & 0xff, (palette.get(i) >> 8) & 0xff,
                    palette.get(i) & 0xff);
            if(minDiff > newDiff){
                minDiff = newDiff;
                resultColor = palette.get(i);
            }
        }

        return resultColor;
    }


    @Override
    public List<Parameter> getParameters() {
        return parameters;
    }
}

