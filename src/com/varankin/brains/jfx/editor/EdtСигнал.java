package com.varankin.brains.jfx.editor;

import com.varankin.brains.artificial.io.xml.XmlBrains;
import static com.varankin.brains.artificial.io.xml.XmlSvg.SVG_ATTR_TRANSFORM;
import static com.varankin.brains.artificial.io.xml.XmlSvg.XMLNS_SVG;
import com.varankin.brains.db.Неизвестный;
import com.varankin.brains.db.Сигнал;
import javafx.scene.*;

import static com.varankin.brains.db.neo4j.Architect.*;
import static com.varankin.brains.jfx.editor.EdtФрагмент.toTransforms;

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
    
    Node загрузить( boolean изменяемый )
    {
        Group group = new Group();
        group.setUserData( ЭЛЕМЕНТ );

        String ts = toStringValue( ЭЛЕМЕНТ.атрибут( SVG_ATTR_TRANSFORM, XMLNS_SVG, "" ) );
        group.getTransforms().addAll( toTransforms( ts ) );

        String атрибутName  = toStringValue( ЭЛЕМЕНТ.атрибут( XmlBrains.XML_NAME, XmlBrains.XMLNS_BRAINS, "" ) );
        
        for( Неизвестный н : ЭЛЕМЕНТ.прочее() )
            group.getChildren().add( new EdtНеизвестный( н ).загрузить( изменяемый ) );
        
        return group;
    }
    
}
