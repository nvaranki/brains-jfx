package com.varankin.brains.jfx;

import java.util.regex.Pattern;

/**
 * Фильтр вещественного числа.
 * 
 * @author &copy; 2017 Николай Варанкин
 */
public class FloatFilter extends AbstractFilter
{
    private static final Pattern PATTERN = Pattern.compile( 
            "[\\+\\-]?((\\d+([\\.\\,]\\d*)?)|(\\d*[\\.\\,]\\d+))" );

    public FloatFilter() 
    {
        super( PATTERN, "filter.Float" );
    }
    
}
