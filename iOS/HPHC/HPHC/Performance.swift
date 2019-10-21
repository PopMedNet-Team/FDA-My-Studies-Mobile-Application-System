//
//  Performance.swift
//  HPHC
//
//  Created by Surender on 11/07/19.
//  Copyright © 2019 BTC. All rights reserved.
//

import Foundation

@discardableResult
func measure<A>(name: String, _ block: () -> A) -> A {
    let startTime = CFAbsoluteTimeGetCurrent()
    let result = block()
    let timeElapsed = CFAbsoluteTimeGetCurrent() - startTime
    print("Time: \(name) : \(timeElapsed)")
    return result
}
