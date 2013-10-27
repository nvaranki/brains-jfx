package com.varankin.brains.jfx.analyser;

import javafx.scene.paint.Color;

/**
 * Отметка в графической зоне. 
 * 
 * @author &copy; 2013 Николай Варанкин
 */
public class Dot
{
    final float v;
    final long t;
    final Color color;
    final int[][] pattern;

    public Dot( float v, long t, Color color, int[][] pattern )
    {
        this.v = v;
        this.t = t;
        this.color = color;
        this.pattern = pattern;
    }
    
    public static final int[][] DOT     = new int[][]{{0,0}};
    public static final int[][] CROSS   = new int[][]{{0,0},{0,1},{1,0},{0,-1},{-1,0}};
    public static final int[][] CROSS45 = new int[][]{{0,0},{1,1},{1,-1},{-1,-1},{-1,1}};
    
}
