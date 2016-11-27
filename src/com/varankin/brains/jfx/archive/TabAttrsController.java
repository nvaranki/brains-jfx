package com.varankin.brains.jfx.archive;

import com.varankin.brains.db.DbАтрибутный;
import com.varankin.brains.jfx.db.FxProperty;
import com.varankin.brains.jfx.db.FxЭлемент;
import com.varankin.util.LoggerX;
import java.util.Arrays;
import java.util.Collection;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.util.Builder;

/**
 * FXML-контроллер панели дополнительных параметров.
 * 
 * @author &copy; 2016 Николай Варанкин
 */
public class TabAttrsController implements Builder<BorderPane>
{
    private static final LoggerX LOGGER = LoggerX.getLogger( TabAttrsController.class );
    private static final String RESOURCE_CSS  = "/fxml/archive/TabAttrs.css";
    private static final String CSS_CLASS = "properties-tab-attrs";

//    private final AttributeAgent pathAgent, nameAgent;

    private FxЭлемент элемент;
    
    @FXML private TableView<FxProperty> table;

    public TabAttrsController()
    {
//        nameAgent = new NameAgent();
//        pathAgent = new PathAgent();
    }
    
    /**
     * Создает панель дополнительных параметров.
     * Применяется в конфигурации без FXML.
     * 
     * @return панель дополнительных параметров.
     */
    @Override
    public BorderPane build()
    {
        TableColumn<FxProperty,String> colName = new TableColumn<>( LOGGER.text( "properties.tab.attrs.col.name" ) );
        colName.setCellValueFactory( (cdf) -> new SimpleStringProperty(cdf.getValue().getName()) );
        TableColumn<FxProperty,String> colValue = new TableColumn<>( LOGGER.text( "properties.tab.attrs.col.value" ) );
        colValue.setCellValueFactory( (cdf) -> new SimpleStringProperty(
                DbАтрибутный.toStringValue( cdf.getValue().getValue() ) ) );
        TableColumn<FxProperty,String> colScope = new TableColumn<>( LOGGER.text( "properties.tab.attrs.col.scope" ) );
        colScope.setCellValueFactory( (cdf) -> new SimpleStringProperty( cdf.getValue().getScope()) );
        
        table = new TableView<>();
        table.getColumns().addAll( colName, colValue, colScope );
        table.setPlaceholder( new Text( LOGGER.text( "properties.tab.attrs.empty" ) ) );
        
        BorderPane pane = new BorderPane();
        pane.setCenter( table );
        
        pane.getStyleClass().add( CSS_CLASS );
        pane.getStylesheets().add( getClass().getResource( RESOURCE_CSS ).toExternalForm() );

        initialize();
        
        return pane;
    }
    
    @FXML
    protected void initialize()
    {
    }
    
    Collection<AttributeAgent> getAgents()
    {
        return Arrays.asList( /*pathAgent, nameAgent*/ );
    }

    void reset( FxЭлемент элемент )
    {
        this.элемент = элемент;
        table.itemsProperty().bind( элемент.атрибуты() );
    }
    
//    private class PathAgent implements AttributeAgent
//    {
//        volatile String значение;
//
//        @Override
//        public void fromScreen()
//        {
//        }
//        
//        @Override
//        public void toScreen()
//        {
//            path.setText( значение );
//        }
//        
//        @Override
//        public void fromStorage()
//        {
//            значение = элемент.положение( "/" );
//            значение = значение.substring( 0, значение.length() - элемент.название().length() );
//        }
//        
//        @Override
//        public void toStorage()
//        {
//        }
//
//    }
//    
//    private class NameAgent implements AttributeAgent
//    {
//        volatile String значение;
//
//        @Override
//        public void fromScreen()
//        {
//            значение = name.getText();
//        }
//        
//        @Override
//        public void toScreen()
//        {
//            name.setText( значение );
//        }
//        
//        @Override
//        public void fromStorage()
//        {
//            значение = элемент.название();
//        }
//        
//        @Override
//        public void toStorage()
//        {
//            элемент.название( значение );
//        }
//        
//    }
    
}
