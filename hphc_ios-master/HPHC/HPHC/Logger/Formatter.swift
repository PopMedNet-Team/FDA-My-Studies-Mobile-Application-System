//
//  Formatter.swift
//  Logger
//
//  Created by xdf on 3/20/16.
//  Copyright © 2016 xdf. All rights reserved.
//

open class Formatter {

    internal func format(level: Level, items: [Any], file: String, line: Int, column: Int) -> String {
        var items = items
        let fileInfo = ">> \(level.toUpperCase) \(line):\(column)"
        items.insert(fileInfo, at: 0)
        return items.map({
            String(describing: $0)
        }).joined(separator: " ") + "\n"
    }
}
