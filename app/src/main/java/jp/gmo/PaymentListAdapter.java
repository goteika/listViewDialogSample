package jp.gmo;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;


public class PaymentListAdapter extends BaseAdapter {
    public int getSelectedPosition() {
        return selectedPosition;
    }

    public void setSelectedPosition(int selectedPosition) {
        this.selectedPosition = selectedPosition;
    }

    private int selectedPosition = -1;//选中的项目

    public Payments getSelectedPayment() {
        return selectedPayment;
    }

    public void setSelectedPayment(Payments selectedPayment) {
        this.selectedPayment = selectedPayment;
    }

    private Payments selectedPayment = null;//選択中の支払方法

    private Activity mContext = null;
    private int mResourceId;
    private List<Payments> mItems;


    public PaymentListAdapter(Activity context, int resId, List<Payments> itemList) {
        mContext = context;
        mResourceId = resId;
        mItems = new ArrayList<>();
        mItems = itemList;
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Object getItem(int position) {
        if (mItems != null) {
            return mItems.get(position);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //return super.getView(position, convertView, parent);

        LayoutInflater inflater = mContext.getLayoutInflater();
        View itemView = inflater.inflate(mResourceId, null);

        ImageView iconView = itemView.findViewById(R.id.list_pay_icon);
        TextView txtView = itemView.findViewById(R.id.list_pay_name);

        Payments bean = mItems.get(position);
        txtView.setText(bean.getPaymentName());
        int iconId = mContext.getResources().getIdentifier(bean.getIconName(), "drawable", mContext.getPackageName());
        iconView.setImageResource(iconId);
        Bitmap bmp = ((BitmapDrawable) mContext.getResources().getDrawable(iconId, null)).getBitmap();
        int height = 100;
        bmp = Bitmap.createScaledBitmap(bmp, (bmp.getWidth() * height / bmp.getHeight()), height, true);
        iconView.setImageBitmap(bmp);

//        if(position==this.selectedPosition){
        if (bean == this.selectedPayment) {
            txtView.setTextColor(Color.WHITE);
            itemView.setBackgroundColor(R.color.smcc_common_blue);
        }

        return itemView;
    }

    public void setSelectPosition(int position) {
        if (!(position < 0 || position > mItems.size())) {
            selectedPosition = position;
        }
    }

    public void clear() {
        this.mItems.clear();
    }

    public void setData(List<Payments> beans) {
        this.mItems.addAll(beans);

    }


    public enum Payments {
//        credit_jcb(1, "credit","JCB","icon_credit_jcb.png"),
//        credit_visa(2, "credit","VISA","icon_credit_visa.jpg"),
//        credit_master(3, "credit","MASTER","icon_credit_master.jpg"),
//        credit_amex(4, "credit","AMEX","icon_credit_amex.png"),
//        emoney_edy(5, "emoney","Edy","icon_e_edy.png"),
//        emoney_id(6, "emoney","ID","icon_e_id.png"),
//        emoney_nanaco(7, "emoney","nanaco","icon_e_nanaco.png"),
//        emoney_pasmo(8, "emoney","PASMO","icon_e_pasmo.png"),
//        emoney_suica(9, "emoney","SUICA","icon_e_suica.png"),
//        emoney_waon(10, "emoney","WAON","icon_e_waon.png"),
//        qrcode_alipay(11, "qrcode","ALIPAY","icon_qr_alipay.jpg"),
//        qrcode_dpay(12, "qrcode","D払い","icon_qr_dpay.png"),
//        qrcode_origami(13, "qrcode","オリガミペイ","icon_qr_origami.png"),
//        qrcode_paypay(14, "qrcode","Paypay","icon_qr_paypay.png"),
//        qrcode_wechatpay(15, "qrcode","WeChat Pay","icon_qr_wechatpay.png");

        credit_visa(1, "クレジット", "VISA", "icon_credit_visa"),
        credit_master(2, "クレジット", "MasterCard", "icon_credit_master"),
        credit_jcb(3, "クレジット", "JCB", "icon_credit_jcb"),
        credit_amex(4, "クレジット", "AmericanExpress", "icon_credit_amex"),
        credit_diners(5, "クレジット", "Diners", "icon_credit_diners"),
        credit_unionpay(6, "クレジット", "銀聯", "icon_unionpay"),
        emoney_id(101, "電子マネー", "iD", "icon_e_id"),
        emoney_ic(102, "電子マネー", "交通系IC", "icon_e_ic"),
        emoney_edy(103, "電子マネー", "楽天Edy", "icon_e_edy"),
        emoney_waon(104, "電子マネー", "WAON", "icon_e_waon"),
        emoney_nanaco(105, "電子マネー", "nanaco", "icon_e_nanaco"),
        emoney_quicpay(106, "電子マネー", "QUICPay plus", "icon_e_quicpay_plus"),
        emoney_pitapa(107, "電子マネー", "PiTaPa", "icon_e_pitapa"),
        qrcode_rakutenpay(201, "QR", "楽天ペイ", "icon_qr_rakutenpay"),
        qrcode_linepay(202, "QR", "LINEPay", "icon_qr_linepay"),
        qrcode_paypay(203, "QR", "PayPay", "icon_qr_paypay"),
        qrcode_dpay(204, "QR", "d払い", "icon_qr_dpay"),
        qrcode_aupay(205, "QR", "auPay", "icon_qr_aupay"),
        qrcode_merpay(206, "QR", "メルペイ", "icon_qr_merpay"),
        qrcode_origami(207, "QR", "Origami", "icon_qr_origami"),
        qrcode_ginkoupay(208, "QR", "銀行Pay", "icon_qr_ginkoupay"),
        qrcode_wechatpay(209, "QR", "WeChatPay", "icon_qr_wechatpay"),
        qrcode_alipay(210, "QR", "AliPay", "icon_qr_alipay"),
        qrcode_unionpay(211, "QR", "銀聯", "icon_unionpay"),
        qrcode_bankpay(212, "QR", "BankPay", "icon_qr_bankpay");

        private final int paymentId;
        private final String paymentType;
        private final String paymentName;
        private final String iconName;

        public boolean isSelected() {
            return selected;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
        }

        private boolean selected;

        private Payments(int paymentId, String paymentType, String paymentName, String iconName) {
            this.paymentId = paymentId;
            this.paymentType = paymentType;
            this.paymentName = paymentName;
            this.iconName = iconName;
        }

        int getPaymentId() {
            return paymentId;
        }

        String getPaymentType() {
            return paymentType;
        }

        String getPaymentName() {
            return paymentName;
        }

        String getIconName() {
            return iconName;
        }


    }
}
