package converter.ecomXslToJson.logic;

import com.fasterxml.jackson.databind.JsonNode;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class GisAddressSpecifier {
    private WebClient wc = getWebClientConnect();

    private final String url = "https://address.pochta.ru/validate/api/v7_1";
    private final String authCode = "c73e3b60-2189-4014-8419-1632e0e00119";

    /**
     * Метод создаёт соединение WebClient.
     *
     * @return созданное WebClient соединение.
     */
    private WebClient getWebClientConnect() {
        return WebClient.builder()
                .baseUrl(url).defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("AuthCode", authCode).build();
    }

    /**
     * Формирует JSON объект для запроса.
     *
     * @param find искомая строка.
     * @return JSON node объект.
     */
    private JsonNode getJsonNode(String find) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("version", "1");
        jsonObject.put("outLang", "R");
        jsonObject.put("addr", new JSONArray().put(new JSONObject().put("val", find)));
        return wc.post().body(Mono.just(jsonObject.toString()), String.class)
                .exchange().block().bodyToMono(JsonNode.class).block();
    }

    /**
     * Метод получения идентификатора guid по искомой строке.
     * Во всех случаях кроме получения состояния 301, 302 и 303 выдаёт нот фаунд, в них выдает гуид.
     *
     * @param find искомая строка.
     * @return строчное представление идентификатора guid.
     */
    public String getAddressGuid(String find) {
        JsonNode jsonNode = getJsonNode(find);
        JsonNode jsonState = jsonNode.findValue("state");
        switch (jsonState.textValue()) {
            case "301":
            case "302":
            case "303":
                return jsonNode.findValue("guid").textValue();
            default:
                return "Not found";
        }
    }
}