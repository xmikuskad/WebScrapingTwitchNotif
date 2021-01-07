import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.*;
import org.json.simple.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class Verification {

    private String NAME;
    private String PASSWORD;

    public Verification(){
        LoadProperties();
    }

    public void LoadProperties() {
        Properties prop = new Properties();
        try (InputStream inputStream = MyMain.class.getResourceAsStream("/config.properties")) {
            prop.load(inputStream);

            NAME = prop.getProperty("NAME", "null");
            PASSWORD = prop.getProperty("PASSWORD", "null");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public boolean tryToVerificate(String name, String surname, String email) {
        String link = "https://login.uniba.sk/?cosign-proxy-votr.uniba.sk&https://votr.uniba.sk/proxylogin?server=0&destination=" +
                "%3Faction%3DregisterOsob%26meno%3D"+name+"%26priezvisko%3D"+surname+"%26studenti%3Dtrue%26akademickyRok%3D2020%252F2021";

        //TODO update rokov

        try {
            final WebClient webClient = new WebClient(BrowserVersion.CHROME);

            WebRequest request = new WebRequest(
                    new URL(link));

            webClient.getOptions().setThrowExceptionOnScriptError(false);
            webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
            webClient.setJavaScriptTimeout(200);
            webClient.getOptions().setJavaScriptEnabled(true);
            webClient.setAjaxController(new NicelyResynchronizingAjaxController());
            webClient.getOptions().setTimeout(0);
            webClient.getOptions().setRedirectEnabled(true);
            webClient.setCookieManager(new CookieManager());
            webClient.getOptions().setThrowExceptionOnScriptError(false);
            HtmlPage page = webClient.getPage(request);
            webClient.waitForBackgroundJavaScript(150);
            webClient.waitForBackgroundJavaScriptStartingBefore(100);

            HtmlInput intputBox = (HtmlInput)page.getHtmlElementById("login");
            intputBox.setValueAttribute(NAME);

            HtmlInput intputBox2 = (HtmlInput)page.getHtmlElementById("krbpwd_sk");
            intputBox2.setValueAttribute(PASSWORD);


            HtmlElement theElement5 = (HtmlElement) page.getElementById("submit1");
            page = (HtmlPage) theElement5.click();


            /*
            //System.out.println("CSRF: "+ getCsrfToken(page));
            System.out.println("COOKIE");
            System.out.println(cookieMan.getCookie("klaro"));

            //System.out.println( page.getWebClient().getCookieManager().getCookies());

            Set<Cookie> set = page.getWebClient().getCookieManager().getCookies();
            StringBuilder cookieString = new StringBuilder();
            cookieString.append("klaro=%7B%22uniba%22%3Atrue%2C%22youtube%22%3Atrue%2C%22fbpixel%22%3Atrue%2C%22googleAnalytics%22%3Atrue%2C%22google%22%3Atrue%7D; anketaKolacik2021Zima=on; ");
            for(Cookie tempck : set)    {
                System.out.println(tempck.getName()+"="+tempck.getValue() + "; ");
                cookieString.append(tempck.getName()).append("=").append(tempck.getValue()).append("; ");
            }

            String testik = page.asXml().split("\"csrf_token\": ")[1];
            String testik2 = testik.split(",")[0];

            System.out.println(testik2);*/

            //test(cookieString.toString(),testik2);

            String javascriptCode = "httpGet('https://votr.uniba.sk/rpc?name=vyhladaj_osobu')\n" +
                    "function httpGet(url){\n" +
                    "    var xmlHttp = new XMLHttpRequest();\n" +
                    "    xmlHttp.open(\"POST\", url, false );\n" +
                    "    xmlHttp.setRequestHeader(\"Content-Type\",\"application/json\");\n" +
                    "    xmlHttp.setRequestHeader(\"Accept\", \"*/*\");\n" +
                    "    //xmlHttp.setRequestHeader(\"Cookie\",document.cookie);  \n" +
                    "    \n" +
                    "    var testik = document.body.innerHTML.split(\"\\\"csrf_token\\\": \\\"\")[1];\n" +
                    "    var testik2 = testik.split(\"\\\",\")[0];    \n" +
                    "    console.log(testik2)\n" +
                    "    \n" +
                    "    xmlHttp.setRequestHeader(\"X-CSRF-Token\",testik2);  \n" +
                    "    \n" +
                    "    var jsonInputString = [\n" +
                    "                    \""+name+"\",\n" +
                    "                    \""+surname+"\",\n" +
                    "                    false,\n" +
                    "                    false,\n" +
                    "                    false,\n" +
                    "                    \"2020/2021\",\n" +
                    "                    null,\n" +
                    "                    null,\n" +
                    "                    false,\n" +
                    "                    false,\n" +
                    "                    false,\n" +
                    "                    false,\n" +
                    "                    false,\n" +
                    "                    false,\n" +
                    "                    false,\n" +
                    "                    false,\n" +
                    "                    false,\n" +
                    "                    false]\n" +
                    "    \n" +
                    "    xmlHttp.send(JSON.stringify(jsonInputString));\n" +
                    "    return xmlHttp.responseText;\n" +
                    "}";

            String result = (String) page.executeJavaScript(javascriptCode).getJavaScriptResult();
            String totalResult = result.split("result\": ")[1];
            System.out.println("RESULT IS: "+totalResult);
            System.out.println("DONE!");

            boolean isFound = totalResult.contains(email);

            return isFound;

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("FAILED!");
        }
        return false;
    }

    public static void test(String cookie, String csrf)
    {
        System.out.println(cookie);

        HttpURLConnection urlConnection = null;
        try {

            //Connect to twitch
            URL url = new URL("https://votr.uniba.sk/rpc?name=vyhladaj_osobu");
            urlConnection = (HttpURLConnection) url.openConnection();
            /*JsonObject obj = new JsonObject();
            obj.addProperty("0","Gabriel");
            obj.addProperty("1","Drgona");
            obj.addProperty("2",false);
            obj.addProperty("3",true);
            obj.addProperty("4",true);
            obj.addProperty("5",true);
            obj.addProperty("6",true);
            obj.addProperty("7",true);
            obj.addProperty("8",true);
            obj.addProperty("9",true);
            obj.addProperty("10",true);
            obj.addProperty("11",,true);
            obj.addProperty("3",true);
            obj.addProperty("3",true);
            obj.addProperty("3",true);
            obj.addProperty("3",true);*/

            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Accept", "*/*");
            urlConnection.setRequestProperty("Cookie", cookie);
            urlConnection.setRequestProperty("X-CSRF-Token", csrf);

            /*Set<Cookie> set = page.getWebClient().getCookieManager().getCookies();
            for(Cookie tempck : set)    {
                System.out.println("Set-Cookie: " + tempck.getName()+"="+tempck.getValue() + "; " + "path=" + tempck.getPath() + ";");
            }*/

            urlConnection.setRequestMethod("POST");

            urlConnection.setDoOutput(true);


            String jsonInputString = "{"+
                    "0:\"Gabriel\", " +
                    "1:\"Drgona\", " +
                    "2:false, " +
                    "3:true, " +
                    "4:false, " +
                    "5:\"2020/2021\"', " +
                    "6:null, " +
                    "7:null, " +
                    "8:false, " +
                    "9:false, " +
                    "10:false, " +
                    "11:false, " +
                    "12:false, " +
                    "13:false, " +
                    "14:false, " +
                    "15:false, " +
                    "16:false, " +
                    "17:false}";

            //System.out.println(jsonInputString);

            JSONObject obj = new JSONObject();
            obj.put("0","Gabriel");
            obj.put("1","Drgona");
            obj.put("2", Boolean.FALSE);
            obj.put("3",Boolean.TRUE);
            obj.put("4",Boolean.FALSE);
            obj.put("5","2020/2021");
            obj.put("6",null);

            System.out.println(obj.toJSONString());

            StringWriter out = new StringWriter();
            obj.writeJSONString(out);

            String jsonText = out.toString();
            System.out.println(jsonText);

            System.out.println("------------");

            try(OutputStream os = urlConnection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }


            InputStream in = new BufferedInputStream(urlConnection.getInputStream());

            //Get response and parse it to class
            String response = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            System.out.println(response);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}
