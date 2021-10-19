package kr.co.ssis.wms.menu.ship;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import kr.co.siss.wms.R;
import kr.co.ssis.wms.common.Define;
import kr.co.ssis.wms.common.SharedData;
import kr.co.ssis.wms.common.Utils;
import kr.co.ssis.wms.custom.CommonFragment;
import kr.co.ssis.wms.menu.main.BaseActivity;
import kr.co.ssis.wms.menu.out_in.OutInAdapter;
import kr.co.ssis.wms.menu.out_in.OutInFragment;
import kr.co.ssis.wms.menu.popup.LocationCstListPopup;
import kr.co.ssis.wms.menu.popup.LocationItmSearchPopup;
import kr.co.ssis.wms.menu.popup.LocationListPopup;
import kr.co.ssis.wms.menu.popup.OneBtnPopup;
import kr.co.ssis.wms.menu.popup.ShipItmSearchPopup;
import kr.co.ssis.wms.menu.popup.TwoBtnPopup;
import kr.co.ssis.wms.menu.stock.StockFragment;
import kr.co.ssis.wms.model.ItmListModel;
import kr.co.ssis.wms.model.MatOutDetailModel;
import kr.co.ssis.wms.model.MatOutSerialScanModel;
import kr.co.ssis.wms.model.OutInModel;
import kr.co.ssis.wms.model.ResultModel;
import kr.co.ssis.wms.model.ShipCstModel;
import kr.co.ssis.wms.model.ShipListModel;
import kr.co.ssis.wms.model.ShipOkModel;
import kr.co.ssis.wms.model.WarehouseModel;
import kr.co.ssis.wms.network.ApiClientService;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShipFragment extends CommonFragment {

    ShipItmSearchPopup mlocationItmListPopup;
    Context mContext;
    TextView item_date, all_cnt, no_cnt, tv_slash;
    DatePickerDialog.OnDateSetListener callbackMethod;
    ImageButton bt_cst, btn_next, bt_itm;
    List<ShipCstModel.Item> ShipCstList;
    ShipCstModel ShipCstModel;
    LocationCstListPopup mLocationListPopup;
    ShipCstModel.Item mShipCstModel;
    ItmListModel.Item mItmModel;
    EditText et_from, et_itm;
    String cst_code, s_itm_code;
    List<ShipListModel.ShipItem> mShipListModel;
    ShipListModel mShipModel = null;
    List<ShipOkModel.Item> mShipOkListModel;
    //ShipAdapter mAdapter;
    //RecyclerView ship_listview;
    RecyclerView ship_listview;
    ShipAdapter mAdapter;
    OneBtnPopup mOneBtnPopup;
    TwoBtnPopup mTwoBtnPopup;
    List<ShipOkModel.Item> okList;
    List<ItmListModel.Item> mItmListModel;
    ShipOkModel mShipOkModel = null;
    ShipOkModel.Item order = null;
    int mPosition = -1;

    Handler mHandler;
    ShipListModel.ShipItem mOrder = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();


    }//Close onCreate


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.frag_ship, container, false);

        item_date = v.findViewById(R.id.item_date);
        bt_cst = v.findViewById(R.id.bt_cst);
        et_from = v.findViewById(R.id.et_from);
        btn_next = v.findViewById(R.id.btn_next);
        bt_itm = v.findViewById(R.id.bt_itm);
        et_itm = v.findViewById(R.id.et_itm);
        all_cnt = v.findViewById(R.id.all_cnt);
        no_cnt = v.findViewById(R.id.no_cnt);
        tv_slash = v.findViewById(R.id.tv_slash);


        mHandler = handler;

        ship_listview = v.findViewById(R.id.ship_listview);
        ship_listview.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mAdapter = new ShipAdapter(getActivity());
        ship_listview.setAdapter(mAdapter);


        item_date.setOnClickListener(onClickListener);
        bt_cst.setOnClickListener(onClickListener);
        et_from.setOnClickListener(onClickListener);
        btn_next.setOnClickListener(onClickListener);
        bt_itm.setOnClickListener(onClickListener);
        et_itm.setOnClickListener(onClickListener);

        et_itm.setTextIsSelectable(false);
        et_itm.setShowSoftInputOnFocus(false);
        et_from.setTextIsSelectable(false);
        et_from.setShowSoftInputOnFocus(false);

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

        mAdapter.setRetHandler(new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what >= 0) {
                    ShipScanList(msg.what);
                }
            }
        });

        return v;

    }//Close onCreateView


    @Override
    public void onResume() {
        super.onResume();


    }

    private void ShipScanList(int position) {
        List<ShipListModel.ShipItem> datas = new ArrayList<>();

        Intent intent = new Intent(getActivity(), BaseActivity.class);
        intent.putExtra("menu", Define.MENU_SHIP_OK);

        Bundle extras = new Bundle();
        extras.putSerializable("model", mShipModel);
        extras.putSerializable("position", position);
        intent.putExtra("args", extras);
        startActivityForResult(intent, 100);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            if (resultCode == Activity.RESULT_OK) {
                mShipModel = (ShipListModel) data.getSerializableExtra("model");
                mAdapter.setData(mShipModel.getItems());
                mAdapter.notifyDataSetChanged();
                int ok_cnt = 0;
                for (int i = 0; i < mAdapter.getItemCount(); i++) {

                    if (mShipModel.getItems().get(i).getShip_qty() == mShipModel.getItems().get(i).getScan_qty()) {
                        ok_cnt++;

                        no_cnt.setText(String.valueOf(mAdapter.getItemCount() - ok_cnt));
                    }
                }
            }
        }
    }//Close onActivityResult


    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.item_date:
                    if (mAdapter.getItemCount() > 0) {
                        mAdapter.clearData();
                        mAdapter.notifyDataSetChanged();
                        et_itm.setText("");
                        et_from.setText("");
                        all_cnt.setText("");
                        no_cnt.setText("");
                        tv_slash.setVisibility(View.GONE);
                    }
                    int c_year = Integer.parseInt(item_date.getText().toString().substring(0, 4));
                    int c_month = Integer.parseInt(item_date.getText().toString().substring(5, 7));
                    int c_day = Integer.parseInt(item_date.getText().toString().substring(8, 10));

                    DatePickerDialog dialog = new DatePickerDialog(mContext, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT, callbackMethod, c_year, c_month - 1, c_day);
                    dialog.show();
                    break;

                case R.id.bt_cst:
                    if (mAdapter.getItemCount() > 0) {
                        mAdapter.clearData();
                        mAdapter.itemsList.clear();
                        mAdapter.notifyDataSetChanged();
                        et_from.setText("");
                    }
                    no_cnt.setText("");
                    all_cnt.setText("");
                    tv_slash.setVisibility(View.GONE);
                    requestCstlist();
                    break;

                case R.id.et_from:
                    if (mAdapter.getItemCount() > 0) {
                        mAdapter.clearData();
                        mAdapter.itemsList.clear();
                        mAdapter.notifyDataSetChanged();
                        et_from.setText("");
                    }
                    no_cnt.setText("");
                    all_cnt.setText("");
                    tv_slash.setVisibility(View.GONE);
                    requestCstlist();
                    break;

                case R.id.btn_next:
                    if (mAdapter.getItemCount() <= 0) {
                        Utils.Toast(mContext, "거래처를 선택해 주세요");
                        return;
                    } else {
                        btn_next.setEnabled(false);
                        request_ship_save();
                    }
                    break;

                case R.id.bt_itm:
                    if (et_from.getText().toString().equals("")) {
                        Utils.Toast(mContext, "거래처를 선택해주세요");
                        return;
                    } else {
                        String m_date = item_date.getText().toString().replace("-", "");
                        mlocationItmListPopup = new ShipItmSearchPopup(getActivity(), R.drawable.popup_title_searchloc, m_date, mShipCstModel.getCst_code(), new Handler() {
                            @Override
                            public void handleMessage(Message msg) {
                                if (msg.what == 1) {
                                    mlocationItmListPopup.hideDialog();
                                    if (mAdapter.getItemCount() > 0) {
                                        mAdapter.clearData();
                                        mAdapter.notifyDataSetChanged();
                                        et_itm.setText("");

                                    }

                                    ItmListModel.Item order = (ItmListModel.Item) msg.obj;
                                    et_itm.setText("[" + order.getItm_code() + "] " + order.getItm_name());
                                    bt_itm.setSelected(true);
                                    ShipList(order.getItm_code());
                                    //requesItmlist(order.getItm_code());
                                }
                            }
                        });
                    }
                    break;

                case R.id.et_itm:
                    if (et_from.getText().toString().equals("")) {
                        Utils.Toast(mContext, "거래처를 선택해주세요");
                        return;
                    } else {
                        String m_date = item_date.getText().toString().replace("-", "");
                        mlocationItmListPopup = new ShipItmSearchPopup(getActivity(), R.drawable.popup_title_searchloc, m_date, mShipCstModel.getCst_code(), new Handler() {
                            @Override
                            public void handleMessage(Message msg) {
                                if (msg.what == 1) {
                                    mlocationItmListPopup.hideDialog();
                                    if (mAdapter.getItemCount() > 0) {
                                        mAdapter.clearData();
                                        mAdapter.notifyDataSetChanged();
                                        et_itm.setText("");

                                    }
                                    ItmListModel.Item order = (ItmListModel.Item) msg.obj;
                                    et_itm.setText("[" + order.getItm_code() + "] " + order.getItm_name());
                                    bt_itm.setSelected(true);
                                    ShipList(order.getItm_code());
                                    //requesItmlist(order.getItm_code());
                                }
                            }
                        });
                    }
                    break;
            }

        }
    };

    /**
     * 품목종류
     */
    private void requesItmlist(final String itm_code) {
        ApiClientService service = ApiClientService.retrofit.create(ApiClientService.class);
        String m_date = item_date.getText().toString().replace("-", "");
        Call<ItmListModel> call = service.ship_itm_list("sp_pda_ship_itm_list", m_date, mShipCstModel.getCst_code(), itm_code);

        call.enqueue(new Callback<ItmListModel>() {
            @Override
            public void onResponse(Call<ItmListModel> call, Response<ItmListModel> response) {
                if (response.isSuccessful()) {
                    ItmListModel model = response.body();
                    //Utils.Log("model ==> :" + new Gson().toJson(model));
                    if (model != null) {
                        if (model.getFlag() == ResultModel.SUCCESS) {

                            mItmListModel = model.getItems();
                            //Call<ShipListModel> call = service.shipListSearch("sp_pda_ship_list", m_date, cst_code, order.getItm_code());

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
            public void onFailure(Call<ItmListModel> call, Throwable t) {
                Utils.LogLine(t.getMessage());
                Utils.Toast(mContext, getString(R.string.error_network));
            }
        });
    }//Close


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


    /**
     * 거래처 리스트
     */
    private void requestCstlist() {
        ApiClientService service = ApiClientService.retrofit.create(ApiClientService.class);
        String m_date = item_date.getText().toString().replace("-", "");
        Call<ShipCstModel> call = service.shipCstList("sp_pda_ship_cst_list", m_date);

        call.enqueue(new Callback<ShipCstModel>() {
            @Override
            public void onResponse(Call<ShipCstModel> call, Response<ShipCstModel> response) {
                if (response.isSuccessful()) {
                    ShipCstModel model = response.body();
                    //Utils.Log("model ==> :" + new Gson().toJson(model));
                    if (model != null) {
                        if (model.getFlag() == ResultModel.SUCCESS) {
                            mLocationListPopup = new LocationCstListPopup(getActivity(), model.getItems(), R.drawable.popup_title_searchloc, new Handler() {
                                @Override
                                public void handleMessage(Message msg) {
                                    ShipCstModel.Item item = (ShipCstModel.Item) msg.obj;
                                    mShipCstModel = item;
                                    et_from.setText("[" + mShipCstModel.getCst_code() + "] " + mShipCstModel.getCst_name());
                                    //mAdapter.notifyDataSetChanged();
                                    cst_code = mShipCstModel.getCst_code();
                                    mLocationListPopup.hideDialog();
                                    if (mAdapter.getItemCount() > 0) {
                                        mAdapter.clearData();
                                        mAdapter.itemsList.clear();
                                    }
                                    ShipList("");

                                }
                            });
                            ShipCstList = model.getItems();


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
            public void onFailure(Call<ShipCstModel> call, Throwable t) {
                Utils.LogLine(t.getMessage());
                Utils.Toast(mContext, getString(R.string.error_network));
            }
        });
    }//Close


    /**
     * 리스트조회
     */
    private void ShipList(String itm_code) {
        ApiClientService service = ApiClientService.retrofit.create(ApiClientService.class);
        String m_date = item_date.getText().toString().replace("-", "");

        Call<ShipListModel> call = service.shipListSearch("sp_pda_ship_list", m_date, cst_code, itm_code);

        call.enqueue(new Callback<ShipListModel>() {
            @Override
            public void onResponse(Call<ShipListModel> call, Response<ShipListModel> response) {
                if (response.isSuccessful()) {
                    mShipModel = response.body();
                    final ShipListModel model = response.body();
                    if (model != null) {
                        if (model.getFlag() == ResultModel.SUCCESS) {

                            if (model.getItems().size() > 0) {
                                for (int i = 0; i < model.getItems().size(); i++) {
                                    ShipListModel.ShipItem item = (ShipListModel.ShipItem) model.getItems().get(i);

                                    mAdapter.addData(item);

                                }
                                mAdapter.notifyDataSetChanged();
                                ship_listview.setAdapter(mAdapter);
                                tv_slash.setVisibility(View.VISIBLE);
                                all_cnt.setText(String.valueOf(mAdapter.getItemCount()));
                                no_cnt.setText(String.valueOf(mAdapter.getItemCount()));
                            }
                        } else {
                            Utils.Toast(mContext, model.getMSG());
                        }
                    }
                } else {
                    tv_slash.setVisibility(View.GONE);
                    all_cnt.setText("");
                    no_cnt.setText("");
                    Utils.LogLine(response.message());
                    Utils.Toast(mContext, response.code() + " : " + response.message());
                }
            }


            @Override
            public void onFailure(Call<ShipListModel> call, Throwable t) {
                Utils.LogLine(t.getMessage());
                Utils.Toast(mContext, getString(R.string.error_network));
            }
        });
    }//Close


    /**
     * 출하등록
     */
    private void request_ship_save() {

        ApiClientService service = ApiClientService.retrofit.create(ApiClientService.class);
        JsonObject json = new JsonObject();
        //로그인 ID
        String userID = (String) SharedData.getSharedData(mContext, SharedData.UserValue.USER_ID.name(), "");

        List<ShipListModel.ShipItem> items = (List<ShipListModel.ShipItem>) mShipModel.getItems();
        JsonArray list = new JsonArray();
        if (items != null && items.size() > 0) {
            for (ShipListModel.ShipItem item : items) {

                List<ShipOkModel.Item> snList = item.getItems();

                if (snList != null && snList.size() > 0) {

                    for (ShipOkModel.Item sn : snList) {
                        if (sn.getWrk_qty() > 0) {
                            JsonObject obj = new JsonObject();
                            //출하순번1
                            obj.addProperty("ship_no1", item.getShip_no1());
                            //출하순번2
                            obj.addProperty("ship_no2", item.getShip_no2());
                            //아이템코드
                            obj.addProperty("itm_code", item.getItm_code());
                            //스캔바코드
                            obj.addProperty("serial_no", sn.getLot_no());
                            //창고코드
                            obj.addProperty("wh_code", sn.getWh_code());
                            //스캔수량
                            obj.addProperty("serial_qty", sn.getWrk_qty());
                            list.add(obj);
                        }
                    }

                }
            }
        }
        json.addProperty("p_corp_code", mShipModel.getItems().get(0).getCorp_code());   //사업장코드
        json.addProperty("p_sal_id", mShipModel.getItems().get(0).getSal_id());         //내수구분
        json.addProperty("p_ship_date", mShipModel.getItems().get(0).getShip_date());    //출하일자
        //json.addProperty("p_ship_no1", mShipModel.getItems().get(0).getShip_no1());     //출하순번1
        json.addProperty("p_user_id", userID);    //로그인ID

        json.add("detail", list);

        Utils.Log("new Gson().toJson(json) ==> : " + new Gson().toJson(json));

        RequestBody body = RequestBody.create(MediaType.parse("application/json"), new Gson().toJson(json));

        Call<ResultModel> call = service.postShipSave(body);

        call.enqueue(new Callback<ResultModel>() {
            @Override
            public void onResponse(Call<ResultModel> call, Response<ResultModel> response) {
                if (response.isSuccessful()) {
                    ResultModel model = response.body();
                    //Utils.Log("model ==> : "+new Gson().toJson(model));
                    if (model != null) {
                        if (model.getFlag() == ResultModel.SUCCESS) {

                            mOneBtnPopup = new OneBtnPopup(getActivity(), "출하등록 되었습니다.", R.drawable.popup_title_alert, new Handler() {
                                @Override
                                public void handleMessage(Message msg) {
                                    if (msg.what == 1) {
                                        //getActivity().finish();
                                        //ShipList();
                                        mOneBtnPopup.hideDialog();
                                        mAdapter.clearData();
                                        mAdapter.notifyDataSetChanged();
                                        no_cnt.setText("");
                                        all_cnt.setText("");
                                        et_itm.setText("");
                                        et_from.setText("");
                                        cst_code = "";
                                        tv_slash.setVisibility(View.GONE);
                                        btn_next.setEnabled(true);
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

                    mTwoBtnPopup = new TwoBtnPopup(getActivity(), "출하등록을 실패하였습니다.\n 재전송 하시겠습니까?", R.drawable.popup_title_alert, new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            if (msg.what == 1) {
                                request_ship_save();
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
                mTwoBtnPopup = new TwoBtnPopup(getActivity(), "출하등록을 실패하였습니다.\n 재전송 하시겠습니까?", R.drawable.popup_title_alert, new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        if (msg.what == 1) {
                            request_ship_save();
                            mTwoBtnPopup.hideDialog();
                            btn_next.setEnabled(true);

                        }
                    }
                });
            }
        });

    }//Close


}//Close Class
