package jp.gmo;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.IBinder;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

public class SmccPaymentActivity extends AppCompatActivity {
    private ListView listView;
    private PaymentListAdapter adapter = null;
    private List<PaymentListAdapter.Payments> allBeans = new ArrayList<>();
    private EditText amountInputEditText = null;
    // 金額
    private int amount = 0;
    // 取引モード
    private String transactionMode;
    // 取引種別
    private String transactionType;
    // 税
    private int tax = 0;
    // 伝票番号
    private String slipNumber = "00001";

    // APOS2020から渡されたパッケージ名称のプレフィックス
    private final String requestPackgePrefix = ""; //"jp.gmo.";
    // APOS2020に渡すパッケージ名称のプレフィックス
    private final String responsePackgePrefix = ""; //"com.example.client.";

    private Map<String, String> mapPaymentType = new HashMap<String, String>() {{
        put("クレジット", "01");
        put("電子マネー", "02");
        put("QR", "03");
    }};

    private Map<String, String> mapCreditCardBrand = new HashMap<String, String>() {{
        put("VISA", "01");
        put("MasterCard", "02");
        put("JCB", "03");
        put("AmericanExpress", "04");
        put("Diners", "05");
        put("銀聯", "06");
    }};
    private Map<String, String> mapEMoneyType = new HashMap<String, String>() {{
        put("iD", "01");
        put("交通系IC", "02");
        put("楽天Edy", "03");
        put("WAON", "04");
        put("nanaco", "05");
        put("QUICPay plus", "06");
        put("PiTaPa", "07");
    }};
    private Map<String, String> mapQRPayType = new HashMap<String, String>() {{
        put("楽天ペイ", "11");
        put("LINEPay", "12");
        put("PayPay", "13");
        put("d払い", "14");
        put("auPay", "15");
        put("メルペイ", "16");
        put("Origami", "17");
        put("銀行Pay", "19");
        put("WeChatPay", "21");
        put("AliPay", "22");
        put("銀聯", "23");
        put("BankPay", "35");
    }};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.smcc_activity_payment);

        String packageName = "";
        String activityName = "";
        try {
            packageName = getCallingActivity().getPackageName();
            activityName = getCallingActivity().getClassName();
            Log.d("Stera呼出元apackageName",packageName );
            Log.d("Stera呼出元activity",activityName);
        } catch (Exception e) {
            Log.d("呼出元activity", packageName);
        }

        initToolbar();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            for (String key : bundle.keySet()) {
                String value = (bundle.get(key) != null ? bundle.get(key).toString() : "NULL");
                Log.d("Stera決済APP戻り項目 ", key + " : " + (bundle.get(key) != null ? bundle.get(key) : "NULL"));
                switch (key) {
                    case "TransactionMode":
                        // 取引モード
                        this.transactionMode = value;
                        break;
                    case "TransactionType":
                        // 取引種別
                        this.transactionType = value;
                        break;
                    case "Amount":
                        // 金額
                        try {
                            this.amount = Integer.parseInt(value);
                        } catch (NumberFormatException e) {
                            Log.d("SmccPaymentCenter", String.format("Amount[%s]がnot a number", value));
                        }
                        break;
                    case "SlipNumber":
                        // 伝票番号
                        this.slipNumber = value;
                        break;
                    case "Tax":
                        // 税
                        try {
                            this.tax = Integer.parseInt(value);
                        } catch (NumberFormatException e) {
                            Log.d("SmccPaymentCenter", String.format("Tax[%s]がnot a number", value));
                        }
                        break;
                    default:
                        break;
                }
            }
        }


        // 支払金額
        int totalPayAmount = this.amount + this.tax;

        //決済金額入力ボックス初期化
        initAmountInput(totalPayAmount);

        //RadioGroup初期化
        initRadioGroup();

        //ListView初期化
        initListView();

//        //取消
//        Button button_cancel = findViewById(R.id.smcc_payment_cancel);
//        button_cancel.setOnClickListener(v -> showErrorDialog());
//
//        //エラー
//        Button button_error = findViewById(R.id.smcc_payment_error);
//        button_error.setOnClickListener(v -> showErrorDialog());
//
//        //決済
//        Button button_confirm = findViewById(R.id.smcc_payment_exe);
//        button_confirm.setOnClickListener(
//                new View.OnClickListener(){
//                    @Override
//                    public void onClick(View v) {
//
//                    }
//                }
//        );

//        SteraCustomerDisplayManager displayer = new SteraCustomerDisplayManager();
//        displayer.pay();
    }


    private void initToolbar() {
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.smcc_payment_tb);

//        //メインとして起動する場合戻る印は不要
//        Intent intent = getIntent();
//        intent.getAction();
//        if(intent.getAction()=="android.intent.action.MAIN"){
//            Log.d("debug", "initIntent: メイン画面として起動する");
//            toolbar.setNavigationIcon(null);
//        }else{
        //ヘッダツールバー設定：前画面に戻る
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                    finish();
                Intent intent = new Intent(SmccPaymentActivity.this, FullscreenActivity.class);
                intent.putExtra("TransactionMode", transactionMode);
                intent.putExtra("TransactionType", transactionType);
                startActivity(intent);
            }
        });
//        }
    }


    private void initAmountInput(int amountInput) {
//        LayoutInflater inflater = getLayoutInflater();
//        View subLayout = inflater.inflate(R.layout.smcc_input_box,null);
//        amountInputEditText  = subLayout.findViewById(R.id.smcc_payment_amount_input);
        amountInputEditText = findViewById(R.id.smcc_payment_amount_input).findViewById(R.id.txt_amount_input);
        amountInputEditText.setMinLines(1);


        //キーボード表示しない、入力可、でもカーソルがない

        amountInputEditText.setRawInputType(InputType.TYPE_NULL);
        amountInputEditText.setFocusable(false);
        amountInputEditText.setTextIsSelectable(true);

        amountInputEditText.setText(String.valueOf(amountInput));

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);//默认软键盘不显示方法
//        editText.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                EditText editText = (EditText)v;
//                int inType = editText.getInputType();
//                editText.setInputType(InputType.TYPE_NULL);
//                editText.onTouchEvent(event);
//                editText.setInputType(inType);
//
//                return true;
//            }
//        });
        amountInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String ss = s.toString();
                if (ss.isEmpty()) {
                    ss = "0";
                }

                Integer amount_tmp = Integer.parseInt(ss);
                TextView textView = findViewById(R.id.smcc_payment_amount_display);
                NumberFormat nfCur = NumberFormat.getCurrencyInstance(Locale.JAPAN);  //通貨形式
                textView.setText(nfCur.format(amount_tmp));
                amount = amount_tmp;
            }
        });


    }


    //支払方法一覧データを準備する
    private void setPaymentData() {
        allBeans.clear();
        for (PaymentListAdapter.Payments payment : PaymentListAdapter.Payments.values()) {
//            PaymentBean bean = new PaymentBean();
//            bean.setPaymentId(bean.getPaymentId());
//            bean.setPayType(payment.getPaymentType());
//            bean.setPaymentName(payment.getPaymentName());
//            bean.setIconName(payment.getIconName());

            allBeans.add(payment);
        }
    }

    //支払方法選択を初期化
    private void initRadioGroup() {
        RadioGroup radioGroup = findViewById(R.id.smcc_payment_filter_radio_group);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                List<PaymentListAdapter.Payments> showBeans = new ArrayList<>();
                setPaymentData();
                switch (checkedId) {
                    case R.id.smcc_radio_credit:
                        //showBeans = allBeans.stream().filter( bean -> bean.getPayType()=="credit").toArray(PaymentBean[]::new);
                        showBeans = allBeans.stream().filter(bean -> bean.getPaymentType() == "クレジット").collect(Collectors.toList());
                        break;
                    case R.id.smcc_radio_qr:
                        showBeans = allBeans.stream().filter(bean -> bean.getPaymentType() == "QR").collect(Collectors.toList());
                        break;
                    case R.id.smcc_radio_emoney:
                        showBeans = allBeans.stream().filter(bean -> bean.getPaymentType() == "電子マネー").collect(Collectors.toList());
                        break;
                }
                adapter.clear();
                adapter.setData(showBeans);
                adapter.notifyDataSetChanged();//変更とは、データセットが変更されたことを意味します。個々のアイテムが更新されたか、アイテムが追加または削除されました。//データの新規作成 or 更新
                //adapter.notifyDataSetInvalidated();//Invalidatedは、データソースが使用できなくなったことを意味します。  //アクセスできるデータがないとき
            }
        });
    }

    //一覧を初期化
    private void initListView() {
        //一覧データを用意
        setPaymentData();

        final ListView listView = findViewById(R.id.smcc_payment_list);
        adapter = new PaymentListAdapter(SmccPaymentActivity.this, R.layout.smcc_listview_in_out_history_item_pay, allBeans);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //選択中項目設定
                PaymentListAdapter.Payments item = (PaymentListAdapter.Payments) parent.getItemAtPosition(position);
//                if(item.isSelected()){
//                    item.setSelected(false);
//                }else {
//                    item.setSelected(true);
//                }

                String selectedPayment = "";
                if (adapter.getSelectedPayment() == item) {
                    adapter.setSelectedPayment(null);
                } else {
                    adapter.setSelectedPayment(item);
                    selectedPayment = item.getPaymentName();
                }
                //adapter.setSelectedPosition(position);
                adapter.notifyDataSetInvalidated();

                //底バナー決済方法更新
                TextView textView = findViewById(R.id.smcc_payment_selected);
                textView.setText(selectedPayment);


//                ((PaymentListAdapter) adapter).setSelectPosition(position);
//                //効かない
//                listView.setItemChecked(position, true);

                //List項目
//                TextView itemTextView = view.findViewById(R.id.list_pay_name);
//                txtView.setTextColor(R.color.smcc_common_white);
//                view.setBackgroundColor(R.color.smcc_common_white);
//                listView.invalidate();


//                for(int i=0;i< parent.getCount(); i++){
//                    View v = (View)parent.getChildAt(parent.getCount()-1-i);
//                    if(position==i){
//                        view.setBackgroundColor(R.color.smcc_common_blue);
//                    }
//                    else {
//                        view.setBackgroundColor(R.color.smcc_common_white);
//                    }
//                }

            }
        });

        RadioButton radioButton = findViewById(R.id.smcc_radio_credit);
        radioButton.performClick();

    }

//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        EditText editText = findViewById(R.id.smcc_payment_amount_input);
//
//        if(!isInView(editText, event)){
//            View v = getCurrentFocus();
//            hideKeyboard(v.getWindowToken());
//        }
//
//        return super.onTouchEvent(event);
//    }
//
//
//
//    /**
//     * 判断触摸的点是否在View范围内
//     */
//    public boolean isInView(View v, MotionEvent event) {
//        int[] l = {v.getLeft(), v.getTop()};
//        v.getLocationInWindow(l);
//        int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left + v.getWidth();
//        float eventX = event.getX();
//        float eventY = event.getY();
//        Rect rect = new Rect(left, top, right, bottom);
//        return rect.contains((int) eventX, (int) eventY);
//    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideKeyboard(v, ev)) {
                hideKeyboard(v.getWindowToken());
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * EditTextのポジションによる、キーボード非表示させるかどうかを判断
     *
     * @param v
     * @param event
     * @return
     */
    private boolean isShouldHideKeyboard(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0], top = l[1],
                    bottom = top + v.getHeight(),
                    right = left + v.getWidth();
            Rect rect = new Rect(left, top, right, bottom);
            if (rect.contains((int) event.getX(), (int) event.getY())) {
                // EditTextの押下イベントを無視する
                ((EditText) v).setInputType(InputType.TYPE_CLASS_NUMBER);
                v.setFocusable(true);
                return false;
            } else {
                amountInputEditText.setInputType(InputType.TYPE_NULL);
                return true;
            }
        }
        // EditTextがフォーカスされていない状態であれば、FALSEを返す
        return false;
    }


    /**
     * InputMethodManagerを取得し，キーボードを非表示
     *
     * @param token
     */
    private void hideKeyboard(IBinder token) {

        if (token != null) {
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }


    public void showErrorDialog(View view) {
        //        //solution1
        //        View view = LayoutInflater.from(this).inflate(R.layout.smcc_payment_error_dialog,null,false);
        //        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //        builder.setTitle("エラー種別を選択してください");
        //        builder.setView(view);
        //        builder.setCancelable(false);//设置为不可取消
        //        builder.setPositiveButton("確定", new DialogInterface.OnClickListener() {
        //            @Override
        //            public void onClick(DialogInterface dialog, int which) {
        //
        //            }
        //        });
        //        builder.setPositiveButton("取消", new DialogInterface.OnClickListener() {
        //            @Override
        //            public void onClick(DialogInterface dialog, int which) {
        //
        //            }
        //        });
        //        builder.show();


        //一括日計
        if ("5".equals(transactionType)) {
            Intent intent = new Intent();

            intent.putExtra(this.responsePackgePrefix + "TransactionMode", this.transactionMode);
            intent.putExtra(this.responsePackgePrefix + "TransactionType", this.transactionType);

            intent.putExtra(this.responsePackgePrefix + "PaymentType", "{\"01\", \"03\"}");//固定02 クレジット "{\"01\", \"03\", \"02-05\"}"
            intent.putExtra(this.responsePackgePrefix + "ErrorCode", "{\"AAAAAAAA\", \"BBBBBBBB\"}");

            // 失敗
            setResult(1, intent);
            finish();
        } else {
            SmccPayErrorDialog dialog = new SmccPayErrorDialog(this);
            dialog.show(getSupportFragmentManager(), null);
        }

    }

    // キャンセル
    public void payCancel(View view) {
        Intent intent = new Intent();

        // キャンセル
        setResult(2, intent);
        intent.putExtra(this.responsePackgePrefix + "ErrorCode", "");// エラーコードがないことを設定
        intent.putExtra(this.responsePackgePrefix + "TransactionMode", this.transactionMode);
        intent.putExtra(this.responsePackgePrefix + "TransactionType", this.transactionType);
        finish();
    }

    // 決済ボタン
    public void payConfirm(View view) {
        PaymentListAdapter.Payments selectItem = this.adapter.getSelectedPayment();

        if ("1".equals(transactionType)) {
            if (selectItem == null) {
                AlertDialog.Builder builer = new AlertDialog.Builder(this);
                builer.setMessage("決済方法を選択してください。");
                builer.setPositiveButton("確定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builer.show();

                return;
            }
        }

        if ("2".equals(transactionType) || "3".equals(transactionType)) {
            if (selectItem == null) {
                AlertDialog.Builder builer = new AlertDialog.Builder(this);
                builer.setMessage("取消・返品決済方法を選択してください。");
                builer.setPositiveButton("確定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builer.show();

                return;
            }
        }

        Intent intent = new Intent();
        intent.putExtra(this.responsePackgePrefix + "TransactionMode", this.transactionMode);
        intent.putExtra(this.responsePackgePrefix + "TransactionType", this.transactionType);

        //決済
        if ("1".equals(transactionType)) {
            String paymentName = selectItem.getPaymentName();
            String paymentType = selectItem.getPaymentType();

            // 伝票番号を設定
            Random rand = new Random();
            int num = rand.nextInt(100000);
            this.slipNumber = String.valueOf(num);
            intent.putExtra(this.responsePackgePrefix + "SlipNumber", this.slipNumber);

            //amount
            intent.putExtra(this.responsePackgePrefix + "Amount", String.valueOf(amount));

            // クレジットカード
            if (paymentType == "クレジット") {
                intent.putExtra(this.responsePackgePrefix + "PaymentType", mapPaymentType.get(paymentType));
                intent.putExtra(this.responsePackgePrefix + "CreditCardBrand", mapCreditCardBrand.get(paymentName));
                intent.putExtra(this.responsePackgePrefix + "CreditCardMaskedPAN", "123456******1234");
                intent.putExtra(this.responsePackgePrefix + "PayCurrency", "JPY");
            }
            // 電子マネー
            if (paymentType == "電子マネー") {
                intent.putExtra(this.responsePackgePrefix + "PaymentType", mapPaymentType.get(paymentType));
                intent.putExtra(this.responsePackgePrefix + "EMoneyType", mapEMoneyType.get(paymentName));
                intent.putExtra(this.responsePackgePrefix + "EMoneyNumber", "1234567890123456");
            }
            // QR決済
            if (paymentType == "QR") {
                intent.putExtra(this.responsePackgePrefix + "PaymentType", mapPaymentType.get(paymentType));
                intent.putExtra(this.responsePackgePrefix + "QRPayType", mapQRPayType.get(paymentName));
            }
        }

        //取消、返品
        if ("2".equals(transactionType) || "3".equals(transactionType)) {
            intent.putExtra(this.responsePackgePrefix + "SlipNumber", this.slipNumber);

            //amount
            intent.putExtra(this.responsePackgePrefix + "Amount", String.valueOf(amount));

            String paymentName = "VISA";
            String paymentType = "クレジット";
            if (selectItem != null) {
                paymentName = selectItem.getPaymentName();
                paymentType = selectItem.getPaymentType();
            }

            // クレジットカード
            if (paymentType == "クレジット") {
                intent.putExtra(this.responsePackgePrefix + "PaymentType", mapPaymentType.get(paymentType));
                intent.putExtra(this.responsePackgePrefix + "CreditCardBrand", mapCreditCardBrand.get(paymentName));
                intent.putExtra(this.responsePackgePrefix + "CreditCardMaskedPAN", "123456******1234");
                intent.putExtra(this.responsePackgePrefix + "PayCurrency", "JPY");
            }
            // 電子マネー
            if (paymentType == "電子マネー") {
                intent.putExtra(this.responsePackgePrefix + "PaymentType", mapPaymentType.get(paymentType));
                intent.putExtra(this.responsePackgePrefix + "EMoneyType", mapEMoneyType.get(paymentName));
                intent.putExtra(this.responsePackgePrefix + "EMoneyNumber", "1234567890123456");
            }
            // QR決済
            if (paymentType == "QR") {
                intent.putExtra(this.responsePackgePrefix + "PaymentType", mapPaymentType.get(paymentType));
                intent.putExtra(this.responsePackgePrefix + "QRPayType", mapQRPayType.get(paymentName));
            }
        }

        //再印字
        if ("4".equals(transactionType)) {
            ;
        }

        //一括日計
        if ("5".equals(transactionType)) {
            ;
        }

        setResult(0, intent); // 成功
        intent.putExtra(this.responsePackgePrefix + "ErrorCode", "");// エラーコードがないことを設定
        finish();
    }

    // エラー
    public void payError(View view, String errorCode, String errorName) {
        // 画面上選択されたエラーコード
        Intent intent = new Intent();
        intent.putExtra(this.responsePackgePrefix + "ErrorCode", errorCode);
        intent.putExtra(this.responsePackgePrefix + "TransactionType", this.transactionType);
        intent.putExtra(this.responsePackgePrefix + "TransactionMode", this.transactionMode);

        //取消、返品
        if (transactionType == "2" || transactionType == "3") {
            //intent.putExtra(this.responsePackgePrefix + "Amount", this.amount);
            //intent.putExtra(this.responsePackgePrefix + "SlipNumber", this.slipNumber);
        }

        //再印字
        if (transactionType == "4") {
            intent.putExtra(this.responsePackgePrefix + "TransactionType", "01");//固定01 クレジット
        }

        //一括日計
        if (transactionType == "5") {
            intent.putExtra(this.responsePackgePrefix + "PaymentType", "{\"01\", \"03\"}");//固定02 クレジット "{\"01\", \"03\", \"02-05\"}"
            intent.putExtra(this.responsePackgePrefix + "ErrorCode", "{\"AAAAAAAA\", \"BBBBBBBB\"}");
        }

        // 失敗
        setResult(1, intent);
        finish();
    }
}
