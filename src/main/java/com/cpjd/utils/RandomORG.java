package com.cpjd.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class RandomORG {

    public static int[] doRequest() {
        HttpURLConnection connection = null;
        try {
            URL url = new URL( "https://www.random.org/integers/?num=10&min=1&max=500000&col=1&base=10&format=plain&rnd=new");
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
           // connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "wdavies973@gmail.com");
            //connection.setDoInput(true);
            connection.setReadTimeout(5000);
            //connection.setRequestProperty("charset", "utf-8");
            connection.setUseCaches(false);

            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            StringBuilder response = new StringBuilder();

            String line;
            int[] nums = new int[7];

            while ((line = rd.readLine()) != null) {

                for(int i = 0; i < nums.length; i++) {
                    nums[i] = Integer.parseInt(line);
                }
            }
            rd.close();
            return nums;
        } catch(Exception e) {
            // do nothing, should be handled somewhere else
            e.printStackTrace();
        } finally {
            if(connection != null) connection.disconnect();
        }
        return null;
    }

    public static void main(String[] args) {
        doRequest();
    }

}
