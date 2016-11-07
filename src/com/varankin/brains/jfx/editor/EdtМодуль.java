package com.varankin.brains.jfx.editor;

import com.varankin.brains.db.DbМодуль;
import com.varankin.brains.jfx.db.*;
import javafx.scene.*;


/**
 *
 * @author Николай
 */
class EdtМодуль extends EdtЭлемент<DbМодуль,FxМодуль>
{
    EdtМодуль( FxМодуль элемент )
    {
        super( элемент );
    }
    
    @Override
    public Group загрузить( boolean основной )
    {
        Group group = super.загрузить( основной );

        for( FxФрагмент фрагмент : ЭЛЕМЕНТ.фрагменты() )
            group.getChildren().add( new EdtФрагмент( фрагмент ).загрузить( false ) );
        for( FxПроцессор процессор : ЭЛЕМЕНТ.процессоры() )
            group.getChildren().add( new EdtПроцессор( процессор ).загрузить( false ) );
        for( FxСигнал сигнал : ЭЛЕМЕНТ.сигналы() )
            group.getChildren().add( new EdtСигнал( сигнал ).загрузить( false ) );
        for( FxСоединение соединение : ЭЛЕМЕНТ.соединения() )
            group.getChildren().add( new EdtСоединение( соединение ).загрузить( false ) );
        for( FxБиблиотека библиотека : ЭЛЕМЕНТ.библиотеки() )
            group.getChildren().add( new EdtБиблиотека( библиотека ).загрузить( false ) );
        
        return group;
    }
    
}
