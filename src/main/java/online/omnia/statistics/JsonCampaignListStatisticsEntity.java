package online.omnia.statistics;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lollipop on 28.01.2018.
 */
public class JsonCampaignListStatisticsEntity implements JsonDeserializer<List<JsonCampaignStatisticsEntity>>{
    @Override
    public List<JsonCampaignStatisticsEntity> deserialize(JsonElement jsonElement,
                            Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonArray array = jsonElement.getAsJsonObject().get("content").getAsJsonArray();
        List<JsonCampaignStatisticsEntity> jsonCampaignStatisticsEntities = new ArrayList<>();
        JsonCampaignStatisticsEntity jsonCampaignStatisticsEntity;
        for (JsonElement element : array) {
            jsonCampaignStatisticsEntity = new JsonCampaignStatisticsEntity();
            jsonCampaignStatisticsEntity.setImpressions(element.getAsJsonObject().get("impressions").getAsInt());
            jsonCampaignStatisticsEntity.setClicks(element.getAsJsonObject().get("clicks").getAsInt());
            jsonCampaignStatisticsEntity.setCampaignId(element.getAsJsonObject().get("campaignId").getAsString());
            jsonCampaignStatisticsEntity.setCampaignName(element.getAsJsonObject().get("campaignName").getAsString());
            jsonCampaignStatisticsEntity.setConversions(element.getAsJsonObject().get("conversions").getAsInt());
            jsonCampaignStatisticsEntity.setCost(element.getAsJsonObject().get("cost").getAsDouble());
            jsonCampaignStatisticsEntity.setCPM(element.getAsJsonObject().get("CPM").getAsDouble());
            jsonCampaignStatisticsEntity.setCTR(element.getAsJsonObject().get("CTR").getAsDouble());
            jsonCampaignStatisticsEntities.add(jsonCampaignStatisticsEntity);
        }

        return jsonCampaignStatisticsEntities;
    }
}
