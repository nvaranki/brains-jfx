package com.varankin.brains.jfx.analyser;

/**
 * Конвертер значений графика в координату Y {@link  WritableImage}.
 * 
 * @author &copy; 2014 Николай Варанкин
 */
final class ValueConvertor
{
    private volatile double v0, vx;
    private float vMin, vSize;

    ValueConvertor( float low, float high )
    {
        v0 = high;
        vMin = low;
        vSize = high - low;
    }

    public int valueToImage( float v )
    {
        return (int)Math.round( ( v0 - v ) * vx ); // Y axis runs down on +
    }
    
    void reset( int height )
    {
        vx = Double.valueOf( height - 1 )/ vSize;
    }

    float getMin()
    {
        return vMin;
    }

    float getMax()
    {
        return vMin + vSize;
    }

    float getSize()
    {
        return vSize;
    }

    void setMax( Float value )
    {
        vSize -= v0;
        v0 = value;
        vSize += v0;
    }

    void setMin( Float value )
    {
        vSize += vMin;
        vMin = value;
        vSize -= vMin;
    }

}
