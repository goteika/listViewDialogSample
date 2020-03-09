package jp.gmo.bean;

import android.graphics.Bitmap;

public class PaymentBean {
    private String payType;//クレジット　　//電子マネー　　　//QRコード
    private String paymentId;  //visa master paypay alipay suica
    private String paymentName;
    private String iconName;
    private boolean enable;
    private long paymentIconId;
    private Bitmap icon = null;

    public String getIconName() {
        return iconName;
    }

    public void setIconName(String iconName) {
        this.iconName = iconName;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    private boolean selected;

    public Bitmap getIcon() {
        return icon;
    }

    public void setIcon(Bitmap icon) {
        this.icon = icon;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;

//        switch (paymentId){
//            case "01":
//
//                Bitmap bmp = ((BitmapDrawable)R.drawable.logo_coiney).getBitmap();
//                icon = bmp;
//                break;
//            default:
//                icon =  ((BitmapDrawable)R.drawable.logo_coiney).getBitmap();
//                break;
//        }
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public long getPaymentIconId() {
        return paymentIconId;
    }

    public void setPaymentIconId(long paymenIconId) {
        this.paymentIconId = paymenIconId;
    }

    public String getPaymentName() {
        return paymentName;
    }

    public void setPaymentName(String paymentName) {
        this.paymentName = paymentName;
    }
}
