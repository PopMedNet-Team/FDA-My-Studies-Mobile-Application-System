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
import Foundation


protocol searchBarDelegate {
    func didTapOnCancel()
    func  search(text: String)
}


class SearchBarView: UIView {
    
    @IBOutlet weak var textFieldSearch: UITextField?
    @IBOutlet weak var buttonCancel: UIButton?
    @IBOutlet weak var viewBackground: UIView?
    var delegate: searchBarDelegate?
    
    required init?(coder aDecoder: NSCoder) {
        
        super.init(coder: aDecoder)
        //fatalError("init(coder:) has not been implemented")
        
    }
    class func instanceFromNib(frame: CGRect,detail: Dictionary<String,Any>?) -> SearchBarView {
        
        let view = UINib(nibName: "SearchBarView", bundle: nil).instantiate(withOwner: nil, options: nil)[0] as! SearchBarView
        view.frame = frame
        view.layoutIfNeeded()
        
        view.viewBackground?.layer.cornerRadius = 3.0
        view.viewBackground?.clipsToBounds = true
        
        return view
    }
    
    @IBAction func buttonCancelAction(){
        
        UIView.animate(withDuration: 0.2,
                       delay: 0.1,
                       options: UIView.AnimationOptions.preferredFramesPerSecond60,
                       animations: { () -> Void in
                        self.frame = CGRect(x: 0 , y: -300 , width: self.frame.size.width , height: 64.0)
                        
                        
        }, completion: { (finished) -> Void in
            
            self.delegate?.didTapOnCancel()
           
        })
    }
}

extension SearchBarView: UITextFieldDelegate{
    
    func textFieldDidBeginEditing(_ textField: UITextField) {
        print(textField.tag)
        
    }
    
    func textField(_ textField: UITextField, shouldChangeCharactersIn range: NSRange, replacementString string: String) -> Bool {
        
        
        let finalString = textField.text! + string
        
        
        if textField == textFieldSearch {
            
            if finalString != nil && finalString.count > 500 {
                return false
            } else {
                return true
            }
        } else {
            
            return false
        }
    }
    
    func textFieldDidEndEditing(_ textField: UITextField) {
        print(textField.text!)
        textField.text =  textField.text?.trimmingCharacters(in: CharacterSet.whitespacesAndNewlines)
        
        textField.resignFirstResponder()
        
        if textField.text != nil && (textField.text?.count)! > 0 {
            self.delegate?.search(text: textField.text!)
        }
        
        
    }
    
    func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        textField.resignFirstResponder()
        return true
    }
}
