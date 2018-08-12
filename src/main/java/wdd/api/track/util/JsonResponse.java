package wdd.api.track.util;

public class JsonResponse {

    private boolean ok;
    private String description;
    private Object data;

    private JsonResponse() { }

    public static JsonResponse of(Object data) {
        JsonResponse response = new JsonResponse();
        response.setOk(true);
        response.setDescription("成功");
        response.setData(data);
        return response;
    }

    public static JsonResponse of(Object data, String description) {
        JsonResponse response = new JsonResponse();
        response.setOk(true);
        response.setDescription(description);
        response.setData(data);
        return response;
    }

    public static JsonResponse ofError(String description) {
        JsonResponse response = new JsonResponse();
        response.setOk(false);
        response.setDescription(description);
        response.setData(null);
        return response;
    }

    public boolean isOk() {
        return ok;
    }

    public void setOk(boolean ok) {
        this.ok = ok;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
