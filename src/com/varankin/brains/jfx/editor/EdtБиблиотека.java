package com.varankin.brains.jfx.editor;

import com.varankin.brains.db.Библиотека;
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
    
    Node загрузить()
    {
        Group group = new Group();
        group.setUserData( ЭЛЕМЕНТ );
        
        
        return group;
    }
    
}
