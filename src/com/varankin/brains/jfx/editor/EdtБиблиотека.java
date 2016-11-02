package com.varankin.brains.jfx.editor;

import com.varankin.brains.db.DbАтрибутный;
import com.varankin.brains.db.DbБиблиотека;
import com.varankin.brains.db.DbМодуль;
import com.varankin.brains.db.DbПроцессор;
import com.varankin.brains.db.DbРасчет;
import javafx.scene.*;

import static com.varankin.brains.io.xml.XmlSvg.SVG_ATTR_TRANSFORM;
import static com.varankin.brains.io.xml.XmlSvg.XMLNS_SVG;
import static com.varankin.brains.jfx.editor.EdtФрагмент.toTransforms;

/**
 *
 * @author Николай
 */
class EdtБиблиотека extends EdtЭлемент<DbБиблиотека>
{
    EdtБиблиотека( DbБиблиотека элемент )
    {
        super( элемент );
    }
    
    @Override
    public Group загрузить( boolean основной )
    {
        Group group = super.загрузить( основной );

        for( DbМодуль модуль : ЭЛЕМЕНТ.модули() )
            group.getChildren().add( new EdtМодуль( модуль ).загрузить( false ) );
        for( DbРасчет расчет : ЭЛЕМЕНТ.расчеты() )
            group.getChildren().add( new EdtРасчет( расчет ).загрузить( false ) );
        for( DbПроцессор процессор : ЭЛЕМЕНТ.процессоры() )
            group.getChildren().add( new EdtПроцессор( процессор ).загрузить( false ) );
        
        return group;
    }
    
}
