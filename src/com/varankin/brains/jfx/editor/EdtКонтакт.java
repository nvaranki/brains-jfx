package com.varankin.brains.jfx.editor;

import com.varankin.brains.db.DbКонтакт;
import javafx.scene.*;

import static com.varankin.brains.artificial.io.xml.XmlSvg.SVG_ATTR_TRANSFORM;
import static com.varankin.brains.artificial.io.xml.XmlSvg.XMLNS_SVG;
import com.varankin.brains.db.Атрибутный;
import static com.varankin.brains.jfx.editor.EdtФрагмент.toTransforms;

/**
 *
 * @author Николай
 */
class EdtКонтакт extends EdtАтрибутныйЭлемент<DbКонтакт>
{
    EdtКонтакт( DbКонтакт элемент )
    {
        super( элемент );
    }
    
    Node загрузить( boolean изменяемый )
    {
        Group group = new Group();
        if( изменяемый ) group.setUserData( ЭЛЕМЕНТ );
        
        String ts = Атрибутный.toStringValue( ЭЛЕМЕНТ.атрибут( SVG_ATTR_TRANSFORM, XMLNS_SVG, "" ) );
        group.getTransforms().addAll( toTransforms( ts ) );

        for( Атрибутный н : ЭЛЕМЕНТ.прочее() )
            group.getChildren().add( new EdtНеизвестный( н ).загрузить( изменяемый ) );
        
        return group;
    }
    
}
