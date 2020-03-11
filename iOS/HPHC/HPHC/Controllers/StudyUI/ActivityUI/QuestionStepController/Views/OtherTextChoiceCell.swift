//
//  OtherTextChoiceCell.swift
//  Survey-Demo
//
//  Created by Tushar on 3/27/19.
//  Copyright © 2019 Tushar. All rights reserved.
//

import UIKit

protocol OtherTextChoiceCellDelegate : class {
    func didEndEditing(with text: String?)
}

class OtherTextChoiceCell: UITableViewCell {
    
    @IBOutlet weak var mainView: UIView!
    @IBOutlet weak var otherField: UITextField!
    @IBOutlet weak var titleLbl: UILabel!
    @IBOutlet weak var checkmarkView: UIImageView!
    @IBOutlet weak var detailedTextLbl: UILabel!
    @IBOutlet weak var otherView: UIView!
    @IBOutlet weak var otherViewHeightConstraint: NSLayoutConstraint!
    
    weak var delegate: OtherTextChoiceCellDelegate?
    var didSelected: Bool = false {
        didSet {
            if didSelected {
                self.titleLbl.textColor = #colorLiteral(red: 0.2431372549, green: 0.5411764706, blue: 0.9921568627, alpha: 1)
                self.checkmarkView.isHidden = false
            } else {
                self.titleLbl.textColor = #colorLiteral(red: 0, green: 0, blue: 0, alpha: 1)
                self.checkmarkView.isHidden = true
                self.otherField?.endEditing(true)
            }
        }
    }
    
    override func awakeFromNib() {
        super.awakeFromNib()
        self.mainView.clipsToBounds = true
        self.mainView.layer.cornerRadius = 10
        self.mainView.layer.maskedCorners = [.layerMinXMaxYCorner,.layerMaxXMaxYCorner]
        self.otherField?.delegate = self
    }
    
    func updateOtherView(isShow: Bool) {
        switch isShow {
        case true:
            self.otherViewHeightConstraint.constant = 70
        case false:
            self.otherViewHeightConstraint.constant = 0
        }
        self.layoutIfNeeded()
    }
    
}

extension OtherTextChoiceCell: UITextFieldDelegate {
    
    func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        textField.endEditing(true)
        return true
    }
    
    func textFieldDidEndEditing(_ textField: UITextField) {
        delegate?.didEndEditing(with: textField.text)
    }
    
    func textFieldDidBeginEditing(_ textField: UITextField) {
        print("Editing start")
    }
    
    func textField(_ textField: UITextField, shouldChangeCharactersIn range: NSRange, replacementString string: String) -> Bool {
        return range.location < 250
    }
}
