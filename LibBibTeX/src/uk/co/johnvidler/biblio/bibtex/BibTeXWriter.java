package uk.co.johnvidler.biblio.bibtex;

import uk.co.johnvidler.biblio.Entry;
import uk.co.johnvidler.biblio.EntryWriter;

import java.io.BufferedWriter;
import java.io.PrintWriter;

public class BibTeXWriter implements EntryWriter
{
    protected PrintWriter writer = null;
    
    public BibTeXWriter( BufferedWriter writer )
    {
        this.writer = new PrintWriter( writer );
    }

    public BibTeXWriter( PrintWriter writer )
    {
        this.writer = writer;
    }
    
    public void write(Entry entry)
    {
        writer.append( "@" ).append( entry.getProperty("type") ).append( "{\n\t" ).append( entry.getProperty("key") );
        for( String key : entry.getProperties() )
        {
            String property = entry.getProperty(key).toString();

            if( property.startsWith("{") )
                writer.append( ",\n\t" ).append( key ).append( " = " ).append( property );
            else
                writer.append( ",\n\t" ).append( key ).append( " = \"" ).append( property ).append( "\"" );
        }
        writer.append( "\n},\n" );

        writer.flush();
    }
}
