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
class EdtПараметр extends EdtЭлемент<DbПараметр>
{
    EdtПараметр( DbПараметр элемент )
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

        for( DbПараметр н : ЭЛЕМЕНТ.параметры() )
            group.getChildren().add( new EdtПараметр( н ).загрузить( изменяемый ) );
        for( DbКлассJava э : ЭЛЕМЕНТ.классы() )
            group.getChildren().add( new EdtКлассJava( э ).загрузить( изменяемый ) );
        
        return group;
    }
    
}
