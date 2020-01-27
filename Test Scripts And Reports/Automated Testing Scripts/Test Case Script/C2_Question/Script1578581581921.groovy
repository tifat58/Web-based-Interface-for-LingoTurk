import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject
import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys

WebUI.openBrowser('http://localhost:9000/')

WebUI.setText(findTestObject('Object Repository/C2_Question_OR/Page_Login/input_Username_username'), 'admin')

WebUI.setEncryptedText(findTestObject('Object Repository/C2_Question_OR/Page_Login/input_Password_password'), 'BR2H/KIz0NBfgccJ+PbLCA==')

WebUI.sendKeys(findTestObject('Object Repository/C2_Question_OR/Page_Login/input_Password_password'), Keys.chord(Keys.ENTER))

WebUI.click(findTestObject('Object Repository/C2_Question_OR/Page_LINGOturk/div_Edit Experiment'))

WebUI.click(findTestObject('Object Repository/C2_Question_OR/Page_Edit Experiment/div_ChoiceAndComment'))

WebUI.click(findTestObject('Object Repository/C2_Question_OR/Page_LINGOturk/span_Question'))

WebUI.selectOptionByValue(findTestObject('Object Repository/C2_Question_OR/Page_LINGOturk/select_-- select csv field -- sentence'), 
    'string:sentence', true)

WebUI.click(findTestObject('Object Repository/C2_Question_OR/Page_LINGOturk/button_Add Option'))

WebUI.selectOptionByValue(findTestObject('Object Repository/C2_Question_OR/Page_LINGOturk/select_-- select option name -- restrictAns_55f4fb'), 
    'string:restrictAnswer', true)

WebUI.selectOptionByValue(findTestObject('Object Repository/C2_Question_OR/Page_LINGOturk/select_-- select csv field -- sentence_1'), 
    'string:sentence', true)

WebUI.click(findTestObject('Object Repository/C2_Question_OR/Page_LINGOturk/button_Add Option'))

WebUI.selectOptionByValue(findTestObject('Object Repository/C2_Question_OR/Page_LINGOturk/select_-- select option name -- restrictAns_55f4fb'), 
    'string:restrictAnswer', true)

WebUI.selectOptionByValue(findTestObject('Object Repository/C2_Question_OR/Page_LINGOturk/select_-- select csv field -- sentence_1'), 
    'string:sentence', true)

WebUI.click(findTestObject('Object Repository/C2_Question_OR/Page_LINGOturk/button_Add Condition'))

WebUI.selectOptionByValue(findTestObject('Object Repository/C2_Question_OR/Page_LINGOturk/select_-- select option -- mandatoryallowMu_2f4d0b'), 
    'string:mandatory', true)

WebUI.selectOptionByValue(findTestObject('Object Repository/C2_Question_OR/Page_LINGOturk/select_-- select csv field -- truefalse'), 
    'string:false', true)

WebUI.click(findTestObject('Object Repository/C2_Question_OR/Page_LINGOturk/button_Save'))

WebUI.click(findTestObject('Object Repository/C2_Question_OR/Page_LINGOturk/span_Text Answer'))

WebUI.click(findTestObject('Object Repository/C2_Question_OR/Page_LINGOturk/button_Close'))

WebUI.click(findTestObject('Object Repository/C2_Question_OR/Page_LINGOturk/span_Question'))

WebUI.selectOptionByValue(findTestObject('Object Repository/C2_Question_OR/Page_LINGOturk/select_-- select csv field -- sentence'), 
    'string:sentence', true)

WebUI.click(findTestObject('Object Repository/C2_Question_OR/Page_LINGOturk/button_Add Option'))

WebUI.selectOptionByValue(findTestObject('Object Repository/C2_Question_OR/Page_LINGOturk/select_-- select option name -- restrictAns_55f4fb'), 
    'string:options', true)

WebUI.selectOptionByValue(findTestObject('Object Repository/C2_Question_OR/Page_LINGOturk/select_-- select csv field -- sentence_1'), 
    'string:sentence', true)

WebUI.click(findTestObject('Object Repository/C2_Question_OR/Page_LINGOturk/button_Add Option'))

WebUI.selectOptionByValue(findTestObject('Object Repository/C2_Question_OR/Page_LINGOturk/select_-- select option name -- restrictAns_55f4fb'), 
    'string:options', true)

WebUI.selectOptionByValue(findTestObject('Object Repository/C2_Question_OR/Page_LINGOturk/select_-- select csv field -- sentence_1'), 
    'string:sentence', true)

WebUI.click(findTestObject('Object Repository/C2_Question_OR/Page_LINGOturk/button_Add Condition'))

WebUI.selectOptionByValue(findTestObject('Object Repository/C2_Question_OR/Page_LINGOturk/select_-- select option -- mandatoryallowMu_2f4d0b'), 
    'string:mandatory', true)

WebUI.click(findTestObject('Object Repository/C2_Question_OR/Page_LINGOturk/button_Save'))

WebUI.click(findTestObject('Object Repository/C2_Question_OR/Page_LINGOturk/button_Delete'))

WebUI.click(findTestObject('Object Repository/C2_Question_OR/Page_LINGOturk/button_OK'))

WebUI.click(findTestObject('Object Repository/C2_Question_OR/Page_LINGOturk/span_Done Editing'))

WebUI.click(findTestObject('Object Repository/C2_Question_OR/Page_LINGOturk/button_OK'))

