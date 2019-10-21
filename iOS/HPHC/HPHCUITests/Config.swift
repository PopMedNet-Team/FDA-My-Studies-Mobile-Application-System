//
//  Config.swift
//  SafePassageDriverUITests
//
//  Created by Surender on 30/07/19.
//  Copyright © 2019 BTC. All rights reserved.
//

import Embassy
import EnvoyAmbassador

class DefaultRouter: Router {
    //https://testapi.io/api/invinciible/login?token={token_here}
    static let loginPath = "login.api"

    override init() {
        super.init()
        self[DefaultRouter.loginPath] = DelayResponse(JSONResponse(handler: ({ environ -> Any in
            return [
                "userId": "12",
                "accessToken": "alkdsjaldjll",
                "refreshToken": "Adaddaddad",
                "tempPassword": true
            ]
        })))
    }
}
