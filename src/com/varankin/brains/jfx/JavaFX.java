package com.varankin.brains.jfx;

import com.varankin.brains.artificial.io.Фабрика;
import com.varankin.brains.db.Коллекция;
import com.varankin.brains.db.Элемент;
import com.varankin.brains.Контекст;
import com.varankin.util.Текст;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.ObservableValueBase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * Контекст среды JavaFX.
 *
 * @author &copy; 2012 Николай Варанкин
 */
public final class JavaFX 
{
    private static JavaFX JFX;
    
    public static JavaFX getInstance()
    {
        return JFX;
    }
    
    static JavaFX newInstance( Stage платформа, Контекст контекст )
    {
        return JFX = new JavaFX( платформа, контекст );
    }
    
    public static final String STYLE_WRONG_TEXT_VALUE = "-fx-text-fill: red;";

    public final Контекст контекст;
    public final Stage платформа;
    //public final Позиционер позиционер;
    private final ObservableObjectList<TitledSceneGraph> views;
    private final ExecutorService es;
    private final ScheduledExecutorService ses;

    /**
     * @param платформа первичная платформа приложения JavaFX.
     * @param контекст  контекст приложения.
     */
    private JavaFX( Stage платформа, Контекст контекст )
    {
        this.платформа = платформа;
        this.контекст = контекст;
        //this.позиционер = new Позиционер();
        es = new ThreadPoolExecutor( //TODO load preferences
            0, Integer.MAX_VALUE,
            60L, TimeUnit.SECONDS,
            new SynchronousQueue<>() );
        ses = new ScheduledThreadPoolExecutor( 10 ); //TODO appl config
        views = new ObservableObjectList<>( new ArrayList<TitledSceneGraph>() );
    }
    
    ObservableValue<ObservableList<TitledSceneGraph>> getViews()
    {
        return views;
    }
    
    TitledSceneGraph isShown( Элемент элемент, Predicate<? super TitledSceneGraph> predicate )
    {
        Optional<TitledSceneGraph> o = views.getValue().stream()
            .filter( predicate )
            .filter( ( TitledSceneGraph tsg ) -> элемент.equals( tsg.node.getUserData() ) )
            .findFirst();
        return o.isPresent() ? o.get() : null;
    }
    
    <E extends Элемент> TitledSceneGraph show( E элемент, Predicate<? super TitledSceneGraph> predicate, 
            Фабрика<E,TitledSceneGraph> фабрика )
    {
        List<TitledSceneGraph> список = views.getValue();
        TitledSceneGraph tsg = isShown( элемент, predicate );
        if( tsg == null )
            список.add( tsg = фабрика.создать( элемент ) );
        else
            //TODO временный обходной вариант для активации view
            список.set( список.indexOf( tsg ), new TitledSceneGraph( tsg.node, tsg.title ) );
        return tsg;
    }

    /**
     * Открывает HTML-ссылку в стандартном браузере операционной системы.
     * 
     * @param href HTML-ссылка.
     * 
     * @throws IOException при невозможности вызвать браузер операционной системы.
     * @throws MalformedURLException при некорректной ссылке.
     * @throws URISyntaxException при некорректной ссылке.
     * @throws UnsupportedOperationException если десктоп не поддерживается.
     */
    void browse( String href ) throws MalformedURLException, URISyntaxException, IOException
    {
        if( Desktop.isDesktopSupported() )
        {
            Desktop.getDesktop().browse( new URL( href ).toURI() );
        }
        else
        {
            throw new UnsupportedOperationException( "No desktop supported." );
        }
    }

    public double getDefaultGap()
    {
        return 10; //TODO params
    }

    /**
     * @return сервис для запуска {@linkplain Task задач}.
     */
    public ExecutorService getExecutorService()
    {
        return es;
    }
    
    /**
     * @return сервис для запуска повторяющихся {@linkplain Task задач}.
     */
    public final ScheduledExecutorService getScheduledExecutorService()
    {
        return ses;
    }
    
    void старт()
    {
        new GuiBuilder( this ).createView( платформа );
        платформа.show();
        es.execute( new Task<Void>()
        {
            @Override
            protected Void call() throws Exception
            {
                //TODO should not be called!!!
                контекст.архив.проекты().setPropertyValue( Коллекция.PROPERTY_UPDATED, Boolean.TRUE );
                контекст.архив.библиотеки().setPropertyValue( Коллекция.PROPERTY_UPDATED, Boolean.TRUE );
                return null;
            }
        } );
        Runnable r = () -> { Font.getFamilies(); };
        Thread t = new Thread( r, "Font Family loader" );
        t.setPriority( Thread.currentThread().getPriority() - 1 );
        t.start();
    }

    void стоп()
    {
        Parent root = платформа.getScene().getRoot();
        ((Pane)root).getChildren().clear(); // brute force signal TODO validate approach
        es.shutdown();
        ses.shutdown();
        платформа.close();
    }
    
    public Текст словарь( Class<?> c )
    {
        return Текст.ПАКЕТЫ.словарь( c, контекст.специфика );
    }

    File getCurrentLocalDirectory()
    {
        return new File( System.getProperty( "user.dir" ) );
    }

    public static ImageView icon( String path )
    {
        InputStream stream = path.isEmpty() ? null : JavaFX.class.getClassLoader().getResourceAsStream( path );
        return stream != null ? new ImageView( new Image( stream ) ) : null;
    }

    public static void copyMenuItems( List<? extends MenuItem> from, List<MenuItem> to )
    {
        for( MenuItem оригинал : from )
        {
            Node graphic = оригинал.getGraphic();
            if( graphic instanceof ImageView )
                graphic = new ImageView( ((ImageView)graphic).getImage() );

            if( оригинал instanceof SeparatorMenuItem )
            {
                to.add( new SeparatorMenuItem() );
            }
            else if( оригинал instanceof Menu )
            {
                Menu копия = new Menu();
                копия.setText( оригинал.getText() );
                копия.setGraphic( graphic );
                copyMenuItems( ((Menu)оригинал).getItems(), копия.getItems() );
                to.add( копия );
            }
            else
            {
                MenuItem копия = new MenuItem();
                копия.setText( оригинал.getText() );
                копия.setGraphic( graphic );
                копия.setOnAction( оригинал.getOnAction() );
                to.add( копия );
            }
        }
    }
    
    public static void copyMenuItems( List<? extends MenuItem> from, List<MenuItem> to, boolean separate )
    {
        if( from != null && !from.isEmpty() && to != null ) 
        {
            if( !to.isEmpty() && separate )
                to.add( new SeparatorMenuItem() );
            copyMenuItems( from, to );
        }
    }

    public boolean useFxmlLoader()
    {
        return false; //TODO appl. setup
    }

    //<editor-fold defaultstate="collapsed" desc="классы">
    
    private class ObservableObjectList<T> extends ObservableValueBase<ObservableList<T>>
    {
        final ObservableList<T> value;

        ObservableObjectList( List<T> storage )
        {
            value = FXCollections.observableList( storage );
        }
        
        @Override
        public final ObservableList<T> getValue()
        {
            return value;
        }

    }
    
    //</editor-fold>

}
