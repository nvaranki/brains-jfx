package com.varankin.brains.jfx.editor;

import com.varankin.brains.db.type.DbТекстовыйБлок;
import javafx.scene.*;
import com.varankin.brains.jfx.db.FxТекстовыйБлок;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;


/**
 *
 * @author Николай
 */
class EdtТекстовыйБлок extends EdtАтрибутный<DbТекстовыйБлок,FxТекстовыйБлок>
{

    EdtТекстовыйБлок( FxТекстовыйБлок элемент )
    {
        super( элемент );
    }
    
    @Override
    public Node загрузить( boolean основной )
    {
        Node node;
        if( true/*основной*/ ) 
        {
            TextField textField = new TextField();
            textField.textProperty().bindBidirectional( ЭЛЕМЕНТ.текст() );
            node = textField;
        }
        else
        {
            Label label = new Label();
            label.textProperty().bind( ЭЛЕМЕНТ.текст() );
            node = label;
        }
        node = super.загрузить( node, основной );
        return node;
    }

}   
