package com.varankin.brains.jfx.editor;

import com.varankin.brains.db.DbАтрибутный;
import com.varankin.brains.db.DbГрафика;
import com.varankin.brains.db.DbЗаметка;
import javafx.scene.*;

import static com.varankin.brains.io.xml.XmlSvg.SVG_ATTR_TRANSFORM;
import static com.varankin.brains.io.xml.XmlSvg.XMLNS_SVG;
import static com.varankin.brains.jfx.editor.EdtФрагмент.toTransforms;

/**
 *
 * @author Николай
 */
class EdtЗаметка extends EdtУзел<DbЗаметка>
{
    EdtЗаметка( DbЗаметка элемент )
    {
        super( элемент );
    }
    
    @Override
    public Group загрузить( boolean основной )
    {
        Group group = super.загрузить( основной );

        for( DbГрафика э : ЭЛЕМЕНТ.графики() )
            group.getChildren().add( new EdtГрафика( э ).загрузить( false ) );
//        for( DbИнструкция н : ЭЛЕМЕНТ.инструкции() )
//            group.getChildren().add( new EdtИнструкция( н ).загрузить( изменяемый ) );
//        for( DbТекстовыйБлок н : ЭЛЕМЕНТ.тексты() )
//            group.getChildren().add( new EdtТекстовыйБлок( н ).загрузить( изменяемый ) );
//        for( DbАтрибутный н : ЭЛЕМЕНТ.прочее() )
//            group.getChildren().add( new EdtНеизвестный( н ).загрузить( изменяемый ) );
        
        return group;
    }
    
}
