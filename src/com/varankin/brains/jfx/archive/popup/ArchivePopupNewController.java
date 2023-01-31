package com.varankin.brains.jfx.archive.popup;

import com.varankin.brains.jfx.archive.action.ActionProcessor;
import com.varankin.util.LoggerX;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.util.Builder;

import static com.varankin.brains.jfx.JavaFX.icon;

/**
 * FXML-контроллер контекстного меню новых элементов навигатора по архиву.
 * 
 * @author &copy; 2023 Николай Варанкин
 */
public final class ArchivePopupNewController implements Builder<Menu>
{
    private static final LoggerX LOGGER = LoggerX.getLogger( ArchivePopupNewController.class );
    //private static final String RESOURCE_CSS  = "/fxml/archive/ArchivePopupNew.css";
    //private static final String CSS_CLASS = "archive-popup-new";
    
    //public static final String RESOURCE_FXML  = "/fxml/archive/ArchivePopupNew.fxml";
    //public static final ResourceBundle RESOURCE_BUNDLE = LOGGER.getLogger().getResourceBundle();

    private ActionProcessor processor;
    
    @FXML private final BooleanProperty
        disableNewПакет, disableNewПараметр,
        disableNewПроект, disableNewБиблиотека, disableNewФрагмент, 
        disableNewСенсор, disableNewСигнал, disableNewСоединение, disableNewКонтакт, 
        disableNewЛента, disableNewМодуль, disableNewРасчет, disableNewПоле, disableNewПроцессор, 
        disableNewТочка, disableNewЗаметка, disableNewИнструкция, 
        disableNewКлассJava, disableNewТекстовыйБлок, disableNewXmlNameSpace;

    public ArchivePopupNewController()
    {
        disableNewПакет = new SimpleBooleanProperty( this, "disableNewПакет" );
        disableNewПараметр = new SimpleBooleanProperty( this, "disableNewПараметр" );
        disableNewПроект = new SimpleBooleanProperty( this, "disableNewПроект" );
        disableNewБиблиотека = new SimpleBooleanProperty( this, "disableNewБиблиотека" );
        disableNewФрагмент = new SimpleBooleanProperty( this, "disableNewФрагмент" );
        disableNewСенсор = new SimpleBooleanProperty( this, "disableNewСенсор" );
        disableNewСигнал = new SimpleBooleanProperty( this, "disableNewСигнал" );
        disableNewСоединение = new SimpleBooleanProperty( this, "disableNewСоединение" );
        disableNewКонтакт = new SimpleBooleanProperty( this, "disableNewКонтакт" );
        disableNewЛента = new SimpleBooleanProperty( this, "disableNewЛента" );
        disableNewМодуль = new SimpleBooleanProperty( this, "disableNewМодуль" );
        disableNewРасчет = new SimpleBooleanProperty( this, "disableNewРасчет" );
        disableNewПоле = new SimpleBooleanProperty( this, "disableNewПоле" );
        disableNewПроцессор = new SimpleBooleanProperty( this, "disableNewПроцессор" );
        disableNewТочка = new SimpleBooleanProperty( this, "disableNewТочка" );
        disableNewЗаметка = new SimpleBooleanProperty( this, "disableNewЗаметка" );
        disableNewИнструкция = new SimpleBooleanProperty( this, "disableNewИнструкция" );
        disableNewКлассJava = new SimpleBooleanProperty( this, "disableNewКлассJava" );
        disableNewТекстовыйБлок = new SimpleBooleanProperty( this, "disableNewТекстовыйБлок" );
        disableNewXmlNameSpace = new SimpleBooleanProperty( this, "disableNewXmlNameSpace" );
    }

    /**
     * Создает меню. 
     * Применяется в конфигурации без FXML.
     * 
     * @return меню. 
     */
    @Override
    public Menu build()
    {
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
        
        MenuItem menuNewЛента = new MenuItem(
                LOGGER.text( "cell.timeline" ), icon( "icons16x16/timeline.png" ) );
        menuNewЛента.setOnAction( this::onActionNewЛента );
        menuNewЛента.disableProperty().bind( disableNewЛента );
        
        MenuItem menuNewМодуль = new MenuItem(
                LOGGER.text( "cell.module" ), icon( "icons16x16/module.png" ) );
        menuNewМодуль.setOnAction( this::onActionNewМодуль );
        menuNewМодуль.disableProperty().bind( disableNewМодуль );
        
        MenuItem menuNewРасчет = new MenuItem(
                LOGGER.text( "cell.compute" ), icon( "icons16x16/compute.png" ) );
        menuNewРасчет.setOnAction( this::onActionNewРасчет );
        menuNewРасчет.disableProperty().bind( disableNewРасчет );
        
        MenuItem menuNewПакет = new MenuItem(
                LOGGER.text( "cell.package" ), icon( "icons16x16/file-xml.png" ) );
        menuNewПакет.setOnAction( this::onActionNewПакет );
        menuNewПакет.disableProperty().bind( disableNewПакет );
        
        MenuItem menuNewПараметр = new MenuItem(
                LOGGER.text( "cell.parameter" ), icon( "icons16x16/parameter.png" ) );
        menuNewПараметр.setOnAction( this::onActionNewПараметр );
        menuNewПараметр.disableProperty().bind( disableNewПараметр );
        
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
        
        MenuItem menuNewСенсор = new MenuItem(
                LOGGER.text( "cell.sensor" ), icon( "icons16x16/sensor.png" ) );
        menuNewСенсор.setOnAction( this::onActionNewСенсор );
        menuNewСенсор.disableProperty().bind( disableNewСенсор );
        
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
                menuNewПакет,
                new SeparatorMenuItem(),
                menuNewПроект,
                menuNewБиблиотека,
                new SeparatorMenuItem(),
                menuNewФрагмент,
                menuNewСигнал,
                menuNewСенсор,
                menuNewСоединение,
                menuNewКонтакт,
                new SeparatorMenuItem(),
                menuNewМодуль,
                menuNewПоле,
                menuNewРасчет,
                menuNewЛента,
                menuNewПроцессор,
                new SeparatorMenuItem(),
                menuNewТочка,
                new SeparatorMenuItem(),
                menuNewПараметр,
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
    private void onActionNewБиблиотека( ActionEvent event )
    {
        processor.onActionNewБиблиотека();
        event.consume();
    }
    
    @FXML
    private void onActionNewЗаметка( ActionEvent event )
    {
        processor.onActionNewЗаметка();
        event.consume();
    }
    
    @FXML
    private void onActionNewИнструкция( ActionEvent event )
    {
        processor.onActionNewИнструкция();
        event.consume();
    }
    
    @FXML
    private void onActionNewКлассJava( ActionEvent event )
    {
        processor.onActionNewКлассJava();
        event.consume();
    }
    
    @FXML
    private void onActionNewКонтакт( ActionEvent event )
    {
        processor.onActionNewКонтакт();
        event.consume();
    }
    
    @FXML
    private void onActionNewЛента( ActionEvent event )
    {
        processor.onActionNewЛента();
        event.consume();
    }
    
    @FXML
    private void onActionNewМодуль( ActionEvent event )
    {
        processor.onActionNewМодуль();
        event.consume();
    }
    
    @FXML
    private void onActionNewРасчет( ActionEvent event )
    {
        processor.onActionNewРасчет();
        event.consume();
    }
    
    @FXML
    private void onActionNewПакет( ActionEvent event )
    {
        processor.onActionNewПакет();
        event.consume();
    }
    
    @FXML
    private void onActionNewПараметр( ActionEvent event )
    {
        processor.onActionNewПараметр();
        event.consume();
    }
    
    @FXML
    private void onActionNewПоле( ActionEvent event )
    {
        processor.onActionNewПоле();
        event.consume();
    }
    
    @FXML
    private void onActionNewПроект( ActionEvent event )
    {
        processor.onActionNewПроект();
        event.consume();
    }
    
    @FXML
    private void onActionNewПроцессор( ActionEvent event )
    {
        processor.onActionNewПроцессор();
        event.consume();
    }
    
    @FXML
    private void onActionNewСенсор( ActionEvent event )
    {
        processor.onActionNewСенсор();
        event.consume();
    }
    
    @FXML
    private void onActionNewСигнал( ActionEvent event )
    {
        processor.onActionNewСигнал();
        event.consume();
    }
    
    @FXML
    private void onActionNewСоединение( ActionEvent event )
    {
        processor.onActionNewСоединение();
        event.consume();
    }
    
    @FXML
    private void onActionNewТекстовыйБлок( ActionEvent event )
    {
        processor.onActionNewТекстовыйБлок();
        event.consume();
    }
    
    @FXML
    private void onActionNewТочка( ActionEvent event )
    {
        processor.onActionNewТочка();
        event.consume();
    }
    
    @FXML
    private void onActionNewФрагмент( ActionEvent event )
    {
        processor.onActionNewФрагмент();
        event.consume();
    }
    
    @FXML
    private void onActionNewXmlNameSpace( ActionEvent event )
    {
        processor.onActionNewXmlNameSpace();
        event.consume();
    }

    public BooleanProperty disableNewПакетProperty() { return disableNewПакет; }
    public BooleanProperty disableNewПроектProperty() { return disableNewПроект; }
    public BooleanProperty disableNewБиблиотекаProperty() { return disableNewБиблиотека; }
    public BooleanProperty disableNewФрагментProperty() { return disableNewФрагмент; }
    public BooleanProperty disableNewСигналProperty() { return disableNewСигнал; }
    public BooleanProperty disableNewСоединениеProperty() { return disableNewСоединение; }
    public BooleanProperty disableNewКонтактProperty() { return disableNewКонтакт; }
    public BooleanProperty disableNewЛентаProperty() { return disableNewЛента; }
    public BooleanProperty disableNewМодульProperty() { return disableNewМодуль; }
    public BooleanProperty disableNewПолеProperty() { return disableNewПоле; }
    public BooleanProperty disableNewПроцессорProperty() { return disableNewПроцессор; }
    public BooleanProperty disableNewТочкаProperty() { return disableNewТочка; }
    public BooleanProperty disableNewЗаметкаProperty() { return disableNewЗаметка; }
    public BooleanProperty disableNewИнструкцияProperty() { return disableNewИнструкция; }
    public BooleanProperty disableNewКлассJavaProperty() { return disableNewКлассJava; }
    public BooleanProperty disableNewТекстовыйБлокProperty() { return disableNewТекстовыйБлок; }
    public BooleanProperty disableNewXmlNameSpaceProperty() { return disableNewXmlNameSpace; }
    
    public boolean getDisableNewПакет() { return disableNewПакет.get(); }
    public boolean getDisableNewПроект() { return disableNewПроект.get(); }
    public boolean getDisableNewБиблиотека() { return disableNewБиблиотека.get(); }
    public boolean getDisableNewФрагмент() { return disableNewФрагмент.get(); }
    public boolean getDisableNewСигнал() { return disableNewСигнал.get(); }
    public boolean getDisableNewСоединение() { return disableNewСоединение.get(); }
    public boolean getDisableNewКонтакт() { return disableNewКонтакт.get(); }
    public boolean getDisableNewЛента() { return disableNewЛента.get(); }
    public boolean getDisableNewМодуль() { return disableNewМодуль.get(); }
    public boolean getDisableNewПоле() { return disableNewПоле.get(); }
    public boolean getDisableNewПроцессор() { return disableNewПроцессор.get(); }
    public boolean getDisableNewТочка() { return disableNewТочка.get(); }
    public boolean getDisableNewЗаметка() { return disableNewЗаметка.get(); }
    public boolean getDisableNewИнструкция() { return disableNewИнструкция.get(); }
    public boolean getDisableNewКлассJava() { return disableNewКлассJava.get(); }
    public boolean getDisableNewТекстовыйБлок() { return disableNewТекстовыйБлок.get(); }
    public boolean getDisableNewXmlNameSpace() { return disableNewXmlNameSpace.get(); }

    void setProcessor( ActionProcessor processor )
    {
        this.processor = processor;
        
        if( processor == null ) return;
        disableNewПакет.bind( processor.disableNewПакетProperty() );
        disableNewПараметр.bind( processor.disableNewПараметрProperty() );
        disableNewПроект.bind( processor.disableNewПроектProperty() );
        disableNewБиблиотека.bind( processor.disableNewБиблиотекаProperty() );
        disableNewФрагмент.bind( processor.disableNewФрагментProperty() );
        disableNewСенсор.bind( processor.disableNewСенсорProperty() );
        disableNewСигнал.bind( processor.disableNewСигналProperty() );
        disableNewСоединение.bind( processor.disableNewСоединениеProperty() );
        disableNewКонтакт.bind( processor.disableNewКонтактProperty() );
        disableNewЛента.bind( processor.disableNewЛентаProperty() );
        disableNewМодуль.bind( processor.disableNewМодульProperty() );
        disableNewПоле.bind( processor.disableNewПолеProperty() );
        disableNewПроцессор.bind( processor.disableNewПроцессорProperty() );
        disableNewРасчет.bind( processor.disableNewРасчетProperty() );
        disableNewТочка.bind( processor.disableNewТочкаProperty() );
        disableNewЗаметка.bind( processor.disableNewЗаметкаProperty() );
        disableNewИнструкция.bind( processor.disableNewИнструкцияProperty() );
        disableNewКлассJava.bind( processor.disableNewКлассJavaProperty() );
        disableNewТекстовыйБлок.bind( processor.disableNewТекстовыйБлокProperty() );
        disableNewXmlNameSpace.bind( processor.disableNewXmlNameSpaceProperty() );
    }

}
