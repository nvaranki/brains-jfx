package com.varankin.brains.jfx.analyser;

/**
 * Отметка в графической зоне. 
 * 
 * @author &copy; 2014 Николай Варанкин
 */
public class Dot
{
    final float v;
    final long t;

    public Dot( float v, long t )
    {
        this.v = v;
        this.t = t;
    }
    
    public interface Convertor<T>
    {
        Dot toDot( T value, long timestamp );
    }
}
