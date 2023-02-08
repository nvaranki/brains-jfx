package com.varankin.brains.jfx.analyser;

import com.varankin.brains.jfx.JavaFX;
import com.varankin.brains.Контекст;
import com.varankin.brains.Конфигурация;

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
 * @author &copy; 2023 Николай Варанкин
 */
public abstract class AbstractRulerController implements Builder<Pane>
{
    private int tickSizeLarge, tickSizeMedium, tickSizeSmall;
    private final Property<Color> tickColorProperty, textColorProperty;
    private final Property<Font> fontProperty;

    protected AbstractRulerController()
    {
        Контекст контекст = JavaFX.getInstance().контекст;
        tickSizeLarge = Integer.valueOf( контекст.конфигурация.параметр( Конфигурация.Параметры.TICK_LARGE ) );
        tickSizeMedium = Integer.valueOf( контекст.конфигурация.параметр( Конфигурация.Параметры.TICK_MEDIUM ) );
        tickSizeSmall = Integer.valueOf( контекст.конфигурация.параметр( Конфигурация.Параметры.TICK_SMALL ) );

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

    abstract protected void reset();
    
    protected class SizeChangeListener implements ChangeListener<Number>
    {
        @Override
        public void changed( ObservableValue<? extends Number> __, 
                            Number oldValue, Number newValue )
        {
            if( newValue.intValue() > 0 ) reset();
        }
    }

    protected class BoundChangeListener<T> implements ChangeListener<T>
    {
        @Override
        public void changed( ObservableValue<? extends T> __, 
                            T oldValue, T newValue )
        {
            if( newValue != null && !newValue.equals( oldValue ) ) reset();
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
