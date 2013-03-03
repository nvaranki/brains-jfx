package com.varankin.brains.jfx;

import com.varankin.io.container.Provider;
import com.varankin.io.stream.FileInputStreamProvider;
import com.varankin.util.Текст;
import java.io.File;
import java.io.InputStream;
import java.util.logging.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * Интерактивный генератор поставщика потоков XML файлов.
 * 
 * @author &copy; 2013 Николай Варанкин
 */
class XmlFileSelector implements Provider<Provider<InputStream>>
{
    static private final Logger LOGGER = Logger.getLogger( XmlFileSelector.class.getName() );

    private final Текст словарь;
    private final Stage платформа;
    private final FileChooser селектор;

    XmlFileSelector( JavaFX jfx ) 
    {
        словарь = Текст.ПАКЕТЫ.словарь( XmlFileSelector.class, 
                jfx.контекст.специфика );
        платформа = jfx.платформа;
        селектор = new FileChooser();
        селектор.setInitialDirectory( jfx.getCurrentLocalDirectory() );
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter(
                словарь.текст( "ext.xml" ), "*.xml" );
        селектор.getExtensionFilters().add( filter );
    }

    @Override
    public Provider<InputStream> newInstance() 
    {
        File выбор = селектор.showOpenDialog( платформа );
        if( выбор == null )
            return null;
        else
        {
            String путь = выбор.getAbsolutePath();
            LOGGER.log( Level.FINE, словарь.текст( "path", путь ) );
            File директория = выбор.getParentFile();
            if( директория != null )
                селектор.setInitialDirectory( директория );
            return new FileInputStreamProvider( выбор );
        }
    }
    
}
