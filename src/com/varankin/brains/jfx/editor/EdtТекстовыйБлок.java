package com.varankin.brains.jfx.editor;

import com.varankin.brains.db.DbТекстовыйБлок;
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
        Node node = true/*основной*/ ? new TextField( ЭЛЕМЕНТ.getSource().текст() ) : new Label( ЭЛЕМЕНТ.getSource().текст() );
        node = super.загрузить( node, основной );
        return node;
    }

}   
