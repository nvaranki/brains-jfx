package com.varankin.brains.jfx.editor;

import com.varankin.brains.db.DbЗаметка;
import com.varankin.brains.jfx.db.*;
import java.util.List;
import java.util.Queue;
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
        group.getChildren().addAll( загрузить( ЭЛЕМЕНТ.графики() ) );

        return group;
    }
    
    @Override
    protected void тексты( List<Node> children ) {}
    
    @Override
    public boolean составить( Queue<int[]> path )
    {
        if( path.isEmpty() ) return false;
        ЭЛЕМЕНТ.тексты().add( блок( "Новая заметка" ) );
        ЭЛЕМЕНТ.графики().add( надпись( "../text()", path.poll() ) );
        return path.isEmpty();
    }

}
