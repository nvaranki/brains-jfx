package com.varankin.brains.jfx.selector;

import com.varankin.brains.jfx.JavaFX;
import com.varankin.io.container.Provider;
import java.net.URL;

/**
 * Интерактивный генератор поставщика потоков удаленных XML файлов.
 * 
 * @author &copy; 2016 Николай Варанкин
 */
public class UrlSelector implements Provider<URL>
{
    private final UrlChooser селектор;

    public UrlSelector() 
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

    public void setTitle( String text )
    {
        селектор.setTitle( text );
    }
    
}
