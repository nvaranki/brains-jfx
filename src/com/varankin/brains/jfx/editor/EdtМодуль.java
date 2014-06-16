package com.varankin.brains.jfx.editor;

import com.varankin.brains.db.Модуль;
import com.varankin.brains.db.Неизвестный;
import javafx.scene.*;

/**
 *
 * @author Николай
 */
class EdtМодуль extends EdtАтрибутныйЭлемент<Модуль>
{
    EdtМодуль( Модуль элемент )
    {
        super( элемент );
    }
    
    Node загрузить( boolean изменяемый )
    {
        Group group = new Group();
        group.setUserData( ЭЛЕМЕНТ );
        
        for( Неизвестный н : ЭЛЕМЕНТ.прочее() )
            group.getChildren().add( new EdtНеизвестный( н ).загрузить( изменяемый ) );
        
        
        return group;
    }
    
}
