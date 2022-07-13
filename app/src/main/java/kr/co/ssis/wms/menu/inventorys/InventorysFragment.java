package kr.co.ssis.wms.menu.inventorys;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.honeywell.aidc.BarcodeReadEvent;

import java.util.ArrayList;
import java.util.List;

import kr.co.siss.wms.R;
import kr.co.ssis.wms.common.SharedData;
import kr.co.ssis.wms.common.Utils;
import kr.co.ssis.wms.custom.CommonFragment;
import kr.co.ssis.wms.honeywell.AidcReader;
import kr.co.ssis.wms.menu.popup.LocationCstListPopup;
import kr.co.ssis.wms.menu.popup.LocationDisTypeListPopup;
import kr.co.ssis.wms.menu.popup.LocationWhListPopup;
import kr.co.ssis.wms.menu.popup.OneBtnPopup;
import kr.co.ssis.wms.menu.popup.TwoBtnPopup;
import kr.co.ssis.wms.model.DisTypeModel;
import kr.co.ssis.wms.model.InvenModel;
import kr.co.ssis.wms.model.MatOutDetailGet;
import kr.co.ssis.wms.model.OutOkModel;
import kr.co.ssis.wms.model.ResultModel;
import kr.co.ssis.wms.model.ShipCstModel;
import kr.co.ssis.wms.model.WhModel;
import kr.co.ssis.wms.network.ApiClientService;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InventorysFragment extends CommonFragment {

    Context mContext;
    EditText et_from, et_wh_from, et_ok_from, et_qty;
    TextView tv_itm_code, tv_itm_name, tv_itm_size, tv_itm_unit, tv_wh_name, tv_qty;
    ImageButton bt_cst, bt_cst1, btn_next;
    String barcodeScan, wh_code, dis_code;
    InvenModel mInvenModel;
    List<InvenModel.Item> mInvenListModel;
    LocationWhListPopup mLocationWhListPopup;
    LocationDisTypeListPopup mLocationDisTypePopup;
    WhModel.Item mWhmodel;
    List<WhModel.Item> mWhListModel;
    DisTypeModel.Item mDisModel;
    List<DisTypeModel.Item> mDisListModel;
    OneBtnPopup mOneBtnPopup;
    TwoBtnPopup mTwoBtnPopup;
    String beg_barcode;
    List<String> mBarcode;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        mBarcode = new ArrayList<>();

    }//Close onCreate


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.frag_inventorys, container, false);

        tv_itm_code = v.findViewById(R.id.tv_itm_code);
        tv_itm_name = v.findViewById(R.id.tv_itm_name);
        tv_itm_size = v.findViewById(R.id.tv_itm_size);
        tv_itm_unit = v.findViewById(R.id.tv_itm_unit);
        tv_wh_name = v.findViewById(R.id.tv_wh_name);
        tv_qty = v.findViewById(R.id.tv_qty);
        et_from = v.findViewById(R.id.et_from);
        et_wh_from = v.findViewById(R.id.et_wh_from);
        et_ok_from = v.findViewById(R.id.et_ok_from);
        bt_cst = v.findViewById(R.id.bt_cst);
        bt_cst1 = v.findViewById(R.id.bt_cst1);
        et_qty = v.findViewById(R.id.et_qty);
        btn_next = v.findViewById(R.id.btn_next);

        bt_cst.setOnClickListener(onClickListener);
        bt_cst1.setOnClickListener(onClickListener);
        btn_next.setOnClickListener(onClickListener);



        return v;

    }//Close onCreateView

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

                    if (mBarcode.contains(barcode)) {
                        Utils.Toast(mContext, "동일한 SerialNo를 스캔하셨습니다.");
                        return;
                    }

                    if (beg_barcode != null) {
                        if (beg_barcode.equals(barcodeScan)) {
                            Utils.Toast(mContext, "동일한 바코드를 스캔하였습니다.");
                            return;
                        }
                    }


                    pdaSerialScan();

                    mBarcode.add(barcodeScan);
                }
            }
        });

    }//Close onResume



    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {

                case R.id.bt_cst:
                    if (tv_itm_code == null) {
                        Utils.Toast(mContext, "품목을 스캔해주세요.");
                        return;
                    } else {
                        requestWhlist();
                    }
                    break;

                case R.id.bt_cst1:
                    if (tv_itm_code == null) {
                        Utils.Toast(mContext, "품목을 스캔해주세요.");
                        return;

                    }else if (et_qty.getText().toString().equals("")){
                        Utils.Toast(mContext, "실사수량을 입력해주세요.");
                        return;

                    }else if (tv_qty.getText().toString().equals(et_qty.getText().toString())) {
                        Utils.Toast(mContext, "품목 수량과 실사수량이 같습니다.");
                        return;
                    }else {
                        requesTypelist();
                    }
                    break;

                case R.id.btn_next:
                    if (et_qty.getText().equals("")) {
                        Utils.Toast(mContext, "실사수량을 입력해주세요.");
                        return;
                    }
                    if (wh_code == null) {
                        Utils.Toast(mContext, "변경할 창고를 선택해주세요.");
                        return;
                    }

                    if (dis_code == null) {
                        Utils.Toast(mContext, "실사 종류를 선택해주세요.");
                        return;
                    }
                    if (tv_qty.getText().toString().equals(et_qty.getText().toString())) {
                        Utils.Toast(mContext, "변경된 내역이 없습니다.");
                        return;
                    } else {
                        request_dis_save();
                        btn_next.setEnabled(false);
                    }
                    break;
            }

        }
    };

    /**
     * 창고 리스트
     */
    private void requestWhlist() {
        ApiClientService service = ApiClientService.retrofit.create(ApiClientService.class);

        Call<WhModel> call = service.WhList("sp_pda_wh_list");

        call.enqueue(new Callback<WhModel>() {
            @Override
            public void onResponse(Call<WhModel> call, Response<WhModel> response) {
                if (response.isSuccessful()) {
                    WhModel model = response.body();
                    //Utils.Log("model ==> :" + new Gson().toJson(model));
                    if (model != null) {
                        if (model.getFlag() == ResultModel.SUCCESS) {
                            mLocationWhListPopup = new LocationWhListPopup(getActivity(), model.getItems(), R.drawable.popup_title_searchloc, new Handler() {
                                @Override
                                public void handleMessage(Message msg) {
                                    WhModel.Item item = (WhModel.Item) msg.obj;
                                    mWhmodel = item;
                                    et_wh_from.setText("[" + mWhmodel.getWh_code() + "] " + mWhmodel.getWh_name());
                                    //mAdapter.notifyDataSetChanged();
                                    wh_code = mWhmodel.getWh_code();
                                    mLocationWhListPopup.hideDialog();
                                 /*   if (mAdapter.getCount() > 0) {
                                        mAdapter.clearData();
                                        mShipModel = null;
                                    }*/

                                }
                            });
                            mWhListModel = model.getItems();


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
            public void onFailure(Call<WhModel> call, Throwable t) {
                Utils.LogLine(t.getMessage());
                Utils.Toast(mContext, getString(R.string.error_network));
            }
        });
    }//Close

    /**
     * 실사종류
     */
    private void requesTypelist() {
        ApiClientService service = ApiClientService.retrofit.create(ApiClientService.class);

        Call<DisTypeModel> call = service.DisTypeList("sp_pda_dis_type_list", tv_qty.getText().toString(), et_qty.getText().toString());

        call.enqueue(new Callback<DisTypeModel>() {
            @Override
            public void onResponse(Call<DisTypeModel> call, Response<DisTypeModel> response) {
                if (response.isSuccessful()) {
                    DisTypeModel model = response.body();
                    //Utils.Log("model ==> :" + new Gson().toJson(model));
                    if (model != null) {
                        if (model.getFlag() == ResultModel.SUCCESS) {
                            mLocationDisTypePopup = new LocationDisTypeListPopup(getActivity(), model.getItems(), R.drawable.popup_title_searchloc, new Handler() {
                                @Override
                                public void handleMessage(Message msg) {
                                    DisTypeModel.Item item = (DisTypeModel.Item) msg.obj;
                                    mDisModel = item;
                                    et_ok_from.setText("[" + mDisModel.getC_code() + "] " + mDisModel.getC_name());
                                    //mAdapter.notifyDataSetChanged();
                                    dis_code = mDisModel.getC_code();
                                    mLocationDisTypePopup.hideDialog();
                                 /*   if (mAdapter.getCount() > 0) {
                                        mAdapter.clearData();
                                        mShipModel = null;
                                    }*/

                                }
                            });
                            mDisListModel = model.getItems();


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
            public void onFailure(Call<DisTypeModel> call, Throwable t) {
                Utils.LogLine(t.getMessage());
                Utils.Toast(mContext, getString(R.string.error_network));
            }
        });
    }//Close

    /**
     * 재고실사등록 스캔
     */
    private void pdaSerialScan() {
        ApiClientService service = ApiClientService.retrofit.create(ApiClientService.class);

        Call<InvenModel> call = service.InvenSerialScan("sp_pda_lot_list", barcodeScan);

        call.enqueue(new Callback<InvenModel>() {
            @Override
            public void onResponse(Call<InvenModel> call, Response<InvenModel> response) {
                if (response.isSuccessful()) {
                    mInvenModel = response.body();
                    final InvenModel model = response.body();
                    Utils.Log("model ==> :" + new Gson().toJson(model));
                    if (mInvenModel != null) {
                        if (mInvenModel.getFlag() == ResultModel.SUCCESS) {
                            if (model.getItems().size() > 0) {
                                //tv_bor_code, tv_wh_code, tv_itm_code, tv_itm_size, tv_itm_unit, tv_qty;
                                tv_itm_code.setText(mInvenModel.getItems().get(0).getItm_code());
                                tv_itm_name.setText(mInvenModel.getItems().get(0).getItm_name());
                                tv_itm_size.setText(mInvenModel.getItems().get(0).getItm_size());
                                tv_itm_unit.setText(mInvenModel.getItems().get(0).getC_name());
                                tv_wh_name.setText(mInvenModel.getItems().get(0).getWh_name());
                                tv_qty.setText(Integer.toString(mInvenModel.getItems().get(0).getInv_qty()));

                            }
                            beg_barcode = barcodeScan;

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
            public void onFailure(Call<InvenModel> call, Throwable t) {
                Utils.LogLine(t.getMessage());
                Utils.Toast(mContext, getString(R.string.error_network));
            }
        });
    }//Close


    /**
     * 재고실사 저장
     */
    private void request_dis_save() {

        ApiClientService service = ApiClientService.retrofit.create(ApiClientService.class);
        JsonObject json = new JsonObject();
        String userID = (String) SharedData.getSharedData(mContext, SharedData.UserValue.USER_ID.name(), "");

        int qty = Integer.parseInt(tv_qty.getText().toString()) - Integer.parseInt(et_qty.getText().toString());


        JsonArray list = new JsonArray();

        JsonObject obj = new JsonObject();
        obj.addProperty("p_lot_no", barcodeScan);
        obj.addProperty("p_itm_code", mInvenModel.getItems().get(0).getItm_code());
        obj.addProperty("p_dis_qty", qty);
        obj.addProperty("p_wh_code", mInvenModel.getItems().get(0).getWh_code());
        obj.addProperty("p_dis_type", dis_code);
        list.add(obj);

       /* json.addProperty("p_lot_no", barcodeScan);          //LOT_NO
        json.addProperty("p_itm_code", mInvenModel.getItems().get(0).getItm_code());          //품목코드
        json.addProperty("p_dis_qty", qty);    //차이 수량
        json.addProperty("p_wh_code", mInvenModel.getItems().get(0).getWh_code());    //창고코드
        json.addProperty("p_dis_type", dis_code);    //실사종류*/
        json.addProperty("p_user_id", userID);    //로그인ID
        json.add("detail", list);

        Utils.Log("new Gson().toJson(json) ==> : " + new Gson().toJson(json));

        RequestBody body = RequestBody.create(MediaType.parse("application/json"), new Gson().toJson(json));

        Call<ResultModel> call = service.postDisSave(body);

        call.enqueue(new Callback<ResultModel>() {
            @Override
            public void onResponse(Call<ResultModel> call, Response<ResultModel> response) {
                if (response.isSuccessful()) {
                    ResultModel model = response.body();
                    //Utils.Log("model ==> : "+new Gson().toJson(model));
                    if (model != null) {
                        if (model.getFlag() == ResultModel.SUCCESS) {

                            mOneBtnPopup = new OneBtnPopup(getActivity(), "실사처리 되었습니다.", R.drawable.popup_title_alert, new Handler() {
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
                    mTwoBtnPopup = new TwoBtnPopup(getActivity(), "실사처리를 실패하였습니다.\n 재전송 하시겠습니까?", R.drawable.popup_title_alert, new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            if (msg.what == 1) {
                                request_dis_save();
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
                mTwoBtnPopup = new TwoBtnPopup(getActivity(), "실사처리를 실패하였습니다.\n 재전송 하시겠습니까?", R.drawable.popup_title_alert, new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        if (msg.what == 1) {
                            request_dis_save();
                            mTwoBtnPopup.hideDialog();
                            btn_next.setEnabled(true);

                        }
                    }
                });
            }
        });

    }//Close


}