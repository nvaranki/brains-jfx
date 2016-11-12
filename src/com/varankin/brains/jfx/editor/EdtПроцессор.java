package com.varankin.brains.jfx.editor;

import com.varankin.brains.db.DbПроцессор;
import com.varankin.brains.jfx.db.*;
import javafx.collections.ObservableList;
import javafx.scene.*;

/**
 *
 * @author Николай
 */
class EdtПроцессор extends EdtЭлемент<DbПроцессор,FxПроцессор>
{
    EdtПроцессор( FxПроцессор элемент )
    {
        super( элемент );
    }
    
    @Override
    public Group загрузить( boolean основной )
    {
        Group group = super.загрузить( основной );
        ObservableList<Node> children = group.getChildren();
        children.addAll( загрузить( ЭЛЕМЕНТ.параметры() ) );
        children.addAll( загрузить( ЭЛЕМЕНТ.классы() ) );

        return group;
    }
    
}
