/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jaddon.icons;

import jaddon.controller.StaticStandard;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import net.sf.image4j.codec.ico.ICODecoder;

/**
 *
 * @author Paul
 */
public class IconPlus {
    
    public static ImageIcon getImageIcon(String path) {
        try {
            ImageIcon icon = null;
            if(path.endsWith(".ico")) {
                List<BufferedImage> image = ICODecoder.read(IconPlus.class.getResourceAsStream(path));
                icon = new ImageIcon(image.get(0));
            } else if(path.endsWith(".gif")) {
                icon = new ImageIcon(IconPlus.class.getResource(path));
            } else {
                icon = new ImageIcon(ImageIO.read(IconPlus.class.getResourceAsStream(path)));
            }
            return icon;
        } catch (Exception ex) {
            StaticStandard.logErr("Error while loading icon \"" + path + "\": " + ex, ex);
            return null;
        }
    }
    
    public static Image getImage(String path) {
        return getImageIcon(path).getImage();
    }
    
}
