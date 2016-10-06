package com.varankin.brains.jfx.editor;

import javafx.scene.*;
import com.varankin.brains.db.DbТекстовыйБлок;
import javafx.scene.control.TextField;


/**
 *
 * @author Николай
 */
class EdtТекстовыйБлок extends EdtАтрибутный<DbТекстовыйБлок>
{

    EdtТекстовыйБлок( DbТекстовыйБлок элемент )
    {
        super( элемент );
    }
    
    @Override
    public Node загрузить( boolean изменяемый )
    {
        Node node;
        String s;
        
        node = super.загрузить( new TextField( "TODO" ), изменяемый );//null;//.setUserData( ЭЛЕМЕНТ );
        return node;
    }

}   
