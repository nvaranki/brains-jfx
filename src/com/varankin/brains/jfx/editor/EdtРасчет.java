package com.varankin.brains.jfx.editor;

import com.varankin.brains.db.DbАтрибутный;
import com.varankin.brains.db.DbРасчет;
import com.varankin.brains.db.КлючImpl;
import com.varankin.brains.io.xml.XmlBrains;
import com.varankin.brains.jfx.db.*;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.*;


/**
 *
 * @author Николай
 */
class EdtРасчет extends EdtЭлемент<DbРасчет,FxРасчет>
{
    EdtРасчет( FxРасчет элемент )
    {
        super( элемент );
    }
    
    @Override
    public Group загрузить( boolean основной )
    {
        Group group = super.загрузить( основной );

        for( FxСоединение соединение : ЭЛЕМЕНТ.соединения() )
            group.getChildren().add( new EdtСоединение( соединение ).загрузить( false ) );
        for( FxТочка соединение : ЭЛЕМЕНТ.точки() )
            group.getChildren().add( new EdtТочка( соединение ).загрузить( false ) );
        
        return group;
    }
    
    @Override
    public List<DbАтрибутный.Ключ> компоненты()
    {
        List<DbАтрибутный.Ключ> list = new ArrayList<>( super.компоненты() );
        list.add( 0, new КлючImpl( XmlBrains.XML_POINT, XmlBrains.XMLNS_BRAINS, null ) );
        list.add( 1, new КлючImpl( XmlBrains.XML_JOINT, XmlBrains.XMLNS_BRAINS, null ) );
        return list;
    }

}
