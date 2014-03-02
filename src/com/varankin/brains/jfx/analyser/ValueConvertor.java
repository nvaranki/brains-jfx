package com.varankin.brains.jfx.analyser;

/**
 * Конвертер значений графика в координату Y {@link  WritableImage}.
 * 
 * @author &copy; 2014 Николай Варанкин
 */
final class ValueConvertor
{
    private volatile double v0, vx;
    private float vSize;

    ValueConvertor( float low, float high, int height )
    {
        v0 = high;
        vSize = high - low;
        vx = Double.valueOf( height - 1 ) / vSize;
    }

    public int valueToImage( float v )
    {
        return (int)Math.round( ( v0 - v ) * vx ); // Y axis runs down on +
    }
    
}
