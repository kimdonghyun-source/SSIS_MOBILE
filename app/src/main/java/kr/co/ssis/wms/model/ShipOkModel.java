package kr.co.ssis.wms.model;

import java.util.List;

public class ShipOkModel extends ResultModel {
    List<ShipOkModel.Item> items;

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public class Item extends ResultModel{
        //품목코드
        String itm_code;
        //품목명
        String itm_name;
        //시리얼번호
        String lot_no;
        //창고코드
        String wh_code;
        //재고수량
        int wrk_qty;
        //순번(내가만든순번)
        int no;


        List<ShipOkModel.Item> items;

        public String getItm_code() {
            return itm_code;
        }

        public void setItm_code(String itm_code) {
            this.itm_code = itm_code;
        }

        public String getItm_name() {
            return itm_name;
        }

        public void setItm_name(String itm_name) {
            this.itm_name = itm_name;
        }

        public String getLot_no() {
            return lot_no;
        }

        public void setLot_no(String lot_no) {
            this.lot_no = lot_no;
        }

        public String getWh_code() {
            return wh_code;
        }

        public void setWh_code(String wh_code) {
            this.wh_code = wh_code;
        }

        public int getWrk_qty() {
            return wrk_qty;
        }

        public void setWrk_qty(int wrk_qty) {
            this.wrk_qty = wrk_qty;
        }

        public List<Item> getItems() {
            return items;
        }

        public void setItems(List<Item> items) {
            this.items = items;
        }

        public int getNo() {
            return no;
        }

        public void setNo(int no) {
            this.no = no;
        }
    }
}
