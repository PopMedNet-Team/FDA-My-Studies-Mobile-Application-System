//
//  Cell.swift
//  HPHC
//
//  Created by Tushar Katyal on 11/04/19.
//  Copyright © 2019 BTC. All rights reserved.
//

import UIKit


/**
 The `CellConfigurable` protocol is adopted by cells that want to provide an easy
 way to obtain the `UINib` and a `reuseIdentifier`.
 
 This framework has a default implementation that uses the name of the class
 for the `reuseIdentifier` and assumes the nib name is the same as the
 `reuseIdentifier`, in case your nib name is different you can either return
 the name of your nib in the `reuseIdentifier` variable or return your nib
 implementing the `nib` variable.
 
 This framework also adds conformance to the protocol to `UITableViewCell` and
 `UICollectionViewCell`
 */
public protocol CellConfigurable {
    static var reuseIdentifier: String { get }
    static var nib: UINib { get }
}

extension CellConfigurable where Self : UITableViewCell{
    public static var reuseIdentifier: String {
        return String(describing: self)
    }
    
    public static var nib: UINib {
        return UINib(nibName: reuseIdentifier, bundle: nil)
    }
}

extension CellConfigurable where Self : UICollectionViewCell{
    public static var reuseIdentifier: String {
        return String(describing: self)
    }
    
    public static var nib: UINib {
        return UINib(nibName: reuseIdentifier, bundle: nil)
    }
}

extension UITableViewCell: CellConfigurable {}
extension UICollectionViewCell: CellConfigurable {}

/*========================================================*/

/**
 The `CellRegistrable` protocol is adopted objects that want to provide an easy
 way to register the cells that conform to the `CellConfigurable` protocol.
 
 This framework adds conformance to the protocol to `UITableView` and
 `UICollectionView`
 */
public protocol CellRegistrable {
    func registerCell(cell: CellConfigurable.Type)
}

extension UITableView: CellRegistrable {
    public func registerCell(cell: CellConfigurable.Type) {
        register(cell.nib, forCellReuseIdentifier: cell.reuseIdentifier)
    }
}

extension UICollectionView: CellRegistrable {
    public func registerCell(cell: CellConfigurable.Type) {
        register(cell.nib, forCellWithReuseIdentifier: cell.reuseIdentifier)
    }
}

extension UIView {
    
    /** This is a function to get subViews of a particular type from view recursively. It would look recursively in all subviews and return back the subviews of the type T */
    func allSubViewsOf<T : UIView>(type : T.Type) -> [T]{
        var all = [T]()
        func getSubview(view: UIView) {
            if let aView = view as? T{
                all.append(aView)
            }
            guard view.subviews.count>0 else { return }
            view.subviews.forEach{ getSubview(view: $0) }
        }
        getSubview(view: self)
        return all
    }
    
}
