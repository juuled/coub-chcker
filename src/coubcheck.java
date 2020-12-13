import com.google.gson.Gson;
import com.google.gson.JsonObject;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;

import java.io.*;
import java.util.ArrayList;
import java.util.Random;

public class coubcheck {

    // globes
    public static int threads = 0;
    // grab tokens from network under inspect element
    public static String tokens[] = {"", ""};
    public static int i = 0;
    public static String lFile = "src/users.txt";
    public static String oFile = "src/open.txt";
    public static ArrayList<String> openUsers = new ArrayList();
    public static void main(String args[]) throws IOException {
        userCheck(readList(lFile));
    }
    // read input
    public static ArrayList readList(String filepath) throws IOException {
        FileReader reader = new FileReader(filepath);
        BufferedReader read = new BufferedReader(reader);
        ArrayList users = new ArrayList();
        for(String lineRead = read.readLine(); lineRead != null; lineRead = read.readLine()) {
            users.add(lineRead);
        }
        return users;
    }
    // runner
    public static void userCheck(ArrayList<String> user){
        setup();
        while(user.size() > i) {
            try {
                userCheckRequest(user.get(i));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            i++;
        }
        try {
            noteOpens(oFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // actual request
    public static void userCheckRequest(String user) throws InterruptedException {
       if(i % 10 == 0 || i % 35 == 0) {
           int rando = (int)(Math.random()*400) + 2500;
           Thread.sleep(rando);
       }

       // pick random token from array
        int t = (int)(Math.random()*10);
        if(t > 5) {
            t = (int) (Math.random()* tokens.length);
        } else {
            t = 0;
        }
        HttpResponse<String> response = Unirest.get("https://coub.com/api/v2/channels/validate_permalink?channel%5Bpermalink%5D=" + user)
                .header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.198 Safari/537.36")
                .header("Accept-Language", "en-US,en;q=0.9")
                .header("Cookie", "remember_token="+ tokens[t] + ";")
                .asString();
        i++;
        int codes = response.getStatus();
        Gson gson = new Gson();
        JsonObject vals = new Gson().fromJson(response.getBody(), JsonObject.class);
        String result = "";
        // tf lol
        try {
            result = vals.get("result").getAsString();
        } catch (Exception e) {
            System.out.println("Ratelimit or other error, the processor got a malformed response");
            System.out.println(codes + " was the status code returned :(");
            try {
                noteOpens(oFile);
                System.exit(0);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
        if(result.equals("free")) {
            System.out.println(codes + " |" +
                    " The name " + user + " is open");
            openUsers.add(user);
        }
        if(codes != 200) {
            System.out.println("Uhoh a problem happened the code is " + codes + " stopping the checker now" );
            System.exit(0);
        }
    }
    public static void noteOpens(String filepath) throws IOException {
        FileWriter openings = new FileWriter(filepath);
        // clean file
        new FileWriter(filepath, false).close();
        // for loop 4 details
        openings.write( openUsers.size() + " open usernames were found! go claim them");
        for(int x = 0; x < openUsers.size(); x++) {
            String hold = openUsers.get(x);
            openings.write("\n" + hold);
        }
        // close writer
        openings.close();
    }
    // ascii + cookies be gone
    public static void setup() {
        Unirest.config().enableCookieManagement(false);
        String art = "                         \n" +
                "    )           )        \n" +
                " ( /(     )  ( /(     )  \n" +
                " )\\()) ( /(  )\\()) ( /(  \n" +
                "((_)\\  )(_))((_)\\  )(_)) \n" +
                "| |(_)((_)_ | |(_)((_)_  \n" +
                "| ' \\ / _` || ' \\ / _` | \n" +
                "|_||_|\\__,_||_||_|\\__,_| ";
        System.out.println(art);
        System.out.println("Coub checker by ju#5905");
    }
    // ok dokie
}
