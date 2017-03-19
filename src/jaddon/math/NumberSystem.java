/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jaddon.math;

/**
 *
 * @author Paul
 */
public enum NumberSystem {
    
    UNARY       (1),
    BINARY      (2),
    TERNARY     (3),
    QUATERNARY  (4),
    QUINARY     (5),
    SENARY      (6),
    SEPTARY     (7),
    OCTAL       (8),
    NONAL       (9),
    DECIMAL     (10),
    SYSTEM11    (11),
    DUODECIMAL  (12),
    SYSTEM13    (13),
    SYSTEM14    (14),
    SYSTEM15    (15),
    HEXADECIMAL (16),
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
    
    private final int system;
            
    NumberSystem(int system) {
        this.system = system;
    }
    
    public final int getSystem() {
        return system;
    }
    
}
