package com.varankin.brains.jfx.editor;

import com.varankin.brains.db.*;
import javafx.scene.*;

/**
 *
 * @author Николай
 */
abstract class EdtЭлемент<T extends DbЭлемент> extends EdtУзел<T>
{
    EdtЭлемент( T элемент )
    {
        super( элемент );
    }
    
    @Override
    public Group загрузить( boolean изменяемый )
    {
        Group group = super.загрузить( изменяемый );

        for( DbЗаметка э : ЭЛЕМЕНТ.заметки() )
            group.getChildren().add( new EdtЗаметка( э ).загрузить( false ) );
        for( DbГрафика э : ЭЛЕМЕНТ.графики() )
            group.getChildren().add( new EdtГрафика( э ).загрузить( false ) );
        
        return group;
    }
    
}
