package com.varankin.brains.jfx.editor;

import com.varankin.brains.db.DbЛента;
import com.varankin.brains.jfx.db.*;
import javafx.scene.*;

/**
 *
 * @author Николай
 */
class EdtЛента extends EdtЭлемент<DbЛента,FxЛента>
{
    EdtЛента( FxЛента элемент )
    {
        super( элемент );
    }
    
    @Override
    public Group загрузить( boolean основной )
    {
        Group group = super.загрузить( основной );
        group.getChildren().addAll( загрузить( ЭЛЕМЕНТ.соединения() ) );

        return group;
    }
    
}
