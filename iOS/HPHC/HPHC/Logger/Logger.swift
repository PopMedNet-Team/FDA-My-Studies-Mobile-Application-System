//
//  Logger.swift
//  Logger
//
//  Created by xdf on 3/20/16.
//  Copyright © 2016 xdf. All rights reserved.
//

import UIKit

public enum Level {
    case info, debug, warn, error
    
    var toUpperCase: String {
        return String(describing: self).uppercased()
    }
    
}

open class Logger {

    open var formatter = Formatter()

    static let sharedInstance = Logger()
    
    fileprivate func logger(_ level: Level, items: [Any], file: String, line: Int, column: Int) {

        
        let result = formatter.format(
            level: level,
            items: items,
            file: file,
            line: line,
            column: column
        )

        print(Date(),result, separator: "", terminator: "")
        
    }
    
    public init() {
    }
    
    open func info(_ items: Any..., file: String = #file, line: Int = #line, column: Int = #column) {
        logger(.info, items: items, file: file, line: line, column: column)
    }
    
    
    open func debug(_ items: Any..., file: String = #file, line: Int = #line, column: Int = #column) {
        logger(.debug, items: items, file: file, line: line, column: column)
    }
    
    open func warn(_ items: Any..., file: String = #file, line: Int = #line, column: Int = #column) {
        logger(.warn, items: items, file: file, line: line, column: column)
    }
    
    open func error(_ items: Any..., file: String = #file, line: Int = #line, column: Int = #column) {
        logger(.error, items: items, file: file, line: line, column: column)
    }
}
