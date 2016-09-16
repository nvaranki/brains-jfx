package com.varankin.brains.jfx.editor;

import javafx.scene.*;
import com.varankin.brains.db.DbТекстовыйБлок;


/**
 *
 * @author Николай
 */
class EdtТекстовыйБлок extends EdtАтрибутныйЭлемент<DbТекстовыйБлок>
{

    EdtТекстовыйБлок( DbТекстовыйБлок элемент )
    {
        super( элемент );
    }
    
    Node загрузить( boolean изменяемый )
    {
        Node node;
        String s;
        
        node = null;//.setUserData( ЭЛЕМЕНТ );
        return node;
    }

}   
