package com.voyager.btchat.Utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.voyager.btchat.R;
import com.voyager.btchat.entities.ChatMsgEntity;

import java.util.List;

/**
 * Created by wuhaojie on 2015/11/1.
 */
public class ChatMsgViewAdapter extends BaseAdapter {
    private List<ChatMsgEntity> data;
    private Context context;
    private LayoutInflater mInflacter;

    static interface IMsgViewType {
        int IMVT_COME_MSG = 0;
        int IMVT_TO_MSG = 1;
    }


    public ChatMsgViewAdapter(Context context, List<ChatMsgEntity> mDataArrays) {
        this.data = mDataArrays;
        this.context = context;
        mInflacter = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        ChatMsgEntity entity = data.get(position);
        return entity.isMsgType() ? IMsgViewType.IMVT_COME_MSG : IMsgViewType.IMVT_TO_MSG;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ChatMsgEntity entity = data.get(position);
        boolean isComeMsg = entity.isMsgType();

        ViewHolder viewHolder = null;
        if (convertView == null) {
            if (isComeMsg) {
                convertView = mInflacter.inflate(R.layout.chat_activity_msg_left, null);
            } else {
                convertView = mInflacter.inflate(R.layout.chat_activity_msg_right, null);
            }
            viewHolder = new ViewHolder();
            viewHolder.tv_send_time = (TextView) convertView.findViewById(R.id.tv_sendtime);
            viewHolder.tv_user_name = (TextView) convertView.findViewById(R.id.tv_username);
            viewHolder.tv_content = (TextView) convertView.findViewById(R.id.tv_chatcontent);
            viewHolder.isComeMsg = isComeMsg;
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.tv_send_time.setText(entity.getDate());
        viewHolder.tv_user_name.setText(entity.getName());
        viewHolder.tv_content.setText(entity.getText());

        return convertView;
    }

    static private class ViewHolder {
        public TextView tv_send_time;
        public TextView tv_user_name;
        public TextView tv_content;
        public boolean isComeMsg = true;
    }

}
