//
//  MainController.swift
//  SwiftMobileIMSDK
//
//  Created by fishbay on 2021/8/20.
//

import UIKit

class MainController: UIViewController, UITextFieldDelegate, UITableViewDelegate, UITableViewDataSource {
    /// 通信状态指示图标
    var statusIndicatorImage: UIImageView?
    /// 通信状态文案
    var statusMessage: UILabel?
    /// 当前账户名称
    var userLabel: UILabel?
    /// 接收方id输入框
    var receiverTextField: UITextField?
    /// 发送的消息输入框
    var messageTextField: UITextField?
    
    /// 掉线重连图标
    var redirectImage: UIImageView?
    /// 心跳图标
    var keepAliveImage: UIImageView?
    /// QoS发送图标
    var qosSendImage: UIImageView?
    /// QoS接收图标
    var qosReceiverImage: UIImageView?
    
    /// 消息列表视图
    var tableView: UITableView?
    /// 消息列表数据
    var chatList = [MessageData]()
    
    let dateFormatter = DateFormatter()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.view.backgroundColor = .lightGray
        
        self.dateFormatter.dateFormat = "HH:mm:ss"
        
        // 初始化UI
        initUI()
        
        // debug
        initDebug()
        
        // 通知
        initNotification()
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        
        self.refreshConnectStatus()
        self.refreshMobileSDKThreadStatus()
        
        // 将当前账号显示出来
        self.userLabel?.text = ClientCoreSDK.sharedInstance().getCurrentLoginInfo()?.loginUserId
    }
    
    private func initUI() {
        // 通信状态标签
        let statusLabel = UILabel.init(frame: CGRect(x: 20, y: 60, width: 50, height: 22))
        self.view.addSubview(statusLabel)
        statusLabel.text = "通信状态: "
        statusLabel.textColor = .black
        statusLabel.font = normalFont(size: 18)
        statusLabel.sizeToFit()
        
        // 通信状态指示图标
        let statusIndicatorImage = UIImageView.init(frame: CGRect(x: statusLabel.right + 3, y: 0, width: 16, height: 16))
        self.view.addSubview(statusIndicatorImage)
        self.statusIndicatorImage = statusIndicatorImage
        statusIndicatorImage.centerY = statusLabel.centerY
        statusIndicatorImage.image = UIImage(named: "gray")
        
        // 通信状态文案
        let statusMessage = UILabel.init(frame: CGRect(x: statusIndicatorImage.right + 3, y: 0, width: 50, height: 22))
        self.view.addSubview(statusMessage)
        self.statusMessage = statusMessage
        statusMessage.centerY = statusIndicatorImage.centerY
        statusMessage.text = "连接断开"
        statusMessage.textColor = .magenta
        statusMessage.font = normalFont(size: 18)
        statusMessage.sizeToFit()
        
        // 当前账户标签
        let accountLabel = UILabel.init(frame: CGRect(x: 20, y: statusLabel.bottom + 3, width: 50, height: 22))
        self.view.addSubview(accountLabel)
        accountLabel.text = "当前账号: "
        accountLabel.textColor = .gray
        accountLabel.font = normalFont(size: 18)
        accountLabel.sizeToFit()
        
        // 当前账户名称
        let userLabel = UILabel(frame: CGRect(x: accountLabel.right + 3, y: 0, width: 50, height: 22))
        self.view.addSubview(userLabel)
        self.userLabel = userLabel
        userLabel.centerY = accountLabel.centerY
        userLabel.text = "-"
        userLabel.textColor = .blue
        userLabel.font = normalFont(size: 18)
        
        // 退出登陆
        let logoutButton = UIButton.init(frame: CGRect(x: SCREEN_WIDTH - 140, y: statusLabel.top + 3, width: 120, height: 40))
        self.view.addSubview(logoutButton)
        logoutButton.setTitle("退出登陆", for: .normal)
        logoutButton.setTitleColor(.white, for: .normal)
        logoutButton.titleLabel?.font = normalFont(size: 20)
        logoutButton.setBackgroundImage(imageFromColor(color: .orange, viewSize: logoutButton.frame.size), for: .normal)
        logoutButton.addTarget(self, action: #selector(logout), for: .touchUpInside)
        
        // 分割线
        let separatorLine1 = UIImageView(frame: CGRect(x: 0, y: userLabel.bottom + 10, width: SCREEN_WIDTH, height: 2))
        self.view.addSubview(separatorLine1)
        separatorLine1.image = UIImage(named: "dashed_line")
        
        // 接收方id输入框
        let receiverTextField = UITextField.init(frame: CGRect(x: 20, y: separatorLine1.bottom + 10, width: 120, height: 40))
        self.view.addSubview(receiverTextField)
        self.receiverTextField = receiverTextField
        receiverTextField.backgroundColor = .white
        receiverTextField.textColor = .black
        receiverTextField.borderStyle = .roundedRect
        receiverTextField.font = normalFont(size: 16)
        receiverTextField.textAlignment = .left
        receiverTextField.contentVerticalAlignment = .center
        receiverTextField.placeholder = "接收方的id"
        receiverTextField.placeholderColor = UIColor.gray
        receiverTextField.delegate = self
        
        // 发送的消息输入框
        let messageTextField = UITextField.init(frame: CGRect(x: receiverTextField.right + 15, y: receiverTextField.top, width: SCREEN_WIDTH - receiverTextField.right - 35, height: 40))
        self.view.addSubview(messageTextField)
        self.messageTextField = messageTextField
        messageTextField.backgroundColor = .white
        messageTextField.textColor = .black
        messageTextField.borderStyle = .roundedRect
        messageTextField.font = normalFont(size: 16)
        messageTextField.textAlignment = .left
        messageTextField.contentVerticalAlignment = .center
        messageTextField.placeholder = "输入要发送的消息"
        messageTextField.placeholderColor = UIColor.gray
        messageTextField.delegate = self
        
        // 发送按钮
        let sendButton = UIButton.init(frame: CGRect(x: 20, y: receiverTextField.bottom + 15, width: SCREEN_WIDTH - 40, height: 40))
        self.view.addSubview(sendButton)
        sendButton.setTitle("发送消息", for: .normal)
        sendButton.setTitleColor(.white, for: .normal)
        sendButton.titleLabel?.font = normalFont(size: 20)
        sendButton.setBackgroundImage(imageFromColor(color: .systemBlue, viewSize: logoutButton.frame.size), for: .normal)
        sendButton.addTarget(self, action: #selector(sendMessage), for: .touchUpInside)
        
        // 分割线
        let separatorLine2 = UIImageView(frame: CGRect(x: 0, y: sendButton.bottom + 10, width: SCREEN_WIDTH, height: 2))
        self.view.addSubview(separatorLine2)
        separatorLine2.image = UIImage(named: "dashed_line")
        
        // 线程动态标签
        let networkLabel = UILabel(frame: CGRect(x: 20, y: separatorLine2.bottom + 10, width: 30, height: 18))
        self.view.addSubview(networkLabel)
        networkLabel.text = "线程动态: "
        networkLabel.textColor = .systemBlue
        networkLabel.font = normalFont(size: 13)
        networkLabel.sizeToFit()
        
        // 掉线重连图标
        let redirectImage = UIImageView.init(frame: CGRect(x: networkLabel.right + 3, y: 0, width: 13, height: 13))
        self.view.addSubview(redirectImage)
        self.redirectImage = redirectImage
        redirectImage.centerY = networkLabel.centerY
        redirectImage.image = UIImage(named: "gray")
        
        // 掉线重连标签
        let redirectLabel = UILabel(frame: CGRect(x: redirectImage.right + 2, y: networkLabel.top, width: 50, height: 18))
        self.view.addSubview(redirectLabel)
        redirectLabel.text = "掉线重连"
        redirectLabel.textColor = .gray
        redirectLabel.font = normalFont(size: 13)
        redirectLabel.sizeToFit()
        
        // 心跳图标
        let keepAliveImage = UIImageView.init(frame: CGRect(x: redirectLabel.right + 5, y: 0, width: 13, height: 13))
        self.view.addSubview(keepAliveImage)
        self.keepAliveImage = keepAliveImage
        keepAliveImage.centerY = networkLabel.centerY
        keepAliveImage.image = UIImage(named: "gray")
        
        // 心跳标签
        let keepAliveLabel = UILabel(frame: CGRect(x: keepAliveImage.right + 2, y: networkLabel.top, width: 50, height: 18))
        self.view.addSubview(keepAliveLabel)
        keepAliveLabel.text = "KeepAlive"
        keepAliveLabel.textColor = .gray
        keepAliveLabel.font = normalFont(size: 13)
        keepAliveLabel.sizeToFit()
        
        // QoS发送图标
        let qosSendImage = UIImageView.init(frame: CGRect(x: keepAliveLabel.right + 5, y: 0, width: 13, height: 13))
        self.view.addSubview(qosSendImage)
        self.qosSendImage = qosSendImage
        qosSendImage.centerY = networkLabel.centerY
        qosSendImage.image = UIImage(named: "gray")
        
        // QoS发送标签
        let qosSendLabel = UILabel(frame: CGRect(x: qosSendImage.right + 2, y: networkLabel.top, width: 50, height: 18))
        self.view.addSubview(qosSendLabel)
        qosSendLabel.text = "QoS(发)"
        qosSendLabel.textColor = .gray
        qosSendLabel.font = normalFont(size: 13)
        qosSendLabel.sizeToFit()
        
        // QoS接收图标
        let qosReceiverImage = UIImageView.init(frame: CGRect(x: qosSendLabel.right + 5, y: 0, width: 13, height: 13))
        self.view.addSubview(qosReceiverImage)
        self.qosReceiverImage = qosReceiverImage
        qosReceiverImage.centerY = networkLabel.centerY
        qosReceiverImage.image = UIImage(named: "gray")
        
        // QoS接收标签
        let qosReceiverLabel = UILabel(frame: CGRect(x: qosReceiverImage.right + 2, y: networkLabel.top, width: 50, height: 18))
        self.view.addSubview(qosReceiverLabel)
        qosReceiverLabel.text = "QoS(发)"
        qosReceiverLabel.textColor = .gray
        qosReceiverLabel.font = normalFont(size: 13)
        qosReceiverLabel.sizeToFit()
        
        // 消息列表
        let tableView = UITableView.init(frame: CGRect(x: 0, y: networkLabel.bottom + 10, width: SCREEN_WIDTH, height: SCREEN_HEIGHT - networkLabel.bottom - 10))
        self.view.addSubview(tableView)
        self.tableView = tableView
        tableView.backgroundColor = .white
        tableView.estimatedRowHeight = 0
        tableView.delegate = self
        tableView.dataSource = self
        tableView.sectionHeaderHeight = 0
        tableView.sectionFooterHeight = 10
        tableView.showsVerticalScrollIndicator = false
        // 注册cell
        tableView.register(MessageCell.self, forCellReuseIdentifier: description)
        
        // 添加手势，隐藏键盘
        let tap = UITapGestureRecognizer.init(target: self, action: #selector(viewTapped(tap:)))
        tap.cancelsTouchesInView = false
        self.view.addGestureRecognizer(tap)
    }
    
    private func initDebug() {
        self.setupAnimation(imageView: self.redirectImage!)
        self.setupAnimation(imageView: self.keepAliveImage!)
        self.setupAnimation(imageView: self.qosSendImage!)
        self.setupAnimation(imageView: self.qosReceiverImage!)
        
        AutoReLoginDaemon.sharedInstance().setDebugObserver(debugObserver: self.createObserverCompletion(imageView: self.redirectImage!))
        KeepAliveDaemon.sharedInstance().setDebugObserver(debugObserver: self.createObserverCompletion(imageView: self.keepAliveImage!))
        QoS4SendDaemon.sharedInstance().setDebugObserver(debugObserver: self.createObserverCompletion(imageView: self.qosSendImage!))
        QoS4ReceiveDaemon.sharedInstance().setDebugObserver(debugObserver: self.createObserverCompletion(imageView: self.qosReceiverImage!))
    }
    
    private func initNotification() {
        // 注册消息列表的通知
        NotificationCenter.default.addObserver(self, selector: #selector(updateChatList), name: .MyNotificationUpdateChatList, object: nil)
        // 注册刷新网络连接状态的通知
        NotificationCenter.default.addObserver(self, selector: #selector(refreshConnectStatus), name: .MyNotificationRefreshConnectStatus, object: nil)
        // 注册退出登陆的通知
        NotificationCenter.default.addObserver(self, selector: #selector(logout), name: .MyNotificationKickout, object: nil)
    }
    
    /// 通知处理方法
    @objc func updateChatList(notification: Notification) {
        CAPrint("收到通知：\(notification)")
        
        let messageData = notification.object as! MessageData
        if messageData.content == nil || messageData.color == nil {
            return
        }
        
        showIMInfo(content: messageData.content!, color: messageData.color!)
    }
    
    private func setupAnimation(imageView: UIImageView) {
        imageView.animationImages = [UIImage.init(named: "green_light")!, UIImage.init(named: "green")!]
        imageView.animationDuration = 0.5
        imageView.animationRepeatCount = 1
    }
    
    func createObserverCompletion(imageView: UIImageView) -> ObserverCompletion {
        let block = { (observerable: Any?, data: Any?) in
            let status = data as! Int
            self.showStatusImage(status: status, imageView: imageView)
        }
        
        return block
    }
    
    /// 显示网络状态图片
    func showStatusImage(status: Int, imageView: UIImageView) {
        if imageView.isHidden {
            imageView.isHidden = false
        }
        
        if status == 1 {
            // 确保先stop ，否则正在动画中时此时设置图片则只会停在动画的最后一帧
            if imageView.isAnimating {
                imageView.stopAnimating()
            }
            
            imageView.image = UIImage.init(named: "green")!
        } else if status == 2 {
            imageView.image = UIImage.init(named: "green")
            
            if imageView.isAnimating {
                imageView.stopAnimating()
            }
            
            imageView.startAnimating()
        } else {
            // 确保先stop ，否则正在动画中时此时设置图片则只会停在动画的最后一帧
            if imageView.isAnimating {
                imageView.stopAnimating()
            }
            
            imageView.image = UIImage.init(named: "gray")
        }
    }
    
    /// 刷新网络状态
    @objc func refreshConnectStatus() {
        let isConnected = ClientCoreSDK.sharedInstance().connectedToServer
        if isConnected {
            self.statusMessage?.text = "通信正常"
            self.statusMessage?.textColor = rgb(r: 66/255, g: 201/255, b: 88/255)
            self.statusIndicatorImage?.image = UIImage.init(named: "green")
        } else {
            self.statusMessage?.text = "连接断开"
            self.statusMessage?.textColor = rgb(r: 255/255, g: 0/255, b: 255/255)
            self.statusIndicatorImage?.image = UIImage.init(named: "gray")
        }
    }
    
    func refreshMobileSDKThreadStatus() {
        self.showStatusImage(status: AutoReLoginDaemon.sharedInstance().isAutoReLoginRunning() ? 1: 0, imageView: self.redirectImage!)
        self.showStatusImage(status: KeepAliveDaemon.sharedInstance().isKeepAliveRunning() ? 1 : 0, imageView: self.keepAliveImage!)
        self.showStatusImage(status: QoS4SendDaemon.sharedInstance().isRunning() ? 1 : 0, imageView: self.qosSendImage!)
        self.showStatusImage(status: QoS4ReceiveDaemon.sharedInstance().isRunning() ? 1 : 0, imageView: self.qosReceiverImage!)
    }
    
    /// 发送IM消息
    func showIMInfo(content: String, color: UIColor) {
        let messageData = MessageData()
        messageData.content = "[\(dateFormatter.string(from: Date()))] \(content)"
        messageData.color = color
        
        self.chatList.append(messageData)
        self.tableView?.reloadData()
        
        // 自动显示最后一行
        let section = self.tableView?.numberOfSections ?? 0
        if section < 1 {
            return
        }
        
        let row = self.tableView?.numberOfRows(inSection: section - 1) ?? 0
        if row < 1 {
            return
        }
        
        let indexPath = IndexPath.init(row: row - 1, section: section - 1)
        self.tableView?.scrollToRow(at: indexPath, at: .bottom, animated: true)
    }
    
    // 根据显示内容计算行高
    func calculateCellSize(indexPath: IndexPath) -> CGSize {
        if self.chatList.count <= 0 {
            return CGSize(width: self.tableView!.width, height: 16)
        }
        
        let messageData = self.chatList[indexPath.section]
        let rect = messageData.content!.boundingRect(with: CGSize(width: SCREEN_WIDTH * 0.7, height: 1000), options: .usesLineFragmentOrigin, attributes: [.font : normalFont(size: 16)] , context: nil)
        return rect.size
    }
    
    // MARK: - delegate
    func numberOfSections(in tableView: UITableView) -> Int {
        return self.chatList.count
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return 1
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell: MessageCell = tableView.dequeueReusableCell(withIdentifier: description, for: indexPath) as! MessageCell
        let messageData = self.chatList[indexPath.section]
        cell.messageLabel?.text = messageData.content
        cell.messageLabel?.textColor = messageData.color
        
        var rect = cell.textLabel!.textRect(forBounds: cell.textLabel!.frame, limitedToNumberOfLines: 0)
        rect.size = self.calculateCellSize(indexPath: indexPath)
        cell.messageLabel!.height = rect.height
        
        return cell
    }
    
    func tableView(_: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return self.calculateCellSize(indexPath: indexPath).height
    }
    
    func tableView(_ tableView: UITableView, viewForHeaderInSection section: Int) -> UIView? {
        let header = UIView.init(frame: CGRect.zero)
        return header
    }
    
    func tableView(_ tableView: UITableView, viewForFooterInSection section: Int) -> UIView? {
        let footer = UIView.init(frame: CGRect.zero)
        return footer
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        self.tableView?.deselectRow(at: indexPath, animated: true)
    }
    
    // MARK: - 发送消息
    @objc func sendMessage() {
        // 接收方的id
        let receiverId = self.receiverTextField?.text
        if receiverId.isBlank {
            ProgressHUD.showError("请输入对方的id")
            return
        }
        
        // 消息内容
        let messageText = self.messageTextField?.text
        if messageText.isBlank {
            ProgressHUD.showError("请输入消息内容")
            return
        }
        
        self.showIMInfo(content: "我对\(receiverId!)说：\(messageText!)", color: .black)
        
        let message = ProtocolFactory.createCommonData(dataContent: messageText!, fromUserId: ClientCoreSDK.sharedInstance().getCurrentLoginInfo()!.loginUserId!, toUserId: receiverId!, QoS: true, fp: nil, typeu: -1)
        
        let code = LocalDataSender.sharedInstance().sendCommonData(message: message)
        if code == .commonCodeOK {
            ProgressHUD.showSucceed("您的消息已成功发出")
        } else {
            ProgressHUD.showError("您的消息发送失败，错误码：\(code ?? .unknown)")
        }
    }
    
    // MARK: - 退出登陆
    @objc func logout() {
        // 发出退出登陆请求包
        let code = LocalDataSender.sharedInstance().sendLoginout()
        if code == .commonCodeOK {
            ProgressHUD.show("注销登陆请求已发出")
            self.refreshConnectStatus()
        } else {
            ProgressHUD.showError("注销登陆请求发送失败，错误码: \(code)")
        }
        
        // 退出登陆时记得一定要调用此行，不然不退出APP的情况下再登陆时会报 code=203错误
        IMClientManager.sharedInstance().releaseMobileSDK()
        
        DispatchQueue.main.asyncAfter(deadline: DispatchTime.now() + 1) {
            ProgressHUD.dismiss()
            
            // 退出程序
            UIView.animate(withDuration: 0.5) {
                let window = SceneDelegate.shared?.window
                window?.alpha = 0
                window?.frame = CGRect(x: 0, y: SCREEN_WIDTH, width: 0, height: 0)
            } completion: { finish in
                exit(0)
            }
        }
    }
    
    // MARK: - UITextFieldDelegate
    func textFieldShouldBeginEditing(_ textField: UITextField) -> Bool {
        return true
    }
    
    // MARK: - 手势按钮
    @objc func viewTapped(tap: UITapGestureRecognizer) {
        self.view.endEditing(true)
    }
    
    override func didReceiveMemoryWarning() {
        // 移除通知
        NotificationCenter.default.removeObserver(self)
    }
}
