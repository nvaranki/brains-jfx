package com.varankin.brains.jfx.archive;

import com.varankin.brains.jfx.db.FxКонтакт;
import com.varankin.util.LoggerX;

import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Builder;
import javafx.util.StringConverter;

import static com.varankin.brains.db.DbКонтакт.*;

/**
 * FXML-контроллер панели установки параметров контакта.
 * 
 * @author &copy; 2017 Николай Варанкин
 */
public class TabPinController implements Builder<GridPane>
{
    private static final LoggerX LOGGER = LoggerX.getLogger( TabPinController.class );
    private static final String RESOURCE_CSS  = "/fxml/archive/TabPin.css";
    private static final String CSS_CLASS = "properties-tab-pin";
    private static final StringConverter<Integer> CNV_PRIORITY 
            = new ToStringConverter<>( s -> Integer.valueOf( s ) );

    static final String RESOURCE_FXML = "/fxml/archive/TabPin.fxml";
    static final ResourceBundle RESOURCE_BUNDLE = LOGGER.getLogger().getResourceBundle();

    private final ChangeListener<Short> listenerСВОЙСТВА;
    
    private FxКонтакт контакт;
    
    @FXML private TextField priority;
    @FXML private CheckBox receiver;
    @FXML private CheckBox trasmitter;

    public TabPinController()
    {
        listenerСВОЙСТВА = (v,o,n) -> свойства( n );
    }

    /**
     * Создает панель установки параметров.
     * Применяется в конфигурации без FXML.
     * 
     * @return панель установки параметров.
     */
    @Override
    public GridPane build()
    {
        priority = new TextField();
        priority.setFocusTraversable( true );
        
        receiver = new CheckBox();
        receiver.setId( "receiver" );
        receiver.setFocusTraversable( true );
        receiver.setOnAction( this::onReceiverChanged );
        
        trasmitter = new CheckBox();
        trasmitter.setId( "trasmitter" );
        trasmitter.setFocusTraversable( true );
        trasmitter.setOnAction( this::onTrasmitterChanged );
        
        GridPane pane = new GridPane();
        pane.add( new Label( LOGGER.text( "tab.pin.priority" ) ), 0, 0 );
        pane.add( priority, 1, 0 );
        pane.add( new Label( LOGGER.text( "tab.pin.receiver" ) ), 0, 1 );
        pane.add( receiver, 1, 1 );
        pane.add( new Label( LOGGER.text( "tab.pin.trasmitter" ) ), 0, 2 );
        pane.add( trasmitter, 1, 2 );
        
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
    private void onReceiverChanged( ActionEvent event )
    {
        свойства( ПРИЕМНИК, receiver.selectedProperty().get() );
    }
            
    @FXML
    private void onTrasmitterChanged( ActionEvent event )
    {
        свойства( ИСТОЧНИК, trasmitter.selectedProperty().get() );
    }
            
    void set( FxКонтакт контакт )
    {
        if( this.контакт != null )
        {
            priority.textProperty().unbindBidirectional( this.контакт.приоритет() );
            this.контакт.свойства().removeListener( listenerСВОЙСТВА );
        }
        if( контакт != null )
        {
            priority.textProperty().bindBidirectional( контакт.приоритет(), CNV_PRIORITY );
            контакт.свойства().addListener( listenerСВОЙСТВА );
            Short value = контакт.свойства().getValue();
            if( value != null ) свойства( value );
        }
        this.контакт = контакт;
    }
    
    private void свойства( short значение )
    {
        receiver  .selectedProperty().setValue( ( значение & ПРИЕМНИК ) != 0 );
        trasmitter.selectedProperty().setValue( ( значение & ИСТОЧНИК ) != 0 );
    }
    
    private void свойства( short маска, boolean значение )
    {
        Short value = контакт.свойства().getValue();
        short свойства = value != null ? value : 0;
        свойства &= ~маска;
        свойства |= значение ? маска : НЕТ;
        контакт.свойства().setValue( свойства );
    }
    
}
