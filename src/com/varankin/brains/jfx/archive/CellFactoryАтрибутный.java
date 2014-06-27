package com.varankin.brains.jfx.archive;

import com.varankin.brains.db.*;
import com.varankin.util.LoggerX;
import java.util.logging.Logger;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeView;
import javafx.util.Callback;

/**
 *
 * @author Николай
 */
class CellFactoryАтрибутный implements Callback<TreeView<Атрибутный>, TreeCell<Атрибутный>>
{
    private static final LoggerX LOGGER = LoggerX.getLogger( CellFactoryАтрибутный.class );

    @Override
    public TreeCell<Атрибутный> call( TreeView<Атрибутный> view )
    {
        return new TreeCellАтрибутный();
    }
    
    static private class TreeCellАтрибутный extends TreeCell<Атрибутный>
    {
        @Override
        public void updateItem( Атрибутный item, boolean empty ) 
        {
            super.updateItem( item, empty );
            setText( empty ? null : название( item ) );
        }
        
        String название( Атрибутный item )
        {
            String текст = "";
            if( item == null )
            {
                текст = "null";
            }
            else if( item instanceof Элемент )
            {
                текст = ((Элемент)item).название();
            }
            else if( item instanceof Инструкция  )
            {
                текст = ((Инструкция)item).код();
                if( текст != null && текст.length() > 10 )
                    текст = текст.substring( 0, 7 ).concat( "..." );
            }
            else if( item instanceof ТекстовыйБлок )
            {
                текст = ((ТекстовыйБлок)item).текст();
                if( текст != null && текст.length() > 10 )
                    текст = текст.substring( 0, 7 ).concat( "..." );
            }
            else if( item instanceof Пакет )
            {
                текст = "Пакет";//((Пакет)item).название();
            }
            else if( item instanceof XmlNameSpace )
            {
                текст = ((XmlNameSpace)item).название();
                if( текст == null || текст.trim().isEmpty() )
                    текст = "XmlNameSpace"; //TODO
            }
            else if( item instanceof Архив )
            {
                Архив архив = (Архив)item;
                //TODO distinguish local and remote архив
                текст = LOGGER.text( "cell.archive" );
            }
            else 
                текст = item.getClass().getSimpleName();
            return текст;
        }
        
    }
    
}
