
package lingoturk2;
//package controllers;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;





//import javax.inject.Inject;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;



import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import javax.json.*;


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
    // Adding new thing! List of conditions.
    ArrayList<Condition> conditions; // list of conditions.
}




public class Lingoturk2 {

    public String submitEditExperiment(String jsonstring) throws IOException {
        try {
            int componentCounter = 0;
            System.out.println("Here");
            //ObjectMapper mapper = new ObjectMapper();
            ObjectMapper mapper = new ObjectMapper();
            JsonNode json = mapper.readTree(jsonstring);
            //how to do string to json
            //JsonObject body = Json.createReader(new StringReader(jsonstring)).readObject();
            //JsonNode json = request().body().asJson();
            //System.out.println(request().body());
            //String name = json.get("name").asText();
            //Component[] myObjects = mapper.readValue(json.get("component"), Component[].class);
            //Object ex = mapper.treeToValue(json, EditedExperiment.class);
            EditedExperiment ex = new EditedExperiment();
            ex.name = json.get("name").asText() + "Experiment";
            System.out.println("Exp Name: " + ex.name);
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
                            // The following needs to be done only for components other
                            // than the Next Slide button, as that one's field attribute
                            // is the button text.
                            if (!c.name.equals("nextSlideButton")) {
                                try {
                                    c.model_attr = n.get("model_attr").asText();
                                    if (c.model_attr.equals("")) {
                                        componentCounter += 1;
                                        c.model_attr = compName + "Response" + Integer.toString(componentCounter);
                                    }
                                } catch (Exception e) {
                                    // Now we are going to set the model attribute automatically for all components.
                                    // This will remove the need to send it from front-end all the time, which is
                                    // redundant.
                                    componentCounter += 1;
                                    c.model_attr = compName + "Response" + Integer.toString(componentCounter);
                                    //c.model_attr = null;
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
                                        // Create single option object.
                                        Option o = new Option();
                                        o.name = optName;
                                        System.out.println("Option Name: " + optName);
                                        String optVal = node.get("value").asText();
                                        o.value = optVal;
                                        // Add the option to component's option list
                                        c.options.add(o);
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
                                        Condition cd = new Condition();
                                        cd.name = condName;
                                        String condVal = node.get("value").asText();
                                        cd.value = condVal;
                                        c.conditions.add(cd);
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
            //fileNameWhole =
            Path currentPath = Paths.get(System.getProperty("user.dir"));
            //Path filePath = Paths.get(currentPath.toString(), "app", "views", "ExperimentRendering", ex.name, ex.name+"_render.html");
            Path filePath = Paths.get(currentPath.toString(), "templateExperimentRendering.html");
            System.out.println(filePath.toString());

            //Jsoup

            File input = new File(filePath.toString());
            Document doc = Jsoup.parse(input, "UTF-8", "");

            Element content = doc.getElementById("experiment_content");

            Element heading = content.getElementById("experiment_heading_1");
            Element body = content.getElementById("experiment_body_1");
            Element panel = content.getElementById("experiment_panel_1");

            String panel_html = panel.outerHtml();


            content.attr("ng-init", "RC.subslide = 1");

            Element child_panel1 = content.getElementById("experiment_panel_1");

            content = content.empty();

            // Get the button child of body. We need to add that back eventually.
            // TODO: ENSURE THAT THE BUTTON IS THIS ONE. TEMPLATE HAS TWO STYLE OF BUTTONS. MAYBE ADD AN OPTION IN FRONT-END.
            Elements body_button = body.getElementsByAttributeValue("ng-click", "RC.nextQuestion()");
            Element button = body_button.get(0);

            // Clean the whole body
            //body = body.empty();
            // Start the loop
            System.out.println("Now iterate the Exp object");
            Element panel_body = panel.getElementById("experiment_body_1");
            // New List to track all mandatory fields. These need to be added to final Submit button.
            ArrayList<String> mandatoryFields = new ArrayList<String>();
            // Checkers for conditions on text-answer.
            ArrayList<String> textCheckers = new ArrayList<String>();
            for (Iterator<Slide> it = ex.slides.iterator(); it.hasNext(); ) {

                dragDropPresent = false;

                Slide s = it.next();

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
                // Unsure what will go here... For now putting Experiment Name
                panel_heading.text(ex.name);

                panel_body = panel.getElementById("experiment_body_1");
                panel_body.attr("id", "experiment_body_1_" + s.num);

                panel_body = panel_body.empty();

                //System.out.println("************\n"+content.outerHtml()+"\n**************");

                ArrayList<Component> components = s.components;

                ArrayList<String> slideMandatoryFields = new ArrayList<String>();

                int slideChangeButtonAdded = 0;

                for (Iterator<Component> it1 = components.iterator(); it1.hasNext(); ) {
                    Component c = it1.next();
                    String name = c.name;
                    String field = c.model_attr;

                    // TODO: ALSO PARSE AND ADD THE OPTIONS
                    // TODO: ALSO PARSE AND ADD THE CONDITIONS
                    //System.out.println("Component_Name:" + name);
                    // TODO: These HTML bits need to be stored somewhere ideally, and we would use as a lookup!
                    if (name.equals("question")) {
                        panel_body.append("<span>{{question." + field + "}}</span>");
                    }
                    if (name.equals("textArea")) {
                        // <textarea class="form-control textInput" ng-model="question.answer"></textarea>
                        panel_body.append("<textarea class=\"form-control textInput\" ng-model=\"question." + field + "\"></textarea>");
                        // Look for conditions in text-area as well now.
                        ArrayList<Condition> conds = c.conditions;
                        for (Iterator<Condition> i = conds.iterator(); i.hasNext();) {
                            Condition cd = i.next();
                            if (cd.name.equals("minWordCount")) {
                                if (Integer.parseInt(cd.value) > 0) {
                                    textCheckers.add("question." + field + " === undefined || question." + field + " === '' || question." + field + " == {} || RC.checkMinWordCount(question." + field + "," + cd.value + ")");
                                }
//                                else {
//                                    textCheckers.add("RC.checkMinWordCount(question." + field + "," + cd.value + ")");
//                                }
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
                        //panel_body.append(("<h5>This is where some friendly instruction to answer will go!</h5><hr/>"));
                        //panel_body.append("<h5>" )
                        String html = "<h5> ";

                        if (field != null) {
                            if (!field.equals("null")) {
                                //panel_body.append("<text-answer answer=\"question." + field + "\" ");
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
                                //panel_body.append("<text-answer answer=\"question." + field + "\" ");
                                html += "<text-answer answer=\"question." + field + "\" ";
                            } else {
                                //panel_body.append("<input type=\"text\" class=\"form-control textInput\"></input>");
                                //panel_body.append("<text-answer ");
                                html += "<text-answer ";
                            }
                        } else {
                            //panel_body.append("<input type=\"text\" class=\"form-control textInput\"></input>");
                            //panel_body.append("<text-answer ");
                            html += "<text-answer ";
                        }
                        ArrayList<Option> opts = c.options;
                        ArrayList<Condition> conds = c.conditions;
                        // Parse options
                        for (Iterator<Option> k = opts.iterator(); k.hasNext();) {
                            Option opt = k.next();
                            if (opt.name.equals("restrictAnswer")) {
                                //TODO: This needs to be ensured that it is functional. This checks regex.
                                //TODO: See if question.value or {{question.value}} should be sent
                                html += "restrictAnswer=\"question." + opt.value + "\" ";
                            }
                        }
                        // TODO: ADD CONDITION CODE
                        for (Iterator<Condition> k = conds.iterator(); k.hasNext();) {
                            Condition cd = k.next();
                            if (cd.name.equals("minWordCount")) {
                                if (Integer.parseInt(cd.value) > 0) {
                                    textCheckers.add("question." + field + " === undefined || question." + field + " === '' || question." + field + " == {} || RC.checkMinWordCount(question." + field + "," + cd.value + ")");
                                }
//                                else {
//                                    textCheckers.add("RC.checkMinWordCount(question." + field + "," + cd.value + ")");
//                                }
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
                        html += "></text-answer>";
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
                                // THIS MIGHT NEED TO CHANGE!
                                String[] arrOfVals = val.split(",");
                                for (String a : arrOfVals) {
                                    //html = html + "\'" + a.trim() + "\',";
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
                                // THIS MIGHT NEED TO CHANGE!
                                String[] arrOfVals = val.split(",");
                                String lastEl = arrOfVals[arrOfVals.length-1];

                                for (String a : arrOfVals) {
                                    if (a != lastEl){
                                        //html = html + "\'" + a.trim() + "\',";
                                        html = html + "question." + a.trim() + ",";
                                    }
                                    else {
                                        html = html + "question." + a.trim();
                                    }
                                }
                                html = html + "]\"";
                            }
                            else if (op.name.equals("descriptions")) {
                                html = html + " descriptions=\"[";
                                String val = op.value;
                                // THIS MIGHT NEED TO CHANGE!
                                String[] arrOfVals = val.split(",");
                                String lastEl = arrOfVals[arrOfVals.length-1];

                                for (String a : arrOfVals) {
                                    if (a != lastEl){
                                        //html = html + "\'" + a.trim() + "\',";
                                        html = html + "question." + a.trim() + ",";
                                    }
                                    else {
                                        html = html + "question." + a.trim();
                                    }
                                }
                                html = html + "]\"";
                                //html += " descriptions=\"question." + op.value + "\"";
                            }
                            else if (op.name.equals("addNone")) {
                                html += " add-none=\"question." + op.value + "\"";
                            }
                        }
                        html = html + "></checkbox-answer";
                        panel_body.append(html);
                    }

                    //sliderAnswer
                    // TODO: implement option for usescale features in the frontend and handle them in backend.
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
                                // THIS MIGHT NEED TO CHANGE!
                                String[] arrOfVals = val.split(",");
                                String lastEl = arrOfVals[arrOfVals.length-1];

                                for (String a : arrOfVals) {

                                    if (a != lastEl){
                                        //html = html + "\'" + a.trim() + "\',";
                                        html = html + "{{question." + a.trim() + "}},";
                                    }
                                    else {
                                        html = html + "{{question." + a.trim() + "}}";
                                    }

                                }
                                html = html + "\"";
                            }
                            else if (op.name.equals("useScale")) {
                                html += " use-scale=\"question." + op.value + "\"";
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
                                // THIS MIGHT NEED TO CHANGE!
                                String[] arrOfVals = val.split(",");
                                for (String a : arrOfVals) {
                                    //html = html + "\'" + a.trim() + "\',";
                                    html = html + "question." + a.trim() + ",";
                                }
                                html = html + "]\"";
                            }
                            else if (op.name.equals("maxStars")) {
                                html += " max-stars=\"question." + op.value + "\"";
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
                                // THIS MIGHT NEED TO CHANGE!
                                String[] arrOfVals = val.split(",");
                                for (String a : arrOfVals) {
                                    //html = html + "\'" + a.trim() + "\',";
                                    html = html + "question." + a.trim() + ",";
                                }
                                html = html + "]\"";
                            }
                            //else if (op.name.equals("content")) {
                            //    html += " content=\"question"+ "\"";
                            //}

                            else if (op.name.equals("click")) {

                                html += " click=\"RC.nextQuestion"+ "\"";
                            }

                            else if (op.name.equals("instructions1")) {
                                html += " instructions1=\"" + op.value + "\"";
//                                html += " instructions1=\"{{question." + op.value + "}}\"";
                            }

                            else if (op.name.equals("instructions2")) {
                                html += " instructions2=\"" + op.value + "\"";
//                                html += " instructions2=\"{{question." + op.value + "}}\"";
                            }

                            else if (op.name.equals("connectives")) {
                                html = html + " connectives=\"";
                                String val = op.value;
                                // THIS MIGHT NEED TO CHANGE!
                                String[] arrOfVals = val.split(",");
                                String lastEl = arrOfVals[arrOfVals.length-1];

                                for (String a : arrOfVals) {

                                    if (a != lastEl){
                                        //html = html + "\'" + a.trim() + "\',";
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
//                                html += " splitter=\"question." + op.value + "\"";
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
//                                html += " html-input=\"question." + op.value + "\"";
                            }
                            else if (op.name.equals("boxTitle")) {
                                html += " box-title=\"" + op.value + "\"";
//                                html += " box-title=\"question." + op.value + "\"";
                            }
                            else if (op.name.equals("goBackText")) {
                                html += " go-back-text=\"" + op.value + "\"";
//                                html += " go-back-text=\"question." + op.value + "\"";
                            }
                            else if (op.name.equals("noneStyle")) {
                                html += " none-style=\"" + op.value + "\"";
//                                html += " none-style=\"question." + op.value + "\"";
                            }
                            else if (op.name.equals("defaultConnective")) {
                                html += " default-connectives=\"" + op.value + "\"";
//                                html += " default-connective=\"{{question." + op.value + "}}\"";
                            }
                            else if (op.name.equals("none-of-these-text")) {
                                html += " none-of-these-text=\"{{question." + op.value + "}}\"";
                            }
                            else if (op.name.equals("allowNoneOfThese")) {
                                html += " allow-none-of-these=\"" + op.value + "\"";
//                                html += " allow-none-of-these=\"question." + op.value + "\"";
                            }
                            else if (op.name.equals("boxPosition")) {
                                html += " box-position=\"" + op.value + "\"";
//                                html += " box-position=\"question." + op.value + "\"";
                            }
                            else if (op.name.equals("display-style")) {
                                html += " display-style=\"" + op.value + "\"";
//                                html += " display-style=\"question." + op.value + "\"";
                            }
                            else if (op.name.equals("allowNone")) {
                                html += " allow-none=\"" + op.value + "\"";
//                                html += " allow-none=\"question." + op.value + "\"";
                            }
                            else if (op.name.equals("none-text")) {
                                html += " none-text=\"{{question." + op.value + "}}\"";
                            }
                            else if (op.name.equals("contentClass")) {
                                html += " content-class=\"" + op.value + "\"";
//                                html += " content-class=\"question." + op.value + "\"";
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

                            //                           these are commented out because we are now taking allow multiple in options
//                            if (cd.name.equals("randomize-connectives")) {
//                                html += " randomize-connectives="+cd.value.toLowerCase()+"\"";
//                            }

//                            else if (cd.name.equals("allowMultipleSelection")) {
//                                html += " allow-multiple="+cd.value.toLowerCase()+"";
//                            }
                        }

                        html = html + " ></drag-and-drop";
                        panel_body.append(html);
                    }


                    //dragAndDrop
                    // TODO: implement more features in the frontend and handle them in backend.

                    // IMP: CHECKING FOR MANDATORY CONDITION.
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
                        if (field == null || field.equals("")) {
                            slideChangeHtml = "<button id=next_slide_" + s.num + " type=\"button\" ng-click=\"RC.switchSlide()\" class=\"btn btn-default\" style=\"float:right ; margin-top:20px\"> Next Slide </button>";
                        }
                        else {
                            slideChangeHtml = "<button id=next_slide_" + s.num + " type=\"button\" ng-click=\"RC.switchSlide()\" class=\"btn btn-default\" style=\"float:right ; margin-top:20px\"> " + field + " </button>";
                        }
                        System.out.println(slideChangeHtml);
                        panel_body.append(slideChangeHtml);
                    }

                    //slideChangeHtml += slideDisable + "> Next Slide </button>";

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


                //System.out.println("\n$$$$$$$$$$\n"+content.outerHtml()+"\n$$$$$$$$$$");
                if(s.num.equals("1")) {
                    slide.append(panel.outerHtml());
                }
                //System.out.println("\n#########\n"+content.outerHtml()+"\n#########");
            }
            // Finally, add our button back!
            // TODO: MODIFY SUBMIT BUTTON BASED ON MANDATORY/OPTIONAL SETTING.
            String disable = "";

//            // TODO: REMOVE THIS. THIS IS ONLY FOR TESTING!
//            if (mandatoryFields.size() == 0) {
//                mandatoryFields.add("answer");
//            }

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
                System.out.println("Modifying submit button disabled option");
                System.out.println(slideNum);
                button.attr("ng-disabled", disable);
            }
            panel_body = content.getElementById("experiment_body_1_"+slideNum);
            // Do not add the button if drag drop component is there in last slide!
            if (!dragDropPresent) {
                panel_body.append(button.outerHtml());
            }
            // Now write the file in! And we are done!

            //Path filePathOut = Paths.get(currentPath.toString(), "app", "views", "ExperimentRendering", ex.name, ex.name + "_render.html");
            //File f = new File(filePathOut.toString());
            //FileUtils.writeStringToFile(f, doc.outerHtml(), "UTF-8");
            System.out.println(content);
            
            String name = "Successfully checked";

            return name;
        }
        catch (Exception e) {
            String error = "Unsuccessful attempt";
            //System.out.println(error);
            return error;
            //throw e;
            
        
            //return error; 
        }
    }
public static void main(String[] args){
        //Case:1: One Slide, radio button, one condition and one option
        //String jsonString = "{\"name\":\"LanguageGameListener1\",\"slides\":[{\"num\":1,\"components\":[{\"name\":\"radio\",\"model_attr\":\"stimuli\",\"options\":[{\"name\":\"options\",\"value\":\"dc1,dc2,dc3\"}],\"conditions\":[{\"name\":\"mandatory\",\"value\":\"true\"}]}]}]}";
        //Case:2: One Slide, checkbox, 3 options
        //String jsonString = "{\"name\":\"testLingoturkUI2\",\"slides\":[{\"num\":1,\"components\":[{\"name\":\"checkBox\",\"model_attr\":\"Interests\",\"options\":[{\"name\":\"options\",\"value\":\"op1,op2,op3\"}],\"conditions\":[]}]}]}";
        //Case:3: Two Slides, radio button, 2 options, slide answer, 2 options, checkbox, 2 answer  
        //String jsonString = "{\"name\":\"testLingoturkUI3\",\"slides\":[{\"num\":1,\"components\":[{\"name\":\"radio\",\"model_attr\":\"Radio 1\",\"options\":[{\"name\":\"options\",\"value\":\"op1,op2\"}],\"conditions\":[]},{\"name\":\"sliderAnswer\",\"model_attr\":\"Slider 1\",\"options\":[{\"name\":\"options\",\"value\":\"op1,op2\"}],\"conditions\":[]}]},{\"num\":2,\"components\":[{\"name\":\"checkBox\",\"model_attr\":\"Checkbox 1\",\"options\":[{\"name\":\"options\",\"value\":\"op1,op2\"}],\"conditions\":[]},{\"name\":\"starAnswer\",\"model_attr\":\"Star 1\",\"options\":[{\"name\":\"maxStars\",\"value\":\"stars\"}],\"conditions\":[]}]}]}";
        //Case:4: Invalid string
        //String jsonString = "{\"name\":\"testLingoturkUI3\",\"slides\":[{\"n\":1,\"components\":[{\"ne\":\"rio\",\"model_attr\":\"Radio 1\",\"options\":[{\"name\":\"options\",\"value\":\"op1,op2\"}],\"conditions\":[]},{\"na\":\"sliderAnswer\",\"model_attr\":\"Slider 1\",\"options\":[{\"name\":\"options\",\"value\":\"op1,op2\"}],\"conditions\":[]}]},{\"num\":2,\"components\":[{\"name\":\"checkBox\",\"model_attr\":\"Checkbox 1\",\"options\":[{\"name\":\"options\",\"value\":\"op1,op2\"}],\"conditions\":[]},{\"name\":\"starAnswer\",\"model_attr\":\"Star 1\",\"options\":[{\"name\":\"maxStars\",\"value\":\"stars\"}],\"conditions\":[]}]}]}";
        //Case:5:
        //String jsonString = "{\"name\":\"testLingoturkUIPrint\",\"slides\":[{\"num\":1,\"name\":\"Slide 1\",\"components\":[{\"name\":\"textArea\",\"model_attr\":\"textAnswer\",\"options\":[],\"conditions\":[{\"name\":\"minWordCount\",\"value\":50},{\"name\":\"maxWordCount\",\"value\":500},{\"name\":\"preventCharacters\",\"value\":\"-!\"}]}]},{\"num\":2,\"name\":\"New Slide\",\"components\":[{\"name\":\"starAnswer\",\"model_attr\":\"starAnswer1\",\"options\":[],\"conditions\":[{\"name\":\"mandatory\",\"value\":\"true\"}]},{\"name\":\"dragDrop\",\"model_attr\":\"dragDrop\",\"options\":[{\"name\":\"connectives\",\"value\":\"conncectives,conncectives\"},{\"name\":\"context1\",\"value\":\"context1\"},{\"name\":\"context2\",\"value\":\"context2\"},{\"name\":\"sentence1\",\"value\":\"sentence1\"},{\"name\":\"sentence2\",\"value\":\"sentence2\"},{\"name\":\"content\",\"value\":\"\"},{\"name\":\"htmlInput\",\"value\":\"false\"},{\"name\":\"boxPosition\",\"value\":\"middle\"},{\"name\":\"boxTitle\",\"value\":\"Candidate connectives\"},{\"name\":\"displayStyle\",\"value\":\"default\"},{\"name\":\"allowMultiple\",\"value\":\"false\"},{\"name\":\"instructions1\",\"value\":\"Please drag the best-suited connective into the green target box below.\"},{\"name\":\"instructions2\",\"value\":\"You can now drag one more connective into the box below.\"},{\"name\":\"goBackText\",\"value\":\"go back\"},{\"name\":\"allowNoneOfThese\",\"value\":\"true\"},{\"name\":\"noneOfTheseText\",\"value\":\"none of these\"},{\"name\":\"allowNone\",\"value\":\"false\"},{\"name\":\"allowNoneText\",\"value\":\"no connective\"},{\"name\":\"noneStyle\",\"value\":\"block\"},{\"name\":\"defaultConnective\",\"value\":\"\"},{\"name\":\"contentClass\",\"value\":\"col-md-12\"},{\"name\":\"click\",\"value\":\"RC.nextQuestion\"},{\"name\":\"randomizeConnectives\",\"value\":\"true\"},{\"name\":\"splitter\",\"value\":\",\"}],\"conditions\":[]}]}]}";
        //Case:6:
        //String jsonString = "{\"name\":\"testLingoturkUIPrint\",\"slides\":[{\"num\":1,\"name\":\"Slide 1\",\"components\":[{\"name\":\"question\",\"model_attr\":\"instructionText\",\"options\":[],\"conditions\":[]},{\"name\":\"radio\",\"model_attr\":\"radio1\",\"options\":[{\"name\":\"options\",\"value\":\"op1,op2\"}],\"conditions\":[{\"name\":\"mandatory\",\"value\":\"true\"}]}]},{\"num\":2,\"name\":\"Slide 2\",\"components\":[{\"name\":\"question\",\"model_attr\":\"instructionText\",\"options\":[],\"conditions\":[]},{\"name\":\"checkBox\",\"model_attr\":\"checkbox\",\"options\":[{\"name\":\"options\",\"value\":\"op1,op2,op3\"},{\"name\":\"descriptions\",\"value\":\"checkboxDescription\"}],\"conditions\":[{\"name\":\"mandatory\",\"value\":\"true\"},{\"name\":\"allowMultipleSelection\",\"value\":\"false\"}]}]},{\"num\":3,\"name\":\"Slide 3\",\"components\":[{\"name\":\"question\",\"model_attr\":\"descriptionText\",\"options\":[],\"conditions\":[]},{\"name\":\"sliderAnswer\",\"model_attr\":\"slider\",\"options\":[],\"conditions\":[]}]},{\"num\":4,\"name\":\"Slide 4\",\"components\":[{\"name\":\"question\",\"model_attr\":\"instructionText\",\"options\":[],\"conditions\":[]},{\"name\":\"dragDrop\",\"model_attr\":\"dd1\",\"options\":[{\"name\":\"connectives\",\"value\":\"conncectives\"},{\"name\":\"context1\",\"value\":\"context1\"},{\"name\":\"context2\",\"value\":\"context2\"},{\"name\":\"sentence1\",\"value\":\"sentence1\"},{\"name\":\"sentence2\",\"value\":\"sentence2\"},{\"name\":\"content\",\"value\":\"\"},{\"name\":\"htmlInput\",\"value\":\"false\"},{\"name\":\"boxPosition\",\"value\":\"middle\"},{\"name\":\"boxTitle\",\"value\":\"Candidate connectives\"},{\"name\":\"displayStyle\",\"value\":\"default\"},{\"name\":\"allowMultiple\",\"value\":\"false\"},{\"name\":\"instructions1\",\"value\":\"Please drag the best-suited connective into the green target box below.\"},{\"name\":\"instructions2\",\"value\":\"You can now drag one more connective into the box below.\"},{\"name\":\"goBackText\",\"value\":\"go back\"},{\"name\":\"allowNoneOfThese\",\"value\":\"true\"},{\"name\":\"noneOfTheseText\",\"value\":\"none of these\"},{\"name\":\"allowNone\",\"value\":\"false\"},{\"name\":\"allowNoneText\",\"value\":\"no connective\"},{\"name\":\"noneStyle\",\"value\":\"block\"},{\"name\":\"defaultConnective\",\"value\":\"\"},{\"name\":\"contentClass\",\"value\":\"col-md-12\"},{\"name\":\"click\",\"value\":\"RC.nextQuestion\"},{\"name\":\"randomizeConnectives\",\"value\":\"true\"},{\"name\":\"splitter\",\"value\":\",\"}],\"conditions\":[]},{\"name\":\"nextSlideButton\",\"model_attr\":\"next\",\"options\":[],\"conditions\":[]}]},{\"num\":5,\"name\":\"Slide 5\",\"components\":[{\"name\":\"question\",\"model_attr\":\"questionText\",\"options\":[],\"conditions\":[]},{\"name\":\"textAnswer\",\"model_attr\":\"text4\",\"options\":[],\"conditions\":[{\"name\":\"minWordCount\",\"value\":50},{\"name\":\"maxWordCount\",\"value\":500},{\"name\":\"preventCharacters\",\"value\":\".,\"}]},{\"name\":\"nextSlideButton\",\"model_attr\":\"next 2\",\"options\":[],\"conditions\":[]}]}]}";
        //Case:7:
        //String jsonString = "{\"name\":\"testLingoturkUIPrint\",\"slides\":[{\"num\":1,\"name\":\"Slide 1\",\"components\":[{\"name\":\"question\",\"model_attr\":\"descriptionText\",\"options\":[],\"conditions\":[]},{\"name\":\"dragDrop\",\"model_attr\":\"\",\"options\":[{\"name\":\"connectives\",\"value\":\"conncectives\"},{\"name\":\"context1\",\"value\":\"context1\"},{\"name\":\"context2\",\"value\":\"context2\"},{\"name\":\"sentence1\",\"value\":\"sentence1\"},{\"name\":\"sentence2\",\"value\":\"sentence2\"},{\"name\":\"content\",\"value\":\"\"},{\"name\":\"htmlInput\",\"value\":\"false\"},{\"name\":\"boxPosition\",\"value\":\"middle\"},{\"name\":\"boxTitle\",\"value\":\"Candidate connectives\"},{\"name\":\"displayStyle\",\"value\":\"default\"},{\"name\":\"allowMultiple\",\"value\":\"false\"},{\"name\":\"instructions1\",\"value\":\"Please drag the best-suited connective into the green target box below.\"},{\"name\":\"instructions2\",\"value\":\"You can now drag one more connective into the box below.\"},{\"name\":\"goBackText\",\"value\":\"go back\"},{\"name\":\"allowNoneOfThese\",\"value\":\"true\"},{\"name\":\"noneOfTheseText\",\"value\":\"none of these\"},{\"name\":\"allowNone\",\"value\":\"false\"},{\"name\":\"allowNoneText\",\"value\":\"no connective\"},{\"name\":\"noneStyle\",\"value\":\"block\"},{\"name\":\"defaultConnective\",\"value\":\"\"},{\"name\":\"contentClass\",\"value\":\"col-md-12\"},{\"name\":\"click\",\"value\":\"RC.nextQuestion\"},{\"name\":\"randomizeConnectives\",\"value\":\"true\"},{\"name\":\"splitter\",\"value\":\",\"}],\"conditions\":[]}]},{\"num\":2,\"name\":\"New Slide\",\"components\":[{\"name\":\"question\",\"model_attr\":\"instructionText\",\"options\":[],\"conditions\":[]},{\"name\":\"dragDrop\",\"model_attr\":\"\",\"options\":[{\"name\":\"connectives\",\"value\":\"conncectives\"},{\"name\":\"context1\",\"value\":\"context1\"},{\"name\":\"context2\",\"value\":\"context2\"},{\"name\":\"sentence1\",\"value\":\"sentence1\"},{\"name\":\"sentence2\",\"value\":\"sentence2\"},{\"name\":\"content\",\"value\":\"\"},{\"name\":\"htmlInput\",\"value\":\"false\"},{\"name\":\"boxPosition\",\"value\":\"middle\"},{\"name\":\"boxTitle\",\"value\":\"Candidate connectives\"},{\"name\":\"displayStyle\",\"value\":\"default\"},{\"name\":\"allowMultiple\",\"value\":\"false\"},{\"name\":\"instructions1\",\"value\":\"Please drag the best-suited connective into the green target box below.\"},{\"name\":\"instructions2\",\"value\":\"You can now drag one more connective into the box below.\"},{\"name\":\"goBackText\",\"value\":\"go back\"},{\"name\":\"allowNoneOfThese\",\"value\":\"true\"},{\"name\":\"noneOfTheseText\",\"value\":\"none of these\"},{\"name\":\"allowNone\",\"value\":\"false\"},{\"name\":\"allowNoneText\",\"value\":\"no connective\"},{\"name\":\"noneStyle\",\"value\":\"block\"},{\"name\":\"defaultConnective\",\"value\":\"\"},{\"name\":\"contentClass\",\"value\":\"col-md-12\"},{\"name\":\"click\",\"value\":\"RC.nextQuestion\"},{\"name\":\"randomizeConnectives\",\"value\":\"true\"},{\"name\":\"splitter\",\"value\":\",\"}],\"conditions\":[]},{\"name\":\"question\",\"model_attr\":\"instructionText\",\"options\":[],\"conditions\":[]},{\"name\":\"dragDrop\",\"model_attr\":\"\",\"options\":[{\"name\":\"connectives\",\"value\":\"conncectives\"},{\"name\":\"context1\",\"value\":\"context1\"},{\"name\":\"context2\",\"value\":\"context2\"},{\"name\":\"sentence1\",\"value\":\"sentence1\"},{\"name\":\"sentence2\",\"value\":\"sentence2\"},{\"name\":\"content\",\"value\":\"\"},{\"name\":\"htmlInput\",\"value\":\"false\"},{\"name\":\"boxPosition\",\"value\":\"middle\"},{\"name\":\"boxTitle\",\"value\":\"Candidate connectives\"},{\"name\":\"displayStyle\",\"value\":\"default\"},{\"name\":\"allowMultiple\",\"value\":\"false\"},{\"name\":\"instructions1\",\"value\":\"Please drag the best-suited connective into the green target box below.\"},{\"name\":\"instructions2\",\"value\":\"You can now drag one more connective into the box below.\"},{\"name\":\"goBackText\",\"value\":\"go back\"},{\"name\":\"allowNoneOfThese\",\"value\":\"true\"},{\"name\":\"noneOfTheseText\",\"value\":\"none of these\"},{\"name\":\"allowNone\",\"value\":\"false\"},{\"name\":\"allowNoneText\",\"value\":\"no connective\"},{\"name\":\"noneStyle\",\"value\":\"block\"},{\"name\":\"defaultConnective\",\"value\":\"\"},{\"name\":\"contentClass\",\"value\":\"col-md-12\"},{\"name\":\"click\",\"value\":\"RC.nextQuestion\"},{\"name\":\"randomizeConnectives\",\"value\":\"true\"},{\"name\":\"splitter\",\"value\":\",\"}],\"conditions\":[]}]}]}";
        //Case:8:
        //String jsonString = "{\"name\":\"testLingoturkUIPrint\",\"slides\":[{\"num\":1,\"name\":\"Slide 1\",\"components\":[{\"name\":\"question\",\"model_attr\":\"instructionText\",\"options\":[],\"conditions\":[]}]},{\"num\":2,\"name\":\"New Slide\",\"components\":[{\"name\":\"question\",\"model_attr\":\"instructionText\",\"options\":[],\"conditions\":[]},{\"name\":\"sliderAnswer\",\"model_attr\":\"\",\"options\":[],\"conditions\":[]}]},{\"num\":3,\"name\":\"New Slide\",\"components\":[{\"name\":\"question\",\"model_attr\":\"instructionText\",\"options\":[],\"conditions\":[]},{\"name\":\"radio\",\"model_attr\":\"\",\"options\":[{\"name\":\"options\",\"value\":\"op1,op2\"}],\"conditions\":[{\"name\":\"mandatory\",\"value\":\"true\"}]},{\"name\":\"checkBox\",\"model_attr\":\"\",\"options\":[{\"name\":\"options\",\"value\":\"op1,op3\"}],\"conditions\":[{\"name\":\"mandatory\",\"value\":\"true\"},{\"name\":\"allowMultipleSelection\",\"value\":\"false\"}]}]},{\"num\":4,\"name\":\"New Slide\",\"components\":[{\"name\":\"dragDrop\",\"model_attr\":\"\",\"options\":[{\"name\":\"connectives\",\"value\":\"conncectives\"},{\"name\":\"context1\",\"value\":\"context1\"},{\"name\":\"context2\",\"value\":\"context2\"},{\"name\":\"sentence1\",\"value\":\"sentence1\"},{\"name\":\"sentence2\",\"value\":\"sentence2\"},{\"name\":\"content\",\"value\":\"\"},{\"name\":\"htmlInput\",\"value\":\"false\"},{\"name\":\"boxPosition\",\"value\":\"middle\"},{\"name\":\"boxTitle\",\"value\":\"Candidate connectives\"},{\"name\":\"displayStyle\",\"value\":\"default\"},{\"name\":\"allowMultiple\",\"value\":\"false\"},{\"name\":\"instructions1\",\"value\":\"Please drag the best-suited connective into the green target box below.\"},{\"name\":\"instructions2\",\"value\":\"You can now drag one more connective into the box below.\"},{\"name\":\"goBackText\",\"value\":\"go back\"},{\"name\":\"allowNoneOfThese\",\"value\":\"true\"},{\"name\":\"noneOfTheseText\",\"value\":\"none of these\"},{\"name\":\"allowNone\",\"value\":\"false\"},{\"name\":\"allowNoneText\",\"value\":\"no connective\"},{\"name\":\"noneStyle\",\"value\":\"block\"},{\"name\":\"defaultConnective\",\"value\":\"\"},{\"name\":\"contentClass\",\"value\":\"col-md-12\"},{\"name\":\"click\",\"value\":\"RC.nextQuestion\"},{\"name\":\"randomizeConnectives\",\"value\":\"true\"},{\"name\":\"splitter\",\"value\":\",\"}],\"conditions\":[]}]}]}";
        //Case:9:
        String jsonString = "{\"name\":\"testLingoturkUIPrint\",\"slides\":[{\"num\":1,\"name\":\"Slide 1\",\"components\":[{\"name\":\"question\",\"model_attr\":\"instructionText\",\"options\":[],\"conditions\":[]},{\"name\":\"starAnswer\",\"model_attr\":\"\",\"options\":[],\"conditions\":[{\"name\":\"mandatory\",\"value\":\"true\"}]}]},{\"num\":2,\"name\":\"New Slide\",\"components\":[{\"name\":\"question\",\"model_attr\":\"instructionText\",\"options\":[],\"conditions\":[]},{\"name\":\"sliderAnswer\",\"model_attr\":\"\",\"options\":[],\"conditions\":[]}]},{\"num\":3,\"name\":\"New Slide\",\"components\":[{\"name\":\"question\",\"model_attr\":\"descriptionText\",\"options\":[],\"conditions\":[]},{\"name\":\"dragDrop\",\"model_attr\":\"\",\"options\":[{\"name\":\"connectives\",\"value\":\"conncectives\"},{\"name\":\"context1\",\"value\":\"context1\"},{\"name\":\"context2\",\"value\":\"context2\"},{\"name\":\"sentence1\",\"value\":\"sentence1\"},{\"name\":\"sentence2\",\"value\":\"sentence2\"},{\"name\":\"content\",\"value\":\"\"},{\"name\":\"htmlInput\",\"value\":\"false\"},{\"name\":\"boxPosition\",\"value\":\"middle\"},{\"name\":\"boxTitle\",\"value\":\"Candidate connectives\"},{\"name\":\"displayStyle\",\"value\":\"default\"},{\"name\":\"allowMultiple\",\"value\":\"false\"},{\"name\":\"instructions1\",\"value\":\"Please drag the best-suited connective into the green target box below.\"},{\"name\":\"instructions2\",\"value\":\"You can now drag one more connective into the box below.\"},{\"name\":\"goBackText\",\"value\":\"go back\"},{\"name\":\"allowNoneOfThese\",\"value\":\"true\"},{\"name\":\"noneOfTheseText\",\"value\":\"none of these\"},{\"name\":\"allowNone\",\"value\":\"false\"},{\"name\":\"allowNoneText\",\"value\":\"no connective\"},{\"name\":\"noneStyle\",\"value\":\"block\"},{\"name\":\"defaultConnective\",\"value\":\"\"},{\"name\":\"contentClass\",\"value\":\"col-md-12\"},{\"name\":\"click\",\"value\":\"RC.nextQuestion\"},{\"name\":\"randomizeConnectives\",\"value\":\"true\"},{\"name\":\"splitter\",\"value\":\",\"}],\"conditions\":[]}]}]}";
        //Case:10:
        //String jsonString = "";
        //Case:11:
        //String jsonString = "";
        //Case:12:
        //String jsonString = "";
        //Case:13:
        //String jsonString = "";
        //Case:14:
        //String jsonString = "";
        //Case:15:
        //String jsonString = "";
        //Case:16:
        //String jsonString = "";
        //Case:17:
        //String jsonString = "";
        
        
        Lingoturk2 lingobaba = new Lingoturk2();
        try{
            String ret = lingobaba.submitEditExperiment(jsonString);
            System.out.println(ret);
        }
        catch (Exception e){
            System.out.println(e);
        }
        }
    }
    
    

