//
//  UITextField+Extension.swift
//  Confidence
//
//  Created by fishbay on 2021/7/4.
//

import UIKit

extension UITextField {
    private struct PlaceholderColorKey {
        static var identifier: String = "PlaceholderColorKey"
    }
    
    var placeholderColor: UIColor {
        get {
            return objc_getAssociatedObject(self, &PlaceholderColorKey.identifier) as! UIColor
        }
        
        set(newColor) {
            objc_setAssociatedObject(self, &PlaceholderColorKey.identifier, newColor, .OBJC_ASSOCIATION_COPY_NONATOMIC)
            let attrString = NSMutableAttributedString(string: self.placeholder ?? "",
                                                       attributes: [NSAttributedString.Key.foregroundColor: newColor,
                                                                    NSAttributedString.Key.font: self.font ?? UIFont.systemFont(ofSize: 15)])
            self.attributedPlaceholder = attrString
        }
    }
}
