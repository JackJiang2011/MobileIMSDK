//
//  LoginController.swift
//  SwiftMobileIMSDK
//
//  Created by fishbay on 2021/8/14.
//

import UIKit

class LoginController: UIViewController, UITextFieldDelegate {
    /// 登陆成功回调
    var onLoginSuccessObserver: ObserverCompletion?
    /// ip输入框
    var ipTextField: UITextField?
    /// 端口号输入框
    var portTextField: UITextField?
    /// 用户名输入框
    var nameTextField: UITextField?
    /// 密码输入框
    var passwordTextField: UITextField?
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.title = "登陆"
        self.view.backgroundColor = .lightGray
        
        // 初始化ui
        initUI()
        
        // 设置回调事件
        initEvent()
    }
    
    func initEvent() {
        // 登陆成功回调
        self.onLoginSuccessObserver = { (observerable: Any?, data: Any?) in
            // 停止登陆进度的定时器
            ProgressHUDUtils.stopTimer()
            
            let code = data as! Int
            if code == 0 {
                CAPrint("登陆成功")
                SceneDelegate.shared?.window?.rootViewController = MainController()
            } else {
                PopupDialogUtils.showDialog(target: self, title: "友情提示", subtitle: "Sorry，登陆失败，错误码=\(code)", successTitle: "知道了") {
                    
                }
            }
            
            // 取消设置好服务端反馈的登陆结果观察者（当客户端收到服务端反馈过来的登陆消息时将被通知）
            let baseEvent = IMClientManager.sharedInstance().baseEventListener as! ChatBaseEvent
            baseEvent.setLoginOkForLaunchObserver(loginOkForLaunchObserver: nil)
        }
    }
    
    func initUI() {
        // 创建ip输入框
        let ipTextField = UITextField.init(frame: CGRect(x: 20, y: 60, width: Int(SCREEN_WIDTH * 0.6), height: FIELD_HEIGHT))
        self.view.addSubview(ipTextField)
        ipTextField.backgroundColor = .white
        ipTextField.textColor = .systemBlue
        ipTextField.text = "127.0.0.1"
        ipTextField.borderStyle = .roundedRect
        ipTextField.font = normalFont(size: 18)
        ipTextField.textAlignment = .left
        ipTextField.contentVerticalAlignment = .center
        ipTextField.delegate = self
        ipTextField.tag = 0
        self.ipTextField = ipTextField
        
        // ip和端口的分隔符
        let separetorLabel = UILabel(frame: CGRect(x: ipTextField.right, y: ipTextField.top + 10, width: 20, height: ipTextField.height - 20))
        self.view.addSubview(separetorLabel)
        separetorLabel.text = ":"
        separetorLabel.textAlignment = .center
        separetorLabel.textColor = .black
        separetorLabel.font = normalFont(size: 18)
        
        // 创建ip输入框
        let portTextField = UITextField.init(frame: CGRect(x: separetorLabel.right, y: ipTextField.top, width: SCREEN_WIDTH - ipTextField.width - 40, height: ipTextField.height))
        self.view.addSubview(portTextField)
        portTextField.backgroundColor = .white
        portTextField.textColor = .systemBlue
        portTextField.text = "8901"
        portTextField.borderStyle = .roundedRect
        portTextField.font = UIFont.systemFont(ofSize: 18)
        portTextField.textAlignment = .left
        portTextField.contentVerticalAlignment = .center
        portTextField.delegate = self
        portTextField.tag = 1
        self.portTextField = portTextField
        
        // 登陆用户名
        let nameTextField = UITextField.init(frame: CGRect(x: ipTextField.left, y: ipTextField.bottom + 10, width: (SCREEN_WIDTH - 50) * 0.5, height: ipTextField.height))
        self.view.addSubview(nameTextField)
        nameTextField.backgroundColor = .white
        nameTextField.textColor = .black
        nameTextField.borderStyle = .roundedRect
        nameTextField.font = UIFont.systemFont(ofSize: 18)
        nameTextField.textAlignment = .left
        nameTextField.contentVerticalAlignment = .center
        nameTextField.placeholder = "登陆用户名"
        nameTextField.placeholderColor = .lightGray
        nameTextField.delegate = self
        nameTextField.tag = 2
        self.nameTextField = nameTextField
        
        // 登陆密码
        let passwordTextField = UITextField.init(frame: CGRect(x: nameTextField.right + 10, y: nameTextField.top, width: nameTextField.width, height: nameTextField.height))
        self.view.addSubview(passwordTextField)
        passwordTextField.backgroundColor = .white
        passwordTextField.textColor = .systemBlue
        passwordTextField.borderStyle = .roundedRect
        passwordTextField.font = UIFont.systemFont(ofSize: 18)
        passwordTextField.textAlignment = .left
        passwordTextField.contentVerticalAlignment = .center
        passwordTextField.placeholder = "登陆密码"
        passwordTextField.placeholderColor = .lightGray
        passwordTextField.delegate = self
        passwordTextField.tag = 3
        self.passwordTextField = passwordTextField
        
        // 分割线
        let separatorLine = UIImageView(frame: CGRect(x: 20, y: nameTextField.bottom + 20, width: SCREEN_WIDTH - 40, height: 2))
        self.view.addSubview(separatorLine)
        separatorLine.image = UIImage(named: "dashed_line")
        
        // 登陆按钮
        let loginButton = UIButton(frame: CGRect(x: separatorLine.left, y: separatorLine.bottom + 40, width: separatorLine.width, height: 50))
        self.view.addSubview(loginButton)
        loginButton.setTitle("登陆", for: .normal)
        loginButton.setTitleColor(.white, for: .normal)
        loginButton.setBackgroundImage(btnBlueBgColor(size: loginButton.frame.size), for: .normal)
        loginButton.corner(radii: 5)
        loginButton.addTarget(self, action: #selector(login), for: .touchUpInside)
        
        // 添加手势，隐藏键盘
        let tap = UITapGestureRecognizer.init(target: self, action: #selector(viewTapped(tap:)))
        tap.cancelsTouchesInView = false
        self.view.addGestureRecognizer(tap)
    }
    
    // MARK: - 登陆
    @objc func login() {
        let ip = self.ipTextField!.text?.trimmingCharacters(in: .whitespacesAndNewlines) ?? ""
        let port = self.portTextField!.text?.trimmingCharacters(in: .whitespacesAndNewlines) ?? ""
        
        if ip.isBlank || port.isBlank {
            ProgressHUD.showError("服务器地址或端口号不能为空")
            return
        }
        
        ConfigEntity.setServerIP(serverIP: ip)
        ConfigEntity.setServerPort(serverPort: Int(port)!)
        
        let name = self.nameTextField!.text?.trimmingCharacters(in: .whitespacesAndNewlines) ?? ""
        let password = self.passwordTextField!.text?.trimmingCharacters(in: .whitespacesAndNewlines) ?? ""
        
        if name.isBlank || password.isBlank {
            ProgressHUD.showError("用户名或密码不正确")
            return
        }
        
        // 设置好服务端反馈的登陆结果观察者（当客户端收到服务端反馈过来的登陆消息时将被通知）
        (IMClientManager.sharedInstance().baseEventListener as! ChatBaseEvent).setLoginOkForLaunchObserver(loginOkForLaunchObserver: self.onLoginSuccessObserver)
        
        // 创建登陆对象
        let loginInfo = LoginInfo()
        loginInfo.loginUserId = name
        loginInfo.loginToken = password
        
        let result = LocalDataSender.sharedInstance().sendLogin(loginInfo: loginInfo)
        if result == .commonCodeOK {
            // 显示发送进度
            ProgressHUDUtils.showLoginProgress {
                // 停止登陆进度的定时器
                ProgressHUDUtils.stopTimer()
                
                PopupDialogUtils.showDialog(target: self,
                                            title: "超时了",
                                            subtitle: "登陆超时，可能是网络故障或服务器无法连接，是否重试？",
                                            successTitle: "重试！") {
                    
                    self.login()
                } cancel: {
                    CAPrint("取消")
                }
            }
        } else {
            ProgressHUD.showError("登陆请求发送失败，错误码：\(result)")
        }
    }
    
    // MARK: - 输入框代理
    func textFieldShouldBeginEditing(_ textField: UITextField) -> Bool {
        return true
    }
    
    // MARK: - 手势动作
    @objc func viewTapped(tap: UITapGestureRecognizer) {
        self.view.endEditing(true)
    }
}
