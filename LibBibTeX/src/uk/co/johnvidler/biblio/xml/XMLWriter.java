package uk.co.johnvidler.biblio.xml;

import uk.co.johnvidler.biblio.Entry;
import uk.co.johnvidler.biblio.EntryWriter;

import java.io.BufferedWriter;
import java.io.PrintWriter;

public class XMLWriter implements EntryWriter
{
    protected PrintWriter writer = null;

    public XMLWriter( BufferedWriter writer )
    {
        this.writer = new PrintWriter( writer );
    }

    public XMLWriter( PrintWriter writer )
    {
        this.writer = writer;
    }

    public void write( Entry entry )
    {
        writer.println( "<entry>" );

        for( String key : entry.getProperties() )
        {
            writer.print("<" + key + ">");
            writer.print("<![CDATA[" +entry.getProperty(key)+ "]]>");
            writer.println("</" + key + ">");
        }

        writer.println( "</entry>" );
        writer.flush();
    }
}
