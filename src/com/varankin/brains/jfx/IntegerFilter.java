package com.varankin.brains.jfx;

import java.util.regex.Pattern;

/**
 * Фильтр целого числа.
 * 
 * @author &copy; 2017 Николай Варанкин
 */
public class IntegerFilter extends AbstractFilter
{
    private static final Pattern PATTERN = Pattern.compile( "[\\+\\-]?\\d+" );

    public IntegerFilter() 
    {
        super( PATTERN, "filter.Integer" );
    }
    
}
