package com.varankin.brains.jfx.editor;

import com.varankin.brains.db.DbКонтакт;
import com.varankin.brains.db.DbСоединение;
import javafx.scene.*;

import static com.varankin.brains.artificial.io.xml.XmlSvg.SVG_ATTR_TRANSFORM;
import static com.varankin.brains.artificial.io.xml.XmlSvg.XMLNS_SVG;
import com.varankin.brains.db.Атрибутный;
import static com.varankin.brains.jfx.editor.EdtФрагмент.toTransforms;

/**
 *
 * @author Николай
 */
class EdtСоединение extends EdtАтрибутныйЭлемент<DbСоединение>
{
    EdtСоединение( DbСоединение элемент )
    {
        super( элемент );
    }
    
    Node загрузить( boolean изменяемый )
    {
        Group group = new Group();
        if( изменяемый ) group.setUserData( ЭЛЕМЕНТ );
        
        String ts = Атрибутный.toStringValue( ЭЛЕМЕНТ.атрибут( SVG_ATTR_TRANSFORM, XMLNS_SVG, "" ) );
        group.getTransforms().addAll( toTransforms( ts ) );

        for( DbКонтакт контакт : ЭЛЕМЕНТ.контакты() )
            group.getChildren().add( new EdtКонтакт( контакт ).загрузить( изменяемый ) );
        for( Атрибутный н : ЭЛЕМЕНТ.прочее() )
            group.getChildren().add( new EdtНеизвестный( н ).загрузить( изменяемый ) );
        
        return group;
    }
    
}
