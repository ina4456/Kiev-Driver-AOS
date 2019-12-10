package com.kiev.driver.aos.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kiev.driver.aos.R;
import com.kiev.driver.aos.model.entity.Notice;

import java.util.List;


/**
 * Created by seok-beomkwon on 2017. 3. 28..
 */

public class NoticeAdapter extends BaseExpandableListAdapter {

    private List<Notice> groupList;
    private LayoutInflater inflater;
    private ViewHolder viewHolder;
    private Context mContext;

    public NoticeAdapter(Context context, List<Notice> groupList) {
        super();
	    this.mContext = context;
        this.inflater = LayoutInflater.from(context);
        this.groupList = groupList;
    }

	public void setGroupList(List<Notice> groupList) {
		this.groupList = groupList;
		notifyDataSetChanged();
	}

	@Override
    public Notice getGroup(int groupPosition) {
        return groupList.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return groupList.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        View v = convertView;

        if (v == null) {
            viewHolder = new ViewHolder();
            v = inflater.inflate(R.layout.item_notice_group, parent, false);
            viewHolder.tvNoticeTitle = (TextView) v.findViewById(R.id.tv_notice_title);
            viewHolder.tvNoticeDate = (TextView) v.findViewById(R.id.tv_notice_date);
            viewHolder.ivNoticeToggle = v.findViewById(R.id.ibtn_notice_expand);
            viewHolder.viewDivider = v.findViewById(R.id.view_divider);

            v.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) v.getTag();
        }

	    Notice item = getGroup(groupPosition);
        viewHolder.tvNoticeTitle.setText(item.isNotice() ? item.getTitle() : item.getContent());
        viewHolder.tvNoticeDate.setText(item.getDate());

        if (isExpanded) {
	        viewHolder.ivNoticeToggle.setImageDrawable(mContext.getResources().getDrawable(R.drawable.hs_dropdown_arrow_up));
	        viewHolder.viewDivider.setVisibility(View.INVISIBLE);
        } else {
	        viewHolder.ivNoticeToggle.setImageDrawable(mContext.getResources().getDrawable(R.drawable.hs_dropdown_arrow_down));
	        viewHolder.viewDivider.setVisibility(View.VISIBLE);
        }

        View childView = getChildView(groupPosition, 0, false, convertView, parent);
        View viewDivider = childView.findViewById(R.id.view_divider_expanded_bottom);
        if (viewDivider != null)
	        viewDivider.setVisibility(isExpanded ? View.VISIBLE : View.INVISIBLE);


        return v;
    }

    @Override
    public String getChild(int groupPosition, int childPosition) {
        return groupList.get(groupPosition).getContent();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            viewHolder = new ViewHolder();
            v = inflater.inflate(R.layout.item_notice_child, null);
            viewHolder.tvNoticeContent = v.findViewById(R.id.tv_notice_body);
            viewHolder.viewDividerBottom = v.findViewById(R.id.view_divider_expanded_bottom);
            v.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) v.getTag();
        }

        if (viewHolder.tvNoticeContent != null)
            viewHolder.tvNoticeContent.setText(getChild(groupPosition, childPosition));

        return v;
    }



    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPostion, int childPosition) {
        return true;
    }

    class ViewHolder {
        public TextView tvNoticeTitle;
        public TextView tvNoticeDate;
        public TextView tvNoticeContent;
	    public ImageView ivNoticeToggle;
	    public View viewDivider;
	    public View viewDividerBottom;
    }
}