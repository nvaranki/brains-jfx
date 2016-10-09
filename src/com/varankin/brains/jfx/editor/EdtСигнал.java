package com.varankin.brains.jfx.editor;

import com.varankin.brains.db.DbКлассJava;
import com.varankin.brains.db.DbПараметр;
import com.varankin.brains.db.DbСигнал;
import javafx.scene.*;

/**
 *
 * @author Николай
 */
class EdtСигнал extends EdtЭлемент<DbСигнал>
{
    EdtСигнал( DbСигнал элемент )
    {
        super( элемент );
    }
    
    @Override
    public Group загрузить( boolean изменяемый )
    {
        Group group = super.загрузить( изменяемый );

        for( DbПараметр н : ЭЛЕМЕНТ.параметры() )
            group.getChildren().add( new EdtПараметр( н ).загрузить( false ) );
        for( DbКлассJava н : ЭЛЕМЕНТ.классы() )
            group.getChildren().add( new EdtНеизвестный( н ).загрузить( false ) );
        
        return group;
    }
    
}
