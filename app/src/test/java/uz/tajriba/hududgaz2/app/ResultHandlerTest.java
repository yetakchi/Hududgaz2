package uz.tajriba.hududgaz2.app;

import static org.junit.Assert.assertNotNull;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class ResultHandlerTest {

    private final ResultHandler resultHandler = new ResultHandler();

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void onReceiveResult() throws JSONException {
        JSONObject jsonObject = new JSONObject("{\"code\": \"1561\"}");
        System.out.println(jsonObject);
        assertNotNull(jsonObject);

        resultHandler.onReceiveResult("{\"code\": \"1561\"}");
    }
}