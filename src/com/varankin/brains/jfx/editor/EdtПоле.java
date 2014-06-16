package com.varankin.brains.jfx.editor;

import com.varankin.brains.db.Неизвестный;
import com.varankin.brains.db.Поле;
import com.varankin.brains.db.Сигнал;
import com.varankin.brains.db.Соединение;
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
        
        for( Сигнал сигнал : ЭЛЕМЕНТ.сигналы() )
            group.getChildren().add( new EdtСигнал( сигнал ).загрузить( изменяемый ) );
        for( Соединение соединение : ЭЛЕМЕНТ.соединения() )
            group.getChildren().add( new EdtСоединение( соединение ).загрузить( изменяемый ) );
        for( Неизвестный н : ЭЛЕМЕНТ.прочее() )
            group.getChildren().add( new EdtНеизвестный( н ).загрузить( изменяемый ) );
        
        
        return group;
    }
    
}
