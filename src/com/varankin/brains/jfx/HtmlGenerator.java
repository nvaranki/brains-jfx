package com.varankin.brains.jfx;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Николай
 */
public class HtmlGenerator
{
    private static final Logger LOGGER = Logger.getLogger( HtmlGenerator.class.getName() );
    private static Charset cs = Charset.forName( "utf-8" );

    public static String toHtml( String explanation, Throwable throwable )
    {
        StringBuilder body = new StringBuilder();
        body
            .append( "<font color='red'><em>" )
            .append( explanation )
            .append( "</em></font>" );
        
        if( throwable != null ) 
        {
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            PrintStream ps;
            try
            {
                ps = new PrintStream( byteStream, true, cs.name() );
            }
            catch( UnsupportedEncodingException ex )
            {
                LOGGER.log( Level.WARNING, null, ex );
                ps = new PrintStream( byteStream, true );
            }
            throwable.printStackTrace( ps );
            ps.close();
            body
                .append( "<br><font size='-1'><pre>" )
                .append( new String( byteStream.toByteArray(), cs ) )
                .append( "</pre></font>" );
        }
        
        StringBuilder html = new StringBuilder();
        html
            .append( "<html>" )
            .append( "<head><meta http-equiv='Content-Type' content='text/html; charset=" )
            .append( cs.name() )
            .append( "'></head>" )
            .append( "<body>" ).append( body ).append( "</body>" )
            .append( "</html>" );
        
        return html.toString();
    }
    
}
