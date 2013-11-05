package com.varankin.brains.jfx;

import com.varankin.brains.Контекст;
import java.util.Arrays;
import java.util.List;
import javafx.stage.Stage;

/**
 * Приложение для среды JavaFX.
 *
 * @author &copy; 2013 Николай Варанкин
 */
public class Application extends javafx.application.Application
{
    private volatile Контекст контекст;
    
    public Application() {}
    
    @Override
    public void init() throws Exception
    {
        List<String> parameters = getParameters().getRaw();
        контекст = new Контекст( parameters.toArray( new String[parameters.size()] ) );
    }
    
    @Override
    public void start( Stage primaryStage )
    {
        контекст.старт();
        JavaFX.newInstance( primaryStage, контекст ).старт();
    }
    
    @Override
    public void stop() throws Exception
    {
        JavaFX.getInstance().стоп();
        контекст.стоп( "main", "JavaFX-Launcher", "JavaFX Application Thread" );
    }
    
    /**
     * Запускает приложение из JVM.
     * 
     * @param args the command line arguments
     */
    public static void main( String[] args )
    {
        launch( args );
    }

}
