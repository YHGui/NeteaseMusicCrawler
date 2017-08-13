package com.katsura.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONPath;
import com.google.common.collect.ImmutableMap;
import com.katsura.model.Song;
import com.katsura.model.WebPage;
import com.katsura.model.WebPage.PageType;
import org.apache.tomcat.util.codec.binary.Base64;
import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.List;
import java.util.stream.Collectors;

public class CrawlerThread implements Runnable {

    public static final String BASE_URL = "http://music.163.com/";
    public static final String text = "{\"username\": \"\", \"rememberLogin\": \"true\", \"password\": \"\"}";
    private CrawlerService crawlerService;

    public CrawlerThread() {
        super();
    }

    public CrawlerThread(CrawlerService crawlerService) {
        this.crawlerService = crawlerService;
    }

    @Override
    public void run() {
        while (true) {
            WebPage webPage = crawlerService.getUnCrawlPage();
            if (webPage == null)
                return; // 拿不到url，说明没有需要爬的url，直接退出
            try {
                if (fetchHtml(webPage))
                    parse(webPage);
            } catch (Exception e) { }
        }
    }

    /**
     * 通过url拿到html
     * @param webPage
     * @return
     * @throws IOException
     */
    private boolean fetchHtml(WebPage webPage) throws IOException {
        Connection.Response response = Jsoup.connect(webPage.getUrl()).timeout(3000).execute();
        webPage.setHtml(response.body());
        return response.statusCode() / 100 == 2 ? true : false;
    }

    private void parse(WebPage webPage) throws Exception {
        if (PageType.playlists.equals(webPage.getType()))
            parsePlaylists(webPage).forEach(page -> crawlerService.savePage(page));
        if (PageType.playlist.equals(webPage.getType()))
            parsePlaylist(webPage).forEach(page -> crawlerService.savePage(page));
        if (PageType.song.equals(webPage.getType()))
            crawlerService.saveSong(parseSong(webPage));
    }

    private List<WebPage> parsePlaylists(WebPage webPage) {
        Elements playlists = Jsoup.parse(webPage.getHtml()).select(".tit.f-thide.s-fc0");
        return playlists.stream().map(e -> new WebPage(BASE_URL + e.attr("href"), PageType.playlist)).collect(Collectors.toList());
    }

    private List<WebPage> parsePlaylist(WebPage webPage) {
        Elements songs = Jsoup.parse(webPage.getHtml()).select("ul.f-hide li a");
        return songs.stream().map(e -> new WebPage(BASE_URL + e.attr("href"), PageType.song, e.html())).collect(Collectors.toList());
    }

    private Song parseSong(WebPage webPage) throws Exception {
        return new Song(webPage.getUrl(), webPage.getTitle(), getCommentCount(webPage.getUrl().split("=")[1]));
    }

    private Long getCommentCount(String id) throws Exception {
        //声称AES密钥需要随机，否则服务器会dump掉相同密钥的请求
        String secKey = new BigInteger(100, new SecureRandom()).toString(32).substring(0, 16);
        String encText = aesEncrypt(aesEncrypt(text, "0CoJUm6Qyw8W8jud"), secKey);
        String encSecKey = rsaEncrypt(secKey);
        Response response = Jsoup
                .connect("http://music.163.com/weapi/v1/resource/comments/R_SO_4_" + id + "/?csrf_token=")
                .method(Connection.Method.POST).header("Referer", BASE_URL)
                .data(ImmutableMap.of("params", encText, "encSecKey", encSecKey)).execute();
        return Long.parseLong(JSONPath.eval(JSON.parse(response.body()), "$.total").toString());
    }

    private String aesEncrypt(String value, String key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key.getBytes("UTF-8"), "AES"), new IvParameterSpec(
                "0102030405060708".getBytes("UTF-8")));
        return Base64.encodeBase64String(cipher.doFinal(value.getBytes()));
    }

    private String rsaEncrypt(String value) throws UnsupportedEncodingException {
        value = new StringBuilder(value).reverse().toString();
        BigInteger valueInt = hexToBigInteger(stringToHex(value));
        BigInteger pubkey = hexToBigInteger("010001");
        BigInteger modulus = hexToBigInteger("00e0b509f6259df8642dbc35662901477df22677ec152b5ff68ace615bb7b725152b3ab17a876aea8a5aa76d2e417629ec4ee341f56135fccf695280104e0312ecbda92557c93870114af6c9d05c4f7f0c3685b7a46bee255932575cce10b424d813cfe4875d3e82047b97ddef52741d546b8e289dc6935b3ece0462db0a22b8e7");
        return valueInt.modPow(pubkey, modulus).toString(16);
    }

    private BigInteger hexToBigInteger(String hex) {
        return new BigInteger(hex, 16);
    }

    private String stringToHex(String text) throws UnsupportedEncodingException {
        return DatatypeConverter.printHexBinary(text.getBytes("UTF-8"));
    }

}
