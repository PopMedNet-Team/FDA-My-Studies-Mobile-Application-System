/*
 License Agreement for FDA My Studies
Copyright © 2017-2019 Harvard Pilgrim Health Care Institute (HPHCI) and its Contributors. Permission is
hereby granted, free of charge, to any person obtaining a copy of this software and associated
documentation files (the &quot;Software&quot;), to deal in the Software without restriction, including without
limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the
Software, and to permit persons to whom the Software is furnished to do so, subject to the following
conditions:
The above copyright notice and this permission notice shall be included in all copies or substantial
portions of the Software.
Funding Source: Food and Drug Administration (“Funding Agency”) effective 18 September 2014 as
Contract no. HHSF22320140030I/HHSF22301006T (the “Prime Contract”).
THE SOFTWARE IS PROVIDED &quot;AS IS&quot;, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
OTHER DEALINGS IN THE SOFTWARE.
 */

import UIKit


let kDeleteButtonTag = 11220
let kRetainButtonTag = 11221

let kConfirmationOptionalDefaultTypeRetain = "retain"
let kConfirmationOptionalDefaultTypeDelete = "delete"


protocol ConfirmationOptionalDelegate {
    func confirmationCell(cell: ConfirmationOptionalTableViewCell , forStudy study: Study, deleteData: Bool)
}
class ConfirmationOptionalTableViewCell: UITableViewCell {

    @IBOutlet var buttonDeleteData: UIButton?
    @IBOutlet var buttonRetainData: UIButton?
    @IBOutlet var labelTitle: UILabel?
    @IBOutlet var imageViewDeleteCheckBox: UIImageView?
    @IBOutlet var imageViewRetainCheckBox: UIImageView?
    var study: Study!
    var delegate: ConfirmationOptionalDelegate? = nil
    
    override func awakeFromNib() {
        super.awakeFromNib()
    }

// MARK:IBActions
    
    @IBAction func deleteOrRetainDataButtonAction(_ sender: UIButton?){
        
        var deleteData = false
        if sender?.tag == kDeleteButtonTag {
            if (imageViewDeleteCheckBox?.image?.isEqual(#imageLiteral(resourceName: "notChecked")))! {
                imageViewDeleteCheckBox?.image = #imageLiteral(resourceName: "checked")
                imageViewRetainCheckBox?.image = #imageLiteral(resourceName: "notChecked")
                deleteData = true;
            }
        }else {
            if (imageViewRetainCheckBox?.image?.isEqual(#imageLiteral(resourceName: "notChecked")))! {
                imageViewRetainCheckBox?.image = #imageLiteral(resourceName: "checked")
                imageViewDeleteCheckBox?.image = #imageLiteral(resourceName: "notChecked")
                deleteData = false;
            }
        }
        self.delegate?.confirmationCell(cell: self, forStudy: study, deleteData: deleteData)
    }
   
    // MARK:Utility methods
    
    func setDefaultDeleteAction(defaultValue: String){
        if defaultValue == kConfirmationOptionalDefaultTypeRetain {
            imageViewRetainCheckBox?.image = #imageLiteral(resourceName: "notChecked")
            imageViewDeleteCheckBox?.image = #imageLiteral(resourceName: "checked")

        }else{
            imageViewDeleteCheckBox?.image = #imageLiteral(resourceName: "checked")
            imageViewRetainCheckBox?.image = #imageLiteral(resourceName: "notChecked")
        }
    }
    
    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

    }

}
