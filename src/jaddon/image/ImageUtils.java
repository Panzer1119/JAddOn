/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jaddon.image;

import java.awt.Color;

/**
 *
 * @author Paul
 */
public class ImageUtils {
    
    public static Color[] getLinearGradient(Color color_start, Color color_stop, int steps) {
        final Color[] colors = new Color[steps];
        final double r_div = ((color_stop.getRed() - color_start.getRed()) / (steps - 1.0));
        final double g_div = ((color_stop.getGreen() - color_start.getGreen()) / (steps - 1.0));
        final double b_div = ((color_stop.getBlue() - color_start.getBlue()) / (steps - 1.0));
        for(int i = 0; i < steps; i++) {
            colors[i] = new Color((int) (color_start.getRed() + (i * r_div)), (int) (color_start.getGreen() + (i * g_div)), (int) (color_start.getBlue() + (i * b_div)));
        }
        return colors;
    }
    
}
