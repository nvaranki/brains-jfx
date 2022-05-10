package com.varankin.brains.jfx.archive.props;

import com.varankin.brains.db.type.DbАтрибутный;
import javafx.event.ActionEvent;
import javafx.scene.layout.Pane;
import javafx.util.Builder;

/**
 * FXML-контроллер панели выбора и установки параметров.
 * 
 * @author &copy; 2021 Николай Варанкин
 */
@Deprecated
public interface IPropertiesController<A extends DbАтрибутный> 
        extends Builder<Pane>
{
    
    String title();
    
    void onActionApply( ActionEvent event );
    
    void reset( A элемент );
}
