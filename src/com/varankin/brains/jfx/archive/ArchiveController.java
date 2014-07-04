package com.varankin.brains.jfx.archive;

import com.varankin.biz.action.Действие;
import com.varankin.brains.appl.ДействияПоПорядку;
import com.varankin.brains.appl.ДействияПоПорядку.Приоритет;
import com.varankin.brains.appl.ЗагрузитьАрхивныйПроект;
import com.varankin.brains.appl.Импортировать;
import com.varankin.brains.appl.СоздатьНовыйПакет;
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
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.web.WebView;
import javafx.stage.*;
import javafx.util.Builder;

import static com.varankin.brains.jfx.JavaFX.icon;
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

    public static final String RESOURCE_FXML  = "/fxml/editor/Archive.fxml";
    public static final ResourceBundle RESOURCE_BUNDLE = LOGGER.getLogger().getResourceBundle();

    private Stage properties;
    private Provider<File> fileProviderExport;
    private BooleanBinding disableNew, disableLoad, disableRemove, disableProperties;
    
    @FXML private TreeView<Атрибутный> навигатор;


    public ArchiveController()
    {
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
        навигатор = new TreeView<>( new TreeItem<Атрибутный>() );
        навигатор.setShowRoot( false );
        навигатор.setEditable( false );
        навигатор.getSelectionModel().setSelectionMode( SelectionMode.MULTIPLE );
        навигатор.setCellFactory( ( TreeView<Атрибутный> view ) -> new CellАтрибутный() );
//        навигатор.addEventHandler( TreeItem.<Атрибутный>branchExpandedEvent(), 
//                new EventHandler<TreeItem.TreeModificationEvent<Атрибутный>>()
//        {
//
//            @Override
//            public void handle( TreeItem.TreeModificationEvent<Атрибутный> event )
//            {
//                event.getTreeItem();
//            }
//        });
//        навигатор.addEventHandler( TreeItem.<Атрибутный>branchCollapsedEvent(), 
//                new EventHandler<TreeItem.TreeModificationEvent<Атрибутный>>()
//        {
//
//            @Override
//            public void handle( TreeItem.TreeModificationEvent<Атрибутный> event )
//            {
//                event.getTreeItem();
//            }
//        });

        ObservableList selection = навигатор.getSelectionModel().getSelectedItems();
        disableNew = createBooleanBinding( () -> disableActionNew(), selection );
        disableLoad = createBooleanBinding( () -> disableActionLoad(), selection );
        disableRemove = createBooleanBinding( () -> disableActionRemove(), selection );
        disableProperties = createBooleanBinding( () -> disableActionProperties(), selection );
        
        навигатор.setContextMenu( buildContextMenu() );

        Pane box = new HBox();// spacing );
        box.setPrefWidth( 250d );
        HBox.setHgrow( навигатор, Priority.ALWAYS );
        box.getChildren().addAll( buildToolBar(), навигатор );
        
        TitledPane pane = new TitledPane( LOGGER.text( "archive.title" ), box );
        
        pane.getStyleClass().add( CSS_CLASS );
        pane.getStylesheets().add( getClass().getResource( RESOURCE_CSS ).toExternalForm() );

        initialize();
        
        return pane;
    }
    
    private ContextMenu buildContextMenu()
    {
        MenuItem menuLoad = new MenuItem( 
                LOGGER.text( "archive.action.load" ), icon( "icons16x16/load.png" ) );
        menuLoad.setOnAction( this::onActionLoad );
        menuLoad.disableProperty().bind( disableLoad );
        
        MenuItem menuNewБиблиотека = new MenuItem( 
                LOGGER.text( "cell.library" ), icon( "icons16x16/new-library.png" ) );
        menuNewБиблиотека.setOnAction( this::onActionNewБиблиотека );
        
        MenuItem menuNewЗаметка = new MenuItem( 
                LOGGER.text( "cell.note" ), icon( "icons16x16/properties.png" ) );
        menuNewЗаметка.setOnAction( this::onActionNewЗаметка );
        
        MenuItem menuNewИнструкция = new MenuItem( 
                LOGGER.text( "cell.instruction" ) );//, icon( "icons16x16/new-library.png" ) );
        menuNewИнструкция.setOnAction( this::onActionNewИнструкция );
        
        MenuItem menuNewКлассJava = new MenuItem( 
                LOGGER.text( "cell.class.java" ) );//, icon( "icons16x16/new-library.png" ) );
        menuNewКлассJava.setOnAction( this::onActionNewКлассJava );
        
        MenuItem menuNewКонтакт = new MenuItem( 
                LOGGER.text( "cell.pin" ), icon( "icons16x16/pin.png" ) );
        menuNewКонтакт.setOnAction( this::onActionNewКонтакт );
        
        MenuItem menuNewМодуль = new MenuItem( 
                LOGGER.text( "cell.module" ), icon( "icons16x16/module.png" ) );
        menuNewМодуль.setOnAction( this::onActionNewМодуль );
        
        MenuItem menuNewПакет = new MenuItem( 
                LOGGER.text( "cell.package" ), icon( "icons16x16/file-xml.png" ) );
        menuNewПакет.setOnAction( this::onActionNewПакет );
        
        MenuItem menuNewПоле = new MenuItem( 
                LOGGER.text( "cell.field" ), icon( "icons16x16/field2.png" ) );
        menuNewПоле.setOnAction( this::onActionNewПоле );
        
        MenuItem menuNewПроект = new MenuItem( 
                LOGGER.text( "cell.project" ), icon( "icons16x16/new-project.png" ) );
        menuNewПроект.setOnAction( this::onActionNewПроект );
        
        MenuItem menuNewПроцессор = new MenuItem( 
                LOGGER.text( "cell.processor" ), icon( "icons16x16/processor2.png" ) );
        menuNewПроцессор.setOnAction( this::onActionNewПроцессор );
        
        MenuItem menuNewСигнал = new MenuItem( 
                LOGGER.text( "cell.signal" ), icon( "icons16x16/signal.png" ) );
        menuNewСигнал.setOnAction( this::onActionNewСигнал );
        
        MenuItem menuNewСоединение = new MenuItem( 
                LOGGER.text( "cell.connector" ), icon( "icons16x16/connector.png" ) );
        menuNewСоединение.setOnAction( this::onActionNewСоединение );
        
        MenuItem menuNewТекстовыйБлок = new MenuItem( 
                LOGGER.text( "cell.text" ), icon( "icons16x16/file.png" ) );
        menuNewТекстовыйБлок.setOnAction( this::onActionNewТекстовыйБлок );
        
        MenuItem menuNewТочка = new MenuItem( 
                LOGGER.text( "cell.point" ), icon( "icons16x16/point.png" ) );
        menuNewТочка.setOnAction( this::onActionNewТочка );
        
        MenuItem menuNewФрагмент = new MenuItem( 
                LOGGER.text( "cell.fragment" ), icon( "icons16x16/fragment.png" ) );
        menuNewФрагмент.setOnAction( this::onActionNewФрагмент );
        
        MenuItem menuNewXmlNameSpace = new MenuItem( 
                LOGGER.text( "cell.namespace" ) );//, icon( "icons16x16/.png" ) );
        menuNewXmlNameSpace.setOnAction( this::onActionNewXmlNameSpace );
        
        Menu menuNew = new Menu( LOGGER.text( "archive.action.new" ) );
                //, icon( "icons16x16/new-library.png" ) );
        //menuNew.setOnAction( this::onActionNew );
        //menuNew.disableProperty().bind( disableNew );
        menuNew.getItems().addAll
        ( 
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
        
        MenuItem menuPreview = new MenuItem( 
                LOGGER.text( "archive.action.preview" ), icon( "icons16x16/preview.png" ) );
        menuPreview.setOnAction( this::onActionPreview );
        
        MenuItem menuEdit = new MenuItem( 
                LOGGER.text( "archive.action.edit" ), icon( "icons16x16/edit.png" ) );
        menuEdit.setOnAction( this::onActionEdit );
        
        MenuItem menuRemove = new MenuItem( 
                LOGGER.text( "archive.action.remove" ), icon( "icons16x16/remove.png" ) );
        menuRemove.setOnAction( this::onActionRemove );
        menuRemove.disableProperty().bind( disableRemove );
        
        MenuItem menuImportFile = new MenuItem( 
                LOGGER.text( "archive.action.import.file" ), icon( "icons16x16/file-xml.png" ) );
        menuImportFile.setOnAction( this::onActionImportFile );
        
        MenuItem menuImportNet = new MenuItem( 
                LOGGER.text( "archive.action.import.network" ), icon( "icons16x16/load-internet.png" ) );
        menuImportNet.setOnAction( this::onActionImportNet );
        
        MenuItem menuExportXml = new MenuItem( 
                LOGGER.text( "archive.action.export.xml" ), icon( "icons16x16/file-export.png" ) );
        menuExportXml.setOnAction( this::onActionExportXml );
        
        MenuItem menuExportPic = new MenuItem( 
                LOGGER.text( "archive.action.export.pic" ), icon( "icons16x16/file-export.png" ) );
        menuExportPic.setOnAction( this::onActionExportPic );
        
        MenuItem menuProperties = new MenuItem( 
                LOGGER.text( "archive.action.properties" ), icon( "icons16x16/properties.png" ) );
        menuProperties.setOnAction( this::onActionProperties );
        menuProperties.disableProperty().bind( disableProperties );
        
        ContextMenu menu = new ContextMenu();
        menu.getItems().addAll
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
        return menu;
    }
    
    private ToolBar buildToolBar()
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
        
        Button buttonEdit = new Button();
        buttonEdit.setTooltip( new Tooltip( LOGGER.text( "archive.action.edit" ) ) );
        buttonEdit.setGraphic( icon( "icons16x16/edit.png" ) );
        buttonEdit.setOnAction( this::onActionEdit );
        
        Button buttonRemove = new Button();
        buttonRemove.setTooltip( new Tooltip( LOGGER.text( "archive.action.remove" ) ) );
        buttonRemove.setGraphic( icon( "icons16x16/remove.png" ) );
        buttonRemove.setOnAction( this::onActionRemove );
        buttonRemove.disableProperty().bind( disableRemove );
        
        Button buttonImportFile = new Button();
        buttonImportFile.setTooltip( new Tooltip( LOGGER.text( "archive.action.import.file" ) ) );
        buttonImportFile.setGraphic( icon( "icons16x16/file-xml.png" ) );
        buttonImportFile.setOnAction( this::onActionImportFile );
        
        Button buttonImportNet = new Button();
        buttonImportNet.setTooltip( new Tooltip( LOGGER.text( "archive.action.import.network" ) ) );
        buttonImportNet.setGraphic( icon( "icons16x16/load-internet.png" ) );
        buttonImportNet.setOnAction( this::onActionImportNet );
        
        Button buttonExportXml = new Button();
        buttonExportXml.setTooltip( new Tooltip( LOGGER.text( "archive.action.export.xml" ) ) );
        buttonExportXml.setGraphic( icon( "icons16x16/file-export.png" ) );
        buttonExportXml.setOnAction( this::onActionExportXml );
        
        Button buttonExportPic = new Button();
        buttonExportPic.setTooltip( new Tooltip( LOGGER.text( "archive.action.export.pic" ) ) );
        buttonExportPic.setGraphic( icon( "icons16x16/file-export.png" ) );
        buttonExportPic.setOnAction( this::onActionExportPic );
        
        Button buttonProperties = new Button();
        buttonProperties.setTooltip( new Tooltip( LOGGER.text( "archive.action.properties" ) ) );
        buttonProperties.setGraphic( icon( "icons16x16/properties.png" ) );
        buttonProperties.setOnAction( this::onActionProperties );
        buttonProperties.disableProperty().bind( disableProperties );
        
        ToolBar toolbar = new ToolBar();
        toolbar.setOrientation( Orientation.VERTICAL );
        toolbar.getItems().addAll
        ( 
                buttonLoad,
                new Separator( Orientation.HORIZONTAL ),
                buttonPreview,
                buttonEdit,
                buttonRemove,
                new Separator( Orientation.HORIZONTAL ),
                buttonNew,
                buttonImportFile,
                buttonImportNet,
                buttonExportXml,
                buttonExportPic,
                new Separator( Orientation.HORIZONTAL ),
                buttonProperties
        );
        return toolbar;
    }
    
    @FXML
    protected void initialize()
    {
        Архив архив = JavaFX.getInstance().контекст.архив;
        TreeItem<Атрибутный> item = new TreeItem<>( архив );
//        архив.пакеты().наблюдатели().add( new МониторКоллекции( item.getChildren() ) );
//        архив.namespaces().наблюдатели().add( new МониторКоллекции( item.getChildren() ) );
        навигатор.getRoot().getChildren().add( item );
    }
    
    @FXML
    private void onActionNew( ActionEvent event )
    {
        List<TreeItem<Атрибутный>> s = навигатор.getSelectionModel().getSelectedItems();
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
        List<Проект> ceлектор = навигатор.getSelectionModel().getSelectedItems().stream()
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
        for( TreeItem<Атрибутный> item : навигатор.selectionModelProperty().getValue().getSelectedItems() )
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
        List<TreeItem<Атрибутный>> ceлектор = навигатор.getSelectionModel().getSelectedItems();
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
        JavaFX jfx = JavaFX.getInstance();
        Provider<InputStream> provider = jfx.getImportXmlFilelProvider().newInstance();
        if( provider != null )
            jfx.execute( new Импортировать( jfx.контекст ), new Импортировать.Контекст( 
                    provider, jfx.контекст.архив ) );
        event.consume();
    }
    
    @FXML
    private void onActionImportNet( ActionEvent event )
    {
        JavaFX jfx = JavaFX.getInstance();
        Provider<InputStream> provider = jfx.getImportXmlUrlProvider().newInstance();
        if( provider != null )
            jfx.execute( new Импортировать( jfx.контекст ), new Импортировать.Контекст( 
                    provider, jfx.контекст.архив ) );
        event.consume();
    }
    
    @FXML
    private void onActionExportXml( ActionEvent event )
    {
        List<TreeItem<Атрибутный>> сeлектор = new ArrayList<>( навигатор.getSelectionModel().getSelectedItems() );
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
        List<TreeItem<Атрибутный>> сeлектор = new ArrayList<>( навигатор.getSelectionModel().getSelectedItems() );
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
        if( properties == null )
        {
            BuilderFX<Parent,PropertiesController> builder = new BuilderFX<>();
            builder.init( PropertiesController.class, 
                PropertiesController.RESOURCE_FXML, PropertiesController.RESOURCE_BUNDLE );
            PropertiesController controller = builder.getController();

            properties = new Stage();
            properties.initStyle( StageStyle.DECORATED );
            properties.initModality( Modality.NONE );
            properties.initOwner( JavaFX.getInstance().платформа );
            properties.getIcons().add( JavaFX.icon( "icons16x16/properties.png" ).getImage() );

            properties.setResizable( true );
            properties.setMinHeight( 150d );
            properties.setMinWidth( 350d );
            properties.setHeight( 150d ); //TODO save/restore size&pos
            properties.setWidth( 350d );
            properties.setScene( new Scene( builder.getNode() ) );
            properties.setOnShowing( ( WindowEvent e ) -> controller.reset() );

            properties.titleProperty().bind( controller.titleProperty() );
//            controller.bindColorProperty( colorProperty );
//            controller.bindPatternProperty( patternProperty );
//            controller.bindScaleProperty( new SimpleObjectProperty( 3 ) );
            controller.reset();
        }
        properties.show();
        properties.toFront();
        event.consume();
    }
  
    private boolean disableActionLoad()
    {
        List<TreeItem<Атрибутный>> s = навигатор.getSelectionModel().getSelectedItems();
        return s.isEmpty() || !s.stream()
            .flatMap( ( TreeItem<Атрибутный> i ) -> Stream.of( i.getValue() ) )
            .allMatch( ( Атрибутный i ) -> i instanceof Проект ); 
    }

    private boolean disableActionRemove()
    {
        List<TreeItem<Атрибутный>> s = навигатор.getSelectionModel().getSelectedItems();
        return s.isEmpty() || s.stream()
            .flatMap( ( TreeItem<Атрибутный> i ) -> Stream.of( i.getValue() ) )
            .anyMatch( ( Атрибутный i ) -> i instanceof Архив ); 
    }

    private boolean disableActionProperties()
    {
        List<TreeItem<Атрибутный>> s = навигатор.getSelectionModel().getSelectedItems();
        return s.size() != 1; 
    }

    private boolean disableActionNew()
    {
        List<TreeItem<Атрибутный>> s = навигатор.getSelectionModel().getSelectedItems();
        if( s.size() != 1 ) return true;
        for( TreeItem<Атрибутный> item = s.get(0); item != null; item = item.getParent() )
            if( item.getValue() instanceof Мусор ) return true;
        Атрибутный value = s.get(0).getValue();
        return !( value instanceof Архив || value instanceof Пакет );
    }

}
