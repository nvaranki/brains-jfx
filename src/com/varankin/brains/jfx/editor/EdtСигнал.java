package com.varankin.brains.jfx.editor;

import com.varankin.brains.artificial.io.xml.XmlBrains;
import com.varankin.brains.db.Неизвестный;
import com.varankin.brains.db.Сигнал;
import javafx.scene.*;

import static com.varankin.brains.db.neo4j.Architect.*;

/**
 *
 * @author Николай
 */
class EdtСигнал extends EdtАтрибутныйЭлемент<Сигнал>
{
    EdtСигнал( Сигнал элемент )
    {
        super( элемент );
    }
    
    Node загрузить()
    {
        Group group = new Group();
        group.setUserData( ЭЛЕМЕНТ );

        String атрибутName  = toStringValue( ЭЛЕМЕНТ.атрибут( XmlBrains.XML_NAME, XmlBrains.XMLNS_BRAINS, "" ) );
        
        for( Неизвестный н : ЭЛЕМЕНТ.прочее() )
            group.getChildren().add( new EdtНеизвестный( н ).загрузить() );
        
        return group;
    }
    
}
