package com.varankin.brains.jfx.editor;

import javafx.scene.*;
import com.varankin.brains.db.DbТекстовыйБлок;
import javafx.scene.control.Label;
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
    public Node загрузить( boolean основной )
    {
        Node node = true/*основной*/ ? new TextField( ЭЛЕМЕНТ.текст() ) : new Label( ЭЛЕМЕНТ.текст() );
        node = super.загрузить( node, основной );
        return node;
    }

}   
