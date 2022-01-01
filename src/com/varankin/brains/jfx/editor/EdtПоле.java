package com.varankin.brains.jfx.editor;

import com.varankin.brains.db.type.DbПоле;
import com.varankin.brains.jfx.db.*;
import java.util.Queue;
import javafx.collections.ObservableList;
import javafx.scene.*;

import static com.varankin.brains.db.xml.XmlBrains.*;

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
        children.addAll( загрузить( ЭЛЕМЕНТ.соединения(), 0, XML_JOINT ) );
        children.addAll( загрузить( ЭЛЕМЕНТ.сенсоры(), 1, XML_SENSOR ) );

        return group;
    }
    
    @Override
    public boolean составить( Queue<int[]> path )
    {
        if( path.isEmpty() ) return false;
        позиция( path.poll() );
        ЭЛЕМЕНТ.графики().add( название( "Новое поле", "../@" + XML_NAME ) );
        return path.isEmpty();
    }

}
