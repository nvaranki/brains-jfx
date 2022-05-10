package com.varankin.brains.jfx.browser;

import com.varankin.biz.action.Действие;
import com.varankin.brains.appl.ВыгрузитьПроект;
import com.varankin.brains.appl.ДействияПоПорядку;
import com.varankin.brains.appl.УправлениеПроцессом;
import com.varankin.brains.artificial.async.Процесс;
import com.varankin.brains.artificial.Проект;
import com.varankin.brains.artificial.Элемент;
import com.varankin.brains.factory.Вложенный;
import com.varankin.brains.jfx.ApplicationActionWorker;
import com.varankin.brains.jfx.BuilderFX;
import com.varankin.brains.jfx.JavaFX;

import java.util.List;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.SimpleListProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.*;
import static javafx.beans.binding.Bindings.createBooleanBinding;

/**
 * Основа FXML-контроллера для набора инструментов навигатора по проектам.
 * 
 * @author &copy; 2019 Николай Варанкин
 */
abstract class AbstractActionController 
{
    private static final Logger LOGGER = Logger.getLogger(
            AbstractActionController.class.getName(),
            AbstractActionController.class.getPackage().getName() + ".text" );
    private static final Predicate<Элемент> ФИЛЬТР_ПРОЦЕСС = i -> Вложенный.процесс( i ) != null;

    private final ListProperty<Элемент>  selection;
    private final ReadOnlyBooleanWrapper disableStart;
    private final ReadOnlyBooleanWrapper disablePause;
    private final ReadOnlyBooleanWrapper disableStop;
    private final ReadOnlyBooleanWrapper disableRemove;
    private final ReadOnlyBooleanWrapper disableProperties;

    AbstractActionController() 
    {
        selection = new SimpleListProperty<>( this, "selection" );
        
        disableStart      = new ReadOnlyBooleanWrapper( this, "disableStart" );
        disablePause      = new ReadOnlyBooleanWrapper( this, "disablePause" );
        disableStop       = new ReadOnlyBooleanWrapper( this, "disableStop" );
        disableRemove     = new ReadOnlyBooleanWrapper( this, "disableRemove" );
        disableProperties = new ReadOnlyBooleanWrapper( this, "disableProperties" );
        
        disableStart     .bind( createBooleanBinding( () -> !enableActionStart(), selection ) );
        disablePause     .bind( createBooleanBinding( () -> !enableActionPause(), selection ) );
        disableStop      .bind( createBooleanBinding( () -> !enableActionStop(), selection ) );
        disableRemove    .bind( createBooleanBinding( () -> !enableActionRemove(), selection ) );
        disableProperties.bind( createBooleanBinding( () -> !enableActionProperties(), selection ) );
    }

    ListProperty<Элемент> selectionProperty() { return selection; }
    
    public ReadOnlyBooleanProperty disableStartProperty()      { return disableStart.getReadOnlyProperty(); }
    public ReadOnlyBooleanProperty disablePauseProperty()      { return disablePause.getReadOnlyProperty(); }
    public ReadOnlyBooleanProperty disableStopProperty()       { return disableStop.getReadOnlyProperty(); }
    public ReadOnlyBooleanProperty disableRemoveProperty()     { return disableRemove.getReadOnlyProperty(); }
    public ReadOnlyBooleanProperty disablePropertiesProperty() { return disableProperties.getReadOnlyProperty(); }

    public boolean getDisableStart()      { return disableStart.get(); }
    public boolean getDisablePause()      { return disablePause.get(); }
    public boolean getDisableStop()       { return disableStop.get(); }
    public boolean getDisableRemove()     { return disableRemove.get(); }
    public boolean getDisableProperties() { return disableProperties.get(); }

    private void onChangeProcesses( УправлениеПроцессом.Команда команда )
    {
        Действие<List<Процесс>> действие = new ДействияПоПорядку( ДействияПоПорядку.Приоритет.КОНТЕКСТ, 
                new УправлениеПроцессом( JavaFX.getInstance().контекст, команда ) );
        List<Процесс> ceлектор = selection.stream()
            .map( Вложенный::процесс )
            .filter( i -> i != null )
            .collect( Collectors.toList() );
        new ApplicationActionWorker<>( действие, ceлектор ).execute( JavaFX.getInstance() );
    }
    
    private Stage buildProperties()
    {
        BuilderFX<Parent,BrowserPropertiesController> builder = new BuilderFX<>();
        builder.init( BrowserPropertiesController.class,
                BrowserPropertiesController.RESOURCE_FXML, 
                BrowserPropertiesController.RESOURCE_BUNDLE );
        BrowserPropertiesController controller = builder.getController();

        Stage stage = new Stage();
        stage.initStyle( StageStyle.DECORATED );
        stage.initModality( Modality.NONE );
        stage.initOwner( JavaFX.getInstance().платформа );
        stage.getIcons().add( JavaFX.icon( "icons16x16/properties.png" ).getImage() );

        stage.setResizable( true );
        stage.setMinHeight( 350d );
        stage.setMinWidth( 400d );
        stage.setHeight( 350d ); //TODO save/restore size&pos
        stage.setWidth( 400d );
        stage.setScene( new Scene( builder.getNode() ) );
        stage.setOnShowing( controller::populate );
        stage.setOnHidden( controller::onHidden );

        stage.titleProperty().bind( controller.titleProperty() );

        return stage;
    }

    @FXML
    void onActionStart( ActionEvent event )
    {
        onChangeProcesses( УправлениеПроцессом.Команда.СТАРТ );
        event.consume();
    }
    
    @FXML
    void onActionPause( ActionEvent event )
    {
        onChangeProcesses( УправлениеПроцессом.Команда.ПАУЗА );
        event.consume();
    }
    
    @FXML
    void onActionStop( ActionEvent event )
    {
        onChangeProcesses( УправлениеПроцессом.Команда.СТОП );
        event.consume();
    }
    
    @FXML
    void onActionRemove( ActionEvent event )
    {
        УправлениеПроцессом управлениеПроцессом = new УправлениеПроцессом( JavaFX.getInstance().контекст, УправлениеПроцессом.Команда.СТОП );
        Действие<Проект> управлениеПроектом = проект -> управлениеПроцессом.выполнить( Вложенный.процесс( проект ) );
        Действие<List<Проект>> действие = new ДействияПоПорядку( ДействияПоПорядку.Приоритет.КОНТЕКСТ, 
                управлениеПроектом,
                new ВыгрузитьПроект( JavaFX.getInstance().контекст ) );
        List<Проект> ceлектор = selection.stream()
            .filter(  ( Элемент i ) -> i instanceof Проект )
            .map( ( Элемент i ) -> (Проект)i )
            .collect( Collectors.toList() );
        new ApplicationActionWorker<>( действие, ceлектор ).execute( JavaFX.getInstance() );
        selection.clear();
        event.consume();
    }
    
    @FXML
    void onActionProperties( ActionEvent event )
    {
//        Действие<List<Элемент>> действие = null; //TODO NOT IMPL.
//        List<Элемент> ceлектор = selection;/*.stream()
//            .filter(  ( Элемент i ) -> i instanceof Проект )
//            .flatMap( ( Элемент i ) -> Stream.of( (Проект)i ) )
//            .collect( Collectors.toList() );*/
//        LOGGER.info( "Sorry, the command is not implemented." );//TODO not impl.
//        if( selection.isEmpty() )
//            LOGGER.log( "002005005I" );
//        else if( selection.size() > 1 )
//            LOGGER.log( "002005006I", selection.size() );
//        else
        {
            Stage properties;
            /*if( properties == null )*/ properties = buildProperties();
            properties.getScene().getRoot().setUserData( selection.get( 0 ) );
            properties.show();
            properties.toFront();
        }
        event.consume();
    }
    
    private boolean enableActionStart() 
    {
        return !selection.isEmpty() && selection.stream().allMatch( ФИЛЬТР_ПРОЦЕСС );
    }

    private boolean enableActionPause() 
    {
        return !selection.isEmpty() && selection.stream().allMatch( ФИЛЬТР_ПРОЦЕСС );
    }

    private boolean enableActionStop() 
    {
        return !selection.isEmpty() && selection.stream().allMatch( ФИЛЬТР_ПРОЦЕСС );
    }

    private boolean enableActionRemove() 
    {
        return !selection.isEmpty() && selection.stream()
                .allMatch( ( Элемент i ) -> i instanceof Проект );
    }

    private boolean enableActionProperties() 
    {
        return selection.size() == 1;
    }

    static String text( String ключ )
    {
        return LOGGER.getResourceBundle().getString( ключ );
    }

}
