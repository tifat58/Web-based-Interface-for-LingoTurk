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

WebUI.setText(findTestObject('Object Repository/C7_Drag_n_Drop/Page_Login/input_Username_username'), 'admin')

WebUI.setEncryptedText(findTestObject('Object Repository/C7_Drag_n_Drop/Page_Login/input_Password_password'), 'BR2H/KIz0NBfgccJ+PbLCA==')

WebUI.click(findTestObject('Object Repository/C7_Drag_n_Drop/Page_Login/button_Let me in'))

WebUI.click(findTestObject('Object Repository/C7_Drag_n_Drop/Page_LINGOturk/div_Edit Experiment'))

WebUI.click(findTestObject('Object Repository/C7_Drag_n_Drop/Page_Edit Experiment/div_SampleTest101'))

WebUI.click(findTestObject('Object Repository/C7_Drag_n_Drop/Page_LINGOturk/span_Drag and Drop'))

WebUI.selectOptionByValue(findTestObject('Object Repository/C7_Drag_n_Drop/Page_LINGOturk/select_-- select csv field -- t1t2t3'), 
    'string:t1', true)

WebUI.selectOptionByValue(findTestObject('Object Repository/C7_Drag_n_Drop/Page_LINGOturk/select_-- select csv field -- t1t2t3_1'), 
    'string:t2', true)

WebUI.selectOptionByValue(findTestObject('Object Repository/C7_Drag_n_Drop/Page_LINGOturk/select_-- select csv field -- t1t2t3_1_2'), 
    'string:t3', true)

WebUI.selectOptionByValue(findTestObject('Object Repository/C7_Drag_n_Drop/Page_LINGOturk/select_-- select csv field -- t1t2t3_1_2_3'), 
    'string:t1', true)

WebUI.selectOptionByValue(findTestObject('Object Repository/C7_Drag_n_Drop/Page_LINGOturk/select_-- select csv field -- t1t2t3_1_2_3_4'), 
    'string:t2', true)

WebUI.setText(findTestObject('Object Repository/C7_Drag_n_Drop/Page_LINGOturk/input_Splitter_splitter'), '2,2')

WebUI.click(findTestObject('Object Repository/C7_Drag_n_Drop/Page_LINGOturk/button_Add Option'))

WebUI.selectOptionByValue(findTestObject('Object Repository/C7_Drag_n_Drop/Page_LINGOturk/select_-- select option name -- connectives_c2d916'), 
    'string:displayStyle', true)

WebUI.selectOptionByValue(findTestObject('Object Repository/C7_Drag_n_Drop/Page_LINGOturk/select_-- select csv field -- t1t2t3_1_2_3_4_5'), 
    'string:t2', true)

WebUI.click(findTestObject('Object Repository/C7_Drag_n_Drop/Page_LINGOturk/button_Save'))

WebUI.click(findTestObject('Object Repository/C7_Drag_n_Drop/Page_LINGOturk/button_Edit'))

WebUI.click(findTestObject('Object Repository/C7_Drag_n_Drop/Page_LINGOturk/button_-'))

WebUI.click(findTestObject('Object Repository/C7_Drag_n_Drop/Page_LINGOturk/button_Save'))

WebUI.closeBrowser()

