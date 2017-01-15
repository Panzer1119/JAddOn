/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jaddon.statistics;

import jaddon.exceptions.UnsupportedSizeException;
import jaddon.math.JData;

/**
 *
 * @author Paul
 */
public class JStatistic {
    
    private static final JData DOWNLOADEDDATA = new JData(0, JData.B);
    
    public static void addData(long datalength, int size) throws UnsupportedSizeException {
        DOWNLOADEDDATA.convertThis(size);
        DOWNLOADEDDATA.setNumber(DOWNLOADEDDATA.getNumber() + datalength);
        DOWNLOADEDDATA.convertThis(JData.B);
    }
    
    public static JData getData() {
        JData copy = new JData(DOWNLOADEDDATA.getNumber(), DOWNLOADEDDATA.getSize());
        return copy;
    }
    
}
