/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jaddon.math;

import jaddon.controller.StaticStandard;

/**
 *
 * @author Paul
 */
public enum NumeralSystem {
    
    UNÄR       (1),
    BINÄR      (2),
    TERNÄR     (3),
    QUATERNÄR  (4),
    QUINÄR     (5),
    SENÄR      (6),
    SEPTÄR     (7),
    OKTAL       (8),
    NONAL       (9),
    DEZIMAL     (10),
    SYSTEM11    (11),
    DUODEZIMAL  (12),
    SYSTEM13    (13),
    SYSTEM14    (14),
    SYSTEM15    (15),
    HEXADEZIMAL (16),
    SYSTEM17    (17),
    SYSTEM18    (18),
    SYSTEM19    (19),
    SYSTEM20    (20),
    SYSTEM21    (21),
    SYSTEM22    (22),
    SYSTEM23    (23),
    SYSTEM24    (24),
    SYSTEM25    (25),
    SYSTEM26    (26),
    SYSTEM27    (27),
    SYSTEM28    (28),
    SYSTEM29    (29),
    SYSTEM30    (30),
    SYSTEM31    (31),
    SYSTEM32    (32),
    SYSTEM33    (33),
    SYSTEM34    (34),
    SYSTEM35    (35),
    SYSTEM36    (36);
    
    /**
     * All normal numbers from 0 to 9
     */
    public static final String NUMBERSNORMAL = "0123456789";
    
    /**
     * All normal numbers from 0 to 9 and all advanced numbers from A to Z
     */
    public static final String NUMBERSADVANCED = NUMBERSNORMAL + "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    
    public static final NumeralSystem[] ALLNUMBERSYSTEMS = new NumeralSystem[] {UNÄR, BINÄR, TERNÄR, QUATERNÄR, QUINÄR, SENÄR, SEPTÄR, OKTAL, NONAL, DEZIMAL, SYSTEM11, DUODEZIMAL, SYSTEM13, SYSTEM14, SYSTEM15, HEXADEZIMAL, SYSTEM17, SYSTEM18, SYSTEM19, SYSTEM20, SYSTEM21, SYSTEM22, SYSTEM23, SYSTEM24, SYSTEM25, SYSTEM26, SYSTEM27, SYSTEM28, SYSTEM29, SYSTEM30, SYSTEM31, SYSTEM32, SYSTEM33, SYSTEM34, SYSTEM35, SYSTEM36};
    
    private final int system;
            
    NumeralSystem(int system) {
        this.system = system;
    }
    
    public final int getSystem() {
        return system;
    }
    
    @Override
    public String toString() {
        boolean german = StaticStandard.getLang().getLang().equalsIgnoreCase("DE");
        String temp = (german ? name() : name().toLowerCase().replaceAll("är", "ary").replaceAll("z", "c").replaceAll("k", "c"));
        temp = ("" + temp.charAt(0)).toUpperCase() + temp.substring(1);
        return temp;
    }
    
}
