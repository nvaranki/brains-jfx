package com.varankin.brains.jfx.archive;

import com.varankin.biz.action.Действие;
import com.varankin.brains.jfx.history.LocalNeo4jProvider;
import com.varankin.brains.jfx.history.RemoteNeo4jProvider;
import com.varankin.brains.appl.ДействияПоПорядку;
import com.varankin.brains.appl.ЗагрузитьАрхивныйПроект;
import com.varankin.brains.appl.УдалитьИзКоллекции;
import com.varankin.brains.appl.ЭкспортироватьSvg;
import com.varankin.brains.appl.ЭкспортироватьXml;
import com.varankin.brains.db.*;
import com.varankin.brains.jfx.*;
import com.varankin.brains.jfx.db.*;
import com.varankin.brains.jfx.editor.EditorController;
import com.varankin.io.container.Provider;
import com.varankin.brains.jfx.history.LocalInputStreamProvider;
import com.varankin.brains.jfx.history.RemoteInputStreamProvider;
import com.varankin.brains.jfx.history.SerializableProvider;
import com.varankin.brains.jfx.selector.LocalFileSelector;
import com.varankin.brains.jfx.selector.UrlSelector;
import com.varankin.util.LoggerX;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.function.Predicate;
import java.util.logging.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.*;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.*;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.web.WebView;
import javafx.stage.*;

import static javafx.beans.binding.Bindings.createBooleanBinding;

/**
 *
 * @author Николай
 */
final class ActionProcessor //TODO RT-37820
{
    private static final LoggerX LOGGER = LoggerX.getLogger( ActionProcessor.class );
    private static final Действие<DbПроект> действиеЗагрузитьПроект 
        = new ЗагрузитьАрхивныйПроект();
    
    private final ObservableList<TreeItem<FxАтрибутный>> SELECTION;

    private Stage properties;
    private Provider<File> fileProviderExportXml, fileProviderExportSvg;
    
    private final BooleanBinding
        disableNew, disableLoad, 
        disablePreview, disableEdit, disableRemove, 
        disableProperties, 
        disableImport, disableExportXml, disableExportPic;
    
    ActionProcessor( ObservableList<TreeItem<FxАтрибутный>> selection )
    {
        SELECTION = selection;
        disableNew = createBooleanBinding( () -> disableActionNew(), selection );
        disableLoad = createBooleanBinding( () -> disableActionLoad(), selection );
        disablePreview = createBooleanBinding( () -> disableActionPreview(), selection );
        disableEdit = createBooleanBinding( () -> disableActionEdit(), selection );
        disableRemove = createBooleanBinding( () -> disableActionRemove(), selection );
        disableProperties = createBooleanBinding( () -> disableActionProperties(), selection );
        disableImport = createBooleanBinding( () -> disableActionImport(), selection );
        disableExportXml = createBooleanBinding( () -> disableActionExportXml(), selection );
        disableExportPic = createBooleanBinding( () -> disableActionExportPic(), selection );
    }

    BooleanBinding disableNewProperty() { return disableNew; }
    BooleanBinding disableLoadProperty() { return disableLoad; }
    BooleanBinding disablePreviewProperty() { return disablePreview; }
    BooleanBinding disableEditProperty() { return disableEdit; }
    BooleanBinding disableRemoveProperty() { return disableRemove; }
    BooleanBinding disablePropertiesProperty() { return disableProperties; }
    BooleanBinding disableImportProperty() { return disableImport; }
    BooleanBinding disableExportXmlProperty() { return disableExportXml; }
    BooleanBinding disableExportPicProperty() { return disableExportPic; }
    
    //<editor-fold defaultstate="collapsed" desc="onActionXxx()">

    void onActionNew( ActionEvent event )
    {
        if( SELECTION.size() == 1 )
        {
            FxАтрибутный value = SELECTION.get( 0 ).getValue();
//            if( value instanceof Архив )
//                onActionNewПакет( event );
//            else if( value instanceof Пакет )
//                onActionNewПроект( event );
//            else
                LOGGER.log( Level.WARNING, "Uncertain what to create for {0}.", 
                        value.getClass().getSimpleName() );
        }
        else
            LOGGER.log( Level.WARNING, "Uncertain what to create for {0}.", "selection" );
    }
    
    void onActionLoad( ActionEvent event )
    {
        List<DbПроект> ceлектор = SELECTION.stream()
            .map( ti -> ti.getValue() )
            .filter( i -> i instanceof FxПроект )
            .map( i -> ((FxПроект)i).getSource() )
            .collect( Collectors.toList() );
        if( ceлектор.isEmpty() )
            LOGGER.log( Level.INFO, "002005002I" );
        else
            JavaFX.getInstance().execute( new ДействияПоПорядку<>( 
                ДействияПоПорядку.Приоритет.КОНТЕКСТ, действиеЗагрузитьПроект ), ceлектор );
    }
    
    void onActionPreview( ActionEvent event )
    {
        Predicate<TitledSceneGraph> inBrowser = ( TitledSceneGraph tsg ) -> tsg.node instanceof WebView;
        for( TreeItem<FxАтрибутный> item : SELECTION )
        {
            DbАтрибутный value = item.getValue().getSource();
            if( value instanceof DbЭлемент )
            {
                DbЭлемент элемент = (DbЭлемент)value;
                JavaFX jfx = JavaFX.getInstance();
                if( jfx.isShown( элемент, inBrowser ) )
                    LOGGER.log( Level.INFO, "002005008I", item instanceof TitledTreeItem ? 
                            ((TitledTreeItem)item).getTitle() : item.toString() );
                else
                {
                    // Создать, разместить и показатеть пустой навигатор
                    WebView view = new WebView();
                    view.setUserData( элемент );
                    SimpleStringProperty название = new SimpleStringProperty();
                    Image icon = JavaFX.icon( "icons16x16/preview.png" ).getImage();
                    jfx.show( элемент, inBrowser, ( DbЭлемент э ) -> new TitledSceneGraph( view, icon, название ) );
                    // загрузить элемент для просмотра
                    jfx.execute( new WebViewLoaderTask( элемент, название, view.getEngine() ) );
                }
            }
            else
                LOGGER.log( Level.WARNING, "002005010W", item instanceof TitledTreeItem ? 
                            ((TitledTreeItem)item).getTitle() : item.toString() );
        }
    }
    
    void onActionEdit( ActionEvent event )
    {
        Predicate<TitledSceneGraph> inEditor = ( TitledSceneGraph tsg ) -> tsg!=null;//tsg.node instanceof Pane; //TODO identification;
        for( TreeItem<FxАтрибутный> item : SELECTION )
        {
            DbАтрибутный value = item.getValue().getSource();
            if( value instanceof DbЭлемент )
            {
                DbЭлемент элемент = (DbЭлемент)value;
                JavaFX jfx = JavaFX.getInstance();
                if( jfx.isShown( элемент, inEditor ) )
                    LOGGER.log( Level.INFO, "002005009I", item instanceof TitledTreeItem ? 
                            ((TitledTreeItem)item).getTitle() : item.toString() );
                else
                {
                    // Создать, разместить и показатеть пустой редактор
                    BuilderFX<Parent,EditorController> builder = new BuilderFX<>();
                    builder.init( EditorController.class, EditorController.RESOURCE_FXML, EditorController.RESOURCE_BUNDLE );
                    EditorController controller = builder.getController();
                    Parent view = controller.build();
                    SimpleStringProperty название = new SimpleStringProperty();
                    Image icon = JavaFX.icon( "icons16x16/edit.png" ).getImage();
                    jfx.show( элемент, inEditor, ( DbЭлемент э ) -> new TitledSceneGraph( view, icon, название ) );
                    // загрузить элемент для редактирования
                    jfx.execute( new EditLoaderTask( элемент, название, controller ) );
                }
            }
            else
                LOGGER.log( Level.WARNING, "002005011W", item instanceof TitledTreeItem ? 
                            ((TitledTreeItem)item).getTitle() : item.toString() );
        }
    }
    
    void onActionRemove( ActionEvent event )
    {
        //TODO confirmation dialog
        ДействияПоПорядку<УдалитьИзКоллекции.Контекст> действие = 
            new ДействияПоПорядку<>( ДействияПоПорядку.Приоритет.КОНТЕКСТ, new УдалитьИзКоллекции() );
        JavaFX.getInstance().execute( действие, SELECTION.stream()
            .map( ti -> new УдалитьИзКоллекции.Контекст( ti.getValue().getSource(), ti.getParent().getValue().getSource() ) )
            .collect( Collectors.toList() ) );
    }
    
    static void onArchiveFromFile( ActionEvent event )
    {
        JavaFX jfx = JavaFX.getInstance();
        File file = jfx.getLocalFolderSelector().newInstance();
        if( file != null && file.isDirectory() )
            jfx.execute(new ArchiveTask( new LocalNeo4jProvider( file ) ) );
    }
    
    static void onArchiveFromNet( ActionEvent event )
    {
        JavaFX jfx = JavaFX.getInstance();
        URL url = jfx.getUrlSelector().newInstance();
        if( url != null )
            jfx.execute(new ArchiveTask( new RemoteNeo4jProvider( url ) ) );
    }
    
    static void onArchiveFromHistory( int позиция, ActionEvent event )
    {
        JavaFX jfx = JavaFX.getInstance();
        SerializableProvider<DbАрхив> provider = jfx.history.archive.get( позиция );
        if( provider != null )
            jfx.execute( new ArchiveTask( provider ) );
    }
    
    void onPackageFromFile( ActionEvent event )
    {
        JavaFX jfx = JavaFX.getInstance();
        LocalFileSelector selector = jfx.getLocalFileSelector();
        selector.setFilters( jfx.filtersXml );
        selector.setTitle( LOGGER.text( "import.xml.title" ) );
        File file = selector.newInstance();
        if( file != null )
            selectiveImport( new LocalInputStreamProvider( file ) );
    }
    
    void onPackageFromNet( ActionEvent event )
    {
        UrlSelector selector = JavaFX.getInstance().getUrlSelector();
        selector.setTitle( LOGGER.text( "import.xml.title" ) );
        URL url = selector.newInstance();
        if( url != null )
            selectiveImport( new RemoteInputStreamProvider( url ) );
    }
    
    void onPackageFromHistory( int позиция, ActionEvent event )
    {
        SerializableProvider<InputStream> provider = JavaFX.getInstance().history.xml.get( позиция );
        if( provider != null )
            selectiveImport( provider );
    }
    
    private void selectiveImport( SerializableProvider<InputStream> provider )
    {
        SELECTION.stream()
            .map( ti -> ti.getValue() )
            .filter( i -> i instanceof DbАрхив ).map( i -> (DbАрхив)i )
            .forEach( архив -> JavaFX.getInstance().execute( new ImportTask( provider, архив ) ) );
    }
    
    void onActionExportXml( ActionEvent event )
    {
        List<DbЭлемент> list = SELECTION.stream()
                .map( i -> i.getValue() )
                .filter( i -> i instanceof DbЭлемент ).map( i -> (DbЭлемент)i )
                .collect( Collectors.toList() );
        if( list.size() != 1 )
            LOGGER.log( Level.SEVERE, "Cannot save multiple {0} elements into single file.", list.size() );
        else
        {
            DbЭлемент элемент = list.get( 0 );
            JavaFX jfx = JavaFX.getInstance();
            if( fileProviderExportXml == null ) 
            {
                FileChooser.ExtensionFilter фильтр = new FileChooser.ExtensionFilter( LOGGER.text( "ext.xml" ), "*.xml" );
                fileProviderExportXml = new ExportFileSelector( фильтр );
            }
            File file = fileProviderExportXml.newInstance();
            if( file != null )
                jfx.execute( new ЭкспортироватьXml(), new ЭкспортироватьXml.Контекст( элемент, file ) );
        }
        event.consume();
    }
    
    void onActionExportPic( ActionEvent event )
    {
        if( SELECTION.size() != 1 )
            LOGGER.log( Level.SEVERE, "Cannot save multiple {0} elements into single file.", SELECTION.size() );
        else
        {
            DbАтрибутный элемент = SELECTION.get( 0 ).getValue().getSource();
            if( элемент instanceof DbЭлемент )
            {
                JavaFX jfx = JavaFX.getInstance();
                if( fileProviderExportSvg == null ) 
                {
                    FileChooser.ExtensionFilter фильтр = new FileChooser.ExtensionFilter( LOGGER.text( "ext.svg" ), "*.svg" );
                    fileProviderExportSvg = new ExportFileSelector( фильтр );
                }
                File file = fileProviderExportSvg.newInstance();
                if( file != null )
                    jfx.execute( new ЭкспортироватьSvg(), new ЭкспортироватьSvg.Контекст( 
                            (DbЭлемент)элемент, file ) );
            }
            else
                LOGGER.getLogger().log( Level.WARNING, "Unnamed item cannot be exported: {0}", элемент.getClass().getName());
        }
        event.consume();
    }
    
    void onActionProperties( ActionEvent event )
    {
        if( SELECTION.isEmpty() )
            LOGGER.log( "002005005I" );
        else if( SELECTION.size() > 1 )
            LOGGER.log( "002005006I", SELECTION.size() );
        else
        {
            if( properties == null ) properties = buildProperties();
            properties.getScene().getRoot().setUserData( SELECTION.get( 0 ).getValue() );
            properties.show();
            properties.toFront();
        }
        event.consume();
    }

    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="disableXxx()">
    
    boolean disableActionNew()
    {
//        if( selection.size() != 1 ) return true;
////        for( Атрибутный item = tree.getSelectionModel().getSelectedItems().get(0); item != null; item = item.getParent() )
////            if( item.getValue() instanceof Мусор ) return true;
//        Атрибутный value = selection.get(0);
//        return !( value instanceof Архив || value instanceof Пакет );
        return true;
    }

    boolean disableActionLoad()
    {
        return SELECTION.isEmpty() || !SELECTION.stream()
                .map( ti -> ti.getValue() ).allMatch( i -> i instanceof FxПроект );
    }
    
    boolean disableActionPreview()
    {
        return SELECTION.isEmpty() || !SELECTION.stream()
                .map( ti -> ti.getValue() ).allMatch( i -> i instanceof FxЭлемент );
    }
    
    boolean disableActionEdit()
    {
        return SELECTION.isEmpty() || !SELECTION.stream()
                .map( ti -> ti.getValue() ).allMatch( i -> i instanceof FxЭлемент );
    }
    
    boolean disableActionRemove()
    {
        return SELECTION.isEmpty() || SELECTION.stream()
                .map( ti -> ti.getValue() ).anyMatch( i -> i instanceof FxАрхив );
    }
    
    boolean disableActionProperties()
    {
        return SELECTION.size() != 1;
    }
    
    boolean disableActionImport()
    {
        return SELECTION.isEmpty() || !SELECTION.stream()
                .map( ti -> ti.getValue() ).allMatch( i -> i instanceof FxАрхив );
    }
    
    boolean disableActionExportXml()
    {
        return SELECTION.size() != 1 || !SELECTION.stream()
                .map( ti -> ti.getValue() ).allMatch( i -> i instanceof FxЭлемент );//TODO( i -> !( i instanceof DbАрхив || i instanceof DbМусор ) );
    }
    
    boolean disableActionExportPic()
    {
        return SELECTION.size() != 1 || !SELECTION.stream()
                .map( ti -> ti.getValue() ).allMatch( i -> i instanceof FxЭлемент );
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

}
