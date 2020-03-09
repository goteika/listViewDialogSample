package jp.gmo;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class PayErrorListAdapter extends BaseAdapter {

    List<PayError> items = new ArrayList<>();
    private Activity  mContext;

    PayError selectedItem;

    public PayError getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(PayError selectedItem) {
        this.selectedItem = selectedItem;
    }

    public void clickItem(PayError clickedItem) {
        if(clickedItem == this.selectedItem){
            this.setSelectedItem(null);
        }
        else {
            this.selectedItem = clickedItem;
        }
    }

    public PayErrorListAdapter(Activity mContext) {
        super();
        this.mContext = mContext;

        PayError err1 = new PayError("000000F1", "残高不足", "残高不足です");
        PayError err2 = new PayError("000000F2", "PINコードエラー", "PINコードエラー");
        PayError err3 = new PayError("000000F3", "限度額超過", "限度額超過");
        PayError err4 = new PayError("000000F4", "カード凍結されてる", "カード凍結されてる");
        PayError err5 = new PayError("000000F5", "ネットワークエラー", "ネットワークエラーが発生");
        PayError err6 = new PayError("000000F6", "タイムアウト", "タイムアウトです");

        items.add(err1);
        items.add(err2);
        items.add(err3);
        items.add(err4);
        items.add(err5);
        items.add(err6);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public PayError getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        PayError error = items.get(position);
        View itemView;
        if(convertView == null){
            LayoutInflater inflater = mContext.getLayoutInflater();
            itemView = inflater.inflate(R.layout.smcc_listview_pay_error_item,null);

            TextView errCode = itemView.findViewById(R.id.list_pay_error_code);
            TextView errName = itemView.findViewById(R.id.list_pay_error_name);

            holder = new ViewHolder();
            holder.errCode = errCode;
            holder.errName = errName;

            itemView.setTag(holder);
            convertView = itemView;
        }
        else{
            holder = (ViewHolder)convertView.getTag();
        }

        holder.errCode.setText(error.ErrCode);
        holder.errName.setText(error.ErrName);

        if(error == getSelectedItem()){
            convertView.setBackgroundColor(Color.CYAN);
        }
        else {
            convertView.setBackgroundColor(Color.WHITE);
        }

        return convertView;
    }


    static class  ViewHolder{
        TextView errCode ;
        TextView errName;
    }

    public class PayError{
        String ErrCode;//エラーコード

        public  PayError(){

        }
        public  PayError(String ErrCode, String ErrName, String ErrInfo){
            this.ErrCode = ErrCode;
            this.ErrName = ErrName;
            this.ErrInfo = ErrInfo;
        }

        public String getErrCode() {
            return ErrCode;
        }

        public void setErrCode(String errCode) {
            ErrCode = errCode;
        }

        public String getErrName() {
            return ErrName;
        }

        public void setErrName(String errName) {
            ErrName = errName;
        }

        public String getErrInfo() {
            return ErrInfo;
        }

        public void setErrInfo(String errInfo) {
            ErrInfo = errInfo;
        }

        String ErrName;//エラー名
        String ErrInfo;//エラー詳細

    }
}
