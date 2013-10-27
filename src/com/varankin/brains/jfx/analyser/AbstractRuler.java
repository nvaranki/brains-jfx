package com.varankin.brains.jfx.analyser;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

/**
 * Заготовка линейки. 
 * 
 * @author &copy; 2013 Николай Варанкин
 */
abstract class AbstractRuler extends Pane
{
    private int tickSizeLarge, tickSizeMedium, tickSizeSmall;
    private Paint tickPaint, valuePaint;

    protected AbstractRuler()
    {
        //TODO appl. param.
        tickSizeLarge = 9;
        tickSizeMedium = 6;
        tickSizeSmall = 3;
        tickPaint = Color.BLACK;
        valuePaint = Color.BLACK;
    }

    final int getTickSizeLarge()
    {
        return tickSizeLarge;
    }

    final void setTickSizeLarge( int size )
    {
        tickSizeLarge = size;
    }

    final int getTickSizeMedium()
    {
        return tickSizeMedium;
    }

    final void setTickSizeMedium( int size )
    {
        tickSizeMedium = size;
    }

    final int getTickSizeSmall()
    {
        return tickSizeSmall;
    }

    final void setTickSizeSmall( int size )
    {
        tickSizeSmall = size;
    }

    final Paint getTickPaint()
    {
        return tickPaint;
    }

    final void setTickPaint( Paint paint )
    {
        tickPaint = paint;
    }

    final Paint getValuePaint()
    {
        return valuePaint;
    }

    final void setValuePaint( Paint paint )
    {
        valuePaint = paint;
    }
    
    static double roundToFactor( double value, double factor )
    {
        double exp = Math.floor( Math.log10( value ) );
        double sample = Math.pow( 10d, exp );
        double rounded = Math.round( value / sample / factor ) * sample * factor;
        return rounded;
    }

}
