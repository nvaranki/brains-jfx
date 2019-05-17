package com.varankin.brains.jfx.browser;

import com.varankin.brains.appl.AbstractIconLocator;
import com.varankin.brains.artificial.async.Процесс;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javafx.scene.Node;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.ColorInput;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

/**
 *
 *
 * @author &copy; 2019 Николай Варанкин
 */
class BrowserRenderer 
{
    private static final Map<Процесс.Состояние,Color> ФОН;
    static
    {
        ФОН = new HashMap<>();
        ФОН.put( Процесс.Состояние.ОСТАНОВ, Color.LIGHTSALMON );
        ФОН.put( Процесс.Состояние.РАБОТА, Color.LIGHTGREEN );
        ФОН.put( Процесс.Состояние.ПАУЗА, Color.YELLOW );
    }
    
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

    void setBgColor( Node graphic, Процесс.Состояние состояние )
    {
        if( graphic instanceof ImageView )
        {
            ImageView view = (ImageView)graphic;
            Blend blend = null;
            Color фон = ФОН.get( состояние );
            if( фон != null )
            {
                Image image = view.getImage();
                int width  = (int)Math.round( image.getWidth() );
                int height = (int)Math.round( image.getHeight() );
                ColorInput effect = new ColorInput( 0, 0, width, height, фон );
                blend = new Blend();
                blend.setMode( BlendMode.COLOR_BURN );
                blend.setTopInput( effect );
            }
            view.setEffect( blend );
        }
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
