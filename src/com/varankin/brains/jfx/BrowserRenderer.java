package com.varankin.brains.jfx;

import com.varankin.brains.appl.AbstracResourceLocator;
import com.varankin.brains.db.factory.Базовый;
import com.varankin.brains.db.Элемент;
import java.net.URL;
import java.util.*;
import java.util.logging.*;
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
    private static final Logger LOGGER = Logger.getLogger( BrowserRenderer.class.getName() );
    
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
            Class класс = null;
            if( id instanceof Базовый )
            {
                Элемент шаблон = ((Базовый)id).шаблон();
                if( шаблон != null )
                    класс = шаблон.getClass();
            }
            else if( id != null )
            {
                класс = id.getClass();
            }
            LOGGER.log( Level.FINE, "Icon search for class {0}.", класс );
            if( класс != null )
                for( Map.Entry<Class,String> e : КАТАЛОГ.entrySet() )
                    if( e.getKey().isAssignableFrom( класс ) )
                        return e.getValue();
            return null;
        }
        
    }
    
    private static final Map<Class,String> КАТАЛОГ = new HashMap<>();
    static
    {
        // Иконки базы данных
        КАТАЛОГ.put( com.varankin.brains.db.Проект.class, "new-project.png" );
        КАТАЛОГ.put( com.varankin.brains.db.Модуль.class, "module.png" );
        КАТАЛОГ.put( com.varankin.brains.db.Расчет.class, "function.png" );
        КАТАЛОГ.put( com.varankin.brains.db.Процессор.class, "load1.png" );
        КАТАЛОГ.put( com.varankin.brains.db.Поле.class, "field2.png" );
        КАТАЛОГ.put( com.varankin.brains.db.Сигнал.class, "signal.png" );
        КАТАЛОГ.put( com.varankin.brains.db.Точка.class, "point.png" );
        КАТАЛОГ.put( com.varankin.brains.db.Контакт.class, "pin.png" );
        КАТАЛОГ.put( com.varankin.brains.db.Соединение.class, "connector.png" );
        КАТАЛОГ.put( com.varankin.brains.db.Фрагмент.class, "fragment.png" );
        // Иконки элементов мыслительной структуры        
        КАТАЛОГ.put( com.varankin.brains.artificial.Аргумент.class, "point.png" );
        КАТАЛОГ.put( com.varankin.brains.artificial.Ветвь.class, "point.png" );
        КАТАЛОГ.put( com.varankin.brains.artificial.Значение.class, "point.png" );
        КАТАЛОГ.put( com.varankin.brains.artificial.Источник.class, "transmitter2.png" );
        КАТАЛОГ.put( com.varankin.brains.artificial.Приемник.class, "receiver2.png" );
        КАТАЛОГ.put( com.varankin.brains.artificial.Проект.class, "new-project.png" );
        КАТАЛОГ.put( com.varankin.brains.artificial.ПроцессорРасчета.class, "load1.png" );
        КАТАЛОГ.put( com.varankin.brains.artificial.Разветвитель.class, "pin.png" );
        КАТАЛОГ.put( com.varankin.brains.artificial.Сенсор.class, "sensor.png" );
        КАТАЛОГ.put( com.varankin.brains.artificial.СенсорноеПоле.class, "field2.png" );
        КАТАЛОГ.put( com.varankin.brains.artificial.Сигнал.class, "signal.png" );
    }

}
