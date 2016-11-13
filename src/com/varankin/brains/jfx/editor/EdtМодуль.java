package com.varankin.brains.jfx.editor;

import com.varankin.brains.db.DbМодуль;
import com.varankin.brains.jfx.db.*;
import java.util.Queue;
import javafx.collections.ObservableList;
import javafx.scene.*;

import static com.varankin.brains.io.xml.XmlBrains.*;


/**
 *
 * @author Николай
 */
class EdtМодуль extends EdtЭлемент<DbМодуль,FxМодуль>
{
    EdtМодуль( FxМодуль элемент )
    {
        super( элемент );
    }
    
    @Override
    public Group загрузить( boolean основной )
    {
        Group group = super.загрузить( основной );
        ObservableList<Node> children = group.getChildren();
        children.addAll( загрузить( ЭЛЕМЕНТ.фрагменты(), 0, XML_FRAGMENT ) );
        children.addAll( загрузить( ЭЛЕМЕНТ.соединения(), 1, XML_JOINT ) );
        children.addAll( загрузить( ЭЛЕМЕНТ.сигналы(), 2, XML_SIGNAL ) );
        children.addAll( загрузить( ЭЛЕМЕНТ.процессоры(), 3, XML_PROCESSOR ) );
        children.addAll( загрузить( ЭЛЕМЕНТ.библиотеки(), 4, XML_LIBRARY ) );
        return group;
    }
    
    @Override
    public boolean составить( Queue<int[]> path )
    {
        if( path.isEmpty() ) return false;
        позиция( path.poll() );
        ЭЛЕМЕНТ.графики().add( название( "Новый модуль", "../@" + XML_NAME ) );
        return path.isEmpty();
    }

}
