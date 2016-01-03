package simplenlg;

import simplenlg.features.Feature;
import simplenlg.features.Form;
import simplenlg.features.Tense;
import simplenlg.framework.*;
import simplenlg.lexicon.Lexicon;
import simplenlg.lexicon.XMLLexicon;
import simplenlg.morphology.english.MorphologyRules;
import simplenlg.phrasespec.AdjPhraseSpec;
import simplenlg.phrasespec.NPPhraseSpec;
import simplenlg.phrasespec.PPPhraseSpec;
import simplenlg.phrasespec.SPhraseSpec;
import simplenlg.realiser.english.Realiser;

import java.util.HashMap;
import java.util.Map;

/**
 * @author niyatiagarwal
 */
public class nlgtest {
    public static void main(String[] args) {

        // below is a simple complete example of using simplenlg V4
        // afterwards is an example of using simplenlg just for morphology

        // set up
        simplenlg.lexicon.Lexicon lexicon = new simplenlg.lexicon.XMLLexicon();                          // default simplenlg lexicon
        simplenlg.framework.NLGFactory nlgFactory = new NLGFactory(lexicon);             // factory based on lexicon

        // create sentences
        // 	"John did not go to the bigger park. He played football there."
        NPPhraseSpec thePark = nlgFactory.createNounPhrase("the", "park");   // create an NP
        AdjPhraseSpec bigp = nlgFactory.createAdjectivePhrase("big");        // create AdjP
        bigp.setFeature(Feature.IS_COMPARATIVE, true);                       // use comparative form ("bigger")
        thePark.addModifier(bigp);                                        // add adj as modifier in NP
        // above relies on default placement rules.  You can force placement as a premodifier
        // (before head) by using addPreModifier
        PPPhraseSpec toThePark = nlgFactory.createPrepositionPhrase("to");    // create a PP
        toThePark.setObject(thePark);                                     // set PP object
        // could also just say nlgFactory.createPrepositionPhrase("to", the Park);

        SPhraseSpec johnGoToThePark = nlgFactory.createClause("John",      // create sentence
                "go", toThePark);

        johnGoToThePark.setFeature(Feature.TENSE, Tense.PAST);              // set tense
        johnGoToThePark.setFeature(Feature.NEGATED, true);                 // set negated

        // note that constituents (such as subject and object) are set with setXXX methods
        // while features are set with setFeature

        DocumentElement sentence = nlgFactory                            // create a sentence DocumentElement from SPhraseSpec
                .createSentence(johnGoToThePark);

        // below creates a sentence DocumentElement by concatenating strings
        StringElement hePlayed = new StringElement("he played");
        StringElement there = new StringElement("there");
        WordElement football = new WordElement("football");

        DocumentElement sentence2 = nlgFactory.createSentence();
        sentence2.addComponent(hePlayed);
        sentence2.addComponent(football);
        sentence2.addComponent(there);

        // now create a paragraph which contains these sentences
        DocumentElement paragraph = nlgFactory.createParagraph();
        paragraph.addComponent(sentence);
        paragraph.addComponent(sentence2);

        // create a realiser.  Note that a lexicon is specified, this should be
        // the same one used by the NLGFactory
        Realiser realiser = new Realiser(lexicon);
        //realiser.setDebugMode(true);     // uncomment this to print out debug info during realisation
        NLGElement realised = realiser.realise(paragraph);

        System.out.println(realised.getRealisation());

        // end of main example

        // second example - using simplenlg just for morphology
        // below is clumsy as direct access to morphology isn't properly supported in V4.2
        // hopefully will be better supported in later versions

        // get word element for "child"
        WordElement word = (WordElement) nlgFactory.createWord("child", LexicalCategory.NOUN);
        // create InflectedWordElement from word element
        InflectedWordElement inflectedWord = new InflectedWordElement(word);
        // set the inflected word to plural
        inflectedWord.setPlural(true);
        // realise the inflected word
        String result = realiser.realise(inflectedWord).getRealisation();

        System.out.println(result);
        createSentence();
    }

    public static void createSentence() {
        Lexicon lexicon = new XMLLexicon();
        NLGFactory nlgFactory = new NLGFactory(lexicon);
        Realiser realiser = new Realiser(lexicon);

        // "can u help me find the best eating joints for indian food near sohna road open for delivery at midnight"
        // entities:
         /*  location - sohna road
             category - delivery
             cuisine -  indian
             sorting-basis - rating
             sorting-order - ascending
             datetime - time
             datetime/role - opens at
         */
        Map entities = new HashMap<String, String>();
        entities.put("category", "delivery");
        entities.put("cuisine", "Indian");
        String category = (String) entities.get("category");
        String verb = (category != null && !"".equals(category)) ? category : "serving";
        String cuisine = (String) entities.get("cuisine");
        String object = cuisine != null ? cuisine + " food" : "food";

        SPhraseSpec inputSentence = nlgFactory.createClause();
        inputSentence.setSubject("Restaurants");
        inputSentence.setVerb(verb);
        inputSentence.setObject(object);
        inputSentence.setPlural(true);
      //  MorphologyRules.buildRegularPresPartVerb(verb);
        inputSentence.setFeature(Feature.FORM,Form.PRESENT_PARTICIPLE);

        String result = realiser.realiseSentence(inputSentence);
        System.out.print(result);
    }

}
