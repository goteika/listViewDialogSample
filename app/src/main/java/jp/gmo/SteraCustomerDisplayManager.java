package jp.gmo;

import android.util.Log;

import java.io.OutputStream;
import java.net.Socket;


/**
 * カスタマーディスプレイ制御クラス
 */
public class SteraCustomerDisplayManager   {
    /** スタブ利用か　開発際のみtrue */
    private boolean useStub = true;


    /** 多言語対応：現在利用中の言語 */
    private String _label_head_select_item = "商品選択";
    private String _label_head_account = "お会計";
    private String _label_discount ="値割引";
    private String _label_sub_total = "小計";
    private String _label_total = "合計";
    private String _label_recieved = "お預かり";
    private String _label_change = "お釣り";
    private String _label_unit = "点";
    private String _label_currency_mark = "￥";

    /**
     * コンストラクタ
     *
     * @param listener プリンタ制御リスナー
     */

    public SteraCustomerDisplayManager() {
        //多言語決定

/*            //日本語以外
            _label_head_select_item = "SELECT ITEM";
            _label_head_account = "BILL";
            _label_discount ="DISCOUNT";
            _label_sub_total = "SUB TOTAL";
            _label_total = "TOTAL";
            _label_keep = "RECIEVED";
            _label_change = "CHANGE";
            _label_unit = "UNIT(S)";*/

    }

    /**
     * 注文商品表示
     * 即会計で使用する
     *
     * @param trnOrderDetail オーダー詳細情報
     */

    public void showOrderItem() {
        displayImmi( "商品名",  new Long("9999"), 19, new Long("1999"), new Long("99999"));
    }

    /**
     * 初期表示
     */
    public void init() {

        if(useStub){
            displayStub("init");
        }
    }

    /**
     * 終了
     */
    public void term() {

        if(useStub){
            //stubはなにもしない
            //displayStub("term");
        }
    }

    /**
     * 終了
     */
    public void pay() {

        if(useStub){
            displayStub("pay");
        }
    }

    public void showInitialDisplay() {
        SteraDisplayMessage message = new SteraDisplayMessage();
        message.addHeader(1, "");
        message.addHeader(2, "");
        message.addHeader(3, "");
        message.addBody(1,"");
        message.addBody(2,"");
        message.addBody(3,"");
        message.addBody(4,"");
        message.addBody(5,"");
        String sendMsg = message.getXml();
        if(useStub){
            displayStub(sendMsg);
        }
    }

    /**
     * 合計金額表示
     *
     * @param trnOrderBase オーダー基本情報
     */

    public void showTotalPrice() {
        displayTotal( 99, new Long("9999"), new Long("1999"), new Long("29999"), new Long("39999"));
    }

    /**
     * 会計終了後表示(同期処理)
     *
     * @param trnOrderBase オーダー基本情報
     */

    public void showFinishedSynchronously() {
        displayTotalFinish( 99, new Long("9999"), new Long("9999"), new Long("9999"), new Long("9999"), new Long("9999"));
    }


    /**
     *
     * @param goodsName 商品名
     * @param price　価格
     * @param amound　数量
     * @param reduce　値割引
     * @param total　小計
     */
    private  void displayImmi(String goodsName, Long price, Integer amount, Long reduce, Long total){
        String strPrice = "　" + _label_currency_mark + String.format("%1$,3d", price);
        int goodsNameLen = 20 - strPrice.length();//メッセージエリアは最大全角20文字なので
        if(goodsName.length()>goodsNameLen){
            goodsName = goodsName.substring(0,goodsNameLen);
        }
        String msg1 = goodsName + strPrice;
        String msg2 = amount + _label_unit;
        String msg3 = _label_discount + " " + _label_currency_mark + String.format("%1$,3d", reduce);
        String msg4 = _label_sub_total + " " + _label_currency_mark + String.format("%1$,3d", total);

        SteraDisplayMessage message = new SteraDisplayMessage();
        message.addHeader(1, _label_head_select_item);
        message.addHeader(2, "");
        message.addHeader(3, "");
        message.addBody(1,msg1);
        message.addBody(2,msg2);
        message.addBody(3,msg3);
        message.addBody(4,msg4);
        message.addBody(5,"");
        String sendMsg = message.getXml();

        if(useStub){
            displayStub(sendMsg);
        }
    }

    /**
     *
     * @param amount 数量
     * @param total　合計
     * @param reduce　値割引
     * @param keep　お預かり
     * @param change　お釣り　
     * @param payTotal　実際会計金額
     */
    private  void displayTotalFinish(int amount, Long total, Long reduce, Long keep, Long change, Long payTotal){
        String head1 = _label_head_account;
        String head3 =  _label_currency_mark + String.format("%1$,3d", payTotal);
        String msg1 = amount + _label_unit;
        String msg2 = _label_total + "　" + _label_currency_mark + String.format("%1$,3d", total);
        String msg3 = _label_discount + " " + _label_currency_mark + String.format("%1$,3d", reduce);
        String msg4 = _label_recieved + " " + _label_currency_mark + String.format("%1$,3d", keep);
        String msg5 = _label_change + " " + _label_currency_mark + String.format("%1$,3d", change);

        SteraDisplayMessage message = new SteraDisplayMessage();
        message.addHeader(1, head1);
        message.addHeader(2, "");
        message.addHeader(3, head3);
        message.addBody(1,msg1);
        message.addBody(2,msg2);
        message.addBody(3,msg3);
        message.addBody(4,msg4);
        message.addBody(5,msg5);
        String sendMsg = message.getXml();
        if(useStub){
            displayStub(sendMsg);
        }
    }

    private void displayTotal(Integer amount, Long total, Long reduce, Long keep,  Long payTotal) {
        //会計中はお釣りは固定０
        displayTotalFinish( amount, total, reduce, keep, new Long("0"), payTotal);
    }

    /**
     * stubに表示する
     * @param xml
     */
    private void displayStub(String xml){
        new Thread(new Runnable(){
            @Override
            public void run() {
                sendSocket(xml);
            }
        }).start();
    }
    private void sendSocket(String xml){
        try {
            Socket socket = new  Socket("10.0.2.2", 8080);
            OutputStream output = socket.getOutputStream();
            output.write("init".getBytes());
            output.close();
            socket.close();

            socket = new  Socket("10.0.2.2", 8080);
            output = socket.getOutputStream();
            output.write(xml.getBytes());
            output.close();
            socket.close();
        } catch (Exception e)  {
            Log.d("stub表示エラー、無視可", e.toString());
            e.printStackTrace();
        }
    }
    private class SteraDisplayMessage {
        String message = "";
        public  SteraDisplayMessage(){

        }

        public void  addHeader(int headNum, String content){
            String ret="<headerArea>";
            ret +="<headerAreaNumber>" + headNum + "</headerAreaNumber>";
            ret +="<customerString>" + content + "</customerString>";
            ret +="</headerArea>";
            message += ret;
        }
        public void addBody(int msgId, String content){
            String ret="<messageArea>";
            ret += "<messageAreaNumber>" + msgId + "</messageAreaNumber>";
            ret += "<customerString>" + content + "</customerString>";
            ret += "</messageArea>";
            message += ret;
        }

        public String getXml(){
            String xml = "<?xml version=\"1.0\" encoding=\"UTF-16\" standalone=\"yes\"?>";
            xml += "<customerDisplayApi id=\"xxxxxxxx\">";

            xml += "<screenPattern>11</screenPattern>";
/*            xml += creatHeader(1, "");
            xml +=createMessage(1, "");
            xml +=createMessage(1, "");
            xml +=createMessage(1, "");
            xml +=createMessage(1, "");
            xml +=createMessage(1, "");*/
            xml += message;
            xml += "</customerDisplayApi>";

            return xml;
        }

    }
}
