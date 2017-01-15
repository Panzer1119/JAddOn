/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jaddon.input;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

/**
 * Java Advanced Text Input
 * @author Paul
 */
public class JATextInput extends JFrame {
    
    private JTextArea ta = new JTextArea(10, 30);
    private JScrollPane sp = new JScrollPane(ta, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    private JPanel panelControl = new JPanel();
    
    public JATextInput(Component c, String title, String borderText) {
        setPreferredSize(new Dimension(100, 300));
        panelControl.setLayout(new FlowLayout());
        setLayout(new BorderLayout());
        setLocationRelativeTo(c);
        setTitle(title);
        ta.setBorder(new TitledBorder(new EtchedBorder(), borderText));
        add(sp, BorderLayout.CENTER);
    }
    
    public void setPanelControl(JPanel panelControl) {
        this.panelControl = panelControl;
    }
    
    public JPanel getPanelControl() {
        return panelControl;
    }
    
    public Component getCenter() {
        return sp;
    }
    
    public void setCenter(Component c) {
        add(c, BorderLayout.CENTER);
    }
    
    public JTextArea getTextArea() {
        return ta;
    }
}
