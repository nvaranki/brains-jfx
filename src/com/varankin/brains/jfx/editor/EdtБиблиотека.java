package com.varankin.brains.jfx.editor;

import static com.varankin.brains.io.xml.XmlSvg.SVG_ATTR_TRANSFORM;
import static com.varankin.brains.io.xml.XmlSvg.XMLNS_SVG;
import com.varankin.brains.db.Атрибутный;
import com.varankin.brains.db.DbБиблиотека;
import com.varankin.brains.db.DbМодуль;
import com.varankin.brains.db.DbПроцессор;
import com.varankin.brains.db.DbРасчет;
import static com.varankin.brains.jfx.editor.EdtФрагмент.toTransforms;
import javafx.scene.*;

/**
 *
 * @author Николай
 */
class EdtБиблиотека extends EdtАтрибутныйЭлемент<DbБиблиотека>
{
    EdtБиблиотека( DbБиблиотека элемент )
    {
        super( элемент );
    }
    
    Node загрузить( boolean изменяемый )
    {
        Group group = new Group();
        group.setUserData( ЭЛЕМЕНТ );
        
        String ts = Атрибутный.toStringValue( ЭЛЕМЕНТ.атрибут( SVG_ATTR_TRANSFORM, XMLNS_SVG, "" ) );
        group.getTransforms().addAll( toTransforms( ts ) );

        for( DbМодуль модуль : ЭЛЕМЕНТ.модули() )
            group.getChildren().add( new EdtМодуль( модуль ).загрузить( изменяемый ) );
        for( DbРасчет расчет : ЭЛЕМЕНТ.расчеты() )
            group.getChildren().add( new EdtРасчет( расчет ).загрузить( изменяемый ) );
        for( DbПроцессор процессор : ЭЛЕМЕНТ.процессоры() )
            group.getChildren().add( new EdtПроцессор( процессор ).загрузить( изменяемый ) );
        
        return group;
    }
    
}
