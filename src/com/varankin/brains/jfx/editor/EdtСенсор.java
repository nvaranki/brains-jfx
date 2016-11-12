package com.varankin.brains.jfx.editor;

import com.varankin.brains.db.DbСенсор;
import com.varankin.brains.io.xml.XmlBrains;
import com.varankin.brains.jfx.db.*;
import javafx.collections.ObservableList;
import javafx.scene.*;


/**
 *
 * @author Николай
 */
class EdtСенсор extends EdtЭлемент<DbСенсор,FxСенсор>
{
    EdtСенсор( FxСенсор элемент )
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

        String атрибутName  = ЭЛЕМЕНТ.getSource().атрибут( XmlBrains.XML_NAME, "" );
        

        return group;
    }
    
}
