package com.varankin.brains.jfx.archive;

import com.varankin.biz.action.Действие;
import com.varankin.brains.appl.ДействияПоПорядку;
import com.varankin.brains.appl.ДействияПоПорядку.Приоритет;
import com.varankin.brains.appl.ЗагрузитьАрхивныйПроект;
import com.varankin.brains.appl.Импортировать;
import com.varankin.brains.appl.УдалитьИзАрхива;
import com.varankin.brains.appl.ЭкспортироватьSvg;
import com.varankin.brains.db.*;
import com.varankin.brains.db.factory.DbФабрикаКомпозитныхЭлементов;
import com.varankin.brains.jfx.*;
import com.varankin.io.container.Provider;
import com.varankin.util.LoggerX;
import java.io.File;
import java.io.InputStream;
import java.util.*;
import java.util.function.Predicate;
import java.util.logging.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javafx.beans.binding.*;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.web.WebView;
import javafx.stage.*;
import javafx.util.Builder;

import static com.varankin.brains.jfx.JavaFX.icon;
import static javafx.beans.binding.Bindings.bindContentBidirectional;
import static javafx.beans.binding.Bindings.createBooleanBinding;

/**
 * FXML-контроллер навигатора по архиву. 
 * 
 * @author &copy; 2014 Николай Варанкин
 */
public final class ArchiveController implements Builder<TitledPane>
{
    private static final LoggerX LOGGER = LoggerX.getLogger( ArchiveController.class );
    private static final String RESOURCE_CSS  = "/fxml/archive/Archive.css";
    private static final String CSS_CLASS = "archive";
    private static final Действие<Проект> действиеЗагрузитьПроект 
        = new ЗагрузитьАрхивныйПроект( JavaFX.getInstance().контекст, 
            DbФабрикаКомпозитныхЭлементов.class );
    private static final Импортировать импортировать
        = new Импортировать( JavaFX.getInstance().контекст );

    public static final String RESOURCE_FXML  = "/fxml/archive/Archive.fxml";
    public static final ResourceBundle RESOURCE_BUNDLE = LOGGER.getLogger().getResourceBundle();

    private final SelectionListBinding selection;
    
    private Stage properties;
    private Provider<File> fileProviderExport;
    
    @FXML private final BooleanBinding  //TODO RT-37820
            disableNew, disableLoad, 
            disablePreview, disableEdit, disableRemove, 
            disableNewАрхив, disableNewПакет, 
            disableNewПроект, disableNewБиблиотека, disableNewФрагмент, 
            disableNewСигнал, disableNewСоединение, disableNewКонтакт, 
            disableNewМодуль, disableNewПоле, disableNewПроцессор, 
            disableNewТочка, disableNewЗаметка, disableNewИнструкция, 
            disableNewКлассJava, disableNewТекстовыйБлок, disableNewXmlNameSpace, 
            disableProperties, 
            disableImportFile, disableImportNet, disableExportXml, disableExportPic;
    @FXML private TreeView<Атрибутный> tree;

    public ArchiveController()
    {
        selection = new SelectionListBinding();
        disableNew = createBooleanBinding( () -> disableActionNew(), selection );
        disableLoad = createBooleanBinding( () -> disableActionLoad(), selection );
        disablePreview = createBooleanBinding( () -> disableActionPreview(), selection );
        disableEdit = createBooleanBinding( () -> disableActionEdit(), selection );
        disableRemove = createBooleanBinding( () -> disableActionRemove(), selection );
        disableNewАрхив = createBooleanBinding( () -> disableActionNewАрхив(), selection );
        disableNewПакет = createBooleanBinding( () -> disableActionNewПакет(), selection );
        disableNewПроект = createBooleanBinding( () -> disableActionNewПроект(), selection );
        disableNewБиблиотека = createBooleanBinding( () -> disableActionNewБиблиотека(), selection );
        disableNewФрагмент = createBooleanBinding( () -> disableActionNewФрагмент(), selection );
        disableNewСигнал = createBooleanBinding( () -> disableActionNewСигнал(), selection );
        disableNewСоединение = createBooleanBinding( () -> disableActionNewСоединение(), selection );
        disableNewКонтакт = createBooleanBinding( () -> disableActionNewКонтакт(), selection );
        disableNewМодуль = createBooleanBinding( () -> disableActionNewМодуль(), selection );
        disableNewПоле = createBooleanBinding( () -> disableActionNewПоле(), selection );
        disableNewПроцессор = createBooleanBinding( () -> disableActionNewПроцессор(), selection );
        disableNewТочка = createBooleanBinding( () -> disableActionNewТочка(), selection );
        disableNewЗаметка = createBooleanBinding( () -> disableActionNewЗаметка(), selection );
        disableNewИнструкция = createBooleanBinding( () -> disableActionNewИнструкция(), selection );
        disableNewКлассJava = createBooleanBinding( () -> disableActionNewКлассJava(), selection );
        disableNewТекстовыйБлок = createBooleanBinding( () -> disableActionNewТекстовыйБлок(), selection );
        disableNewXmlNameSpace = createBooleanBinding( () -> disableActionNewXmlNameSpace(), selection );
        disableProperties = createBooleanBinding( () -> disableActionProperties(), selection );
        disableImportFile = createBooleanBinding( () -> disableActionImportFile(), selection );
        disableImportNet = createBooleanBinding( () -> disableActionImportNet(), selection );
        disableExportXml = createBooleanBinding( () -> disableActionExportXml(), selection );
        disableExportPic = createBooleanBinding( () -> disableActionExportPic(), selection );
    }

    /**
     * Создает панель навигатора. 
     * Применяется в конфигурации без FXML.
     * 
     * @return панель навигатора. 
     */
    @Override
    public TitledPane build()
    {
        tree = new TreeView<>();
        tree.setShowRoot( false );
        tree.setEditable( false );

        //ObservableList selection = 
        //Bindings.b
        //selection.bind( //Bindings.createObjectBinding( null, dependencies )
        
        Menu menuNew = new Menu( LOGGER.text( "archive.action.new" ) );
        menuNew.getItems().addAll( buildContextMenuNew() );
        ContextMenu menu = new ContextMenu();
        menu.getItems().addAll( buildContextMenu( menuNew ) );
        tree.setContextMenu( menu );

        ToolBar toolbar = new ToolBar();
        toolbar.setOrientation( Orientation.VERTICAL );
        toolbar.getItems().addAll( buildToolBarButtons() );
        
        Pane box = new HBox();// spacing );
        //box.setPrefWidth( 250d );
        HBox.setHgrow( tree, Priority.ALWAYS );
        box.getChildren().addAll( toolbar, tree );
        
        TitledPane pane = new TitledPane( LOGGER.text( "archive.title" ), box );
        
        pane.getStyleClass().add( CSS_CLASS );
        pane.getStylesheets().add( getClass().getResource( RESOURCE_CSS ).toExternalForm() );

        initialize();
        
        return pane;
    }

    //<editor-fold defaultstate="collapsed" desc="buildXxx()">
    
    private Collection<MenuItem>  buildContextMenu( Menu menuNew )
    {
        MenuItem menuLoad = new MenuItem(
                LOGGER.text( "archive.action.load" ), icon( "icons16x16/load.png" ) );
        menuLoad.setOnAction( this::onActionLoad );
        menuLoad.disableProperty().bind( disableLoad );
        
        MenuItem menuPreview = new MenuItem(
                LOGGER.text( "archive.action.preview" ), icon( "icons16x16/preview.png" ) );
        menuPreview.setOnAction( this::onActionPreview );
        menuPreview.disableProperty().bind( disablePreview );
        
        MenuItem menuEdit = new MenuItem(
                LOGGER.text( "archive.action.edit" ), icon( "icons16x16/edit.png" ) );
        menuEdit.setOnAction( this::onActionEdit );
        menuEdit.disableProperty().bind( disableEdit );
        
        MenuItem menuRemove = new MenuItem(
                LOGGER.text( "archive.action.remove" ), icon( "icons16x16/remove.png" ) );
        menuRemove.setOnAction( this::onActionRemove );
        menuRemove.disableProperty().bind( disableRemove );
        
        MenuItem menuImportFile = new MenuItem(
                LOGGER.text( "archive.action.import.file" ), icon( "icons16x16/file-xml.png" ) );
        menuImportFile.setOnAction( this::onActionImportFile );
        menuImportFile.disableProperty().bind( disableImportFile );
        
        MenuItem menuImportNet = new MenuItem(
                LOGGER.text( "archive.action.import.network" ), icon( "icons16x16/load-internet.png" ) );
        menuImportNet.setOnAction( this::onActionImportNet );
        menuImportNet.disableProperty().bind( disableImportNet );
        
        MenuItem menuExportXml = new MenuItem(
                LOGGER.text( "archive.action.export.xml" ), icon( "icons16x16/file-export.png" ) );
        menuExportXml.setOnAction( this::onActionExportXml );
        menuExportXml.disableProperty().bind( disableExportXml );
        
        MenuItem menuExportPic = new MenuItem(
                LOGGER.text( "archive.action.export.pic" ), icon( "icons16x16/file-export.png" ) );
        menuExportPic.setOnAction( this::onActionExportPic );
        menuExportPic.disableProperty().bind( disableExportPic );
        
        MenuItem menuProperties = new MenuItem(
                LOGGER.text( "archive.action.properties" ), icon( "icons16x16/properties.png" ) );
        menuProperties.setOnAction( this::onActionProperties );
        menuProperties.disableProperty().bind( disableProperties );
        
        return Arrays.<MenuItem>asList
                (
                        menuLoad,
                        new SeparatorMenuItem(),
                        menuPreview,
                        menuEdit,
                        menuRemove,
                        menuNew,
                        menuProperties,
                        new SeparatorMenuItem(),
                        menuImportFile,
                        menuImportNet,
                        menuExportXml,
                        menuExportPic
                );
    }
    
    private Collection<MenuItem> buildContextMenuNew()
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
        
        return Arrays.<MenuItem>asList
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
    }
    
    private Collection<Node> buildToolBarButtons()
    {
        Button buttonLoad = new Button();
        buttonLoad.setTooltip( new Tooltip( LOGGER.text( "archive.action.load" ) ) );
        buttonLoad.setGraphic( icon( "icons16x16/load.png" ) );
        buttonLoad.setOnAction( this::onActionLoad );
        buttonLoad.disableProperty().bind( disableLoad );
        
        Button buttonNew = new Button();
        buttonNew.setTooltip( new Tooltip( LOGGER.text( "archive.action.new" ) ) );
        buttonNew.setGraphic( icon( "icons16x16/new-library.png" ) );
        buttonNew.setOnAction( this::onActionNew );
        buttonNew.disableProperty().bind( disableNew );
        
        Button buttonPreview = new Button();
        buttonPreview.setTooltip( new Tooltip( LOGGER.text( "archive.action.preview" ) ) );
        buttonPreview.setGraphic( icon( "icons16x16/preview.png" ) );
        buttonPreview.setOnAction( this::onActionPreview );
        buttonPreview.disableProperty().bind( disablePreview );
        
        Button buttonEdit = new Button();
        buttonEdit.setTooltip( new Tooltip( LOGGER.text( "archive.action.edit" ) ) );
        buttonEdit.setGraphic( icon( "icons16x16/edit.png" ) );
        buttonEdit.setOnAction( this::onActionEdit );
        buttonEdit.disableProperty().bind( disableEdit );
        
        Button buttonRemove = new Button();
        buttonRemove.setTooltip( new Tooltip( LOGGER.text( "archive.action.remove" ) ) );
        buttonRemove.setGraphic( icon( "icons16x16/remove.png" ) );
        buttonRemove.setOnAction( this::onActionRemove );
        buttonRemove.disableProperty().bind( disableRemove );
        
        Button buttonImportFile = new Button();
        buttonImportFile.setTooltip( new Tooltip( LOGGER.text( "archive.action.import.file" ) ) );
        buttonImportFile.setGraphic( icon( "icons16x16/file-xml.png" ) );
        buttonImportFile.setOnAction( this::onActionImportFile );
        buttonImportFile.disableProperty().bind( disableImportFile );
        
        Button buttonImportNet = new Button();
        buttonImportNet.setTooltip( new Tooltip( LOGGER.text( "archive.action.import.network" ) ) );
        buttonImportNet.setGraphic( icon( "icons16x16/load-internet.png" ) );
        buttonImportNet.setOnAction( this::onActionImportNet );
        buttonImportNet.disableProperty().bind( disableImportNet );
        
        Button buttonExportXml = new Button();
        buttonExportXml.setTooltip( new Tooltip( LOGGER.text( "archive.action.export.xml" ) ) );
        buttonExportXml.setGraphic( icon( "icons16x16/file-export.png" ) );
        buttonExportXml.setOnAction( this::onActionExportXml );
        buttonExportXml.disableProperty().bind( disableExportXml );
        
        Button buttonExportPic = new Button();
        buttonExportPic.setTooltip( new Tooltip( LOGGER.text( "archive.action.export.pic" ) ) );
        buttonExportPic.setGraphic( icon( "icons16x16/file-export.png" ) );
        buttonExportPic.setOnAction( this::onActionExportPic );
        buttonExportPic.disableProperty().bind( disableExportPic );
        
        Button buttonProperties = new Button();
        buttonProperties.setTooltip( new Tooltip( LOGGER.text( "archive.action.properties" ) ) );
        buttonProperties.setGraphic( icon( "icons16x16/properties.png" ) );
        buttonProperties.setOnAction( this::onActionProperties );
        buttonProperties.disableProperty().bind( disableProperties );
        
        return Arrays.<Node>asList
                (
                        buttonLoad,
                        new Separator( Orientation.HORIZONTAL ),
                        buttonPreview,
                        buttonEdit,
                        buttonRemove,
                        buttonNew,
                        buttonProperties,
                        new Separator( Orientation.HORIZONTAL ),
                        buttonImportFile,
                        buttonImportNet,
                        buttonExportXml,
                        buttonExportPic
                );
    }
    
    //</editor-fold>
    
    @FXML
    protected void initialize()
    {
        Архив архив = JavaFX.getInstance().контекст.архив;
        TreeItem<Атрибутный> item = new TreeItem<>( архив );
//        архив.пакеты().наблюдатели().add( new МониторКоллекции( item.getChildren() ) );
//        архив.namespaces().наблюдатели().add( new МониторКоллекции( item.getChildren() ) );
        tree.getSelectionModel().setSelectionMode( SelectionMode.MULTIPLE );
        tree.setCellFactory( ( TreeView<Атрибутный> view ) -> new CellАтрибутный() );
        tree.setRoot( new TreeItem<>() );
        tree.getRoot().getChildren().add( item );
        selection.bind( tree.getSelectionModel().getSelectedItems() );



//        tree.addEventHandler( TreeItem.<Атрибутный>branchExpandedEvent(), 
//                new EventHandler<TreeItem.TreeModificationEvent<Атрибутный>>()
//        {
//
//            @Override
//            public void handle( TreeItem.TreeModificationEvent<Атрибутный> event )
//            {
//                event.getTreeItem();
//            }
//        });
//        tree.addEventHandler( TreeItem.<Атрибутный>branchCollapsedEvent(), 
//                new EventHandler<TreeItem.TreeModificationEvent<Атрибутный>>()
//        {
//
//            @Override
//            public void handle( TreeItem.TreeModificationEvent<Атрибутный> event )
//            {
//                event.getTreeItem();
//            }
//        });
    }
    
    //<editor-fold defaultstate="collapsed" desc="onActionXxx()">
    
    @FXML
    private void onActionNew( ActionEvent event )
    {
        List<TreeItem<Атрибутный>> s = tree.getSelectionModel().getSelectedItems();
        if( s.size() == 1 )
        {
            Атрибутный value = s.get( 0 ).getValue();
            if( value instanceof Архив )
                onActionNewПакет( event );
            else if( value instanceof Пакет )
                onActionNewПроект( event );
            else
                LOGGER.log( Level.WARNING, "Uncertain what to create for {0}.", 
                        value.getClass().getSimpleName() );
        }
        else
            LOGGER.log( Level.WARNING, "Uncertain what to create for {0}.", "selection" );
        event.consume();
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
    
    @FXML
    private void onActionLoad( ActionEvent event )
    {
        List<Проект> ceлектор = tree.getSelectionModel().getSelectedItems().stream()
            .flatMap( ( TreeItem<Атрибутный> i ) -> Stream.of( i.getValue() ) )
//TODO org.neo4j.graphdb.NotInTransactionException : i.пакеты() i.проекты()       
//            .flatMap( ( Атрибутный i ) -> i instanceof Архив ? ((Архив)i).пакеты().stream() : Stream.of( i ) )
//            .flatMap( ( Атрибутный i ) -> i instanceof Пакет ? ((Пакет)i).проекты().stream() : Stream.of( i ) )
            .filter(  ( Атрибутный i ) -> i instanceof Проект )
            .flatMap( ( Атрибутный i ) -> Stream.of( (Проект)i ) )
            .collect( Collectors.toList() );
        if( ceлектор.isEmpty() )
            LOGGER.log( Level.INFO, "002005002I" );
        else
            JavaFX.getInstance().execute( new ДействияПоПорядку<>( 
                Приоритет.КОНТЕКСТ, действиеЗагрузитьПроект ), ceлектор );
        event.consume();
    }
    
    @FXML
    private void onActionPreview( ActionEvent event )
    {
        Predicate<TitledSceneGraph> inBrowser = ( TitledSceneGraph tsg ) -> tsg.node instanceof WebView;
        for( TreeItem<Атрибутный> item : tree.selectionModelProperty().getValue().getSelectedItems() )
        {
            Атрибутный value = item.getValue();
            if( value instanceof Элемент )
            {
                Элемент элемент = (Элемент)value;
                WebView view = new WebView();
                view.setUserData( элемент );
                SimpleStringProperty название = new SimpleStringProperty();
                JavaFX jfx = JavaFX.getInstance();
                jfx.execute( new WebViewLoaderTask( элемент, название, view.getEngine() ) );
                Image icon = JavaFX.icon( "icons16x16/preview.png" ).getImage();
                jfx.show( элемент, inBrowser, ( Элемент э ) -> new TitledSceneGraph( view, icon, название ) );
            }
            else
                LOGGER.getLogger().log( Level.WARNING, "Unnamed item cannot be drawn separately: {0}", value.getClass().getName());
        }
        event.consume();
    }
    
    @FXML
    private void onActionEdit( ActionEvent event )
    {
        
        event.consume();
    }
    
    @FXML
    private void onActionRemove( ActionEvent event )
    {
        List<TreeItem<Атрибутный>> ceлектор = tree.getSelectionModel().getSelectedItems();
        Collection<Атрибутный> элементы = new ArrayList<>( ceлектор.size() );
        ceлектор.forEach( ( TreeItem<Атрибутный> t ) -> элементы.add( t.getValue() ) );
        //TODO confirmation dialog
        JavaFX.getInstance().execute( new ДействияПоПорядку<Атрибутный>( 
                ДействияПоПорядку.Приоритет.КОНТЕКСТ, new УдалитьИзАрхива() ), элементы );
        event.consume();
    }
    
    @FXML
    private void onActionImportFile( ActionEvent event )
    {
        импортироватьXml( JavaFX.getInstance().getImportXmlFilelProvider() );
        event.consume();
    }
    
    @FXML
    private void onActionImportNet( ActionEvent event )
    {
        импортироватьXml( JavaFX.getInstance().getImportXmlUrlProvider() );
        event.consume();
    }
    
    @FXML
    private void onActionExportXml( ActionEvent event )
    {
        List<TreeItem<Атрибутный>> сeлектор = new ArrayList<>( tree.getSelectionModel().getSelectedItems() );
        JavaFX jfx = JavaFX.getInstance();
//        Provider<InputStream> provider = jfx.getExportXmlFilelProvider().newInstance();
//        if( provider != null )
//            jfx.execute( new Экспортировать( jfx.контекст ), new Экспортировать.Контекст( 
//                    provider, jfx.контекст.архив ) );
        event.consume();
    }
    
    @FXML
    private void onActionExportPic( ActionEvent event )
    {
        List<TreeItem<Атрибутный>> сeлектор = new ArrayList<>( tree.getSelectionModel().getSelectedItems() );
        if( сeлектор.size() != 1 )
            LOGGER.log( Level.SEVERE, "Cannot save multiple {0} elements into single file.", сeлектор.size() );
        else
        {
            Атрибутный элемент = сeлектор.get( 0 ).getValue();
            if( элемент instanceof Элемент )
            {
                if( fileProviderExport == null ) 
                    fileProviderExport = new ExportFileSelector( JavaFX.getInstance() );
                File file = fileProviderExport.newInstance();
                if( file != null )
                    JavaFX.getInstance().execute( new ЭкспортироватьSvg( JavaFX.getInstance().контекст ), 
                            new ЭкспортироватьSvg.Контекст( (Элемент)элемент, file ) );
            }
            else
                LOGGER.getLogger().log( Level.WARNING, "Unnamed item cannot be exported: {0}", элемент.getClass().getName());
        }
        event.consume();
    }
    
    @FXML
    private void onActionProperties( ActionEvent event )
    {
        if( selection.isEmpty() )
            LOGGER.log( "002005005I" );
        else if( selection.size() > 1 )
            LOGGER.log( "002005006I", selection.size() );
        else
        {
            if( properties == null ) properties = buildProperties();
            properties.getScene().getRoot().setUserData( selection.get( 0 ) );
            properties.show();
            properties.toFront();
        }
        event.consume();
    }

    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="disableXxx()">
    
    private boolean disableActionNew()
    {
        List<TreeItem<Атрибутный>> s = tree.getSelectionModel().getSelectedItems();
        if( s.size() != 1 ) return true;
        for( TreeItem<Атрибутный> item = s.get(0); item != null; item = item.getParent() )
            if( item.getValue() instanceof Мусор ) return true;
        Атрибутный value = s.get(0).getValue();
        return !( value instanceof Архив || value instanceof Пакет );
    }

    private boolean disableActionLoad()
    {
        List<TreeItem<Атрибутный>> s = tree.getSelectionModel().getSelectedItems();
        return s.isEmpty() || !s.stream()
                .flatMap( ( TreeItem<Атрибутный> i ) -> Stream.of( i.getValue() ) )
                .allMatch( ( Атрибутный i ) -> i instanceof Проект );
    }
    
    private boolean disableActionPreview()
    {
        List<TreeItem<Атрибутный>> s = tree.getSelectionModel().getSelectedItems();
        return false;
    }
    
    private boolean disableActionEdit()
    {
        List<TreeItem<Атрибутный>> s = tree.getSelectionModel().getSelectedItems();
        return false;
    }
    
    private boolean disableActionRemove()
    {
        List<TreeItem<Атрибутный>> s = tree.getSelectionModel().getSelectedItems();
        return s.isEmpty() || s.stream()
                .flatMap( ( TreeItem<Атрибутный> i ) -> Stream.of( i.getValue() ) )
                .anyMatch( ( Атрибутный i ) -> i instanceof Архив );
    }
    
    private boolean disableActionNewАрхив()
    {
        List<TreeItem<Атрибутный>> s = tree.getSelectionModel().getSelectedItems();
        return false;
    }
    
    private boolean disableActionNewПакет()
    {
        List<TreeItem<Атрибутный>> s = tree.getSelectionModel().getSelectedItems();
        return false;
    }
    
    private boolean disableActionNewПроект()
    {
        List<TreeItem<Атрибутный>> s = tree.getSelectionModel().getSelectedItems();
        return false;
    }
    
    private boolean disableActionNewБиблиотека()
    {
        List<TreeItem<Атрибутный>> s = tree.getSelectionModel().getSelectedItems();
        return false;
    }
    
    private boolean disableActionNewФрагмент()
    {
        List<TreeItem<Атрибутный>> s = tree.getSelectionModel().getSelectedItems();
        return false;
    }
    
    private boolean disableActionNewСигнал()
    {
        List<TreeItem<Атрибутный>> s = tree.getSelectionModel().getSelectedItems();
        return false;
    }
    
    private boolean disableActionNewСоединение()
    {
        List<TreeItem<Атрибутный>> s = tree.getSelectionModel().getSelectedItems();
        return false;
    }
    
    private boolean disableActionNewКонтакт()
    {
        List<TreeItem<Атрибутный>> s = tree.getSelectionModel().getSelectedItems();
        return false;
    }
    
    private boolean disableActionNewМодуль()
    {
        List<TreeItem<Атрибутный>> s = tree.getSelectionModel().getSelectedItems();
        return false;
    }
    
    private boolean disableActionNewПоле()
    {
        List<TreeItem<Атрибутный>> s = tree.getSelectionModel().getSelectedItems();
        return false;
    }
    
    private boolean disableActionNewПроцессор()
    {
        List<TreeItem<Атрибутный>> s = tree.getSelectionModel().getSelectedItems();
        return false;
    }
    
    private boolean disableActionNewТочка()
    {
        List<TreeItem<Атрибутный>> s = tree.getSelectionModel().getSelectedItems();
        return false;
    }
    
    private boolean disableActionNewЗаметка()
    {
        List<TreeItem<Атрибутный>> s = tree.getSelectionModel().getSelectedItems();
        return false;
    }
    
    private boolean disableActionNewИнструкция()
    {
        List<TreeItem<Атрибутный>> s = tree.getSelectionModel().getSelectedItems();
        return false;
    }
    
    private boolean disableActionNewКлассJava()
    {
        List<TreeItem<Атрибутный>> s = tree.getSelectionModel().getSelectedItems();
        return false;
    }
    
    private boolean disableActionNewТекстовыйБлок()
    {
        List<TreeItem<Атрибутный>> s = tree.getSelectionModel().getSelectedItems();
        return false;
    }
    
    private boolean disableActionNewXmlNameSpace()
    {
        List<TreeItem<Атрибутный>> s = tree.getSelectionModel().getSelectedItems();
        return false;
    }
    
    private boolean disableActionProperties()
    {
        List<TreeItem<Атрибутный>> s = tree.getSelectionModel().getSelectedItems();
        return s.size() != 1;
    }
    
    private boolean disableActionImportFile()
    {
        return selection.size() != 1 || !selection.stream()
                .allMatch( ( Атрибутный i ) -> i instanceof Архив );
    }
    
    private boolean disableActionImportNet()
    {
        return selection.size() != 1 || !selection.stream()
                .allMatch( ( Атрибутный i ) -> i instanceof Архив );
    }
    
    private boolean disableActionExportXml()
    {
        List<TreeItem<Атрибутный>> s = tree.getSelectionModel().getSelectedItems();
        return false;
    }
    
    private boolean disableActionExportPic()
    {
        List<TreeItem<Атрибутный>> s = tree.getSelectionModel().getSelectedItems();
        return false;
    }
    
    //</editor-fold>
    
    private Stage buildProperties()
    {
        BuilderFX<Parent,PropertiesController> builder = new BuilderFX<>();
        builder.init( PropertiesController.class,
                PropertiesController.RESOURCE_FXML, PropertiesController.RESOURCE_BUNDLE );
        PropertiesController controller = builder.getController();

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
        stage.setOnShowing( ( WindowEvent e ) -> controller.reset() );

        stage.titleProperty().bind( controller.titleProperty() );
//            controller.bindColorProperty( colorProperty );
//            controller.bindPatternProperty( patternProperty );
//            controller.bindScaleProperty( new SimpleObjectProperty( 3 ) );
        controller.reset();
        return stage;
    }
    
    private void импортироватьXml( Provider<Provider<InputStream>> селектор )
    {
        JavaFX jfx = JavaFX.getInstance();
        List<Архив> архивы = selection.stream()
            .flatMap( ( Атрибутный i ) -> i instanceof Архив ? Stream.of( (Архив)i ) : Stream.empty() )
            .collect( Collectors.toList() );
        if( архивы.isEmpty() )
            LOGGER.log( "002005003I" );
        else if( архивы.size() > 1 )
            LOGGER.log( "002005004I", архивы.size() );
        else
        {
            Архив архив = архивы.get( 0 );
            Provider<InputStream> поставщик = селектор.newInstance();
            if( поставщик != null )
                jfx.execute( new ApplicationActionWorker<Импортировать.Контекст>( 
                        импортировать, new Импортировать.Контекст( поставщик, архив ) )
                {
                    @Override
                    protected void succeeded()
                    {
                        super.succeeded();
                        jfx.historyXml.advance( контекст().поставщик() );
                    }
                } );
        }
    }
    
    /**
     * Список выбранных {@linkplain Атрибутный элементов} архива.
     */
    private class SelectionListBinding extends ListBinding<Атрибутный>
    {
        final ObservableList<Атрибутный> LIST = FXCollections.<Атрибутный>observableArrayList();
        
        /**
         * Связывает данный список со списком выбора в {@link TreeView}.
         * 
         * @param list список выбора в {@link TreeView}.
         */
        void bind( ObservableList<TreeItem<Атрибутный>> list )
        {
            super.bind( list );
        }
        
        @Override
        protected ObservableList<Атрибутный> computeValue()
        {
            List<Атрибутный> value = tree == null ? Collections.emptyList() :
                    tree.getSelectionModel().getSelectedItems().stream()
                    .flatMap( ( TreeItem<Атрибутный> i ) -> Stream.of( i.getValue() ) )
                    .collect( Collectors.toList() );
            LIST.setAll( value );
            return LIST;
        }
        
    }
    
}
