package com.varankin.brains.jfx;

import javafx.beans.value.ObservableStringValue;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.image.Image;

/**
 * Контейнер озаглавленного {@linkplain Node узла} графа сцены.
 *
 * @author &copy; 2012 Николай Варанкин
 */
@Deprecated // use Node.getProperties()
public class TitledSceneGraph 
{
    public final Node node;
    public final Image icon;
    public final ObservableValue<String> title;

    public TitledSceneGraph( Node node, Image icon, ObservableValue<String> title )
    {
        this.node = node;
        this.icon = icon;
        this.title = title;
    }

}
