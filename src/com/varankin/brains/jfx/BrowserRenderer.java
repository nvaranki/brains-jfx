package com.varankin.brains.jfx;

import com.varankin.brains.appl.AbstractIconLocator;
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

    static private class IconLocator extends AbstractIconLocator<Image>
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

    }
    
}
