package com.rev.admin.tests;

import org.testng.Assert;
import org.testng.annotations.Test;
import io.restassured.response.Response;

import com.aventstack.extentreports.ExtentTest;
import com.rev.admin.base.BaseTest;
import com.rev.admin.endpoints.AdminEndpoints;
import com.rev.admin.reports.ExtentTestManager;
import com.rev.admin.utils.ApiUtils;
import com.rev.admin.utils.ConfigReader;
import com.rev.admin.utils.TokenManager;


public class ProcedrueLoading extends BaseTest {
	
@Test	
public void procedureLoadingStatusCodeAndResposeVerification(){
	
	ExtentTest test = ExtentTestManager.getTest();
   
    // Uses the utility method that relies on the RestAssured specification 
    // configured in BaseTest (which contains the sAccessToken cookie).
    
   // System.out.println("From Procedure : "+FrontToken);
	
	 test.info("Procedures Loading test");
    Response VerifyProcedreLoading = ApiUtils.SendGetWithAccessTokenCookie(ConfigReader.get("baseUrl")+AdminEndpoints.Procedure_Loaing,Cookie);
System.out.println("\n--- Procedure Loading Response ---");
System.out.println("Status Code: " + VerifyProcedreLoading.getStatusCode());
System.out.println("Response Body: " + VerifyProcedreLoading.getBody().asString());
System.out.println("----------------------------------\n");

if(VerifyProcedreLoading.getStatusCode()==200) {
	Assert.assertEquals(VerifyProcedreLoading.getStatusCode(), 200,
"Get Procedure Loading Failed (Status Code). Response: " + VerifyProcedreLoading.getBody().asString());
test.pass("Procedures loading successfully");
}
else
	test.fail("Status code not matched");
}
}