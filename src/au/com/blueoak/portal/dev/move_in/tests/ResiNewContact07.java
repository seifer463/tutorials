package au.com.blueoak.portal.dev.move_in.tests;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.util.Calendar;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.testng.SkipException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import au.com.blueoak.portal.dev.move_in.MoveInDevBase;
import au.com.blueoak.portal.pageObjects.move_in.AcceptanceMoveIn;
import au.com.blueoak.portal.pageObjects.move_in.AccountDetailsMoveIn;
import au.com.blueoak.portal.pageObjects.move_in.AdditionalContactMoveIn;
import au.com.blueoak.portal.pageObjects.move_in.AdditionalNoteMoveIn;
import au.com.blueoak.portal.pageObjects.move_in.ConcessionMoveIn;
import au.com.blueoak.portal.pageObjects.move_in.DirectDebitMoveIn;
import au.com.blueoak.portal.pageObjects.move_in.MainAccountContactMoveIn;
import au.com.blueoak.portal.pageObjects.move_in.ManagerHolidayLettingMoveIn;
import au.com.blueoak.portal.pageObjects.move_in.PostalAddressMoveIn;
import au.com.blueoak.portal.pageObjects.move_in.SupplyDetailsMoveIn;
import au.com.blueoak.portal.utility.AccessS3BucketWithVfs;
import au.com.blueoak.portal.utility.BrowserLocalSessionStorage;

public class ResiNewContact07 extends MoveInDevBase {

	/**
	 * Initialize the page objects factory
	 */
	SupplyDetailsMoveIn supplydetailsmovein;
	AccountDetailsMoveIn accountdetailsmovein;
	MainAccountContactMoveIn mainaccountcontactmovein;
	AdditionalContactMoveIn additionalcontactmovein;
	PostalAddressMoveIn postaladdressmovein;
	ConcessionMoveIn concessionmovein;
	ManagerHolidayLettingMoveIn managerholidaylettingmovein;
	DirectDebitMoveIn directdebitmovein;
	AdditionalNoteMoveIn additionalnotemovein;
	AcceptanceMoveIn acceptancemovein;
	AccessS3BucketWithVfs s3Access;

	/**
	 * Store the name of the class for logging
	 */
	String className;

	/** 
	 * 
	 * */
	String tenantMoveInDate;

	/** 
	 * 
	 * */
	String propManMoveInDate;

	/** 
	 * 
	 * */
	String propManMoveInDateCRM;

	/** 
	 * 
	 * */
	String dateOfBirthMainContact;

	/**
	 * 
	 *  */
	String dateOfBirthAddCont1;

	/**
	 * This is the source ID of the portal session
	 *
	 */
	String sourceID;

	/**
	 * The ID of the online request created
	 */
	String onlineReqId;

	@BeforeClass
	public void beforeClass() {

		// get the current class for logging
		this.className = getTestClassExecuting();
		logTestClassStart(className);

		s3Access = new AccessS3BucketWithVfs(getAwsAccessKeyId(), getAwsSecretAccessKey());

		// upload the correct portal_config.json we are testing
		uploadMoveInConfig(s3Access, "11\\", "portal_config.json");

		// upload the custom language files we are going to use
		uploadMoveInCustomLangFile(s3Access, "01\\", "custom_en.json");

		// let's access the portal we are testing with
		if (getPortalType().equals("standalone") && getPopulateDataMethod().equals("manual")) {
			accessPortal(getStandaloneUrlMoveIn(), true);
			loadStandaloneMoveInPortal(false);
		} else if (getPortalType().equals("embedded") && getPopulateDataMethod().equals("manual")) {
			accessPortal(getEmbeddedUrlMoveIn(), true);
			loadEmbeddedMoveInPortal(false, false);
		} else if (getPortalType().equals("embedded") && getPopulateDataMethod().equals("thirdPartyPrefill")) {
			accessPortal(getThirdPartyPrefillUrlMoveIn(), true);
			// enter the values of the prefill
			String initialDate = getCurrentDateWithTimeZone(MELBOURNE_TIME_ZONE, MONTH_DATE_YEAR_FORMAT_SLASH);
			populate3rdPartyPrefill("502-514", "Burwood", StreetTypesEnum.HWY, "Vermont South",
					AustralianStatesEnum.VIC, "3133", AccountTypesEnum.RESIDENTIAL, AccountCategoryEnum.OWNER,
					initialDate, true);
		} else if (getPortalType().equals("standalone") && getPopulateDataMethod().equals("urlPrefill")) {
			String moveInDate = getSpecificDateWithTimeZone(MELBOURNE_TIME_ZONE, -20, DB_DATE_FORMAT);
			String urlPrefill = constructUrlPrefill(PortalTypesEnum.STANDALONE, "config=portal_config.json",
					"&move_in_date=", moveInDate, "&tenancy_type=", "&tenancy_number=",
					"&tenancy_street_number=502-514", "&tenancy_street_name=Burwood", "&tenancy_street_type=",
					StreetTypesEnum.HWY.name(), "&tenancy_suburb=Vermont South", "&tenancy_postcode=3133",
					"&tenancy_state=", AustralianStatesEnum.VIC.getLabelText(), "&account_type=",
					AccountTypesEnum.RESIDENTIAL.name(), "&business_number=", getProp("test_data_valid_acn2"),
					"&business_trading_name=My Cloud", "&mobile_number=&invalid_value",
					"&business_hour_phone=&invalid_value", "&after_hour_phone=&invalid_value",
					"&email_address=email#testing.com",
					"&extra_data={\"Community\":\"Arc By Crown\",\"CTS\":\"\",\"Service Fee\":\" \",\"Electricity\":{\"Elecrate\":\"22.37 c/kWh (inc GST)\",\"ElecSupply\":\"88 c/day (inc GST)\",\"ElecCom\":\"\"},\"Cooktop\":{\"Gasrate\":\"27.5 c/day (inc GST)\",\"GasComments\":\"\"},\"HW\":{\"HWrate\":\"0.176 c/ltr (inc GST)\",\"HWSupply\":\"49.5 c/day (inc GST)\",\"ThermalCom\":\"\"}}");
			accessPortal(urlPrefill, true);
			loadStandaloneMoveInPortal(false);
		} else if (getPortalType().equals("embedded") && getPopulateDataMethod().equals("urlPrefill")) {
			String moveInDate = getSpecificDateWithTimeZone(MELBOURNE_TIME_ZONE, -20, DB_DATE_FORMAT);
			String urlPrefill = constructUrlPrefill(PortalTypesEnum.EMBEDDED, "config=portal_config.json",
					"&move_in_date=", moveInDate, "&tenancy_type=", "&tenancy_number=",
					"&tenancy_street_number=502-514", "&tenancy_street_name=Burwood", "&tenancy_street_type=",
					StreetTypesEnum.HWY.name(), "&tenancy_suburb=Vermont South", "&tenancy_postcode=3133",
					"&tenancy_state=", AustralianStatesEnum.VIC.getLabelText(), "&account_type=",
					AccountTypesEnum.RESIDENTIAL.name(), "&business_number=", getProp("test_data_valid_acn2"),
					"&business_trading_name=My Cloud", "&mobile_number=&invalid_value",
					"&business_hour_phone=&invalid_value", "&after_hour_phone=&invalid_value",
					"&email_address=email#testing.com",
					"&extra_data={\"Community\":\"Arc By Crown\",\"CTS\":\"\",\"Service Fee\":\" \",\"Electricity\":{\"Elecrate\":\"22.37 c/kWh (inc GST)\",\"ElecSupply\":\"88 c/day (inc GST)\",\"ElecCom\":\"\"},\"Cooktop\":{\"Gasrate\":\"27.5 c/day (inc GST)\",\"GasComments\":\"\"},\"HW\":{\"HWrate\":\"0.176 c/ltr (inc GST)\",\"HWSupply\":\"49.5 c/day (inc GST)\",\"ThermalCom\":\"\"}}");
			accessPortal(urlPrefill, true);
			loadEmbeddedMoveInPortal(false, false);
		} else {
			throw new SkipException(concatStrings(
					"Verify your test run parameters as it does not match any known combination, skipping test class ",
					this.className));
		}
	}

	@AfterClass
	public void afterClass() {

		deleteMoveInGlobalLangFiles(s3Access);

		saveProp();
		logTestClassEnd(className);
	}

	@BeforeMethod
	public void beforeMethod() {

		// let's initialize the page objects
		supplydetailsmovein = new SupplyDetailsMoveIn(driver);
		accountdetailsmovein = new AccountDetailsMoveIn(driver);
		mainaccountcontactmovein = new MainAccountContactMoveIn(driver);
		additionalcontactmovein = new AdditionalContactMoveIn(driver);
		postaladdressmovein = new PostalAddressMoveIn(driver);
		concessionmovein = new ConcessionMoveIn(driver);
		managerholidaylettingmovein = new ManagerHolidayLettingMoveIn(driver);
		directdebitmovein = new DirectDebitMoveIn(driver);
		additionalnotemovein = new AdditionalNoteMoveIn(driver);
		acceptancemovein = new AcceptanceMoveIn(driver);
	}

	@Test(priority = 1)
	public void verifySupplyDetails() {

		// let's switch to the Move-In Iframe
		// if it's embedded
		embeddedMoveInSwitchFrame(1);
		// make sure that the elements are now displayed
		waitUntilElementIsDisplayed(supplydetailsmovein.header, PORTAL_ELEMENT_WAIT_TIMEOUT,
				PORTAL_IMPLICIT_WAIT_TIMEOUT);

		// initialize the Soft Assert
		SoftAssert softAssertion = new SoftAssert();

		// here we just uploaded a custom_en.json custom language file
		// however custom language file is turned off in the config
		// so we just verify that it picked up the global en.json language file
		softAssertion.assertEquals(getDisplayedText(supplydetailsmovein.lblMovingInHeader, true), "I am a:",
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertEquals(getDisplayedText(supplydetailsmovein.lblTenant, true), "Tenant",
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertEquals(getDisplayedText(supplydetailsmovein.lblOwner, true), "Owner",
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertEquals(getDisplayedText(supplydetailsmovein.lblPropManager, true),
				"Property Manager or Letting Agent", assertionErrorMsg(getLineNumber()));
		// verify the radio buttons are not selected
		softAssertion.assertFalse(isElementTicked(supplydetailsmovein.tenant, 0), assertionErrorMsg(getLineNumber()));
		if (getPortalType().equals("standalone") && getPopulateDataMethod().equals("manual")
				|| getPortalType().equals("embedded") && getPopulateDataMethod().equals("manual")) {
			softAssertion.assertFalse(isElementTicked(supplydetailsmovein.owner, 0),
					assertionErrorMsg(getLineNumber()));
		} else if (getPortalType().equals("embedded") && getPopulateDataMethod().equals("thirdPartyPrefill")) {
			softAssertion.assertTrue(isElementTicked(supplydetailsmovein.owner, 0), assertionErrorMsg(getLineNumber()));
		} else if (getPortalType().equals("embedded") && getPopulateDataMethod().equals("urlPrefill")
				|| getPortalType().equals("standalone") && getPopulateDataMethod().equals("urlPrefill")) {
			softAssertion.assertFalse(isElementTicked(supplydetailsmovein.owner, 0),
					assertionErrorMsg(getLineNumber()));
			setImplicitWait(0);
			softAssertion.assertFalse(isElementExists(supplydetailsmovein.moveInDateOwnerList),
					assertionErrorMsg(getLineNumber()));
			setImplicitWait(PORTAL_IMPLICIT_WAIT_TIMEOUT);
		} else {
			fail(concatStrings("Verify the assertion(s) for this configuration. Portal type '", getPortalType(),
					"' and Populate Data method '", getPopulateDataMethod(), "'"));
		}
		softAssertion.assertFalse(isElementTicked(supplydetailsmovein.propManager, 0),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(isElementTicked(supplydetailsmovein.medCoolingYes, 0),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(isElementTicked(supplydetailsmovein.medCoolingNo, 0),
				assertionErrorMsg(getLineNumber()));
		// verify fields are not in error state
		softAssertion.assertFalse(isElementInError(supplydetailsmovein.tenant, 5, 0),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(isElementInError(supplydetailsmovein.owner, 5, 0),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(isElementInError(supplydetailsmovein.propManager, 5, 0),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(isElementInError(supplydetailsmovein.supplyAddComplexName, 5, 0),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(isElementInError(supplydetailsmovein.supplyAddTenancyType, 5, 0),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(isElementInError(supplydetailsmovein.supplyAddTenancyNum, 5, 0),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(isElementInError(supplydetailsmovein.supplyAddStreetNum, 5, 0),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(isElementInError(supplydetailsmovein.supplyAddStreetName, 5, 0),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(isElementInError(supplydetailsmovein.supplyAddStreetType, 5, 0),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(isElementInError(supplydetailsmovein.supplyAddCity, 5, 0),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(isElementInError(supplydetailsmovein.supplyAddState, 5, 0),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(isElementInError(supplydetailsmovein.supplyAddPostcode, 5, 0),
				assertionErrorMsg(getLineNumber()));
		supplydetailsmovein = new SupplyDetailsMoveIn(driver, 0);
		softAssertion.assertFalse(isElementExists(supplydetailsmovein.supplyAddSearchList),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(isElementExists(supplydetailsmovein.lblSupplyConnectedHeaderList),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(isElementExists(supplydetailsmovein.lblSupplyConnectedIntroList),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(isElementExists(supplydetailsmovein.lblSupplyConnectedQuestionList),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(isElementExists(supplydetailsmovein.supplyConnectedList),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(isElementExists(supplydetailsmovein.supplyDisconnectedList),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(isElementExists(supplydetailsmovein.supplyUnknownList),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(isElementExists(supplydetailsmovein.lifeSupYesList),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(isElementExists(supplydetailsmovein.lifeSupNoList),
				assertionErrorMsg(getLineNumber()));
		setImplicitWait(PORTAL_IMPLICIT_WAIT_TIMEOUT);
		// verify that the life support introduction is not displayed
		String lifeSupIntro = getDisplayedText(supplydetailsmovein.lblLifeSupIntro, false);
		softAssertion.assertTrue(StringUtils.isBlank(lifeSupIntro), assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(isElementInError(supplydetailsmovein.medCoolingYes, 5, 0),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(isElementInError(supplydetailsmovein.medCoolingNo, 5, 0),
				assertionErrorMsg(getLineNumber()));
		// verify all assertions
		softAssertion.assertAll();

		if (getPortalType().equals("standalone") && getPopulateDataMethod().equals("manual")
				|| getPortalType().equals("embedded") && getPopulateDataMethod().equals("manual")
				|| getPortalType().equals("embedded") && getPopulateDataMethod().equals("urlPrefill")
				|| getPortalType().equals("standalone") && getPopulateDataMethod().equals("urlPrefill")) {
			clickElementAction(supplydetailsmovein.owner);

			softAssertion.assertTrue(StringUtils.isBlank(getDisplayedValue(supplydetailsmovein.moveInDateOwner, false)),
					assertionErrorMsg(getLineNumber()));
			softAssertion.assertFalse(isElementInError(supplydetailsmovein.moveInDateOwner, 5, 0),
					assertionErrorMsg(getLineNumber()));
			// verify all assertions
			softAssertion.assertAll();
		} else if (getPortalType().equals("embedded") && getPopulateDataMethod().equals("thirdPartyPrefill")) {
			clearDateField(supplydetailsmovein.moveInDateOwner);
		} else {
			fail(concatStrings("Verify the assertion(s) for this configuration. Portal type '", getPortalType(),
					"' and Populate Data method '", getPopulateDataMethod(), "'"));
		}
		String future4Days = getSpecificDateWithTimeZone(MELBOURNE_TIME_ZONE, 4, DATE_MONTH_YEAR_FORMAT_SLASH);
		clickElementAction(supplydetailsmovein.moveInDateOwner);
		pauseSeleniumExecution(1000);
		supplydetailsmovein.moveInDateOwner.sendKeys(future4Days, Keys.TAB);
		// initial click on the same field to dismiss the calendar
		// because the Keys.TAB on selenium did not dismiss the calendar
		clickElementAction(supplydetailsmovein.ownerSettleYes);
		clickElementAction(supplydetailsmovein.ownerSettleYes);
		clickElementAction(supplydetailsmovein.ownerPropManHolidayNo);

		if (getPortalType().equals("standalone") && getPopulateDataMethod().equals("manual")
				|| getPortalType().equals("embedded") && getPopulateDataMethod().equals("manual")) {
			supplydetailsmovein.supplyAddStreetNum.sendKeys("502-514");
			supplydetailsmovein.supplyAddStreetName.sendKeys("Burwood");
			supplydetailsmovein.supplyAddStreetType.sendKeys("Highway", Keys.TAB);
			supplydetailsmovein.supplyAddCity.sendKeys("Vermont South");
			supplydetailsmovein.supplyAddState.sendKeys("Victoria", Keys.TAB);
			supplydetailsmovein.supplyAddPostcode.sendKeys("3133");
		}

		// verify fields are populated correctly
		String complexName = getDisplayedValue(supplydetailsmovein.supplyAddComplexName, false);
		String tenancyType = getDisplayedValue(supplydetailsmovein.supplyAddTenancyType, false);
		String tenancyNum = getDisplayedValue(supplydetailsmovein.supplyAddTenancyNum, false);
		String stNum = getDisplayedValue(supplydetailsmovein.supplyAddStreetNum, false);
		String stName = getDisplayedValue(supplydetailsmovein.supplyAddStreetName, false);
		String stType = getDisplayedValue(supplydetailsmovein.supplyAddStreetType, false);
		String city = getDisplayedValue(supplydetailsmovein.supplyAddCity, false);
		String state = getDisplayedValue(supplydetailsmovein.supplyAddState, false);
		String postcode = getDisplayedValue(supplydetailsmovein.supplyAddPostcode, false);
		if (getPortalType().equals("standalone") && getPopulateDataMethod().equals("manual")
				|| getPortalType().equals("embedded") && getPopulateDataMethod().equals("manual")
				|| getPortalType().equals("embedded") && getPopulateDataMethod().equals("urlPrefill")
				|| getPortalType().equals("standalone") && getPopulateDataMethod().equals("urlPrefill")) {
			softAssertion.assertTrue(StringUtils.isBlank(complexName), assertionErrorMsg(getLineNumber()));
		} else if (getPortalType().equals("embedded") && getPopulateDataMethod().equals("thirdPartyPrefill")) {
			clickElementAction(supplydetailsmovein.supplyAddComplexName);
			deleteAllTextFromField();
		} else {
			fail(concatStrings("Verify the assertion(s) for this configuration. Portal type '", getPortalType(),
					"' and Populate Data method '", getPopulateDataMethod(), "'"));
		}
		softAssertion.assertTrue(StringUtils.isBlank(tenancyType), assertionErrorMsg(getLineNumber()));
		softAssertion.assertTrue(StringUtils.isBlank(tenancyNum), assertionErrorMsg(getLineNumber()));
		softAssertion.assertEquals(stNum, "502-514", assertionErrorMsg(getLineNumber()));
		softAssertion.assertEquals(stName, "Burwood", assertionErrorMsg(getLineNumber()));
		softAssertion.assertEquals(stType, "Highway", assertionErrorMsg(getLineNumber()));
		softAssertion.assertEquals(city, "Vermont South", assertionErrorMsg(getLineNumber()));
		softAssertion.assertEquals(state, "Victoria", assertionErrorMsg(getLineNumber()));
		softAssertion.assertEquals(postcode, "3133", assertionErrorMsg(getLineNumber()));
		supplydetailsmovein.supplyAddTenancyType.sendKeys("Lot", Keys.TAB);
		supplydetailsmovein.supplyAddTenancyNum.sendKeys("51");

		clickElementAction(supplydetailsmovein.medCoolingYes);
		// verify that the upload section is displayed
		// and not in error state
		softAssertion.assertTrue(isElementDisplayed(supplydetailsmovein.dragAndDropArea, 0),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(isElementInError(supplydetailsmovein.dragAndDropArea, 0, 3),
				assertionErrorMsg(getLineNumber()));
		// verify all assertions
		softAssertion.assertAll();

		clickElementAction(supplydetailsmovein.next);
		pauseSeleniumExecution(1000);
		// verify that the attachment is required
		softAssertion.assertTrue(isElementDisplayed(supplydetailsmovein.dragAndDropArea, 0),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertTrue(isElementInError(supplydetailsmovein.dragAndDropArea, 0, 3),
				assertionErrorMsg(getLineNumber()));
		// verify all assertions
		softAssertion.assertAll();

		// upload life support and medical cooling files
		uploadLifeSupMedCoolingFiles(ARTIFACTS_DIR, "Sprin't 02 Story 'Board.pdf", "typing jim carrey.gif");
		// wait for the files to display in the upload area
		// and also in the S3 bucket
		waitForFilesToBeUploaded(PORTAL_FILE_UPLOAD_WAIT_TIMEOUT);
		// check if the file(s) is/are already uploaded in the S3 bucket
		if (System.getenv("DELETE_PRESIGN_BUCKET") != null
				&& System.getenv("DELETE_PRESIGN_BUCKET").toLowerCase().equals("yes")) {
			int actualSize = s3Access.getNumOfObjectsInABucket(S3_PORTAL_PRESIGN_BUCKET_NAME);
			List<String> objectIds = s3Access.getObjectIdsInABucket(S3_PORTAL_PRESIGN_BUCKET_NAME);
			logDebugMessage(concatStrings(this.className, " actualSize in the S3 bucket for Supply Details ",
					S3_PORTAL_PRESIGN_BUCKET_NAME, " is <", Integer.toString(actualSize),
					"> and attachments ID's is/are -> ", objectIds.toString()));
		}
		String dragAndDropText = getDisplayedText(supplydetailsmovein.dragAndDropText, true);
		String lifeSupMedCoolingUploadArea = getDisplayedText(supplydetailsmovein.dragAndDropArea, true);
		// verify all files were uploaded
		softAssertion.assertEquals(concatStrings(dragAndDropText, " ", lifeSupMedCoolingUploadArea),
				"cloud_upload Drag-and-drop file here or click to browse for file to upload medical certificate associated with your life support requirement Sprin't 02 Story 'Board .pdf 0.4 MB File uploaded successfully typing jim carrey .gif 0.5 MB File uploaded successfully",
				assertionErrorMsg(getLineNumber()));
		// verify all assertions
		softAssertion.assertAll();

		clickElementAction(supplydetailsmovein.next);
		pauseSeleniumExecution(1000);
		// verify we are in the next section
		softAssertion.assertTrue(isElementDisplayed(accountdetailsmovein.residential, 0),
				"We're not yet in the Account Details section");
		// verify all assertions
		softAssertion.assertAll();
	}

	/** 
	 * 
	 * */
	@Test(priority = 2, dependsOnMethods = { "verifySupplyDetails" })
	public void verifyAccountDetails() {

		// let's switch to the Move-In Iframe
		embeddedMoveInSwitchFrame(1);

		// initialize the Soft Assert
		SoftAssert softAssertion = new SoftAssert();

		if (getPortalType().equals("standalone") && getPopulateDataMethod().equals("manual")
				|| getPortalType().equals("embedded") && getPopulateDataMethod().equals("manual")) {
			// verify radio buttons not ticked
			softAssertion.assertFalse(isElementTicked(accountdetailsmovein.residential, 0),
					assertionErrorMsg(getLineNumber()));
			softAssertion.assertFalse(isElementTicked(accountdetailsmovein.commercial, 0),
					assertionErrorMsg(getLineNumber()));
			// verify fields not in error state
			softAssertion.assertFalse(isElementInError(accountdetailsmovein.residential, 5, 0),
					assertionErrorMsg(getLineNumber()));
			softAssertion.assertFalse(isElementInError(accountdetailsmovein.commercial, 5, 0),
					assertionErrorMsg(getLineNumber()));
			// verify all assertions
			softAssertion.assertAll();

			clickElementAction(accountdetailsmovein.residential);
		} else if (getPortalType().equals("embedded") && getPopulateDataMethod().equals("thirdPartyPrefill")
				|| getPortalType().equals("embedded") && getPopulateDataMethod().equals("urlPrefill")
				|| getPortalType().equals("standalone") && getPopulateDataMethod().equals("urlPrefill")) {
			// verify radio buttons not ticked
			softAssertion.assertTrue(isElementTicked(accountdetailsmovein.residential, 0),
					assertionErrorMsg(getLineNumber()));
			softAssertion.assertFalse(isElementTicked(accountdetailsmovein.commercial, 0),
					assertionErrorMsg(getLineNumber()));
			// verify fields are editable
			softAssertion.assertTrue(isElementEnabled(accountdetailsmovein.residential, 0),
					assertionErrorMsg(getLineNumber()));
			softAssertion.assertTrue(isElementEnabled(accountdetailsmovein.commercial, 0),
					assertionErrorMsg(getLineNumber()));
			// verify all assertions
			softAssertion.assertAll();
		} else {
			fail(concatStrings("Verify the assertion(s) for this configuration. Portal type '", getPortalType(),
					"' and Populate Data method '", getPopulateDataMethod(), "'"));
		}

		clickElementAction(mainaccountcontactmovein.header);
		pauseSeleniumExecution(1000);
		softAssertion.assertTrue(isElementDisplayed(mainaccountcontactmovein.firstName, 0),
				"We are not yet in the Main Account Contact section");
		// verify all assertions
		softAssertion.assertAll();
	}

	/** 
	 * 
	 * */
	@Test(priority = 3, dependsOnMethods = { "verifyAccountDetails" })
	public void verifyMainContact() {

		// let's switch to the Move-In Iframe
		embeddedMoveInSwitchFrame(1);

		// initialize the Soft Assert
		SoftAssert softAssertion = new SoftAssert();

		// verify that radio button not ticked by default
		softAssertion.assertFalse(isElementTicked(mainaccountcontactmovein.driversLicence, 0),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(isElementTicked(mainaccountcontactmovein.passport, 0),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(isElementTicked(mainaccountcontactmovein.medicareCard, 0),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(isElementTicked(mainaccountcontactmovein.provideNone, 0),
				assertionErrorMsg(getLineNumber()));
		// verify fields are not in error state
		softAssertion.assertFalse(isElementInError(mainaccountcontactmovein.firstName, 5, 0),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(isElementInError(mainaccountcontactmovein.lastName, 5, 0),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(isElementInError(mainaccountcontactmovein.dateOfBirth, 5, 0),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(isElementInError(mainaccountcontactmovein.driversLicence, 5, 0),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(isElementInError(mainaccountcontactmovein.passport, 5, 0),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(isElementInError(mainaccountcontactmovein.medicareCard, 5, 0),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(isElementInError(mainaccountcontactmovein.provideNone, 5, 0),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(isElementInError(mainaccountcontactmovein.billsPostal, 0, 3),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(isElementInError(mainaccountcontactmovein.billsEmail, 0, 3),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(isElementInError(mainaccountcontactmovein.billsSMS, 0, 3),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(isElementInError(mainaccountcontactmovein.acctnotifAndRemindersPostal, 0, 3),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(isElementInError(mainaccountcontactmovein.acctnotifAndRemindersEmail, 0, 3),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(isElementInError(mainaccountcontactmovein.acctnotifAndRemindersSMS, 0, 3),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(isElementInError(mainaccountcontactmovein.marketingComPostal, 0, 3),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(isElementInError(mainaccountcontactmovein.marketingComEmail, 0, 3),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(isElementInError(mainaccountcontactmovein.marketingComSMS, 0, 3),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(isElementInError(mainaccountcontactmovein.emailAddress, 5, 0),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(isElementInError(mainaccountcontactmovein.mobilePhone, 5, 0),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(isElementInError(mainaccountcontactmovein.businessPhone, 5, 0),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(isElementInError(mainaccountcontactmovein.afterhoursPhone, 5, 0),
				assertionErrorMsg(getLineNumber()));
		mainaccountcontactmovein = new MainAccountContactMoveIn(driver, 0);
		softAssertion.assertFalse(isElementExists(mainaccountcontactmovein.contactSecretCodeList),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(isElementExists(mainaccountcontactmovein.addAnotherContactList),
				assertionErrorMsg(getLineNumber()));
		setImplicitWait(PORTAL_IMPLICIT_WAIT_TIMEOUT);
		// verify all assertions
		softAssertion.assertAll();

		// verify url prefill
		if (getPortalType().equals("embedded") && getPopulateDataMethod().equals("urlPrefill")
				|| getPortalType().equals("standalone") && getPopulateDataMethod().equals("urlPrefill")) {
			softAssertion.assertEquals(getDisplayedValue(mainaccountcontactmovein.emailAddress, false),
					"email#testing.com", assertionErrorMsg(getLineNumber()));
			softAssertion.assertTrue(
					StringUtils.isBlank(getDisplayedValue(mainaccountcontactmovein.mobilePhone, false)),
					assertionErrorMsg(getLineNumber()));
			softAssertion.assertTrue(
					StringUtils.isBlank(getDisplayedValue(mainaccountcontactmovein.businessPhone, false)),
					assertionErrorMsg(getLineNumber()));
			softAssertion.assertTrue(
					StringUtils.isBlank(getDisplayedValue(mainaccountcontactmovein.afterhoursPhone, false)),
					assertionErrorMsg(getLineNumber()));
		}

		clickElementAction(additionalcontactmovein.addCont1Header);
		pauseSeleniumExecution(1000);
		// verify that radio button not ticked by default
		softAssertion.assertFalse(isElementTicked(mainaccountcontactmovein.driversLicence, 0),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(isElementTicked(mainaccountcontactmovein.passport, 0),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(isElementTicked(mainaccountcontactmovein.medicareCard, 0),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(isElementTicked(mainaccountcontactmovein.provideNone, 0),
				assertionErrorMsg(getLineNumber()));
		// verify fields are in error state
		softAssertion.assertTrue(isElementInError(mainaccountcontactmovein.firstName, 5, 0),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertTrue(isElementInError(mainaccountcontactmovein.lastName, 5, 0),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(isElementInError(mainaccountcontactmovein.dateOfBirth, 5, 0),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertTrue(isElementInError(mainaccountcontactmovein.driversLicence, 5, 0),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertTrue(isElementInError(mainaccountcontactmovein.passport, 5, 0),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertTrue(isElementInError(mainaccountcontactmovein.medicareCard, 5, 0),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertTrue(isElementInError(mainaccountcontactmovein.provideNone, 5, 0),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(isElementInError(mainaccountcontactmovein.billsPostal, 0, 3),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(isElementInError(mainaccountcontactmovein.billsEmail, 0, 3),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(isElementInError(mainaccountcontactmovein.billsSMS, 0, 3),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(isElementInError(mainaccountcontactmovein.acctnotifAndRemindersPostal, 0, 3),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(isElementInError(mainaccountcontactmovein.acctnotifAndRemindersEmail, 0, 3),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(isElementInError(mainaccountcontactmovein.acctnotifAndRemindersSMS, 0, 3),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(isElementInError(mainaccountcontactmovein.marketingComPostal, 0, 3),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(isElementInError(mainaccountcontactmovein.marketingComEmail, 0, 3),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(isElementInError(mainaccountcontactmovein.marketingComSMS, 0, 3),
				assertionErrorMsg(getLineNumber()));
		if (getPortalType().equals("standalone") && getPopulateDataMethod().equals("manual")
				|| getPortalType().equals("embedded") && getPopulateDataMethod().equals("manual")
				|| getPortalType().equals("embedded") && getPopulateDataMethod().equals("thirdPartyPrefill")) {
			softAssertion.assertFalse(isElementInError(mainaccountcontactmovein.emailAddress, 5, 0),
					assertionErrorMsg(getLineNumber()));
		} else if (getPortalType().equals("embedded") && getPopulateDataMethod().equals("urlPrefill")
				|| getPortalType().equals("standalone") && getPopulateDataMethod().equals("urlPrefill")) {
			softAssertion.assertTrue(isElementInError(mainaccountcontactmovein.emailAddress, 5, 0),
					assertionErrorMsg(getLineNumber()));
		} else {
			fail(concatStrings("Verify the assertion(s) for this configuration. Portal type '", getPortalType(),
					"' and Populate Data method '", getPopulateDataMethod(), "'"));
		}
		softAssertion.assertFalse(isElementInError(mainaccountcontactmovein.mobilePhone, 5, 0),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(isElementInError(mainaccountcontactmovein.businessPhone, 5, 0),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(isElementInError(mainaccountcontactmovein.afterhoursPhone, 5, 0),
				assertionErrorMsg(getLineNumber()));
		mainaccountcontactmovein = new MainAccountContactMoveIn(driver, 0);
		softAssertion.assertFalse(isElementExists(mainaccountcontactmovein.contactSecretCodeList),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(isElementExists(mainaccountcontactmovein.addAnotherContactList),
				assertionErrorMsg(getLineNumber()));
		setImplicitWait(PORTAL_IMPLICIT_WAIT_TIMEOUT);
		// verify all assertions
		softAssertion.assertAll();

		if (getPortalType().equals("embedded") && getPopulateDataMethod().equals("urlPrefill")
				|| getPortalType().equals("standalone") && getPopulateDataMethod().equals("urlPrefill")) {
			clickElementAction(mainaccountcontactmovein.emailAddress);
			deleteAllTextFromField();
		}

		clickElementAction(mainaccountcontactmovein.passport);
		mainaccountcontactmovein.passportNumber.sendKeys("00Abc9800");
		mainaccountcontactmovein.passportCountry.sendKeys("�land Islands", Keys.TAB);

		clickElementAction(additionalcontactmovein.addCont1Header);
		pauseSeleniumExecution(1000);
		// verify fields in error state
		softAssertion.assertTrue(isElementInError(mainaccountcontactmovein.firstName, 5, 0),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertTrue(isElementInError(mainaccountcontactmovein.lastName, 5, 0),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(isElementInError(mainaccountcontactmovein.dateOfBirth, 5, 0),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(isElementInError(mainaccountcontactmovein.driversLicence, 5, 0),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(isElementInError(mainaccountcontactmovein.passport, 5, 0),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(isElementInError(mainaccountcontactmovein.medicareCard, 5, 0),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(isElementInError(mainaccountcontactmovein.provideNone, 5, 0),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(isElementInError(mainaccountcontactmovein.billsPostal, 0, 3),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(isElementInError(mainaccountcontactmovein.billsEmail, 0, 3),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(isElementInError(mainaccountcontactmovein.billsSMS, 0, 3),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(isElementInError(mainaccountcontactmovein.acctnotifAndRemindersPostal, 0, 3),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(isElementInError(mainaccountcontactmovein.acctnotifAndRemindersEmail, 0, 3),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(isElementInError(mainaccountcontactmovein.acctnotifAndRemindersSMS, 0, 3),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(isElementInError(mainaccountcontactmovein.marketingComPostal, 0, 3),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(isElementInError(mainaccountcontactmovein.marketingComEmail, 0, 3),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(isElementInError(mainaccountcontactmovein.marketingComSMS, 0, 3),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(isElementInError(mainaccountcontactmovein.passportNumber, 5, 0),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(isElementInError(mainaccountcontactmovein.passportCountry, 5, 0),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(isElementInError(mainaccountcontactmovein.emailAddress, 5, 0),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(isElementInError(mainaccountcontactmovein.mobilePhone, 5, 0),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(isElementInError(mainaccountcontactmovein.businessPhone, 5, 0),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(isElementInError(mainaccountcontactmovein.afterhoursPhone, 5, 0),
				assertionErrorMsg(getLineNumber()));
		mainaccountcontactmovein = new MainAccountContactMoveIn(driver, 0);
		softAssertion.assertFalse(isElementExists(mainaccountcontactmovein.contactSecretCodeList),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(isElementExists(mainaccountcontactmovein.addAnotherContactList),
				assertionErrorMsg(getLineNumber()));
		setImplicitWait(PORTAL_IMPLICIT_WAIT_TIMEOUT);
		// verify all assertions
		softAssertion.assertAll();

		mainaccountcontactmovein.firstName.sendKeys("Tony");
		mainaccountcontactmovein.lastName.sendKeys("Michael");
		mainaccountcontactmovein.businessPhone.sendKeys("1800503291");

		// initial click to resolve ElementNotInteractableException exception
		clickElementAction(mainaccountcontactmovein.dateOfBirth);
		pauseSeleniumExecution(1000);
		String todayMinus6 = getSpecificDateWithTimeZone(MELBOURNE_TIME_ZONE, -6, DATE_MONTH_YEAR_FORMAT_SLASH);
		String birthYr = Integer.toString(Calendar.getInstance().get(Calendar.YEAR) - 18);
		String validBirthDate = getString(todayMinus6, 0, todayMinus6.length() - 4);
		validBirthDate = validBirthDate + birthYr;
		mainaccountcontactmovein.dateOfBirth.sendKeys(validBirthDate, Keys.TAB);
		pauseSeleniumExecution(1000);
		clickElementAction(mainaccountcontactmovein.emailAddress);
		// verify we have a value on the Date of Birth
		String dateOfBirth = getDisplayedValue(mainaccountcontactmovein.dateOfBirth, true);
		this.dateOfBirthMainContact = dateOfBirth;
		// verify Date of Birth is not in error state
		softAssertion.assertFalse(isElementInError(additionalcontactmovein.addCont1DateOfBirth, 5, 0),
				assertionErrorMsg(getLineNumber()));
		// verify all assertions
		softAssertion.assertAll();

		clickElementAction(mainaccountcontactmovein.next);
		pauseSeleniumExecution(1000);
		// verify we are in the 1st additional contact section
		assertTrue(isElementDisplayed(additionalcontactmovein.addCont1FirstName, 0),
				"We are not yet in the 1st Additional Contact section");

		scrollPageUp(500);
		clickElementAction(supplydetailsmovein.header);
		pauseSeleniumExecution(1000);
		assertTrue(isElementDisplayed(supplydetailsmovein.owner, 0), "We are not in the Supply Details section");

		// let's put an invalid value in the Move In date
		String invalidMoveIn = getSpecificDateWithTimeZone(MELBOURNE_TIME_ZONE, 60, DATE_MONTH_YEAR_FORMAT_SLASH);
		assertFalse(isElementInError(supplydetailsmovein.moveInDateOwner, 5, 0),
				"Owner Move In date is in error state");
		clearDateField(supplydetailsmovein.moveInDateOwner);
		clickElementAction(supplydetailsmovein.moveInDateOwner);
		pauseSeleniumExecution(1000);
		supplydetailsmovein.moveInDateOwner.sendKeys(invalidMoveIn, Keys.TAB);
		// clicked an already ticked button to collapse the calendar
		clickElementAction(supplydetailsmovein.owner);
		assertTrue(isElementInError(supplydetailsmovein.moveInDateOwner, 5, 0),
				"Owner Move In date is not in error state");

		// let's put an invalid value in the Settlement Date
		String invalidSettle = getSpecificDateWithTimeZone(MELBOURNE_TIME_ZONE, -60, DATE_MONTH_YEAR_FORMAT_SLASH);
		clickElementAction(supplydetailsmovein.ownerSettleNo);
		assertFalse(isElementInError(supplydetailsmovein.settlementDateOwner, 5, 0),
				"Owner Settlement date is not in error state");
		clickElementAction(supplydetailsmovein.settlementDateOwner);
		pauseSeleniumExecution(1000);
		supplydetailsmovein.settlementDateOwner.sendKeys(invalidSettle, Keys.TAB);
		// clicked an already ticked button to collapse the calendar
		clickElementAction(supplydetailsmovein.owner);
		assertTrue(isElementInError(supplydetailsmovein.settlementDateOwner, 5, 0),
				"Owner Settlement date is not in error state");

		// update the supply details to Tenant
		clickElementAction(supplydetailsmovein.tenant);
		String future3Days = getSpecificDateWithTimeZone(MELBOURNE_TIME_ZONE, 3, DATE_MONTH_YEAR_FORMAT_SLASH);
		clickElementAction(supplydetailsmovein.moveInDateTenant);
		pauseSeleniumExecution(1000);
		supplydetailsmovein.moveInDateTenant.sendKeys(future3Days, Keys.TAB);
		this.tenantMoveInDate = future3Days;
		// click button again to dismiss the calendar
		clickElementAction(supplydetailsmovein.tenant);

		scrollPageDown(600);
		clickElementAction(mainaccountcontactmovein.header);
		pauseSeleniumExecution(1000);
		// verify we are redirected into the correct section
		softAssertion.assertTrue(isElementDisplayed(mainaccountcontactmovein.firstName, 0),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertTrue(isElementDisplayed(mainaccountcontactmovein.lastName, 0),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertTrue(isElementDisplayed(mainaccountcontactmovein.addAnotherContact, 0),
				assertionErrorMsg(getLineNumber()));
		// verify all assertions
		softAssertion.assertAll();

		clickElementAction(mainaccountcontactmovein.next);
		pauseSeleniumExecution(1000);
		// verify we are in the concession details
		softAssertion.assertTrue(isElementDisplayed(concessionmovein.addConcessionYes, 0),
				"We are not yet in the Concession section");
		// verify all assertions
		softAssertion.assertAll();
	}

	/** 
	 * 
	 * 
	 * */
	@Test(priority = 4, dependsOnMethods = { "verifyMainContact" })
	public void verifyConcessionDetails() {

		// let's switch to the Move-In Iframe
		embeddedMoveInSwitchFrame(1);

		// initialize Soft Assert
		SoftAssert softAssertion = new SoftAssert();

		// verify radio buttons not ticked by default
		softAssertion.assertFalse(isElementTicked(concessionmovein.addConcessionYes, 0),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(isElementTicked(concessionmovein.addConcessionNo, 0),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertTrue(isElementDisplayed(concessionmovein.addConcessionYes, 0),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertTrue(isElementDisplayed(concessionmovein.addConcessionNo, 0),
				assertionErrorMsg(getLineNumber()));
		// verify fields not in error state
		softAssertion.assertFalse(isElementInError(concessionmovein.addConcessionYes, 5, 0),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(isElementInError(concessionmovein.addConcessionNo, 5, 0),
				assertionErrorMsg(getLineNumber()));
		// verify all assertions
		softAssertion.assertAll();

		clickElementAction(concessionmovein.addConcessionYes);
		concessionmovein.cardHolderName.sendKeys("Ms Terio");
		clickElementAction(concessionmovein.typeOfConcessionCard);
		pauseSeleniumExecution(1000);
		verifyNumOfMatOptionValuesDisp(concessionmovein.typeOfConcessionCardDiv, 4);
		chooseFromList(concessionmovein.typeOfConcessionCardDiv, 3);
		pauseSeleniumExecution(1000);
		concessionmovein.cardNumber.sendKeys("1095868400");

		// let's upload concession card details
		uploadConcessionFiles(ARTIFACTS_DIR, "Sprin't 02 Story 'Board.pdf");
		// wait for the files to display in the upload area
		// and also in the S3 bucket
		waitForFilesToBeUploaded(60000);
		// check if the file(s) is/are already uploaded in the S3 bucket
		if (System.getenv("DELETE_PRESIGN_BUCKET") != null
				&& System.getenv("DELETE_PRESIGN_BUCKET").toLowerCase().equals("yes")) {
			int actualSize = s3Access.getNumOfObjectsInABucket(S3_PORTAL_PRESIGN_BUCKET_NAME);
			List<String> objectIds = s3Access.getObjectIdsInABucket(S3_PORTAL_PRESIGN_BUCKET_NAME);
			logDebugMessage(concatStrings(this.className, " actualSize in the S3 bucket for Concession ",
					S3_PORTAL_PRESIGN_BUCKET_NAME, " is <", Integer.toString(actualSize),
					"> and attachments ID's is/are -> ", objectIds.toString()));
		}
		String dragAndDropText = getDisplayedText(concessionmovein.dragAndDropText, true);
		String concessionUploadArea = getDisplayedText(concessionmovein.dragAndDropArea, true);
		// verify only 1 file was uploaded
		softAssertion.assertEquals(concatStrings(dragAndDropText, " ", concessionUploadArea),
				"cloud_upload Drag-and-drop file here or click to browse for file to upload a scan or picture of your concession card Sprin't 02 Story 'Board .pdf 0.4 MB File uploaded successfully",
				assertionErrorMsg(getLineNumber()));
		// verify all assertions
		softAssertion.assertAll();

		clickElementAction(concessionmovein.next);
		pauseSeleniumExecution(1000);
		softAssertion.assertTrue(isElementDisplayed(directdebitmovein.noDirectDebit, 0),
				"We are not yet in the Direct Debit section");
		// verify all assertions
		softAssertion.assertAll();
	}

	/** 
	 * 
	 * */
	@Test(priority = 5, dependsOnMethods = { "verifyConcessionDetails" })
	public void verifyDirectDebitDetails() {

		// let's switch to the Move-In Iframe
		embeddedMoveInSwitchFrame(1);

		// initialize Soft Assert
		SoftAssert softAssertion = new SoftAssert();

		// verify that values are not ticked by default
		softAssertion.assertFalse(isElementTicked(directdebitmovein.bankAccount, 0),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(isElementTicked(directdebitmovein.creditCard, 0), assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(isElementTicked(directdebitmovein.noDirectDebit, 0),
				assertionErrorMsg(getLineNumber()));
		// verify not in error state
		softAssertion.assertFalse(isElementInError(directdebitmovein.bankAccount, 5, 0),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(isElementInError(directdebitmovein.creditCard, 5, 0),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(isElementInError(directdebitmovein.noDirectDebit, 5, 0),
				assertionErrorMsg(getLineNumber()));
		// verify all assertions
		softAssertion.assertAll();

		clickElementAction(directdebitmovein.noDirectDebit);
		clickElementAction(acceptancemovein.header);
		pauseSeleniumExecution(1000);
		softAssertion.assertTrue(isElementDisplayed(acceptancemovein.lblAcceptanceIntro, 0),
				"We are not yet in the Acceptance section");
		// verify all assertions
		softAssertion.assertAll();
	}

	/** 
	 * 
	 * */
	@Test(priority = 6, dependsOnMethods = { "verifyDirectDebitDetails" })
	public void verifyAcceptanceDetails() {

		// let's switch to the Move-In Iframe
		embeddedMoveInSwitchFrame(1);

		// initialize Soft Assert
		SoftAssert softAssertion = new SoftAssert();

		// verify each section
		String movingIn = getDisplayedText(acceptancemovein.movingInRow, true);
		String servAdd = getDisplayedText(acceptancemovein.serviceAddressRow, true);
		String lifeSup = getDisplayedText(acceptancemovein.lifeSupportRow, true);
		String acctDetails = getDisplayedText(acceptancemovein.accountDetailsRow, true);
		String mainContact = getDisplayedText(acceptancemovein.mainContactRow, true);
		String mainContactNotif = getDisplayedText(acceptancemovein.mainContactNotifRow, true);
		String concession = getDisplayedText(acceptancemovein.concessionRow, true);
		String directDebit = getDisplayedText(acceptancemovein.directDebitRow, true);
		String addNote = getDisplayedText(acceptancemovein.additionalNoteRow, true);
		softAssertion.assertEquals(movingIn,
				concatStrings("Moving In update Moving in as Tenant ", this.tenantMoveInDate),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertEquals(servAdd,
				"Service Address update Lot 51, 502-514 Burwood Highway Vermont South, Victoria, 3133",
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertEquals(lifeSup, "Life Support update Medical Cooling Required Medical Certificate Provided",
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertEquals(acctDetails, "Account Details update Residential Account",
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertEquals(mainContact,
				concatStrings("Main Account Contact update Tony Michael Business Phone: 1800503291 Birthdate: ",
						this.dateOfBirthMainContact, " Personal Id: Passport (00Abc9800, �land Islands)"),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertEquals(mainContactNotif,
				"Main Account Contact Notification update Bills (None) Notifications and Reminders (None) Marketing (None)",
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertEquals(concession,
				"Concession update Ms Terio Health Care Card 1095868400 Concession Card Uploaded",
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertEquals(directDebit, "Direct Debit update None Specified",
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertEquals(addNote, "Additional Note update None Specified",
				assertionErrorMsg(getLineNumber()));
		// verify no trade waste section displayed and no 1st additional contact
		// displayed
		acceptancemovein = new AcceptanceMoveIn(driver, 0);
		softAssertion.assertFalse(isElementExists(acceptancemovein.addContact1LblList),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(isElementExists(acceptancemovein.addContact1UpdateList),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(isElementExists(acceptancemovein.addContact1DataList),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(isElementExists(acceptancemovein.addContact1NotifRowList),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(isElementExists(acceptancemovein.tradeWasteRowList),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(isElementExists(acceptancemovein.dischargeInfoRowList),
				assertionErrorMsg(getLineNumber()));
		setImplicitWait(PORTAL_IMPLICIT_WAIT_TIMEOUT);
		// verify all assertions
		softAssertion.assertAll();

		// update the supply details as Property Manager
		// then go back to Acceptance page
		clickExactLinkNameFromElement(acceptancemovein.movingInRow, "update");
		pauseSeleniumExecution(1000);
		assertTrue(isElementDisplayed(supplydetailsmovein.tenant, 0), "We are not yet in the Supply Details section");
		clickElementAction(supplydetailsmovein.propManager);
		String future2Days = getSpecificDateWithTimeZone(MELBOURNE_TIME_ZONE, 2, DATE_MONTH_YEAR_FORMAT_SLASH);
		String future2D = getSpecificDateWithTimeZone(MELBOURNE_TIME_ZONE, 2, DATE_MONTH_YEAR_FORMAT_DASH);
		clickElementAction(supplydetailsmovein.moveInDatePropMan);
		pauseSeleniumExecution(1000);
		supplydetailsmovein.moveInDatePropMan.sendKeys(future2Days, Keys.TAB);
		this.propManMoveInDate = future2Days;
		this.propManMoveInDateCRM = future2D;
		// initial click on the same field to dismiss the calendar
		// because the Keys.TAB on selenium did not dismiss the calendar
		clickElementAction(supplydetailsmovein.propManSettleYes);
		clickElementAction(supplydetailsmovein.propManSettleYes);
		clickElementAction(supplydetailsmovein.ownerPropManHolidayNo);
		scrollPageDown(1000);
		clickElementAction(acceptancemovein.header);
		pauseSeleniumExecution(1000);
		// user should be redirected into the Additional Contact immediately since it's
		// required
		assertTrue(isElementDisplayed(additionalcontactmovein.addCont1FirstName, 0),
				"We are not yet in the 1st Additional Contact section");

		// verify fields in error state
		// verify radio buttons not ticked by default
		softAssertion.assertFalse(isElementTicked(additionalcontactmovein.addCont1DriversLicence, 0),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(isElementTicked(additionalcontactmovein.addCont1Passport, 0),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(isElementTicked(additionalcontactmovein.addCont1MedicareCard, 0),
				assertionErrorMsg(getLineNumber()));
		// verify fields are not in error state
		softAssertion.assertTrue(isElementInError(additionalcontactmovein.addCont1FirstName, 5, 0),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertTrue(isElementInError(additionalcontactmovein.addCont1LastName, 5, 0),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertTrue(isElementInError(additionalcontactmovein.addCont1DateOfBirth, 5, 0),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertTrue(isElementInError(additionalcontactmovein.addCont1DriversLicence, 5, 0),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertTrue(isElementInError(additionalcontactmovein.addCont1Passport, 5, 0),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertTrue(isElementInError(additionalcontactmovein.addCont1MedicareCard, 5, 0),
				assertionErrorMsg(getLineNumber()));
		additionalcontactmovein = new AdditionalContactMoveIn(driver, 0);
		// verify that Provide None option is not displayed
		softAssertion.assertFalse(isElementExists(additionalcontactmovein.addCont1ProvideNoneList),
				assertionErrorMsg(getLineNumber()));
		setImplicitWait(PORTAL_IMPLICIT_WAIT_TIMEOUT);
		softAssertion.assertFalse(isElementInError(additionalcontactmovein.addCont1BillsPostal, 0, 3),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(isElementInError(additionalcontactmovein.addCont1BillsEmail, 0, 3),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(isElementInError(additionalcontactmovein.addCont1BillsSMS, 0, 3),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(isElementInError(additionalcontactmovein.addCont1AcctnotifAndRemindersPostal, 0, 3),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(isElementInError(additionalcontactmovein.addCont1AcctnotifAndRemindersEmail, 0, 3),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(isElementInError(additionalcontactmovein.addCont1AcctnotifAndRemindersSMS, 0, 3),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(isElementInError(additionalcontactmovein.addCont1MarketingComPostal, 0, 3),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(isElementInError(additionalcontactmovein.addCont1MarketingComEmail, 0, 3),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(isElementInError(additionalcontactmovein.addCont1MarketingComSMS, 0, 3),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(isElementInError(additionalcontactmovein.addCont1EmailAddress, 5, 0),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(isElementInError(additionalcontactmovein.addCont1MobilePhone, 5, 0),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(isElementInError(additionalcontactmovein.addCont1BusinessPhone, 5, 0),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(isElementInError(additionalcontactmovein.addCont1AfterhoursPhone, 5, 0),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertTrue(isElementDisplayed(additionalcontactmovein.addCont1AddAnotherContact, 0),
				assertionErrorMsg(getLineNumber()));
		additionalcontactmovein = new AdditionalContactMoveIn(driver, 0);
		softAssertion.assertFalse(isElementDisplayed(additionalcontactmovein.addCont1RemAdditionalContact, 0),
				assertionErrorMsg(getLineNumber()));
		setImplicitWait(PORTAL_IMPLICIT_WAIT_TIMEOUT);
		// verify all assertions
		softAssertion.assertAll();
		additionalcontactmovein.addCont1FirstName.sendKeys("Davut");
		additionalcontactmovein.addCont1LastName.sendKeys("Ataman");
		// initial click to resolve ElementNotInteractableException exception
		clickElementAction(additionalcontactmovein.addCont1DateOfBirth);
		pauseSeleniumExecution(1000);
		String todayMinus5 = getSpecificDateWithTimeZone(MELBOURNE_TIME_ZONE, -5, DATE_MONTH_YEAR_FORMAT_SLASH);
		String birthYr = Integer.toString(Calendar.getInstance().get(Calendar.YEAR) - 18);
		String validBirthDate = getString(todayMinus5, 0, todayMinus5.length() - 4);
		validBirthDate = validBirthDate + birthYr;
		additionalcontactmovein.addCont1DateOfBirth.sendKeys(validBirthDate, Keys.TAB);
		pauseSeleniumExecution(1000);
		clickElementAction(additionalcontactmovein.addCont1EmailAddress);
		// verify we have a value on the Date of Birth
		String dateOfBirth = getDisplayedValue(additionalcontactmovein.addCont1DateOfBirth, true);
		this.dateOfBirthAddCont1 = dateOfBirth;
		// verify Date of Birth is not in error state
		softAssertion.assertFalse(isElementInError(additionalcontactmovein.addCont1DateOfBirth, 5, 0),
				assertionErrorMsg(getLineNumber()));
		clickElementAction(additionalcontactmovein.addCont1DriversLicence);
		additionalcontactmovein.addCont1DriversLicenceNumber.sendKeys("00Abc98450");
		additionalcontactmovein.addCont1DriversLicenceState.sendKeys("Western Australia");
		clickElementAction(additionalcontactmovein.addCont1LblNotificationHeader);
		javaScriptClickElementAction(additionalcontactmovein.addCont1BillsPostal);
		softAssertion.assertTrue(isElementTicked(additionalcontactmovein.addCont1BillsPostal, 0),
				assertionErrorMsg(getLineNumber()));
		// verify all assertions
		softAssertion.assertAll();
		scrollPageDown(900);
		clickElementAction(acceptancemovein.header);
		pauseSeleniumExecution(1000);
		// user should be redirected into the Postal address section immediately
		assertTrue(isElementDisplayed(postaladdressmovein.sameSupAddressYes, 0),
				"We are not yet in the Postal Address section");

		// verify we are in the postal address section
		// verify radio buttons not ticked by default
		softAssertion.assertFalse(isElementTicked(postaladdressmovein.sameSupAddressYes, 0),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(isElementTicked(postaladdressmovein.sameSupAddressNo, 0),
				assertionErrorMsg(getLineNumber()));
		// verify fields in error state
		softAssertion.assertTrue(isElementInError(postaladdressmovein.sameSupAddressYes, 5, 0),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertTrue(isElementInError(postaladdressmovein.sameSupAddressNo, 5, 0),
				assertionErrorMsg(getLineNumber()));
		// verify all assertions
		softAssertion.assertAll();
		clickElementAction(postaladdressmovein.sameSupAddressYes);
		scrollPageDown(800);
		clickElementAction(acceptancemovein.header);
		pauseSeleniumExecution(1000);
		assertTrue(isElementDisplayed(acceptancemovein.lblAcceptanceIntro, 0),
				"We are not yet in the Acceptance section");

		// verify the session and local keys created
		BrowserLocalSessionStorage storage = new BrowserLocalSessionStorage(driver);
		List<String> sessionKeys = storage.getAllKeysFromSessionStorage();
		long sessionLength = storage.getSessionStorageLength();
		logDebugMessage(concatStrings("The value of sessionKeys ", sessionKeys.toString(), " and the size is <",
				String.valueOf(sessionLength), ">"));
		List<String> localKeys = storage.getAllKeysFromLocalStorage();
		long localLength = storage.getLocalStorageLength();
		logDebugMessage(concatStrings("The value of localKeys ", localKeys.toString(), " and the size is <",
				String.valueOf(localLength), ">"));
		// let's confirm the keys in the session storage
		softAssertion.assertTrue(sessionKeys.contains("move-in.supply_details"), assertionErrorMsg(getLineNumber()));
		softAssertion.assertTrue(sessionKeys.contains("move-in_life_support_file"), assertionErrorMsg(getLineNumber()));
		softAssertion.assertTrue(sessionKeys.contains("move-in.account_details"), assertionErrorMsg(getLineNumber()));
		softAssertion.assertTrue(sessionKeys.contains("move-in.main_contact"), assertionErrorMsg(getLineNumber()));
		softAssertion.assertTrue(sessionKeys.contains("move-in.additional_contact"),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertTrue(sessionKeys.contains("move-in.postal_address"), assertionErrorMsg(getLineNumber()));
		softAssertion.assertTrue(sessionKeys.contains("move-in.concession_card"), assertionErrorMsg(getLineNumber()));
		softAssertion.assertTrue(sessionKeys.contains("move-in_concession_file"), assertionErrorMsg(getLineNumber()));
		softAssertion.assertTrue(sessionKeys.contains("move-in.letting_agent"), assertionErrorMsg(getLineNumber()));
		softAssertion.assertTrue(sessionKeys.contains("move-in.direct_debit"), assertionErrorMsg(getLineNumber()));
		softAssertion.assertTrue(sessionKeys.contains("portalConfiguration"), assertionErrorMsg(getLineNumber()));
		softAssertion.assertTrue(sessionKeys.contains("move-in.steps"), assertionErrorMsg(getLineNumber()));
		softAssertion.assertTrue(sessionKeys.contains("application_id"), assertionErrorMsg(getLineNumber()));
		softAssertion.assertTrue(sessionKeys.contains("source_id"), assertionErrorMsg(getLineNumber()));
		if (getPortalType().equals("standalone") && getPopulateDataMethod().equals("manual")
				|| getPortalType().equals("embedded") && getPopulateDataMethod().equals("manual")) {
			// verify the expected number of keys
			softAssertion.assertEquals(sessionLength, 14, assertionErrorMsg(getLineNumber()));
		} else if (getPortalType().equals("embedded") && getPopulateDataMethod().equals("thirdPartyPrefill")) {
			softAssertion.assertTrue(sessionKeys.contains("readOnlyIfThirdParty"), assertionErrorMsg(getLineNumber()));
			softAssertion.assertTrue(sessionKeys.contains("move-in-query-headless1"),
					assertionErrorMsg(getLineNumber()));
			softAssertion.assertTrue(sessionKeys.contains("move-in.extra_data"), assertionErrorMsg(getLineNumber()));
			// verify the expected number of keys
			softAssertion.assertEquals(sessionLength, 17, assertionErrorMsg(getLineNumber()));
		} else if (getPortalType().equals("embedded") && getPopulateDataMethod().equals("urlPrefill")
				|| getPortalType().equals("standalone") && getPopulateDataMethod().equals("urlPrefill")) {
			softAssertion.assertTrue(sessionKeys.contains("move-in-query-move_in_date"),
					assertionErrorMsg(getLineNumber()));
			softAssertion.assertTrue(sessionKeys.contains("move-in-query-tenancy_street_number"),
					assertionErrorMsg(getLineNumber()));
			softAssertion.assertTrue(sessionKeys.contains("move-in-query-tenancy_street_name"),
					assertionErrorMsg(getLineNumber()));
			softAssertion.assertTrue(sessionKeys.contains("move-in-query-tenancy_street_type"),
					assertionErrorMsg(getLineNumber()));
			softAssertion.assertTrue(sessionKeys.contains("move-in-query-tenancy_suburb"),
					assertionErrorMsg(getLineNumber()));
			softAssertion.assertTrue(sessionKeys.contains("move-in-query-tenancy_state"),
					assertionErrorMsg(getLineNumber()));
			softAssertion.assertTrue(sessionKeys.contains("move-in-query-tenancy_postcode"),
					assertionErrorMsg(getLineNumber()));
			softAssertion.assertTrue(sessionKeys.contains("move-in-query-account_type"),
					assertionErrorMsg(getLineNumber()));
			softAssertion.assertTrue(sessionKeys.contains("move-in-query-business_number"),
					assertionErrorMsg(getLineNumber()));
			softAssertion.assertTrue(sessionKeys.contains("move-in-query-business_trading_name"),
					assertionErrorMsg(getLineNumber()));
			softAssertion.assertTrue(sessionKeys.contains("move-in-query-email_address"),
					assertionErrorMsg(getLineNumber()));
			softAssertion.assertTrue(sessionKeys.contains("move-in-query-mobile_number"),
					assertionErrorMsg(getLineNumber()));
			softAssertion.assertTrue(sessionKeys.contains("move-in-query-business_hour_phone"),
					assertionErrorMsg(getLineNumber()));
			softAssertion.assertTrue(sessionKeys.contains("move-in-query-after_hour_phone"),
					assertionErrorMsg(getLineNumber()));
			softAssertion.assertTrue(sessionKeys.contains("move-in-query-extra_data"),
					assertionErrorMsg(getLineNumber()));
			softAssertion.assertTrue(sessionKeys.contains("move-in-query-config"), assertionErrorMsg(getLineNumber()));
			softAssertion.assertTrue(sessionKeys.contains("readOnlyIfThirdParty"), assertionErrorMsg(getLineNumber()));
			softAssertion.assertTrue(sessionKeys.contains("move-in.extra_data"), assertionErrorMsg(getLineNumber()));
			// verify the expected number of keys
			softAssertion.assertEquals(sessionLength, 32, assertionErrorMsg(getLineNumber()));
		} else {
			fail(concatStrings("Verify the assertion(s) for this configuration. Portal type '", getPortalType(),
					"' and Populate Data method '", getPopulateDataMethod(), "'"));
		}
		// let's confirm the keys in the local storage
		softAssertion.assertTrue(localKeys.contains("_grecaptcha"), assertionErrorMsg(getLineNumber()));
		softAssertion.assertTrue(localKeys.contains("raygun4js-userid"), assertionErrorMsg(getLineNumber()));
		// verify the expected number of keys
		softAssertion.assertEquals(localLength, 2, assertionErrorMsg(getLineNumber()));
		// let's confirm the values stored in the session storage are not empty
		String sessionSupplyDetails = storage.getItemFromSessionStorage("move-in.supply_details");
		String sessionLifeSuppFile = storage.getItemFromSessionStorage("move-in_life_support_file");
		String sessionAccountDetails = storage.getItemFromSessionStorage("move-in.account_details");
		String sessionMainContact = storage.getItemFromSessionStorage("move-in.main_contact");
		String sessionAdditionalContact = storage.getItemFromSessionStorage("move-in.additional_contact");
		String sessionPostalAdd = storage.getItemFromSessionStorage("move-in.postal_address");
		String sessionConcessionDetails = storage.getItemFromSessionStorage("move-in.concession_card");
		String sessionConcessionFile = storage.getItemFromSessionStorage("move-in_concession_file");
		String sessionPropManager = storage.getItemFromSessionStorage("move-in.letting_agent");
		String sessionDirectDebit = storage.getItemFromSessionStorage("move-in.direct_debit");
		String sessionPortalConfig = storage.getItemFromSessionStorage("portalConfiguration");
		String sessionMoveInSteps = storage.getItemFromSessionStorage("move-in.steps");
		String sessionAppId = storage.getItemFromSessionStorage("application_id");
		String sessionSourceId = storage.getItemFromSessionStorage("source_id");
		softAssertion.assertFalse(StringUtils.isBlank(sessionSupplyDetails), assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(StringUtils.isBlank(sessionLifeSuppFile), assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(StringUtils.isBlank(sessionAccountDetails), assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(StringUtils.isBlank(sessionMainContact), assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(StringUtils.isBlank(sessionAdditionalContact), assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(StringUtils.isBlank(sessionPostalAdd), assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(StringUtils.isBlank(sessionConcessionDetails), assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(StringUtils.isBlank(sessionConcessionFile), assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(StringUtils.isBlank(sessionPropManager), assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(StringUtils.isBlank(sessionDirectDebit), assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(StringUtils.isBlank(sessionPortalConfig), assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(StringUtils.isBlank(sessionMoveInSteps), assertionErrorMsg(getLineNumber()));
		softAssertion.assertEquals(sessionAppId, "move-in", assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(StringUtils.isBlank(sessionSourceId), assertionErrorMsg(getLineNumber()));
		if (getPortalType().equals("standalone") && getPopulateDataMethod().equals("manual")
				|| getPortalType().equals("embedded") && getPopulateDataMethod().equals("manual")) {
			softAssertion.assertFalse(sessionKeys.contains("readOnlyIfThirdParty"), assertionErrorMsg(getLineNumber()));
			softAssertion.assertFalse(sessionKeys.contains("move-in-query-headless1"),
					assertionErrorMsg(getLineNumber()));
			softAssertion.assertFalse(sessionKeys.contains("move-in.extra_data"), assertionErrorMsg(getLineNumber()));
		} else if (getPortalType().equals("embedded") && getPopulateDataMethod().equals("thirdPartyPrefill")) {
			String sessionReadOnly3rdParty = storage.getItemFromSessionStorage("readOnlyIfThirdParty");
			String sessionQueryHeadless = storage.getItemFromSessionStorage("move-in-query-headless1");
			String sessionExtraData = storage.getItemFromSessionStorage("move-in.extra_data");
			softAssertion.assertFalse(StringUtils.isBlank(sessionReadOnly3rdParty), assertionErrorMsg(getLineNumber()));
			softAssertion.assertFalse(StringUtils.isBlank(sessionQueryHeadless), assertionErrorMsg(getLineNumber()));
			softAssertion.assertFalse(StringUtils.isBlank(sessionExtraData), assertionErrorMsg(getLineNumber()));
		} else if (getPortalType().equals("embedded") && getPopulateDataMethod().equals("urlPrefill")
				|| getPortalType().equals("standalone") && getPopulateDataMethod().equals("urlPrefill")) {
			String sessionQueryMoveInDate = storage.getItemFromSessionStorage("move-in-query-move_in_date");
			String sessionQueryTenancyStNum = storage.getItemFromSessionStorage("move-in-query-tenancy_street_number");
			String sessionQueryTenancyStName = storage.getItemFromSessionStorage("move-in-query-tenancy_street_name");
			String sessionQueryTenancyStType = storage.getItemFromSessionStorage("move-in-query-tenancy_street_type");
			String sessionQueryTenancyCity = storage.getItemFromSessionStorage("move-in-query-tenancy_suburb");
			String sessionQueryTenancyState = storage.getItemFromSessionStorage("move-in-query-tenancy_state");
			String sessionQueryTenancyPostcode = storage.getItemFromSessionStorage("move-in-query-tenancy_postcode");
			String sessionQueryAcctType = storage.getItemFromSessionStorage("move-in-query-account_type");
			String sessionQueryAbnAcn = storage.getItemFromSessionStorage("move-in-query-business_number");
			String sessionQueryTradingName = storage.getItemFromSessionStorage("move-in-query-business_trading_name");
			String sessionQueryEmailAdd = storage.getItemFromSessionStorage("move-in-query-email_address");
			String sessionQueryMobNum = storage.getItemFromSessionStorage("move-in-query-mobile_number");
			String sessionQueryBusNum = storage.getItemFromSessionStorage("move-in-query-business_hour_phone");
			String sessionQueryAfterHrNum = storage.getItemFromSessionStorage("move-in-query-after_hour_phone");
			String sessionQueryExtraData = storage.getItemFromSessionStorage("move-in-query-extra_data");
			String sessionQueryConfig = storage.getItemFromSessionStorage("move-in-query-config");
			String sessionReadOnly3rdParty = storage.getItemFromSessionStorage("readOnlyIfThirdParty");
			String sessionExtraData = storage.getItemFromSessionStorage("move-in.extra_data");
			softAssertion.assertFalse(StringUtils.isBlank(sessionQueryMoveInDate), assertionErrorMsg(getLineNumber()));
			softAssertion.assertFalse(StringUtils.isBlank(sessionQueryTenancyStNum),
					assertionErrorMsg(getLineNumber()));
			softAssertion.assertFalse(StringUtils.isBlank(sessionQueryTenancyStName),
					assertionErrorMsg(getLineNumber()));
			softAssertion.assertFalse(StringUtils.isBlank(sessionQueryTenancyStType),
					assertionErrorMsg(getLineNumber()));
			softAssertion.assertFalse(StringUtils.isBlank(sessionQueryTenancyCity), assertionErrorMsg(getLineNumber()));
			softAssertion.assertFalse(StringUtils.isBlank(sessionQueryTenancyState),
					assertionErrorMsg(getLineNumber()));
			softAssertion.assertFalse(StringUtils.isBlank(sessionQueryTenancyPostcode),
					assertionErrorMsg(getLineNumber()));
			softAssertion.assertFalse(StringUtils.isBlank(sessionQueryAcctType), assertionErrorMsg(getLineNumber()));
			softAssertion.assertFalse(StringUtils.isBlank(sessionQueryAbnAcn), assertionErrorMsg(getLineNumber()));
			softAssertion.assertFalse(StringUtils.isBlank(sessionQueryTradingName), assertionErrorMsg(getLineNumber()));
			softAssertion.assertFalse(StringUtils.isBlank(sessionQueryEmailAdd), assertionErrorMsg(getLineNumber()));
			softAssertion.assertFalse(StringUtils.isBlank(sessionQueryMobNum), assertionErrorMsg(getLineNumber()));
			softAssertion.assertFalse(StringUtils.isBlank(sessionQueryBusNum), assertionErrorMsg(getLineNumber()));
			softAssertion.assertFalse(StringUtils.isBlank(sessionQueryAfterHrNum), assertionErrorMsg(getLineNumber()));
			softAssertion.assertFalse(StringUtils.isBlank(sessionQueryExtraData), assertionErrorMsg(getLineNumber()));
			softAssertion.assertFalse(StringUtils.isBlank(sessionQueryConfig), assertionErrorMsg(getLineNumber()));
			softAssertion.assertFalse(StringUtils.isBlank(sessionReadOnly3rdParty), assertionErrorMsg(getLineNumber()));
			softAssertion.assertFalse(StringUtils.isBlank(sessionExtraData), assertionErrorMsg(getLineNumber()));
		} else {
			fail(concatStrings("Verify the assertion(s) for this configuration. Portal type '", getPortalType(),
					"' and Populate Data method '", getPopulateDataMethod(), "'"));
		}
		// let's confirm the values stored in the local storage
		String localGrecaptcha = storage.getItemFromLocalStorage("_grecaptcha");
		String localRaygunUserId = storage.getItemFromLocalStorage("raygun4js-userid");
		softAssertion.assertFalse(StringUtils.isBlank(localGrecaptcha), assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(StringUtils.isBlank(localRaygunUserId), assertionErrorMsg(getLineNumber()));
		// verify all assertions
		softAssertion.assertAll();

		scrollPageDown(500);
		// update the concession details and choose No
		// then go back to the Acceptance page
		clickExactLinkNameFromElement(acceptancemovein.concessionRow, "update");
		pauseSeleniumExecution(1000);
		assertTrue(isElementDisplayed(concessionmovein.addConcessionNo, 0), "We are not yet in the Concession section");
		clickElementAction(concessionmovein.cardHolderName);
		deleteAllTextFromField();
		clickElementAction(concessionmovein.cardNumber);
		deleteAllTextFromField();
		// let's put invalid values
		concessionmovein.cardHolderName.sendKeys("~!@#$%^&*()_+|`-=\\{}[]:\";'<>?,./");
		concessionmovein.cardNumber.sendKeys("~!@#$%^&*()_+|`-=\\{}[]:\";'<>?,./");
		clickElementAction(concessionmovein.addConcessionNo);
		scrollPageDown(700);
		clickElementAction(acceptancemovein.header);
		pauseSeleniumExecution(1000);

		// verify each line again
		movingIn = getDisplayedText(acceptancemovein.movingInRow, true);
		servAdd = getDisplayedText(acceptancemovein.serviceAddressRow, true);
		lifeSup = getDisplayedText(acceptancemovein.lifeSupportRow, true);
		acctDetails = getDisplayedText(acceptancemovein.accountDetailsRow, true);
		mainContact = getDisplayedText(acceptancemovein.mainContactRow, true);
		mainContactNotif = getDisplayedText(acceptancemovein.mainContactNotifRow, true);
		String addContact1lbl = getDisplayedText(acceptancemovein.addContact1Lbl, true);
		String addContact1UpdLink = getDisplayedText(acceptancemovein.addContact1Update, true);
		String addContact1Data = getDisplayedText(acceptancemovein.addContact1Data, true);
		String addContact1Notif = getDisplayedText(acceptancemovein.addContact1NotifRow, true);
		String postAdd = getDisplayedText(acceptancemovein.postalAddressRow, true);
		concession = getDisplayedText(acceptancemovein.concessionRow, true);
		String propMan = getDisplayedText(acceptancemovein.propManLettingAgentRow, true);
		directDebit = getDisplayedText(acceptancemovein.directDebitRow, true);
		addNote = getDisplayedText(acceptancemovein.additionalNoteRow, true);
		softAssertion.assertEquals(movingIn,
				concatStrings("Moving In update Moving in as Property Manager or Letting Agent ",
						this.propManMoveInDate),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertEquals(servAdd,
				"Service Address update Lot 51, 502-514 Burwood Highway Vermont South, Victoria, 3133",
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertEquals(lifeSup, "Life Support update Medical Cooling Required Medical Certificate Provided",
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertEquals(acctDetails, "Account Details update Residential Account",
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertEquals(mainContact, "Main Account Contact update Tony Michael Business Phone: 1800503291",
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertEquals(mainContactNotif,
				"Main Account Contact Notification update Bills (None) Notifications and Reminders (None) Marketing (None)",
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertEquals(addContact1lbl, "Additional Contact 1", assertionErrorMsg(getLineNumber()));
		softAssertion.assertEquals(addContact1UpdLink, "update", assertionErrorMsg(getLineNumber()));
		softAssertion.assertEquals(addContact1Data,
				concatStrings("Davut Ataman Birthdate: ", this.dateOfBirthAddCont1,
						" Personal Id: Driver Licence (00Abc98450, Western Australia)"),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertEquals(addContact1Notif,
				"Additional Contact 1 Notification update Bills (Postal) Notifications and Reminders (None) Marketing (None)",
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertEquals(postAdd,
				"Postal Address update Lot 51, 502-514 Burwood Highway Vermont South, Victoria, 3133",
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertEquals(concession, "Concession update None Specified", assertionErrorMsg(getLineNumber()));
		softAssertion.assertEquals(propMan, "Property Manager / Letting Agent update None Specified",
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertEquals(directDebit, "Direct Debit update None Specified",
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertEquals(addNote, "Additional Note update None Specified",
				assertionErrorMsg(getLineNumber()));
		// verify all assertions
		softAssertion.assertAll();

		scrollPageDown(500);
		clickExactLinkNameFromElement(acceptancemovein.propManLettingAgentRow, "update");
		pauseSeleniumExecution(1000);
		// verify we are in the Manaer/Agent Company Details
		softAssertion.assertTrue(isElementDisplayed(managerholidaylettingmovein.companyName, 0),
				assertionErrorMsg(getLineNumber()));
		// verify fields not in error state
		// verify the intro message
		String introMsg = getDisplayedText(managerholidaylettingmovein.lblPropManHolidayLettingIntro, true);
		softAssertion.assertEquals(introMsg,
				"Please specify the property company via which the property is being managed",
				assertionErrorMsg(getLineNumber()));
		// verify the fields are not in error state
		softAssertion.assertFalse(isElementInError(managerholidaylettingmovein.companyName, 5, 0),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(isElementInError(managerholidaylettingmovein.companyContactNum, 5, 0),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(isElementInError(managerholidaylettingmovein.address01, 5, 0),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(isElementInError(managerholidaylettingmovein.address02, 5, 0),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(isElementInError(managerholidaylettingmovein.address03, 5, 0),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(isElementInError(managerholidaylettingmovein.address04, 5, 0),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(isElementInError(managerholidaylettingmovein.city, 5, 0),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(isElementInError(managerholidaylettingmovein.state, 5, 0),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(isElementInError(managerholidaylettingmovein.postCode, 5, 0),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(isElementInError(managerholidaylettingmovein.country, 5, 0),
				assertionErrorMsg(getLineNumber()));
		// verify all assertions
		softAssertion.assertAll();
		managerholidaylettingmovein.address03.sendKeys("Add-03");
		managerholidaylettingmovein.country.sendKeys("�land Islands");
		scrollPageDown(600);
		clickElementAction(acceptancemovein.header);
		pauseSeleniumExecution(1000);

		// verify each line again
		movingIn = getDisplayedText(acceptancemovein.movingInRow, true);
		servAdd = getDisplayedText(acceptancemovein.serviceAddressRow, true);
		lifeSup = getDisplayedText(acceptancemovein.lifeSupportRow, true);
		acctDetails = getDisplayedText(acceptancemovein.accountDetailsRow, true);
		mainContact = getDisplayedText(acceptancemovein.mainContactRow, true);
		mainContactNotif = getDisplayedText(acceptancemovein.mainContactNotifRow, true);
		addContact1lbl = getDisplayedText(acceptancemovein.addContact1Lbl, true);
		addContact1UpdLink = getDisplayedText(acceptancemovein.addContact1Update, true);
		addContact1Data = getDisplayedText(acceptancemovein.addContact1Data, true);
		addContact1Notif = getDisplayedText(acceptancemovein.addContact1NotifRow, true);
		postAdd = getDisplayedText(acceptancemovein.postalAddressRow, true);
		concession = getDisplayedText(acceptancemovein.concessionRow, true);
		propMan = getDisplayedText(acceptancemovein.propManLettingAgentRow, true);
		directDebit = getDisplayedText(acceptancemovein.directDebitRow, true);
		addNote = getDisplayedText(acceptancemovein.additionalNoteRow, true);
		softAssertion.assertEquals(movingIn,
				concatStrings("Moving In update Moving in as Property Manager or Letting Agent ",
						this.propManMoveInDate),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertEquals(servAdd,
				"Service Address update Lot 51, 502-514 Burwood Highway Vermont South, Victoria, 3133",
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertEquals(lifeSup, "Life Support update Medical Cooling Required Medical Certificate Provided",
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertEquals(acctDetails, "Account Details update Residential Account",
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertEquals(mainContact, "Main Account Contact update Tony Michael Business Phone: 1800503291",
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertEquals(mainContactNotif,
				"Main Account Contact Notification update Bills (None) Notifications and Reminders (None) Marketing (None)",
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertEquals(addContact1lbl, "Additional Contact 1", assertionErrorMsg(getLineNumber()));
		softAssertion.assertEquals(addContact1UpdLink, "update", assertionErrorMsg(getLineNumber()));
		softAssertion.assertEquals(addContact1Data,
				concatStrings("Davut Ataman Birthdate: ", this.dateOfBirthAddCont1,
						" Personal Id: Driver Licence (00Abc98450, Western Australia)"),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertEquals(addContact1Notif,
				"Additional Contact 1 Notification update Bills (Postal) Notifications and Reminders (None) Marketing (None)",
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertEquals(postAdd,
				"Postal Address update Lot 51, 502-514 Burwood Highway Vermont South, Victoria, 3133",
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertEquals(concession, "Concession update None Specified", assertionErrorMsg(getLineNumber()));
		softAssertion.assertEquals(propMan, "Property Manager / Letting Agent update Add-03 �land Islands",
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertEquals(directDebit, "Direct Debit update None Specified",
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertEquals(addNote, "Additional Note update None Specified",
				assertionErrorMsg(getLineNumber()));
		// verify all assertions
		softAssertion.assertAll();
	}

	/** 
	 * 
	 * */
	@Test(priority = 7, dependsOnMethods = { "verifyAcceptanceDetails" })
	public void verifySessionDetails() {

		// let's switch to the Move-In Iframe
		embeddedMoveInSwitchFrame(1);

		// initialize Soft Assert
		SoftAssert softAssertion = new SoftAssert();

		BrowserLocalSessionStorage storage = new BrowserLocalSessionStorage(driver);

		List<String> sessionKeys = storage.getAllKeysFromSessionStorage();
		long sessionLength = storage.getSessionStorageLength();
		logDebugMessage(concatStrings("The value of sessionKeys ", sessionKeys.toString(), " and the size is <",
				String.valueOf(sessionLength), ">"));
		List<String> localKeys = storage.getAllKeysFromLocalStorage();
		long localLength = storage.getLocalStorageLength();
		logDebugMessage(concatStrings("The value of localKeys ", localKeys.toString(), " and the size is <",
				String.valueOf(localLength), ">"));

		// let's confirm the keys in the session storage
		softAssertion.assertTrue(sessionKeys.contains("move-in.supply_details"), assertionErrorMsg(getLineNumber()));
		softAssertion.assertTrue(sessionKeys.contains("move-in_life_support_file"), assertionErrorMsg(getLineNumber()));
		softAssertion.assertTrue(sessionKeys.contains("move-in.account_details"), assertionErrorMsg(getLineNumber()));
		softAssertion.assertTrue(sessionKeys.contains("move-in.main_contact"), assertionErrorMsg(getLineNumber()));
		softAssertion.assertTrue(sessionKeys.contains("move-in.additional_contact"),
				assertionErrorMsg(getLineNumber()));
		softAssertion.assertTrue(sessionKeys.contains("move-in.postal_address"), assertionErrorMsg(getLineNumber()));
		softAssertion.assertTrue(sessionKeys.contains("move-in.concession_card"), assertionErrorMsg(getLineNumber()));
		softAssertion.assertTrue(sessionKeys.contains("move-in.letting_agent"), assertionErrorMsg(getLineNumber()));
		softAssertion.assertTrue(sessionKeys.contains("move-in.direct_debit"), assertionErrorMsg(getLineNumber()));
		softAssertion.assertTrue(sessionKeys.contains("portalConfiguration"), assertionErrorMsg(getLineNumber()));
		softAssertion.assertTrue(sessionKeys.contains("move-in.steps"), assertionErrorMsg(getLineNumber()));
		softAssertion.assertTrue(sessionKeys.contains("application_id"), assertionErrorMsg(getLineNumber()));
		softAssertion.assertTrue(sessionKeys.contains("source_id"), assertionErrorMsg(getLineNumber()));
		if (getPortalType().equals("standalone") && getPopulateDataMethod().equals("manual")
				|| getPortalType().equals("embedded") && getPopulateDataMethod().equals("manual")) {
			// verify the expected number of keys
			softAssertion.assertEquals(sessionLength, 13, assertionErrorMsg(getLineNumber()));
		} else if (getPortalType().equals("embedded") && getPopulateDataMethod().equals("thirdPartyPrefill")) {
			softAssertion.assertTrue(sessionKeys.contains("readOnlyIfThirdParty"), assertionErrorMsg(getLineNumber()));
			softAssertion.assertTrue(sessionKeys.contains("move-in-query-headless1"),
					assertionErrorMsg(getLineNumber()));
			softAssertion.assertTrue(sessionKeys.contains("move-in.extra_data"), assertionErrorMsg(getLineNumber()));
			// verify the expected number of keys
			softAssertion.assertEquals(sessionLength, 16, assertionErrorMsg(getLineNumber()));
		} else if (getPortalType().equals("embedded") && getPopulateDataMethod().equals("urlPrefill")
				|| getPortalType().equals("standalone") && getPopulateDataMethod().equals("urlPrefill")) {
			softAssertion.assertTrue(sessionKeys.contains("move-in-query-move_in_date"),
					assertionErrorMsg(getLineNumber()));
			softAssertion.assertTrue(sessionKeys.contains("move-in-query-tenancy_street_number"),
					assertionErrorMsg(getLineNumber()));
			softAssertion.assertTrue(sessionKeys.contains("move-in-query-tenancy_street_name"),
					assertionErrorMsg(getLineNumber()));
			softAssertion.assertTrue(sessionKeys.contains("move-in-query-tenancy_street_type"),
					assertionErrorMsg(getLineNumber()));
			softAssertion.assertTrue(sessionKeys.contains("move-in-query-tenancy_suburb"),
					assertionErrorMsg(getLineNumber()));
			softAssertion.assertTrue(sessionKeys.contains("move-in-query-tenancy_state"),
					assertionErrorMsg(getLineNumber()));
			softAssertion.assertTrue(sessionKeys.contains("move-in-query-tenancy_postcode"),
					assertionErrorMsg(getLineNumber()));
			softAssertion.assertTrue(sessionKeys.contains("move-in-query-account_type"),
					assertionErrorMsg(getLineNumber()));
			softAssertion.assertTrue(sessionKeys.contains("move-in-query-business_number"),
					assertionErrorMsg(getLineNumber()));
			softAssertion.assertTrue(sessionKeys.contains("move-in-query-business_trading_name"),
					assertionErrorMsg(getLineNumber()));
			softAssertion.assertTrue(sessionKeys.contains("move-in-query-email_address"),
					assertionErrorMsg(getLineNumber()));
			softAssertion.assertTrue(sessionKeys.contains("move-in-query-mobile_number"),
					assertionErrorMsg(getLineNumber()));
			softAssertion.assertTrue(sessionKeys.contains("move-in-query-business_hour_phone"),
					assertionErrorMsg(getLineNumber()));
			softAssertion.assertTrue(sessionKeys.contains("move-in-query-after_hour_phone"),
					assertionErrorMsg(getLineNumber()));
			softAssertion.assertTrue(sessionKeys.contains("move-in-query-extra_data"),
					assertionErrorMsg(getLineNumber()));
			softAssertion.assertTrue(sessionKeys.contains("move-in-query-config"), assertionErrorMsg(getLineNumber()));
			softAssertion.assertTrue(sessionKeys.contains("readOnlyIfThirdParty"), assertionErrorMsg(getLineNumber()));
			softAssertion.assertTrue(sessionKeys.contains("move-in.extra_data"), assertionErrorMsg(getLineNumber()));
			// verify the expected number of keys
			softAssertion.assertEquals(sessionLength, 31, assertionErrorMsg(getLineNumber()));
		} else {
			fail(concatStrings("Verify the assertion(s) for this configuration. Portal type '", getPortalType(),
					"' and Populate Data method '", getPopulateDataMethod(), "'"));
		}
		// let's confirm the keys in the local storage
		softAssertion.assertTrue(localKeys.contains("_grecaptcha"), assertionErrorMsg(getLineNumber()));
		softAssertion.assertTrue(localKeys.contains("raygun4js-userid"), assertionErrorMsg(getLineNumber()));
		// verify the expected number of keys
		softAssertion.assertEquals(localLength, 2, assertionErrorMsg(getLineNumber()));
		// let's confirm the values stored in the session storage are not empty
		String sessionSupplyDetails = storage.getItemFromSessionStorage("move-in.supply_details");
		String sessionLifeSuppFile = storage.getItemFromSessionStorage("move-in_life_support_file");
		String sessionAccountDetails = storage.getItemFromSessionStorage("move-in.account_details");
		String sessionMainContact = storage.getItemFromSessionStorage("move-in.main_contact");
		String sessionAdditionalContact = storage.getItemFromSessionStorage("move-in.additional_contact");
		String sessionPostalAdd = storage.getItemFromSessionStorage("move-in.postal_address");
		String sessionConcessionDetails = storage.getItemFromSessionStorage("move-in.concession_card");
		String sessionPropManager = storage.getItemFromSessionStorage("move-in.letting_agent");
		String sessionDirectDebit = storage.getItemFromSessionStorage("move-in.direct_debit");
		String sessionPortalConfig = storage.getItemFromSessionStorage("portalConfiguration");
		String sessionMoveInSteps = storage.getItemFromSessionStorage("move-in.steps");
		String sessionAppId = storage.getItemFromSessionStorage("application_id");
		String sessionSourceId = storage.getItemFromSessionStorage("source_id");
		softAssertion.assertFalse(StringUtils.isBlank(sessionSupplyDetails), assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(StringUtils.isBlank(sessionLifeSuppFile), assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(StringUtils.isBlank(sessionAccountDetails), assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(StringUtils.isBlank(sessionMainContact), assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(StringUtils.isBlank(sessionAdditionalContact), assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(StringUtils.isBlank(sessionPostalAdd), assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(StringUtils.isBlank(sessionConcessionDetails), assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(StringUtils.isBlank(sessionPropManager), assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(StringUtils.isBlank(sessionDirectDebit), assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(StringUtils.isBlank(sessionPortalConfig), assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(StringUtils.isBlank(sessionMoveInSteps), assertionErrorMsg(getLineNumber()));
		softAssertion.assertEquals(sessionAppId, "move-in", assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(StringUtils.isBlank(sessionSourceId), assertionErrorMsg(getLineNumber()));
		if (getPortalType().equals("standalone") && getPopulateDataMethod().equals("manual")
				|| getPortalType().equals("embedded") && getPopulateDataMethod().equals("manual")) {
			softAssertion.assertFalse(sessionKeys.contains("readOnlyIfThirdParty"), assertionErrorMsg(getLineNumber()));
			softAssertion.assertFalse(sessionKeys.contains("move-in-query-headless1"),
					assertionErrorMsg(getLineNumber()));
			softAssertion.assertFalse(sessionKeys.contains("move-in.extra_data"), assertionErrorMsg(getLineNumber()));
		} else if (getPortalType().equals("embedded") && getPopulateDataMethod().equals("thirdPartyPrefill")) {
			String sessionReadOnly3rdParty = storage.getItemFromSessionStorage("readOnlyIfThirdParty");
			String sessionQueryHeadless = storage.getItemFromSessionStorage("move-in-query-headless1");
			String sessionExtraData = storage.getItemFromSessionStorage("move-in.extra_data");
			softAssertion.assertFalse(StringUtils.isBlank(sessionReadOnly3rdParty), assertionErrorMsg(getLineNumber()));
			softAssertion.assertFalse(StringUtils.isBlank(sessionQueryHeadless), assertionErrorMsg(getLineNumber()));
			softAssertion.assertFalse(StringUtils.isBlank(sessionExtraData), assertionErrorMsg(getLineNumber()));
		} else if (getPortalType().equals("embedded") && getPopulateDataMethod().equals("urlPrefill")
				|| getPortalType().equals("standalone") && getPopulateDataMethod().equals("urlPrefill")) {
			String sessionQueryMoveInDate = storage.getItemFromSessionStorage("move-in-query-move_in_date");
			String sessionQueryTenancyStNum = storage.getItemFromSessionStorage("move-in-query-tenancy_street_number");
			String sessionQueryTenancyStName = storage.getItemFromSessionStorage("move-in-query-tenancy_street_name");
			String sessionQueryTenancyStType = storage.getItemFromSessionStorage("move-in-query-tenancy_street_type");
			String sessionQueryTenancyCity = storage.getItemFromSessionStorage("move-in-query-tenancy_suburb");
			String sessionQueryTenancyState = storage.getItemFromSessionStorage("move-in-query-tenancy_state");
			String sessionQueryTenancyPostcode = storage.getItemFromSessionStorage("move-in-query-tenancy_postcode");
			String sessionQueryAcctType = storage.getItemFromSessionStorage("move-in-query-account_type");
			String sessionQueryAbnAcn = storage.getItemFromSessionStorage("move-in-query-business_number");
			String sessionQueryTradingName = storage.getItemFromSessionStorage("move-in-query-business_trading_name");
			String sessionQueryEmailAdd = storage.getItemFromSessionStorage("move-in-query-email_address");
			String sessionQueryMobNum = storage.getItemFromSessionStorage("move-in-query-mobile_number");
			String sessionQueryBusNum = storage.getItemFromSessionStorage("move-in-query-business_hour_phone");
			String sessionQueryAfterHrNum = storage.getItemFromSessionStorage("move-in-query-after_hour_phone");
			String sessionQueryExtraData = storage.getItemFromSessionStorage("move-in-query-extra_data");
			String sessionQueryConfig = storage.getItemFromSessionStorage("move-in-query-config");
			String sessionReadOnly3rdParty = storage.getItemFromSessionStorage("readOnlyIfThirdParty");
			String sessionExtraData = storage.getItemFromSessionStorage("move-in.extra_data");
			softAssertion.assertFalse(StringUtils.isBlank(sessionQueryMoveInDate), assertionErrorMsg(getLineNumber()));
			softAssertion.assertFalse(StringUtils.isBlank(sessionQueryTenancyStNum),
					assertionErrorMsg(getLineNumber()));
			softAssertion.assertFalse(StringUtils.isBlank(sessionQueryTenancyStName),
					assertionErrorMsg(getLineNumber()));
			softAssertion.assertFalse(StringUtils.isBlank(sessionQueryTenancyStType),
					assertionErrorMsg(getLineNumber()));
			softAssertion.assertFalse(StringUtils.isBlank(sessionQueryTenancyCity), assertionErrorMsg(getLineNumber()));
			softAssertion.assertFalse(StringUtils.isBlank(sessionQueryTenancyState),
					assertionErrorMsg(getLineNumber()));
			softAssertion.assertFalse(StringUtils.isBlank(sessionQueryTenancyPostcode),
					assertionErrorMsg(getLineNumber()));
			softAssertion.assertFalse(StringUtils.isBlank(sessionQueryAcctType), assertionErrorMsg(getLineNumber()));
			softAssertion.assertFalse(StringUtils.isBlank(sessionQueryAbnAcn), assertionErrorMsg(getLineNumber()));
			softAssertion.assertFalse(StringUtils.isBlank(sessionQueryTradingName), assertionErrorMsg(getLineNumber()));
			softAssertion.assertFalse(StringUtils.isBlank(sessionQueryEmailAdd), assertionErrorMsg(getLineNumber()));
			softAssertion.assertFalse(StringUtils.isBlank(sessionQueryMobNum), assertionErrorMsg(getLineNumber()));
			softAssertion.assertFalse(StringUtils.isBlank(sessionQueryBusNum), assertionErrorMsg(getLineNumber()));
			softAssertion.assertFalse(StringUtils.isBlank(sessionQueryAfterHrNum), assertionErrorMsg(getLineNumber()));
			softAssertion.assertFalse(StringUtils.isBlank(sessionQueryExtraData), assertionErrorMsg(getLineNumber()));
			softAssertion.assertFalse(StringUtils.isBlank(sessionQueryConfig), assertionErrorMsg(getLineNumber()));
			softAssertion.assertFalse(StringUtils.isBlank(sessionReadOnly3rdParty), assertionErrorMsg(getLineNumber()));
			softAssertion.assertFalse(StringUtils.isBlank(sessionExtraData), assertionErrorMsg(getLineNumber()));
		} else {
			fail(concatStrings("Verify the assertion(s) for this configuration. Portal type '", getPortalType(),
					"' and Populate Data method '", getPopulateDataMethod(), "'"));
		}
		this.sourceID = sessionSourceId;
		// let's confirm the values stored in the local storage
		String localGrecaptcha = storage.getItemFromLocalStorage("_grecaptcha");
		String localRaygunUserId = storage.getItemFromLocalStorage("raygun4js-userid");
		softAssertion.assertFalse(StringUtils.isBlank(localGrecaptcha), assertionErrorMsg(getLineNumber()));
		softAssertion.assertFalse(StringUtils.isBlank(localRaygunUserId), assertionErrorMsg(getLineNumber()));
		// verify all assertions
		softAssertion.assertAll();

		// verify in the S3 bucket development-presign-upload that the files are there
		// before submitting the request
		if (System.getenv("DELETE_PRESIGN_BUCKET") != null
				&& System.getenv("DELETE_PRESIGN_BUCKET").toLowerCase().equals("yes")) {
			int actualSize = s3Access.getNumOfObjectsInABucket(S3_PORTAL_PRESIGN_BUCKET_NAME);
			List<String> objectIds = s3Access.getObjectIdsInABucket(S3_PORTAL_PRESIGN_BUCKET_NAME);
			logDebugMessage(concatStrings("Before submitting the request for class '", this.className,
					"', actualSize for S3 bucket ", S3_PORTAL_PRESIGN_BUCKET_NAME, " is <",
					Integer.toString(actualSize), "> and attachments ID's is/are -> ", objectIds.toString()));
//			softAssertion.assertEquals(actualSize, 21, "Incorrect number of objects inside the bucket '"
//					.concat(S3_PORTAL_PRESIGN_BUCKET_NAME).concat("'"));
		}
		// verify all assertions
		softAssertion.assertAll();

		scrollPageDown(1500);
		// tick all 3 checkboxes
		clickElementAction(acceptancemovein.firstCheckbox);
		if (getPortalType().equals("standalone")) {
			// we use javaScriptClickButton to click it because the method
			// clickButton mistakenly clicks the link which opens a new tab in standalone
			javaScriptClickElementAction(acceptancemovein.secondCheckbox);
		} else if (getPortalType().equals("embedded")) {
			clickElementAction(acceptancemovein.secondCheckbox);
		}
		clickElementAction(acceptancemovein.thirdCheckbox);

		// add the property files before submitting the request
		addProp("ResiNewContact07_propManMoveInDate", this.propManMoveInDate);
		addProp("ResiNewContact07_propManMoveInDateCRM", this.propManMoveInDateCRM);
		addProp("ResiNewContact07_dateOfBirthAddCont1", this.dateOfBirthAddCont1);
		addProp("ResiNewContact07_sourceID", this.sourceID);
		addProp("ResiNewContact07_dateSubmittedSlash",
				getCurrentDateWithTimeZone(MELBOURNE_TIME_ZONE, DATE_MONTH_YEAR_FORMAT_SLASH));

		// submit the request
		clickElementAction(acceptancemovein.submit);
		// did this because there was an issue where initial click
		// on the submit button did not work
		retryClickSubmit(1);

		// used try/catch because sometimes we get StaleElementReferenceException
		String submitMsg = null;
		boolean submitMsgDisp = false;
		boolean checkSubmitMsg = true;
		try {
			submitMsg = getDisplayedText(acceptancemovein.submittingMessage, true);
			submitMsgDisp = true;
		} catch (StaleElementReferenceException sere) {
			logDebugMessage(concatStrings(
					"StaleElementReferenceException has been encountred while checking for the progress bar text. Will reinitialize the element. See message for more details -> ",
					sere.getMessage()));
			// sometimes the message is not displayed and portal already returned a response
			// so we check if it no longer exists
			acceptancemovein = new AcceptanceMoveIn(driver, 0);
			if (isElementExists(acceptancemovein.submittingMessageList)) {
				submitMsg = getDisplayedText(acceptancemovein.submittingMessage, true);
				submitMsgDisp = true;
			}
		} catch (NoSuchElementException nsee) {
			logDebugMessage(
					"The 'Submitting your request...' message was missed because of retryClickSubmit(1). We will just check for the Success message");
			checkSubmitMsg = false;
		} finally {
			setImplicitWait(PORTAL_IMPLICIT_WAIT_TIMEOUT);
		}
		logDebugMessage(concatStrings("The value of submitMsgDisp [", String.valueOf(submitMsgDisp),
				"] while the value of checkSubmitMsg is [", String.valueOf(checkSubmitMsg), "]"));
		if (submitMsgDisp && checkSubmitMsg) {
			assertEquals(submitMsg, "Submitting your request...", assertionErrorMsg(getLineNumber()));
		}
		submitRequest(acceptancemovein.dialogContainerText);
		// verify it was a success
		String respHeader = getDisplayedText(acceptancemovein.dialogContainerHeader, true);
		softAssertion.assertEquals(respHeader, "Your application has been a success!",
				assertionErrorMsg(getLineNumber()));
		// verify all assertions
		softAssertion.assertAll();

		clickElementAction(acceptancemovein.okDialog);
		pauseSeleniumExecution(1000);
		String closeMsg = getDisplayedText(acceptancemovein.closeMessage, true);
		softAssertion.assertEquals(closeMsg,
				"This window/tab is no longer required, for privacy reasons we encourage you to close it",
				assertionErrorMsg(getLineNumber()));
		sessionKeys = storage.getAllKeysFromSessionStorage();
		sessionLength = storage.getSessionStorageLength();
		logDebugMessage(concatStrings("The value of sessionKeys ", sessionKeys.toString(), " and the size is <",
				String.valueOf(sessionLength), ">"));
		localKeys = storage.getAllKeysFromLocalStorage();
		localLength = storage.getLocalStorageLength();
		logDebugMessage(concatStrings("The value of localKeys ", localKeys.toString(), " and the size is <",
				String.valueOf(localLength), ">"));
		softAssertion.assertEquals(sessionLength, 0, assertionErrorMsg(getLineNumber()));
		softAssertion.assertEquals(localLength, 2, assertionErrorMsg(getLineNumber()));
		// verify all assertions
		softAssertion.assertAll();
	}

}
