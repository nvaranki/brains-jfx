package com.varankin.brains.jfx.editor;

import com.varankin.brains.db.*;
import javafx.scene.*;

import static com.varankin.brains.io.xml.XmlSvg.SVG_ATTR_TRANSFORM;
import static com.varankin.brains.io.xml.XmlSvg.XMLNS_SVG;
import static com.varankin.brains.jfx.editor.EdtФрагмент.toTransforms;

/**
 *
 * @author Николай
 */
abstract class EdtЭлемент<T extends DbЭлемент> extends EdtУзел<T>
{
    EdtЭлемент( T элемент )
    {
        super( элемент );
    }
    
    @Override
    public Group загрузить( boolean изменяемый )
    {
        Group group = super.загрузить( изменяемый );

        for( DbЗаметка э : ЭЛЕМЕНТ.заметки() )
            group.getChildren().add( new EdtЗаметка( э ).загрузить( изменяемый ) );
        for( DbГрафика э : ЭЛЕМЕНТ.графики() )
            group.getChildren().add( new EdtГрафика( э ).загрузить( изменяемый ) );
        
        return group;
    }
    
}
