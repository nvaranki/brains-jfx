package com.varankin.brains.jfx.analyser;

import com.varankin.brains.artificial.rating.Ранжируемый;
import com.varankin.brains.artificial.rating.СтандартныйРанжировщик;
import com.varankin.characteristic.Изменение;
import com.varankin.characteristic.НаблюдаемоеСвойство;
import com.varankin.characteristic.Наблюдатель;
import com.varankin.property.PropertyMonitor;
import com.varankin.util.LoggerX;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
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
    
    final String property;
    final Ранжируемый convertor;
    final DotPainter painter;
    final int[][] pattern;
    final Color color;
    final String title;

    private PropertyChangeListener наблюдатель_p;
    private Наблюдатель наблюдатель;
    private final Collection<PropertyChangeListener> наблюдатели_p;
    private final Collection наблюдатели;
    
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
            Ранжируемый convertor,
            DotPainter painter,
            int[][] pattern, Color color, String title )
    {
        this.наблюдатели_p = pm.listeners();
        this.наблюдатели = null;
        this.property = property;
        this.convertor = convertor;//TODO != null ? convertor : new СтандартныйРанжировщик();
        this.painter = painter;
        this.title = title;
        this.color = color;
        this.pattern = pattern;
    }
    
    /**
     * @param property  название значения как атрибута в источнике значений.
     * @param convertor преобразователь значения в тип {@link Float}.
     * @param painter   менеджер рисования отметок в графической зоне.
     * @param pattern   шаблон отметки на графике.
     * @param color     цвет рисования шаблона отметки на графике.
     * @param title     название значения для отображения на графике.
     */
    Value( НаблюдаемоеСвойство property, 
            Ранжируемый convertor,
            DotPainter painter,
            int[][] pattern, Color color, String title )
    {
        this.наблюдатели_p = null;
        this.наблюдатели = property.наблюдатели();
        this.property = null;
        this.convertor = convertor;//TODO != null ? convertor : new СтандартныйРанжировщик();
        this.painter = painter;
        this.title = title;
        this.color = color;
        this.pattern = pattern;
    }
    
    void startMonitoring()
    {
        if( наблюдатели_p != null )
        {
            наблюдатели_p.add( наблюдатель_p = this::onPropertyChange );
        }
        else if( наблюдатели != null )
        {
            наблюдатели.add( наблюдатель = this::onPropertyChange );
        }
    }
    
    void stopMonitoring()
    {
        if( наблюдатели_p != null )
        {
            наблюдатели_p.remove( наблюдатель_p );
            наблюдатель_p = null;
        }
        else if( наблюдатели != null )
        {
            наблюдатели.remove( наблюдатель );
            наблюдатель = null;
        }
    }
    
    private void onPropertyChange( Изменение изменение )
    {
        onPropertyChange( изменение.ПРЕЖНЕЕ, изменение.АКТУАЛЬНОЕ );
    }
    
    private void onPropertyChange( PropertyChangeEvent evt )
    {
        if( property.equals( evt.getPropertyName() ) )
            onPropertyChange( evt.getOldValue(), evt.getNewValue() );
    }
    
    private void onPropertyChange( Object прежнее, Object актуальное )
    {
        if( convertor instanceof СтандартныйРанжировщик )
            ((СтандартныйРанжировщик)convertor).setOldValue( прежнее );

        Dot dot = new Dot( convertor.значение( актуальное ), System.currentTimeMillis() );

        if( !painter.offer( dot ) )
            LOGGER.log( Level.FINEST, "Painter of \"{0}\" rejected a dot.", title );
    }
    
}
