package id.co.hilmi.bemobile.constant.response;

public enum GenericResponseKey {
    SUCCESS,
    INTERNAL_SERVER_ERROR,
    GATEWAY_TIMEOUT_ERROR,
    UNKNOWN_ERROR,
    GENERAL_ERROR,
    IN_PROGRESS;

    private GenericResponseKey() {
    }

    public String getResponseKey() {
        return this.toString();
    }
}
