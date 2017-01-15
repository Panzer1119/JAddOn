/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jaddon.jtree;

import java.util.ArrayList;
import javax.swing.JTree;

/**
 *
 * @author Paul
 */
public interface ExpandedPaths {
    
    public ArrayList<String> getExpandedPaths(JTree tree);
    public void expandPaths(JTree tree, ArrayList<String> expandedpaths);
    
}
