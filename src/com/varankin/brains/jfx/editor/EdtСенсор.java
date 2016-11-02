package com.varankin.brains.jfx.editor;

import com.varankin.brains.db.DbАтрибутный;
import com.varankin.brains.db.DbИнструкция;
import com.varankin.brains.db.DbСенсор;
import com.varankin.brains.db.DbТекстовыйБлок;
import com.varankin.brains.io.xml.XmlBrains;
import javafx.scene.*;

import static com.varankin.brains.io.xml.XmlSvg.SVG_ATTR_TRANSFORM;
import static com.varankin.brains.io.xml.XmlSvg.XMLNS_SVG;
import static com.varankin.brains.jfx.editor.EdtФрагмент.toTransforms;

/**
 *
 * @author Николай
 */
class EdtСенсор extends EdtЭлемент<DbСенсор>
{
    EdtСенсор( DbСенсор элемент )
    {
        super( элемент );
    }
    
    @Override
    public Group загрузить( boolean основной )
    {
        Group group = super.загрузить( основной );

        String атрибутName  = ЭЛЕМЕНТ.атрибут( XmlBrains.XML_NAME, "" );
        
        for( DbИнструкция н : ЭЛЕМЕНТ.инструкции() )
            group.getChildren().add( new EdtИнструкция( н ).загрузить( false ) );
        for( DbТекстовыйБлок н : ЭЛЕМЕНТ.тексты() )
            group.getChildren().add( new EdtТекстовыйБлок( н ).загрузить( false ) );
        for( DbАтрибутный н : ЭЛЕМЕНТ.прочее() )
            group.getChildren().add( new EdtНеизвестный( н ).загрузить( false ) );
        
        return group;
    }
    
}
