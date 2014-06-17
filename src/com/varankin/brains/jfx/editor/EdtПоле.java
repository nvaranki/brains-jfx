package com.varankin.brains.jfx.editor;

import static com.varankin.brains.artificial.io.xml.XmlSvg.SVG_ATTR_TRANSFORM;
import static com.varankin.brains.artificial.io.xml.XmlSvg.XMLNS_SVG;
import static com.varankin.brains.db.neo4j.Architect.toStringValue;
import com.varankin.brains.db.Неизвестный;
import com.varankin.brains.db.Поле;
import com.varankin.brains.db.Сигнал;
import com.varankin.brains.db.Соединение;
import static com.varankin.brains.jfx.editor.EdtФрагмент.toTransforms;
import javafx.scene.*;

/**
 *
 * @author Николай
 */
class EdtПоле extends EdtАтрибутныйЭлемент<Поле>
{
    EdtПоле( Поле элемент )
    {
        super( элемент );
    }
    
    Node загрузить( boolean изменяемый )
    {
        Group group = new Group();
        if( изменяемый ) group.setUserData( ЭЛЕМЕНТ );
        
        String ts = toStringValue( ЭЛЕМЕНТ.атрибут( SVG_ATTR_TRANSFORM, XMLNS_SVG, "" ) );
        group.getTransforms().addAll( toTransforms( ts ) );

        for( Сигнал сигнал : ЭЛЕМЕНТ.сигналы() )
            group.getChildren().add( new EdtСигнал( сигнал ).загрузить( изменяемый ) );
        for( Соединение соединение : ЭЛЕМЕНТ.соединения() )
            group.getChildren().add( new EdtСоединение( соединение ).загрузить( изменяемый ) );
        for( Неизвестный н : ЭЛЕМЕНТ.прочее() )
            group.getChildren().add( new EdtНеизвестный( н ).загрузить( изменяемый ) );
        
        
        return group;
    }
    
}
