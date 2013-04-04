package com.varankin.brains.jfx;

import com.varankin.biz.action.СогласованноеДействие;
import com.varankin.brains.appl.Импортировать;
import com.varankin.io.container.Provider;
import com.varankin.util.HistoryList;
import java.io.InputStream;
import javafx.event.ActionEvent;

/**
 * Действие JavaFX для импорта данных о мыслительной структуре в локальный архив.
 * Источником служит файл формата XML.
 *
 * @author &copy; 2013 Николай Варанкин
 */
class ApplicationActionImportXml extends CoordinatedAction<Импортировать.Контекст>
{
    private final Provider<Provider<InputStream>> селектор;
    private final HistoryList<Provider<InputStream>> история;
    
    /**
     * @param действие согласованное действие для выполнения.
     * @param контекст контекст действия.
     * @param jfx      контекст JavaFX.
     * @param селектор генератор поставщика потоков XML, для запуска в среде JavaFX.
     * @param история  менеджер истории, допускается {@code null}. 
     */
    ApplicationActionImportXml( 
            СогласованноеДействие<Импортировать.Контекст> действие, 
            Импортировать.Контекст контекст, JavaFX jfx, 
            Provider<Provider<InputStream>> селектор, 
            HistoryList<Provider<InputStream>> история )
    {
        super( действие, контекст, jfx, селектор.getClass().getSimpleName() );
        this.история = история;
        this.селектор = селектор;
    }

    @Override
    public void handle( ActionEvent _ )
    {
        if( селектор != null && ( контекст.поставщик = селектор.newInstance() ) != null )
            new ApplicationActionWorker<Импортировать.Контекст>( действие, контекст ) // новый, т.к. одноразовый
            {
                @Override
                protected void succeeded()
                {
                    super.succeeded();
                    if( история != null )
                        история.advance( контекст.поставщик );
                }            
            }.execute( jfx );
    }
    
}
