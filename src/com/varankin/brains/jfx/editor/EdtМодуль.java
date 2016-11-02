package com.varankin.brains.jfx.editor;

import com.varankin.brains.db.DbАтрибутный;
import com.varankin.brains.db.DbБиблиотека;
import com.varankin.brains.db.DbИнструкция;
import com.varankin.brains.db.DbМодуль;
import com.varankin.brains.db.DbПроцессор;
import com.varankin.brains.db.DbСигнал;
import com.varankin.brains.db.DbСоединение;
import com.varankin.brains.db.DbТекстовыйБлок;
import com.varankin.brains.db.DbФрагмент;
import javafx.scene.*;

import static com.varankin.brains.io.xml.XmlSvg.SVG_ATTR_TRANSFORM;
import static com.varankin.brains.io.xml.XmlSvg.XMLNS_SVG;
import static com.varankin.brains.jfx.editor.EdtФрагмент.toTransforms;

/**
 *
 * @author Николай
 */
class EdtМодуль extends EdtЭлемент<DbМодуль>
{
    EdtМодуль( DbМодуль элемент )
    {
        super( элемент );
    }
    
    @Override
    public Group загрузить( boolean основной )
    {
        Group group = super.загрузить( основной );

        for( DbФрагмент фрагмент : ЭЛЕМЕНТ.фрагменты() )
            group.getChildren().add( new EdtФрагмент( фрагмент ).загрузить( false ) );
        for( DbПроцессор процессор : ЭЛЕМЕНТ.процессоры() )
            group.getChildren().add( new EdtПроцессор( процессор ).загрузить( false ) );
        for( DbСигнал сигнал : ЭЛЕМЕНТ.сигналы() )
            group.getChildren().add( new EdtСигнал( сигнал ).загрузить( false ) );
        for( DbСоединение соединение : ЭЛЕМЕНТ.соединения() )
            group.getChildren().add( new EdtСоединение( соединение ).загрузить( false ) );
        for( DbИнструкция н : ЭЛЕМЕНТ.инструкции() )
            group.getChildren().add( new EdtИнструкция( н ).загрузить( false ) );
        for( DbТекстовыйБлок н : ЭЛЕМЕНТ.тексты() )
            group.getChildren().add( new EdtТекстовыйБлок( н ).загрузить( false ) );
        for( DbАтрибутный н : ЭЛЕМЕНТ.прочее() )
            group.getChildren().add( new EdtНеизвестный( н ).загрузить( false ) );
        for( DbБиблиотека библиотека : ЭЛЕМЕНТ.библиотеки() )
            group.getChildren().add( new EdtБиблиотека( библиотека ).загрузить( false ) );
        
        return group;
    }
    
}
