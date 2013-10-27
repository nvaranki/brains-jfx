package com.varankin.brains.jfx.analyser;

import java.nio.IntBuffer;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.image.WritablePixelFormat;
import javafx.scene.paint.Color;

/**
 * Графическая зона для рисования отметок. 
 * 
 * @author &copy; 2013 Николай Варанкин
 */
final class DrawArea extends WritableImage implements TimeConvertor, ValueConvertor
{
    private final int blank[][];
    private double t0, tx, v0, vx;
    private int xAdopted, offset;
    private int pixelWidth, pixelHeight, zero;
    private Color timeAxisColor, valueAxisColor, zeroValueAxisColor;
    private final WritablePixelFormat<IntBuffer> pixelFormat;

    DrawArea( int width, int height, float vMin, float vMax, long tMin, long tMax )
    {
        super( width, height );
        pixelFormat = PixelFormat.getIntArgbInstance();
        pixelWidth = width;
        pixelHeight = height;
        blank = new int[][]{ new int[pixelHeight], new int[pixelHeight] };
        timeAxisColor = Color.BLACK;
        valueAxisColor = Color.BLACK;
        zeroValueAxisColor = Color.GRAY;
        xAdopted = width - 10;
        t0 = tMin;
        tx = Double.valueOf( width )/(tMax - tMin);
        v0 = vMax;
        vx = Double.valueOf( height )/(vMin - vMax);
        zero = valueToImage( 0.0F );
        axes( width, height );
    }

    final void axes( int width, int height )
    {
        PixelWriter writer = getPixelWriter();
        for( int i = 0; i < width; i++ ) 
            writer.setColor( i, 0, getTimeAxisColor() ); // timeline
        for( int i = 0; i < height; i++ ) 
            writer.setColor( 0, i, getValueAxisColor() ); // value
        if( 0 < zero && zero < pixelHeight )
        {
            for( int i = 2; i < width; i += 2 ) 
                writer.setColor( i, zero, getZeroValueAxisColor() ); // zero value
        }
        getPixelReader().getPixels( 2, 0, 1, pixelHeight, pixelFormat, blank[0], 0, 1 );
        getPixelReader().getPixels( 1, 0, 1, pixelHeight, pixelFormat, blank[1], 0, 1 );
    }

    void adopt( long now )
    {
        int shift = timeToImage( now ) - xAdopted;
        if( shift > 0 )
        {
            // подправить расчет
            offset -= shift;
            // смещение всей зоны; +-1 для сохранения оси значений
            PixelWriter writer = getPixelWriter();
            writer.setPixels( 1, 0, pixelWidth - shift - 1, pixelHeight, getPixelReader(), shift + 1, 0 );
            // очистить справа от скопированной зоны
            for( int i = pixelWidth - shift; i < pixelWidth; i++ )
                writer.setPixels( i, 0, 1, pixelHeight, pixelFormat, blank[(-offset%2)^(i%2)], 0, 1 );
        }
    }

    @Override
    public int timeToImage( long t )
    {
        return offset + (int)Math.round( ( t - t0 ) * tx );
    }

    @Override
    public int valueToImage( float v )
    {
        return (int)Math.round( ( v - v0 ) * vx );
    }
    
    public Color getTimeAxisColor()
    {
        return timeAxisColor;
    }

    public void setTimeAxisPaint( Color color )
    {
        timeAxisColor = color;
    }

    public Color getValueAxisColor()
    {
        return valueAxisColor;
    }

    public void setValueAxisPaint( Color color )
    {
        valueAxisColor = color;
    }

    public Color getZeroValueAxisColor()
    {
        return zeroValueAxisColor;
    }

    public void setZeroValueAxisPaint( Color color )
    {
        zeroValueAxisColor = color;
    }

}
