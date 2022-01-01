package com.varankin.brains.jfx.editor;

import com.varankin.brains.db.type.DbКлассJava;
import com.varankin.brains.jfx.db.*;
import java.util.Queue;
import javafx.scene.*;

import static com.varankin.brains.db.xml.XmlBrains.*;

/**
 *
 * @author Николай
 */
class EdtКлассJava extends EdtЭлемент<DbКлассJava,FxКлассJava>
{
    EdtКлассJava( FxКлассJava элемент )
    {
        super( элемент );
    }
    
    @Override
    public Group загрузить( boolean основной )
    {
        Group group = super.загрузить( основной );
        return group;
    }
    
    @Override
    public boolean составить( Queue<int[]> path )
    {
        if( path.isEmpty() ) return false;
        позиция( path.poll() );
        ЭЛЕМЕНТ.графики().add( название( "Новый класс", "../@" + XML_NAME ) );
        return path.isEmpty();
    }
    
}
