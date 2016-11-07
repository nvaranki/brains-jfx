package com.varankin.brains.jfx.editor;

import com.varankin.brains.db.DbИнструкция;
import javafx.scene.*;
import com.varankin.brains.jfx.db.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;


/**
 *
 * @author Николай
 */
class EdtИнструкция extends EdtАтрибутный<DbИнструкция,FxИнструкция>
{

    EdtИнструкция( FxИнструкция элемент )
    {
        super( элемент );
    }
    
    @Override
    public Node загрузить( boolean основной )
    {
        Node node = основной ? new HBox( new TextField( ЭЛЕМЕНТ.getSource().процессор() ), 
                new TextField( ЭЛЕМЕНТ.getSource().код() ) ) : new Label( ЭЛЕМЕНТ.getSource().выполнить() );
        node = super.загрузить( node, основной );
        return node;
    }

}   
