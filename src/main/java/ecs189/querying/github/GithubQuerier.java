package ecs189.querying.github;

import ecs189.querying.Util;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Vincent on 10/1/2017.
 */
public class GithubQuerier {

    private static final String BASE_URL = "https://api.github.com/users/";
    private static final int TOTAL_EVENTS = 10;
    private static final String VALID_TYPE = "PushEvent";

    public static String eventsAsHTML(String user) throws IOException, ParseException {
        List<JSONObject> response = getEvents(user);
        StringBuilder sb = new StringBuilder();
        sb.append("<div>");

        int numEvents = 0;

        for (int i = 0; i < response.size(); i++) {
            JSONObject event = response.get(i);
            // Get event type
            String type = event.getString("type");
            // Get created_at date, and format it in a more pleasant style
            String creationDate = event.getString("created_at");
            SimpleDateFormat inFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'");
            SimpleDateFormat outFormat = new SimpleDateFormat("dd MMM, yyyy");
            Date date = inFormat.parse(creationDate);
            String formatted = outFormat.format(date);

            // Add type of event as header
            sb.append("<h3 class=\"type\">");
            sb.append(type);
            sb.append("</h3>");

            // Add formatted date
            sb.append(" on ");
            sb.append(formatted);
            sb.append("<br />");

            // Add collapsible JSON textbox (don't worry about this for the homework; it's just a nice CSS thing I like)
            sb.append("<a data-toggle=\"collapse\" href=\"#event-" + i + "\">JSON</a>");
            sb.append("<div id=event-" + i + " class=\"collapse\" style=\"height: auto;\"> <pre>");
            sb.append(event.toString());
            sb.append("</pre> </div>");
        }
        sb.append("</div>");
        return sb.toString();
    }

    private static List<JSONObject> getEvents(String user) throws IOException {
        List<JSONObject> eventList = new ArrayList<JSONObject>();
        String url = BASE_URL + user + "/events";
        System.out.println(url);
//        System.out.println(json);
        int page = 0;
        int numEvents = 0;

        JSONObject json = null;
        JSONArray events = null;

//        System.out.println("URL: " + url + "?&access_token=6b6be0366f6b5ad854530e6a4171fb370300e0e1" + "&page=" + page);
        json = Util.queryAPI(new URL(url + "?&access_token=6b6be0366f6b5ad854530e6a4171fb370300e0e1" + "&page=" + page++));
        events = json.getJSONArray("root");

        while (events.length() > 0) {
            JSONObject thisEvent;

            for (int i = 0; i < events.length() && (numEvents < TOTAL_EVENTS); i++) {
                thisEvent = events.getJSONObject(i);

                if (thisEvent.getString("type").equals(VALID_TYPE)) {       // only add pushevents
                    eventList.add(thisEvent);
                    numEvents++;
                }
            }

            if (numEvents == TOTAL_EVENTS) {    // we at 10 events
                break;
            }

            json = Util.queryAPI(new URL(url + "&access_token=6b6be0366f6b5ad854530e6a4171fb370300e0e1" + "&page=" + page++));
            events = json.getJSONArray("root");
        }
        return eventList;
    }
}