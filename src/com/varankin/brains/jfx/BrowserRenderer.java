package com.varankin.brains.jfx;

import com.varankin.brains.appl.AbstracResourceLocator;
import com.varankin.brains.artificial.*;
import java.net.URL;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 *
 *
 * @author &copy; 2012 Николай Варанкин
 */
public class BrowserRenderer 
{
    private final IconLocator локатор;
    
    BrowserRenderer()
    {
        локатор = new IconLocator( "icons16x16/" );
    }
    
    Node getIcon( Object id )
    {
        Image image = локатор.get( id );
        return image != null ? new ImageView( image ) : null;
    }

    static private class IconLocator extends AbstracResourceLocator<Image>
    {
        
        IconLocator( String адрес )
        {
            super( адрес, IconLocator.class.getClassLoader() );
        }

        @Override
        protected Image create( URL url )
        {
            return new Image( url.toExternalForm() );
        }

        @Override
        protected String name( Object id )
        {
            String название;
            if( id instanceof Разветвитель )
                название = "splitter.png";
            else if( id instanceof Приемник )
                название = "receiver2.png";
            else if( id instanceof Источник )
                название = "transmitter2.png";
            else if( id instanceof СенсорноеПоле )
                название = "field2.png";
            else if( id instanceof Сенсор )
                название = "sensor.png";
            else if( id instanceof КогнитивнаяФункция )
                название = "function.png";
            else if( id instanceof ПроцессорРасчета )
                название = "processor2.png";
            //TODO and so on
            else
                название = null;
            return название;
        }
        
    }

}
