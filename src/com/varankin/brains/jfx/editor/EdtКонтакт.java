package com.varankin.brains.jfx.editor;

import com.varankin.brains.db.Контакт;
import javafx.scene.*;

import static com.varankin.brains.artificial.io.xml.XmlSvg.SVG_ATTR_TRANSFORM;
import static com.varankin.brains.artificial.io.xml.XmlSvg.XMLNS_SVG;
import static com.varankin.brains.db.neo4j.Architect.toStringValue;
import com.varankin.brains.db.Неизвестный;
import static com.varankin.brains.jfx.editor.EdtФрагмент.toTransforms;

/**
 *
 * @author Николай
 */
class EdtКонтакт extends EdtАтрибутныйЭлемент<Контакт>
{
    EdtКонтакт( Контакт элемент )
    {
        super( элемент );
    }
    
    Node загрузить( boolean изменяемый )
    {
        Group group = new Group();
        if( изменяемый ) group.setUserData( ЭЛЕМЕНТ );
        
        String ts = toStringValue( ЭЛЕМЕНТ.атрибут( SVG_ATTR_TRANSFORM, XMLNS_SVG, "" ) );
        group.getTransforms().addAll( toTransforms( ts ) );

        for( Неизвестный н : ЭЛЕМЕНТ.прочее() )
            group.getChildren().add( new EdtНеизвестный( н ).загрузить( изменяемый ) );
        
        return group;
    }
    
}
