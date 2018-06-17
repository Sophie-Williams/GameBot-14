package com.cpjd.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.SecureRandom;
import java.util.Random;

//import org.apache.log4j.Logger;

/**
 * A random.org seeded random generator.
 *
 * @author Kenzo
 *
 */
public class RandomOrgSeededRandomGenerator {

    private Random random;

    /**
     * Construct a new random.org seeded random generator.
     *
     */
    public RandomOrgSeededRandomGenerator() {

        try {
            final URL url = new URL(
                    "http://www.random.org/strings/?num=10&len=10&digits=on&unique=on&format=plain&rnd=new");
            URLConnection connection = url.openConnection();
            connection.setConnectTimeout(5000);
            final BufferedReader in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));

            final StringBuilder stringBuilder = new StringBuilder();

            String line;
            while ((line = in.readLine()) != null) {
                stringBuilder.append(line);
            }

            in.close();

            final byte[] seed = stringBuilder.toString().getBytes();

            random = new SecureRandom(seed);
        } catch (final IOException e) {
            //logger.info("Default secure random is used.");
            random = new SecureRandom();
        }
    }

    /**
     * Returns a random-object.
     *
     * The default implementation uses the current time as seed.
     *
     * @return A random-object.
     */
    public Random getRandom() {
        return random;
    }
}