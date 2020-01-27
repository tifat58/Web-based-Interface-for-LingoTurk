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

WebUI.openBrowser('')

WebUI.navigateToUrl('http://localhost:9000/login')

WebUI.setText(findTestObject('Object Repository/C9_Slide_OR/Page_Login/input_Username_username'), 'admin')

WebUI.setEncryptedText(findTestObject('Object Repository/C9_Slide_OR/Page_Login/input_Password_password'), 'BR2H/KIz0NBfgccJ+PbLCA==')

WebUI.click(findTestObject('Object Repository/C9_Slide_OR/Page_Login/strong_Let me in'))

WebUI.click(findTestObject('Object Repository/C9_Slide_OR/Page_LINGOturk/b_Edit Experiment'))

WebUI.click(findTestObject('Object Repository/C9_Slide_OR/Page_Edit Experiment/div_SampleTest101'))

WebUI.click(findTestObject('Object Repository/C9_Slide_OR/Page_LINGOturk/i_Slide 1_glyphicon glyphicon-remove tooltipclose'))

WebUI.click(findTestObject('Object Repository/C9_Slide_OR/Page_LINGOturk/button_OK'))

WebUI.click(findTestObject('Object Repository/C9_Slide_OR/Page_LINGOturk/button_Add Slide'))

WebUI.click(findTestObject('Object Repository/C9_Slide_OR/Page_LINGOturk/button_Add Slide'))

WebUI.click(findTestObject('Object Repository/C9_Slide_OR/Page_LINGOturk/span_Text Answer'))

WebUI.click(findTestObject('Object Repository/C9_Slide_OR/Page_LINGOturk/button_Save'))

WebUI.click(findTestObject('Object Repository/C9_Slide_OR/Page_LINGOturk/span_Text Answer'))

WebUI.click(findTestObject('Object Repository/C9_Slide_OR/Page_LINGOturk/button_Save'))

WebUI.click(findTestObject('Object Repository/C9_Slide_OR/Page_LINGOturk/button_Edit'))

WebUI.click(findTestObject('Object Repository/C9_Slide_OR/Page_LINGOturk/button_Add Option'))

WebUI.selectOptionByValue(findTestObject('Object Repository/C9_Slide_OR/Page_LINGOturk/select_-- select option -- restrictAnswerop_62ed0e'), 
    'string:restrictAnswer', true)

WebUI.selectOptionByValue(findTestObject('Object Repository/C9_Slide_OR/Page_LINGOturk/select_-- select csv field -- t1t2t3'), 
    'string:t1', true)

WebUI.click(findTestObject('Object Repository/C9_Slide_OR/Page_LINGOturk/button_Save'))

WebUI.click(findTestObject('Object Repository/C9_Slide_OR/Page_LINGOturk/i_Slide 1_glyphicon glyphicon-remove tooltipclose'))

WebUI.click(findTestObject('Object Repository/C9_Slide_OR/Page_LINGOturk/button_OK'))

WebUI.click(findTestObject('Object Repository/C9_Slide_OR/Page_LINGOturk/span_Done Editing'))

WebUI.click(findTestObject('Object Repository/C9_Slide_OR/Page_LINGOturk/button_OK'))

