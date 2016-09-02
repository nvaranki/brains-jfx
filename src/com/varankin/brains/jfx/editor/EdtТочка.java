package com.varankin.brains.jfx.editor;

import static com.varankin.brains.io.xml.XmlSvg.SVG_ATTR_TRANSFORM;
import static com.varankin.brains.io.xml.XmlSvg.XMLNS_SVG;
import com.varankin.brains.db.Атрибутный;
import com.varankin.brains.db.DbТочка;
import static com.varankin.brains.jfx.editor.EdtФрагмент.toTransforms;
import javafx.scene.*;

/**
 *
 * @author Николай
 */
class EdtТочка extends EdtАтрибутныйЭлемент<DbТочка>
{
    EdtТочка( DbТочка элемент )
    {
        super( элемент );
    }
    
    Node загрузить( boolean изменяемый )
    {
        Group group = new Group();
        if( изменяемый ) group.setUserData( ЭЛЕМЕНТ );
        
        String ts = Атрибутный.toStringValue( ЭЛЕМЕНТ.атрибут( SVG_ATTR_TRANSFORM, XMLNS_SVG, "" ) );
        group.getTransforms().addAll( toTransforms( ts ) );

        for( DbТочка соединение : ЭЛЕМЕНТ.точки() )
            group.getChildren().add( new EdtТочка( соединение ).загрузить( изменяемый ) );
        for( Атрибутный н : ЭЛЕМЕНТ.прочее() )
            group.getChildren().add( new EdtНеизвестный( н ).загрузить( изменяемый ) );
        
        
        return group;
    }
    
}
