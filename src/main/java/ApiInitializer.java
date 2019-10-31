import static spark.Spark.after;

public class ApiInitializer {

    public void run() {
        setJsonContentTypeHeaderToResponse();
    }

    private void setJsonContentTypeHeaderToResponse() {
        after((req, res) -> res.type("application/json;charset=UTF-8"));
    }
}
