package com.varankin.brains.jfx.editor;

import com.varankin.brains.db.DbЗаметка;
import com.varankin.brains.jfx.db.*;
import javafx.scene.*;


/**
 *
 * @author Николай
 */
class EdtЗаметка extends EdtУзел<DbЗаметка,FxЗаметка>
{
    EdtЗаметка( FxЗаметка элемент )
    {
        super( элемент );
    }
    
    @Override
    public Group загрузить( boolean основной )
    {
        Group group = super.загрузить( основной );

        for( FxГрафика э : ЭЛЕМЕНТ.графики() )
            group.getChildren().add( new EdtГрафика( э ).загрузить( false ) );
//        for( FxИнструкция н : ЭЛЕМЕНТ.инструкции() )
//            group.getChildren().add( new EdtИнструкция( н ).загрузить( изменяемый ) );
//        for( FxТекстовыйБлок н : ЭЛЕМЕНТ.тексты() )
//            group.getChildren().add( new EdtТекстовыйБлок( н ).загрузить( изменяемый ) );
//        for( FxАтрибутный н : ЭЛЕМЕНТ.прочее() )
//            group.getChildren().add( new EdtНеизвестный( н ).загрузить( изменяемый ) );
        
        return group;
    }
    
}
