package com.varankin.brains.jfx;

import com.varankin.util.LoggerX;
import java.util.function.UnaryOperator;
import java.util.logging.Level;
import java.util.regex.Pattern;
import javafx.scene.control.TextFormatter;

/**
 * Абстрактный фильтр текста.
 * 
 * @author &copy; 2017 Николай Варанкин
 */
public abstract class AbstractFilter 
        implements UnaryOperator<TextFormatter.Change> 
{
    private static final LoggerX LOGGER = LoggerX.getLogger( AbstractFilter.class );
    
    private final Pattern pattern;
    private final String msgkey;

    public AbstractFilter( Pattern pattern, String msgkey ) 
    {
        this.pattern = pattern;
        this.msgkey = msgkey;
    }
    
    @Override
    public final TextFormatter.Change apply( TextFormatter.Change c ) 
    {
        String nt = c.getControlNewText();
        if( !( nt.isEmpty() || pattern.matcher( nt ).matches() ) )
        {
            LOGGER.log( Level.WARNING, msgkey, c.getText() );
            c.setText( "" );
        }
        return c;
    }
    
}
