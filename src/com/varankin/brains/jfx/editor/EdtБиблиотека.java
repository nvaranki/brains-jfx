package com.varankin.brains.jfx.editor;

import com.varankin.brains.db.type.DbБиблиотека;
import com.varankin.brains.jfx.db.*;
import java.util.Queue;
import javafx.collections.ObservableList;
import javafx.scene.*;

import static com.varankin.brains.db.xml.XmlBrains.*;

/**
 *
 * @author Николай
 */
class EdtБиблиотека extends EdtЭлемент<DbБиблиотека,FxБиблиотека>
{
    EdtБиблиотека( FxБиблиотека элемент )
    {
        super( элемент );
    }
    
    @Override
    public Group загрузить( boolean основной )
    {
        Group group = super.загрузить( основной );
        ObservableList<Node> children = group.getChildren();
        children.addAll( загрузить( ЭЛЕМЕНТ.модули(), 0, XML_MODULE ) );
        children.addAll( загрузить( ЭЛЕМЕНТ.расчеты(), 1, XML_COMPUTE ) );
        children.addAll( загрузить( ЭЛЕМЕНТ.ленты(), 2, XML_TIMELINE ) );
        children.addAll( загрузить( ЭЛЕМЕНТ.поля(), 3, XML_FIELD ) );
        children.addAll( загрузить( ЭЛЕМЕНТ.процессоры(), 4, XML_PROCESSOR ) );
        children.addAll( загрузить( ЭЛЕМЕНТ.классы(), 5, XML_JAVA ) );
        return group;
    }

    @Override
    public boolean составить( Queue<int[]> path )
    {
        if( path.isEmpty() ) return false;
        позиция( path.poll() );
        ЭЛЕМЕНТ.графики().add( название( "Новая библиотека", "../@" + XML_NAME ) );
        return path.isEmpty();
    }

}
