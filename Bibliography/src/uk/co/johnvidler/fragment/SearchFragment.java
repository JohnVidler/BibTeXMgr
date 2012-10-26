package uk.co.johnvidler.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import uk.co.johnvidler.Bibliography;
import uk.co.johnvidler.R;
import uk.co.johnvidler.biblio.bibtex.BibTeXEntry;
import uk.co.johnvidler.storage.local.BibSQL;

/**
 * Created by IntelliJ IDEA.
 * User: john
 * Date: 21/05/11
 * Time: 03:31
 * To change this template use File | Settings | File Templates.
 */
public class SearchFragment extends Fragment
{
    View.OnClickListener searchButtonListener = new View.OnClickListener()
    {
        public void onClick(View v)
        {
            new Thread(new Runnable()
                {
                    public void run()
                    {
                        try
                        {
                            BibSQL sql = Bibliography.mSQL;
                            EditText inputBox = (EditText)getActivity().findViewById(R.id.searchBox);

                            BibTeXEntry entry = sql.getByKey( inputBox.getText().toString() );

                            showToast( "Title: " + entry.getProperty("title") );
                        }
                        catch( Throwable t )
                        {
                            Log.e("SEARCH", ""+t.getMessage());
                        }
                    }
                }).start();
        }
    };

    Handler toastHandler = new Handler()
    {
        @Override
        public void handleMessage( Message msg )
        {
            String msgMsg = msg.getData().getString("message");
            Toast.makeText(getActivity(), msgMsg, Toast.LENGTH_LONG).show();
        }
    };
    public void showToast( String message )
    {
        Message newMsg = Message.obtain();
        newMsg.getData().putString("message", message);
        toastHandler.sendMessage(newMsg);

    }

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState )
    {
        return inflater.inflate( R.layout.search_fragment, container, false );
    }

    @Override
    public void onActivityCreated( Bundle savedInstance )
    {
        super.onActivityCreated( savedInstance );
        Button searchButton = (Button)getActivity().findViewById(R.id.searchButton);
        searchButton.setOnClickListener( searchButtonListener );
    }

}
