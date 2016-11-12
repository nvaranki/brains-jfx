package com.varankin.brains.jfx.editor;

import com.varankin.brains.db.DbМодуль;
import com.varankin.brains.jfx.db.*;
import javafx.collections.ObservableList;
import javafx.scene.*;


/**
 *
 * @author Николай
 */
class EdtМодуль extends EdtЭлемент<DbМодуль,FxМодуль>
{
    EdtМодуль( FxМодуль элемент )
    {
        super( элемент );
    }
    
    @Override
    public Group загрузить( boolean основной )
    {
        Group group = super.загрузить( основной );
        ObservableList<Node> children = group.getChildren();
        children.addAll( загрузить( ЭЛЕМЕНТ.фрагменты() ) );
        children.addAll( загрузить( ЭЛЕМЕНТ.процессоры() ) );
        children.addAll( загрузить( ЭЛЕМЕНТ.сигналы() ) );
        children.addAll( загрузить( ЭЛЕМЕНТ.соединения() ) );
        children.addAll( загрузить( ЭЛЕМЕНТ.библиотеки() ) );

        return group;
    }
    
}
