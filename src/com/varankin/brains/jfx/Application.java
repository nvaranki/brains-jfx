package com.varankin.brains.jfx;

import com.varankin.brains.Контекст;
import java.util.List;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * Приложение для среды JavaFX.
 *
 * @author &copy; 2023 Николай Варанкин
 */
public class Application extends javafx.application.Application
{
    public Application() {}
    
    @Override
    public void init() throws Exception
    {
        List<String> parameters = getParameters().getRaw();
        Контекст.newInstance( parameters.toArray( String[]::new ) ).старт();
        Thread t = new Thread( Font::getFamilies, "Font Family loader" );
        t.setPriority( Thread.currentThread().getPriority() - 1 );
        t.start();
    }
    
    @Override
    public void start( Stage primaryStage )
    {
        JavaFX.newInstance( primaryStage, Контекст.getInstance() ).старт();
    }
    
    @Override
    public void stop() throws Exception
    {
        JavaFX.getInstance().стоп();
        super.stop();
        Контекст.getInstance().стоп( "main", "JavaFX-Launcher", "JavaFX Application Thread" );
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
