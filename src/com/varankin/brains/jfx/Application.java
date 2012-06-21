package com.varankin.brains.jfx;

import com.varankin.brains.Контекст;
import java.util.List;
import javafx.stage.Stage;

/**
 * Приложение для среды JavaFX.
 *
 * @author &copy; 2012 Николай Варанкин
 */
public class Application extends javafx.application.Application
{
    private volatile Контекст контекст;
    private JavaFX jfx;
    
    /**
     * @param args the command line arguments
     */
    public static void main( String[] args )
    {
        launch( args );
    }
    
    @Override
    public void init() throws Exception
    {
        List<String> parameters = getParameters().getRaw();
        контекст = new Контекст( parameters.toArray( new String[parameters.size()] ) );
    }
    
    @Override
    public void start( Stage primaryStage )
    {
        jfx = new JavaFX( primaryStage, контекст );
        primaryStage.setScene( new ApplicationView( jfx ) );
        primaryStage.show();
        jfx.старт();
    }
    
    @Override
    public void stop() throws Exception
    {
        jfx.стоп();
    }
}
