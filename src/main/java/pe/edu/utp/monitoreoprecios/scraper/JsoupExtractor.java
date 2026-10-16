package pe.edu.utp.monitoreoprecios.scraper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class JsoupExtractor {

    public String extraerTitulo(String url) throws Exception {
        Document doc = Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (Academic price monitoring prototype)")
                .timeout(10_000)
                .get();

        return doc.title();
    }
}
