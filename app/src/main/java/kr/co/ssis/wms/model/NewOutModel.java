package kr.co.ssis.wms.model;

import java.util.List;

public class NewOutModel extends ResultModel {

    List<NewOutModel.Item> items;

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public class Item extends ResultModel{
        //로트번호
        String lot_no;
        //품목코드
        String itm_code;
        //품목명
        String itm_name;
        //창고코드
        String wh_code;
        //창고명
        String wh_name;
        //수량
        int inv_qty;
        //변경수량
        int modify_inv_qty;

        public String getLot_no() {
            return lot_no;
        }

        public void setLot_no(String lot_no) {
            this.lot_no = lot_no;
        }

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

        public String getWh_code() {
            return wh_code;
        }

        public void setWh_code(String wh_code) {
            this.wh_code = wh_code;
        }

        public String getWh_name() {
            return wh_name;
        }

        public void setWh_name(String wh_name) {
            this.wh_name = wh_name;
        }

        public int getInv_qty() {
            return inv_qty;
        }

        public void setInv_qty(int inv_qty) {
            this.inv_qty = inv_qty;
        }

        public int getModify_inv_qty() {
            return modify_inv_qty;
        }

        public void setModify_inv_qty(int modify_inv_qty) {
            this.modify_inv_qty = modify_inv_qty;
        }
    }



}
