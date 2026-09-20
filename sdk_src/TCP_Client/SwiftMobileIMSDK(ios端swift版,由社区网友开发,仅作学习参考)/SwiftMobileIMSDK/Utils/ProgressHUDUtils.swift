//
//  ProgressHUDUtils.swift
//  SwiftMobileIMSDK
//
//  Created by fishbay on 2021/8/18.
//

import Foundation

class ProgressHUDUtils {
    static var timer: Timer?
    
    ///   展示进度条
    /// - Parameters:
    ///   - status: 提示文本
    ///   - step: 步长，总长度是1.0
    static func showProgress(status: String? = nil, step: TimeInterval? = 0.025) {
        // 如果timer做运行，先销毁
        Self.timer?.invalidate()
        Self.timer = nil
        
        var progress: CGFloat = 0.0
        ProgressHUD.showProgress(status, progress)
        
        // 创建timer
        Self.timer = Timer.scheduledTimer(withTimeInterval: step! , repeats: true, block: { timer in
            progress += 1
            ProgressHUD.showProgress(status, progress / 100)
            
            if progress >= 100 {
                Self.timer?.invalidate()
                Self.timer = nil
                
                DispatchQueue.main.asyncAfter(deadline: .now() + 0.3) {
                    ProgressHUD.showSuccess(interaction: false)
                }
            }
        })
        
        // 立即执行
        self.timer!.fire()
    }
    
    /// 展示登录状态
    /// - Parameters:
    ///   - timeout: 超时时间，默认6s
    ///   - timeoutHandle: 超时回调
    static func showLoginProgress(timeout: TimeInterval = 6,
                                  timeoutHandle: @escaping () -> Void) {
        // 如果timer做运行，先销毁
        Self.timer?.invalidate()
        Self.timer = nil
        
        ProgressHUD.animationType = .circleSpinFade
        ProgressHUD.show()
        
        // 创建timer
        Self.timer = Timer.scheduledTimer(withTimeInterval: timeout, repeats: false, block: { _ in
            ProgressHUD.dismiss()
            timeoutHandle()
        })
    }
    
    // 停止定时器
    static func stopTimer() {
        if Self.timer == nil {
            return
        }
        
        ProgressHUD.dismiss()
        
        if Self.timer!.isValid {
            Self.timer!.invalidate()
        }
        Self.timer = nil
    }
}
