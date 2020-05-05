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
    private int daysInfected;
    
    public Cell (int x, int y, boolean infected, int daysInfected) {
        super(x, y);
        this.infected = infected;
        this.daysInfected = infected ? daysInfected : 0;
    }
    
    public Cell (int x, int y) {
        super(x, y);
        this.infected = false;
        this.daysInfected = 0;
    }

    public boolean isInfected() {
        return infected;
    }

    public int getDaysInfected() {
        return daysInfected;
    }  
}
