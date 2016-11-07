package com.varankin.brains.jfx.editor;

import com.varankin.brains.db.DbБиблиотека;
import com.varankin.brains.jfx.db.*;
import javafx.scene.*;


/**
 *
 * @author Николай
 */
class EdtБиблиотека extends EdtЭлемент<DbБиблиотека,FxБиблиотека>
{
    EdtБиблиотека( FxБиблиотека элемент )
    {
        super( элемент );
    }
    
    @Override
    public Group загрузить( boolean основной )
    {
        Group group = super.загрузить( основной );

        for( FxМодуль модуль : ЭЛЕМЕНТ.модули() )
            group.getChildren().add( new EdtМодуль( модуль ).загрузить( false ) );
        for( FxРасчет расчет : ЭЛЕМЕНТ.расчеты() )
            group.getChildren().add( new EdtРасчет( расчет ).загрузить( false ) );
        for( FxПроцессор процессор : ЭЛЕМЕНТ.процессоры() )
            group.getChildren().add( new EdtПроцессор( процессор ).загрузить( false ) );
        
        return group;
    }
    
}
