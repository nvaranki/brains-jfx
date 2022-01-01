package com.varankin.brains.jfx.editor;

import com.varankin.brains.db.type.DbСоединение;
import com.varankin.brains.jfx.db.*;
import java.util.Queue;
import javafx.scene.*;

import static com.varankin.brains.db.xml.XmlBrains.*;

/**
 *
 * @author Николай
 */
class EdtСоединение extends EdtЭлемент<DbСоединение,FxСоединение>
{
    EdtСоединение( FxСоединение элемент )
    {
        super( элемент );
    }
    
    @Override
    public Group загрузить( boolean основной )
    {
        Group group = super.загрузить( основной );
        group.getChildren().addAll( загрузить( ЭЛЕМЕНТ.контакты(), 0, XML_PIN ) );

        return group;
    }
    
    @Override
    public boolean составить( Queue<int[]> path )
    {
        if( path.isEmpty() ) return false;
        int[] a = path.poll();
        позиция( a );
        ЭЛЕМЕНТ.графики().add( название( "Новое соединение", "../@" + XML_NAME, 
            path.isEmpty() ? new int[]{0,-15} : отн( path.poll(), a ) ) );
        return path.isEmpty();
    }

}
