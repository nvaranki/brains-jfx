package com.varankin.brains.jfx.editor;

import com.varankin.brains.db.DbЗаметка;
import com.varankin.brains.jfx.db.*;
import javafx.scene.*;


/**
 *
 * @author Николай
 */
class EdtЗаметка extends EdtУзел<DbЗаметка,FxЗаметка>
{
    EdtЗаметка( FxЗаметка элемент )
    {
        super( элемент );
    }
    
    @Override
    public Group загрузить( boolean основной )
    {
        Group group = super.загрузить( основной );
        group.getChildren().addAll( загрузить( ЭЛЕМЕНТ.графики() ) );

        return group;
    }
    
}
