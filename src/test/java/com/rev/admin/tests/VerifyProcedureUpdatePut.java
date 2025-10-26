package com.rev.admin.tests;

import com.rev.admin.base.BaseTest;
import com.rev.admin.endpoints.AdminEndpoints;
import com.rev.admin.utils.ApiUtils;
import com.rev.admin.reports.ExtentTestManager; // New Import
import com.aventstack.extentreports.ExtentTest; // New Import
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import com.rev.admin.utils.ConfigReader;

public class VerifyProcedureUpdatePut extends BaseTest{
	
	@Test
	public void procedureUpdatePut() {
		  ExtentTest test = ExtentTestManager.getTest();
		  
		  
		  test.info("Verify the procedure update via put ");
		  Response ProcedureUpdateResponse = ApiUtils.sendPut(AdminEndpoints.Procedure_Update,ConfigReader.get("procedureUpdatebody"));
	

	        try {
	            Assert.assertEquals(ProcedureUpdateResponse.getStatusCode(), 200, "Procedure patch failed (Status Code).");
	            test.pass("ProcedureUpdatePut Passed. Status Code: 200.");
	        } catch (AssertionError e) {
	            test.fail("Failed. Expected Status 200, but got " + ProcedureUpdateResponse.getStatusCode() + ". Error: " + e.getMessage());
	           // test.fail((Throwable) ProcedureUpdateResponse.body());
	           
	            throw e;
	        }
	        System.out.println(ProcedureUpdateResponse.body());
		  
	}
}
