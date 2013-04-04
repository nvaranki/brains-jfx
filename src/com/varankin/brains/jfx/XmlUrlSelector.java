package com.varankin.brains.jfx;

import com.varankin.io.container.Provider;
import com.varankin.io.stream.UrlInputStreamProvider;
import com.varankin.util.Текст;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.*;
import javafx.stage.Stage;

/**
 * Интерактивный генератор поставщика потоков XML файлов.
 * 
 * @author &copy; 2013 Николай Варанкин
 */
class XmlUrlSelector implements Provider<Provider<InputStream>>
{
    static private final Logger LOGGER = Logger.getLogger( XmlUrlSelector.class.getName() );

    private final Текст словарь;
    private final Stage платформа;
//    private final FileChooser селектор;

    XmlUrlSelector( JavaFX jfx ) 
    {
        словарь = Текст.ПАКЕТЫ.словарь( XmlUrlSelector.class, 
                jfx.контекст.специфика );
        платформа = jfx.платформа;
//        селектор = new FileChooser();
//        селектор.setInitialDirectory( jfx.getCurrentLocalDirectory() );
//        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter(
//                словарь.текст( "ext.xml" ), "*.xml" );
//        селектор.getExtensionFilters().add( filter );
    }

    @Override
    public Provider<InputStream> newInstance() 
    {
        URL urL;
        try
        {
            urL = new URL("http://www.varankin.com/test/SelfSummer2x2.xml");
            return new UrlInputStreamProvider( urL);
        } 
        catch( MalformedURLException ex )
        {
            LOGGER.log( Level.SEVERE, null, ex );
            return null;
        }
    }
    
}
