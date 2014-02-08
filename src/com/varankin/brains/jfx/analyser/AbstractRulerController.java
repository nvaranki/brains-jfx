package com.varankin.brains.jfx.analyser;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Builder;

/**
 * Абстрактный FXML-контроллер шкалы по некоторой оси.
 * 
 * @author &copy; 2014 Николай Варанкин
 */
public abstract class AbstractRulerController implements Builder<Pane>
{
    private int tickSizeLarge, tickSizeMedium, tickSizeSmall;
    private final Property<Color> tickColorProperty, textColorProperty;
    private final Property<Font> fontProperty;

    protected AbstractRulerController()
    {
        //TODO appl. param.
        tickSizeLarge = 9;
        tickSizeMedium = 6;
        tickSizeSmall = 3;

        tickColorProperty = new SimpleObjectProperty<>( Color.BLACK );
        textColorProperty = new SimpleObjectProperty<>( Color.BLACK );
        fontProperty = new SimpleObjectProperty<>( new Text().getFont() );
    }

    final int getTickSizeLarge()
    {
        return tickSizeLarge;
    }

    final int getTickSizeMedium()
    {
        return tickSizeMedium;
    }

    final int getTickSizeSmall()
    {
        return tickSizeSmall;
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
    
    protected void reset( AbstractRulerController pattern )
    {
        tickSizeLarge = pattern.tickSizeLarge;
        tickSizeMedium = pattern.tickSizeMedium;
        tickSizeSmall = pattern.tickSizeSmall;
        tickColorProperty.setValue( pattern.tickColorProperty.getValue() );
        textColorProperty.setValue( pattern.textColorProperty.getValue() );
        fontProperty.setValue( pattern.fontProperty.getValue() );
    }

    abstract protected void reset( int size );
    abstract protected void generateRuler();
    abstract protected void removeRuler();
    
    protected class SizeChangeListener implements ChangeListener<Number>
    {
        @Override
        public void changed( ObservableValue<? extends Number> observable, 
                            Number oldValue, Number newValue )
        {
            int size = newValue.intValue();
            if( size > 0 )
            {
                reset( size );
                removeRuler();
                generateRuler();
            }
        }
    }

    static double roundToFactor( double value, double factor )
    {
        double exp = Math.floor( Math.log10( value ) );
        double sample = Math.pow( 10d, exp );
        double rounded = Math.round( value / sample / factor ) * sample * factor;
        return rounded;
    }
    
}
