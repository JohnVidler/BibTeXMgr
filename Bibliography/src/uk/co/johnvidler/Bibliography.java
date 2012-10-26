package uk.co.johnvidler;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import uk.co.johnvidler.biblio.Entry;
import uk.co.johnvidler.biblio.bibtex.BibTeXEntry;
import uk.co.johnvidler.biblio.bibtex.BibTeXReader;
import uk.co.johnvidler.storage.local.BibSQL;
import uk.co.johnvidler.util.DialogUtils;

import java.util.TreeSet;

public class Bibliography extends Activity
{
    public static final int DIALOG_ALERT = 0;
    public static final int DIALOG_LOADING = 1;

    public static final int DATA_IMPORT = 0;

    public static BibSQL mSQL = null;

    private Bibliography self = null;

    public ProgressDialog mProgressDLG = null;
    public AlertDialog mAlertDLG = null;
    public Handler mHandler = new Handler();

    /** Called when the activity is first created. */
    @Override
    public void onCreate( Bundle savedInstanceState )
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        /* Hook ourselves! */
        self = this;

        mSQL = new BibSQL( this );

        /* Dialogs */
        mProgressDLG = DialogUtils.createProgressDialog(this, getString(R.string.dialog_loading));
        mAlertDLG = DialogUtils.createAlertDialog( this, "Note: This software is BETA and may break with no prior warning!" );

        /* Tell the users this is BETA! */
        //mAlertDLG.show();

        /* Ensure we get an action bar, it's fairly critical! */
        final ActionBar actionBar = getActionBar();
        actionBar.show();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        
        if( mSQL.mReadDB != null )
            mSQL.mReadDB.close();
        
        if( mSQL.mWriteDB != null )
            mSQL.mWriteDB.close();
    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu )
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate( R.menu.actionbar_main_menu, menu );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item )
    {
        switch ( item.getItemId() )
        {
            case R.id.menu_open:
                final Resources res = getResources();
                
                new Thread(new Runnable()
                {
                    public void run()
                    {
                        Entry entry = null;

                        try
                        {
                            TreeSet<Entry> entries = new TreeSet<Entry>();
                            BibTeXReader reader = new BibTeXReader( res.openRawResource(R.raw.mid) );

                            int count = 0;
                            entry = reader.read();
                            while( entry != null )
                            {
                                /* Prepare the entry for database loading, require a key and type */
                                showProgress( "Importing: " +entry.getProperty("key"), 33 );
                                if( !entry.hasProperty( "key" ) )
                                    entry.setProperty( "key", "entry:"+count );

                                if( !entry.hasProperty( "type" ) )
                                    entry.setProperty( "type", "null" );

                                /* Insert in to the database! */
                                showProgress( "Importing: " +entry.getProperty("key"), 66 );
                                mSQL.update( entry );

                                /* Done! */
                                showProgress( "Importing: " +entry.getProperty("key"), 100 );
                                count++;
                                entry = reader.read();
                            }
                            showProgress( "Done!", -1 );

                            showAlert( "Loaded " +count+ " entries!" );

                        }
                        catch( Throwable t )
                        {
                            Log.e("Loader", t.getMessage() );
                            printStackTrace( "Loader", t );

                            showProgress( "", -1 );
                            if( entry != null )
                                showAlert( "An error occurred whilst importing " +entry.getProperty("key") );
                            else
                                showAlert( "An error occurred whilst importing an entry!\n(Sorry, I'm not sure which one!)" );
                        }
                    }
                }).start();
                
                return true;

            case R.id.menu_edit:
                BibTeXEntry entry = mSQL.getByKey( "ADSh:81" );

                // Do STUFF!

                return true;

            case R.id.menu_purge_db:
                if( mSQL.mWriteDB == null || !mSQL.mWriteDB.isOpen() )
                    mSQL.mWriteDB = mSQL.getWritableDatabase();
                
                mSQL.mWriteDB.execSQL( "DROP TABLE IF EXISTS entries" );
                mSQL.mWriteDB.execSQL( "DROP TABLE IF EXISTS properties" );

                mSQL.mWriteDB.close();

                showAlert( "Purged all data!" );

                return true;

            default:
                return super.onOptionsItemSelected( item );
        }

    }


    public void showAlert( final String message )
    {
        mHandler.post( new Runnable() {
            public void run() {
                mAlertDLG.setMessage( message );
                mAlertDLG.show();
            }
        } );
    }

    public void showProgress( final String message, final int progress )
    {
        if( progress < 0 )
        {
            mHandler.post( new Runnable() {
                public void run() {
                    mProgressDLG.hide();
                }
            } );
            return;
        }

        mHandler.post( new Runnable() {
            public void run() {
                mProgressDLG.setMessage( message );
                mProgressDLG.setProgress( progress );
                mProgressDLG.show();
            }
        } );
    }


    private void printStackTrace( String tag, Throwable t )
    {
        for( StackTraceElement e : t.getStackTrace() )
        {
            Log.e( tag, e.toString() );
        }
    }
}
