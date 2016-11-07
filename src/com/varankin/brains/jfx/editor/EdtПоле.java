package com.varankin.brains.jfx.editor;

import com.varankin.brains.db.DbПоле;
import com.varankin.brains.jfx.db.*;
import javafx.scene.*;

/**
 *
 * @author Николай
 */
class EdtПоле extends EdtЭлемент<DbПоле,FxПоле>
{
    EdtПоле( FxПоле элемент )
    {
        super( элемент );
    }
    
    @Override
    public Group загрузить( boolean основной )
    {
        Group group = super.загрузить( основной );

        for( FxСенсор сигнал : ЭЛЕМЕНТ.сенсоры() )
            group.getChildren().add( new EdtСенсор( сигнал ).загрузить( false ) );
        for( FxСоединение соединение : ЭЛЕМЕНТ.соединения() )
            group.getChildren().add( new EdtСоединение( соединение ).загрузить( false ) );
        
        return group;
    }
    
}
