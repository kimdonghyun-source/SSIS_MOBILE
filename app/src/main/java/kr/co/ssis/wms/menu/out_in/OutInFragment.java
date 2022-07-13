package kr.co.ssis.wms.menu.out_in;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.honeywell.aidc.BarcodeReadEvent;
import com.honeywell.aidc.BarcodeReader;

import java.security.Key;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import kr.co.siss.wms.R;
import kr.co.ssis.wms.common.SharedData;
import kr.co.ssis.wms.common.Utils;
import kr.co.ssis.wms.custom.CommonFragment;
import kr.co.ssis.wms.honeywell.AidcReader;
import kr.co.ssis.wms.menu.popup.LocationListPopup;
import kr.co.ssis.wms.menu.popup.OneBtnPopup;
import kr.co.ssis.wms.menu.popup.TwoBtnPopup;
import kr.co.ssis.wms.model.MatOutSerialScanModel;
import kr.co.ssis.wms.model.OutInModel;
import kr.co.ssis.wms.model.ResultModel;
import kr.co.ssis.wms.model.WarehouseModel;
import kr.co.ssis.wms.network.ApiClientService;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.internal.Util;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OutInFragment extends CommonFragment {

    Context mContext;
    RecyclerView outin_listview;
    List<OutInModel.Item> outListModel;
    OutInModel outModel;
    OutInAdapter mAdapter;
    String barcodeScan, beg_barcode = null, wh_code;
    TextView tv_bor_code, tv_itm_name, tv_itm_size, tv_c_name, tv_no, tv_cst, tv_itm_code, tv_qty;
    EditText et_from, et_wh;
    TwoBtnPopup mPopup;
    Activity mActivity;
    OneBtnPopup mOneBtnPopup;
    TwoBtnPopup mTwoBtnPopup;
    ImageButton btn_next, bt_wh;
    TextView item_date;
    DatePickerDialog.OnDateSetListener callbackMethod;
    LocationListPopup mLocationListPopup;
    WarehouseModel.Items WareLocation;
    List<WarehouseModel.Items> mWarehouseList;

    List<String> mIncode;
    List<String> newList;

    private SoundPool sound_pool;
    int soundId;
    MediaPlayer mediaPlayer;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();

        mIncode = new ArrayList<>();
        newList = new ArrayList<>();


    }//Close onCreate


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.frag_out_in, container, false);

        outin_listview = v.findViewById(R.id.outin_listview);
        tv_no = v.findViewById(R.id.tv_no);
        tv_cst = v.findViewById(R.id.tv_cst);
        tv_qty = v.findViewById(R.id.tv_qty);
        tv_itm_code = v.findViewById(R.id.tv_itm_code);
        tv_bor_code = v.findViewById(R.id.tv_bor_code);
        tv_itm_name = v.findViewById(R.id.tv_itm_name);
        tv_itm_size = v.findViewById(R.id.tv_itm_size);
        tv_c_name = v.findViewById(R.id.tv_c_name);
        et_from = v.findViewById(R.id.et_from);
        btn_next = v.findViewById(R.id.btn_next);
        item_date = v.findViewById(R.id.item_date);
        bt_wh = v.findViewById(R.id.bt_wh);
        et_wh = v.findViewById(R.id.et_wh);

        outin_listview.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mAdapter = new OutInAdapter(getActivity());
        outin_listview.setAdapter(mAdapter);

        int year1 = Integer.parseInt(yearFormat.format(currentTime));
        int month1 = Integer.parseInt(monthFormat.format(currentTime));
        int day1 = Integer.parseInt(dayFormat.format(currentTime));

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

        btn_next.setOnClickListener(onClickListener);
        item_date.setOnClickListener(onClickListener);
        bt_wh.setOnClickListener(onClickListener);

        et_wh.setText("[800] 입고대기창고");
        wh_code = "800";

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
                    if (outModel == null) {
                        Utils.Toast(mContext, "입고할 품목을 스캔해주세요.");
                        return;
                    } else if (wh_code == null) {
                        Utils.Toast(mContext, "입고처를 골라주세요.");
                        return;
                    } else {
                        btn_next.setEnabled(false);
                        request_mat_out_save();
                    }
                    break;

                case R.id.item_date:
                    int c_year = Integer.parseInt(item_date.getText().toString().substring(0, 4));
                    int c_month = Integer.parseInt(item_date.getText().toString().substring(5, 7));
                    int c_day = Integer.parseInt(item_date.getText().toString().substring(8, 10));

                    DatePickerDialog dialog = new DatePickerDialog(mContext, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT, callbackMethod, c_year, c_month - 1, c_day);
                    dialog.show();
                    break;

                case R.id.bt_wh:
                    requestWhlist();
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
                            return;
                        }
                    }

                    if (mAdapter.itemsList != null) {

                        for (int i = 0; i < mAdapter.itemsList.size(); i++) {
                            if (mAdapter.itemsList.get(i).getLot_no().equals(barcodeScan)) {
                                Utils.Toast(mContext, "동일한 바코드를 스캔하였습니다.");
                                return;
                            }
                        }
                    }

                    if (beg_barcode != null) {
                        if (beg_barcode.equals(barcodeScan)) {
                            Utils.Toast(mContext, "동일한 바코드를 스캔하였습니다.");
                            return;
                        }
                    }
                    //barcodeReader.close();
                    //AidcReader.getInstance().release();   스캐너 죽이기
                    pdaSerialScan();

                    beg_barcode = barcodeScan;
                }
            }
        });

    }//Close onResume

    /**
     * 입고처 리스트
     */
    private void requestWhlist() {
        ApiClientService service = ApiClientService.retrofit.create(ApiClientService.class);

        Call<WarehouseModel> call = service.morWarehouse("sp_pda_scm_wh_list", "");

        call.enqueue(new Callback<WarehouseModel>() {
            @Override
            public void onResponse(Call<WarehouseModel> call, Response<WarehouseModel> response) {
                if (response.isSuccessful()) {
                    WarehouseModel model = response.body();
                    //Utils.Log("model ==> :" + new Gson().toJson(model));
                    if (model != null) {
                        if (model.getFlag() == ResultModel.SUCCESS) {
                            mLocationListPopup = new LocationListPopup(getActivity(), model.getItems(), R.drawable.popup_title_searchloc, new Handler() {
                                @Override
                                public void handleMessage(Message msg) {
                                    WarehouseModel.Items item = (WarehouseModel.Items) msg.obj;
                                    WareLocation = item;
                                    et_wh.setText("[" + WareLocation.getWh_code() + "] " + WareLocation.getWh_name());
                                    //mAdapter.notifyDataSetChanged();
                                    wh_code = WareLocation.getWh_code();
                                    mLocationListPopup.hideDialog();
                                }
                            });
                            mWarehouseList = model.getItems();


                        } else {
                            Utils.Toast(mContext, model.getMSG());
                        }
                    }
                } else {
                    Utils.LogLine(response.message());
                    Utils.Toast(mContext, response.code() + " : " + response.message());
                }
            }

            @Override
            public void onFailure(Call<WarehouseModel> call, Throwable t) {
                Utils.LogLine(t.getMessage());
                Utils.Toast(mContext, getString(R.string.error_network));
            }
        });
    }


    /**
     * 외주품가입고 바코드스캔
     */
    private void pdaSerialScan() {
        ApiClientService service = ApiClientService.retrofit.create(ApiClientService.class);

        Call<OutInModel> call = service.outinSerialScan("sp_pda_scm_list", barcodeScan);

        call.enqueue(new Callback<OutInModel>() {
            @Override
            public void onResponse(Call<OutInModel> call, Response<OutInModel> response) {
                if (response.isSuccessful()) {
                    outModel = response.body();
                    final OutInModel model = response.body();
                    Utils.Log("model ==> :" + new Gson().toJson(model));
                    if (outModel != null) {
                        if (outModel.getFlag() == ResultModel.SUCCESS) {

                            //AidcReader.getInstance().claim(mContext);    스캐너 다시 활성화

                            if (model.getItems().size() > 0) {

                                outListModel = model.getItems();
                                for (int i = 0; i < model.getItems().size(); i++) {

                                    OutInModel.Item item = (OutInModel.Item) model.getItems().get(i);
                                    mAdapter.addData(item);
                                    tv_cst.setText(item.getCst_name());
                                    tv_itm_code.setText(item.getItm_code());
                                    tv_itm_name.setText(item.getItm_name());
                                    tv_itm_size.setText(item.getItm_size());
                                    tv_c_name.setText(item.getC_name());
                                    tv_qty.setText(Integer.toString(item.getTin_qty()));
                                    tv_no.setText(item.getBor_code());
                                    //mIncode.add(item.getCst_code());

                                }

                                mAdapter.notifyDataSetChanged();
                                outin_listview.setAdapter(mAdapter);
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
            public void onFailure(Call<OutInModel> call, Throwable t) {
                Utils.LogLine(t.getMessage());
                Utils.Toast(mContext, getString(R.string.error_network));
            }
        });
    }//Close

    /**
     * 외주품가입고 리프레시
     */
    private void pdaSerialRefresh(final int position) {
        ApiClientService service = ApiClientService.retrofit.create(ApiClientService.class);

        Call<OutInModel> call = service.outinSerialScan("sp_pda_scm_list", outModel.getItems().get(position).getLot_no());

        call.enqueue(new Callback<OutInModel>() {
            @Override
            public void onResponse(Call<OutInModel> call, Response<OutInModel> response) {
                if (response.isSuccessful()) {

                    if (outModel != null) {
                        if (outModel.getFlag() == ResultModel.SUCCESS) {
                            tv_itm_name.setText("");
                            tv_itm_size.setText("");
                            tv_c_name.setText("");
                            tv_qty.setText("");
                            tv_bor_code.setText("");
                            et_from.setText("");


                        } else {
                            //Utils.Toast(mContext, model.getMSG());
                        }
                    }
                } else {
                    Utils.LogLine(response.message());
                    Utils.Toast(mContext, response.code() + " : " + response.message());
                }
            }


            @Override
            public void onFailure(Call<OutInModel> call, Throwable t) {
                Utils.LogLine(t.getMessage());
                Utils.Toast(mContext, getString(R.string.error_network));
            }
        });
    }//Close


    /**
     * 가입고 등록
     */
    private void request_mat_out_save() {

        ApiClientService service = ApiClientService.retrofit.create(ApiClientService.class);
        JsonObject json = new JsonObject();
        String emp_code = (String) SharedData.getSharedData(mContext, "emp_code", "");
        String userID = (String) SharedData.getSharedData(mContext, SharedData.UserValue.USER_ID.name(), "");
        String m_date = item_date.getText().toString().replace("-", "");

        JsonArray list = new JsonArray();

        /*Set<String> set = new HashSet<String>(mIncode);
        newList = new ArrayList<String>(set);
        Log.d("바코드중복값:  ", String.valueOf(newList));*/

        //List<MatOutSerialScanModel.Item> items = scanAdapter.getData();
        List<OutInModel.Item> items = mAdapter.getData();

        for (OutInModel.Item item : items) {
            JsonObject obj = new JsonObject();
            obj.addProperty("corp_code", item.getCorp_code());    //사업장
            obj.addProperty("scm_id", item.getTin_id());          //내수구분
            obj.addProperty("scm_date", item.getTin_date());      //출하일자
            obj.addProperty("scm_no1", item.getTin_no1());        //출하순번1
            obj.addProperty("scm_no2", item.getTin_no2());        //출하순번2
            obj.addProperty("scm_no3", item.getTin_no3());        //출하순번3
            obj.addProperty("bor_code", item.getBor_code());      //발주코드
            obj.addProperty("bor_date", item.getBor_date());      //발주일자
            obj.addProperty("bor_no1", item.getBor_no1());        //발주순번1
            obj.addProperty("bor_no2", item.getBor_no2());        //발주순번2
            obj.addProperty("bor_no3", item.getBor_no3());        //발주순번3
            obj.addProperty("wh_code", item.getWh_code());        //창고코드
            obj.addProperty("make_date", m_date);                 //입고일자
            obj.addProperty("itm_code", item.getItm_code());      //품목코드
            obj.addProperty("lot_no", item.getLot_no());          //로트번호
            obj.addProperty("lot_qty", item.getTin_dtl_qty());    //수량
            obj.addProperty("user_id", userID);                   //로그인ID

            list.add(obj);
        }


        /*json.addProperty("p_corp_code", outModel.getItems().get(0).getCorp_code());    //사업장코드
        json.addProperty("p_scm_id", outModel.getItems().get(0).getTin_id());       //내수구분
        json.addProperty("p_scm_date", outModel.getItems().get(0).getTin_date());   //출하일자
        json.addProperty("p_scm_no1", outModel.getItems().get(0).getTin_no1());     //출하순번1
        json.addProperty("p_scm_no2", outModel.getItems().get(0).getTin_no2());     //출하순번2
        json.addProperty("p_scm_no3", outModel.getItems().get(0).getTin_no3());     //출하순번3
        json.addProperty("p_make_date", m_date);    //입고일자
        json.addProperty("p_wh_code", wh_code);    //창고코드
        json.addProperty("p_user_id", userID);      //로그인ID*/
        json.add("detail", list);

        Utils.Log("new Gson().toJson(json) ==> : " + new Gson().toJson(json));

        RequestBody body = RequestBody.create(MediaType.parse("application/json"), new Gson().toJson(json));

        Call<ResultModel> call = service.postOutInSave(body);

        call.enqueue(new Callback<ResultModel>() {
            @Override
            public void onResponse(Call<ResultModel> call, Response<ResultModel> response) {
                if (response.isSuccessful()) {
                    ResultModel model = response.body();
                    //Utils.Log("model ==> : "+new Gson().toJson(model));
                    if (model != null) {
                        if (model.getFlag() == ResultModel.SUCCESS) {

                            mOneBtnPopup = new OneBtnPopup(getActivity(), "이동처리 되었습니다.", R.drawable.popup_title_alert, new Handler() {
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
                    mTwoBtnPopup = new TwoBtnPopup(getActivity(), "이동 전송을 실패하였습니다.\n 재전송 하시겠습니까?", R.drawable.popup_title_alert, new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            if (msg.what == 1) {
                                request_mat_out_save();
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
                mTwoBtnPopup = new TwoBtnPopup(getActivity(), "이동 전송을 실패하였습니다.\n 재전송 하시겠습니까?", R.drawable.popup_title_alert, new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        if (msg.what == 1) {
                            request_mat_out_save();
                            mTwoBtnPopup.hideDialog();
                            btn_next.setEnabled(true);

                        }
                    }
                });
            }
        });

    }//Close


}//Close Activity
