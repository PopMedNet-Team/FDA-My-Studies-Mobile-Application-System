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
import UIKit

let alert = UIAlertView()

public typealias AlertAction = () -> Void

class UIUtilities: NSObject {
    
    
    // MARK: UI changes for textField
    /* Initial Padding space before displaying the texts */
    class func paddingViewForTextField(textField: UITextField) ->Void {
        let paddingView =  UIView.init(frame: CGRect(x: 0, y: 0, width: 10, height: textField.frame.height))
        textField.leftView = paddingView
        textField.leftViewMode = UITextField.ViewMode.always
    }
    
    /* Add a border to Textfield
     @params:- textfield - the data which is used to show
     @return:- textfield - returns text field with padding space
     */
    class func addingBorderToTextField(textField:UITextField)->UITextField {
        //  textField.borderStyle =  UITextBorderStyle.RoundedRect
        textField.layer.borderWidth = 2
        textField.layer.borderColor = Utilities.hexStringToUIColor("556085").cgColor
        textField.backgroundColor =  Utilities.hexStringToUIColor("414c6f")
        
        return textField
    }
    
    /* Used to show invalid input for that particular textfield */
    class func getTextfieldWithInvalidInputBorder(textField: UITextField, layerBorderColor: String, backgroundColor: String) {
        
        //textField.borderStyle =  UITextBorderStyle.RoundedRect
        textField.layer.borderWidth = 2
        textField.layer.borderColor = Utilities.hexStringToUIColor(layerBorderColor).cgColor
        textField.backgroundColor =  Utilities.hexStringToUIColor(backgroundColor)
        //"bf7266"
        //"414c6f"
    }
    // MARK: UI effects & View Changes
    
    /* Used to remove border text field */
    class func removeTheBorderToTextField(textField: UITextField)->UITextField {
        
        textField.borderStyle =  UITextField.BorderStyle.none
        textField.layer.borderWidth = 0
        textField.layer.borderColor = UIColor.clear.cgColor
        textField.backgroundColor =  Utilities.hexStringToUIColor("556085")
        
        return textField
        
    }
    
    class func segmentSeparatorColor(color: UIColor,segment: UISegmentedControl) -> UIImage {
        
        // let rect = CGRectMake(0.0, 0.0, 1.5, segment.frame.size.height)
        
        let rect =  CGRect(x: 0.0, y: 0.0, width: 1.5, height: segment.frame.size.height)
        UIGraphicsBeginImageContext(rect.size)
        let context = UIGraphicsGetCurrentContext()
        context!.setFillColor(color.cgColor);
        context!.fill(rect);
        let image = UIGraphicsGetImageFromCurrentImageContext();
        UIGraphicsEndImageContext();
        return image!
        
    }
    
    class func applyBlurrEffect() -> UIVisualEffectView
    {
        
        let blurEffect = UIBlurEffect.init(style: UIBlurEffect.Style.dark)
        let visualEffect = UIVisualEffectView.init(effect: blurEffect)
        visualEffect.tag = 100
        visualEffect.alpha = 1
        visualEffect.autoresizingMask = [.flexibleWidth, .flexibleHeight]
        return visualEffect
    }
    
    class func applyBlurrEffectForFrequency() -> UIVisualEffectView
    {
        
        let blurEffect = UIBlurEffect.init(style: UIBlurEffect.Style.dark)
        let visualEffect = UIVisualEffectView.init(effect: blurEffect)
        visualEffect.tag = 500
        visualEffect.alpha = 1
        visualEffect.autoresizingMask = [.flexibleWidth, .flexibleHeight]
        return visualEffect
    }
    
    
    class func removeBlurFromFrequency(fromView: UIView) {
        for subView in fromView.subviews{
            if subView.tag == 500{
                subView.removeFromSuperview()
            }
        }
    }
    
    class func removeBlur(fromView: UIView) {
        for subView in fromView.subviews{
            if subView.tag == 100{
                subView.removeFromSuperview()
            }
        }
    }
    
    /* Performs spinning action using CoreAnimation */
    class func addSpinAnimation(withDuration duration: CFTimeInterval)-> CABasicAnimation{
        let animation = CABasicAnimation(keyPath: "transform.rotation.z")
        animation.fromValue = 0
        animation.toValue = 360
        animation.duration = duration
        return animation
    }
    
    class  func getHexColors() -> NSArray {
        
        let hexColors = ["ff9f30","fdf22f","b6ff14","07f51c"]
        return hexColors as NSArray
    }
    
    class  func addFadedGreenView(view: UIView) ->UIView{
        
        let greenView = UIView.init(frame: view.frame)
        greenView.backgroundColor = Utilities.hexStringToUIColor("1eebb4")
        greenView.alpha = 0.5
        greenView.tag = 200
        view.addSubview(greenView)
        
        return greenView
    }
    
    class  func addFadedGreenViewForFrequencyAttributes(view: UIView) ->UIView{
        
        let greenView = UIView.init(frame:view.frame)
        greenView.backgroundColor = Utilities.hexStringToUIColor("1eebb4")
        greenView.alpha = 0.8
        greenView.tag = 300
        view.addSubview(greenView)
        
        return greenView
    }
    
    class func setWhiteBorderOnView(view: UIView, borderWidth: CGFloat, cornerRadius: CGFloat){
        
        view.layer.borderWidth = borderWidth
        view.layer.cornerRadius = cornerRadius
        view.layer.borderColor = UIColor.white.cgColor
    }
    
    class func setRedBorderOnView(view: UIView, borderWidth: CGFloat, cornerRadius: CGFloat){
        
        view.layer.borderWidth = borderWidth
        view.layer.cornerRadius = cornerRadius
        view.layer.borderColor = Utilities.hexStringToUIColor("kInvalidBorderColor").cgColor
    }
    
    // MARK: JSON serialization helper methods
    class func convertDictionaryIntoString(mutableDic: NSMutableDictionary) -> String{
        
        var jsonString: String!
        do{
            let jsonData: NSData = try JSONSerialization.data(withJSONObject: mutableDic, options: JSONSerialization.WritingOptions.prettyPrinted) as NSData
            jsonString = NSString(data: jsonData as Data, encoding: String.Encoding.utf8.rawValue)! as String
        }
        catch{
            
        }
        return jsonString
    }
    
    class func convertNSMutableArrayIntoString(mutableArray: NSMutableArray) -> String {
        
        var socialMediaNamesString: String!
        do{
            let jsonData: NSData = try JSONSerialization.data(withJSONObject: mutableArray, options: JSONSerialization.WritingOptions.prettyPrinted) as NSData
            socialMediaNamesString = NSString(data: jsonData as Data, encoding: String.Encoding.utf8.rawValue)! as String
        }
        catch{
        }
        return socialMediaNamesString
    }
    // MARK: Alert composers
    
    /* Presents alert message */
    class func showAlertWithTitleAndMessage(title: NSString, message : NSString) -> Void {
        
        let alert = UIAlertController(title: title as String,message: message as String,preferredStyle: UIAlertController.Style.alert)
        alert.addAction(UIAlertAction(title: NSLocalizedString("OK", comment: ""), style: .default, handler: nil))
        var rootViewController = UIApplication.shared.keyWindow?.rootViewController
        if let navigationController = rootViewController as? UINavigationController {
            rootViewController = navigationController.viewControllers.first
        }
        if let tabBarController = rootViewController as? UITabBarController {
            rootViewController = tabBarController.selectedViewController
        }
        rootViewController?.present(alert, animated: true, completion: nil)
        
    }
    
    /* Dismiss alert message*/
    class func dismissAlert(){
        alert.dismiss(withClickedButtonIndex: 0, animated: false)
    }
    
    /* Presents alert message */
    class func showAlertWithMessage(alertMessage: String)->Void{
        self.showAlertWithTitleAndMessage(title: "", message: alertMessage as NSString)
    }
    
    class func showAlertMessageWithTwoActionsAndHandler(_ errorTitle: String,errorMessage : String,errorAlertActionTitle : String ,errorAlertActionTitle2: String?,viewControllerUsed : UIViewController, action1: @escaping AlertAction, action2: @escaping AlertAction){
        let alert = UIAlertController(title: errorTitle, message: errorMessage, preferredStyle:UIAlertController.Style.alert)
        
        alert.addAction(UIAlertAction(title: errorAlertActionTitle, style: UIAlertAction.Style.default, handler: { (action) in
            action1()
        }))
        if errorAlertActionTitle2 != nil {
            alert.addAction(UIAlertAction(title: errorAlertActionTitle2, style: UIAlertAction.Style.default, handler: { (action) in
                action2()
            }))
        }
        
        viewControllerUsed.present(alert, animated: true, completion: nil)
    }
    
    class func showAlertMessageWithThreeActionsAndHandler(_ errorTitle : String,errorMessage : String,errorAlertActionTitle : String ,errorAlertActionTitle2 : String?,errorAlertActionTitle3 : String?,viewControllerUsed : UIViewController, action1: @escaping AlertAction, action2: @escaping AlertAction,action3: @escaping AlertAction){
        let alert = UIAlertController(title: errorTitle, message: errorMessage, preferredStyle:UIAlertController.Style.alert)
        
        alert.addAction(UIAlertAction(title: errorAlertActionTitle, style: UIAlertAction.Style.default, handler: { (action) in
            action1()
        }))
        if errorAlertActionTitle2 != nil {
            alert.addAction(UIAlertAction(title: errorAlertActionTitle2, style: UIAlertAction.Style.default, handler: { (action) in
                action2()
            }))
        }
        
        if errorAlertActionTitle3 != nil {
            alert.addAction(UIAlertAction(title: errorAlertActionTitle3, style: UIAlertAction.Style.default, handler: { (action) in
                action3()
            }))
        }
        
        
        viewControllerUsed.present(alert, animated: true, completion: nil)
    }
    
    class func showAlertMessageWithActionHandler(_ title: String,message: String,buttonTitle : String ,viewControllerUsed: UIViewController, action: @escaping AlertAction){
        
        let alert = UIAlertController(title: title, message: message, preferredStyle: UIAlertController.Style.alert)
        
        alert.addAction(UIAlertAction(title: buttonTitle, style: UIAlertAction.Style.default, handler: { (alertAction) in
            action()
        }))
        
        
        viewControllerUsed.present(alert, animated:true, completion: nil)
    }
    
    class func showAlertMessage(_ errorTitle: String, errorMessage: String, errorAlertActionTitle : String ,viewControllerUsed: UIViewController?){
        let alert = UIAlertController(title: errorTitle, message: errorMessage, preferredStyle:UIAlertController.Style.alert)
        alert.addAction(UIAlertAction(title: errorAlertActionTitle, style: UIAlertAction.Style.default, handler: nil))
        viewControllerUsed!.present(alert, animated:true, completion: nil)
    }
    
}

