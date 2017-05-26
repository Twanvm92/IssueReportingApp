package com.example.justin.verbeterjegemeente.API;

import java.io.IOException;

/**
 * Created by twanv on 26-5-2017.
 */

public class ConnectionChecker {

    /**
     * Method that pings to google.com to check if user is actually
     * connected to the internet.
     * @return True if user is connected to the internet
     * and false if user cannot connect to google.com
     * @throws InterruptedException
     * @throws IOException
     */
    public static boolean isConnected() throws IOException, InterruptedException {
        String command = "ping -c 1 google.com";
        return (Runtime.getRuntime().exec (command).waitFor() == 0);
    }

}
