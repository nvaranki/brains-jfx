package com.varankin.brains.jfx;

import com.varankin.io.container.Provider;
import java.net.URL;

/**
 * Интерактивный генератор поставщика потоков удаленных XML файлов.
 * 
 * @author &copy; 2016 Николай Варанкин
 */
class UrlSelector implements Provider<URL>
{
    private final UrlChooser селектор;

    UrlSelector() 
    {
        селектор = new UrlChooser( JavaFX.getInstance(), false );
    }

    @Override
    public URL newInstance() 
    {
        URL url = селектор.showOpenDialog( JavaFX.getInstance().платформа );
        if( url == null )
            return null;
        else
        {
            селектор.initialPathProperty().setValue( url.toExternalForm() );
            return url;
        } 
    }
    
}
