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
    public Node загрузить( boolean изменяемый )
    {
        Node node = изменяемый ? new TextField( ЭЛЕМЕНТ.текст() ) : new Label( ЭЛЕМЕНТ.текст() );
        node = super.загрузить( node, изменяемый );
        return node;
    }

}   
