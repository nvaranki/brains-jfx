package com.varankin.brains.jfx.editor;

import com.varankin.brains.db.DbАтрибутный;
import com.varankin.brains.db.DbПроцессор;
import com.varankin.brains.db.КлючImpl;
import com.varankin.brains.jfx.db.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import javafx.collections.ObservableList;
import javafx.scene.*;

import static com.varankin.brains.io.xml.XmlBrains.*;

/**
 *
 * @author Николай
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
        children.addAll( загрузить( ЭЛЕМЕНТ.параметры(), 0, XML_PARAMETER ) );
        children.addAll( загрузить( ЭЛЕМЕНТ.классы(), 0, XML_JAVA ) );

        return group;
    }
    
    @Override
    public List<DbАтрибутный.Ключ> компоненты()
    {
        List<DbАтрибутный.Ключ> list = new ArrayList<>( super.компоненты() );
        list.add( 0, new КлючImpl( XML_PARAMETER, XMLNS_BRAINS, null ) );
        list.add( 1, new КлючImpl( XML_JAVA, XMLNS_BRAINS, null ) );
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
