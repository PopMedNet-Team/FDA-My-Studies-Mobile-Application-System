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

class ReachoutOptionsViewController: UIViewController {

    @IBOutlet var tableView: UITableView?
    
    
// MARK:- Viewcontroller Lifecycle
    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.navigationItem.title =  NSLocalizedString("REACH OUT", comment: "")
        
        // Do any additional setup after loading the view.
    }
    override func viewWillAppear(_ animated: Bool) {
        self.setNavigationBarItem()
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    
    /*
     // MARK: - Navigation
     
     // In a storyboard-based application, you will often want to do a little preparation before navigation
     override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
     // Get the new view controller using segue.destinationViewController.
     // Pass the selected object to the new view controller.
     }
     */
    
}

// MARK:- TableView Datasource
extension ReachoutOptionsViewController: UITableViewDataSource {
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return 2
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        
        //let tableViewData = tableViewRowDetails?.object(at: indexPath.row) as! NSDictionary
        
        let cell = tableView.dequeueReusableCell(withIdentifier: "reachoutCell", for: indexPath) as! ReachoutOptionCell
        
        
        switch indexPath.row {
        case 0:
            cell.labelTitle?.text = NSLocalizedString("Leave Anonymous Feedback", comment: "")
        case 1:
            cell.labelTitle?.text = NSLocalizedString("Need Help? Contact Us", comment: "")
        default:
            cell.labelTitle?.text = NSLocalizedString("Need Help? Contact Us", comment: "")
        }
        
        
        return cell
    }
}

// MARK:- TableView Delegates
extension ReachoutOptionsViewController:  UITableViewDelegate {
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: true)
       
        switch indexPath.row {
        case 1:
            self.performSegue(withIdentifier: "contactusSegue", sender: self)
        case 0:
            self.performSegue(withIdentifier: "feedbackSegue", sender: self)
        default:
            debugPrint("default")
        }
    }
}



