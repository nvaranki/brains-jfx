package com.varankin.brains.jfx.analyser;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.util.Builder;

/**
 *
 * @author Николай
 */
public final class GraphPropertiesPaneController implements Builder<Node>
{
    private final BooleanProperty changedProperty;

    public GraphPropertiesPaneController()
    {
        changedProperty = new SimpleBooleanProperty( false );
    }

    @Override
    public Node build()
    {
        GridPane pane = new GridPane();
        
        return pane;
    }
    
    BooleanProperty changedProperty()
    {
        return changedProperty;
    }
    
}
