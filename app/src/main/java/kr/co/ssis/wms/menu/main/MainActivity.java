package kr.co.ssis.wms.menu.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;


import kr.co.ssis.wms.common.Define;
import kr.co.ssis.wms.custom.CommonCompatActivity;
import kr.co.ssis.wms.menu.login.LoginActivity;
import kr.co.ssis.wms.menu.popup.TwoBtnPopup;
import kr.co.siss.wms.R;

public class MainActivity extends CommonCompatActivity {
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this;

        setContentView(R.layout.act_main);



        findViewById(R.id.bt_menu_1).setOnClickListener(onClickListener); //외주품가입고
        findViewById(R.id.bt_menu_2).setOnClickListener(onClickListener); //출하등록
        findViewById(R.id.bt_menu_3).setOnClickListener(onClickListener); //자재입고확인(LOT)
        findViewById(R.id.bt_menu_4).setOnClickListener(onClickListener); //자재입고확인(GROUP)
        findViewById(R.id.bt_menu_5).setOnClickListener(onClickListener); //외주품출고확인
        findViewById(R.id.bt_menu_6).setOnClickListener(onClickListener); //재고실사등록
        findViewById(R.id.bt_menu_7).setOnClickListener(onClickListener); //완제품창고입출력조회
        findViewById(R.id.bt_menu_9).setOnClickListener(onClickListener); //창고이동
        findViewById(R.id.bt_menu_10).setOnClickListener(onClickListener); //개발품출고처리
        findViewById(R.id.bt_logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TwoBtnPopup(MainActivity.this, "로그아웃 하시겠습니까?", R.drawable.popup_title_alert, new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        if (msg.what == 1) {
                            goLogin();
                        }
                    }
                });
            }
        });
    }

    private void goLogin(){
        Intent i = new Intent(mContext,LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int view = v.getId();
            Intent intent = new Intent(mContext, BaseActivity.class);
            switch (view){



                //외주품가입고(성신)
                case R.id.bt_menu_1:
                    intent.putExtra("menu", Define.MENU_OUT_IN);
                    break;

                //출하등록(성신)
                case R.id.bt_menu_2:
                    intent.putExtra("menu", Define.MENU_SHIP);
                    break;

                /*//자재입고확인(LOT)(성신)
                case R.id.bt_menu_3:
                    intent.putExtra("menu", Define.MENU_IN_LOT);
                    break;*/

                //자재입고확인(GROUP)(성신)
               case R.id.bt_menu_4:
                    intent.putExtra("menu", Define.MENU_IN_GROUP);
                    break;

                //외주품출고확인(성신)
                case R.id.bt_menu_5:
                    intent.putExtra("menu", Define.MENU_OUT_OK);
                    break;

                //재고실사등록(성신)
                case R.id.bt_menu_6:
                    intent.putExtra("menu", Define.MENU_INVENTORYS);
                    break;

                //완제품창고입출력조회(성신)
                case R.id.bt_menu_7:
                    intent.putExtra("menu", Define.MENU_WH_INOUT_SEARCH);
                    break;

                //창고이동(성신)
                case R.id.bt_menu_9:
                    intent.putExtra("menu", Define.MENU_WH_MOVE);
                    break;

                //개발품출고처리
                case R.id.bt_menu_10:
                    intent.putExtra("menu", Define.MENU_NEW_OUT_OK);
                    break;

            }
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        }
    };
}
