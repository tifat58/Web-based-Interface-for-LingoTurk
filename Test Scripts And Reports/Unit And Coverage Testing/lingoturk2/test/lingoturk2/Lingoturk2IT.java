/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lingoturk2;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ROMEL
 */
public class Lingoturk2IT {
    
    public Lingoturk2IT() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of submitEditExperiment method, of class Lingoturk2.
     */
    @Test
    public void testSubmitEditExperiment() throws Exception {
        //System.out.println("submitEditExperiment");
        String jsonstring = "{\"name\":\"testLingoturkUIPrint\",\"slides\":[{\"num\":1,\"name\":\"Slide 1\",\"components\":[{\"name\":\"question\",\"model_attr\":\"instructionText\",\"options\":[],\"conditions\":[]}]},{\"num\":2,\"name\":\"New Slide\",\"components\":[{\"name\":\"question\",\"model_attr\":\"instructionText\",\"options\":[],\"conditions\":[]},{\"name\":\"sliderAnswer\",\"model_attr\":\"\",\"options\":[],\"conditions\":[]}]},{\"num\":3,\"name\":\"New Slide\",\"components\":[{\"name\":\"question\",\"model_attr\":\"instructionText\",\"options\":[],\"conditions\":[]},{\"name\":\"radio\",\"model_attr\":\"\",\"options\":[{\"name\":\"options\",\"value\":\"op1,op2\"}],\"conditions\":[{\"name\":\"mandatory\",\"value\":\"true\"}]},{\"name\":\"checkBox\",\"model_attr\":\"\",\"options\":[{\"name\":\"options\",\"value\":\"op1,op3\"}],\"conditions\":[{\"name\":\"mandatory\",\"value\":\"true\"},{\"name\":\"allowMultipleSelection\",\"value\":\"false\"}]}]},{\"num\":4,\"name\":\"New Slide\",\"components\":[{\"name\":\"dragDrop\",\"model_attr\":\"\",\"options\":[{\"name\":\"connectives\",\"value\":\"conncectives\"},{\"name\":\"context1\",\"value\":\"context1\"},{\"name\":\"context2\",\"value\":\"context2\"},{\"name\":\"sentence1\",\"value\":\"sentence1\"},{\"name\":\"sentence2\",\"value\":\"sentence2\"},{\"name\":\"content\",\"value\":\"\"},{\"name\":\"htmlInput\",\"value\":\"false\"},{\"name\":\"boxPosition\",\"value\":\"middle\"},{\"name\":\"boxTitle\",\"value\":\"Candidate connectives\"},{\"name\":\"displayStyle\",\"value\":\"default\"},{\"name\":\"allowMultiple\",\"value\":\"false\"},{\"name\":\"instructions1\",\"value\":\"Please drag the best-suited connective into the green target box below.\"},{\"name\":\"instructions2\",\"value\":\"You can now drag one more connective into the box below.\"},{\"name\":\"goBackText\",\"value\":\"go back\"},{\"name\":\"allowNoneOfThese\",\"value\":\"true\"},{\"name\":\"noneOfTheseText\",\"value\":\"none of these\"},{\"name\":\"allowNone\",\"value\":\"false\"},{\"name\":\"allowNoneText\",\"value\":\"no connective\"},{\"name\":\"noneStyle\",\"value\":\"block\"},{\"name\":\"defaultConnective\",\"value\":\"\"},{\"name\":\"contentClass\",\"value\":\"col-md-12\"},{\"name\":\"click\",\"value\":\"RC.nextQuestion\"},{\"name\":\"randomizeConnectives\",\"value\":\"true\"},{\"name\":\"splitter\",\"value\":\",\"}],\"conditions\":[]}]}]}";
        Lingoturk2 instance = new Lingoturk2();
        String expResult = "Successfully checked";
        String result = instance.submitEditExperiment(jsonstring);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of main method, of class Lingoturk2.
     */
    @Test
    public void testMain() {
        //System.out.println("main");
        //String[] args = null;
        //Lingoturk2.main(args);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }
    
}
