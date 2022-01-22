package com.varankin.brains.jfx.editor;

import com.varankin.brains.db.type.DbПроцессор;
import com.varankin.brains.db.xml.ЗонныйКлюч;
import com.varankin.brains.db.xml.type.XmlПараметр;
import com.varankin.brains.db.xml.type.XmlКлассJava;
import com.varankin.brains.jfx.db.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import javafx.collections.ObservableList;
import javafx.scene.*;

/**
 *
 * @author &copy; 2022 Николай Варанкин
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
        children.addAll( загрузить( ЭЛЕМЕНТ.параметры(), 0, XmlПараметр.КЛЮЧ_Э_ПАРАМЕТР.НАЗВАНИЕ ) );
        children.addAll( загрузить( ЭЛЕМЕНТ.классы(), 0, XmlКлассJava.КЛЮЧ_Э_JAVA.НАЗВАНИЕ ) );

        return group;
    }
    
    @Override
    public List<ЗонныйКлюч> компоненты()
    {
        List<ЗонныйКлюч> list = new ArrayList<>( super.компоненты() );
        list.add( 0, XmlПараметр.КЛЮЧ_Э_ПАРАМЕТР );
        list.add( 1, XmlКлассJava.КЛЮЧ_Э_JAVA );
        return list;
    }

    @Override
    public boolean составить( Queue<int[]> path )
    {
        if( path.isEmpty() ) return false;
        позиция( path.poll() );
        ЭЛЕМЕНТ.графики().add( название( "Новый процессор", "../@name" ) );
        return path.isEmpty();
    }

}
