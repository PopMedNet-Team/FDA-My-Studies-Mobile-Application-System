//
//  LoginVCTests.swift
//  HPHCUITests
//
//  Created by Surender on 12/08/19.
//  Copyright © 2019 BTC. All rights reserved.
//

import XCTest
import Embassy
import EnvoyAmbassador

class LoginVCTests: UITestBase {

    
    func testLoginVCFlows() {
        
        app.launch()
        app.buttons["Sign in"].tap()
        app.buttons["Forgot password?"].tap()
        app.navigationBars["PASSWORD HELP"].buttons["backIcon"].tap()
        
        let textViewsQuery = app.textViews
        textViewsQuery.links["Terms"].tap()
        app.navigationBars["TERMS"].buttons["Cancel"].tap()
        textViewsQuery.links["Privacy Policy"].tap()
        app.navigationBars["PRIVACY POLICY"].buttons["Cancel"].tap()
        app.navigationBars["SIGN IN"].buttons["info"].tap()
        app.alerts["Why Register?"].buttons["OK"].tap()
        app.buttons["New User? Sign Up"].tap()
        app.navigationBars["SIGN UP"].buttons["backIcon"].tap()
        let butttonSignin = app.buttons["Sign In"]
        XCTAssertTrue(butttonSignin.exists)
        
    }
    
    func testLogin() {
        
        
        router[DefaultRouter.loginPath] = JSONResponse() { _ -> Any in
            return [
                "auth": "573973581",
                "verified": false,
                "message": "success",
                "userId": "b081ad2d-aff4-4f95-9f2c-e7b8541f3431",
                "refreshToken": "54914753-89eb-4bfd-af07-6a07ba4065e6"
            ]
        }
        
        app.launch()
       
        app.buttons["Sign in"].tap()
        let tfEmail = app.textFields["enter email"]
        let passwordtf =  app.secureTextFields["enter password"]
        let butttonSignin = app.buttons["Sign In"]
        let toolbarBttonDone =  app.toolbars["Toolbar"].buttons["Done"]
        
        tfEmail.tap()
        tfEmail.typeText("suri@grr.la")
        
        passwordtf.tap()
        passwordtf.typeText("Password@1")
        toolbarBttonDone.tap()
        butttonSignin.tap()
        
       
        XCTAssertFalse(butttonSignin.exists)
        
        
    }

}
extension XCUIElement {
    func clearText(andReplaceWith newText:String? = nil) {
        tap()
        tap() //When there is some text, its parts can be selected on the first tap, the second tap clears the selection
        press(forDuration: 1.0)
        let selectAll = XCUIApplication().menuItems["Select All"]
        //For empty fields there will be no "Select All", so we need to check
        if selectAll.waitForExistence(timeout: 0.5), selectAll.exists {
            selectAll.tap()
            typeText(String(XCUIKeyboardKey.delete.rawValue))
        }
        if let newVal = newText { typeText(newVal) }
    }
}
