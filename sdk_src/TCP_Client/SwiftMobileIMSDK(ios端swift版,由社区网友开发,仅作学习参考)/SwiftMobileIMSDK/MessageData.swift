//
//  MessageData.swift
//  SwiftMobileIMSDK
//
//  Created by fishbay on 2021/8/21.
//

import UIKit

class MessageData {
    var content: String?
    var color: UIColor?
    
    init() {}
    
    init(content: String?, color: UIColor?) {
        self.content = content
        self.color = color
    }
}
