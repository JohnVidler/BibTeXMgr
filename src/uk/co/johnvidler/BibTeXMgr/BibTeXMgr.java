package uk.co.johnvidler.BibTeXMgr;

import uk.co.johnvidler.BibTeXMgr.panels.ListTaskPanel;
import uk.co.johnvidler.BibTeXMgr.ui.MainWindow;
import uk.co.johnvidler.biblio.Entry;
import uk.co.johnvidler.biblio.bibtex.BibTeXReader;
import uk.co.johnvidler.biblio.bibtex.BibTeXWriter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;

public class BibTeXMgr
{

    public static void main( String args[] ){ new BibTeXMgr( args ); }
    public BibTeXMgr( String args[] )
    {

        MainWindow win = new MainWindow();

        ListTaskPanel listTask = new ListTaskPanel();

        listTask.setColumns( new String[]{"Type", "Key", "Title", "Author", "Pages", "Volume", "Note", "Year"} );

        win.setCurrentTaskPanel( listTask );

        try
        {
            //BibTeXReader reader = new BibTeXReader( new BufferedReader( new FileReader( new File("/home/john/example.bib") ) ) );
            BibTeXReader reader = new BibTeXReader( new BufferedReader( new FileReader( new File("/home/john/small.bib") ) ) );
            BibTeXWriter writer = new BibTeXWriter( new PrintWriter(System.out) );

            int count = 0;
            Entry entry = reader.read();
            while ( entry != null )
            {

                writer.write( entry );

                count++;
                entry = reader.read();
            }
            System.out.println( "Total: " + count );
            
        }
        catch( Throwable t )
        {
            t.printStackTrace();
        }

    }

}
