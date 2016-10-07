package com.varankin.brains.jfx.archive;

import com.varankin.biz.action.Действие;
import com.varankin.brains.appl.ДействияПоПорядку;
import com.varankin.brains.appl.ЗагрузитьАрхивныйПроект;
import com.varankin.brains.appl.Импортировать;
import com.varankin.brains.appl.УдалитьИзКоллекции;
import com.varankin.brains.appl.ЭкспортироватьSvg;
import com.varankin.brains.appl.ЭкспортироватьXml;
import com.varankin.brains.db.*;
import com.varankin.brains.jfx.*;
import com.varankin.brains.jfx.editor.EditorController;
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
        = new ЗагрузитьАрхивныйПроект( JavaFX.getInstance().контекст );
    private static final Импортировать импортировать
        = new Импортировать( JavaFX.getInstance().контекст );
    
    private final ObservableList<TreeItem<DbАтрибутный>> SELECTION;

    private Stage properties;
    private Provider<File> fileProviderExportXml, fileProviderExportSvg;
    
    private final BooleanBinding
        disableNew, disableLoad, 
        disablePreview, disableEdit, disableRemove, 
        disableProperties, 
        disableImportFile, disableImportNet, disableExportXml, disableExportPic;
    
    ActionProcessor( ObservableList<TreeItem<DbАтрибутный>> selection )
    {
        SELECTION = selection;
        disableNew = createBooleanBinding( () -> disableActionNew(), selection );
        disableLoad = createBooleanBinding( () -> disableActionLoad(), selection );
        disablePreview = createBooleanBinding( () -> disableActionPreview(), selection );
        disableEdit = createBooleanBinding( () -> disableActionEdit(), selection );
        disableRemove = createBooleanBinding( () -> disableActionRemove(), selection );
        disableProperties = createBooleanBinding( () -> disableActionProperties(), selection );
        disableImportFile = createBooleanBinding( () -> disableActionImportFile(), selection );
        disableImportNet = createBooleanBinding( () -> disableActionImportNet(), selection );
        disableExportXml = createBooleanBinding( () -> disableActionExportXml(), selection );
        disableExportPic = createBooleanBinding( () -> disableActionExportPic(), selection );
    }

    BooleanBinding disableNewProperty() { return disableNew; }
    BooleanBinding disableLoadProperty() { return disableLoad; }
    BooleanBinding disablePreviewProperty() { return disablePreview; }
    BooleanBinding disableEditProperty() { return disableEdit; }
    BooleanBinding disableRemoveProperty() { return disableRemove; }
    BooleanBinding disablePropertiesProperty() { return disableProperties; }
    BooleanBinding disableImportFileProperty() { return disableImportFile; }
    BooleanBinding disableImportNetProperty() { return disableImportNet; }
    BooleanBinding disableExportXmlProperty() { return disableExportXml; }
    BooleanBinding disableExportPicProperty() { return disableExportPic; }
    
    //<editor-fold defaultstate="collapsed" desc="onActionXxx()">

    void onActionNew( ActionEvent event )
    {
        if( SELECTION.size() == 1 )
        {
            DbАтрибутный value = SELECTION.get( 0 ).getValue();
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
//TODO org.neo4j.graphdb.NotInTransactionException : i.пакеты() i.проекты()       
//            .flatMap( ( Атрибутный i ) -> i instanceof Архив ? ((Архив)i).пакеты().stream() : Stream.of( i ) )
//            .flatMap( ( Атрибутный i ) -> i instanceof Пакет ? ((Пакет)i).проекты().stream() : Stream.of( i ) )
            .filter(  ( DbАтрибутный i ) -> i instanceof DbПроект )
            .flatMap( ( DbАтрибутный i ) -> Stream.of( (DbПроект)i ) )
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
        for( TreeItem<DbАтрибутный> item : SELECTION )
        {
            DbАтрибутный value = item.getValue();
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
        for( TreeItem<DbАтрибутный> item : SELECTION )
        {
            DbАтрибутный value = item.getValue();
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
            .map( ti -> new УдалитьИзКоллекции.Контекст( ti.getValue(), ti.getParent().getValue() ) )
            .collect( Collectors.toList() ) );
    }
    
    void onActionImportFile( ActionEvent event )
    {
        импортироватьXml( JavaFX.getInstance().getImportXmlFilelProvider() );
    }
    
    void onActionImportNet( ActionEvent event )
    {
        импортироватьXml( JavaFX.getInstance().getImportXmlUrlProvider() );
    }
    
    void onActionExportXml( ActionEvent event )
    {
        if( SELECTION.size() != 1 )
            LOGGER.log( Level.SEVERE, "Cannot save multiple {0} elements into single file.", SELECTION.size() );
        else
        {
            DbАтрибутный элемент = SELECTION.get( 0 ).getValue();
            JavaFX jfx = JavaFX.getInstance();
            if( fileProviderExportXml == null ) 
            {
                FileChooser.ExtensionFilter фильтр = new FileChooser.ExtensionFilter( LOGGER.text( "ext.xml" ), "*.xml" );
                fileProviderExportXml = new ExportFileSelector( фильтр );
            }
            File file = fileProviderExportXml.newInstance();
            if( file != null )
                jfx.execute( new ЭкспортироватьXml(), new ЭкспортироватьXml.Контекст( 
                        jfx.контекст, элемент, file ) );
        }
        event.consume();
    }
    
    void onActionExportPic( ActionEvent event )
    {
        if( SELECTION.size() != 1 )
            LOGGER.log( Level.SEVERE, "Cannot save multiple {0} elements into single file.", SELECTION.size() );
        else
        {
            DbАтрибутный элемент = SELECTION.get( 0 ).getValue();
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
                            jfx.контекст, (DbЭлемент)элемент, file ) );
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
                .map( ti -> ti.getValue() ).allMatch( i -> i instanceof DbПроект );
    }
    
    boolean disableActionPreview()
    {
        return SELECTION.isEmpty() || !SELECTION.stream()
                .map( ti -> ti.getValue() ).allMatch( i -> i instanceof DbЭлемент );
    }
    
    boolean disableActionEdit()
    {
        return SELECTION.isEmpty() || !SELECTION.stream()
                .map( ti -> ti.getValue() ).allMatch( i -> i instanceof DbЭлемент );
    }
    
    boolean disableActionRemove()
    {
        return SELECTION.isEmpty() || SELECTION.stream()
                .map( ti -> ti.getValue() ).anyMatch( i -> i instanceof DbАрхив );
    }
    
    boolean disableActionProperties()
    {
        return SELECTION.size() != 1;
    }
    
    boolean disableActionImportFile()
    {
        return SELECTION.size() != 1 || !SELECTION.stream()
                .map( ti -> ti.getValue() ).allMatch( i -> i instanceof DbАрхив );
    }
    
    boolean disableActionImportNet()
    {
        return SELECTION.size() != 1 || !SELECTION.stream()
                .map( ti -> ti.getValue() ).allMatch( i -> i instanceof DbАрхив );
    }
    
    boolean disableActionExportXml()
    {
        return SELECTION.size() != 1 || !SELECTION.stream()
                .map( ti -> ti.getValue() ).allMatch( i -> !( i instanceof DbАрхив || i instanceof DbМусор ) );
    }
    
    boolean disableActionExportPic()
    {
        return SELECTION.size() != 1 || !SELECTION.stream()
                .map( ti -> ti.getValue() ).allMatch( i -> i instanceof DbЭлемент );
    }
    
    //</editor-fold>
    
    
    private void импортироватьXml( Provider<Provider<InputStream>> селектор )
    {
        JavaFX jfx = JavaFX.getInstance();
        List<DbАрхив> архивы = SELECTION.stream()
            .map( ti -> ti.getValue() )
            .flatMap( i -> i instanceof DbАрхив ? Stream.of( (DbАрхив)i ) : Stream.empty() )
            .collect( Collectors.toList() );
        if( архивы.isEmpty() )
            LOGGER.log( "002005003I" );
        else if( архивы.size() > 1 )
            LOGGER.log( "002005004I", архивы.size() );
        else
        {
            DbАрхив архив = архивы.get( 0 );
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
