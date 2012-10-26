package uk.co.johnvidler.storage.local;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import uk.co.johnvidler.biblio.Entry;
import uk.co.johnvidler.biblio.bibtex.BibTeXEntry;
import uk.co.johnvidler.storage.BackingStore;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class BibSQL extends SQLiteOpenHelper implements BackingStore
{
    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "bibliography";

    private static final String ENTRIES_DB = "entries";
    private static final String PROPERTIES_DB = "properties";

    private static final String CREATE_BASE_TABLE = "CREATE TABLE IF NOT EXISTS " +ENTRIES_DB+ " ( "
            + "key UNIQUE ON CONFLICT FAIL, "
            + "type TEXT );";

    private static final String CREATE_EXTRA_TABLE = "CREATE TABLE IF NOT EXISTS " +PROPERTIES_DB+ " ( "
            + "key TEXT, "
            + "name TEXT, "
            + "value TEXT );";

    public SQLiteDatabase mWriteDB = null;
    public SQLiteDatabase mReadDB = null;
    

    public BibSQL( Context context )
    {
        super( context, DATABASE_NAME, null, DATABASE_VERSION );
    }

    @Override
    public void onCreate( SQLiteDatabase db )
    {
        db.execSQL( CREATE_BASE_TABLE );
        db.execSQL( CREATE_EXTRA_TABLE );
    }

    @Override
    public void onUpgrade( SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion )
    {
        Log.e("SQLite", "Database is requesting an upgrade!");
    }

    public void update( Entry entry )
    {
        if( mWriteDB == null )
            mWriteDB = getWritableDatabase();

        String key = entry.getProperty("key");
        String type = entry.getProperty("type");

        if( key == null || type == null )
            return;

        /* Insert the new entry in the base table */
        mWriteDB.execSQL( "INSERT OR REPLACE INTO " +ENTRIES_DB+ " (key,type) VALUES(?,?)", new String[]{ key, type });

        /* Insert the properties for this entry */
        for( String property : entry.getProperties() )
        {
            String value = entry.getProperty(property);

            mWriteDB.execSQL( "INSERT OR REPLACE INTO " +PROPERTIES_DB+ " (key,name,value) VALUES(?,?,?)", new String[]{ key, property, value });
        }
    }

    public TreeMap<String, String> getKeyTitleMapping()
    {
        if( mReadDB == null )
            mReadDB = getReadableDatabase();

        TreeMap<String, String> keyTitleMap = new TreeMap<String, String>();
        try
        {
            Cursor keys = mReadDB.query( ENTRIES_DB, new String[]{ "key" }, null, null, null, null, null );

            while( keys.moveToNext() )
            {
                String key = keys.getString(0);
                String title = getTitleForKey( key );

                keyTitleMap.put( key, title );
            }
        }
        catch( Throwable t )
        {
            /* This should log someplace... oops. */
        }

        return keyTitleMap;
    }

    public String[] getKeys()
    {
        List<String> keyArray = getKeysAsList();
        return keyArray.toArray(new String[keyArray.size()]);
    }

    public List<String> getKeysAsList()
    {
        if( mReadDB == null )
            mReadDB = getReadableDatabase();

        ArrayList<String> keyArray = new ArrayList<String>();
        try
        {
            Cursor keys = mReadDB.query( ENTRIES_DB, new String[]{ "key" }, null, null, null, null, null );

            while( keys.moveToNext() )
                keyArray.add( keys.getString(0) );
        }
        catch( Throwable t )
        {
            /* This should log someplace... oops. */
        }

        return keyArray;
    }

    public String[] getValuesForProperty( String property )
    {
        if( mReadDB == null )
            mReadDB = getReadableDatabase();

        ArrayList<String> valueArray = new ArrayList<String>();
        try
        {
            Cursor keys = mReadDB.query( PROPERTIES_DB, new String[]{ "value" }, "name = ?", new String[]{ property }, null, null, null );

            while( keys.moveToNext() )
                valueArray.add( keys.getString(0) );
        }
        catch( Throwable t )
        {
            /* This should log someplace... oops. */
        }

        return valueArray.toArray(new String[valueArray.size()]);
    }

    public String getTitleForKey( String key )
    {
        if( mReadDB == null )
            mReadDB = getReadableDatabase();

        try
        {
            Cursor title = mReadDB.query( PROPERTIES_DB, new String[]{ "value" }, "key = ?", new String[]{ key }, null, null, null );

            if( title.getCount() == 0 )
                return null;

            return title.getString(0);
        }
        catch( Throwable t )
        {
            /* This should log someplace... oops. */
        }

        return "";
    }

    public BibTeXEntry getByKey( String key )
    {
        if( mReadDB == null )
            mReadDB = getReadableDatabase();

        try
        {
            Cursor entryCursor = mReadDB.query( ENTRIES_DB, new String[]{ "key", "type" }, "key = ?", new String[]{ key }, null, null, null );
            if( entryCursor.getCount() == 0 )
                return null;
            entryCursor.moveToFirst();

            Log.e("SQL", ""+entryCursor.getColumnCount() );
            for ( int i=0; i<entryCursor.getColumnCount(); i++ )
                Log.e("SQL", "    " + entryCursor.getColumnName( i ) );

            BibTeXEntry entry = new BibTeXEntry( entryCursor.getString(0), entryCursor.getString(1) );

            Cursor properties = mReadDB.query( PROPERTIES_DB, new String[]{ "name","value" }, "key = ?", new String[]{ key }, null, null, null );
            properties.moveToFirst();
            for( int i=0; i<properties.getCount(); i++ )
            {
                String name = properties.getString(0);
                String value = properties.getString(1);

                entry.setProperty(name, value);

                properties.moveToNext();
            }

            return entry;
        }
        catch( Throwable t )
        {
            /* This should log someplace... oops. */
        }
        return null;
    }

    @Override
    public long getTotalEntries() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean saveEntry(Entry entry) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<Entry> loadEntryByKey(String key) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<Entry> loadEntryByTitle(String title) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
