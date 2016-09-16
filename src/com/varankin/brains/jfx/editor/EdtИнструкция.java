package com.varankin.brains.jfx.editor;

import javafx.scene.*;
import com.varankin.brains.db.DbИнструкция;


/**
 *
 * @author Николай
 */
class EdtИнструкция extends EdtАтрибутныйЭлемент<DbИнструкция>
{

    EdtИнструкция( DbИнструкция элемент )
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
