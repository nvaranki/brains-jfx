package com.varankin.brains.jfx.archive;

import com.varankin.brains.db.Атрибутный;
import com.varankin.brains.db.DbЭлемент;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.util.Builder;

/**
 * FXML-контроллер панели выбора и установки параметров.
 * 
 * @author &copy; 2014 Николай Варанкин
 */
public interface IPropertiesController<A extends Атрибутный> 
        extends Builder<Pane>
{
    
    String title();
    
    void onActionApply( ActionEvent event );
    
    void reset( A элемент );
}
