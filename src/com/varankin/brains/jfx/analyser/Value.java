package com.varankin.brains.jfx.analyser;

import com.varankin.property.PropertyMonitor;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javafx.scene.paint.Color;

/**
 *
 * @author Николай
 */
    /**
     * Добавляет значение, отображаемое на графике.
     * 
     * @param pm        источник значений.
     * @param property  название значения как атрибута в источнике значений.
     * @param convertor преобразователь значения в тип {@link Float}.
     * @param pattern   шаблон отметки на графике.
     * @param color     цвет рисования шаблона отметки на графике.
     * @param title     название значения для отображения на графике.
     */
class Value
{
    PropertyMonitor монитор;
    String property;
    Convertor<Float> convertor;
    int[][] pattern;
    Color color;
    String title;
    DotPainter painter;

    private PropertyChangeListener наблюдатель;
    
    void startMonitoring()
    {
        наблюдатель = new PropertyChangeListener() 
        {
            @Override
            public void propertyChange( PropertyChangeEvent evt )
            {
                if( property.equals( evt.getPropertyName() ) )
                {
                    Object value = evt.getNewValue();
                    if( value instanceof Float )
                    {
                        Dot dot = convertor.toDot( (Float)value, System.currentTimeMillis() );
                        boolean offered = dot != null && painter.offer( dot );
                    }
                    //TODO LOGGER.log( Level.OFF, property );
                }
            }
        };
        монитор.наблюдатели().add( наблюдатель );
    }
    
    void stopMonitoring()
    {
        if( монитор != null )
        {
            монитор.наблюдатели().remove( наблюдатель );
            наблюдатель = null;
        }
    }

    interface Convertor<T>
    {
        Dot toDot( T value, long timestamp );
    }

}
