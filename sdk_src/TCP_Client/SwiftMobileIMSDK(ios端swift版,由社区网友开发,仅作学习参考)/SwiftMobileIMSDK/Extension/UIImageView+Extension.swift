//
//  UIImageView+Extension.swift
//  Confidence
//
//  Created by fishbay on 2021/7/9.
//

import Foundation

extension UIImageView {
    
    func addCorner(radius: CGFloat) {
        self.image = self.image?.kt_drawRectWithRoundedCorner(radius: radius, self.bounds.size)
    }
}
