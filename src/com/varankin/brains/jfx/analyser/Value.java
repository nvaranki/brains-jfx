package com.varankin.brains.jfx.analyser;

import com.varankin.brains.appl.Именованный;
import com.varankin.brains.artificial.algebra.Значимый;
import com.varankin.brains.artificial.Измеримый;
import com.varankin.brains.artificial.Ранжировщик;
import com.varankin.property.PropertyMonitor;
import com.varankin.util.LoggerX;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.logging.Level;
import javafx.scene.paint.Color;

/**
 * Значение, отображаемое на графике.
 * 
 * @author &copy; 2014 Николай Варанкин
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
    static private final LoggerX LOGGER = LoggerX.getLogger( Value.class );
    
    final PropertyMonitor монитор;
    final String property;
    final Ранжировщик convertor;
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
            Ранжировщик convertor,
            DotPainter painter,
            int[][] pattern, Color color, String title )
    {
        this.монитор = pm;
        this.property = property;
        this.convertor = convertor != null ? convertor : new РанжировщикImpl();
        this.painter = painter;
        this.title = title;
        this.color = color;
        this.pattern = pattern;
    }
    
    void startMonitoring()
    {
        наблюдатель = this::onPropertyChange;
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
    
    private void onPropertyChange( PropertyChangeEvent evt )
    {
        if( property.equals( evt.getPropertyName() ) )
        {
            if( convertor instanceof РанжировщикImpl )
                ((РанжировщикImpl)convertor).setOldValue( evt.getOldValue() );

            Dot dot = new Dot( convertor.значение( evt.getNewValue() ), System.currentTimeMillis() );
            
            if( !painter.offer( dot ) )
                LOGGER.log( Level.FINEST, "Painter of \"{0}\" rejected a dot.", title );
        }
    }
    
    static final class РанжировщикImpl implements Ранжировщик, Именованный
    {
        private Object old;
        
        @Override
        public float значение( Object value )
        {
            float measure;
            if( value instanceof Number )
            {
                measure = ((Number)value).floatValue();
            }
            else if( value instanceof Измеримый )
            {
                measure = ((Измеримый)value).значение().floatValue();
            }
            else if( value instanceof Boolean )
            {
                measure = (Boolean)value ? 1f : 0f;
            }
            else if( value == null )
            {
                measure = Значимый.НЕИЗВЕСТНЫЙ.значение();
            }
            else
            {
                measure = value.equals( old ) ? 0f : 1f;
            }
            return measure;
        }

        private void setOldValue( Object value )
        {
            old = value;
        }

        @Override
        public String название()
        {
            return "Стандартный ранжировщик";
        }

    }

}
