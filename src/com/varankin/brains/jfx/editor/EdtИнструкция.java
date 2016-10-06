package com.varankin.brains.jfx.editor;

import javafx.scene.*;
import com.varankin.brains.db.DbИнструкция;
import javafx.scene.control.TextField;


/**
 *
 * @author Николай
 */
class EdtИнструкция extends EdtАтрибутный<DbИнструкция>
{

    EdtИнструкция( DbИнструкция элемент )
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
