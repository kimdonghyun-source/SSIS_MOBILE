package kr.co.ssis.wms.common;

public class Define {


    //성신산업

    //외주품가입고
    public static final int MENU_OUT_IN = 2;

    //출하등록
    public static final int MENU_SHIP = 3;
    //출하피킹
    public static final int MENU_SHIP_OK = 31;

    //창고이동
    public static final int MENU_WH_MOVE = 4;

    /*//자재입고확인(LOT)
    public static final int MENU_IN_LOT = 4;*/

    //자재입고확인(GROUP)
    public static final int MENU_IN_GROUP = 5;

    //외주품출고확인
    public static final int MENU_OUT_OK = 6;

    //재고실사등록
    public static final int MENU_INVENTORYS = 7;

    //완제품창고입출력조회
    public static final int MENU_WH_INOUT_SEARCH = 8;

    //개발품출고처리
    public static final int MENU_NEW_OUT_OK = 9;



    //---------------------------------------------------------

    //외주품가입고
    public static final String TAG_OUT_IN = "outin";

    //출하등록
    public static final String TAG_SHIP = "ship";
    //출하피킹
    public static final String TAG_SHIP_OK = "ship_ok";

    //자재입고확인(LOT)
    public static final String TAG_IN_LOT = "lot";

    //자재입고확인(GROUP)
    public static final String TAG_IN_GROUP = "group";

    //외주품출고확인
    public static final String TAG_OUT_OK = "outok";

    //재고실사등록
    public static final String TAG_INVENTORYS = "inventorys";

    //완제품창고입출력조회
    public static final String TAG_WH_INOUT_SEARCH = "whsearch";

    //창고이동
    public static final String TAG_WH_MOVE = "whmove";

    //개발품출고처리
    public static final String TAG_NEW_OUT_OK = "newoutok";



    //--------------------------------------------------------------------------------

    //주문자재출고(뱅)
    public static final int MENU_PRODUCTION_IN = 12;
    //주문자재출고 상세(뱅)
    public static final int MENU_PRODUCTION_DETAIL = 20;
    //주문자재출고 피킹(뱅)
    public static final int MENU_PRODUCTION_OUT = 21;

    ////창고이동(뱅) 기존꺼
    //public static final int MENU_HOUSE_MOVE = 3;

    //이동요청
    public static final int MENU_MOVE_ASK = 13;

    //창고이동(뱅) 새로추가
    public static final int MENU_HOUSE_MOVE_NEW = 14;
    //창고이동(뱅) 상세조회
    public static final int MENU_HOUSE_MOVE_DATAIL = 41;
    //창고이동(뱅) 스캔리스트 조회
    public static final int MENU_HOUSE_MOVE_SCAN_DATAIL = 42;

    //박스라벨패킹(뱅)
    public static final int MENU_BOXLBL = 15;

    //재고실사(뱅)
    public static final int MENU_INVENTORY = 16;

    //재고조사(뱅)
    public static final int MENU_STOCK = 17;
    //재고조사(뱅) 스캔리스트 조회
    public static final int MENU_STOCK_DETAIL = 71;

    //시리얼위치조회
    public static final int MENU_SERIAL_LOCATION = 18;

   //--------------------------------------------------------------------------------
    //주문자재출고(뱅)
    public static final String TAG_PRODUCTION_IN = "production";
    //주문자재출고 상세(뱅)
    public static final String TAG_PRODUCTION_DETAIL = "production_detail";
    //주문자재출고 피킹(뱅)
    public static final String TAG_PRODUCTION_OUT = "production_out";
    //이동요청
    public static final String TAG_MOVE_ASK = "move_ask";
    //창고이동 (뱅) 기존꺼
    //public static final String TAG_HOUSE_MOVE = "house_move";
    //창고이동 (뱅) 새로추가
    public static final String TAG_HOUSE_MOVE_NEW = "house_move_new";
    //창고이동(뱅) 디테일
    public static final String TAG_HOUSE_MOVE_DETAIL = "house_move_detail";
    //창고이동(뱅) 스캔디테일
    public static final String TAG_HOUSE_MOVE_SCAN_DETAIL = "house_move_detail";
    //박스라벨패킹
    public static final String TAG_BOXLBL = "boxlbl";
    //재고실사
    public static final String TAG_INVENTORY = "invertory";
    //재고조사
    public static final String TAG_STOCK = "stock";
    //재고조사(뱅) 디테일
    public static final String TAG_STOCK_DETAIL = "stock_detail";
    //시리얼위치조회
    public static final String TAG_SERIAL_LOCATION = "serial_location";


    //--------------------------------------------------------------------------------

}
