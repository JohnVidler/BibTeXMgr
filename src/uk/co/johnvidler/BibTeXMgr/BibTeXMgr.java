package uk.co.johnvidler.BibTeXMgr;

import uk.co.johnvidler.BibTeXMgr.panels.ListTaskPanel;
import uk.co.johnvidler.BibTeXMgr.ui.MainWindow;

/**
 * Created by IntelliJ IDEA.
 * User: john
 * Date: 30/03/11
 * Time: 16:55
 * To change this template use File | Settings | File Templates.
 */
public class BibTeXMgr
{

    public static void main( String args[] ){ new BibTeXMgr( args ); }
    public BibTeXMgr( String args[] )
    {

        MainWindow win = new MainWindow();

        ListTaskPanel listTask = new ListTaskPanel();

        listTask.setColumns( new String[]{"Type", "Key", "Title", "Author", "Pages", "Volume", "Note", "Year"} );

        win.setCurrentTaskPanel( listTask );

    }

}
