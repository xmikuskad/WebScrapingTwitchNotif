public class MyMain {

    public static void main(String[] args) {
        System.out.println("HELLO");
        //TwitchChecker twitchChecker = new TwitchChecker();
        Verification verification = new Verification();
        boolean found = verification.tryToVerificate("Viktor","Sisik","sisik1@uniba.sk");

        if(found)
            System.out.println("FOUND");
        else
            System.out.println("NOT FOUND");

    }


}


