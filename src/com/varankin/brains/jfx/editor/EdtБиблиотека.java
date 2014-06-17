package com.varankin.brains.jfx.editor;

import static com.varankin.brains.artificial.io.xml.XmlSvg.SVG_ATTR_TRANSFORM;
import static com.varankin.brains.artificial.io.xml.XmlSvg.XMLNS_SVG;
import static com.varankin.brains.db.neo4j.Architect.toStringValue;
import com.varankin.brains.db.Библиотека;
import com.varankin.brains.db.Модуль;
import com.varankin.brains.db.Процессор;
import com.varankin.brains.db.Расчет;
import static com.varankin.brains.jfx.editor.EdtФрагмент.toTransforms;
import javafx.scene.*;

/**
 *
 * @author Николай
 */
class EdtБиблиотека extends EdtАтрибутныйЭлемент<Библиотека>
{
    EdtБиблиотека( Библиотека элемент )
    {
        super( элемент );
    }
    
    Node загрузить( boolean изменяемый )
    {
        Group group = new Group();
        group.setUserData( ЭЛЕМЕНТ );
        
        String ts = toStringValue( ЭЛЕМЕНТ.атрибут( SVG_ATTR_TRANSFORM, XMLNS_SVG, "" ) );
        group.getTransforms().addAll( toTransforms( ts ) );

        for( Модуль модуль : ЭЛЕМЕНТ.модули() )
            group.getChildren().add( new EdtМодуль( модуль ).загрузить( изменяемый ) );
        for( Расчет расчет : ЭЛЕМЕНТ.расчеты() )
            group.getChildren().add( new EdtРасчет( расчет ).загрузить( изменяемый ) );
        for( Процессор процессор : ЭЛЕМЕНТ.процессоры() )
            group.getChildren().add( new EdtПроцессор( процессор ).загрузить( изменяемый ) );
        
        return group;
    }
    
}
