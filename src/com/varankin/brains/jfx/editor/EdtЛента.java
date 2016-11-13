package com.varankin.brains.jfx.editor;

import com.varankin.brains.db.DbЛента;
import com.varankin.brains.jfx.db.*;
import java.util.Queue;
import javafx.scene.*;

import static com.varankin.brains.io.xml.XmlBrains.*;

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
        group.getChildren().addAll( загрузить( ЭЛЕМЕНТ.соединения(), 0, XML_JOINT ) );

        return group;
    }
    
    @Override
    public boolean составить( Queue<int[]> path )
    {
        if( path.isEmpty() ) return false;
        позиция( path.poll() );
        ЭЛЕМЕНТ.графики().add( название( "Новая лента", "../@" + XML_NAME ) );
        return path.isEmpty();
    }

}
