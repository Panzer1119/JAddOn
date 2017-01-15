/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jaddon.list;

import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JList;

/**
 *
 * @author Paul
 */
public class JListUtils {
    
    public static JList updateJList(JList list, ArrayList<?> data, boolean keepSelected) {
        DefaultListModel model = new DefaultListModel();
        List<Object> selected_values = null;
        if(keepSelected) {
            selected_values = list.getSelectedValuesList();
        }
        for(Object o : data) {
            model.addElement(o);
        }
        list.setModel(model);
        if(keepSelected) {
            int[] indices = new int[selected_values.size()];
            int u = -1;
            for(Object o : selected_values) {
                for(int i = 0; i < list.getModel().getSize(); i++) {
                    if(o.equals(list.getModel().getElementAt(i))) {
                        if(u == -1) {
                            u++;
                        }
                        indices[u] = i;
                        u++;
                        break;
                    }
                }
            }
            if(u != -1) {
                int[] new_a = new int[u];
                for(int i = 0; i < new_a.length; i++) {
                    new_a[i] = indices[i];
                }
                list.setSelectedIndices(new_a);
            }
        }
        return list;
    }
    
}
