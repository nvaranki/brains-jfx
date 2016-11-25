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
        Node node;
        if( основной )
        {
            TextField процессор = new TextField();
            процессор.textProperty().bindBidirectional( ЭЛЕМЕНТ.процессор() );
            TextField код = new TextField();
            код.textProperty().bindBidirectional( ЭЛЕМЕНТ.код() );
            node = new HBox( процессор, код );
        }
        else
        {
            Label label = new Label();
            label.textProperty().bind( ЭЛЕМЕНТ.выполнить() );
            node = label;
        }
        node = super.загрузить( node, основной );
        return node;
    }

}   
