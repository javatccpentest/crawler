/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Crawler.Controller;

import Crawler.DAO.FormJpaController;
import Crawler.DAO.WebpageJpaController;
import Crawler.DAO.WebpageOffJpaController;
import Crawler.Model.Form;
import Crawler.Model.Webpage;
import Crawler.Model.WebpageOff;
import java.io.*;
import java.math.*;
import java.net.*;
import java.security.*;
import java.util.regex.*;
import org.jsoup.*;
import org.jsoup.nodes.*;

/**
 *
 * @author Matheus
 */
public class Page /*implements Runnable */ {

    private Document DOC;
    private String URL;
    private String DIRETORIO;
    private String PROTOCOL;

    private Crawler crawler;

//    @Override
//    public void run() {
//
//        Webpage PAGE;
//        PAGE = new Webpage();
//        PAGE.setDominioId(this.crawler.getDOM());
//        //PAGE.setReferencias();
//        PAGE.setUrl(this.URL);
//        Connect(PAGE);
//        this.crawler.setExe();
//    }
    public Page(Crawler c, String URL, Webpage raiz) {
        this.crawler = c;

        if (URL.charAt(URL.length() - 1) == '/') {
            URL = URL.substring(0, URL.length() - 1);
        }
        if (!URL.contains("http://localhost/phpmyadmin")) {
            this.URL = URL;
            this.DIRETORIO = getPath();
            this.PROTOCOL = URL.split("/")[0].trim();

//            this.crawler.getPool().execute(this);
            Webpage PAGE;
            PAGE = new Webpage();
            PAGE.setDominioId(this.crawler.getDOM());
            //PAGE.setReferencias();
            PAGE.setUrl(this.URL);
            Connect(PAGE, raiz);
//            try {
//                Thread.sleep(500);
//            } catch (InterruptedException ex) {
//
//            }
//            this.crawler.setTotal();
        }
    }

    private String getType() {
        try {
            String[] split = this.URL.split("\\.");

            return split[split.length - 1];
        } catch (ArrayIndexOutOfBoundsException e) {

        }
        return null;
    }

    public String getURL() {
        return URL;
    }

    private void Connect(Webpage PAGE, Webpage raiz) {
        WebpageJpaController webJpa
                = new WebpageJpaController(this.crawler.getEntityManager().getEntityManagerFactory());

        WebpageOffJpaController offJpa
                = new WebpageOffJpaController(this.crawler.getEntityManager().getEntityManagerFactory());

        try {
            //Proxy proxy = new Proxy(Proxy.Type.HTTP,
            //        new InetSocketAddress("", ));

            this.DOC = (Document) Jsoup.connect(URL)
                    .userAgent("Mozilla/5.0")
                    .proxy(Proxy.NO_PROXY)
                    .get();

            //if (findWebPageEntities.isEmpty()) {
            if (!this.crawler.getArray_url().contains(this.URL)) {
                this.crawler.getArray_url().add(URL);
                webJpa.create(PAGE);
//                this.crawler.getPool().execute(new Runnable() {
//                    @Override
//                    public void run() {
                filtrar(PAGE);
//                this.finalize();
//                    }
//                });
            }

        } catch (org.jsoup.UnsupportedMimeTypeException ex) {

            System.err.println("TypeException == " + URL);
            PAGE.setContentType(getType());
            //if (findWebPageEntities.isEmpty()) {
            if (!this.crawler.getArray_url().contains(this.URL)) {
                this.crawler.getArray_url().add(URL);
                webJpa.create(PAGE);
            }

        } catch (HttpStatusException ex) {

//            System.err.println("HttpStatus 1 == " + URL);
//            System.out.println(raiz.getId() + "\t" + getType());
            WebpageOff webpageOff = new WebpageOff();

            webpageOff.setContentType(getType());
            webpageOff.setUrl(this.URL);
            webpageOff.setIdWebpage(raiz);
//            System.err.println("HttpStatus 2 == " + URL);
            offJpa.create(webpageOff);

//            System.err.println("HttpStatus 3 == " + URL);
        } catch (IOException ex) {
            //ex.printStackTrace();
        } catch (Throwable ex) {
            //ex.printStackTrace();
        }
    }

    private String getHash(String s) {
        MessageDigest m;
        try {
            m = MessageDigest.getInstance("MD5");
            m.update(s.getBytes(), 0, s.length());

            return (new BigInteger(1, m.digest()).toString(16));

        } catch (NoSuchAlgorithmException ex) {
            //ex.printStackTrace();
        }

        return null;
    }

    private void filtrar(Webpage PAGE) {
        try {
            for (Element element : DOC.select("FORM")) {
                Form form = new Form();
                form.setForm(element.toString());
                form.setFormHash(getHash(element.toString()));
                form.setWebpageId(PAGE);
                FormJpaController formJpa = new FormJpaController(this.crawler.getEntityManager().getEntityManagerFactory());

                formJpa.create(form);

            }
        } catch (NullPointerException ex) {
            //ex.printStackTrace();
        }

        try {
            String[] tags = {"A"/*, "LINK", "IMG"*/};
            for (String tag : tags) {

                for (Element element : DOC.select(tag)) {
                    try {
                        String LINK = tag.equals("IMG") ? element.attr("SRC") : element.attr("HREF");

                        //tratamento para as ancoras
                        if (LINK.contains("#") && !LINK.equals("#")) {
                            LINK = LINK.split("#")[0].trim();
                        }
                        if (!LINK.equals("") && LINK.charAt(0) != '#') {
//------------------------------------------------------
                            //verifica se inicia com '/'
                            if (LINK.charAt(0) == '/') {

                                String add = PROTOCOL + "//" + crawler.getDOM().getDominio() + LINK;
                                //varifica se não possui '//'
                                if (LINK.charAt(1) != '/') {

                                    new Page(this.crawler, add, PAGE);

                                } else {

                                    if (((PROTOCOL + LINK).split("/")[2].trim()).equals(crawler.getDOM().getDominio())) {

                                        new Page(this.crawler, PROTOCOL + LINK, PAGE);
                                    }
                                }
                            } else {
                                try {
                                    if (LINK.contains("//")) {
                                        String LINK_DOMINIO = LINK.split("/")[2].trim();

                                        if (LINK_DOMINIO.equals(crawler.getDOM().getDominio())
                                                || LINK_DOMINIO.equals(crawler.getDOM().getRemoteAddress())) {

                                            new Page(this.crawler, LINK, PAGE);

                                        }
                                    } else {
                                        new Page(this.crawler, DIRETORIO + LINK, PAGE);
                                    }
                                } catch (Exception ex) {

                                    new Page(this.crawler, DIRETORIO + LINK, PAGE);

                                }
                            }
                        }
                    } catch (Exception ex) {
                        //ex.printStackTrace();
                    }
                }
            }
        } catch (NullPointerException ex) {
            //ex.printStackTrace();
        }
    }

    private String getPath() {
        Pattern p = Pattern.compile("[\\w\\D\\n]*?/", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(URL);
        String a = "";
        while (m.find()) {
            a += m.group();
        }

        return a;
    }

}
