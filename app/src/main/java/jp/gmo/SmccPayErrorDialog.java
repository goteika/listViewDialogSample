package jp.gmo;

import android.app.Activity;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

public class SmccPayErrorDialog extends AppCompatDialogFragment  {
    View view;
    EditText txtInputErrCode;
    Button btnCancel;
    Button btnConfirm;
    Activity mContext;

    private PayErrorListAdapter adapter = null;

    public SmccPayErrorDialog(Activity mContext) {
        super();
        this.mContext = mContext;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE); //タイトルなし
        getDialog().setCanceledOnTouchOutside(true);

        view = inflater.inflate(R.layout.smcc_payment_error_list_dialog, container);


//        //start inputメソッド変更
//        EditText txtInputPayErrCode = null;
//
//        //amountInputEditText = findViewById(R.id.smcc_payment_amount_input).findViewById(R.id.txt_amount_input);
//        txtInputPayErrCode = view.findViewById(R.id.txtInputPayErrCode);
//        txtInputPayErrCode.setMinLines(1);
//
//
//        //キーボード表示しない、入力可、でもカーソルがない
//
//        txtInputPayErrCode.setRawInputType(InputType.TYPE_NULL);
//        txtInputPayErrCode.setFocusable(false);
//        txtInputPayErrCode.setTextIsSelectable(true);
//
//        //txtInputPayErrCode.setText(String.valueOf(amount));
//        this.getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);//默认软键盘不显示方法
//        //end



        initAmountInput();
        initButton();
        initList();

        return view;
    }


    private void initAmountInput(){

        //キーボード表示しない、入力可、でもカーソルがない
        txtInputErrCode  = view.findViewById(R.id.txtInputPayErrCode);

        txtInputErrCode.setRawInputType(InputType.TYPE_CLASS_NUMBER);
        txtInputErrCode.setFocusable(true);
        txtInputErrCode.setTextIsSelectable(true);
        mContext.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);//默认软键盘不显示方法
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
//        })
    }

    private void initButton(){
        btnCancel = view.findViewById(R.id.btn_err_diglog_cancel);
        btnConfirm = view.findViewById(R.id.btn_err_diglog_confirm);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SmccPaymentActivity callingActivity = (SmccPaymentActivity) getActivity();

                jp.gmo.PayErrorListAdapter.PayError selectItem = adapter.getSelectedItem();
                if(selectItem == null){
                    txtInputErrCode  = view.findViewById(R.id.txtInputPayErrCode);
                    callingActivity.payError(v, txtInputErrCode.getText().toString(), "手入力エラーコード");
                }else{
                    callingActivity.payError(v, adapter.getSelectedItem().ErrCode, adapter.getSelectedItem().ErrName);
                }

//                getActivity().finish();
                dismiss();
            }
        });

    }

    private  void initList(){
        ListView list = view.findViewById(R.id.list_pay_error);
        adapter = new PayErrorListAdapter(getActivity());
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapter.clickItem((PayErrorListAdapter.PayError) parent.getItemAtPosition(position));

                PayErrorListAdapter.PayError err = adapter.getSelectedItem();//
                if(err ==null){
                    txtInputErrCode.setText("");
                }
                else
                {
                    txtInputErrCode.setText(err.ErrCode);
                }
                adapter.notifyDataSetInvalidated();
            }
        });

    }
}
