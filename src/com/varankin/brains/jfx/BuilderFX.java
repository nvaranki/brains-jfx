package com.varankin.brains.jfx;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.util.Builder;

/**
 * Построитель узлов графа сцены.
 * 
 * @author &copy; 2014 Николай Варанкин
 */
public final class BuilderFX<N extends Node, C extends Builder<N>>
{
    private N node;
    private C controller;
    
    public N getNode()
    {
        return node;
    }
    
    public C getController()
    {
        return controller;
    }
    
    /**
     * Настройка построителя для использования FXML.
     * 
     * @param fxml    путь к ресурсу в проекте.
     * @param каталог каталог локализованных текстов.
     */
    public void init( URL fxml, ResourceBundle каталог )
    {
        try
        {
            FXMLLoader fxmlLoader = new FXMLLoader( fxml, каталог );
            node = (N)fxmlLoader.load();
            controller = fxmlLoader.getController();
        }
        catch( IOException ex )
        {
            throw new RuntimeException( ex );
        }
    }
    
    /**
     * Настройка построителя для использования контроллера.
     * 
     * @param класс класс контроллера создаваемого узла.
     */
    public void init( Class<C> класс )
    {
        try
        {
            controller = класс.newInstance();
        }
        catch( InstantiationException | IllegalAccessException ex )
        {
            throw new RuntimeException( ex );
        }
        node = controller.build();
    }
    
    /**
     * Универсальная настройка построителя для использования FXML или контроллера,
     * в зависимости от конфигурации приложения.
     * 
     * @see JavaFX#useFxmlLoader() 
     * 
     * @param класс   класс контроллера создаваемого узла.
     * @param fxml    путь к FXML-ресурсу в проекте.
     * @param каталог каталог локализованных текстов.
     */
    public void init( Class<C> класс, String fxml, ResourceBundle каталог )
    {
        if( JavaFX.getInstance().useFxmlLoader() )
        {
            init( класс.getResource( fxml ), каталог );
        }
        else
        {
            init( класс );
        }
    }
    
}
