package com.varankin.brains.jfx;

import com.varankin.biz.action.Действие;
import com.varankin.brains.appl.ЭкспортироватьSvg;
import com.varankin.brains.artificial.io.svg.SvgService;
import com.varankin.brains.artificial.io.Фабрика;
import com.varankin.brains.db.Коллекция;
import com.varankin.brains.db.Сборка;
import com.varankin.brains.db.Элемент;
import com.varankin.brains.jfx.editor.EditorController;
import com.varankin.io.container.Provider;
import com.varankin.util.Текст;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.logging.*;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ListView;
import javafx.scene.web.WebView;
import javafx.util.Callback;

/**
 *
 * @author Николай
 */
abstract class AbstractCatalogView<E extends Элемент> extends ListView<E>
{
    private static final Logger LOGGER = Logger.getLogger( AbstractCatalogView.class.getName() );
    
    private final ReadOnlyStringProperty title;

    protected final JavaFX jfx;
    protected final Текст словарь;
    protected final List<AbstractJfxAction> actions;

    protected AbstractCatalogView( JavaFX jfx, Коллекция<E> коллекция )
    {
        this.jfx = jfx;
        словарь = Текст.ПАКЕТЫ.словарь( getClass(), jfx.контекст.специфика );
        title = new ReadOnlyStringWrapper( словарь.текст( "Name" ) );
        itemsProperty().bind( new ObservableValueList<>( коллекция ) );
        actions = new ArrayList<>();
    }
    
    final ReadOnlyStringProperty titleProperty()
    {
        return title;
    }

    final List<AbstractJfxAction> getActions()
    {
        return actions;
    }
    
    //<editor-fold defaultstate="collapsed" desc="classes">
    
    protected class ActionPreview extends AbstractContextJfxAction<JavaFX>
    {
        private final Фабрика<E,TitledSceneGraph> фабрика;
        private final Predicate<TitledSceneGraph> inBrowser;
        
        ActionPreview( SvgService<E> service )
        {
            super( jfx, jfx.словарь( ActionPreview.class ) );
            inBrowser = ( TitledSceneGraph tsg ) -> tsg.node instanceof WebView;
            фабрика = ( E элемент ) -> 
            {
                WebView view = new WebView();
                view.setUserData( элемент );
                String название = элемент.название();
                контекст.getExecutorService().submit( new WebViewLoaderTask(
                        service.генератор( элемент ), view.getEngine(), название, словарь ) );
                return new TitledSceneGraph( view, new SimpleStringProperty( название ) );
            };
        }
        
        @Override
        public void handle( ActionEvent event )
        {
            for( E элемент : selectionModelProperty().getValue().getSelectedItems() )
                if( элемент != null )
                    jfx.show( элемент, inBrowser, фабрика );
        }
        
    }
    
    protected class ActionEdit extends AbstractContextJfxAction<JavaFX>
    {
        private final Predicate<TitledSceneGraph> inEditor;
        
        ActionEdit()
        {
            super( jfx, jfx.словарь( ActionEdit.class ) );
            inEditor = ( TitledSceneGraph tsg ) -> tsg!=null;//tsg.node instanceof Pane; //TODO identification
        }

        @Override
        public void handle( ActionEvent event )
        {
            for( E элемент : selectionModelProperty().getValue().getSelectedItems() )
                if( элемент == null )
                    LOGGER.log( Level.FINE, "Item to edit is null." );
                else if( jfx.isShown( элемент, inEditor ) != null )
                    LOGGER.log( Level.INFO, "Item to edit \"{}\" is already in editor.", элемент.название() );
                else
                    handleEditElement( ( Void v ) -> элемент );
        }
        
    }
    
    protected void handleEditElement( Callback<Void,E> фабрика )
    {
        // Создать, разместить и показатеть пустой редактор
        BuilderFX<Node,EditorController> builder = new BuilderFX<>();
        builder.init( EditorController.class, EditorController.RESOURCE_FXML, EditorController.RESOURCE_BUNDLE );
        EditorController controller = builder.getController();
        Parent view = controller.build();
        SimpleStringProperty propertyTitle = new SimpleStringProperty();
        TitledSceneGraph tsg = new TitledSceneGraph( view, propertyTitle );
        jfx.getViews().getValue().add( tsg );
        
        // загрузить элемент для редактирования
        jfx.getExecutorService().submit( () -> 
        { 
            E элемент = фабрика.call( null );
            controller.setContent( элемент );
            Platform.runLater( () -> propertyTitle.setValue( элемент.название() ) );
        } );
    }
    
    protected class ActionRemove extends AbstractContextJfxAction<JavaFX>
    {
        private final Действие<Collection<E>> действие;
        
        ActionRemove( Действие<Collection<E>> действие )
        {
            super( jfx, jfx.словарь( ActionRemove.class ) );
            this.действие = действие;
        }
        
        @Override
        public void handle( ActionEvent __ )
        {
            // собрать выделенные элементы немедленно, ибо список может затем измениться другими процессами
            List<E> ceлектор = new ArrayList<>( getSelectionModel().getSelectedItems() );
            //TODO confirmation dialog
            new ApplicationActionWorker<>( действие, ceлектор ).execute( контекст );
        }
    }
    
    protected class ActionExport extends AbstractContextJfxAction<JavaFX>
    {
        final ЭкспортироватьSvg действие;
        final Provider<File> fileToExportToProvider;
        
        ActionExport( ЭкспортироватьSvg действие )
        {
            super( jfx, jfx.словарь( ActionExport.class ) );
            this.действие = действие;
            this.fileToExportToProvider = new ExportFileSelector( jfx );
        }
        
        @Override
        public void handle( ActionEvent __ )
        {
            // собрать выделенные элементы немедленно, ибо список может затем измениться другими процессами
            List<E> сeлектор = new ArrayList<>( getSelectionModel().getSelectedItems() );
            File file = fileToExportToProvider.newInstance();
            if( file != null )
                if( сeлектор.size() == 1 )
                {
                    E элемент = сeлектор.get( 0 );
                    ЭкспортироватьSvg.Контекст к = new ЭкспортироватьSvg.Контекст(
                            контекст.контекст, элемент, new Сборка( элемент ), file );
                    new ApplicationActionWorker<>( действие, к ) // новый, т.к. одноразовый
                            .execute( jfx );
                }
                else
                    LOGGER.log( Level.SEVERE, "Cannot save multiple {0} elements in single file.", сeлектор.size() );
        }
    }
    
    //</editor-fold>
}
