package com.varankin.brains.jfx.editor;

import com.varankin.brains.db.DbКонтакт;
import com.varankin.brains.db.DbСоединение;
import javafx.scene.*;

import static com.varankin.brains.io.xml.XmlSvg.SVG_ATTR_TRANSFORM;
import static com.varankin.brains.io.xml.XmlSvg.XMLNS_SVG;


import static com.varankin.brains.jfx.editor.EdtФрагмент.toTransforms;

import com.varankin.brains.db.DbТекстовыйБлок;
import com.varankin.brains.db.DbИнструкция;
import com.varankin.brains.db.DbАтрибутный;

/**
 *
 * @author Николай
 */
class EdtСоединение extends EdtЭлемент<DbСоединение>
{
    EdtСоединение( DbСоединение элемент )
    {
        super( элемент );
    }
    
    @Override
    public Group загрузить( boolean изменяемый )
    {
        Group group = super.загрузить( изменяемый );

        for( DbКонтакт контакт : ЭЛЕМЕНТ.контакты() )
            group.getChildren().add( new EdtКонтакт( контакт ).загрузить( изменяемый ) );
        
        return group;
    }
    
}
