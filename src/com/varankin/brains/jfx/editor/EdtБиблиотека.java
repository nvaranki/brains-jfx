package com.varankin.brains.jfx.editor;

import com.varankin.brains.db.DbБиблиотека;
import com.varankin.brains.jfx.db.*;
import javafx.collections.ObservableList;
import javafx.scene.*;


/**
 *
 * @author Николай
 */
class EdtБиблиотека extends EdtЭлемент<DbБиблиотека,FxБиблиотека>
{
    EdtБиблиотека( FxБиблиотека элемент )
    {
        super( элемент );
    }
    
    @Override
    public Group загрузить( boolean основной )
    {
        Group group = super.загрузить( основной );
        ObservableList<Node> children = group.getChildren();
        children.addAll( загрузить( ЭЛЕМЕНТ.классы() ) );
        children.addAll( загрузить( ЭЛЕМЕНТ.ленты() ) );
        children.addAll( загрузить( ЭЛЕМЕНТ.модули() ) );
        children.addAll( загрузить( ЭЛЕМЕНТ.поля() ) );
        children.addAll( загрузить( ЭЛЕМЕНТ.процессоры() ) );
        children.addAll( загрузить( ЭЛЕМЕНТ.расчеты() ) );

        return group;
    }
    
}
