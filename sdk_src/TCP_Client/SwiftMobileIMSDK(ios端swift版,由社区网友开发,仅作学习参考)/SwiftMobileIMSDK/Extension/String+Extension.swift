//
//  String+Extension.swift
//  Confidence
//
//  Created by fishbay on 2021/7/8.
//

import Foundation

extension String{
    // check string cellection is whiteSpace
    var isBlank : Bool{
        return allSatisfy({$0.isWhitespace})
    }
    
    // check string cellection is whiteSpace
    var isNotBlank : Bool{
        return !self.isBlank
    }
}

extension Optional where Wrapped == String{
    var isBlank : Bool{
        return self?.isBlank ?? true
    }
}
