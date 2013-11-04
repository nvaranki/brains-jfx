package com.varankin.brains.jfx.analyser;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

class ContextMenuRaiser implements EventHandler<MouseEvent>
{
    private final ContextMenu POPUP;
    private final Node OWNER;

    ContextMenuRaiser( ContextMenu popup, Node owner )
    {
        POPUP = popup;
        OWNER = owner;
    }

    @Override
    public void handle( MouseEvent e )
    {
        if( MouseButton.SECONDARY.equals( e.getButton() ) )
        {
            //System.out.println( "Right click on " + OWNER.getClass().getSimpleName() );
            //owner.setFocused( true );
            POPUP.show( OWNER, e.getScreenX(), e.getScreenY() );
            e.consume();
        }
        else
        {
            //System.out.println( "Click on " + OWNER.getClass().getSimpleName() );
            POPUP.hide();
        }
    }
    
}
