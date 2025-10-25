package com.rev.admin.endpoints;

public class AdminEndpoints {
    
    // Confirmed from logs: Uses /auth/
    public static final String LOGIN = "/v1/admin/auth/signin";
    
    // Confirmed from logs: Does NOT use /auth/
    public static final String REQUEST_CODE = "/v1/admin/signincode"; 
    
    // Confirmed from logs: Does NOT use /auth/
    public static final String VERIFY_OTP = "/v1/admin/signinconsumecode";
    
    // Admin Procedures Loading End Point
   public static final String Procedure_Loaing = "/v1/payload/api/procedures/?limit=10&page=1&searchText=&sort=-updatedAt&where%5Bor%5D%5B0%5D%5BprocedureName%5D%5Blike%5D=";
}