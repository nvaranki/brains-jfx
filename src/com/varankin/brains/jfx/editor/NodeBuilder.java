package com.varankin.brains.jfx.editor;

import com.varankin.brains.db.DbАтрибутный;
import java.util.List;
import java.util.Queue;
import javafx.scene.Node;

/**
 *
 * @author Varankine
 */
public interface NodeBuilder
{
    Node загрузить( boolean изменяемый );
    
    Node загрузить( boolean изменяемый, Queue<int[]> path );
    
    List<DbАтрибутный.Ключ> компоненты();
}
