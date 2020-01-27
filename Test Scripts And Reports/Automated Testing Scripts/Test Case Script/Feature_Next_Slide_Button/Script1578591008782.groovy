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

WebUI.navigateToUrl('http://localhost:9000/login')

WebUI.setText(findTestObject('Object Repository/Feature_Next_Slide_Button_OR/Page_Login/input_Username_username'), 'admin')

WebUI.setEncryptedText(findTestObject('Object Repository/Feature_Next_Slide_Button_OR/Page_Login/input_Password_password'), 
    'BR2H/KIz0NBfgccJ+PbLCA==')

WebUI.click(findTestObject('Object Repository/Feature_Next_Slide_Button_OR/Page_Login/span_Password_glyphicon glyphicon-log-in'))

WebUI.click(findTestObject('Object Repository/Feature_Next_Slide_Button_OR/Page_LINGOturk/div_Edit Experiment'))

WebUI.click(findTestObject('Object Repository/Feature_Next_Slide_Button_OR/Page_Edit Experiment/div_TestSample'))

WebUI.click(findTestObject('Object Repository/Feature_Next_Slide_Button_OR/Page_LINGOturk/span_Next Slide Button'))

WebUI.setText(findTestObject('Object Repository/Feature_Next_Slide_Button_OR/Page_LINGOturk/input_nextSlideButton _componentName'), 
    'Next Slide Button')

WebUI.click(findTestObject('Object Repository/Feature_Next_Slide_Button_OR/Page_LINGOturk/button_Save'))

WebUI.click(findTestObject('Object Repository/Feature_Next_Slide_Button_OR/Page_LINGOturk/button_Add Slide'))

WebUI.click(findTestObject('Object Repository/Feature_Next_Slide_Button_OR/Page_LINGOturk/button_Add Slide'))

WebUI.click(findTestObject('Object Repository/Feature_Next_Slide_Button_OR/Page_LINGOturk/a_Next Slide Button'))

WebUI.selectOptionByValue(findTestObject('Object Repository/Feature_Next_Slide_Button_OR/Page_LINGOturk/select_123'), 'number:2', 
    true)

WebUI.setText(findTestObject('Object Repository/Feature_Next_Slide_Button_OR/Page_LINGOturk/input_nextSlideButton _componentName'), 
    'Next Slide Button 2')

WebUI.click(findTestObject('Object Repository/Feature_Next_Slide_Button_OR/Page_LINGOturk/button_Save'))

WebUI.click(findTestObject('Object Repository/Feature_Next_Slide_Button_OR/Page_LINGOturk/i_Slide 3_glyphicon glyphicon-remove tooltipclose'))

WebUI.click(findTestObject('Object Repository/Feature_Next_Slide_Button_OR/Page_LINGOturk/button_OK'))

WebUI.click(findTestObject('Object Repository/Feature_Next_Slide_Button_OR/Page_LINGOturk/span_Done Editing'))

WebUI.closeBrowser()

