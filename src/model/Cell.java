/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.awt.Point;

/**
 *
 * @author rodrigo98rm
 */
public class Cell extends Point {
    
    private boolean infected;
    
    public Cell (int x, int y, boolean infected) {
        super(x, y);
        this.infected = infected;
    }
    
    public Cell (int x, int y) {
        super(x, y);
        this.infected = false;
    }

    public boolean isInfected() {
        return infected;
    }
    
}
