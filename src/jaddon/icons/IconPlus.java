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
    
    private ImageIcon imageicon = null;
    private String path = "";
    private int size = -1;
    private boolean reloaded = false;
    
    public IconPlus(String path, int size) {
        this.path = path;
        this.size = size;
        reloadImage();
    }
    
    public ImageIcon reloadImage() {
        if(reloaded) {
            return imageicon;
        }
        try {
            imageicon = getImageIcon(String.format(path, size));
            reloaded = true;
        } catch (Exception ex) {
            reloaded = false;
            StaticStandard.logErr("Error while loading image: " + ex, ex);
        }
        return imageicon;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
        reloaded = false;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
        reloaded = false;
    }
    
    public ImageIcon getImageIcon() {
        return reloadImage();
    }
    
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
