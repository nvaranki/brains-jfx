package com.varankin.brains.jfx;

import java.util.regex.Pattern;

/**
 * Фильтр беззнакового целого числа.
 * 
 * @author &copy; 2017 Николай Варанкин
 */
public class SignlessIntegerFilter extends AbstractFilter
{
    private static final Pattern PATTERN = Pattern.compile( "\\d+" );

    public SignlessIntegerFilter() 
    {
        super( PATTERN, "filter.SignlessInteger" );
    }
    
}
