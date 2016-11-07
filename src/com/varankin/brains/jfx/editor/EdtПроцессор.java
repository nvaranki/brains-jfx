package com.varankin.brains.jfx.editor;

import com.varankin.brains.db.DbПроцессор;
import com.varankin.brains.jfx.db.*;
import javafx.scene.*;

/**
 *
 * @author Николай
 */
class EdtПроцессор extends EdtЭлемент<DbПроцессор,FxПроцессор>
{
    EdtПроцессор( FxПроцессор элемент )
    {
        super( элемент );
    }
    
    @Override
    public Group загрузить( boolean основной )
    {
        Group group = super.загрузить( основной );

        for( FxКлассJava н : ЭЛЕМЕНТ.классы() )
            group.getChildren().add( new EdtКлассJava( н ).загрузить( false ) );
        for( FxПараметр н : ЭЛЕМЕНТ.параметры() )
            group.getChildren().add( new EdtПараметр( н ).загрузить( false ) );
        
        return group;
    }
    
}
