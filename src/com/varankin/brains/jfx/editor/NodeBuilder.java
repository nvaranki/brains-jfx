package com.varankin.brains.jfx.editor;

import com.varankin.brains.db.type.DbАтрибутный;
import com.varankin.brains.db.xml.МаркированныйЗонныйКлюч;
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
    
    boolean составить( Queue<int[]> path );
    
    List<МаркированныйЗонныйКлюч> компоненты();
}
