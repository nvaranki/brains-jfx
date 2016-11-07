package com.varankin.brains.jfx.editor;

import com.varankin.brains.db.DbКонвертер;
import com.varankin.brains.jfx.db.*;
import javafx.scene.*;


/**
 *
 * @author Николай
 */
class EdtКонвертер extends EdtЭлемент<DbКонвертер,FxКонвертер>
{
    EdtКонвертер( FxКонвертер элемент )
    {
        super( элемент );
    }
    
    @Override
    public Group загрузить( boolean основной )
    {
        Group group = super.загрузить( основной );

        return group;
    }
    
}
