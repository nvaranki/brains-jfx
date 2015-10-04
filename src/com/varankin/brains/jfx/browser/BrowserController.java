package com.varankin.brains.jfx.browser;

import com.varankin.brains.appl.ФабрикаНазваний;
import com.varankin.brains.artificial.*;
import com.varankin.brains.factory.Составной;
import com.varankin.brains.jfx.JavaFX;
import com.varankin.brains.jfx.SelectionListBinding;
import com.varankin.characteristic.*;
import java.util.*;
import java.util.logging.*;
import java.util.stream.Collectors;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Builder;

/**
 * FXML-контроллер навигатора по проектам. 
 * 
 * @author &copy; 2015 Николай Варанкин
 */
public class BrowserController implements Builder<TitledPane>
{
    private static final Logger LOGGER = Logger.getLogger(
            BrowserController.class.getName(),
            BrowserController.class.getPackage().getName() + ".text" );
    private static final String RESOURCE_CSS  = "/fxml/browser/Browser.css";
    private static final String CSS_CLASS = "archive";

    public static final String RESOURCE_FXML  = "/fxml/browser/Browser.fxml";
    public static final ResourceBundle RESOURCE_BUNDLE = LOGGER.getResourceBundle();

    private static final ФабрикаНазваний фабрикаНазваний = new ФабрикаНазваний( JavaFX.getInstance().контекст.специфика );
    private static final BrowserRenderer фабрикаКартинок = new BrowserRenderer();
    private static final Comparator<TreeItem<Элемент>> TREE_ITEM_COMPARATOR = 
            Comparator.comparing( (TreeItem<Элемент> ti) -> ti.getValue() instanceof Составной ? -1 : +1 )
            .thenComparing( (TreeItem<Элемент> ti) -> фабрикаНазваний.индекс( ti.getValue() ) )
            .thenComparing( (TreeItem<Элемент> ti) -> ti.toString() );

    @FXML private BrowserToolBarController toolbarController;
    @FXML private BrowserPopupController popupController;
    @FXML private TreeView<Элемент> tree;

    public BrowserController()
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
        popupController = new BrowserPopupController();
        toolbarController = new BrowserToolBarController();
        
        tree = new TreeView<>();
        tree.setShowRoot( true );
        tree.setEditable( false );
        tree.setContextMenu( popupController.build() );
        HBox.setHgrow( tree, Priority.ALWAYS );

        Pane box = new HBox();
        box.getChildren().addAll( toolbarController.build(), tree );
        
        TitledPane pane = new TitledPane( RESOURCE_BUNDLE.getString( "browser.title" ), box );
        
        pane.getStyleClass().add( CSS_CLASS );
        pane.getStylesheets().add( getClass().getResource( RESOURCE_CSS ).toExternalForm() );

        initialize();
        
        return pane;
    }

    @FXML
    protected void initialize()
    {
        tree.setRoot( построитьДерево( JavaFX.getInstance().контекст.мыслитель ) );
        tree.setCellFactory( ( TreeView<Элемент> treeView ) -> new BrowserTreeCell<>( treeView ) );
        
        ActionProcessor processor = new ActionProcessor( 
                new SelectionListBinding<>( tree.getSelectionModel() ) );
        toolbarController.setProcessor( processor );
        popupController.setProcessor( processor );

    }
    
    /**
     * Вычисляет рекомендуемую позицию для вставки узла в список.
     * 
     * @param узел   вставляемый узел.
     * @param список список, куда будет вставлен узел.
     * @param comparator средство сравнения узлов.
     * @return позиция для вставки узла в список.
     */
    private static int позиция( TreeItem<Элемент> узел, 
            List<TreeItem<Элемент>> список, Comparator<TreeItem<Элемент>> comparator )
    {
        int позиция = список.size();
        while( позиция > 0 && comparator.compare( узел, список.get( позиция - 1 ) ) < 0 ) позиция--;
        return позиция;
    }
    
    private static TreeItem<Элемент> построитьДерево( Элемент элемент )
    {
        TreeItem<Элемент> treeItem = new TreeItem<Элемент>( элемент, фабрикаКартинок.getIcon( элемент ) )
        {
            private String метка;
            
            @Override
            public String toString()
            {
                return метка != null ? метка : ( метка = фабрикаНазваний.метка( (Object)getValue() ) );
            }
        };
        List<TreeItem<Элемент>> список = treeItem.getChildren();
        Collections.singleton( элемент ).stream()
                .filter( (Элемент э) -> э instanceof Составной )
                .flatMap( (Элемент э) -> ((Составной)э).состав().stream() )
                .filter( (Object o) -> o instanceof Элемент )
                .forEach((Object o) -> 
                {
                    TreeItem<Элемент> дерево = построитьДерево( (Элемент)o );
                    список.add( позиция( дерево, список, TREE_ITEM_COMPARATOR ), дерево );
                });
        установитьНаблюдение( элемент, список );
        return treeItem;
    }
    
    private static void разобратьДерево( Элемент элемент, Collection<TreeItem<Элемент>> список )            
    {
        снятьНаблюдение( элемент );
        список.removeAll( список.stream()
                .filter( (TreeItem<Элемент> ti) -> ti.getValue().equals( элемент ) )
                .peek( (TreeItem<Элемент> ti) -> 
                {
                    new ArrayList<>( ti.getChildren() ).stream().forEach( (TreeItem<Элемент> cti) -> 
                            разобратьДерево( cti.getValue(), ti.getChildren() ) );
                     
                } )
                .collect( Collectors.toList() ) );
    }

    private static void установитьНаблюдение( Элемент элемент, Collection<TreeItem<Элемент>> список )
    {
        Collections.singleton( элемент ).stream()
                .filter( (Элемент э) -> э instanceof Составной )
                .map( (Элемент э) -> ((Составной)э).состав() )
                .filter( ( Collection c) -> c instanceof Наблюдаемый )
                .map( (Collection c) -> ((Наблюдаемый)c).наблюдатели() )
                .forEach( (Collection c) -> c.add( new Составитель( список ) ) );
    }

    private static void снятьНаблюдение( Элемент элемент )
    {
        Collections.singleton( элемент ).stream()
                .filter( (Элемент э) -> э instanceof Составной )
                .map( (Элемент э) -> ((Составной)э).состав() )
                .filter( ( Collection c) -> c instanceof Наблюдаемый )
                .map( (Collection c) -> ((Наблюдаемый)c).наблюдатели() )
                .forEach( (Collection c) -> 
                { 
                    Collection<Наблюдатель<?>> cx = c;
                    cx.removeAll( cx.stream()
                        .filter( (Наблюдатель<?> e) -> e instanceof Составитель )
                        .collect( Collectors.toList() ) );
                } );
    }

    private static class Составитель implements Наблюдатель<Элемент> 
    {
        final Collection<TreeItem<Элемент>> СПИСОК;

        Составитель( Collection<TreeItem<Элемент>> список ) 
        {
            СПИСОК = список;
        }

        @Override
        public void отклик( Изменение<Элемент> изменение ) 
        {
            if( изменение.ПРЕЖНЕЕ != null )
            {
                BrowserController.разобратьДерево( изменение.ПРЕЖНЕЕ, СПИСОК );
            }
            if( изменение.АКТУАЛЬНОЕ != null )
            {
                СПИСОК.add( BrowserController.построитьДерево( изменение.АКТУАЛЬНОЕ ) );
            }
        }
        
    }

}
