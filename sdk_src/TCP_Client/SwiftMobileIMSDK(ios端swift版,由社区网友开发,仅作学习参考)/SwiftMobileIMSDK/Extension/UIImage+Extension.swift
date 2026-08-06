//
//  UIImage+Extension.swift
//  Confidence
//
//  Created by fishbay on 2021/7/3.
//

import UIKit

extension UIImage {
    // 生成圆形图片
    func toCircle() -> UIImage {
        // 取最短边长
        let shotest = min(size.width, size.height)
        
        // 输出尺寸
        let outputRect = CGRect(x: 0, y: 0, width: shotest, height: shotest)
        
        // 开始图片处理上下文（由于输出的图不会进行缩放，所以缩放因子等于屏幕的scale即可）
        UIGraphicsBeginImageContextWithOptions(outputRect.size, false, 0)
        
        let context = UIGraphicsGetCurrentContext()!
        // 添加圆形裁剪区域
        context.addEllipse(in: outputRect)
        context.clip()
        
        // 绘制图片
        draw(in: CGRect(x: (shotest - size.width) / 2,
                        y: (shotest - size.height) / 2,
                        width: size.width,
                        height: size.height))
        // 获得处理后的图片
        let maskedImage = UIGraphicsGetImageFromCurrentImageContext()!
        
        UIGraphicsEndImageContext()
        
        return maskedImage
    }
    
    func kt_drawRectWithRoundedCorner(radius: CGFloat, _ sizetoFit: CGSize) -> UIImage {
        let rect = CGRect(origin: CGPoint(x: 0, y: 0), size: sizetoFit)
        
        UIGraphicsBeginImageContextWithOptions(rect.size, false, UIScreen.main.scale)
        
        UIGraphicsGetCurrentContext()!.addPath(UIBezierPath(roundedRect: rect, byRoundingCorners: UIRectCorner.allCorners,
                                                            cornerRadii: CGSize(width: radius, height: radius)).cgPath)
        
        UIGraphicsGetCurrentContext()!.clip()
        
        self.draw(in: rect)
        
        UIGraphicsGetCurrentContext()!.drawPath(using: .fillStroke)
        
        let output = UIGraphicsGetImageFromCurrentImageContext()!;
        
        UIGraphicsEndImageContext();
        
        return output
        
    }
}
