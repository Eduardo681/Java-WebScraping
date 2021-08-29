package br.com.eduardo.brasileiraoapi.util;

import br.com.eduardo.brasileiraoapi.dto.PartidaGoogleDTO;
import lombok.extern.java.Log;
import org.aspectj.apache.bcel.generic.LOOKUPSWITCH;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.print.Doc;
import java.io.IOException;

public class ScrapingUtil {
    private static final String BASE_URL_GOOGLE = "https://www.google.com/search?q=";
    private static final String COMPLEMENTO = "&hl=pt-BR";
    private static final Logger LOGGER = LoggerFactory.getLogger(ScrapingUtil.class);

    public static void main(String[] args) {
        String url = BASE_URL_GOOGLE + "sao+pauloxjuventude" + COMPLEMENTO;
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
            StatusPartida statusPartida = obterStatusPartida(document);
            if(statusPartida != StatusPartida.PARTIDA_NAO_INICIADA) {
                String tempoPartida = obterTempoPartida(document);
            }
            String nomeEquipeCasa = recuperarNomeEquipeCasa(document);
            String nomeEquipeVisitante = recuperarNomeEquipeVisitante(document);
            String urlLogoEquipeCasa = recuperarUrlLogoEquipeCasa(document);
            String urlLogoEquipeVisitante = recuperarUrlLogoEquipeVisitante(document);

        } catch (IOException e) {
            LOGGER.error("Erro ao conectar no google com JSOUP -> {}", e.getMessage());
        }

        return partida;
    }

    private static StatusPartida obterStatusPartida(Document document) {
        StatusPartida statusPartida = StatusPartida.PARTIDA_NAO_INICIADA;
        boolean isTempoPartida = document.select("div[class=imso_mh_lv-m-stts-cont]").isEmpty();
        if (!isTempoPartida) {
            String tempoPartida = document.select("div[class=imso_mh__lv-m-stts-cont]").first().text();
            statusPartida = StatusPartida.PARTIDA_EM_ANDAMENTO;

            if(tempoPartida.contains("Pênaltis")){
                statusPartida = StatusPartida.PARTIDA_PENALTIS;
            }
        }

        isTempoPartida = document.select("span[class=imso_mh__ft-mtch imso-medium-font imso_mh__ft-mtchc]").isEmpty();

        if(!isTempoPartida){
            statusPartida = StatusPartida.PARTIDA_ENCERRADA;
        }
        return statusPartida;
    }

    private static String obterTempoPartida(Document document){
        String tempoPartida = null;
        boolean isTempoPartida = document.select("div[class=imso_mh__lv-m-stts-cont]").isEmpty();
        if(!isTempoPartida){
            tempoPartida = document.select("div[class=imso_mh__lv-m-stts-cont]").first().text();
        }
        isTempoPartida = document.select("span[class=imso_mh__ft-mtch imso-medium-font imso_mh__ft-mtchc]").isEmpty();
        if(!isTempoPartida){
            tempoPartida = document.select("span[class=imso_mh__ft-mtch imso-medium-font imso_mh__ft-mtchc]").first().text();
        }

        return corrigirTempoPartida(tempoPartida);
    }

    private static String corrigirTempoPartida(String tempo){
        if(tempo.contains("'")){
            return tempo.replace(" ", "").replace("'", " min");
        } else {
            return tempo;
        }
    }

    private static String recuperarNomeEquipeCasa(Document document){
        Element el = document.selectFirst("div[class=imso_mh__first-tn-ed imso_mh__tnal-cont imso-tnol]");
        String nome = el.select("span").text();
        return nome;
    }

    private static String recuperarNomeEquipeVisitante(Document document){
        Element el = document.selectFirst("div[class=imso_mh__second-tn-ed imso_mh__tnal-cont imso-tnol]");
        String nome = el.select("span").text();
        return nome;
    }

    private static String recuperarUrlLogoEquipeCasa(Document document){
        Element el = document.selectFirst("div[class=imso_mh__first-tn-ed imso_mh__tnal-cont imso-tnol]");
        String urlLogo = el.select("img[class=imso_btl__mh-logo]").attr("src");
        return urlLogo;
    }

    private static String recuperarUrlLogoEquipeVisitante(Document document){
        Element el = document.selectFirst("div[class=imso_mh__second-tn-ed imso_mh__tnal-cont imso-tnol]");
        String urlLogo = el.select("img[class=imso_btl__mh-logo]").attr("src");
        return urlLogo;
    }
}
