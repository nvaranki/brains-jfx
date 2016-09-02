package com.varankin.brains.jfx.editor;

import com.varankin.brains.io.xml.XmlBrains;

import static com.varankin.brains.io.xml.XmlSvg.SVG_ATTR_TRANSFORM;
import static com.varankin.brains.io.xml.XmlSvg.XMLNS_SVG;

import com.varankin.brains.db.Атрибутный;
import com.varankin.brains.db.DbСигнал;
import javafx.scene.*;

import static com.varankin.brains.jfx.editor.EdtФрагмент.toTransforms;

/**
 *
 * @author Николай
 */
class EdtСигнал extends EdtАтрибутныйЭлемент<DbСигнал>
{
    EdtСигнал( DbСигнал элемент )
    {
        super( элемент );
    }
    
    Node загрузить( boolean изменяемый )
    {
        Group group = new Group();
        group.setUserData( ЭЛЕМЕНТ );

        String ts = Атрибутный.toStringValue( ЭЛЕМЕНТ.атрибут( SVG_ATTR_TRANSFORM, XMLNS_SVG, "" ) );
        group.getTransforms().addAll( toTransforms( ts ) );

        String атрибутName  = ЭЛЕМЕНТ.атрибут( XmlBrains.XML_NAME, "" );
        
        for( Атрибутный н : ЭЛЕМЕНТ.прочее() )
            group.getChildren().add( new EdtНеизвестный( н ).загрузить( изменяемый ) );
        
        return group;
    }
    
}
