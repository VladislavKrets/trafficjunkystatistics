package online.omnia.statistics;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by lollipop on 28.01.2018.
 */
public class Main {
    public static int days;
    public static long deltaTime = 24L * 60 * 60 * 1000;

    public static void main(String[] args) {
        if (args.length != 1) return;
        if (!args[0].matches("\\d+")) return;
        if (Integer.parseInt(args[0]) == 0) {
            days = 0;
            deltaTime = 0;
        }
        days = Integer.parseInt(args[0]);
        List<AccountsEntity> accountsEntities = MySQLDaoImpl.getInstance().getAccountsEntities("trafficjunky");
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(List.class, new JsonCampaignListStatisticsEntity());
        builder.registerTypeAdapter(String.class, new JsonCampaignAdUrlDeserializer());
        Gson gson = builder.create();
        List<JsonCampaignStatisticsEntity> jsonCampaignStatisticsEntities;
        String answer;
        String url;
        Map<String, String> parameters;
        SourceStatisticsEntity statisticsEntity;
        SourceStatisticsEntity entity;
        for (AccountsEntity accountsEntity : accountsEntities) {
            for (int i = 0; i <= days; i++) {
                answer = HttpMethodUtils.getMethod("https://api.trafficjunky.com/api/campaigns/bids/stats.json?api_key="
                        + accountsEntity.getApiKey() /*"TJ5a6c8573a19909.93589303"*/
                        + "&startDate="
                        + dateFormat.format(new Date(System.currentTimeMillis() - days * 24L * 60 * 60 * 1000 - deltaTime))
                        + "&endDate="
                        + dateFormat.format(new Date(System.currentTimeMillis() - days * 24L * 60 * 60 * 1000 - deltaTime)));
                jsonCampaignStatisticsEntities = gson.fromJson(answer, List.class);
                for (JsonCampaignStatisticsEntity jsonCampaignStatisticsEntity : jsonCampaignStatisticsEntities) {
                    statisticsEntity = new SourceStatisticsEntity();
                    answer = HttpMethodUtils.getMethod("https://api.trafficjunky.com/api/ads/"
                            + jsonCampaignStatisticsEntity.getCampaignId()
                            + ".json?api_key=" +
                            accountsEntity.getApiKey() /*"TJ5a6c8573a19909.93589303"*/);
                    url = gson.fromJson(answer, String.class);
                    if (url != null) {
                        parameters = Utils.getUrlParameters(url);
                        if (parameters.containsKey("affid")) {
                            if (parameters.get("affid").matches("\\d+")
                                    && MySQLDaoImpl.getInstance().getAffiliateByAfid(Integer.parseInt(parameters.get("affid"))) != null) {
                                statisticsEntity.setAfid(Integer.parseInt(parameters.get("affid")));
                            } else {
                                statisticsEntity.setAfid(0);
                            }
                        } else statisticsEntity.setAfid(2);
                    }
                    statisticsEntity.setAccount_id(accountsEntity.getAccountId());
                    statisticsEntity.setBuyerId(accountsEntity.getBuyerId());
                    statisticsEntity.setDate(new java.sql.Date(System.currentTimeMillis() - days * 24L * 60 * 60 * 1000 - deltaTime));
                    statisticsEntity.setReceiver("API");
                    statisticsEntity.setCampaignName(jsonCampaignStatisticsEntity.getCampaignName());
                    statisticsEntity.setCampaignId(jsonCampaignStatisticsEntity.getCampaignId());
                    statisticsEntity.setSpent(jsonCampaignStatisticsEntity.getCost());
                    statisticsEntity.setConversions(jsonCampaignStatisticsEntity.getConversions());
                    statisticsEntity.setClicks(jsonCampaignStatisticsEntity.getClicks());
                    statisticsEntity.setCpm(jsonCampaignStatisticsEntity.getCPM());
                    statisticsEntity.setCtr(jsonCampaignStatisticsEntity.getCTR());
                    statisticsEntity.setImpressions(jsonCampaignStatisticsEntity.getImpressions());
                    if (Main.days != 0) {
                        entity = MySQLDaoImpl.getInstance()
                                .getSourceStatistics(statisticsEntity.getAccount_id(),
                                        statisticsEntity.getCampaignId(),
                                        statisticsEntity.getDate());
                        if (entity == null) {
                            MySQLDaoImpl.getInstance().addSourceStatistics(statisticsEntity);
                            System.out.println(statisticsEntity);
                        } else {
                            statisticsEntity.setId(entity.getId());
                            MySQLDaoImpl.getInstance().updateSourceStatistics(statisticsEntity);
                            entity = null;
                        }
                    }
                    else {
                        if (MySQLDaoImpl.getInstance().isDateInTodayAdsets(statisticsEntity.getDate(), statisticsEntity.getAccount_id(), statisticsEntity.getCampaignId())) {
                            MySQLDaoImpl.getInstance().updateTodayAdset(Utils.getAdset(statisticsEntity));
                        } else MySQLDaoImpl.getInstance().addTodayAdset(Utils.getAdset(statisticsEntity));

                    }
                }
            }

        }
        MySQLDaoImpl.getSessionFactory().close();
    }
}
