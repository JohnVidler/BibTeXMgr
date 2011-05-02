package uk.co.johnvidler.BibTeXMgr.panels;

import uk.co.johnvidler.bibtex.BibTeXEntry;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.TreeMap;

public class ListTaskPanel extends AbstractTaskPanel
{
    private JTable table = null;
    private JScrollPane scrollArea = null;
    private TreeMap<String, BibTeXEntry> dataSource = null;

    //private String[] columns = {"Key", "Title", "Author"};
    private String[] columns = {"Type", "Key", "Title", "Author"};

    private AbstractTableModel dataModel = new AbstractTableModel()
    {
        public String getColumnName( int col )
        {
            return columns[col];
        }

        public int getRowCount()
        {
            if( dataSource == null )
                return 0;
            return dataSource.keySet().size();
        }

        public int getColumnCount()
        {
            return columns.length;
        }

        public Object getValueAt( int row, int col )
        {
            if( dataSource == null )
                return null;

            String keys[] = dataSource.keySet().toArray(new String[0]);

            if( columns[col].equalsIgnoreCase("key") )
                return keys[row];
            else
            {
                BibTeXEntry entry = dataSource.get( keys[row] );

                if( columns[col].equalsIgnoreCase("type") )
                    return entry.getType();
                return entry.getProperty( columns[col] );
            }
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

            String keys[] = dataSource.keySet().toArray(new String[0]);
            BibTeXEntry entry = dataSource.get( keys[row] );


            if( columns[col].equalsIgnoreCase("key") )
            {
                dataSource.remove(entry);
                entry.setKey( (String)value );
                dataSource.put( (String)value, entry );
                repaint();
                return;
            }
            else if( columns[col].equalsIgnoreCase("type") )
            {
                entry.setType( (String)value );
                return;
            }
            entry.addProperty(columns[col], (String) value);
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

            dataSource.put("",new BibTeXEntry("",""));
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
    public void setDataSource(TreeMap<String, BibTeXEntry> dataSource)
    {
        this.dataSource = dataSource;
        table.setModel(dataModel);
        dataModel.fireTableDataChanged();
        repaint();
    }
}
