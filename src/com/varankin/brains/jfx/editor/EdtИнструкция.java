package com.varankin.brains.jfx.editor;

import javafx.scene.*;
import com.varankin.brains.db.DbИнструкция;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;


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
        Node node = изменяемый ? new HBox( new TextField( ЭЛЕМЕНТ.процессор() ), 
                new TextField( ЭЛЕМЕНТ.код() ) ) : new Label( ЭЛЕМЕНТ.выполнить() );
        node = super.загрузить( node, изменяемый );
        return node;
    }

}   
