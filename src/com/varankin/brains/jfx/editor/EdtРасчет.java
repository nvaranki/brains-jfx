package com.varankin.brains.jfx.editor;

import com.varankin.brains.db.DbАтрибутный;
import com.varankin.brains.db.DbИнструкция;
import com.varankin.brains.db.DbРасчет;
import com.varankin.brains.db.DbСоединение;
import com.varankin.brains.db.DbТекстовыйБлок;
import com.varankin.brains.db.DbТочка;
import com.varankin.brains.db.КлючImpl;
import com.varankin.brains.io.xml.XmlBrains;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.*;

import static com.varankin.brains.io.xml.XmlSvg.SVG_ATTR_TRANSFORM;
import static com.varankin.brains.io.xml.XmlSvg.XMLNS_SVG;
import static com.varankin.brains.jfx.editor.EdtФрагмент.toTransforms;

/**
 *
 * @author Николай
 */
class EdtРасчет extends EdtЭлемент<DbРасчет>
{
    EdtРасчет( DbРасчет элемент )
    {
        super( элемент );
    }
    
    @Override
    public Group загрузить( boolean основной )
    {
        Group group = super.загрузить( основной );

        for( DbСоединение соединение : ЭЛЕМЕНТ.соединения() )
            group.getChildren().add( new EdtСоединение( соединение ).загрузить( false ) );
        for( DbТочка соединение : ЭЛЕМЕНТ.точки() )
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
