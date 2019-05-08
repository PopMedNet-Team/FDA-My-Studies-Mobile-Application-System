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

class ResourcesTableViewCell: UITableViewCell {

    @IBOutlet var labelTitle : UILabel?
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }

    func populateCellData(data : String){
        labelTitle?.text = data
    
    }
    
    func animateAvailability(for resource:Resource){
        
        if resource.availableToday {
            
            self.contentView.backgroundColor = UIColor.lightGray
            self.contentView.alpha = 0.6
            UIView.animate(withDuration: 0.4, delay: 0, options: [.curveEaseInOut,.repeat,.autoreverse], animations: {
                self.contentView.alpha = 0.1
            }) { (finished) in
                self.contentView.backgroundColor = UIColor.white
                self.contentView.alpha = 1.0
                resource.availableToday = false
            }
            
            
            DispatchQueue.main.asyncAfter(deadline: .now() + 1 ) {
                self.contentView.layer.removeAllAnimations()
            }
            
        }
    }
  
}
