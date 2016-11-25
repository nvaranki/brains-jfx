package com.varankin.brains.jfx.archive;

import com.varankin.brains.db.Транзакция;
import com.varankin.brains.jfx.JavaFX;
import com.varankin.util.LoggerX;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.util.Builder;
import com.varankin.brains.db.DbNameSpace;

/**
 * FXML-контроллер панели выбора и установки параметров объекта пространства имен XML.
 * 
 * @author &copy; 2014 Николай Варанкин
 */
public final class PropertiesXmlNameSpaceController implements Builder<Pane>
{
    private static final LoggerX LOGGER = LoggerX.getLogger( PropertiesXmlNameSpaceController.class );
    private static final String RESOURCE_CSS  = "/fxml/archive/PropertiesXmlNameSpace.css";
    private static final String CSS_CLASS = "properties-xml-ns";

    private DbNameSpace xmlNameSpace;
    //private final BooleanProperty applied;
    
    @FXML private TextField prefix;
    @FXML private TextField uri;

    /**
     * Создает панель выбора и установки параметров объекта пространства имен XML.
     * Применяется в конфигурации без FXML.
     * 
     * @return панель выбора и установки параметров.
     */
    @Override
    public GridPane build()
    {
        prefix = new TextField();
        prefix.setId( "prefix" );
        prefix.setPrefColumnCount( 6 );
        
        uri = new TextField();
        uri.setId( "uri" );
        uri.setPrefColumnCount( 24 );
        
        GridPane pane = new GridPane();
        pane.add( new Label( LOGGER.text( "properties.ns.prefix" ) ), 0, 0 );
        pane.add( prefix, 1, 0 );
        pane.add( new Label( LOGGER.text( "properties.ns.uri" ) ), 0, 1 );
        pane.add( uri, 1, 1 );
        
        pane.getStyleClass().add( CSS_CLASS );
        pane.getStylesheets().add( getClass().getResource( RESOURCE_CSS ).toExternalForm() );

        initialize();
        
        return pane;
    }

    @FXML
    protected void initialize()
    {
    }
    
    @FXML
    void onActionApply( ActionEvent event )
    {
        //applied.setValue( false );
        JavaFX.getInstance().execute( new SaveTask( prefix.getText(), uri.getText() )  );
    }

    void reset( DbNameSpace xmlNameSpace )
    {
        this.xmlNameSpace = xmlNameSpace;
        JavaFX.getInstance().execute( new ReadTask()  );
    }
    
    private class ReadTask extends Task<Void>
    {
        private volatile String название, текстUri;

        @Override
        protected Void call() throws Exception
        {
            try( Транзакция т = xmlNameSpace.транзакция() )
            {
                т.согласовать( Транзакция.Режим.ЧТЕНИЕ_БЕЗ_ЗАПИСИ, xmlNameSpace );
                название = xmlNameSpace.название();
                текстUri = xmlNameSpace.uri();
            }
            return null;
        }
        
        @Override
        protected void succeeded()
        {
            prefix.setText( название );
            uri.setText( текстUri );
        }
        
    }
    
    private class SaveTask extends Task<Void>
    {
        private final String название, текстUri;

        SaveTask( String название, String текстUri )
        {
            this.название = название;
            this.текстUri = текстUri;
        }

        @Override
        protected Void call() throws Exception
        {
            try( Транзакция т = xmlNameSpace.транзакция() )
            {
                т.согласовать( Транзакция.Режим.ЗАПРЕТ_ДОСТУПА, xmlNameSpace );
                xmlNameSpace.название( название );
                xmlNameSpace.uri( текстUri );
                т.завершить( true );
            }
            return null;
        }

        @Override
        protected void succeeded()
        {
            //applied.setValue( true );
        }
        
    }
    
}
