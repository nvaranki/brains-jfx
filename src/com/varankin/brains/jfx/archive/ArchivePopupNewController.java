package com.varankin.brains.jfx.archive;

import com.varankin.brains.db.Атрибутный;
import com.varankin.util.LoggerX;
//import java.util.ResourceBundle;
import java.util.logging.*;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.util.Builder;

import static com.varankin.brains.jfx.JavaFX.icon;
import static javafx.beans.binding.Bindings.createBooleanBinding;

/**
 * FXML-контроллер контекстного меню новых элементов навигатора по архиву.
 * 
 * @author &copy; 2014 Николай Варанкин
 */
public final class ArchivePopupNewController implements Builder<Menu>
{
    private static final LoggerX LOGGER = LoggerX.getLogger( ArchivePopupNewController.class );
    //private static final String RESOURCE_CSS  = "/fxml/archive/ArchivePopupNew.css";
    //private static final String CSS_CLASS = "archive-popup-new";
    
    //public static final String RESOURCE_FXML  = "/fxml/archive/ArchivePopupNew.fxml";
    //public static final ResourceBundle RESOURCE_BUNDLE = LOGGER.getLogger().getResourceBundle();

    private final ListProperty<Атрибутный> selection;
    
    @FXML private final BooleanProperty
        disableNewАрхив, disableNewПакет, 
        disableNewПроект, disableNewБиблиотека, disableNewФрагмент, 
        disableNewСигнал, disableNewСоединение, disableNewКонтакт, 
        disableNewМодуль, disableNewПоле, disableNewПроцессор, 
        disableNewТочка, disableNewЗаметка, disableNewИнструкция, 
        disableNewКлассJava, disableNewТекстовыйБлок, disableNewXmlNameSpace;

    public ArchivePopupNewController()
    {
        selection = new SimpleListProperty<>();
        
        disableNewАрхив = new SimpleBooleanProperty( this, "disableNewАрхив" );
        disableNewПакет = new SimpleBooleanProperty( this, "disableNewПакет" );
        disableNewПроект = new SimpleBooleanProperty( this, "disableNewПроект" );
        disableNewБиблиотека = new SimpleBooleanProperty( this, "disableNewБиблиотека" );
        disableNewФрагмент = new SimpleBooleanProperty( this, "disableNewФрагмент" );
        disableNewСигнал = new SimpleBooleanProperty( this, "disableNewСигнал" );
        disableNewСоединение = new SimpleBooleanProperty( this, "disableNewСоединение" );
        disableNewКонтакт = new SimpleBooleanProperty( this, "disableNewКонтакт" );
        disableNewМодуль = new SimpleBooleanProperty( this, "disableNewМодуль" );
        disableNewПоле = new SimpleBooleanProperty( this, "disableNewПоле" );
        disableNewПроцессор = new SimpleBooleanProperty( this, "disableNewПроцессор" );
        disableNewТочка = new SimpleBooleanProperty( this, "disableNewТочка" );
        disableNewЗаметка = new SimpleBooleanProperty( this, "disableNewЗаметка" );
        disableNewИнструкция = new SimpleBooleanProperty( this, "disableNewИнструкция" );
        disableNewКлассJava = new SimpleBooleanProperty( this, "disableNewКлассJava" );
        disableNewТекстовыйБлок = new SimpleBooleanProperty( this, "disableNewТекстовыйБлок" );
        disableNewXmlNameSpace = new SimpleBooleanProperty( this, "disableNewXmlNameSpace" );
        
        disableNewАрхив.bind( createBooleanBinding( () -> disableActionNewАрхив(), selection ) );
        disableNewПакет.bind( createBooleanBinding( () -> disableActionNewПакет(), selection ) );
        disableNewПроект.bind( createBooleanBinding( () -> disableActionNewПроект(), selection ) );
        disableNewБиблиотека.bind( createBooleanBinding( () -> disableActionNewБиблиотека(), selection ) );
        disableNewФрагмент.bind( createBooleanBinding( () -> disableActionNewФрагмент(), selection ) );
        disableNewСигнал.bind( createBooleanBinding( () -> disableActionNewСигнал(), selection ) );
        disableNewСоединение.bind( createBooleanBinding( () -> disableActionNewСоединение(), selection ) );
        disableNewКонтакт.bind( createBooleanBinding( () -> disableActionNewКонтакт(), selection ) );
        disableNewМодуль.bind( createBooleanBinding( () -> disableActionNewМодуль(), selection ) );
        disableNewПоле.bind( createBooleanBinding( () -> disableActionNewПоле(), selection ) );
        disableNewПроцессор.bind( createBooleanBinding( () -> disableActionNewПроцессор(), selection ) );
        disableNewТочка.bind( createBooleanBinding( () -> disableActionNewТочка(), selection ) );
        disableNewЗаметка.bind( createBooleanBinding( () -> disableActionNewЗаметка(), selection ) );
        disableNewИнструкция.bind( createBooleanBinding( () -> disableActionNewИнструкция(), selection ) );
        disableNewКлассJava.bind( createBooleanBinding( () -> disableActionNewКлассJava(), selection ) );
        disableNewТекстовыйБлок.bind( createBooleanBinding( () -> disableActionNewТекстовыйБлок(), selection ) );
        disableNewXmlNameSpace.bind( createBooleanBinding( () -> disableActionNewXmlNameSpace(), selection ) );
    }

    /**
     * Создает панель навигатора. 
     * Применяется в конфигурации без FXML.
     * 
     * @return панель навигатора. 
     */
    @Override
    public Menu build()
    {
        MenuItem menuNewАрхив = new MenuItem(
                LOGGER.text( "cell.archive" ) );//, icon( "icons16x16/archive.png" ) );
        menuNewАрхив.setOnAction( this::onActionNewАрхив );
        menuNewАрхив.disableProperty().bind( disableNewАрхив );
        
        MenuItem menuNewБиблиотека = new MenuItem(
                LOGGER.text( "cell.library" ), icon( "icons16x16/new-library.png" ) );
        menuNewБиблиотека.setOnAction( this::onActionNewБиблиотека );
        menuNewБиблиотека.disableProperty().bind( disableNewБиблиотека );
        
        MenuItem menuNewЗаметка = new MenuItem(
                LOGGER.text( "cell.note" ), icon( "icons16x16/properties.png" ) );
        menuNewЗаметка.setOnAction( this::onActionNewЗаметка );
        menuNewЗаметка.disableProperty().bind( disableNewЗаметка );
        
        MenuItem menuNewИнструкция = new MenuItem(
                LOGGER.text( "cell.instruction" ) );//, icon( "icons16x16/new-library.png" ) );
        menuNewИнструкция.setOnAction( this::onActionNewИнструкция );
        menuNewИнструкция.disableProperty().bind( disableNewИнструкция );
        
        MenuItem menuNewКлассJava = new MenuItem(
                LOGGER.text( "cell.class.java" ) );//, icon( "icons16x16/new-library.png" ) );
        menuNewКлассJava.setOnAction( this::onActionNewКлассJava );
        menuNewКлассJava.disableProperty().bind( disableNewКлассJava );
        
        MenuItem menuNewКонтакт = new MenuItem(
                LOGGER.text( "cell.pin" ), icon( "icons16x16/pin.png" ) );
        menuNewКонтакт.setOnAction( this::onActionNewКонтакт );
        menuNewКонтакт.disableProperty().bind( disableNewКонтакт );
        
        MenuItem menuNewМодуль = new MenuItem(
                LOGGER.text( "cell.module" ), icon( "icons16x16/module.png" ) );
        menuNewМодуль.setOnAction( this::onActionNewМодуль );
        menuNewМодуль.disableProperty().bind( disableNewМодуль );
        
        MenuItem menuNewПакет = new MenuItem(
                LOGGER.text( "cell.package" ), icon( "icons16x16/file-xml.png" ) );
        menuNewПакет.setOnAction( this::onActionNewПакет );
        menuNewПакет.disableProperty().bind( disableNewПакет );
        
        MenuItem menuNewПоле = new MenuItem(
                LOGGER.text( "cell.field" ), icon( "icons16x16/field2.png" ) );
        menuNewПоле.setOnAction( this::onActionNewПоле );
        menuNewПоле.disableProperty().bind( disableNewПоле );
        
        MenuItem menuNewПроект = new MenuItem(
                LOGGER.text( "cell.project" ), icon( "icons16x16/new-project.png" ) );
        menuNewПроект.setOnAction( this::onActionNewПроект );
        menuNewПроект.disableProperty().bind( disableNewПроект );
        
        MenuItem menuNewПроцессор = new MenuItem(
                LOGGER.text( "cell.processor" ), icon( "icons16x16/processor2.png" ) );
        menuNewПроцессор.setOnAction( this::onActionNewПроцессор );
        menuNewПроцессор.disableProperty().bind( disableNewПроцессор );
        
        MenuItem menuNewСигнал = new MenuItem(
                LOGGER.text( "cell.signal" ), icon( "icons16x16/signal.png" ) );
        menuNewСигнал.setOnAction( this::onActionNewСигнал );
        menuNewСигнал.disableProperty().bind( disableNewСигнал );
        
        MenuItem menuNewСоединение = new MenuItem(
                LOGGER.text( "cell.connector" ), icon( "icons16x16/connector.png" ) );
        menuNewСоединение.setOnAction( this::onActionNewСоединение );
        menuNewСоединение.disableProperty().bind( disableNewСоединение );
        
        MenuItem menuNewТекстовыйБлок = new MenuItem(
                LOGGER.text( "cell.text" ), icon( "icons16x16/file.png" ) );
        menuNewТекстовыйБлок.setOnAction( this::onActionNewТекстовыйБлок );
        menuNewТекстовыйБлок.disableProperty().bind( disableNewТекстовыйБлок );
        
        MenuItem menuNewТочка = new MenuItem(
                LOGGER.text( "cell.point" ), icon( "icons16x16/point.png" ) );
        menuNewТочка.setOnAction( this::onActionNewТочка );
        menuNewТочка.disableProperty().bind( disableNewТочка );
        
        MenuItem menuNewФрагмент = new MenuItem(
                LOGGER.text( "cell.fragment" ), icon( "icons16x16/fragment.png" ) );
        menuNewФрагмент.setOnAction( this::onActionNewФрагмент );
        menuNewФрагмент.disableProperty().bind( disableNewФрагмент );
        
        MenuItem menuNewXmlNameSpace = new MenuItem(
                LOGGER.text( "cell.namespace" ) );//, icon( "icons16x16/.png" ) );
        menuNewXmlNameSpace.setOnAction( this::onActionNewXmlNameSpace );
        menuNewXmlNameSpace.disableProperty().bind( disableNewXmlNameSpace );
        
        Menu menu = new Menu( LOGGER.text( "archive.action.new" ) );
        menu.getItems().addAll
        (
                menuNewАрхив,
                menuNewПакет,
                new SeparatorMenuItem(),
                menuNewПроект,
                menuNewБиблиотека,
                new SeparatorMenuItem(),
                menuNewФрагмент,
                menuNewСигнал,
                menuNewСоединение,
                menuNewКонтакт,
                new SeparatorMenuItem(),
                menuNewМодуль,
                menuNewПоле,
                menuNewПроцессор,
                new SeparatorMenuItem(),
                menuNewТочка,
                new SeparatorMenuItem(),
                menuNewЗаметка,
                menuNewИнструкция,
                menuNewКлассJava,
                menuNewТекстовыйБлок,
                new SeparatorMenuItem(),
                menuNewXmlNameSpace
        );
        return menu;
    }
    
    @FXML
    private void onActionNewАрхив( ActionEvent event )
    {
            LOGGER.log( Level.WARNING, "Unable to create {0}", "" );
        event.consume();
    }
    
    @FXML
    private void onActionNewБиблиотека( ActionEvent event )
    {
            LOGGER.log( Level.WARNING, "Unable to create {0} for {1}.", "", "" );
        event.consume();
    }
    
    @FXML
    private void onActionNewЗаметка( ActionEvent event )
    {
            LOGGER.log( Level.WARNING, "Unable to create {0} for {1}.", "", "" );
        event.consume();
    }
    
    @FXML
    private void onActionNewИнструкция( ActionEvent event )
    {
            LOGGER.log( Level.WARNING, "Unable to create {0} for {1}.", "", "" );
        event.consume();
    }
    
    @FXML
    private void onActionNewКлассJava( ActionEvent event )
    {
            LOGGER.log( Level.WARNING, "Unable to create {0} for {1}.", "", "" );
        event.consume();
    }
    
    @FXML
    private void onActionNewКонтакт( ActionEvent event )
    {
            LOGGER.log( Level.WARNING, "Unable to create {0} for {1}.", "", "" );
        event.consume();
    }
    
    @FXML
    private void onActionNewМодуль( ActionEvent event )
    {
            LOGGER.log( Level.WARNING, "Unable to create {0} for {1}.", "", "" );
        event.consume();
    }
    
    @FXML
    private void onActionNewПакет( ActionEvent event )
    {
//        Архив архив = JavaFX.getInstance().контекст.архив; //TODO other object types?
//        JavaFX.getInstance().execute( new СоздатьНовыйПакет(), архив );
//        Атрибутный value = s.get(0).getValue();
//        return !( value instanceof Архив || value instanceof Пакет );
            LOGGER.log( Level.WARNING, "Unable to create {0} for {1}.", "", "" );
        event.consume();
    }
    
    @FXML
    private void onActionNewПоле( ActionEvent event )
    {
            LOGGER.log( Level.WARNING, "Unable to create {0} for {1}.", "", "" );
        event.consume();
    }
    
    @FXML
    private void onActionNewПроект( ActionEvent event )
    {
            LOGGER.log( Level.WARNING, "Unable to create {0} for {1}.", "", "" );
        event.consume();
    }
    
    @FXML
    private void onActionNewПроцессор( ActionEvent event )
    {
            LOGGER.log( Level.WARNING, "Unable to create {0} for {1}.", "", "" );
        event.consume();
    }
    
    @FXML
    private void onActionNewСигнал( ActionEvent event )
    {
            LOGGER.log( Level.WARNING, "Unable to create {0} for {1}.", "", "" );
        event.consume();
    }
    
    @FXML
    private void onActionNewСоединение( ActionEvent event )
    {
            LOGGER.log( Level.WARNING, "Unable to create {0} for {1}.", "", "" );
        event.consume();
    }
    
    @FXML
    private void onActionNewТекстовыйБлок( ActionEvent event )
    {
            LOGGER.log( Level.WARNING, "Unable to create {0} for {1}.", "", "" );
        event.consume();
    }
    
    @FXML
    private void onActionNewТочка( ActionEvent event )
    {
            LOGGER.log( Level.WARNING, "Unable to create {0} for {1}.", "", "" );
        event.consume();
    }
    
    @FXML
    private void onActionNewФрагмент( ActionEvent event )
    {
            LOGGER.log( Level.WARNING, "Unable to create {0} for {1}.", "", "" );
        event.consume();
    }
    
    @FXML
    private void onActionNewXmlNameSpace( ActionEvent event )
    {
            LOGGER.log( Level.WARNING, "Unable to create {0} for {1}.", "", "" );
        event.consume();
    }

    ListProperty<Атрибутный> selectionProperty() { return selection; }
    
    public BooleanProperty disableNewАрхивProperty() { return disableNewАрхив; }
    public BooleanProperty disableNewПакетProperty() { return disableNewПакет; }
    public BooleanProperty disableNewПроектProperty() { return disableNewПроект; }
    public BooleanProperty disableNewБиблиотекаProperty() { return disableNewБиблиотека; }
    public BooleanProperty disableNewФрагментProperty() { return disableNewФрагмент; }
    public BooleanProperty disableNewСигналProperty() { return disableNewСигнал; }
    public BooleanProperty disableNewСоединениеProperty() { return disableNewСоединение; }
    public BooleanProperty disableNewКонтактProperty() { return disableNewКонтакт; }
    public BooleanProperty disableNewМодульProperty() { return disableNewМодуль; }
    public BooleanProperty disableNewПолеProperty() { return disableNewПоле; }
    public BooleanProperty disableNewПроцессорProperty() { return disableNewПроцессор; }
    public BooleanProperty disableNewТочкаProperty() { return disableNewТочка; }
    public BooleanProperty disableNewЗаметкаProperty() { return disableNewЗаметка; }
    public BooleanProperty disableNewИнструкцияProperty() { return disableNewИнструкция; }
    public BooleanProperty disableNewКлассJavaProperty() { return disableNewКлассJava; }
    public BooleanProperty disableNewТекстовыйБлокProperty() { return disableNewТекстовыйБлок; }
    public BooleanProperty disableNewXmlNameSpaceProperty() { return disableNewXmlNameSpace; }
    
    public boolean getDisableNewАрхив() { return disableNewАрхив.get(); }
    public boolean getDisableNewПакет() { return disableNewПакет.get(); }
    public boolean getDisableNewПроект() { return disableNewПроект.get(); }
    public boolean getDisableNewБиблиотека() { return disableNewБиблиотека.get(); }
    public boolean getDisableNewФрагмент() { return disableNewФрагмент.get(); }
    public boolean getDisableNewСигнал() { return disableNewСигнал.get(); }
    public boolean getDisableNewСоединение() { return disableNewСоединение.get(); }
    public boolean getDisableNewКонтакт() { return disableNewКонтакт.get(); }
    public boolean getDisableNewМодуль() { return disableNewМодуль.get(); }
    public boolean getDisableNewПоле() { return disableNewПоле.get(); }
    public boolean getDisableNewПроцессор() { return disableNewПроцессор.get(); }
    public boolean getDisableNewТочка() { return disableNewТочка.get(); }
    public boolean getDisableNewЗаметка() { return disableNewЗаметка.get(); }
    public boolean getDisableNewИнструкция() { return disableNewИнструкция.get(); }
    public boolean getDisableNewКлассJava() { return disableNewКлассJava.get(); }
    public boolean getDisableNewТекстовыйБлок() { return disableNewТекстовыйБлок.get(); }
    public boolean getDisableNewXmlNameSpace() { return disableNewXmlNameSpace.get(); }

    private boolean disableActionNewАрхив()
    {
        return false;
    }
    
    private boolean disableActionNewПакет()
    {
        return false;
    }
    
    private boolean disableActionNewПроект()
    {
        return false;
    }
    
    private boolean disableActionNewБиблиотека()
    {
        return false;
    }
    
    private boolean disableActionNewФрагмент()
    {
        return false;
    }
    
    private boolean disableActionNewСигнал()
    {
        return false;
    }
    
    private boolean disableActionNewСоединение()
    {
        return false;
    }
    
    private boolean disableActionNewКонтакт()
    {
        return false;
    }
    
    private boolean disableActionNewМодуль()
    {
        return false;
    }
    
    private boolean disableActionNewПоле()
    {
        return false;
    }
    
    private boolean disableActionNewПроцессор()
    {
        return false;
    }
    
    private boolean disableActionNewТочка()
    {
        return false;
    }
    
    private boolean disableActionNewЗаметка()
    {
        return false;
    }
    
    private boolean disableActionNewИнструкция()
    {
        return false;
    }
    
    private boolean disableActionNewКлассJava()
    {
        return false;
    }
    
    private boolean disableActionNewТекстовыйБлок()
    {
        return false;
    }
    
    private boolean disableActionNewXmlNameSpace()
    {
        return false;
    }
    
}
