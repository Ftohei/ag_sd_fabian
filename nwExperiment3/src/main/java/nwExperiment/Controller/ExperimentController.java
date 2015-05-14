package nwExperiment.Controller;

import com.google.common.collect.Lists;
import nwExperiment.Domain.LotteryParticipant;
import nwExperiment.Domain.Proband;
import nwExperiment.Domain.TmpData;
import nwExperiment.Persistence.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.*;

/**
 * Created by fabiankaupmann on 19.04.15.
 */

@Controller
public class ExperimentController {

    //TODO:

    // - pdfs lokal laden
    // - form validation
    // - nur artikel mit titel nehmen
    // - nur artikel ab einer bestimmten länge nehmen
    // - gleiche artikel rausfiltern
    // - db aufräumen : aktuelle ausgaben rein

    private Stack<Integer> tmpProbandStack = new Stack<>();

    @Autowired
    private ProbandRepository probandRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private ProbandArticleListRepository probandArticleListRepository;

    @Autowired
    private PreferenceRepository preferenceRepository;

    @Autowired
    private LotteryParticipantRepository lotteryParticipantRepository;
    /*
    Registration
     */

    @RequestMapping(value="/nw", method= RequestMethod.GET)
    public String registrationFormNW(Model model){

        String origin = "nw";

        Proband proband = new Proband();
        proband.setOrigin(origin);

        model.addAttribute("proband", proband);
        return "registration";
    }


    @RequestMapping(value="/f", method= RequestMethod.GET)
    public String registrationFormFacebook(Model model){

        String origin = "facebook";

        Proband proband = new Proband();
        proband.setOrigin(origin);

        model.addAttribute("proband", proband);
        return "registration";
    }

    @RequestMapping(value="/", method= RequestMethod.GET)
    public String registrationForm(Model model){

        String origin = "standard";

        Proband proband = new Proband();
        proband.setOrigin(origin);

        model.addAttribute("proband", proband);

        return "registration";
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public String registrationSubmit(@ModelAttribute Proband proband, RedirectAttributes redirectAttributes){

        String origin = proband.getOrigin();

        //Daten des Probanden in DB speichern und 100 zufällig Artikel zuweisen
        ProbandEntity probandEntity = new ProbandEntity(proband.getSex(),
                Integer.parseInt(String.valueOf(proband.getAge())),proband.getPostalCode(),proband.getRubrik(),proband.getInterests(),origin,
                proband.getInterestInPolitics(), proband.getInterestInCulture(), proband.getInterestInLocalArticles(),
                proband.getInterestInSports(), proband.getInterestInLocalSports());
        probandRepository.save(probandEntity);

        //zugewiesene probandId aus Datenbank abfragen
        int probandId = probandRepository.findNewestId();

        //100 artikel zuweisen
        for(byte[] articleId : this.randomArticles()){
            probandArticleListRepository.save(new ProbandArticleListEntity(articleId,probandId));
        }

        //probandId kurzfristig in der Session speichern
        redirectAttributes.addAttribute("probandId", probandId);

        return "redirect:/articles";
    }

    @RequestMapping(value = "/articles", method = RequestMethod.GET)
    public String firstArticle(@ModelAttribute("probandId") int probandId, Model model){

        TmpData tmpData = new TmpData(probandArticleListRepository.findMinIndexOfArticleListForProband(probandId),
                probandArticleListRepository.findMaxIndexOfArticleListForProband(probandId));

        byte[] articleId = probandArticleListRepository.findArticleIdByPraeferenzId(tmpData.getCurrentIndex());

        ArticleEntity article = articleRepository.findById(articleId);

        model.addAttribute("article", article);
        model.addAttribute("tmpData", tmpData);

        return "articles";
    }


    @Transactional
    @RequestMapping(value="/articles", method = RequestMethod.POST)
    public String articles(@ModelAttribute TmpData tmpData, Model model){

        if(tmpData.getCurrentIndex() < tmpData.getMaxIndex()){

            //Praeferenz speichern
            this.savePreference(tmpData.getPreference(), tmpData.getCurrentIndex());

            //currentIndex inkrementieren
            int i = tmpData.getCurrentIndex();
            i++;
            //zum Testen der Endseite
//            i = i +99;
            tmpData.setCurrentIndex(i);

            byte[] articleId = probandArticleListRepository.findArticleIdByPraeferenzId(tmpData.getCurrentIndex());

            ArticleEntity article = articleRepository.findById(articleId);

            model.addAttribute("article", article);
            model.addAttribute("tmpData", tmpData);

            return "articles";
        }
        else if( tmpData.getCurrentIndex() == tmpData.getMaxIndex()){
            //Praeferenz speichern
            this.savePreference(tmpData.getPreference(), tmpData.getCurrentIndex());

            //currentIndex inkrementieren
            int i = tmpData.getCurrentIndex();
            i++;
            tmpData.setCurrentIndex(i);

            byte[] articleId = probandArticleListRepository.findArticleIdByPraeferenzId(tmpData.getCurrentIndex());

            ArticleEntity article = articleRepository.findById(articleId);

            model.addAttribute("article", article);
            model.addAttribute("tmpData", tmpData);

            return "redirect:/thankYouLottery";
        } else {
            return "redirect:/thankYouLottery";
        }

    }


    @RequestMapping(value="/thankYouLottery", method = RequestMethod.GET)
    public String thankYou(Model model){
        model.addAttribute("lotteryParticipant", new LotteryParticipant());
        return "thankYouLottery";
    }

    @RequestMapping(value="/thankYouLottery", method = RequestMethod.POST)
    public String thankYouSubmit(Model model, @ModelAttribute LotteryParticipant lotteryParticipant){
        lotteryParticipantRepository.save(new LotteryParticipantEntity(lotteryParticipant.getMailAddress()));

        return "redirect:/end";
    }

    @RequestMapping(value="/end")
    public String thankYouSubmit(){
        return "end";
    }

    private void savePreference(String preference, int praeferenzId){
        preferenceRepository.save(new PreferenceEntity(praeferenzId,preference));
    }

    private ArrayList<byte[]> randomArticles(){

        ArrayList<byte[]> articles = new ArrayList<>();

        ArrayList<Integer> randomNumbers = new ArrayList<>();

        ArrayList<byte[]> articlesBielefeld = Lists.newArrayList(articleRepository.findAllArticlesBielefeld());

        for(int i = 0; i < articlesBielefeld.size(); i++){
            randomNumbers.add(i);
        }

        Collections.shuffle(randomNumbers);

        for(int i=0;i<20;i++){
            articles.add(articlesBielefeld.get(randomNumbers.get(i)));
        }

        ArrayList<byte[]> articlesKultur = Lists.newArrayList(articleRepository.findAllArticlesKultur());

        randomNumbers.clear();

        for(int i = 0; i < articlesKultur.size(); i++){
            randomNumbers.add(i);
        }

        Collections.shuffle(randomNumbers);

        for(int i=0;i<20;i++){
            articles.add(articlesKultur.get(randomNumbers.get(i)));
        }

        ArrayList<byte[]> articlesPolitik = Lists.newArrayList(articleRepository.findAllArticlesPolitik());

        randomNumbers.clear();

        for(int i = 0; i < articlesPolitik.size(); i++){
            randomNumbers.add(i);
        }

        Collections.shuffle(randomNumbers);

        for(int i=0;i<20;i++){
            articles.add(articlesPolitik.get(randomNumbers.get(i)));
        }

        ArrayList<byte[]> articlesSportBund = Lists.newArrayList(articleRepository.findAllArticlesSportBund());

        randomNumbers.clear();

        for(int i = 0; i < articlesSportBund.size(); i++){
            randomNumbers.add(i);
        }

        Collections.shuffle(randomNumbers);

        for(int i=0;i<20;i++){
            articles.add(articlesSportBund.get(randomNumbers.get(i)));
        }

        ArrayList<byte[]> articlesSportBielefeld = Lists.newArrayList(articleRepository.findAllArticlesSportBielefeld());

        randomNumbers.clear();

        for(int i = 0; i < articlesSportBielefeld.size(); i++){
            randomNumbers.add(i);
        }

        Collections.shuffle(randomNumbers);

        for(int i=0;i<20;i++){
            articles.add(articlesSportBielefeld.get(randomNumbers.get(i)));
        }



        //shuffle final list
        Collections.shuffle(articles);

        return articles;

    }

}
