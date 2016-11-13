package com.varankin.brains.jfx.editor;

import com.varankin.brains.db.DbСенсор;
import com.varankin.brains.jfx.db.*;
import java.util.Queue;
import javafx.collections.ObservableList;
import javafx.scene.*;

import static com.varankin.brains.io.xml.XmlBrains.*;


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
        children.addAll( загрузить( ЭЛЕМЕНТ.параметры(), 0, XML_PARAMETER ) );
        children.addAll( загрузить( ЭЛЕМЕНТ.классы(), 1, XML_JAVA ) );
        return group;
    }
    
    @Override
    public boolean составить( Queue<int[]> path )
    {
        if( path.isEmpty() ) return false;
        позиция( path.poll() );
        ЭЛЕМЕНТ.графики().add( название( "Новый сенсор", "../@" + XML_NAME ) );
        return path.isEmpty();
    }

}
