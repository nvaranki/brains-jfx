package com.varankin.brains.jfx.analyser;

/**
 * Отметка в графической зоне. 
 * 
 * @author &copy; 2014 Николай Варанкин
 */
class Dot
{
    /** нормализованное значение */
    final float v;
    /** момент фиксации значения */
    final long t;

    /**
     * @param v нормализованное значение.
     * @param t момент фиксации значения. 
     */
    Dot( float v, long t )
    {
        this.v = v;
        this.t = t;
    }
    
}
