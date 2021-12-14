package kr.co.ssis.wms.network;

import java.util.concurrent.TimeUnit;


import kr.co.siss.wms.BuildConfig;
import kr.co.ssis.wms.model.BoxlblListModel;
import kr.co.ssis.wms.model.CustomerInfoModel;
import kr.co.ssis.wms.model.DeliveryOrderModel;
import kr.co.ssis.wms.model.DisTypeModel;
import kr.co.ssis.wms.model.EmpModel;
import kr.co.ssis.wms.model.InGroupModel;
import kr.co.ssis.wms.model.InLotModel;
import kr.co.ssis.wms.model.InvenModel;
import kr.co.ssis.wms.model.InventoryModel;
import kr.co.ssis.wms.model.ItmListModel;
import kr.co.ssis.wms.model.LocationModel;
import kr.co.ssis.wms.model.LogQtySearchModel;
import kr.co.ssis.wms.model.LogSearchModel;
import kr.co.ssis.wms.model.LotItemsModel;
import kr.co.ssis.wms.model.MatMoveModel;
import kr.co.ssis.wms.model.MatOutDetailDel;
import kr.co.ssis.wms.model.MatOutDetailGet;
import kr.co.ssis.wms.model.MatOutDetailModel;
import kr.co.ssis.wms.model.MatOutListModel;
import kr.co.ssis.wms.model.MatOutSerialScanModel;
import kr.co.ssis.wms.model.MaterialLocAndLotModel;
import kr.co.ssis.wms.model.MaterialOutDetailModel;
import kr.co.ssis.wms.model.MaterialOutListModel;
import kr.co.ssis.wms.model.MorListModel;
import kr.co.ssis.wms.model.MorSerialScan;
import kr.co.ssis.wms.model.MoveAskModel;
import kr.co.ssis.wms.model.NewOutModel;
import kr.co.ssis.wms.model.OutInModel;
import kr.co.ssis.wms.model.OutOkModel;
import kr.co.ssis.wms.model.PalletSnanModel;
import kr.co.ssis.wms.model.ResultBoxModel;
import kr.co.ssis.wms.model.ResultModel;
import kr.co.ssis.wms.model.SerialLocationModel;
import kr.co.ssis.wms.model.SerialNumberModel;
import kr.co.ssis.wms.model.ShipCstModel;
import kr.co.ssis.wms.model.ShipListModel;
import kr.co.ssis.wms.model.ShipOkModel;
import kr.co.ssis.wms.model.StockDetailModel;
import kr.co.ssis.wms.model.StockModel;
import kr.co.ssis.wms.model.UserInfoModel;
import kr.co.ssis.wms.model.WarehouseModel;
import kr.co.ssis.wms.model.WhModel;
import kr.co.ssis.wms.model.WhMoveListModel;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiClientService {
    /**
     * 로그인
     * @param proc  프로시져
     * @param user_id 아이디
     * @param pass 비밀번호
     * @param version 앱버전
     * @return
     */
    @POST("R2JsonProc.asp")
    Call<UserInfoModel> postLogin(
            @Query("proc") String proc,
            @Query("param1") String user_id,
            @Query("param2") String pass,
            @Query("param3") String version
    );


    //---------------------------------------------성신산업 시작

    /**
     * 외주품가입고(시리얼스캔)
     * @param proc 프로시저
     * @param lot_no lot_no
     * */
    @POST("R2JsonProc.asp")
    Call<OutInModel> outinSerialScan(
            @Query("proc") String proc,
            @Query("param1") String lot_no

    );

    /**
     * 개발품출고(시리얼스캔)
     * @param proc 프로시저
     * @param lot_no lot_no
     * */
    @POST("R2JsonProc.asp")
    Call<NewOutModel> newoutSerialScan(
            @Query("proc") String proc,
            @Query("param1") String lot_no

    );

    /**
     * 창고이동 재고이동(시리얼스캔)
     * @param proc 프로시저
     * @param lot_no lot_no
     * */
    @POST("R2JsonProc.asp")
    Call<WhMoveListModel> WhMoveList(
            @Query("proc") String proc,
            @Query("param1") String lot_no

    );

    /**
     * 거래처리스트
     * @param proc 프로시저
     * @param date 조회일자
     * */
    @POST("R2JsonProc.asp")
    Call<ShipCstModel> shipCstList(
            @Query("proc") String proc,
            @Query("param1") String date
    );

    /**
     * 창고리스트
     * @param proc 프로시저
     * */
    @POST("R2JsonProc.asp")
    Call<WhModel> WhList(
            @Query("proc") String proc
    );

    /**
     * 실사종류리스트
     * @param proc 프로시저
     * @param1 tv_qty 수량
     * @param2 et_qty 수량
     * */
    @POST("R2JsonProc.asp")
    Call<DisTypeModel> DisTypeList(
            @Query("proc") String proc,
            @Query("param1") String tv_qty,
            @Query("param2") String et_qty
    );

    /**
     * 품목종류 리스트
     * @param proc 프로시저
     * @param code 아이템코드
     * */
    @POST("R2JsonProc.asp")
    Call<ItmListModel> ItmList(
            @Query("proc") String proc,
            @Query("param1") String code
    );

    /**
     * 창고종류 리스트
     * @param proc 프로시저
     * @param code 창고코드, 명
     * */
    @POST("R2JsonProc.asp")
    Call<WhModel> WhList(
            @Query("proc") String proc,
            @Query("param1") String code
    );

    /**
     * 출하등록 품목종류 리스트
     * @param proc 프로시저
     * @param date 조회일자
     * @param cst_code 거래처코드
     * @param code 아이템코드
     * */
    @POST("R2JsonProc.asp")
    Call<ItmListModel> ship_itm_list(
            @Query("proc") String proc,
            @Query("param1") String date,
            @Query("param2") String cst_code,
            @Query("param3") String code
    );

    /**
     * 리스트조회
     * @param proc 프로시저
     * @param date 조회일자
     * @param cst_code 거래처
     * @param itm_code 아이템코드
     * */
    @POST("R2JsonProc.asp")
    Call<ShipListModel> shipListSearch(
            @Query("proc") String proc,
            @Query("param1") String date,
            @Query("param2") String cst_code,
            @Query("param3") String itm_code
    );

    /**
     * 출하피킹(시리얼스캔)
     * @param proc 프로시저
     * @param lot_no lot_no
     * @param itm_code 아이템코드
     * @param wh_code 창고코드
     * */
    @POST("R2JsonProc.asp")
    Call<ShipOkModel> shipOkSerialScan(
            @Query("proc") String proc,
            @Query("param1") String lot_no,
            @Query("param2") String itm_code,
            @Query("param3") String wh_code
    );

    /**
     * 가입고저장, LOT가입고 데이터 저장
     * */
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @POST("R2JsonProc_tin_save.asp")
    Call<ResultModel> postOutInSave(
            @Body RequestBody body
    );

    /**
     * 창고이동 저장
     * */
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @POST("R2JsonProc_move_save.asp")
    Call<ResultModel> postWhMoveSave(
            @Body RequestBody body
    );

    /**
     * 출하등록저장
     * */
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @POST("R2JsonProc_ship_save.asp")
    Call<ResultModel> postShipSave(
            @Body RequestBody body
    );

    /**
     * 자재입고(LOT)
     * @param proc 프로시저
     * @param lot_no lot_no
     * */
    @POST("R2JsonProc.asp")
    Call<InLotModel> InLotSerialScan(
            @Query("proc") String proc,
            @Query("param1") String lot_no

    );

    /**
     * 자재입고(GROUP)
     * @param proc 프로시저
     * @param lot_no lot_no
     * */
    @POST("R2JsonProc.asp")
    Call<InGroupModel> InGroupSerialScan(
            @Query("proc") String proc,
            @Query("param1") String lot_no

    );

    /**
     * 외주품출고확인
     * @param proc 프로시저
     * @param lot_no lot_no
     * */
    @POST("R2JsonProc.asp")
    Call<OutOkModel> OutOkSerialScan(
            @Query("proc") String proc,
            @Query("param1") String lot_no

    );

    /**
     * 재고실사등록
     * @param proc 프로시저
     * @param lot_no lot_no
     * */
    @POST("R2JsonProc.asp")
    Call<InvenModel> InvenSerialScan(
            @Query("proc") String proc,
            @Query("param1") String lot_no

    );

    /**
     * 실사처리완료
     * */
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @POST("R2JsonProc_dis_save.asp")
    Call<ResultModel> postDisSave(
            @Body RequestBody body
    );



    /**
     * 이력조회(수량)
     * @param proc 프로시저
     * @param beg_date 시작일자
     * @param end_date 종료일자
     * @param itm_code 품목코드
     * */
    @POST("R2JsonProc.asp")
    Call<LogQtySearchModel> LogSearchQtyList(
            @Query("proc") String proc,
            @Query("param1") String beg_date,
            @Query("param2") String end_date,
            @Query("param3") String itm_code
    );

    /**
     * 이력조회(이월,입출)
     * @param proc 프로시저
     * @param beg_date 시작일자
     * @param end_date 종료일자
     * @param itm_code 품목코드
     * */
    @POST("R2JsonProc.asp")
    Call<LogSearchModel> LogSearchList(
            @Query("proc") String proc,
            @Query("param1") String beg_date,
            @Query("param2") String end_date,
            @Query("param3") String itm_code
    );

    /**
     * 개발품출고처리 타계정등록관리 저장
     * */
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @POST("R2JsonProc_dis_ship_save.asp")
    Call<ResultModel> postNewOutSave(
            @Body RequestBody body
    );






    //---------------------------------------------------성신산업 끝

    //---------------------------------------------뱅 시작
    /**
     * 창고 리스트
     * @param proc  프로시저
     * @param gubun 회원/대리점 구분(A : 대리점, C : 회원)
     * @param s_date 조회일자
     * @param out_no 주문자재출고No
     * @param slip_type 전표타입
     * @return
     */
    @POST("R2JsonProc.asp")
    Call<MorListModel> morlist(
            @Query("proc") String proc,
            @Query("param1") String gubun,
            @Query("param2") String s_date,
            @Query("param3") String out_no,
            @Query("param4") String slip_type
    );

    /**
     * 주문자재출고 상세 회원
     * @param proc 프로시저
     * @param gubun 회원/대리점 구분(A : 대리점, C : 회원)
     * @param slip_no 주문번호
     * @param slip_type 전표타입
     * */
    @POST("R2JsonProc.asp")
    Call<MorListModel> mordetail(
            @Query("proc") String proc,
            @Query("param1") String gubun,
            @Query("param2") String slip_no,
            @Query("param3") String slip_type
    );


    /**
     * 창고리스트
     * @param proc 프로시저
     * @param code 창고코드
     * */
    @POST("R2JsonProc.asp")
    Call<WarehouseModel> morWarehouse(
            @Query("proc") String proc,
            @Query("param1") String code

    );

    /**
     * 스캔내역삭제
     * @param proc 프로시저
     * @param mac 맥주소
     * @param mat_cd 요청코드
     * */
    @POST("R2JsonProc.asp")
    Call<MatOutDetailDel> ScanDel(
            @Query("proc") String proc,
            @Query("param1") String mac,
            @Query("param2") String mat_cd

    );

    /**
     * 재고실사(박스)
     * @param proc 프로시저
     * @param code 시리얼번호
     * */
    @POST("R2JsonProc.asp")
    Call<InvenModel> inventoryBox(
            @Query("proc") String proc,
            @Query("param1") String code

    );



    /**
     * 작업자리스트
     * @param proc 프로시저
     * @param code 명칭
     * */
    @POST("R2JsonProc.asp")
    Call<EmpModel> empList(
            @Query("proc") String proc,
            @Query("param1") String code


    );

    /**
     * 재고조사 리스트 조회
     * @param proc 프로시저
     * @param date 조회일자
     * */
    @POST("R2JsonProc.asp")
    Call<StockModel> stklist(
            @Query("proc") String proc,
            @Query("param1") String date
    );

    /**
     * 재고조사 시리얼 스캔
     * @param proc 프로시저
     * @param serial 시리얼번호
     * @param date 일자
     * @param wh_code 창고
     * @param no 순번
     * */
    @POST("R2JsonProc.asp")
    Call<StockDetailModel> stk_serial_list(
            @Query("proc") String proc,
            @Query("param1") String serial,
            @Query("param2") String date,
            @Query("param3") String wh_code,
            @Query("param4") String no
    );

    /**
     * 이동요청 시리얼 스캔
     * @param proc 프로시저
     * @param date 일자
     * @param itm_code 품목코드
     * @param wh_in_code 입고처
     * @param wh_out_code 출고처
     * */
    @POST("R2JsonProc.asp")
    Call<MoveAskModel> moveSerialScan(
            @Query("proc") String proc,
            @Query("param1") String date,
            @Query("param2") String itm_code,
            @Query("param3") String wh_in_code,
            @Query("param4") String wh_out_code
    );

    /**
     * 창고이동 리스트 조회
     * @param proc 프로시저
     * @param date 조회일자
     * */
    @POST("R2JsonProc.asp")
    Call<MatOutListModel> matlist(
            @Query("proc") String proc,
            @Query("param1") String date
    );

    /**
     * 시리얼위치조회 바코드 스캔
     * @param proc 프로시저
     * @param serial 시리얼번호
     * */
    @POST("R2JsonProc.asp")
    Call<SerialLocationModel> serialLocatonScan(
            @Query("proc") String proc,
            @Query("param1") String serial
    );

    /**
     * 창고이동 디테일 조회
     * @param proc 프로시저
     * @param mac 맥주소
     * @param no 이동요청번호
     * */
    @POST("R2JsonProc.asp")
    Call<MatOutDetailModel> matDetailList(
            @Query("proc") String proc,
            @Query("param1") String mac,
            @Query("param2") String no
    );

    /**
     * 창고이동 new 시리얼 스캔
     * @param proc 프로시저
     * @param serial 시리얼번호
     * @param req_code 요청코드
     * */
    @POST("R2JsonProc.asp")
    Call<MatOutSerialScanModel> matSerialScan(
            @Query("proc") String proc,
            @Query("param1") String serial,
            @Query("param2") String req_code
    );

    /**
     * 박스라벨 시리얼 스캔
     * @param proc 프로시저
     * @param serial 시리얼번호
     * */
    @POST("R2JsonProc.asp")
    Call<BoxlblListModel> boxSerialScan(
            @Query("proc") String proc,
            @Query("param1") String serial
    );

    /**
     * 스캔내역 조회(작업중인 냬역)
     * @param proc 프로시저
     * @param mac 맥주소
     * @param no 이동요청번호
     * @param itm 아이템코드
     * */
    @POST("R2JsonProc.asp")
    Call<MatOutDetailGet> matDetailGet(
            @Query("proc") String proc,
            @Query("param1") String mac,
            @Query("param2") String no,
            @Query("param3") String itm

    );

    /**
     * 창고이동 시리얼 스캔
     * @param proc 프로시저
     * @param code 코드
     * */
    @POST("R2JsonProc.asp")
    Call<MatMoveModel> movescan(
            @Query("proc") String proc,
            @Query("param1") String code

    );

    /**
     * 창고 리스트
     * @param proc
     * @param code 창고코드
     * @return
     */
    @POST("R2JsonProc.asp")
    Call<WarehouseModel> posthouse(
            @Query("proc") String proc,
            @Query("param1") String code
    );

    /**
     *재고실사저장
     * */
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @POST("R2JsonProc_mat_mod_save.asp")
    Call<InvenModel> matModSave(
            @Body RequestBody body
    );

    /**
     *재고실사저장
     * */
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @POST("R2JsonProc_stk_scan_save.asp")
    Call<StockDetailModel> stockSave(
            @Body RequestBody body
    );

    /**
     *이동처리요청
     * */
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @POST("R2JsonProc_mreq_save.asp")
    Call<MoveAskModel> MoveSave(
            @Body RequestBody body
    );

    /**
     *창고이동처리
     * */
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @POST("R2JsonProc_mat_move_save.asp")
    Call<ResultModel> postSendMatMove(
            @Body RequestBody body
    );

    /**
     *주문자재출고 저장
     * */
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @POST("R2JsonProc_dis_mor_save.asp")
    Call<ResultModel> postSendWareSave(
            @Body RequestBody body
    );

    /**
     *스캔내역저장
     * */
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @POST("R2JsonProc_mat_out_scan_save.asp")
    Call<ResultModel> postOutSave(
            @Body RequestBody body
    );

    /**
     * 이동처리완료
     * */
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @POST("R2JsonProc_mat_out_save.asp")
    Call<ResultModel> postMatOutSave(
            @Body RequestBody body
    );

    /**
     * 실사처리완료
     * */
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @POST("R2JsonProc_box_lbl_save.asp")
    Call<ResultBoxModel> postBoxSave(
            @Body RequestBody body
    );

    /**
     *주문자재출고 출고승인(주문)
     * */
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @POST("R2JsonProc_dis_mor_cmf_order.asp")
    Call<ResultModel> postSendWareAgree(
            @Body RequestBody body
    );

    /**
     *주문자재출고 출고승인(AS)
     * */
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @POST("R2JsonProc_dis_mor_cmf_as.asp")
    Call<ResultModel> postSendWareAS(
            @Body RequestBody body
    );

    /**
     *주문자재출고 출고승인(대리점)
     * */
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @POST("R2JsonProc_dis_mor_cmf_order.asp")
    Call<ResultModel> postSendWareStore(
            @Body RequestBody body
    );

    /**
     * 자재출고피킹 시리얼번호 스캔
     * @param proc  프로시저
     * @param serial_no 시리얼번호
     * @param wh_code 입고처
     * @param corp_code 사업장
     * @param out_date 출고일자
     * @param out_no 출고순번
     * @return
     */
    @POST("R2JsonProc.asp")
    Call<MorSerialScan> morSerialScan(
            @Query("proc") String proc,
            @Query("param1") String serial_no,
            @Query("param2") String wh_code,
            @Query("param3") String corp_code,
            @Query("param4") String out_date,
            @Query("param5") String out_no
    );

    //---------------------------------------------뱅 끝

    /**
     * 창고 리스트
     * @param proc
     * @param type 창고타입(M : 원자재, P : 완제품)
     * @return
     */
    @POST("R2JsonProc.asp")
    Call<WarehouseModel> postWarehouse(
            @Query("proc") String proc,
            @Query("param1") String type
    );

    /**
     * 로케이션 리스트
     * @param proc
     * @param code 창고코드
     * @return
     */
    @POST("R2JsonProc.asp")
    Call<LocationModel> postLocation(
            @Query("proc") String proc,
            @Query("param1") String code
    );

    /**
     * 로케이션 리스트
     * @param proc
     * @param code 창고코드
     * @param type 창고타입(M : 원자재, P : 완제품)
     * @return
     */
    @POST("R2JsonProc.asp")
    Call<LocationModel> postScanLocation(
            @Query("proc") String proc,
            @Query("param1") String code,
            @Query("param2") String type
    );

    /**
     * 창고와 로케이션 정보 조회
     * @param proc 프로시져
     * @param code 로케이션 코드
     * @return
     */
    @POST("R2JsonProc.asp")
    Call<LocationModel> postWarehouseLocation(
            @Query("proc") String proc,
            @Query("param1") String code
    );

    /**
     * 로트 재고정보
     * @param proc 프로시져
     * @param code 로케이션코드
     * @param lot 로트번호
     * @return
     */
    @POST("R2JsonProc.asp")
    Call<LotItemsModel> postLotItems(
            @Query("proc") String proc,
            @Query("param1") String code,
            @Query("param2") String lot
    );

    //로케이션 이동
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @POST("R2JsonProc_mat_etc_save.asp")
    Call<ResultModel> postSendLocation(
            @Body RequestBody body
    );

    /**
     * 팔레트 바코드 스캔(시리얼번호)
     * @param proc 프로시져
     * @param barcode 스캔 바코드
     * @return
     */
    @POST("R2JsonProc.asp")
    Call<PalletSnanModel> postScanPallet(
            @Query("proc") String proc,
            @Query("param1") String barcode
    );

    //제품입고 저장
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @POST("R2JsonProc_wms_sin_save.asp")
    Call<ResultModel> postSendProductionIn(
            @Body RequestBody body
    );

    /**
     * 자재불출 지시조회
     * @param proc 프로시져
     * @param param1 불출지시 일자
     * @param param2 불출창고 코드
     * @return
     */
    @POST("R2JsonProc.asp")
    Call<MaterialOutListModel> postOutOrderList(
            @Query("proc") String proc,
            @Query("param1") String param1,
            @Query("param2") String param2
    );

    /**
     * 자재불출 지시 상세
     * @param proc 프로시져
     * @param param1 자재불출번호
     * @return
     */
    @POST("R2JsonProc.asp")
    Call<MaterialOutDetailModel> postOutOrderDetail(
            @Query("proc") String proc,
            @Query("param1") String param1
    );

    /**
     * 자재불출 로케이션, 시스템로트 스캔
     * @param proc 프로시져
     * @param param1 불출창고코드
     * @param param2 로케이션코드
     * @param param3 시스템로트번호
     * @return
     */
    @POST("R2JsonProc.asp")
    Call<MaterialLocAndLotModel> postOutLocAndLot(
            @Query("proc") String proc,
            @Query("param1") String param1,
            @Query("param2") String param2,
            @Query("param3") String param3
    );

    /**
     * 자재불출 지시 상세
     */
    //제품출고 저장
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @POST("R2JsonProc_wms_out_save.asp")
    Call<ResultModel> postMaterialSend(
            @Body RequestBody body
    );

    /**
     * 출고지시서 상세
     * @param proc 프로시져
     * @param param1 출고지시번호
     * @return
     */
    @POST("R2JsonProc.asp")
    Call<CustomerInfoModel> postShipReqList(
            @Query("proc") String proc,
            @Query("param1") String param1,
            @Query("param2") String param2
    );

    /**
     * 출고지시서 상세
     * @param proc 프로시져
     * @param param1 출고지시번호
     * @return
     */
    @POST("R2JsonProc.asp")
    Call<DeliveryOrderModel> postShipReqDetail(
            @Query("proc") String proc,
            @Query("param1") String param1
    );

    //제품출고 저장
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @POST("R2JsonProc_wms_ship_save.asp")
    Call<ResultModel> postSendProductionOut(
            @Body RequestBody body
    );

    /**
     * 파레트관리 분할 전표생성
     * @param proc 프로시져
     * @param param1 시리얼번호
     * @param param2 품목코드
     * @param param3 창고코드
     * @param param4 로케이션코드
     * @param param5 원수량(재고수량)
     * @param param6 분할수량
     * @param param7 로그인ID
     * @return
     */
    @POST("R2JsonProc.asp")
    Call<SerialNumberModel> postMakeBunhalJunphyo(
            @Query("proc") String proc,
            @Query("param1") String param1,
            @Query("param2") String param2,
            @Query("param3") String param3,
            @Query("param4") String param4,
            @Query("param5") String param5,
            @Query("param6") String param6,
            @Query("param7") String param7
    );

    //파레트관리 병합 전표 생성
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @POST("R2JsonProc_plt_mrg_save.asp")
    Call<SerialNumberModel> postMakeMergeJunphyo(
            @Body RequestBody body
    );

    /**
     * 재고실사 상세
     * @param proc 프로시져
     * @param param1 재고실사번호
     * @return
     */
    @POST("R2JsonProc.asp")
    Call<InventoryModel> postInventoryScan(
            @Query("proc") String proc,
            @Query("param1") String param1
    );

    //재고실사 전표 생성
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @POST("R2JsonProc_wms_mod_save.asp")
    Call<ResultModel> postSendInventory(
            @Body RequestBody body
    );

    //R2JsonProc_plt_mrg_save.asp

    //로그 찍기
    //태그 OkHttp 입력(adb logcat OkHttp:D *:S)
    // HttpLoggingInterceptor.Level.BODY  모든 바디 로그 온
    // HttpLoggingInterceptor.Level.NONE  로그 오프
    public static final HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.BODY);

    //타임아웃 1분
    public static final OkHttpClient.Builder builder = new OkHttpClient.Builder()
            .connectTimeout(1, TimeUnit.MINUTES)
            .readTimeout(1, TimeUnit.MINUTES)
            .writeTimeout(1, TimeUnit.MINUTES)
            .addInterceptor(interceptor);

    //Gson으로 리턴
    public static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BuildConfig.API_SERVER)
            .addConverterFactory(GsonConverterFactory.create())
            .client(builder.build())
            .build();

    //String으로 리턴
    public static final Retrofit retrofitString = new Retrofit.Builder()
            .baseUrl(BuildConfig.API_SERVER)
            .addConverterFactory(new ToStringConverterFactory())
            .client(builder.build())
            .build();
}
