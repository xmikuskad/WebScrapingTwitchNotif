import com.google.gson.Gson;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.Interval;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TwitchChecker {

    String CLIENT_ID;
    String AUTH;
    String ID;

    long WAIT_TIME = 30L;

    public TwitchChecker() {
        LoadProperties();

        //Use one thread
        ScheduledExecutorService execService = Executors.newScheduledThreadPool(1);

        // Schedule a task to run every 60 seconds with no initial delay.
        execService.scheduleAtFixedRate(() -> {
            MakeCall();
        }, 1L, WAIT_TIME, TimeUnit.SECONDS);
    }

    public void LoadProperties() {
        Properties prop = new Properties();
        try (InputStream inputStream = MyMain.class.getResourceAsStream("/config.properties")) {
            prop.load(inputStream);

            CLIENT_ID = prop.getProperty("CLIENT_ID", "null");
            ID = prop.getProperty("ID", "null");
            AUTH = prop.getProperty("AUTH", "null");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void MakeCall() {
        HttpURLConnection urlConnection = null;
        try {

            //Connect to twitch
            URL url = new URL("https://api.twitch.tv/kraken/streams/"+ID);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty("Accept", "application/vnd.twitchtv.v5+json");
            urlConnection.setRequestProperty("Authorization", AUTH);
            urlConnection.setRequestProperty("client-id", CLIENT_ID);
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());

            //Get response and parse it to class
            String response = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            System.out.println(response);
            Gson gson = new Gson();
            TwitchResponse twitchResponse = gson.fromJson(response, TwitchResponse.class);

            if(twitchResponse.getStream() != null)
            {
                //We got the stream
                System.out.println("Stream title "+twitchResponse.getStream().getChannel().getStatus());
                System.out.println("Game name "+twitchResponse.getStream().getGame());
                System.out.println("Stream started "+twitchResponse.getStream().getCreated_at());

                //Check if we should show notification
                try {
                    String myDate = twitchResponse.getStream().getCreated_at();
                    myDate = myDate.replace('T',' ');
                    myDate = myDate.replace('Z',' ');

                    DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss ");
                    DateTime dt = formatter.parseDateTime(myDate);
                    dt = dt.plusHours(1);

                    Interval interval = new Interval(dt,new Instant());
                    Duration duration = interval.toDuration();

                    // OPATRNE TOTO MOZE SPOSOBIT 2x ZOBRAZENIE PRI ZLOM NACASOVANI TODO overit
                    //if(duration.getStandardSeconds() < WAIT_TIME+1)
                    System.out.println("BEZI:" +duration.getStandardSeconds());

                    if(duration.getStandardSeconds() <600)
                    {
                        //Show notification
                        System.out.println("NOTIFICATION");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else {
                System.out.println("STREAM IS OFFLINE!");
            }



        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(urlConnection != null)
                urlConnection.disconnect();
        }
    }

}
