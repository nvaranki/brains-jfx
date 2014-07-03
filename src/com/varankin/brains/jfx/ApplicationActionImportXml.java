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
 * @author &copy; 2014 Николай Варанкин
 */
class ApplicationActionImportXml extends CoordinatedAction<Импортировать.Контекст>
{
    private final Provider<Provider<InputStream>> селектор;
    private final HistoryList<Provider<InputStream>> история;
    
    /**
     * @param действие согласованное действие для выполнения.
     * @param селектор генератор поставщика потоков XML, для запуска в среде JavaFX.
     * @param история  менеджер истории, допускается {@code null}. 
     */
    ApplicationActionImportXml( 
            СогласованноеДействие<Импортировать.Контекст> действие, 
            Provider<Provider<InputStream>> селектор, 
            HistoryList<Provider<InputStream>> история )
    {
        super( действие, null, JavaFX.getInstance(), селектор.getClass().getSimpleName() );
        this.история = история;
        this.селектор = селектор;
    }

    @Override
    public void handle( ActionEvent event )
    {
        Provider<InputStream> provider = селектор.newInstance();
        if( provider != null )
        {
            final Импортировать.Контекст к = new Импортировать.Контекст( provider, jfx.контекст.архив );
            new ApplicationActionWorker<Импортировать.Контекст>( действие, к ) // новый, т.к. одноразовый
            {
                @Override
                protected void succeeded()
                {
                    super.succeeded();
                    if( история != null )
                        история.advance( к.поставщик() );
                }            
            }.execute( jfx );
        }
        event.consume();
    }
    
}
