/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jaddon.utils;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;

/**
 *
 * @author Paul
 */
public class ImageUtils {
    
    public static ImageIcon combineIcons(ImageIcon... images) {
        BufferedImage combined_image = new BufferedImage(images[0].getIconWidth(), images[0].getIconHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = combined_image.createGraphics();
        for(ImageIcon image : images) {
            if(image == null) {
                continue;
            }
            g.drawImage(image.getImage(), 0, 0, null);
        }
        g.dispose();
        return new ImageIcon(combined_image);
    }
    
}
