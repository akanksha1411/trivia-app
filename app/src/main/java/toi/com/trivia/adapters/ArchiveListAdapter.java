package toi.com.trivia.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import toi.com.trivia.R;
import toi.com.trivia.model.ArchiveItems;

public class ArchiveListAdapter extends BaseExpandableListAdapter {

    private AppCompatActivity _context;
    private List<String> _listDataHeader = new ArrayList<>(); // header titles
    // child data in format of header title, child title
    private HashMap<String, List<ArchiveItems.Result>> _listDataChild = new HashMap<>();
    public static ImageView icon;
    String UID;
    public ArchiveGridAdapter adapter;

    public ArchiveListAdapter(AppCompatActivity context, List<String> listDataHeader,
                              HashMap<String, List<ArchiveItems.Result>> listChildData, String UID) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
        this.UID = UID;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return _listDataChild.get(_listDataHeader.get(groupPosition));
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        List<ArchiveItems.Result> test = (List<ArchiveItems.Result>) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.game_child, null);
        }

        GridView txtListChild = (GridView) convertView
                .findViewById(R.id.archive_quiz_grid);


        if ((groupPosition % 2) == 0) {
            convertView.setBackgroundColor(ContextCompat.getColor(_context, R.color.archive_grey));
        } else {
            convertView.setBackgroundColor(ContextCompat.getColor(_context, R.color.archieve_dark_grey));
        }
        if (test != null) {
            //set adapter for childview
            adapter = new ArchiveGridAdapter(_context, test, UID);
            txtListChild.setAdapter(adapter);
        }
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.game_parent, null);
        }

        if ((groupPosition % 2) == 0) {
            convertView.setBackgroundColor(ContextCompat.getColor(_context, R.color.archive_grey));
        } else {
            convertView.setBackgroundColor(ContextCompat.getColor(_context, R.color.archieve_dark_grey));
        }


        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.archive_text);

        lblListHeader.setText(headerTitle);


        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
