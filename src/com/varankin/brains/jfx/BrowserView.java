package com.varankin.brains.jfx;

import com.varankin.brains.appl.Элемент;
import java.util.*;
import javafx.scene.control.*;

/**
 * Экранная форма просмотра структуры объектов.
 *
 * @author &copy; 2012 Николай Варанкин
 */
public class BrowserView extends TreeView<Элемент> 
{
    BrowserView( ApplicationView.Context контекст )
    {
        Map<Locale.Category,Locale> специфика = контекст.jfx.контекст.специфика;
        BrowserNodeBuilder строитель = new BrowserNodeBuilder( (TreeView<Элемент>)this, специфика );
        контекст.jfx.контекст.мыслитель.сервис().addBrainsListener( строитель );

        setCellFactory( строитель.фабрика() );
        setShowRoot( true );
        setEditable( false );
        int w = Integer.valueOf( 
                контекст.jfx.контекст.параметр( "frame.browser.width.min", "150" ) );
        setMinWidth( w );
    }
        
}
