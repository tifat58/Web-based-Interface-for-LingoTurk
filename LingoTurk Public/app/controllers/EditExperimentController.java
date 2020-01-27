/* This file is created and compiled by Ayan Majumder, Shakhwat Ahmed Nobin, Hasan Md Tusfiqur Alam, Rayhanul Islam Rumel, Akshay Akshay,
* Chirag Bhuvaneshwara and Khaushik Chowdhury as a Software Engineering Course project in winter semester 2019/20 */
package controllers;
import be.objectify.deadbolt.java.actions.SubjectPresent;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import play.data.FormFactory;

import play.mvc.BodyParser;
import play.mvc.Result;

import play.mvc.Http;
import play.mvc.Controller;
import play.twirl.api.Html;
import services.DatabaseService;
import services.ExperimentWatchService;
import services.LingoturkConfig;

import javax.inject.Inject;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static play.libs.Json.stringify;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import views.html.index;

import static play.mvc.Results.*;

/*
The following classes are defined to store the JSON values in Java object format.
EditedExperiment class has a list of Slide objects, Slide class has a list of
Component objects. Each Component will in turn have a list of Option and Condition classes.
The Option and Condition classes have the option name and option value attributes.
Any new component/option/condition added later will also be stored in this format
automatically.
 */

class Condition {
    String name;
    String value;
}

class Option {
    String name;
    String value;
}

class Slide {
    String num;
    String name;
    ArrayList<Component> components;
}

class EditedExperiment {
    String name; // name of experiment
    ArrayList<Slide> slides; // list of component objects.
}

class Component {
    String name; // name identifier of component
    String model_attr; // this is the ng-model attr we need to attach to.
    String mandatory;
    ArrayList<Option> options; // list of options
    ArrayList<Condition> conditions; // list of conditions.
}

public class EditExperimentController extends Controller {
    private final DatabaseService databaseService;
    private final LingoturkConfig lingoturkConfig;
    private final ExperimentWatchService experimentWatchService;

    @Inject
    public EditExperimentController(final FormFactory formFactory, DatabaseService databaseService, LingoturkConfig lingoturkConfig, ExperimentWatchService experimentWatchService) {
        this.databaseService = databaseService;
        this.lingoturkConfig = lingoturkConfig;
        this.experimentWatchService = experimentWatchService;
    }

    @SubjectPresent
    @BodyParser.Of(value = BodyParser.Json.class)
    public Result submitEditExperiment() throws IOException {
        try {
            int componentCounter = 0;
            System.out.println("Here");
            ObjectMapper mapper = new ObjectMapper();
            // We get our data as JSON, convert that to Java format.
            JsonNode json = request().body().asJson();
//            System.out.println("The body");
//            System.out.println(request().body());
            // Create the main Experiment class to store the data.
            EditedExperiment ex = new EditedExperiment();
            ex.name = json.get("name").asText() + "Experiment";
//            System.out.println("Exp Name: " + ex.name);
            ArrayNode slideArr = (ArrayNode) json.get("slides");
            int totalNumSlides = slideArr.size();
            boolean dragDropPresent = false;
            ex.slides = new ArrayList<Slide>();
            String slideNum = "0";
            if (slideArr != null) {
                // Loop through each slide
                for (Iterator<JsonNode> i = slideArr.iterator(); i.hasNext(); ) {
                    // Create this slide's object
                    Slide s = new Slide();
                    JsonNode n_s = i.next();
                    s.num = n_s.get("num").asText();
                    s.name = n_s.get("name").asText();
                    System.out.println("Slide Num:" + s.num);
                    ArrayNode compArr = (ArrayNode) n_s.get("components");
                    s.components = new ArrayList<Component>();
                    if (compArr != null) {
                        // Loop through the components
                        for (Iterator<JsonNode> it = compArr.iterator(); it.hasNext(); ) {
                            JsonNode n = it.next();
                            String compName = n.get("name").asText();
                            // Create component object.
                            Component c = new Component();
                            c.name = compName;
                            System.out.println("Component Name: " + compName);
                            if (!c.name.equals("nextSlideButton")) {
                                try {
                                    c.model_attr = n.get("model_attr").asText();
                                    if (c.model_attr.equals("")) {
                                        componentCounter += 1;
                                        c.model_attr = compName + "Response" + Integer.toString(componentCounter);
                                    }
                                    else {
                                        c.model_attr = c.model_attr.replaceAll("\\s+","_");

                                    }
                                    System.out.println(c.model_attr);
                                } catch (Exception e) {
                                    // Now we are going to set the model attribute automatically for all components.
                                    // This will remove the need to send it from front-end all the time, which is
                                    // redundant.
                                    componentCounter += 1;
                                    c.model_attr = compName + "Response" + Integer.toString(componentCounter);
                                    //c.model_attr = null;
                                }
                            }
                            else {
                                // This is for the next slide button. The model_attr is the text.
                                try {
                                    String buttonText = n.get("model_attr").asText();
                                    if (!buttonText.equals("")) {
                                        c.model_attr = buttonText;
                                    }
                                }
                                catch (Exception e) {
                                    c.model_attr = null;
                                }
                            }
                            System.out.println("Component Corresponding model attr: " + c.model_attr);
                            c.options = new ArrayList<Option>();
                            ArrayNode optArr = (ArrayNode) n.get("options");
                            if (optArr != null) {
                                // Loop through the options.
                                for (Iterator<JsonNode> it1 = optArr.iterator(); it1.hasNext(); ) {
                                    JsonNode node = it1.next();
                                    if (node.has("name")) {
                                        String optName = node.get("name").asText();
                                        JsonNode optVal = node.get("value");
                                        // Checking nullPointer for option values
                                        if (optVal != null){
                                            String optValText = optVal.asText();
                                            Option o = new Option();
                                            o.name = optName;
                                            System.out.println("Option Name: " + optName);
                                            o.value = optValText;
                                            // Add the option to component's option list
                                            c.options.add(o);
                                        }

                                    }
                                }
                            }
                            // Now we do for conditions.
                            c.conditions = new ArrayList<Condition>();
                            ArrayNode condArr = (ArrayNode) n.get("conditions");
                            if (condArr != null) {
                                // Loop through the conditions.
                                for (Iterator<JsonNode> it2 = condArr.iterator(); it2.hasNext();) {
                                    JsonNode node = it2.next();
                                    if (node.has("name")) {
                                        String condName = node.get("name").asText();
                                        // Create single condition object.
                                        JsonNode condVal = node.get("value");

                                        // Checking null pointer for conditions
                                        if (condVal != null){
                                            String condValText = condVal.asText();
                                            Condition cd = new Condition();
                                            cd.name = condName;
                                            cd.value = condValText;
                                            c.conditions.add(cd);

                                        }

                                    }
                                }
                            }
                            // Add the component to the slide.
                            s.components.add(c);
                        }
                    }
                    // Add the slide to the experiment.
                    ex.slides.add(s);
                }
            }
            // Now to read the files
            String fileName = ex.name;
            Path currentPath = Paths.get(System.getProperty("user.dir"));
            Path filePath = Paths.get(currentPath.toString(), "template", "templateExperimentRendering.html");

            File input = new File(filePath.toString());
            // Parse the entire document using Jsoup.
            Document doc = Jsoup.parse(input, "UTF-8", "");
            // This is the main div that has the content of the experiment for each question.
            Element content = doc.getElementById("experiment_content");

            Element heading = content.getElementById("experiment_heading_1");
            Element body = content.getElementById("experiment_body_1");
            Element panel = content.getElementById("experiment_panel_1");
            // The panel element has the contents.
            String panel_html = panel.outerHtml();


            content.attr("ng-init", "RC.subslide = 1");

            Element child_panel1 = content.getElementById("experiment_panel_1");

            content = content.empty();

            // Get the button child of body. We need to add that back eventually.
            Elements body_button = body.getElementsByAttributeValue("ng-click", "RC.nextQuestion()");
            Element button = body_button.get(0);

            // Start the loop
//            System.out.println("Now iterate the Exp object");
            Element panel_body = panel.getElementById("experiment_body_1");
            // New List to track all mandatory fields. These need to be added to final Submit button.
            ArrayList<String> mandatoryFields = new ArrayList<String>();
            // Checkers for conditions on text-answer.
            ArrayList<String> textCheckers = new ArrayList<String>();
            // THe slide for-loop.
            for (Iterator<Slide> it = ex.slides.iterator(); it.hasNext(); ) {
                // Flag to see if drag and drop is present in this slide. If yes, we do not add Slide change button.
                dragDropPresent = false;

                Slide s = it.next();

                // Start of the slide HTML code.
                String slide_html = "<div ng-if=\"RC.subslide == " + s.num + "\" id=\"slide_" + s.num + "\" style=\"width:90% ; margin:auto\"></div>";
                String slide_name = "slide_" + s.num;

                content = content.append(slide_html);

                Element slide = content.getElementById(slide_name);

                slideNum = s.num;

                if (!s.num.equals("1")) {
                    slide.append(panel_html);

                    panel = content.getElementById("experiment_panel_1");
                }

                String panel_id = "experiment_panel_1_" + s.num; // example experiment_panel_1_(slidenum)
                panel = panel.attr("id", panel_id);

                Element panel_heading = panel.getElementById("experiment_heading_1");
                panel_heading.attr("id", "experiment_heading_1_" + s.num);

                panel_heading.text(s.name);

                panel_body = panel.getElementById("experiment_body_1");
                panel_body.attr("id", "experiment_body_1_" + s.num);

                // Cleaning up the old body to add the new code.
                panel_body = panel_body.empty();

                ArrayList<Component> components = s.components;

                ArrayList<String> slideMandatoryFields = new ArrayList<String>();

                // Flag to see slide change button was added manally through JSON.
                int slideChangeButtonAdded = 0;

                // Loop through the components for THIS slide.
                // N.B. To add any new component, the code has to be written as an if
                // condition inside the following for-loop.
                for (Iterator<Component> it1 = components.iterator(); it1.hasNext(); ) {
                    Component c = it1.next();
                    String name = c.name;
                    String field = c.model_attr;
                    // If-condition for individual components.
                    if (name.equals("question")) {
                        panel_body.append("<span>{{question." + field + "}}</span>");
                    }
                    if (name.equals("textArea")) {
                        panel_body.append("<textarea class=\"form-control col-md-12 textInput\" ng-model=\"question." + field + "\"></textarea><br><br><br><br><br>");
                        // Look for conditions in text-area as well now.
                        ArrayList<Condition> conds = c.conditions;
                        for (Iterator<Condition> i = conds.iterator(); i.hasNext();) {
                            Condition cd = i.next();
                            if (cd.name.equals("minWordCount")) {
                                if (Integer.parseInt(cd.value) > 0) {
                                    System.out.println(field);
                                    textCheckers.add("question." + field + " === undefined || question." + field + " === '' || question." + field + " == {} || RC.checkMinWordCount(question." + field + "," + cd.value + ")");
                                }
                            }
                            else if(cd.name.equals("maxWordCount")) {
                                textCheckers.add("RC.checkMaxWordCount(question."+field+","+cd.value+")");
                            }
                            else if(cd.name.equals("preventCharacters")) {
                                System.out.println(cd.value);
                                textCheckers.add("RC.preventCharacters(question."+field+",\""+cd.value+"\")");
                            }
                        }
                    }
                    if (name.equals("instruction")) {
                        String html = "<h5> ";

                        if (field != null) {
                            if (!field.equals("null")) {
                                html += "{{question." + field + "}}";
                            }
                        }
                        html += "</h5";
                        panel_body.append(html);
                    }
                    if (name.equals("textAnswer")) {
                        String html = "";
                        if (field != null) {
                            if (!field.equals("null")) {
                                html += "<input type=\"text\" ng-model=\"question." + field + "\" class=\"form-control\" ";
                            } else {
                                html += "<text-answer ";
                            }
                        } else {
                            html += "<text-answer ";
                        }
                        ArrayList<Option> opts = c.options;
                        ArrayList<Condition> conds = c.conditions;
                        // Parse options
                        for (Iterator<Option> k = opts.iterator(); k.hasNext();) {
                            Option opt = k.next();
                            if (opt.name.equals("restrictAnswer")) {
                                html += "restrictAnswer=\"question." + opt.value + "\" ";
                            }
                        }
                        for (Iterator<Condition> k = conds.iterator(); k.hasNext();) {
                            Condition cd = k.next();
                            if (cd.name.equals("minWordCount")) {
                                if (Integer.parseInt(cd.value) > 0) {
                                    textCheckers.add("question." + field + " === undefined || question." + field + " === '' || question." + field + " == {} || RC.checkMinWordCount(question." + field + "," + cd.value + ")");
                                }
                            }
                            else if(cd.name.equals("maxWordCount")) {
                                textCheckers.add("RC.checkMaxWordCount(question."+field+","+cd.value+")");
                            }
                            else if(cd.name.equals("preventCharacters")) {
                                System.out.println(cd.value);
                                textCheckers.add("RC.preventCharacters(question."+field+",\""+cd.value+"\")");
                            }
                        }
                        // Close the tag.
                        html += "></text-answer><br><br>";
                        panel_body.append(html);
                    }
                    if (name.equals(("radio"))) {
                        String html = "";

                        if (field != null) {
                            html = "<radio-answer answer=question." + field;
                        } else {
                            html = "<radio-answer";
                        }

                        ArrayList<Option> opts = c.options;
                        for (Iterator<Option> it3 = opts.iterator(); it3.hasNext(); ) {
                            Option op = it3.next();
                            if (op.name.equals("options")) {
                                html = html + " options=\"[";
                                String val = op.value;
                                String[] arrOfVals = val.split(",");
                                for (String a : arrOfVals) {
                                    html = html + "question." + a.trim() + ",";
                                }
                                html = html + "]\"";
                            }
                            else if (op.name.equals("inline")) {
                                html += " inline=\"question." + op.value + "\"";
                            }
                        }
                        html = html + " ></radio-answer";
                        panel_body.append(html);
                    }
                    //checkbox
                    if (name.equals(("checkBox"))) {
                        String html = "";
                        if (field != null) {
                            html = "<checkbox-answer answer=question." + field;
                        } else {
                            html = "<checkbox-answer";
                        }

                        ArrayList<Option> opts = c.options;
                        for (Iterator<Option> it3 = opts.iterator(); it3.hasNext(); ) {
                            Option op = it3.next();
                            if (op.name.equals("options")) {
                                html = html + " options=\"[";
                                String val = op.value;
                                String[] arrOfVals = val.split(",");
                                String lastEl = arrOfVals[arrOfVals.length-1];

                                for (String a : arrOfVals) {
                                    if (a != lastEl){
                                        html = html + "question." + a.trim() + ",";
                                    }
                                    else {
                                        html = html + "question." + a.trim();
                                    }
                                }
                                html = html + "]\"";
                            }
                            // N.B. Descriptions come as comma-separated string. So descriptions
                            // need to be there as separate columns in CSV.
                            else if (op.name.equals("descriptions")) {
                                html = html + " descriptions=\"[";
                                String val = op.value;
                                String[] arrOfVals = val.split(",");
                                String lastEl = arrOfVals[arrOfVals.length-1];
                                for (String a : arrOfVals) {
                                    if (a != lastEl){
                                        html = html + "question." + a.trim() + ",";
                                    }
                                    else {
                                        html = html + "question." + a.trim();
                                    }
                                }
                                html = html + "]\"";
                            }
                            else if (op.name.equals("addNone")) {
                                html += " add-none=\"question." + op.value + "\"";
                            }
                        }
                        html = html + "></checkbox-answer";
                        panel_body.append(html);
                    }
                    if (name.equals(("sliderAnswer"))) {
                        String html = "";

                        if (field != null) {
                            html = "<slider-answer answer=question." + field;
                        } else {
                            html = "<slider-answer";
                        }

                        ArrayList<Option> opts = c.options;
                        for (Iterator<Option> it3 = opts.iterator(); it3.hasNext(); ) {
                            Option op = it3.next();
                            if (op.name.equals("options")) {
                                html = html + " options=\"";
                                String val = op.value;
                                String[] arrOfVals = val.split(",");
                                String lastEl = arrOfVals[arrOfVals.length-1];

                                for (String a : arrOfVals) {
                                    if (a != lastEl){
                                        html = html + "{{question." + a.trim() + "}},";
                                    }
                                    else {
                                        html = html + "{{question." + a.trim() + "}}";
                                    }

                                }
                                html = html + "\"";
                            }
                            else if (op.name.equals("useScale")) {
                                html += " use-scale=\"" + op.value + "\"";
                            }
                        }
                        html = html + " ></slider-answer";
                        panel_body.append(html);
                    }
                    //starAnswer
                    if (name.equals(("starAnswer"))) {
                        String html = "";
                        if (field != null) {
                            html = "<star-answer answer=question." + field;
                        } else {
                            html = "<star-answer";
                        }
                        ArrayList<Option> opts = c.options;
                        for (Iterator<Option> it3 = opts.iterator(); it3.hasNext(); ) {
                            Option op = it3.next();
                            if (op.name.equals("options_wrong")) {
                                html = html + " options=\"[";
                                String val = op.value;
                                String[] arrOfVals = val.split(",");
                                for (String a : arrOfVals) {
                                    html = html + "question." + a.trim() + ",";
                                }
                                html = html + "]\"";
                            }
                            else if (op.name.equals("maxStars")) {
                                // updating max star, now take value from user not csv mapping
                                System.out.println(op.value);
                                html += " max-stars=\"" + op.value + "\"";
                            }
                        }
                        html = html + " ></star-answer";
                        panel_body.append(html);
                    }
                    //fileInput
                    if (name.equals(("fileInput"))) {
                        String html = "";
                        html = "<file-input " ;
                        ArrayList<Option> opts = c.options;
                        for (Iterator<Option> it3 = opts.iterator(); it3.hasNext(); ) {
                            Option op = it3.next();
                            if (op.name.equals("content")) {
                                html = html + " content=\""+ op.value + "\"";
                            }
                            else if (op.name.equals("encoding")) {
                                html += " encoding=\"" + op.value + "\"";
                            }
                        }
                        html = html + " >Load .csv</file-input";
                        panel_body.append(html);
                    }
                    //dragAndDrop
                    if (name.equals(("dragDrop"))) {
                        dragDropPresent = true;
                        String html = "";
                        if (field != null) {
                            if (Integer.parseInt(slideNum) < totalNumSlides) {
                                html = "<drag-and-drop content=\"question\" click=\"RC.switchSlide\" answer=question." + field;
                            }
                            else {
                                html = "<drag-and-drop content=\"question\" click=\"RC.nextQuestion\" answer=question." + field;
                            }
                        } else {
                            if (Integer.parseInt((slideNum)) < totalNumSlides) {
                                html = "<drag-and-drop content=\"question\" click=\"RC.switchSlide\"";
                            }
                            else {
                                html = "<drag-and-drop content=\"question\" click=\"RC.nextQuestion\"";
                            }
                        }

                        ArrayList<Option> opts = c.options;
                        for (Iterator<Option> it3 = opts.iterator(); it3.hasNext(); ) {
                            Option op = it3.next();
                            if (op.name.equals("options_wrong")) {
                                html = html + " options=\"[";
                                String val = op.value;
                                String[] arrOfVals = val.split(",");
                                for (String a : arrOfVals) {
                                    html = html + "question." + a.trim() + ",";
                                }
                                html = html + "]\"";
                            }

                            else if (op.name.equals("click")) {
                                html += " click=\"RC.nextQuestion"+ "\"";
                            }

                            else if (op.name.equals("instructions1")) {
                                html += " instructions1=\"" + op.value + "\"";
                            }

                            else if (op.name.equals("instructions2")) {
                                html += " instructions2=\"" + op.value + "\"";
                            }

                            else if (op.name.equals("connectives")) {
                                html = html + " connectives=\"";
                                String val = op.value;
                                String[] arrOfVals = val.split(",");
                                String lastEl = arrOfVals[arrOfVals.length-1];

                                for (String a : arrOfVals) {

                                    if (a != lastEl){
                                        html = html + "{{question." + a.trim() + "}},";
                                    }
                                    else {
                                        html = html + "{{question." + a.trim() + "}}";
                                    }
                                }
                                html = html + "\"";
                            }

                            else if (op.name.equals("add-connectives-text")) {
                                html += " add-connectives-text=\"{{question." + op.value + "}}\"";
                            }
                            else if (op.name.equals("splitter")) {
                                html += " splitter=\"" + op.value + "\"";
                            }
                            else if (op.name.equals("context1")) {
                                html += " context1=\"question." + op.value + "\"";
                            }
                            else if (op.name.equals("context2")) {
                                html += " context2=\"question." + op.value + "\"";
                            }
                            else if (op.name.equals("sentence1")) {
                                html += " sentence1=\"question." + op.value + "\"";
                            }
                            else if (op.name.equals("sentence2")) {
                                html += " sentence2=\"question." + op.value + "\"";
                            }

                            else if (op.name.equals("htmlInput")) {
                                html += " html-input = \"" + op.value + "\"";
                            }
                            else if (op.name.equals("boxTitle")) {
                                html += " box-title=\"" + op.value + "\"";
                            }
                            else if (op.name.equals("goBackText")) {
                                html += " go-back-text=\"" + op.value + "\"";
                            }
                            else if (op.name.equals("noneStyle")) {
                                html += " none-style=\"" + op.value + "\"";
                            }
                            else if (op.name.equals("defaultConnective")) {
                                html += " default-connectives=\"" + op.value + "\"";
                            }
                            else if (op.name.equals("none-of-these-text")) {
                                html += " none-of-these-text=\"{{question." + op.value + "}}\"";
                            }
                            else if (op.name.equals("allowNoneOfThese")) {
                                html += " allow-none-of-these=\"" + op.value + "\"";
                            }
                            else if (op.name.equals("boxPosition")) {
                                html += " box-position=\"" + op.value + "\"";
                            }
                            else if (op.name.equals("display-style")) {
                                html += " display-style=\"" + op.value + "\"";
                            }
                            else if (op.name.equals("allowNone")) {
                                html += " allow-none=\"" + op.value + "\"";
                            }
                            else if (op.name.equals("none-text")) {
                                html += " none-text=\"{{question." + op.value + "}}\"";
                            }
                            else if (op.name.equals("contentClass")) {
                                html += " content-class=\"" + op.value + "\"";
                            }
                            else if (op.name.equals("add-connectives")) {
                                html += " add-connectives=\"question." + op.value + "\"";
                            }
                            else if (op.name.equals("allowMultiple")) {
                                html += " allow-multiple=\"" + op.value + "\"";
                            }
                            else if (op.name.equals("randomizeConnectives")){
                                html += " randomize-connectives=\"" + op.value + "\"";
                            }
                        }

                        ArrayList<Condition> conds = c.conditions;
                        for (Iterator<Condition> it4 = conds.iterator(); it4.hasNext();) {
                            Condition cd = it4.next();
                        }

                        html = html + " ></drag-and-drop";
                        panel_body.append(html);
                    }
                    ArrayList<Condition> conds = c.conditions;
                    for (Iterator<Condition> it4 = conds.iterator(); it4.hasNext();) {
                        Condition cd = it4.next();
                        if (cd.name.equals("mandatory")) {
                            if (cd.value.toLowerCase().equals("true")) {
                                mandatoryFields.add(c.model_attr);
                                slideMandatoryFields.add(c.model_attr);
                            }
                        }
                    }
                    String slideChangeHtml = "";
                    if (name.equals("nextSlideButton") && !dragDropPresent) {
                        slideChangeButtonAdded = 1;
                        System.out.println("Slide Change Field");
                        System.out.println(field);
                        if (field == null || field.equals("")) {
                            slideChangeHtml = "<button id=next_slide_" + s.num + " type=\"button\" ng-click=\"RC.switchSlide()\" class=\"btn btn-default\" style=\"float:right ; margin-top:20px\"> Next Slide </button>";
                        }
                        else {
                            slideChangeHtml = "<button id=next_slide_" + s.num + " type=\"button\" ng-click=\"RC.switchSlide()\" class=\"btn btn-default\" style=\"float:right ; margin-top:20px\"> " + field + " </button>";
                        }
                        System.out.println(slideChangeHtml);
                        panel_body.append(slideChangeHtml);
                    }
                    // N.B. This is where new code has to be added for any new component that has to be designed.
                    /*
                    NEW COMPONENTS GO HERE!
                     */
                }
                // If this slide is not last slide and slide change button was not added...
                // Add slide change button automatically.
                if (slideChangeButtonAdded == 0 && Integer.parseInt(s.num) < totalNumSlides) {
                    // Adding default text for next slide.
                    panel_body.append("<button id=next_slide_" + s.num + " type=\"button\" ng-click=\"RC.switchSlide()\" class=\"btn btn-default\" style=\"float:right ; margin-top:20px\"> Next Slide </button>");
                    slideChangeButtonAdded = 1;
                }
                // Give disable option to the Slide Change Button.
                if (slideChangeButtonAdded == 1) {
                    String slideDisable = "";
                    for (Iterator<String> j = slideMandatoryFields.iterator(); j.hasNext(); ) {
                        String field = j.next();
                        if (!slideDisable.equals("")) {
                            slideDisable = slideDisable + " || ";
                        }
                        slideDisable = slideDisable + "question." + field + " === undefined || question." + field + " === '' || question." + field + " == {}";
                    }
                    // Now add the textCheckers
                    if(textCheckers.size() > 0) {
                        for (Iterator<String> b = textCheckers.iterator(); b.hasNext();) {
                            if (!slideDisable.equals("")) {
                                slideDisable = slideDisable + " || ";
                            }

                            slideDisable = slideDisable + b.next();

                        }
                    }
                    if (!slideDisable.equals("")) {
                        System.out.println("next_slide_"+s.num);
                        Element slideButton = panel_body.getElementById("next_slide_" + s.num);
                        slideButton.attr("ng-disabled", slideDisable);
                    }
                }
                if(s.num.equals("1")) {
                    slide.append(panel.outerHtml());
                }
            }
            // Finally, add our button back!
            String disable = "";

            for (Iterator<String> j = mandatoryFields.iterator(); j.hasNext();) {
                String field = j.next();
                if (!disable.equals("")) {
                    disable = disable + " || ";
                }
                disable = disable + "question." + field + " === undefined || question." + field + " === '' || question." + field + " == {}";
            }
            // Now add the textCheckers
            if(textCheckers.size() > 0) {
                for (Iterator<String> b = textCheckers.iterator(); b.hasNext();) {
                    if(!disable.equals("")) {
                        disable = disable + " || ";
                    }
                    disable = disable + b.next();
                }
            }
            if (!disable.equals("")) {
//                System.out.println("Modifying submit button disabled option");
//                System.out.println(slideNum);
                button.attr("ng-disabled", disable);
            }
            panel_body = content.getElementById("experiment_body_1_"+slideNum);
            // Do not add the button if drag drop component is there in last slide!
            if (!dragDropPresent) {
                panel_body.append(button.outerHtml());
            }
            // Now write the file in! And we are done!

            Path filePathOut = Paths.get(currentPath.toString(), "app", "views", "ExperimentRendering", ex.name, ex.name + "_render.html");
            File f = new File(filePathOut.toString());
            FileUtils.writeStringToFile(f, doc.outerHtml(), "UTF-8");

            return ok(index.render());
        }
        catch (Exception e) {
            System.out.println(e);
            return internalServerError("Please Report to Admin. Error Occured: " + e);
        }
    }

    /**
     * Renders the experiment type creation interface.
     *
     * @return The rendered experiment type creation interface
     */
    @SubjectPresent
    public Result EditExperimentShowAll() {
        return ok(views.html.EditExperiment.overviewPage.render(lingoturkConfig.getExperimentNames()));
    }

    /**
     * new experiment creation instance for the experiment type specified by {@code name}.
     * First checks if a customized template is available. If that is not the case, fall back
     * to the default template. If no such experiment type exists, return an {@code internalServerError}
     *
     * @param name The name of the experiment type to instantiate
     * @return The customized/fallback tempalte, if available. An internalServerError otherwise.
     */
    @SubjectPresent
    public Result EditExperiment(String name) {
        return ok(views.html.ExperimentCreation.editExperiment.render(name.substring(0, name.length() - "Experiment".length())));
    }

}
