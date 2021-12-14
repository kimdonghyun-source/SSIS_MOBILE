package kr.co.ssis.wms.menu.new_out;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.honeywell.aidc.BarcodeReadEvent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import kr.co.siss.wms.R;
import kr.co.ssis.wms.common.SharedData;
import kr.co.ssis.wms.common.Utils;
import kr.co.ssis.wms.custom.CommonFragment;
import kr.co.ssis.wms.honeywell.AidcReader;
import kr.co.ssis.wms.menu.out_in.OutInAdapter;
import kr.co.ssis.wms.menu.popup.OneBtnPopup;
import kr.co.ssis.wms.menu.popup.TwoBtnPopup;
import kr.co.ssis.wms.model.NewOutModel;
import kr.co.ssis.wms.model.OutInModel;
import kr.co.ssis.wms.model.ResultModel;
import kr.co.ssis.wms.network.ApiClientService;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.internal.Util;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewOutFragment extends CommonFragment {

    Context mContext;
    RecyclerView new_out_listview;
    TextView item_date;
    EditText et_from, et_dpt;
    ImageButton btn_next;
    DatePickerDialog.OnDateSetListener callbackMethod;
    String barcodeScan, beg_barcode;
    List<String> mIncode;
    NewOutModel mNewOutModel;
    List<NewOutModel.Item> mNewOutList;
    private SoundPool sound_pool;
    int soundId;
    MediaPlayer mediaPlayer;
    ListAdapter mAdapter;
    TwoBtnPopup mTwoBtnPopup;
    OneBtnPopup mOneBtnPopup;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        mIncode = new ArrayList<>();

    }//Close onCreate


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.frag_new_out, container, false);

        new_out_listview = v.findViewById(R.id.new_out_listview);
        item_date = v.findViewById(R.id.item_date);
        et_from = v.findViewById(R.id.et_from);
        btn_next = v.findViewById(R.id.btn_next);
        et_dpt = v.findViewById(R.id.et_dpt);

        et_dpt.setText("연구소");

        btn_next.setOnClickListener(onClickListener);

        int year1 = Integer.parseInt(yearFormat.format(currentTime));
        int month1 = Integer.parseInt(monthFormat.format(currentTime));
        int day1 = Integer.parseInt(dayFormat.format(currentTime));

        new_out_listview.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mAdapter = new ListAdapter(getActivity());
        new_out_listview.setAdapter(mAdapter);

        String formattedMonth = "" + month1;
        String formattedDayOfMonth = "" + day1;
        if (month1 < 10) {

            formattedMonth = "0" + month1;
        }
        if (day1 < 10) {
            formattedDayOfMonth = "0" + day1;
        }

        item_date.setText(year1 + "-" + formattedMonth + "-" + formattedDayOfMonth);

        this.InitializeListener();

        sound_pool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        soundId = sound_pool.load(mContext, R.raw.beepum, 1);

        return v;

    }//Close onCreateView

    public void InitializeListener() {
        callbackMethod = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");

                int month = monthOfYear + 1;
                String formattedMonth = "" + month;
                String formattedDayOfMonth = "" + dayOfMonth;

                if (month < 10) {

                    formattedMonth = "0" + month;
                }
                if (dayOfMonth < 10) {

                    formattedDayOfMonth = "0" + dayOfMonth;
                }

                item_date.setText(year + "-" + formattedMonth + "-" + formattedDayOfMonth);

            }
        };
    }

    Date currentTime = Calendar.getInstance().getTime();
    SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
    SimpleDateFormat dayFormat = new SimpleDateFormat("dd", Locale.getDefault());
    SimpleDateFormat monthFormat = new SimpleDateFormat("MM", Locale.getDefault());

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_next:
                    if (mAdapter.getItemCount() <= 0) {
                        Utils.Toast(mContext, "출고처리할 품목을 스캔해주세요.");
                        return;
                    } else {
                        btn_next.setEnabled(false);
                        request_dis_ship_save();
                    }
                    break;
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        AidcReader.getInstance().claim(mContext);
        AidcReader.getInstance().setListenerHandler(new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {

                    BarcodeReadEvent event = (BarcodeReadEvent) msg.obj;
                    String barcode = event.getBarcodeData();
                    barcodeScan = barcode;
                    et_from.setText(barcodeScan);


                    if (mIncode != null) {
                        if (mIncode.contains(barcode)) {
                            Utils.Toast(mContext, "동일한 바코드를 스캔하였습니다.");
                            sound_pool.play(soundId, 1f, 1f, 0, 1, 1f);
                            mediaPlayer = MediaPlayer.create(mContext, R.raw.beepum);
                            mediaPlayer.start();
                            return;
                        }
                    }

                    if (beg_barcode != null) {
                        if (beg_barcode.equals(barcodeScan)) {
                            Utils.Toast(mContext, "동일한 바코드를 스캔하였습니다.");
                            sound_pool.play(soundId, 1f, 1f, 0, 1, 1f);
                            mediaPlayer = MediaPlayer.create(mContext, R.raw.beepum);
                            mediaPlayer.start();
                            return;
                        }
                    }

                    pdaSerialScan();

                    beg_barcode = barcodeScan;
                }
            }
        });

    }//Close onResume



    /**
     * 개발품출고관리 바코드스캔
     */
    private void pdaSerialScan() {
        ApiClientService service = ApiClientService.retrofit.create(ApiClientService.class);

        Call<NewOutModel> call = service.newoutSerialScan("sp_pda_lot_list", barcodeScan);

        call.enqueue(new Callback<NewOutModel>() {
            @Override
            public void onResponse(Call<NewOutModel> call, Response<NewOutModel> response) {
                if (response.isSuccessful()) {
                    mNewOutModel = response.body();
                    final NewOutModel model = response.body();
                    Utils.Log("model ==> :" + new Gson().toJson(model));
                    if (mNewOutModel != null) {
                        if (mNewOutModel.getFlag() == ResultModel.SUCCESS) {

                            //AidcReader.getInstance().claim(mContext);    스캐너 다시 활성화

                            if (model.getItems().size() > 0) {
                                mNewOutList = model.getItems();

                                for (int i = 0; i < model.getItems().size(); i++) {

                                    NewOutModel.Item item = (NewOutModel.Item) model.getItems().get(i);
                                    mAdapter.addData(item);
                                }

                                mAdapter.notifyDataSetChanged();
                                new_out_listview.setAdapter(mAdapter);
                                mIncode.add(barcodeScan);


                            }

                        } else {
                            Utils.Toast(mContext, model.getMSG());
                            sound_pool.play(soundId, 1f, 1f, 0, 1, 1f);
                            mediaPlayer = MediaPlayer.create(mContext, R.raw.beepum);
                            mediaPlayer.start();
                        }
                    }
                } else {
                    Utils.LogLine(response.message());
                    Utils.Toast(mContext, response.code() + " : " + response.message());
                }
            }


            @Override
            public void onFailure(Call<NewOutModel> call, Throwable t) {
                Utils.LogLine(t.getMessage());
                Utils.Toast(mContext, getString(R.string.error_network));
            }
        });
    }//Close



    public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {

        List<NewOutModel.Item> itemsList;
        Activity mActivity;
        Handler mHandler = null;
        TwoBtnPopup mPopup;
        OneBtnPopup mOneBtnPopup;
        TwoBtnPopup mTwoBtnPopup;

        public ListAdapter(Activity context) {
            mActivity = context;
            itemsList = new ArrayList<>();
        }

        public void setRetHandler(Handler h){
            this.mHandler = h;
        }

        public void setData(List<NewOutModel.Item> item){
            itemsList = item;
        }

        public void addData(NewOutModel.Item item) {
            if (itemsList == null) itemsList = new ArrayList<>();
            itemsList.add(item);
        }

        public void clearData(){
            itemsList.clear();
        }

        public void setSumHandler(Handler h){
            this.mHandler = h;
        }

        public List<NewOutModel.Item> getData(){
            return itemsList;
        }

        @Override
        public ListAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cell_new_out_list, viewGroup, false);
            ListAdapter.ViewHolder holder = new ListAdapter.ViewHolder(v);
            return holder;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {

            final NewOutModel.Item item = itemsList.get(position);

            holder.tv_lot_no.setText(item.getLot_no());
            holder.tv_itm_code.setText(item.getItm_code());
            holder.tv_itm_name.setText(item.getItm_name());
            holder.tv_wh_name.setText(item.getWh_name());
            holder.tv_qty.setText(Integer.toString(item.getInv_qty()));

            holder.tv_modify_qty.addTextChangedListener(new TextWatcher(){
                String result="";

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }
                @Override
                public void afterTextChanged(Editable s) {
                    Log.d("JeLib","---------------------------");

                    if (s.toString().length() == 0) {
                        itemsList.get(holder.getAdapterPosition()).setModify_inv_qty(0);
                        result="";
                    }

                    if(s.toString().length() > 0 && !s.toString().equals(result)) {     // StackOverflow를 막기위해,
                        result = s.toString();   // 에딧텍스트의 값을 변환하여, result에 저장.

                        int cnt = Utils.stringToInt(result);

                    //아이템 수량 초과시
                    if(cnt > item.getInv_qty()){
                        Utils.Toast(mContext, "수량이 초과하였습니다.");
                        holder.tv_modify_qty.setText("");
                        //cnt = item.getInv_qty_out();
                        //result = String.valueOf((int)item.getInv_qty_out());
                        return;
                    }

                        holder.tv_modify_qty.setText(result);    // 결과 텍스트 셋팅.
                        holder.tv_modify_qty.setSelection(result.length());     // 커서를 제일 끝으로 보냄.

                        //입력된 수량을 list에 넣어줌
                        itemsList.get(holder.getAdapterPosition()).setModify_inv_qty(cnt);
                    }

                }
            });
        }

        @Override
        public int getItemCount() {
            return (null == itemsList ? 0 : itemsList.size());
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView tv_lot_no;
            TextView tv_itm_code;
            TextView tv_itm_name;
            TextView tv_wh_name;
            TextView tv_qty;
            EditText tv_modify_qty;

            public ViewHolder(View view) {
                super(view);

                tv_lot_no = view.findViewById(R.id.tv_lot_no);
                tv_itm_code = view.findViewById(R.id.tv_itm_code);
                tv_itm_name = view.findViewById(R.id.tv_itm_name);
                tv_wh_name = view.findViewById(R.id.tv_wh_name);
                tv_qty = view.findViewById(R.id.tv_qty);
                tv_modify_qty = view.findViewById(R.id.tv_modify_qty);



            }
        }
    }//Clse Adapter






    /**
     * 타계정등록관리 저장
     */
    private void request_dis_ship_save() {

        ApiClientService service = ApiClientService.retrofit.create(ApiClientService.class);
        JsonObject json = new JsonObject();
        String emp_code = (String) SharedData.getSharedData(mContext, "emp_code", "");
        String userID = (String) SharedData.getSharedData(mContext, SharedData.UserValue.USER_ID.name(), "");
        String m_date = item_date.getText().toString().replace("-", "");

        JsonArray list = new JsonArray();

        //List<MatOutSerialScanModel.Item> items = scanAdapter.getData();
        List<NewOutModel.Item> items = mAdapter.getData();

        for (NewOutModel.Item item : items) {
            if (item.getModify_inv_qty() <= 0){
                Utils.Toast(mContext, "수량을 입력해주세요.");
                btn_next.setEnabled(true);
                return;
            }
            JsonObject obj = new JsonObject();
            obj.addProperty("lot_no", item.getLot_no());             //시리얼번호
            obj.addProperty("itm_code", item.getItm_code());         //품목코드
            obj.addProperty("wh_code", item.getWh_code());           //창고코드
            obj.addProperty("inv_qty", item.getModify_inv_qty());    //수량

            list.add(obj);
        }


        json.addProperty("p_user_id", userID);      //로그인ID
        json.add("detail", list);

        Utils.Log("new Gson().toJson(json) ==> : " + new Gson().toJson(json));

        RequestBody body = RequestBody.create(MediaType.parse("application/json"), new Gson().toJson(json));

        Call<ResultModel> call = service.postNewOutSave(body);

        call.enqueue(new Callback<ResultModel>() {
            @Override
            public void onResponse(Call<ResultModel> call, Response<ResultModel> response) {
                if (response.isSuccessful()) {
                    ResultModel model = response.body();
                    //Utils.Log("model ==> : "+new Gson().toJson(model));
                    if (model != null) {
                        if (model.getFlag() == ResultModel.SUCCESS) {

                            mOneBtnPopup = new OneBtnPopup(getActivity(), "출고처리 되었습니다.", R.drawable.popup_title_alert, new Handler() {
                                @Override
                                public void handleMessage(Message msg) {
                                    if (msg.what == 1) {
                                        getActivity().finish();
                                        mOneBtnPopup.hideDialog();


                                    }
                                }
                            });


                        } else {
                            mOneBtnPopup = new OneBtnPopup(getActivity(), model.getMSG(), R.drawable.popup_title_alert, new Handler() {
                                @Override
                                public void handleMessage(Message msg) {
                                    if (msg.what == 1) {
                                        mOneBtnPopup.hideDialog();
                                        btn_next.setEnabled(true);
                                    }
                                }
                            });
                        }
                    }
                } else {
                    Utils.LogLine(response.message());
                    btn_next.setEnabled(true);
                    mTwoBtnPopup = new TwoBtnPopup(getActivity(), "전송을 실패하였습니다.\n 재전송 하시겠습니까?", R.drawable.popup_title_alert, new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            if (msg.what == 1) {
                                request_dis_ship_save();
                                mTwoBtnPopup.hideDialog();
                                btn_next.setEnabled(true);

                            }
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<ResultModel> call, Throwable t) {
                Utils.LogLine(t.getMessage());
                btn_next.setEnabled(true);
                mTwoBtnPopup = new TwoBtnPopup(getActivity(), "전송을 실패하였습니다.\n 재전송 하시겠습니까?", R.drawable.popup_title_alert, new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        if (msg.what == 1) {
                            request_dis_ship_save();
                            mTwoBtnPopup.hideDialog();
                            btn_next.setEnabled(true);

                        }
                    }
                });
            }
        });

    }//Close




















}//Close Fragment
