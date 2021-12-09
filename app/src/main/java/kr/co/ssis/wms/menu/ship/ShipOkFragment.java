package kr.co.ssis.wms.menu.ship;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.honeywell.aidc.BarcodeReadEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import kr.co.siss.wms.R;
import kr.co.ssis.wms.common.Define;
import kr.co.ssis.wms.common.Utils;
import kr.co.ssis.wms.custom.CommonFragment;
import kr.co.ssis.wms.honeywell.AidcReader;
import kr.co.ssis.wms.menu.main.BaseActivity;
import kr.co.ssis.wms.menu.move_ask.MoveAskScanAdapter;
import kr.co.ssis.wms.menu.popup.TwoBtnPopup;
import kr.co.ssis.wms.model.MoveAskModel;
import kr.co.ssis.wms.model.ResultModel;
import kr.co.ssis.wms.model.ShipListModel;
import kr.co.ssis.wms.model.ShipOkModel;
import kr.co.ssis.wms.network.ApiClientService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShipOkFragment extends CommonFragment {

    Context mContext;

    ShipListModel mShipModel = null;
    ShipListModel.ShipItem order = null;
    int mPosition = -1;

    TextView tv_itm_code;
    TextView tv_itm_name;
    TextView tv_itm_size;
    TextView tv_c_name;
    TextView tv_ship_qty;
    TextView tv_pickin_qty, tv_cnt;
    RecyclerView ship_ok_listview;
    List<ShipOkModel.Item> mOkListModel;
    ShipOkModel.Item mOrderModel;
    ShipOkModel mOkModel;
    ListAdapter mAdapter;
    String barcodeScan, beg_barcode = null;
    int count = 0;
    ImageButton btn_next_ok;
    List<String> mBarcode;
    String mOrderNo;
    TwoBtnPopup mPopup;
    Activity mActivity;
    ArrayList<ShipOkModel.Item> listViewItemList = new ArrayList<ShipOkModel.Item>();


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

        View v = inflater.inflate(R.layout.frag_ship_ok, container, false);

        Bundle arguments = getArguments();
        mShipModel = (ShipListModel) arguments.getSerializable("model");
        mPosition = arguments.getInt("position");
        order = mShipModel.getItems().get(mPosition);

        tv_itm_code = v.findViewById(R.id.tv_itm_code);
        tv_itm_name = v.findViewById(R.id.tv_itm_name);
        tv_itm_size = v.findViewById(R.id.tv_itm_size);
        tv_c_name = v.findViewById(R.id.tv_c_name);
        tv_ship_qty = v.findViewById(R.id.tv_ship_qty);
        tv_pickin_qty = v.findViewById(R.id.tv_pickin_qty);
        ship_ok_listview = v.findViewById(R.id.ship_ok_listview);
        btn_next_ok = v.findViewById(R.id.btn_next_ok);
        tv_cnt = v.findViewById(R.id.tv_cnt);


        ship_ok_listview.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mAdapter = new ListAdapter(getActivity());
        ship_ok_listview.setAdapter(mAdapter);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

        ship_ok_listview.setLayoutManager(mLayoutManager);


        tv_itm_code.setText(order.getItm_code());
        tv_itm_name.setText(order.getItm_name());
        tv_itm_size.setText(order.getItm_size());
        tv_c_name.setText(order.getC_name());
        tv_ship_qty.setText(Integer.toString(order.getShip_qty()));

        btn_next_ok.setOnClickListener(onClickListener);

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
                    barcodeScan = barcode;

                    if (mBarcode != null) {
                        if (mBarcode.contains(barcode)) {
                            Utils.Toast(mContext, "동일한 바코드를 스캔하였습니다.");
                            return;
                        }
                    }


                    if (beg_barcode != null) {
                        if (beg_barcode.equals(barcodeScan)) {
                            Utils.Toast(mContext, "동일한 바코드를 스캔하였습니다.");
                            return;
                        }
                    }

                    if (mAdapter.itemsList != null) {
                        for (int i = 0; i < mAdapter.getItemCount(); i++) {
                            if (mAdapter.itemsList.get(i).getLot_no().equals(barcodeScan)) {
                                Utils.Toast(mContext, "동일한 바코드를 스캔하였습니다.");
                                return;
                            }
                        }
                    }

                    if (order.getShip_qty() == count) {
                        Utils.Toast(mContext, "피킹 수량이 의뢰수량을 초과하였습니다.");
                        return;
                    } else {
                        pdaSerialScan();
                        beg_barcode = barcodeScan;
                        //mOrderNo = barcode;

                    }

                }
            }
        });

    }//Close onResume


    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {

                case R.id.btn_next_ok:

                    if (mAdapter.getItemCount() > 0) {
                        List<ShipOkModel.Item> datas = new ArrayList<>();
                        List<ShipOkModel.Item> itms = mAdapter.getData();
                        int count = 0;
                        for (int i = 0; i < itms.size(); i++) {
                            ShipOkModel.Item itm = itms.get(i);
                            if (itm.getWrk_qty() > 0) {
                                datas.add(itm);
                                count += itm.getWrk_qty();
                                order.setScan_qty(count);
                            }
                        }

                        if (count > order.getShip_qty()) {
                            Utils.Toast(mContext, "피킹수량이 의뢰수량보다 많습니다.");
                            return;
                        }

                        mShipModel.getItems().get(mPosition).setSet_scan_qty(count);
                        mShipModel.getItems().get(mPosition).setItems(datas);

                        Intent i = new Intent();
                        i.putExtra("model", mShipModel);
                        getActivity().setResult(Activity.RESULT_OK, i);
                        getActivity().finish();
                        break;

                    } else {
                        Utils.Toast(mContext, "스캔 내역이 없습니다.");
                    }
            }
        }
    };


    private void ShipScanList() {
        List<ShipOkModel.Item> datas = new ArrayList<>();

        Intent intent = new Intent(getActivity(), BaseActivity.class);
        intent.putExtra("menu", Define.MENU_SHIP);
        Bundle extras = new Bundle();

        extras.putSerializable("model", mOkModel);
        extras.putSerializable("model1", mShipModel);
        intent.putExtra("args", extras);
        getActivity().setResult(Activity.RESULT_OK, intent);
        //startActivityForResult(intent, 100);
        getActivity().finish();
    }


    /**
     * 출하피킹 바코드스캔
     */
    private void pdaSerialScan() {
        ApiClientService service = ApiClientService.retrofit.create(ApiClientService.class);

        Call<ShipOkModel> call = service.shipOkSerialScan("sp_pda_ship_scan", barcodeScan, order.getItm_code(), order.getWh_code());

        call.enqueue(new Callback<ShipOkModel>() {
            @Override
            public void onResponse(Call<ShipOkModel> call, Response<ShipOkModel> response) {
                if (response.isSuccessful()) {
                    mOkModel = response.body();
                    final ShipOkModel model = response.body();
                    Utils.Log("model ==> :" + new Gson().toJson(model));
                    if (mOkModel != null) {
                        if (mOkModel.getFlag() == ResultModel.SUCCESS) {

                            List<ShipOkModel.Item> itms = mAdapter.getData();
                            if (model.getItems().size() > 0) {
                                int c_cnt = 0;

                                for (int i = 0; i < model.getItems().size(); i++) {
                                    ShipOkModel.Item item = (ShipOkModel.Item) model.getItems().get(i);
                                    mAdapter.addData(item);

                                }

                                ship_ok_listview.setAdapter(mAdapter);


                                mAdapter.notifyDataSetChanged();
                                tv_cnt.setText(mAdapter.getItemCount() + " 건");


                                //mAdapter.notifyDataSetChanged();

                                for (int j = 0; j < mAdapter.getItemCount(); j++) {
                                    c_cnt += mAdapter.itemsList.get(j).getWrk_qty();

                                }
                                tv_pickin_qty.setText(Utils.setComma(c_cnt));
                            }

                            mBarcode.add(barcodeScan);



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
            public void onFailure(Call<ShipOkModel> call, Throwable t) {
                Utils.LogLine(t.getMessage());
                Utils.Toast(mContext, getString(R.string.error_network));
            }
        });
    }//Close

    public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {

        List<ShipOkModel.Item> itemsList;
        Activity mActivity;
        Handler mHandler = null;

        public ListAdapter(Activity context) {
            mActivity = context;
            itemsList = new ArrayList<>();
          /*  if (itemsList == null) {
                listViewItemList = new ArrayList<ShipOkModel.Item>() ;
            } else {
                listViewItemList = listViewItemList ;
            }*/
        }


        public void setData(List<ShipOkModel.Item> item) {
            itemsList = item;
        }

        public void addData(ShipOkModel.Item item) {
            if (itemsList == null) itemsList = new ArrayList<>();
            itemsList.add(item);
        }

        public void clearData() {
            itemsList.clear();
        }

        public void setSumHandler(Handler h) {
            this.mHandler = h;
        }

        public List<ShipOkModel.Item> getData() {
            return itemsList;
        }

        public int getCount() {
            if (itemsList == null) {
                return 0;
            }

            return itemsList.size();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cell_ship_ok, viewGroup, false);
            ViewHolder holder = new ViewHolder(v);
            return holder;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {

            final ShipOkModel.Item item = itemsList.get(position);


            holder.lot_no.setText(item.getLot_no());
            holder.tv_ea.setText(order.getC_name());
            holder.inv_qty.setText(Integer.toString(item.getWrk_qty()));
            holder.tv_no.setText(Integer.toString(position));
            item.setNo(mAdapter.getCount());

            holder.inv_qty.addTextChangedListener(new TextWatcher() {
                String result = "";
                int c_cnt = 0;

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    Log.d("JeLib", "---------------------------");

                    if (s.toString().length() == 0) {
                        itemsList.get(holder.getAdapterPosition()).setWrk_qty(0);

                        tv_pickin_qty.setText("");

                        int c_cnt = 0;
                        for (int j = 0; j < mAdapter.getItemCount(); j++) {
                            c_cnt += mAdapter.itemsList.get(j).getWrk_qty();

                        }
                        tv_pickin_qty.setText(Utils.setComma(c_cnt));
                    }

                    if (s.toString().length() > 0 && !s.toString().equals(result)) {     // StackOverflow를 막기위해,
                        result = s.toString();   // 에딧텍스트의 값을 변환하여, result에 저장.

                        int cnt = Utils.stringToInt(result);

                        holder.inv_qty.setText(result);    // 결과 텍스트 셋팅.
                        holder.inv_qty.setSelection(result.length());     // 커서를 제일 끝으로 보냄.

                        //입력된 수량을 list에 넣어줌
                        itemsList.get(holder.getAdapterPosition()).setWrk_qty(cnt);

                        tv_pickin_qty.setText("");

                        int c_cnt = 0;
                        for (int j = 0; j < mAdapter.getItemCount(); j++) {
                            c_cnt += mAdapter.itemsList.get(j).getWrk_qty();

                        }
                        tv_pickin_qty.setText(Utils.setComma(c_cnt));


                    }
                }
            });

            holder.bt_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    itemsList.remove(position);
                    mBarcode.remove(barcodeScan);
                    mAdapter.notifyDataSetChanged();
                    tv_cnt.setText(mAdapter.getItemCount() + " 건");

                    if (mAdapter.getItemCount() > 0) {
                        count = 0;
                        for (int i = 0; i < mAdapter.getItemCount(); i++) {
                            count += itemsList.get(i).getWrk_qty();
                        }
                        tv_pickin_qty.setText(Utils.setComma(count));
                    } else {
                        tv_pickin_qty.setText("");
                    }

                    if (beg_barcode.equals(barcodeScan)) {
                        beg_barcode = "";
                    }
                }

            });


        }

        @Override
        public int getItemCount() {
            return (null == itemsList ? 0 : itemsList.size());
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView lot_no;
            //TextView inv_qty;
            EditText inv_qty;
            TextView tv_ea;
            ImageButton bt_delete;
            TextView tv_no;

            public ViewHolder(View view) {
                super(view);

                lot_no = view.findViewById(R.id.tv_lot_no);
                inv_qty = view.findViewById(R.id.tv_qty);
                tv_ea = view.findViewById(R.id.tv_ea);
                bt_delete = view.findViewById(R.id.bt_delete);
                tv_no = view.findViewById(R.id.tv_no);

            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mBarcode.clear();
    }


}//Close Activity
