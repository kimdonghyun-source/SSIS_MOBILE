package kr.co.ssis.wms.menu.wh_move;

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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.honeywell.aidc.BarcodeReadEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kr.co.siss.wms.R;
import kr.co.ssis.wms.common.SharedData;
import kr.co.ssis.wms.common.Utils;
import kr.co.ssis.wms.custom.CommonFragment;
import kr.co.ssis.wms.honeywell.AidcReader;
import kr.co.ssis.wms.menu.popup.LocationItmSearchPopup;
import kr.co.ssis.wms.menu.popup.LocationWhSearchPopup;
import kr.co.ssis.wms.menu.popup.OneBtnPopup;
import kr.co.ssis.wms.menu.popup.TwoBtnPopup;
import kr.co.ssis.wms.menu.ship.ShipAdapter;
import kr.co.ssis.wms.menu.ship.ShipFragment;
import kr.co.ssis.wms.menu.ship.ShipOkFragment;
import kr.co.ssis.wms.menu.stock.StockFragmentDetail;
import kr.co.ssis.wms.model.ItmListModel;
import kr.co.ssis.wms.model.OutInModel;
import kr.co.ssis.wms.model.ResultModel;
import kr.co.ssis.wms.model.ShipListModel;
import kr.co.ssis.wms.model.ShipOkModel;
import kr.co.ssis.wms.model.StockDetailModel;
import kr.co.ssis.wms.model.WarehouseModel;
import kr.co.ssis.wms.model.WhModel;
import kr.co.ssis.wms.model.WhMoveListModel;
import kr.co.ssis.wms.network.ApiClientService;
import kr.co.ssis.wms.spinner.SpinnerAdapter;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WhMoveFragment extends CommonFragment {

    EditText et_from;
    TextView tv_empty, tv_list_cnt, tv_cnt, et_from2;
    RecyclerView recycleview;
    ImageButton bt_next, bt_from2;
    List<String> mBarcode;
    String mLocation, BarcodeScan;
    LocationWhSearchPopup mlocationWhListPopup;
    List<WhModel.Item> mWhList;
    String wh_code, wh_code2, beg_barcode;
    WhMoveListModel moveModel;
    List<WhMoveListModel.Item> moveListModel;
    RecyclerView wh_move_listView;
    ListAdapter mAdapter;
    OneBtnPopup mOneBtnPopup;
    TwoBtnPopup mTwoBtnPopup;

    private SoundPool sound_pool;
    int soundId;
    MediaPlayer mediaPlayer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        mBarcode = new ArrayList<>();


    }//Close onCreate


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.frag_wh_move, container, false);

        tv_empty = v.findViewById(R.id.tv_empty);
        recycleview = v.findViewById(R.id.recycleview);
        bt_next = v.findViewById(R.id.bt_next);
        et_from = v.findViewById(R.id.et_from);
        et_from2 = v.findViewById(R.id.et_from2);
        bt_from2 = v.findViewById(R.id.bt_from2);
        tv_list_cnt = v.findViewById(R.id.tv_list_cnt);
        tv_cnt = v.findViewById(R.id.tv_cnt);
        wh_move_listView = v.findViewById(R.id.wh_move_listView);


        wh_move_listView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mAdapter = new ListAdapter(getActivity());
        wh_move_listView.setAdapter(mAdapter);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

        wh_move_listView.setLayoutManager(mLayoutManager);

        bt_from2.setOnClickListener(onClickListener);
        bt_next.setOnClickListener(onClickListener);
        et_from2.setOnClickListener(onClickListener);

        sound_pool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        soundId = sound_pool.load(mContext, R.raw.beepum, 1);

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
                    BarcodeScan = barcode;

                    if (wh_code != null && wh_code2 != null) {
                        if (wh_code.equals(wh_code2)) {
                            Utils.Toast(mContext, "동일한 창고를 선택하셨습니다.");
                            sound_pool.play(soundId, 1f, 1f, 0, 1, 1f);
                            mediaPlayer = MediaPlayer.create(mContext, R.raw.beepum);
                            mediaPlayer.start();
                            return;
                        }
                    }

                    if (mBarcode.contains(BarcodeScan)) {
                        Utils.Toast(mContext, "동일한 품목을 선택하셨습니다.");
                        sound_pool.play(soundId, 1f, 1f, 0, 1, 1f);
                        mediaPlayer = MediaPlayer.create(mContext, R.raw.beepum);
                        mediaPlayer.start();
                        return;
                    }

                    if (beg_barcode != null){
                        if (beg_barcode.equals(BarcodeScan)){
                            Utils.Toast(mContext, "동일한 품목을 선택하셨습니다.");
                            sound_pool.play(soundId, 1f, 1f, 0, 1, 1f);
                            mediaPlayer = MediaPlayer.create(mContext, R.raw.beepum);
                            mediaPlayer.start();
                            return;
                        }
                    }

                    if (wh_code2 == null) {
                        Utils.Toast(mContext, "입고창고를 선택해주세요.");
                        sound_pool.play(soundId, 1f, 1f, 0, 1, 1f);
                        mediaPlayer = MediaPlayer.create(mContext, R.raw.beepum);
                        mediaPlayer.start();
                        return;
                    }

                    tv_empty.setVisibility(View.GONE);
                    beg_barcode = BarcodeScan;
                    pdaSerialScan();
                }
            }
        });
    }//Close onResume

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {

                case R.id.bt_from2:
                    mlocationWhListPopup = new LocationWhSearchPopup(getActivity(), R.drawable.popup_title_searchloc, new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            if (msg.what == 1) {
                                mlocationWhListPopup.hideDialog();
                                WhModel.Item order = (WhModel.Item) msg.obj;
                                et_from2.setText("[" + order.getWh_code() + "] " + order.getWh_name());
                                wh_code2 = order.getWh_code();
                            }
                        }
                    });
                    break;

                case R.id.et_from2:
                    mlocationWhListPopup = new LocationWhSearchPopup(getActivity(), R.drawable.popup_title_searchloc, new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            if (msg.what == 1) {
                                mlocationWhListPopup.hideDialog();
                                WhModel.Item order = (WhModel.Item) msg.obj;
                                et_from2.setText("[" + order.getWh_code() + "] " + order.getWh_name());
                                wh_code2 = order.getWh_code();
                            }
                        }
                    });
                    break;

                case R.id.bt_next:
                    if (mAdapter.itemsList.size() <= 0) {
                        Utils.Toast(mContext, "이동할 품목이 없습니다.");
                        return;
                    }
                    if (wh_code2 == null) {
                        Utils.Toast(mContext, "입고창고를 선택해주세요.");
                        return;
                    }
                    if (wh_code != null && wh_code2 != null) {
                        if (wh_code.equals(wh_code2)) {
                            Utils.Toast(mContext, "동일한 창고를 선택하셨습니다.");
                            return;
                        }
                    }
                    bt_next.setEnabled(false);
                    request_wh_move_save();
            }
        }
    };

    /**
     * 창고이동 바코드스캔
     */
    /*private void pdaSerialScan() {
        ApiClientService service = ApiClientService.retrofit.create(ApiClientService.class);

        Call<WhMoveListModel> call = service.WhMoveList("sp_pda_inv_lot_list", BarcodeScan, wh_code2);

        call.enqueue(new Callback<WhMoveListModel>() {
            @Override
            public void onResponse(Call<WhMoveListModel> call, Response<WhMoveListModel> response) {
                if (response.isSuccessful()) {
                    moveModel = response.body();
                    final WhMoveListModel model = response.body();
                    Utils.Log("model ==> :" + new Gson().toJson(model));
                    if (moveModel != null) {
                        if (moveModel.getFlag() == ResultModel.SUCCESS) {
                            if (model.getItems().size() > 0) {
                                int cnt = 0;
                                if (wh_code2.equals(moveModel.getItems().get(0).getWh_code())){
                                    Utils.Toast(mContext, "동일한 창고를 선택하였습니다.");
                                    return;
                                }


                                //cnt += moveModel.getItems().get(0).getInv_qty();
                                for (int i = 0; i < model.getItems().size(); i++) {

                                    WhMoveListModel.Item item = (WhMoveListModel.Item) model.getItems().get(i);
                                    if (wh_code != null) {
                                        if (!wh_code.equals(item.getWh_code())) {
                                            Utils.Toast(mContext, "출하창고에 해당 시리얼 재고가 없습니다.");
                                            return;
                                        }
                                    }
                                    mAdapter.addData(item);

                                }
                                *//*mAdapter.notifyDataSetChanged();
                                wh_move_listView.setAdapter(mAdapter);*//*

                                mBarcode.add(BarcodeScan);
                                et_from.setText("[" + moveModel.getItems().get(0).getWh_code() + "] " + moveModel.getItems().get(0).getWh_name());
                                wh_code = moveModel.getItems().get(0).getWh_code();
                                tv_cnt.setText(Integer.toString(mAdapter.getItemCount()));
                                for (int j = 0; j < mAdapter.getItemCount(); j++){

                                    cnt += moveListModel.get(j).getInv_qty();
                                }
                                tv_list_cnt.setText(Integer.toString(cnt));

                            }

                        } else {
                            Utils.Toast(mContext, model.getMSG());
                            beg_barcode = "";
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
            public void onFailure(Call<WhMoveListModel> call, Throwable t) {
                Utils.LogLine(t.getMessage());
                Utils.Toast(mContext, getString(R.string.error_network));
            }
        });
    }//Close*/


    /**
     * 창고이동 바코드스캔
     */
    private void pdaSerialScan() {
        ApiClientService service = ApiClientService.retrofit.create(ApiClientService.class);

        Call<WhMoveListModel> call = service.WhMoveList("sp_pda_inv_lot_list", BarcodeScan, wh_code2);

        call.enqueue(new Callback<WhMoveListModel>() {
            @Override
            public void onResponse(Call<WhMoveListModel> call, Response<WhMoveListModel> response) {
                if (response.isSuccessful()) {
                    moveModel = response.body();
                    final WhMoveListModel model = response.body();
                    Utils.Log("model ==> :" + new Gson().toJson(model));
                    if (moveModel != null) {
                        if (moveModel.getFlag() == ResultModel.SUCCESS) {

                            //AidcReader.getInstance().claim(mContext);    스캐너 다시 활성화

                            if (model.getItems().size() > 0) {
                                moveListModel = model.getItems();

                                int cnt = 0;
                                if (wh_code2.equals(moveModel.getItems().get(0).getWh_code())){
                                    Utils.Toast(mContext, "동일한 창고를 선택하였습니다.");
                                    return;
                                }

                                for (int i = 0; i < model.getItems().size(); i++) {

                                    WhMoveListModel.Item item = (WhMoveListModel.Item) model.getItems().get(i);
                                    if (wh_code != null) {
                                        if (!wh_code.equals(item.getWh_code())) {
                                            Utils.Toast(mContext, "출하창고에 해당 시리얼 재고가 없습니다.");
                                            return;
                                        }
                                    }
                                    mAdapter.addData(item);

                                }

                                mAdapter.notifyDataSetChanged();
                                wh_move_listView.setAdapter(mAdapter);
                                mBarcode.add(BarcodeScan);

                                et_from.setText("[" + moveModel.getItems().get(0).getWh_code() + "] " + moveModel.getItems().get(0).getWh_name());
                                wh_code = moveModel.getItems().get(0).getWh_code();
                                tv_cnt.setText(Integer.toString(mAdapter.itemsList.size()));
                                for (int j = 0; j < mAdapter.itemsList.size(); j++){
                                    //cnt += moveListModel.get(j).getInv_qty();
                                    cnt += mAdapter.itemsList.get(j).getInv_qty();
                                }
                                tv_list_cnt.setText(Integer.toString(cnt));
                            }
                        } else {
                            Utils.Toast(mContext, model.getMSG());
                            beg_barcode = "";
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
            public void onFailure(Call<WhMoveListModel> call, Throwable t) {
                Utils.LogLine(t.getMessage());
                Utils.Toast(mContext, getString(R.string.error_network));
            }
        });
    }//Close


    public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {

        List<WhMoveListModel.Item> itemsList;
        Activity mActivity;
        Handler mHandler = null;

        public ListAdapter(Activity context) {
            mActivity = context;
        }

        public void setData(List<WhMoveListModel.Item> list){
            itemsList = list;
        }

        public void clearData(){
            if(itemsList != null)itemsList.clear();
        }

        public void setRetHandler(Handler h){
            this.mHandler = h;
        }

        public List<WhMoveListModel.Item> getData(){
            return itemsList;
        }

        public void addData(WhMoveListModel.Item item) {
            if (itemsList == null) itemsList = new ArrayList<>();
            itemsList.add(item);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, final int z) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cell_wh_move_list, viewGroup, false);
            ViewHolder holder = new ViewHolder(v);
            return holder;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {

            final WhMoveListModel.Item item = itemsList.get(position);

            holder.tv_no.setText(Integer.toString(position+1));
            holder.itm_name.setText(item.getItm_code()+ "  " +item.getItm_name());
            holder.tv_c_name.setText(item.getC_name());
            //holder.tv_wh_name.setText(data.getWh_name());
            holder.inv_qty.setText(Integer.toString(item.getInv_qty()));
            holder.tv_lot.setText(item.getLot_no()+ "   " + item.getWh_name());

            holder.bt_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    mTwoBtnPopup = new TwoBtnPopup(mActivity, item.getLot_no() + " 삭제하시겠습니까?", R.drawable.popup_title_alert, new Handler() {
                        public void handleMessage(Message msg) {
                            if (msg.what == 1) {
                                itemsList.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position, itemsList.size());
                                mBarcode.remove(item.getLot_no());
                                if (beg_barcode != null){
                                    if (beg_barcode.equals(item.getLot_no())){
                                        beg_barcode = "";
                                    }
                                }
                                mTwoBtnPopup.hideDialog();
                                mAdapter.notifyDataSetChanged();
                            }
                        }

                    });

                }
            });


        }

        @Override
        public int getItemCount() {
            return (null == itemsList ? 0 : itemsList.size());
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView tv_no;
            TextView itm_name;
            TextView tv_c_name;
            TextView tv_wh_name;
            TextView inv_qty;
            TextView tv_lot;
            ImageButton bt_delete;

            public ViewHolder(View view) {
                super(view);

                tv_no = view.findViewById(R.id.tv_no);
                itm_name = view.findViewById(R.id.tv_itm_name);
                tv_c_name = view.findViewById(R.id.tv_c_name);
                inv_qty = view.findViewById(R.id.tv_inv_qty);
                tv_lot = view.findViewById(R.id.tv_lot);
                bt_delete = view.findViewById(R.id.bt_delete);

                /*view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Message msg = new Message();
                        msg.obj = itemsList.get(getAdapterPosition());
                        msg.what= getAdapterPosition();
                        mHandler.sendMessage(msg);
                    }
                });*/
            }
        }
    }


    /**
     * 창고이동 저장
     */
    private void request_wh_move_save() {

        ApiClientService service = ApiClientService.retrofit.create(ApiClientService.class);
        JsonObject json = new JsonObject();
        String userID = (String) SharedData.getSharedData(mContext, SharedData.UserValue.USER_ID.name(), "");


        JsonArray list = new JsonArray();

        //List<MatOutSerialScanModel.Item> items = scanAdapter.getData();
        List<WhMoveListModel.Item> items = mAdapter.getData();

        for (WhMoveListModel.Item item : items) {
            JsonObject obj = new JsonObject();
            obj.addProperty("lot_no", item.getLot_no());              //시리얼번호
            obj.addProperty("itm_code", item.getItm_code());          //품목코드
            obj.addProperty("move_qty", item.getInv_qty());           //이동수량
            list.add(obj);
        }


        json.addProperty("p_wh_out", wh_code);    //출고창고
        json.addProperty("p_wh_in", wh_code2);    //입고창고
        json.addProperty("p_user_id", userID);      //로그인ID
        json.add("detail", list);

        Utils.Log("new Gson().toJson(json) ==> : " + new Gson().toJson(json));

        RequestBody body = RequestBody.create(MediaType.parse("application/json"), new Gson().toJson(json));

        Call<ResultModel> call = service.postWhMoveSave(body);

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
                                        bt_next.setEnabled(true);
                                    }
                                }
                            });
                        }
                    }
                } else {
                    Utils.LogLine(response.message());
                    bt_next.setEnabled(true);
                    mTwoBtnPopup = new TwoBtnPopup(getActivity(), "이동 전송을 실패하였습니다.\n 재전송 하시겠습니까?", R.drawable.popup_title_alert, new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            if (msg.what == 1) {
                                request_wh_move_save();
                                mTwoBtnPopup.hideDialog();
                                bt_next.setEnabled(true);

                            }
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<ResultModel> call, Throwable t) {
                Utils.LogLine(t.getMessage());
                bt_next.setEnabled(true);
                mTwoBtnPopup = new TwoBtnPopup(getActivity(), "이동 전송을 실패하였습니다.\n 재전송 하시겠습니까?", R.drawable.popup_title_alert, new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        if (msg.what == 1) {
                            request_wh_move_save();
                            mTwoBtnPopup.hideDialog();
                            bt_next.setEnabled(true);

                        }
                    }
                });
            }
        });

    }//Close

}//Close Activity
