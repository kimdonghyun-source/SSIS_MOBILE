package kr.co.ssis.wms.menu.main;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import java.util.ArrayList;

import kr.co.ssis.wms.GlobalApplication;

import kr.co.ssis.wms.common.Define;
import kr.co.ssis.wms.custom.BusProvider;
import kr.co.ssis.wms.custom.CommonCompatActivity;
import kr.co.ssis.wms.custom.CommonFragment;

import kr.co.ssis.wms.menu.inout_search.InOutSearchFragment;
import kr.co.ssis.wms.menu.inventorys.InventorysFragment;
import kr.co.ssis.wms.menu.new_out.NewOutFragment;
import kr.co.ssis.wms.menu.ok_group.InGroupFragment;
import kr.co.ssis.wms.menu.ok_lot.InLotFragment;
import kr.co.ssis.wms.menu.out_list.MorOutListFragment;
import kr.co.ssis.wms.menu.out_in.OutInFragment;
import kr.co.ssis.wms.menu.out_ok.OutOkFragment;
import kr.co.ssis.wms.menu.popup.TwoBtnPopup;

import kr.co.ssis.wms.menu.ship.ShipFragment;
import kr.co.ssis.wms.menu.ship.ShipOkFragment;
import kr.co.ssis.wms.menu.wh_move.WhMoveFragment;
import kr.co.ssis.wms.model.UserInfoModel;
import kr.co.siss.wms.R;

public class BaseActivity extends CommonCompatActivity {
    Context mContext;

    //사이드 메뉴 레이아웃
    DrawerLayout drawer;
    View drawer_layout;
    //사이드 메뉴 리스트
    //DrawerMenu mDrawerMenu;
    //좌측 메뉴 리스트 아답터
    ListAdapter mAdapter;
    //다른 메뉴 이동시 묻는 팝업
    TwoBtnPopup mTwoBtnPopup;
    //메뉴 타이틀
    ImageView iv_title;
    //GNB 배경 이미지(피킹은 햄버거버튼 사용 안하기 때문)
    ImageView iv_gnb;
    ImageButton bt_drawer;
    ImageButton bt_print;
    //선택된 메뉴 postion
    int mSelectMenu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_base);

        mContext = BaseActivity.this;

        findViewById(R.id.bt_back).setOnClickListener(onClickListener);

        drawer = findViewById(R.id.drawer);
        drawer_layout = findViewById(R.id.drawer_layout);
        bt_drawer = findViewById(R.id.bt_drawer);
        bt_drawer.setOnClickListener(onClickListener);
        bt_print = findViewById(R.id.bt_print);
        bt_print.setOnClickListener(onClickListener);
        findViewById(R.id.bt_close).setOnClickListener(onClickListener);

        ArrayList<String> list = new ArrayList<>();
        list.add("외주품가입고");
        list.add("출하 등록");
        list.add("창고 이동");
        //list.add("자재입고확인(LOT)");
        list.add("자재입고확인(GROUP)");
        list.add("외주품출고확인");
        list.add("재고실사등록");
        list.add("완제품창고입출력조회");
        list.add("개발출출고처리");


        ListView listView = findViewById(R.id.list);
        mAdapter = new ListAdapter();
        mAdapter.setData(list);
        listView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        GlobalApplication application = (GlobalApplication) getApplicationContext();
        UserInfoModel.Items model = application.getUserInfoModel();

        TextView tv_name = findViewById(R.id.tv_name);
//        tv_name.setText(model.getDpt_name()+" "+model.getEmp_name());

        iv_title = findViewById(R.id.iv_title);
        iv_gnb = findViewById(R.id.iv_gnb);

        int menu = getIntent().getIntExtra("menu", 0);

        Bundle args = getIntent().getBundleExtra("args");

        mSelectMenu = menu - 2;

        switch (menu) {


            //외주품가입고
            case Define.MENU_OUT_IN: {
                CommonFragment fragment = new OutInFragment();
                replaceContent(fragment, Define.TAG_OUT_IN, R.id.fl_content);
                break;
            }

            //출하등록
            case Define.MENU_SHIP: {
                CommonFragment fragment = new ShipFragment();
                fragment.setArguments(args);
                replaceContent(fragment, Define.TAG_SHIP, R.id.fl_content);
                break;
            }

            //출하피킹
            case Define.MENU_SHIP_OK: {
                CommonFragment fragment = new ShipOkFragment();
                fragment.setArguments(args);
                replaceContent(fragment, Define.TAG_SHIP_OK, R.id.fl_content);
                break;
            }

            /*//자재입고확인(LOT)
            case Define.MENU_IN_LOT: {
                CommonFragment fragment = new InLotFragment();
                replaceContent(fragment, Define.TAG_IN_LOT, R.id.fl_content);
                break;
            }*/

            //창고이동
            case Define.MENU_WH_MOVE: {
                CommonFragment fragment = new WhMoveFragment();
                replaceContent(fragment, Define.TAG_WH_MOVE, R.id.fl_content);
                break;
            }

            //자재입고확인(GROUP)
            case Define.MENU_IN_GROUP: {
                CommonFragment fragment = new InGroupFragment();
                replaceContent(fragment, Define.TAG_IN_GROUP, R.id.fl_content);
                break;
            }

            //외주품출고확인
            case Define.MENU_OUT_OK: {
                CommonFragment fragment = new OutOkFragment();
                replaceContent(fragment, Define.TAG_OUT_OK, R.id.fl_content);
                break;
            }

            //재고실사등록
            case Define.MENU_INVENTORYS: {
                CommonFragment fragment = new InventorysFragment();
                replaceContent(fragment, Define.TAG_INVENTORYS, R.id.fl_content);
                break;
            }

            //완제품창고입출력조회
            case Define.MENU_WH_INOUT_SEARCH: {
                CommonFragment fragment = new InOutSearchFragment();
                replaceContent(fragment, Define.TAG_WH_INOUT_SEARCH, R.id.fl_content);
                break;
            }

            //개발품출고처리
            case Define.MENU_NEW_OUT_OK: {
                CommonFragment fragment = new NewOutFragment();
                replaceContent(fragment, Define.TAG_NEW_OUT_OK, R.id.fl_content);
                break;
            }



        }

        setTitleImage(menu);
    }

    private void setTitleImage(int menu) {
        //메뉴별 타이틀 이미지
        int image = 0;
        //자재불출과 제품출고에 있는 피킹 상단엔 좌측메뉴가 노출되지 않기 때문에 분기해야함
        int gnb = R.drawable.titilbar;
        int isDrawer = View.VISIBLE;
        int isLock = DrawerLayout.LOCK_MODE_UNLOCKED;

        //프린터 화면에서만 노출하면 되기 때문에 gone 처리 후 프린터 화면 진입 시 visible
        bt_print.setVisibility(View.GONE);

        switch (menu) {
//---------------------------------------------------------------------------------뱅

            //외주품가입고
            case Define.MENU_OUT_IN: {
                image = R.drawable.ssis_title1;
                break;
            }

            //출하등록
            case Define.MENU_SHIP: {
                image = R.drawable.ssis_title;
                break;
            }
            //출하피킹
            case Define.MENU_SHIP_OK: {
                image = R.drawable.ssis_title7;
                break;
            }

            /*//자재입고확인(LOT)
            case Define.MENU_IN_LOT: {
                image = R.drawable.ssis_title2;
                break;
            }*/

            //창고이동
            case Define.MENU_WH_MOVE: {
                image = R.drawable.ssis_title9;
                break;
            }

            //자재입고확인(GROUP)
            case Define.MENU_IN_GROUP: {
                image = R.drawable.ssis_title3;
                break;
            }

            //외주품출고확인
            case Define.MENU_OUT_OK: {
                image = R.drawable.ssis_title4;
                break;
            }

            //재고실사등록
            case Define.MENU_INVENTORYS: {
                image = R.drawable.ssis_title5;
                break;
            }

            //완제품창고입출력조회
            case Define.MENU_WH_INOUT_SEARCH: {
                image = R.drawable.ssis_title6;
                break;
            }




        }

        iv_title.setBackgroundResource(image);
        iv_gnb.setBackgroundResource(gnb);
        bt_drawer.setVisibility(isDrawer);
        drawer.setDrawerLockMode(isLock);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int view = v.getId();
            switch (view) {
                case R.id.bt_back:
                    mTwoBtnPopup = new TwoBtnPopup(BaseActivity.this, "작업 내용이 취소됩니다.\n 뒤로가시겠습니까?", R.drawable.popup_title_alert, new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            if (msg.what == 1) {
                                mTwoBtnPopup.hideDialog();
                                finish();
                            }
                        }
                    });
                    //backPressed();
                    break;
                case R.id.bt_drawer:
                    drawer.openDrawer(drawer_layout);
                    break;
                case R.id.bt_close:
                    drawer.closeDrawers();
                    break;
                case R.id.bt_print:
                    BusProvider.getInstance().post(0);
                    break;
            }
        }
    };

    @Override
    public void onBackPressed() {
        mTwoBtnPopup = new TwoBtnPopup(BaseActivity.this, "작업 내용이 취소됩니다.\n 뒤로가시겠습니까?", R.drawable.popup_title_alert, new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    mTwoBtnPopup.hideDialog();
                    finish();
                }
            }
        });
    }

    public void backPressed() {
        try {
            //사이드 메뉴가 열려있으면 닫아준다.
            if (drawer.isDrawerOpen(drawer_layout)) {
                drawer.closeDrawers();
                return;
            }

            FragmentManager mFragmentManager = ((FragmentActivity) mContext).getSupportFragmentManager();
            int count = mFragmentManager.getBackStackEntryCount();

            if (count >= 1) {
                mFragmentManager.popBackStack();
            } else {
                //finish();
            }
        } catch (Exception e) {
            //finish();
        }
    }

    class ListAdapter extends BaseAdapter {
        LayoutInflater mInflater;
        ArrayList<String> mList;

        public void setData(ArrayList<String> list) {
            mList = list;
        }

        public ListAdapter() {
            mInflater = LayoutInflater.from(mContext);
        }

        @Override
        public int getCount() {
            if (mList == null) {
                return 0;
            }

            return mList.size();
        }

        @Override
        public String getItem(int position) {
            return mList.get(position);
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
                v = mInflater.inflate(R.layout.cell_drawer, null);
                v.setTag(holder);

                holder.tv_menu = v.findViewById(R.id.tv_menu);
            } else {
                holder = (ViewHolder) v.getTag();
            }

            if (mSelectMenu == position) {
                holder.tv_menu.setSelected(true);
            } else {
                holder.tv_menu.setSelected(false);
            }

            final String menu = mList.get(position);
            holder.tv_menu.setText((position + 1) + ". " + menu);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mSelectMenu == position) {
                        drawer.closeDrawer(drawer_layout);
                        return;
                    }
                    mTwoBtnPopup = new TwoBtnPopup(BaseActivity.this, menu + " 메뉴로 이동하시겠습니까?", R.drawable.popup_title_alert, new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            if (msg.what == 1) {
                                //팝업 닫기
                                mTwoBtnPopup.hideDialog();
                                //좌측메뉴 닫기
                                drawer.closeDrawer(drawer_layout);
                                //선택한 메뉴 기억
                                mSelectMenu = position;
                                //0이 없고 입고등록이 삭제되어 +2 해줘야함
                                switch (position + 2) {

                                    //외주품가입고
                                    case Define.MENU_OUT_IN: {
                                        CommonFragment fragment = new OutInFragment();
                                        replaceContent(fragment, Define.TAG_OUT_IN, R.id.fl_content);
                                        break;
                                    }

                                    //출하등록
                                    case Define.MENU_SHIP: {
                                        CommonFragment fragment = new ShipFragment();
                                        replaceContent(fragment, Define.TAG_SHIP, R.id.fl_content);
                                        break;
                                    }

                                    /*//자재입고확인(LOT)
                                    case Define.MENU_IN_LOT: {
                                        CommonFragment fragment = new InLotFragment();
                                        replaceContent(fragment, Define.TAG_IN_LOT, R.id.fl_content);
                                        break;
                                    }*/

                                    //창고이동
                                    case Define.MENU_WH_MOVE: {
                                        CommonFragment fragment = new WhMoveFragment();
                                        replaceContent(fragment, Define.TAG_WH_MOVE, R.id.fl_content);
                                        break;
                                    }

                                    //자재입고확인(GROUP)
                                    case Define.MENU_IN_GROUP: {
                                        CommonFragment fragment = new InGroupFragment();
                                        replaceContent(fragment, Define.TAG_IN_GROUP, R.id.fl_content);
                                        break;
                                    }

                                    //외주품출고확인
                                    case Define.MENU_OUT_OK: {
                                        CommonFragment fragment = new OutOkFragment();
                                        replaceContent(fragment, Define.TAG_OUT_OK, R.id.fl_content);
                                        break;
                                    }

                                    //재고실사등록
                                    case Define.MENU_INVENTORYS: {
                                        CommonFragment fragment = new InventorysFragment();
                                        replaceContent(fragment, Define.TAG_INVENTORYS, R.id.fl_content);
                                        break;
                                    }

                                    //완제품창고입출력조회
                                    case Define.MENU_WH_INOUT_SEARCH: {
                                        CommonFragment fragment = new InOutSearchFragment();
                                        replaceContent(fragment, Define.TAG_WH_INOUT_SEARCH, R.id.fl_content);
                                        break;
                                    }

                                }
                                setTitleImage(position + 2);
                                mAdapter.notifyDataSetChanged();
                            }
                        }
                    });
                }
            });

            return v;
        }

        class ViewHolder {
            TextView tv_menu;
        }
    }
}
