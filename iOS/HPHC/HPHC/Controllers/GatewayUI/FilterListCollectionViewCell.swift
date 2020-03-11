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

class FilterListCollectionViewCell: UICollectionViewCell {
    
    @IBOutlet weak var tableView: UITableView?
    @IBOutlet weak var tableViewHeader: UIView?
    @IBOutlet weak var labelHeaderTitle: UILabel?
    
    var headerName = ""
    var studyData = NSMutableArray()
    var filterOptions: FilterOptions!
    
    func displayCollectionData(data: FilterOptions) {
    
        //studyData = data["studyData"] as! NSMutableArray
        filterOptions = data
        if filterOptions.title.count == 0 {
            tableView?.tableHeaderView = nil
        } else {
            labelHeaderTitle?.text = filterOptions.title
            tableView?.tableHeaderView = tableViewHeader
        }
        
        tableView?.reloadData()
        
        self.tableView?.estimatedRowHeight = 50
        self.tableView?.rowHeight = UITableView.automaticDimension
        
        labelHeaderTitle?.sizeToFit()
        
        
        
    }
}


extension FilterListCollectionViewCell: UITableViewDelegate , UITableViewDataSource{

    
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return filterOptions.filterValues.count //studyData.count
    }
    /*
    
    func tableView(_ tableView: UITableView, viewForHeaderInSection section: Int) -> UIView? {
        
        let view = UIView.init(frame: CGRect(x: 0, y: 0, width: tableView.frame.width, height: 20))
        view.backgroundColor = UIColor.lightGray
        
        let data = studyData[section] as! NSDictionary
        let arrayData = data["isHeaderAvailable"] as! String
        
        if arrayData == "YES" {
            
            let label = UILabel.init(frame: CGRect(x: 0, y: 0, width: view.frame.width, height: view.frame.height))
            label.textAlignment = NSTextAlignment.left
            label.text = data["headerText"] as? String
            label.font = UIFont.boldSystemFont(ofSize: 14)

            label.translatesAutoresizingMaskIntoConstraints = false
            view.addSubview(label)
            
            NSLayoutConstraint(item: label, attribute: NSLayoutAttribute.centerX, relatedBy: NSLayoutRelation.equal, toItem: view, attribute: NSLayoutAttribute.centerX, multiplier: 1, constant: 0).isActive = true
            NSLayoutConstraint(item: label, attribute: NSLayoutAttribute.centerY, relatedBy: NSLayoutRelation.equal, toItem: view, attribute: NSLayoutAttribute.centerY, multiplier: 1, constant: 0).isActive = true
            
        }else{
        
        
        }
     
        return view
    }
    
 
 */
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        
        let cell = tableView.dequeueReusableCell(withIdentifier: "cell", for: indexPath) as! FilterListTableViewCell
        
        let data = filterOptions.filterValues[indexPath.row]
        cell.populateCellWith(filterValue: data)
        
        return cell
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: true)
        let data = filterOptions.filterValues[indexPath.row]
        data.isSelected = !data.isSelected
        
        tableView.reloadData()
        
    }



}
