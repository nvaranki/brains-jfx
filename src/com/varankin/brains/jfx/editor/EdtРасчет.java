package com.varankin.brains.jfx.editor;

import com.varankin.brains.db.DbАтрибутный;
import com.varankin.brains.db.DbИнструкция;
import com.varankin.brains.db.DbРасчет;
import com.varankin.brains.db.DbСоединение;
import com.varankin.brains.db.DbТекстовыйБлок;
import com.varankin.brains.db.DbТочка;
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
    public Group загрузить( boolean изменяемый )
    {
        Group group = super.загрузить( изменяемый );
        if( изменяемый ) group.setUserData( ЭЛЕМЕНТ );
        
        String ts = DbАтрибутный.toStringValue( ЭЛЕМЕНТ.атрибут( SVG_ATTR_TRANSFORM, XMLNS_SVG, "" ) );
        group.getTransforms().addAll( toTransforms( ts ) );

        for( DbСоединение соединение : ЭЛЕМЕНТ.соединения() )
            group.getChildren().add( new EdtСоединение( соединение ).загрузить( изменяемый ) );
        for( DbТочка соединение : ЭЛЕМЕНТ.точки() )
            group.getChildren().add( new EdtТочка( соединение ).загрузить( изменяемый ) );
        for( DbИнструкция н : ЭЛЕМЕНТ.инструкции() )
            group.getChildren().add( new EdtИнструкция( н ).загрузить( изменяемый ) );
        for( DbТекстовыйБлок н : ЭЛЕМЕНТ.тексты() )
            group.getChildren().add( new EdtТекстовыйБлок( н ).загрузить( изменяемый ) );
        for( DbАтрибутный н : ЭЛЕМЕНТ.прочее() )
            group.getChildren().add( new EdtНеизвестный( н ).загрузить( изменяемый ) );
        
        
        return group;
    }
    
}
