package kr.co.ssis.wms.menu.out_list;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;


import kr.co.siss.wms.R;
import kr.co.ssis.wms.common.Define;
import kr.co.ssis.wms.common.Utils;
import kr.co.ssis.wms.custom.CommonFragment;
import kr.co.ssis.wms.menu.main.BaseActivity;
import kr.co.ssis.wms.model.MorListModel;
import kr.co.ssis.wms.model.ResultModel;
import kr.co.ssis.wms.network.ApiClientService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MorOutDetailFragment extends CommonFragment {

    Context mContext;
    ImageButton bt_item_out;
    TextView mor_qty, cst_name;
    EditText et_merge_1;
    ListView mlistview;
    ListAdapter mAdapter;
    List<MorListModel.Items> mMorList ;
    MorListModel mmorlistmodel;
    Handler mHandler;
    String m_type, s_gubun;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getActivity();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.frag_mor_detail, container, false);
        bt_item_out = v.findViewById(R.id.bt_item_out);
        cst_name = v.findViewById(R.id.cst_name);
        mor_qty = v.findViewById(R.id.mor_qty);
        et_merge_1 = v.findViewById(R.id.et_merge_1);
        mlistview = v.findViewById(R.id.listview);
        mAdapter = new ListAdapter();
        mlistview.setAdapter(mAdapter);
        mHandler = handler;

        //bt_item_out.setOnClickListener(onClickListener);

        Bundle args = getArguments();
        if (args!=null){
           final String TYPE = args.getString("TYPE");      //?????? / ?????????
           final String GUBUN = args.getString("GUBUN");    //????????????
           final String SLIPNO = args.getString("SLIPNO");
           final String NAME = args.getString("NAME");
           final String QTY = args.getString("QTY");

            mor_qty.setText(QTY);       //????????????
            cst_name.setText(NAME);     //????????????
            et_merge_1.setText(SLIPNO); //????????????
            s_gubun = GUBUN;        //????????????(??????) O=??????, A=AS
            m_type = TYPE;          //?????? / ????????? ??????

            requestMorListDetail();

        }

        return v;
    }//onCreateView Close

    private void requestMorListDetail() {
        ApiClientService service = ApiClientService.retrofit.create(ApiClientService.class);

        Call<MorListModel> call = service.mordetail("sp_pda_dis_mor_detail", m_type, et_merge_1.getText().toString(), s_gubun);

        call.enqueue(new Callback<MorListModel>() {
            @Override
            public void onResponse(Call<MorListModel> call, Response<MorListModel> response) {
                if (response.isSuccessful()) {
                    mmorlistmodel = response.body();
                    final MorListModel model = response.body();

                    if (mmorlistmodel != null) {
                        if (mmorlistmodel.getFlag() == ResultModel.SUCCESS) {
                            Utils.Log("model ==> ??:" + new Gson().toJson(mmorlistmodel));
                            mMorList = mmorlistmodel.getItems();
                            mAdapter.notifyDataSetChanged();
                            mlistview.setAdapter(mAdapter);

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
            public void onFailure(Call<MorListModel> call, Throwable t) {
                Utils.LogLine(t.getMessage());
                Utils.Toast(mContext, getString(R.string.error_network));
            }
        });
    }



    class ListAdapter extends BaseAdapter {
        LayoutInflater mInflater;
        List<MorListModel.Items> itemsList;

        public ListAdapter() {
            mInflater = LayoutInflater.from(mContext);
        }

        public void addData(MorListModel.Items item) {
            if (mMorList == null) mMorList = new ArrayList<>();
            mMorList.add(item);
        }

        @Override
        public int getCount() {
            if (mMorList == null) {
                return 0;
            }

            return mMorList.size();
        }

        public List<MorListModel.Items> getData(){
            return itemsList;
        }

        @Override
        public MorListModel.Items getItem(int position) {
            return mMorList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View v = convertView;
            ViewHolder holder;
            if (v == null) {
                holder = new ViewHolder();
                v = mInflater.inflate(R.layout.cell_mor_detail, null);

                v.setTag(holder);

                holder.itm_name = v.findViewById(R.id.tv_product);
                holder.itm_size = v.findViewById(R.id.tv_size);
                holder.mor_qty = v.findViewById(R.id.tv_qty);
                holder.h_name = v.findViewById(R.id.tv_head);
                holder.mor_h_qty = v.findViewById(R.id.tv_h_count);
                holder.s_name = v.findViewById(R.id.tv_sharft);
                holder.mor_s_qty = v.findViewById(R.id.tv_s_count);


            } else {
                holder = (ListAdapter.ViewHolder) v.getTag();
            }

            final MorListModel.Items data = mMorList.get(position);
            holder.itm_name.setText(data.getItm_name());
            holder.itm_size.setText(data.getItm_size());
            holder.mor_qty.setText(Integer.toString(data.getMor_qty()));
            holder.h_name.setText(data.getH_name());
            holder.mor_h_qty.setText(Integer.toString(data.getMor_h_qty()));
            holder.s_name.setText(data.getS_name());
            holder.mor_s_qty.setText(Integer.toString(data.getMor_s_qty()));


            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Message msg = mHandler.obtainMessage();
                    msg.what = 1;
                    msg.obj = data;
                    mHandler.sendMessage(msg);
                    goMorItem();
                }
            });


            return v;
        }

        public class ViewHolder {
            TextView itm_name;
            TextView itm_size;

            //?????????
            TextView h_code;
            TextView h_name;
            TextView h_color_code;
            TextView h_color_name;
            TextView h_loft_code;
            TextView h_loft_name;
            TextView h_direc_code;
            TextView h_direc_name;
            TextView h_head_code;
            TextView h_head_name;
            TextView h_weight_code;
            TextView h_weight_name;


            //????????????
            TextView s_code;
            TextView s_name;
            TextView s_color_code;
            TextView s_color_name;
            TextView s_strong_code;
            TextView s_strong_name;


            TextView mor_qty;
            TextView mor_h_qty;
            TextView mor_s_qty;



        }
    }

    private void goMorItem(){
        List<MorListModel.Items> itms = mAdapter.getData();
        Intent intent = new Intent(mContext, BaseActivity.class);
        intent.putExtra("menu", Define.MENU_PRODUCTION_OUT);
        Bundle args = new Bundle();
        for (int i = 0; i < mmorlistmodel.getItems().size(); i++){
            MorListModel.Items o = mmorlistmodel.getItems().get(i);

            args.putString("CORPCODE", o.getCorp_code());                            //???????????????
            args.putString("PRODUCT", o.getItm_name());                              //????????????
            args.putString("QTY", String.valueOf(o.getMor_qty()));                   //????????????
            args.putString("H_COUNT", String.valueOf(o.getMor_h_qty()));             //????????????
            args.putString("S_COUNT", String.valueOf(o.getMor_s_qty()));             //???????????????
            args.putString("mor_date", o.getMor_date());                             //???????????????
            args.putString("mor_no1", String.valueOf(o.getMor_no1()));               //??????????????????
            args.putString("mor_h_qty", String.valueOf(o.getMor_h_qty()));           //??????????????????
            args.putString("mor_s_qty", String.valueOf(o.getMor_s_qty()));           //?????????????????????
            args.putString("TYPE", m_type);                                          //??????/????????? ??????
            args.putString("GUBUN", s_gubun);                                        //????????????

            //??????
            args.putString("HAEDCODE", o.getH_code());                               //????????????
            args.putString("HAEDNAME", o.getH_name());                               //?????????
            args.putString("HEADCOLOR_C", o.getH_color_code());                      //??????????????????
            args.putString("HEADCOLOR_NM", o.getH_color_name());                     //???????????????
            args.putString("HEADLOFT_C", o.getH_loft_code());                        //??????????????????
            args.putString("HEADLOFT_NM", o.getH_loft_name());                       //???????????????
            args.putString("HEADDIREC_C", o.getHaed_direc_code());                   //??????????????????
            args.putString("HEADDIREC_NM", o.getHaed_direc_name());                  //???????????????
            args.putString("HEADHEAD_C", o.getHead_code());                          //??????????????????
            args.putString("HEADHEAD_NM", o.getHead_code_name());                    //???????????????
            args.putString("HEADWEIGHT_C", o.getH_weight_code());                    //??????????????????
            args.putString("HEADWEIGHT_NM", o.getH_weight_name());                   //???????????????

            //?????????
            args.putString("SHAFTCODE", o.getS_code());                              //???????????????
            args.putString("SHAFTNAME", o.getS_name());                              //????????????
            args.putString("SHAFTCOLOR_C", o.getS_color_code());                     //?????????????????????
            args.putString("SHAFTCOLOR_NM", o.getS_color_name());                    //??????????????????
            args.putString("SHAFTSTRONG_C", o.getS_strong_code());                   //?????????????????????
            args.putString("SHAFTSTRONG_NM", o.getS_strong_name());                  //??????????????????


        }
        intent.putExtra("args",args);
        startActivity(intent);
    }



}//Class close
