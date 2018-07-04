package online.omnia.statistics;

import com.google.gson.*;

import java.lang.reflect.Type;

/**
 * Created by lollipop on 28.01.2018.
 */
public class JsonCampaignAdUrlDeserializer implements JsonDeserializer<String>{
    @Override
    public String deserialize(JsonElement jsonElement, Type type,
                              JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonArray array = jsonElement.getAsJsonArray();
        String url = null;
        for (JsonElement element : array) {
            if (element.getAsJsonObject().get("target_url") != null) {
                url = element.getAsJsonObject().get("target_url").getAsString();
                break;
            }
        }
        return url;
    }
}
