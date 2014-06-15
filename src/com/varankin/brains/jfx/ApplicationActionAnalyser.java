package com.varankin.brains.jfx;

import com.varankin.brains.jfx.analyser.AnalyserController;
import java.util.List;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;

/**
 * Действие JavaFX для открытия закладки с новым анализатором.
 *
 * @author &copy; 2014 Николай Варанкин
 */
class ApplicationActionAnalyser extends AbstractContextJfxAction<JavaFX> 
{

    ApplicationActionAnalyser( JavaFX jfx ) 
    {
        super( jfx, jfx.словарь( ApplicationActionAnalyser.class ) );
    }

    @Override
    public void handle( ActionEvent event ) 
    {
        List<TitledSceneGraph> views = JavaFX.getInstance().getViews().getValue();
        views.add( new TitledSceneGraph( new AnalyserController().build(), 
                null, new SimpleStringProperty( textProperty().getValue() ) ) );
    }
    
}
