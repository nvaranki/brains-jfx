package com.varankin.brains.jfx;

import com.varankin.io.container.Provider;
import com.varankin.io.stream.FileInputStreamProvider;
import com.varankin.util.Текст;
import java.io.File;
import java.io.InputStream;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * Интерактивный генератор поставщика потоков локальных XML файлов.
 * 
 * @author &copy; 2013 Николай Варанкин
 */
class XmlFileSelector implements Provider<Provider<InputStream>>
{
    private final Stage платформа;
    private final FileChooser селектор;

    XmlFileSelector( JavaFX jfx ) 
    {
        платформа = jfx.платформа;
        селектор = new FileChooser();
        селектор.setInitialDirectory( jfx.getCurrentLocalDirectory() );
        Текст словарь = Текст.ПАКЕТЫ.словарь( XmlFileSelector.class, 
                jfx.контекст.специфика );
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
            File директория = выбор.getParentFile();
            if( директория != null )
                селектор.setInitialDirectory( директория );
            return new FileInputStreamProvider( выбор );
        }
    }
    
}
