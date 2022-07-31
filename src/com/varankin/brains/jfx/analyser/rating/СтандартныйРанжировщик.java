package com.varankin.brains.jfx.analyser.rating;

import com.varankin.brains.artificial.algebra.Константы;
import com.varankin.characteristic.Именованный;
import com.varankin.util.LoggerX;

/**
 * Ранжировщик множества известных объектов.
 * 
 * @author &copy; 2022 Николай Варанкин
 */
public final class СтандартныйРанжировщик implements Ранжируемый, Именованный
{
    private static final LoggerX LOGGER = LoggerX.getLogger( СтандартныйРанжировщик.class );

    private Object old;

    @Override
    public float значение( Object value )
    {
        float measure;
        if( value instanceof Number )
        {
            measure = ( (Number)value ).floatValue();
        }
        else if( value instanceof Измеримый )
        {
            measure = ( (Измеримый)value ).значение().floatValue();
        }
        else if( value instanceof Boolean )
        {
            measure = (Boolean)value ? 1f : 0f;
        }
        else if( value == null )
        {
            measure = Константы.НЕИЗВЕСТНО;
        }
        else
        {
            measure = value.equals( old ) ? 0f : 1f;
        }
        return measure;
    }

    public void setOldValue( Object value )
    {
        old = value;
    }

    @Override
    public String название()
    {
        return LOGGER.text( "rating.standard.name" );
    }
    
}
