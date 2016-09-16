package com.varankin.brains.jfx.archive;

import com.varankin.brains.db.DbЭлемент;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.util.Builder;
import com.varankin.brains.db.DbАтрибутный;

/**
 * FXML-контроллер панели выбора и установки параметров.
 * 
 * @author &copy; 2014 Николай Варанкин
 */
public interface IPropertiesController<A extends DbАтрибутный> 
        extends Builder<Pane>
{
    
    String title();
    
    void onActionApply( ActionEvent event );
    
    void reset( A элемент );
}
