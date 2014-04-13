package com.varankin.brains.jfx.analyser;

import com.varankin.property.PropertyMonitor;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javafx.scene.paint.Color;

/**
 * Значение, отображаемое на графике.
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
    final PropertyMonitor монитор;
    final String property;
    final Convertor<Float> convertor;
    final DotPainter painter;
    final int[][] pattern;
    final Color color;
    final String title;

    private PropertyChangeListener наблюдатель;
    
    /**
     * @param pm        источник значений.
     * @param property  название значения как атрибута в источнике значений.
     * @param convertor преобразователь значения в тип {@link Float}.
     * @param painter   менеджер рисования отметок в графической зоне.
     * @param pattern   шаблон отметки на графике.
     * @param color     цвет рисования шаблона отметки на графике.
     * @param title     название значения для отображения на графике.
     */
    Value( PropertyMonitor pm, String property, 
            Value.Convertor<Float> convertor,
            DotPainter painter,
            int[][] pattern, Color color, String title )
    {
        this.монитор = pm;
        this.property = property;
        this.convertor = convertor;
        this.painter = painter;
        this.title = title;
        this.color = color;
        this.pattern = pattern;
    }
    
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
