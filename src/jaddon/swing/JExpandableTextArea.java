/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jaddon.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.JFrame;
import javax.swing.JPanel;
 
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JViewport;
 
/**
 * Expandable Text Area.
 * 
 * http://blue-walrus.com/2011/02/expandable-text-area-in-swing/
 * @author owatkins edited by Paul Hagedorn
 */
public class JExpandableTextArea extends JTextArea implements MouseMotionListener, MouseListener, KeyListener {
 
    private boolean mousePressedInTriangle = false;
    private Point mousePressedPoint = null;
    private Dimension sizeAtPress = null;
    private Dimension newSize = null;

    //reference to the underlying scrollpane
    private JScrollPane scrollPane = null;

    //height and width.. not hypotenuse
    private int triangleSize = 20;

    //is the mouse in the triangle
    private boolean inTheTriangleZone = false;

    public JExpandableTextArea(int i, int j) {
        super(i, j);
        this.addMouseMotionListener(this);
        this.addMouseListener(this);
    }

    /**
     * paint the text area
     * @param arg0 iwas
     */
    @Override
    protected void paintComponent(Graphics arg0) {
        super.paintComponent(arg0);
        Graphics2D graphics = (Graphics2D) arg0;
        if(inTheTriangleZone) {
            graphics.setColor(new Color(0.5f, 0.5f, 0.5f, 0.75f));
        } else {
            graphics.setColor(new Color(0.5f, 0.5f, 0.5f, 0.2f));
        }
        graphics.fillPolygon(getTriangle());
    }

    private JScrollPane getScrollPane() {
        //get scrollpane, if first time calling this method then add an addjustment listener
        //to the scroll pane
        if(this.getParent() instanceof JViewport) {
            if(scrollPane == null) {
                final JViewport p = (JViewport) this.getParent();
                scrollPane = (JScrollPane) p.getParent();
                scrollPane.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener(){
                    
                    @Override
                    public void adjustmentValueChanged(AdjustmentEvent e) {
                        //need to repaint the triangle when scroll bar moves
                        repaint();
                    }
                    
                });
            }
            return scrollPane;
        } else {
            throw new RuntimeException("Need a scrollpane");
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        final Point p = e.getPoint();
        final Polygon polygon = getTriangle();
        if(polygon.contains(p)){
            inTheTriangleZone = true;
            this.setCursor(new Cursor(Cursor.SE_RESIZE_CURSOR));
            this.repaint();
        } else {
            inTheTriangleZone = false;
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            this.repaint();
        }
    }

    /**
     *
     * Generates a polygon like this :
     *
     *   /|
     *  / |
     * ----
     *
     * This triangle represents the triangle for the bottom right corner of the
     * viewport.
     *
     * @return the triangle
     */

    private Polygon getTriangle() {
        JViewport viewport = getScrollPane().getViewport();
        //get bounds of viewport
        Rectangle bounds = viewport.getBounds();
        //position of viewport relative to text area.
        Point viewportPosition = viewport.getViewPosition();
        int w = viewportPosition.x + bounds.width;
        int h = viewportPosition.y + bounds.height;
        int[] xs = {w, w, w - triangleSize};
        int[] ys = {h - triangleSize, h, h};
        Polygon polygon = new Polygon(xs, ys, 3);
        return polygon;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        final Point p = e.getPoint();
        if(mousePressedInTriangle){ //mouse was pressed in triangle so we can resize
            inTheTriangleZone = true;
            int xDiff = (mousePressedPoint.x - p.x);
            int yDiff = (mousePressedPoint.y - p.y);
            newSize = new Dimension(sizeAtPress.width - xDiff, sizeAtPress.height - yDiff);
            getScrollPane().getViewport().setSize(newSize);
            getScrollPane().getViewport().setPreferredSize(newSize);
            getScrollPane().getViewport().setMinimumSize(newSize);
            getScrollPane().getParent().validate();
            this.repaint();
            this.getParent().validate();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        final Point p = e.getPoint();
        if(getTriangle().contains(p)) {
            mousePressedInTriangle = true;
            mousePressedPoint = p;
            sizeAtPress = getScrollPane().getSize();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        mousePressedInTriangle = false;
        mousePressedPoint = null;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
        inTheTriangleZone = false;
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    public int getTriangleSize() {
        return triangleSize;
    }

    public void setTriangleSize(int triangleSize) {
        this.triangleSize = triangleSize;
    }
    
    public static void test() {
        JFrame frame = new JFrame();
        frame.setPreferredSize(new Dimension(400, 400));
        frame.setLayout(new BorderLayout());
        JExpandableTextArea eta = new JExpandableTextArea(10, 20);
        JScrollPane sp = new JScrollPane(eta);
        JPanel panel = new JPanel();
        panel.add(sp);
        frame.add(panel, BorderLayout.CENTER);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    
}
