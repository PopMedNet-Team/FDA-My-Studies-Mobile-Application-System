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

extension UIViewController{
    
    public func addBackBarButton() {
        
        let customView = UIView.init(frame: CGRect.init(x: -15, y: 0, width: 46, height: 36))
        
        let backbutton: UIButton = UIButton.init(frame: customView.frame)
        backbutton.setImage(#imageLiteral(resourceName: "backIcon"), for: .normal)
        backbutton.addTarget(self, action: #selector(self.popController), for: .touchUpInside)
        customView.addSubview(backbutton)
        navigationItem.leftBarButtonItem = UIBarButtonItem.init(customView: customView)
    }
    
    public func addHomeButton() {
        
        let customView = UIView.init(frame: CGRect.init(x: -15, y: 0, width: 46, height: 36))
        
        let backbutton: UIButton = UIButton.init(frame: customView.frame)
        backbutton.setImage(#imageLiteral(resourceName: "homeIcon"), for: .normal)
        backbutton.addTarget(self, action: #selector(self.popToSpecificController), for: .touchUpInside)
        customView.addSubview(backbutton)
        
        navigationItem.leftBarButtonItem = UIBarButtonItem.init(customView: customView)
    }
    
    @objc public func popToSpecificController() {
        
        var identifier: String? = ""
        
        switch self {
        case is ResourcesViewController:
            identifier = kUnwindToStudyListIdentifier
        case is ActivitiesViewController:
            identifier = kActivityUnwindToStudyListIdentifier
        default:
            break
        }
        
        if identifier != ""{
            self.performSegue(withIdentifier: identifier!, sender: self)
        }
    }
    
    
    @objc public func popController() {
       _ = self.navigationController?.popViewController(animated: true)
    }
}
