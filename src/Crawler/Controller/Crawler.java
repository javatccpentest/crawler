/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Crawler.Controller;

import Crawler.DAO.DominioJpaController;
import Crawler.Model.Dominio;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.persistence.EntityManager;

/**
 *
 * @author Matheus
 */
public abstract class Crawler {

    private final String URL;
    private final Dominio DOM;
    //private final ExecutorService pool = Executors.newCachedThreadPool();
    private final ArrayList<String> array_url = new ArrayList<>();

    private int total = 0;
    private int exe = 0;

    public Dominio getDOM() {
        return DOM;
    }

//    public ExecutorService getPool() {
//        return pool;
//    }
    synchronized ArrayList<String> getArray_url() {
        return array_url;
    }

    //pool.execute(new Runnable() { };
    public abstract EntityManager getEntityManager();

    public Crawler(String URL) {

        this.DOM = new Dominio();
        System.setProperty("https.protocols", "TLSv1");

        if (URL.charAt(URL.length() - 1) == '/') {

            this.URL = URL.substring(0, URL.length() - 1);

        } else {

            this.URL = URL;

        }

        DOM.setDominio(this.URL.split("/")[2].trim());
        try {
            InetAddress address = InetAddress.getByName(new URL(this.URL)
                    .getHost());

            DOM.setRemoteAddress(address.getHostAddress());

            URL obj = new URL(this.URL);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            DOM.setServer(con.getHeaderField("Server"));

        } catch (IOException ex) {
            //ex.printStackTrace();
        }

    }

    public Dominio Play() {

        DominioJpaController jpa = new DominioJpaController(getEntityManager().getEntityManagerFactory());

        jpa.create(DOM);

        new Page(this, this.URL, null);

//        try {
//            while (!this.pool.awaitTermination(10, TimeUnit.SECONDS)) {
//
//            }
//        } catch (InterruptedException ex) {
//            ex.printStackTrace();
//        }
//        while (total != exe | total == 0) {
//            System.out.println("while\t" + total + "\t" + exe);
//            try {
//                Thread.sleep(5);
//            } catch (InterruptedException ex) {
//                ex.printStackTrace();
//            }
//        }
//
//        System.out.println("terminei");
//        System.out.println("while\t" + total + "\t" + exe);
//        pool.shutdown();
        return DOM;
    }

    synchronized void setTotal() {
        this.total++;
    }

    synchronized void setExe() {
        this.exe++;
    }

}
