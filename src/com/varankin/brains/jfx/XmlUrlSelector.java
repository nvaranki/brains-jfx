package com.varankin.brains.jfx;

import com.varankin.io.container.Provider;
import com.varankin.io.stream.UrlInputStreamProvider;
import java.io.InputStream;
import java.net.URL;
import javafx.stage.Stage;

/**
 * Интерактивный генератор поставщика потоков удаленных XML файлов.
 * 
 * @author &copy; 2013 Николай Варанкин
 */
class XmlUrlSelector implements Provider<Provider<InputStream>>
{
    private final Stage платформа;
    private final UrlChooser селектор;

    XmlUrlSelector( JavaFX jfx ) 
    {
        платформа = jfx.платформа;
        селектор = new UrlChooser( jfx, false );
    }

    @Override
    public Provider<InputStream> newInstance() 
    {
        URL url = селектор.showOpenDialog( платформа );
        if( url == null )
            return null;
        else
        {
            селектор.initialPathProperty().setValue( url.toExternalForm() );
            return new UrlInputStreamProvider( url );
        } 
    }
    
}
