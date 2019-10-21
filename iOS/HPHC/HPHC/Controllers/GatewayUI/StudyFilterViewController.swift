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


//Used to do filter based on Apply and Cancel actions
protocol StudyFilterDelegates: class {
    
    func appliedFilter(studyStatus: Array<String>, pariticipationsStatus: Array<String>, categories: Array<String> , searchText: String,bookmarked: Bool)
    
    func didCancelFilter(_ cancel: Bool)
    
}

enum FilterType: Int {
    
    case studyStatus = 0
    case bookMark
    case participantStatus
    case category
    
}


class StudyFilterViewController: UIViewController {
    
    // MARK:- Outlets
    
    @IBOutlet weak var collectionView: UICollectionView?
    @IBOutlet weak var cancelButton: UIButton?
    @IBOutlet weak var applyButton: UIButton?
    
    // MARK:- Properties
    
    weak var delegate: StudyFilterDelegates?
    
    private lazy var studyStatus: [String] = []
    private lazy var pariticipationsStatus: [String] = []
    private lazy var categories: [String] = []
    private lazy var searchText: String = ""
    private lazy var bookmark = true
    
    lazy var previousCollectionData: [[String]] = []
    
    // MARK:- Viewcontroller lifecycle
    override func viewDidLoad() {
        
        super.viewDidLoad()
        applyButton?.layer.borderColor = kUicolorForButtonBackground
        cancelButton?.layer.borderColor = kUicolorForCancelBackground
        
        if let layout = collectionView?.collectionViewLayout as? PinterestLayout {
            layout.delegate = self
        }
        
        if StudyFilterHandler.instance.filterOptions.count == 0 {
            let appDelegate = (UIApplication.shared.delegate as? AppDelegate)!
            appDelegate.setDefaultFilters(previousCollectionData: self.previousCollectionData)
        }
        
        self.collectionView?.reloadData()
    }
    
  
    // MARK:- Button Actions
    
    /**
     Navigate to Studylist screen on Apply button clicked
     @param sender    accepts Anyobject in sender
     */
    
    @IBAction func applyButtonAction(_ sender: AnyObject){
        
        //categories = ["Food Safety","Observational Studies","Cosmetics Safety"]
        //pariticipationsStatus = ["Food Safety","Observational Studies"]
        
        var i: Int = 0
        var isbookmarked = false
        
        for filterOptions in StudyFilterHandler.instance.filterOptions {
            
            let filterType = FilterType.init(rawValue: i)
            let filterValues = (filterOptions.filterValues.filter({$0.isSelected == true}))
            for value in filterValues {
                switch (filterType!) {
                    
                case .studyStatus:
                    studyStatus.append(value.title)
                case .participantStatus:
                    pariticipationsStatus.append(value.title)
                case .bookMark:
                    
                    if User.currentUser.userType == .FDAUser {
                        bookmark = (value.isSelected)
                        isbookmarked = true
                    } else {
                        categories.append(value.title)
                    }
                    
                case .category:
                    categories.append(value.title)
                    //default: break
                }
            }
            i = i + 1
        }
        
        previousCollectionData = []
        previousCollectionData.append(studyStatus)
        
        if User.currentUser.userType == .FDAUser {
            if isbookmarked {
                previousCollectionData.append((bookmark == true ? ["Bookmarked"]: []))
            } else {
                previousCollectionData.append([])
                bookmark = false
            }
        } else {
            previousCollectionData.append(categories)
            bookmark = false
            
        }
        previousCollectionData.append(pariticipationsStatus)
        previousCollectionData.append(categories.count == 0 ? [] : categories)
        
        delegate?.appliedFilter(studyStatus: studyStatus, pariticipationsStatus: pariticipationsStatus, categories: categories,searchText: searchText,bookmarked: bookmark)
        self.dismiss(animated: true, completion: nil)
        
    }
    
    
    /**
     Navigate to Studylist screen on Cancel button clicked
     @param sender    accepts Anyobject in sender
     */
    @IBAction func cancelButtonAction(_ sender: AnyObject) {
        self.delegate?.didCancelFilter(true)
        self.dismiss(animated: true, completion: nil)
    }
}

//// MARK:- Collection Data source & Delegate
extension StudyFilterViewController: UICollectionViewDataSource {//,UICollectionViewDelegate {
    
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return StudyFilterHandler.instance.filterOptions.count //filterData!.count
    }
    
    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        let cell = (collectionView.dequeueReusableCell(withReuseIdentifier: "cell", for: indexPath) as?
            FilterListCollectionViewCell)!
        
        let filterOption = StudyFilterHandler.instance.filterOptions[indexPath.row]
        cell.displayCollectionData(data: filterOption)
        
        return cell
    }
    
}


extension StudyFilterViewController: PinterestLayoutDelegate {
    
    // 1. Returns the photo height
    func collectionView(_ collectionView: UICollectionView, heightForPhotoAtIndexPath indexPath: IndexPath , withWidth width: CGFloat) -> CGFloat {
        
        let filterOptions = StudyFilterHandler.instance.filterOptions[indexPath.row]
        var headerHeight = 0
        if filterOptions.title.count > 0 {
            headerHeight = 60
        }
        let height: CGFloat = CGFloat((filterOptions.filterValues.count * 50) + headerHeight)
        return height
    }
    
    // 2. Returns the annotation size based on the text
    func collectionView(_ collectionView: UICollectionView, heightForAnnotationAtIndexPath indexPath: IndexPath, withWidth width: CGFloat) -> CGFloat {
        return 0
    }
    
}

class StudyFilterHandler {
    var filterOptions: Array<FilterOptions> = []
    var previousAppliedFilters: Array<Array<String>> = []
    var searchText = ""
    static var instance = StudyFilterHandler()
}

class FilterOptions {
    var title: String!
    var filterValues: Array<FilterValues> = []
}

class FilterValues {
    var title: String!
    var isSelected = false
}

extension AppDelegate {
    
    /**
     setter method to set the default filter options if none are selected
     */
    func setDefaultFilters(previousCollectionData: Array<Array<String>>) {
        
        var filterData: NSMutableArray?
        var resource = "AnanomousFilterData"
        
        if User.currentUser.userType == .FDAUser {
            resource = "FilterData"
        }
        
        let plistPath = Bundle.main.path(forResource: resource, ofType: ".plist", inDirectory: nil)
        filterData = NSMutableArray.init(contentsOfFile: plistPath!)!
        
        StudyFilterHandler.instance.filterOptions = []
        var filterOptionsList: Array<FilterOptions> = []
        var i = 0
        
        for options in filterData! {
            let values = ((options as? Dictionary<String,Any>)!["studyData"] as? Array<Dictionary<String,Any>>)!
            let filterOptions = FilterOptions()
            filterOptions.title = ((options as? Dictionary<String,Any>)!["headerText"] as? String)!
            
            var selectedValues: Array<String> = []
            if previousCollectionData.count > 0 {
                selectedValues = previousCollectionData[i]
            }
            
            var filterValues: Array<FilterValues> = []
            for value in values {
                
                var isContained = false
                
                let filterValue = FilterValues()
                filterValue.title = (value["name"] as? String)!
                
                if selectedValues.count > 0 {
                    isContained = selectedValues.contains((value["name"] as? String)!)
                }
                
                if isContained == false {
                    
                    if previousCollectionData.count == 0 {
                        // this means that we are first time accessing the filter screen
                        filterValue.isSelected =  (value["isEnabled"] as? Bool)!
                        
                    } else {
                        // means that filter is already set
                        filterValue.isSelected = false
                    }
                } else {
                    filterValue.isSelected = true
                }
                
                filterValues.append(filterValue)
            }
            filterOptions.filterValues = filterValues
            filterOptionsList.append(filterOptions)
            
            i = i + 1
        }
        StudyFilterHandler.instance.filterOptions = filterOptionsList
        
    }
    
    /**
     returns the array of strings for default filters
     studyStatus: array of default study status
     pariticipationsStatus: array of participation status
     categories: array of categories
     */
    
    func getDefaultFilterStrings()->(studyStatus: Array<String>,pariticipationsStatus: Array<String>,categories: Array<String>,searchText: String,bookmark: Bool){
        
        var studyStatus: Array<String> = []
        var pariticipationsStatus: Array<String> = []
        var categories: Array<String> = []
        var bookmark = true

        //Parsing the filter options
        for (index,filterOptions) in StudyFilterHandler.instance.filterOptions.enumerated() {
            
            let filterType = FilterType.init(rawValue: index)
            let filterValues = (filterOptions.filterValues.filter({$0.isSelected == true}))
            for value in filterValues {
                switch (filterType!) {
                    
                case .studyStatus:
                    studyStatus.append(value.title)
                    
                case .participantStatus:
                    pariticipationsStatus.append(value.title)
                    
                case .bookMark:
                    if User.currentUser.userType == .FDAUser {
                        bookmark = (value.isSelected)
                    } else {
                        categories.append(value.title)
                    }
                    
                case .category:
                    categories.append(value.title)
                    //default: break
                }
            }
        }
        
        if User.currentUser.userType == .FDAUser {
            bookmark = false
        } else {
            bookmark = false
        }
        
        return(studyStatus: studyStatus,pariticipationsStatus : pariticipationsStatus,categories: categories,searchText: "",bookmark: bookmark)
    }
    
    
}


