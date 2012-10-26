package uk.co.johnvidler.fragment;

import android.app.Fragment;
import android.content.Context;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import uk.co.johnvidler.R;
import uk.co.johnvidler.storage.local.BibSQL;

import java.util.ArrayList;
import java.util.List;

public class ListFragment extends Fragment
{
    public ListView list = null;
    public BibSQL mSQL = null;
    public List<String> mKeys = new ArrayList<String>();

    private ListAdapter adapter = new ListAdapter()
    {
        ArrayList<DataSetObserver> observers = new ArrayList<DataSetObserver>();

        public boolean areAllItemsEnabled() {
            return false;
        }

        public boolean isEnabled(int i) {
            return false;
        }

        public void registerDataSetObserver(DataSetObserver dataSetObserver) {
            observers.add( dataSetObserver );
        }

        public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {
            observers.remove(dataSetObserver);
        }

        public int getCount() { return mKeys.size(); }
        public Object getItem( int i ) { return mKeys.get(i); }
        public long getItemId( int i ) { return 0; }

        public boolean hasStableIds() {
            return false;
        }

        public View getView(int i, View oldView, ViewGroup viewGroup)
        {
            if( oldView == null )
            {
                try
                {
                    LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService( Context.LAYOUT_INFLATER_SERVICE );
                    oldView = inflater.inflate( R.layout.list_row, viewGroup, false );

                    TextView content = (TextView)oldView.findViewById( R.id.list_content );
                    CheckBox check = (CheckBox)oldView.findViewById( R.id.list_checkbox );

                    content.setText(mKeys.get(i));
                    check.setChecked( false );
                }
                catch( Throwable t )
                {
                    Log.e( "ADAPTER", t.getMessage() );
                }
            }

            return oldView;
        }

        public int getItemViewType(int i) {
            return 0;
        }

        public int getViewTypeCount() {
            return 1;
        }

        public boolean isEmpty() {
            return mKeys.isEmpty();
        }
    };


    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState )
    {
        return inflater.inflate( R.layout.list_fragment, container, false );
    }

    @Override
    public void onActivityCreated( Bundle savedInstance )
    {
        super.onActivityCreated(savedInstance);
        list = (ListView)getActivity().findViewById(R.id.list);
        Context context = getActivity();

        mSQL = new BibSQL( context );
        mKeys = mSQL.getKeysAsList();

        mKeys.add( "A" );
        mKeys.add( "B" );
        mKeys.add( "C" );
        mKeys.add( "D" );


        list.setAdapter( adapter );
    }

}
