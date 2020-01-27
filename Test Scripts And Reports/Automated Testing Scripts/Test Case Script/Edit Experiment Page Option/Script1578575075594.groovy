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

WebUI.setText(findTestObject('Object Repository/Edit Experiment Page Option/Page_Login/input_Username_username'), 'admin')

WebUI.setEncryptedText(findTestObject('Object Repository/Edit Experiment Page Option/Page_Login/input_Password_password'), 
    'BR2H/KIz0NBfgccJ+PbLCA==')

WebUI.click(findTestObject('Object Repository/Edit Experiment Page Option/Page_Login/strong_Let me in'))

WebUI.navigateToUrl('http://localhost:9000/')

WebUI.click(findTestObject('Object Repository/Edit Experiment Page Option/Page_LINGOturk/div_Edit Experiment'))

WebUI.click(findTestObject('Object Repository/Edit Experiment Page Option/Page_Edit Experiment/div_TestSample'))

WebUI.click(findTestObject('Object Repository/Edit Experiment Page Option/Page_LINGOturk/a_About'))

WebUI.click(findTestObject('Object Repository/Edit Experiment Page Option/Page_Contact/a_Home'))

WebUI.closeBrowser()

