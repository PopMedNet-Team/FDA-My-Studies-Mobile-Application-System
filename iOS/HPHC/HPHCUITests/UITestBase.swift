//
//  UITestBase.swift
//  SafePassageDriverUITests
//
//  Created by Surender on 30/07/19.
//  Copyright © 2019 BTC. All rights reserved.
//

import Foundation
import XCTest
import Embassy
import EnvoyAmbassador

class UITestBase: XCTestCase {
    let port = 8080
    var router: Router!
    var eventLoop: EventLoop!
    var server: HTTPServer!
    var app: XCUIApplication!
    
    var eventLoopThreadCondition: NSCondition!
    var eventLoopThread: Thread!
    
    override func setUp() {
        super.setUp()
        setupWebApp()
        setupApp()
    }
    
    // setup the Embassy web server for testing
    private func setupWebApp() {
        eventLoop = try! SelectorEventLoop(selector: try! KqueueSelector())
        router = DefaultRouter()
        server = DefaultHTTPServer(eventLoop: eventLoop, port: 8080, app: router.app)
        
        // Start HTTP server to listen on the port
        try! server.start()
        
        //eventLoop.runForever()
        
        eventLoopThreadCondition = NSCondition()
        eventLoopThread = Thread(target: self, selector: #selector(runEventLoop), object: nil)
        eventLoopThread.start()
    }
    
    // set up XCUIApplication
    private func setupApp() {
        app = XCUIApplication()
        app.launchEnvironment["RESET_LOGIN"] = "1"
        app.launchArguments += ["UI-TESTING"]
        app.launchEnvironment["ENVOY_BASEURL"] = "http://localhost:\(port)"
    }
    
    override func tearDown() {
        super.tearDown()
        app.terminate()
        server.stopAndWait()
        eventLoopThreadCondition.lock()
        eventLoop.stop()
        while eventLoop.running {
            if !eventLoopThreadCondition.wait(until: NSDate().addingTimeInterval(10) as Date) {
                fatalError("Join eventLoopThread timeout")
            }
        }
    }
    
    @objc private func runEventLoop() {
        eventLoop.runForever()
//        eventLoopThreadCondition.lock()
//        eventLoopThreadCondition.signal()
//        eventLoopThreadCondition.unlock()
    }
}
