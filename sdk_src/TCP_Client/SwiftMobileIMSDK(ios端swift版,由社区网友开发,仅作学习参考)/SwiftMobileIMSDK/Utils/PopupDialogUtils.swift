//
//  PopupDialogUtils.swift
//  SwiftMobileIMSDK
//
//  Created by fishbay on 2021/8/19.
//

import Foundation

class PopupDialogUtils {
    
    /// 交互弹框，一个按钮
    /// - Parameters:
    ///   - target: 目标对象
    ///   - title: 弹框标题
    ///   - subtitle: 副标题
    ///   - success: 确定按钮回调
    ///   - successTitle: 确定按钮标题
    static func showDialog(target: UIViewController,
                           title: String,
                           subtitle: String?,
                           successTitle: String? = "确定",
                           success: @escaping () -> Void
                           ) {
        // Create the dialog
        let popup = PopupDialog(title: title,
                                message: subtitle,
                                buttonAlignment: .horizontal,
                                transitionStyle: .zoomIn,
                                tapGestureDismissal: true,
                                panGestureDismissal: true,
                                hideStatusBar: true) {
            print("Completed")
        }
        
        let okButton = DefaultButton(title: successTitle!) {
            success()
        }
        
        popup.addButtons([okButton])
        
        target.present(popup, animated: true, completion: nil)
    }
    
    /// 交互弹框，两个按钮
    /// - Parameters:
    ///   - target: 目标对象
    ///   - title: 弹框标题
    ///   - subtitle: 副标题
    ///   - success: 确定按钮回调
    ///   - successTitle: 确定按钮标题
    ///   - cancel: 取消按钮回调
    ///   - cancelTitle: 取消按钮标题
    static func showDialog(target: UIViewController,
                           title: String,
                           subtitle: String?,
                           successTitle: String? = "确定",
                           cancelTitle: String? = "取消",
                           success: @escaping () -> Void,
                           cancel: @escaping () -> Void
                           ) {
        // Create the dialog
        let popup = PopupDialog(title: title,
                                message: subtitle,
                                buttonAlignment: .horizontal,
                                transitionStyle: .zoomIn,
                                tapGestureDismissal: true,
                                panGestureDismissal: true,
                                hideStatusBar: true) {
            print("Completed")
        }
        
        let cancelButton = CancelButton(title: cancelTitle!) {
            cancel()
        }
        
        let okButton = DefaultButton(title: successTitle!) {
            success()
        }
        
        popup.addButtons([cancelButton, okButton])
        
        target.present(popup, animated: true, completion: nil)
    }
}
