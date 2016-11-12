package com.varankin.brains.jfx.editor;

import com.varankin.brains.db.DbПоле;
import com.varankin.brains.jfx.db.*;
import javafx.collections.ObservableList;
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
        ObservableList<Node> children = group.getChildren();
        children.addAll( загрузить( ЭЛЕМЕНТ.сенсоры() ) );
        children.addAll( загрузить( ЭЛЕМЕНТ.соединения() ) );

        return group;
    }
    
}
