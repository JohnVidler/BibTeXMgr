package uk.co.johnvidler.biblio.bibtex;

import uk.co.johnvidler.biblio.Entry;

import java.io.Serializable;

public class BibTeXEntry extends Entry implements Serializable
{
    public BibTeXEntry()
    {
        /* Stub, for extension */
    }

    public BibTeXEntry( String type, String key )
    {
        setProperty("type", type);
        setProperty("key", key);
    }

    public String getType(){ return getProperty("type"); }
    public String getKey(){ return getProperty("key"); }

    public void setKey( String newKey ){ setProperty("key", newKey); }
    public void setType( String newType ){ setProperty("type", newType); }
    
}
