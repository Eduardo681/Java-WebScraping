package br.com.eduardo.brasileiraoapi.util;

import br.com.eduardo.brasileiraoapi.dto.PartidaGoogleDTO;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;

public class ScrapingUtil {
    private static final String BASE_URL_GOOGLE = "https://www.google.com/search?q=";
    private static final String COMPLEMENTO = "&hl=pt-BR";
    private static final Logger LOGGER = LoggerFactory.getLogger(ScrapingUtil.class);

    public static void main(String[] args) {
        String url = BASE_URL_GOOGLE + "palmeiras+x+corinthians+08/08/2020" + COMPLEMENTO;
        ScrapingUtil scraping = new ScrapingUtil();
        scraping.obterInformacoesPartida(url);
    }

    public static PartidaGoogleDTO obterInformacoesPartida(String url) {
        PartidaGoogleDTO partida = new PartidaGoogleDTO();
        Document document;

        try {
            document = Jsoup.connect(url).get();
            String title = document.title();
            LOGGER.info(title);
        } catch (IOException e) {
            LOGGER.error("Erro ao conectar no google com JSOUP -> {}", e.getMessage());
        }

        return partida;
    }
}
