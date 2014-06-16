package com.varankin.brains.jfx.editor;

import com.varankin.brains.db.Неизвестный;
import com.varankin.brains.db.Расчет;
import javafx.scene.*;

/**
 *
 * @author Николай
 */
class EdtРасчет extends EdtАтрибутныйЭлемент<Расчет>
{
    EdtРасчет( Расчет элемент )
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
