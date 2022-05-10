package com.varankin.brains.jfx;

import com.varankin.brains.jfx.editor.EditorController;
import java.util.List;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;

/**
 * Действие JavaFX для открытия закладки с новым редактором.
 *
 * @author &copy; 2014 Николай Варанкин
 */
class ApplicationActionEditor extends AbstractContextJfxAction<JavaFX> 
{

    ApplicationActionEditor( JavaFX jfx ) 
    {
        super( jfx, jfx.словарь( ApplicationActionEditor.class ) );
    }

    @Override
    public void handle( ActionEvent event ) 
    {
        List<TitledSceneGraph> views = JavaFX.getInstance().getViews().getValue();
        views.add( new TitledSceneGraph( new EditorController().build(), 
                iconProperty().getValue().getImage(), 
                new SimpleStringProperty( textProperty().getValue() ) ) );
    }
    
}
