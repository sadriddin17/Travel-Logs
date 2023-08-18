package uz.hayot.odoreport.helper;

record ErrorDetail(int status, String message, uz.hayot.odoreport.helper.ErrorDetail.Field... fields) {

    record Field(String key, String value) {
    }
}
