package com.varankin.brains.jfx.analyser.rating;

import com.varankin.characteristic.Именованный;
import com.varankin.brains.artificial.async.Процесс;

/**
 *
 * @author &copy; 2014 Николай Варанкин
 */
public class РанжировщикПроцессСостояние 
        implements Ранжируемый<Процесс.Состояние>, Именованный
{
    @Override
    public float значение( Процесс.Состояние состояние )
    {
        float з;
        if( состояние == null )
        {
            з = Float.NaN;
        }
        else switch( состояние )
        {
            case РАБОТА:  з = 1.0f; break;
            case ПАУЗА:   з = 0.5f; break;
            case ОСТАНОВ: з = 0.0f; break;
            default:      з = Float.NaN;
        }
        return з;
    }

    @Override
    public String название()
    {
        return "Процесс.Состояние";
    }

}
