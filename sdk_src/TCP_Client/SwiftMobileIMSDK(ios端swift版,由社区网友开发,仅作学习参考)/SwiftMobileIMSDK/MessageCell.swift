//
//  MessageCell.swift
//  SwiftMobileIMSDK
//
//  Created by fishbay on 2021/8/20.
//

import UIKit

class MessageCell: UITableViewCell {
    
    var messageLabel: UILabel?
    
    required init?(coder: NSCoder) {
        super.init(coder: coder)
    }

    override init(style: UITableViewCell.CellStyle, reuseIdentifier: String?) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)

        // 取消cell选中的背景色
        self.selectionStyle = UITableViewCell.SelectionStyle.none
        
        self.contentView.backgroundColor = .white
        
        let messageLabel = UILabel.init(frame: CGRect(x: 20, y: 0, width: SCREEN_WIDTH * 0.7, height: 20))
        self.contentView.addSubview(messageLabel)
        self.messageLabel = messageLabel
        messageLabel.backgroundColor = .white
        messageLabel.numberOfLines = 0
        messageLabel.font = normalFont(size: 16)
    }
    
}
