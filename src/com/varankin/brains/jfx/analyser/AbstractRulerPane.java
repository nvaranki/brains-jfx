package com.varankin.brains.jfx.analyser;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableDoubleValue;
import javafx.beans.value.ObservableValue;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;

/**
 * Заготовка линейки. 
 * 
 * @author &copy; 2013 Николай Варанкин
 */
abstract class AbstractRulerPane extends Pane
{
    private int tickSizeLarge, tickSizeMedium, tickSizeSmall;
    private final Property<Color> tickColorProperty, textColorProperty;
    private final Property<Font> fontProperty;

    protected AbstractRulerPane()
    {
        //TODO appl. param.
        tickSizeLarge = 9;
        tickSizeMedium = 6;
        tickSizeSmall = 3;

        fontProperty = new SimpleObjectProperty<>();
        tickColorProperty = new SimpleObjectProperty<>();
        textColorProperty = new SimpleObjectProperty<>();
    }
    
    protected class SizeChangeListener implements ChangeListener<Number>
    {
        @Override
        public void changed( ObservableValue<? extends Number> observable, 
                            Number oldValue, Number newValue )
        {
            if( newValue.intValue() > 0 )
                generateRuler();
        }
    }
    
    abstract protected void generateRuler();

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

    final Property<Font> fontProperty()
    {
        return fontProperty;
    }

    final Property<Color> tickColorProperty()
    {
        return tickColorProperty;
    }

    final Property<Color> textColorProperty()
    {
        return textColorProperty;
    }

    static double roundToFactor( double value, double factor )
    {
        double exp = Math.floor( Math.log10( value ) );
        double sample = Math.pow( 10d, exp );
        double rounded = Math.round( value / sample / factor ) * sample * factor;
        return rounded;
    }

}
