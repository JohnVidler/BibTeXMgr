package uk.co.johnvidler.BibTeXMgr.panels;

import uk.co.johnvidler.bibtex.BibTeXEntry;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

public class ListTaskPanel extends AbstractTaskPanel
{
    private JTable table = null;
    private JScrollPane scrollArea = null;
    private TreeSet<BibTeXEntry> dataSource = null;

    //private String[] columns = {"Key", "Title", "Author"};
    //private String[] columns = {"Type", "Key", "Title", "Author", "Pages", "Volume", "Note"};
    private ArrayList<String> columns = new ArrayList<String>();

    private AbstractTableModel dataModel = new AbstractTableModel()
    {
        public String getColumnName( int col )
        {
            return columns.get(col);
        }

        public int getRowCount()
        {
            if( dataSource == null )
                return 0;
            return dataSource.size();
        }

        public int getColumnCount()
        {
            return columns.size();
        }

        public Object getValueAt( int row, int col )
        {
            if( dataSource == null )
                return null;

            Iterator<BibTeXEntry> iter = dataSource.iterator();
            BibTeXEntry entry = iter.next();
            for ( int i=0; i<row; i++ )
                entry = iter.next();

            Object value = null;
            if( columns.get(col).equalsIgnoreCase("key") )
                value = entry.getKey();
            else if( columns.get(col).equalsIgnoreCase("type") )
                value = entry.getType();
            else
                value = entry.getProperty( columns.get(col) );

            if( value == null)
                return "";

            return value;
        }

        public boolean isCellEditable( int row, int col )
        {
            if( dataSource == null )
                return false;

            return true;
        }

        public void setValueAt( Object value, int row, int col )
        {
            if( dataSource == null )
                return;

            Iterator<BibTeXEntry> iter = dataSource.iterator();
            BibTeXEntry entry = iter.next();
            for ( int i=0; i<row; i++ )
                entry = iter.next();

            if( columns.get(col).equalsIgnoreCase("key") )
                entry.setKey( value.toString() );
            else if( columns.get(col).equalsIgnoreCase("type") )
                entry.setType( value.toString() );
            else
                entry.addProperty( columns.get(col), value.toString() );
        }

    };

    private JButton newEntryBtn = new JButton("New Entry");
    private ActionListener newEntryAction = new ActionListener()
    {
        public void actionPerformed(ActionEvent actionEvent)
        {
            if( dataSource == null )
            {
                rootWin.showDialog("Error!", "No file open to edit!", true);
                return;
            }

            dataSource.add( new BibTeXEntry("Article", "") );
            table.updateUI();
        }
    };

    public ListTaskPanel()
    {
        setLayout( new BorderLayout() );

        table = new JTable();
        table.setAutoResizeMode( JTable.AUTO_RESIZE_LAST_COLUMN );
        
        scrollArea = new JScrollPane( table, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED );
        scrollArea.setVisible(true);
        add(scrollArea, BorderLayout.CENTER);

        JToolBar toolbar = new JToolBar();
        toolbar.add( newEntryBtn );
        newEntryBtn.addActionListener(newEntryAction);
        add(toolbar, BorderLayout.NORTH);
    }

    @Override
    public void paint( Graphics g )
    {
        scrollArea.revalidate();
        super.paint(g);
    }

    @Override
    public void setDataSource( TreeSet<BibTeXEntry> dataSource )
    {
        this.dataSource = dataSource;
        table.setModel(dataModel);
        dataModel.fireTableDataChanged();
        repaint();
    }

    public void setColumns( String[] newCols )
    {
        columns.clear();
        for( String col : newCols )
            columns.add(col);
        dataModel.fireTableDataChanged();
        repaint();
    }
}
