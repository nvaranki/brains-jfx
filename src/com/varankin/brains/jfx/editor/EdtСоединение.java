package com.varankin.brains.jfx.editor;

import com.varankin.brains.db.DbАтрибутный;
import com.varankin.brains.db.DbСоединение;
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

        for( FxКонтакт контакт : ЭЛЕМЕНТ.контакты() )
            group.getChildren().add( new EdtКонтакт( контакт ).загрузить( false ) );
        
        return group;
    }
    
    @Override
    public List<DbАтрибутный.Ключ> компоненты()
    {
        List<DbАтрибутный.Ключ> list = new ArrayList<>( super.компоненты() );
        list.add( 0, new КлючImpl( XmlBrains.XML_PIN, XmlBrains.XMLNS_BRAINS, null ) );
        return list;
    }

}
