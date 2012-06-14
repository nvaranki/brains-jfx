package com.varankin.brains.jfx;

import com.varankin.brains.db.Архив;
import java.util.*;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;

/**
 *
 *
 * @author &copy; 2011 Николай
 */
class CatalogView extends ListView<String>
{
    private final Архив склад;

    CatalogView( ApplicationView.Context context )
    {
        getSelectionModel().setSelectionMode( SelectionMode.SINGLE );
        склад = context.jfx.контекст.склад;
        populate( ); //TODO sep. thread
    }
    
    private void populate()
    {
        List<String> модули = new ArrayList<>( склад.модули() );
        Collections.sort( модули );
        getItems().addAll( модули );
        
    }

}
