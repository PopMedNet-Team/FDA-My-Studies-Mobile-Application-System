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
import Foundation
import CryptoSwift

protocol FileDownloadManagerDelegates {
    func download(manager: FileDownloadManager,didUpdateProgress progress: Float)
    func download(manager: FileDownloadManager,didFinishDownloadingAtPath path: String)
    func download(manager: FileDownloadManager,didFailedWithError error: Error)
}

let kdefaultKeyForEncrytion = "passwordpasswordpasswordpassword"
let kdefaultIVForEncryption = "drowssapdrowssap"

class FileDownloadManager: NSObject,URLSessionDelegate,URLSessionDownloadDelegate {
    
    var sessionManager: Foundation.URLSession!
    open var downloadingArray: [FileDownloadModel] = []
    var delegate: FileDownloadManagerDelegates?
    let taskStartedDate = Date()
    func downloadFile(_ fileName: String, fileURL: String, destinationPath: String){
        
        let url = URL(string: fileURL as String)!
        let request = URLRequest(url: url)
        let config = URLSessionConfiguration.default
        sessionManager = Foundation.URLSession.init(configuration: config, delegate: self , delegateQueue: nil)
        let downloadTask = sessionManager.downloadTask(with: request)
        
        downloadTask.taskDescription = [fileName, fileURL, destinationPath].joined(separator: ",")
        downloadTask.resume()
        
        
        let downloadModel = FileDownloadModel.init(fileName: fileName, fileURL: fileURL, destinationPath: destinationPath)
        downloadModel.startTime = Date()
        downloadModel.status = TaskStatus.downloading.description()
        downloadModel.task = downloadTask
        
        downloadingArray.append(downloadModel)
    }




//extension FileDownloadManager: URLSessionDelegate{

    func urlSession(_ session: Foundation.URLSession, downloadTask: URLSessionDownloadTask, didWriteData bytesWritten: Int64, totalBytesWritten: Int64, totalBytesExpectedToWrite: Int64) {
        
        DispatchQueue.main.async(execute: { () -> Void in
            
            let receivedBytesCount = Double(downloadTask.countOfBytesReceived)
            let totalBytesCount = Double(downloadTask.countOfBytesExpectedToReceive)
            let progress = Float(receivedBytesCount / totalBytesCount)
            
            self.delegate?.download(manager: self, didUpdateProgress: progress)
        })
    }
    func urlSession(_ session: Foundation.URLSession, downloadTask: URLSessionDownloadTask, didFinishDownloadingTo location: URL) {
        
        debugPrint("didFinishDownloadingToURL location \(location)")
        
        for (_, downloadModel) in downloadingArray.enumerated() {
            if downloadTask.isEqual(downloadModel.task) {
                let fileName = downloadModel.fileName as NSString
                let basePath = downloadModel.destinationPath == "" ? AKUtility.baseFilePath : downloadModel.destinationPath
                let destinationPath = (basePath as NSString).appendingPathComponent(fileName as String)
                
                let fileManager: FileManager = FileManager.default
                
                //If all set just move downloaded file to the destination
                //if fileManager.fileExists(atPath: basePath) {
                let fileURL = URL(fileURLWithPath: destinationPath as String)
                debugPrint("directory path = \(destinationPath)")
                
                do {
                    try fileManager.moveItem(at: location, to: fileURL)
                    
                    // encryt the data present at filepath fileURL
                    
                    FileDownloadManager.encyptFile(pathURL: fileURL)
                    
                    self.delegate?.download(manager: self, didFinishDownloadingAtPath: fileName as String)
                } catch let error as NSError {
                    debugPrint("Error while moving downloaded file to destination path:\(error)")
                    DispatchQueue.main.async(execute: { () -> Void in
                        self.delegate?.download(manager: self, didFailedWithError: error)
                    })
                }
                
                break
            }
        }
    }
    func URLSession(_ session: Foundation.URLSession, task: URLSessionTask, didCompleteWithError error: NSError?) {
       // debugPrint("task id: \(task.taskIdentifier)")
       // debugPrint("Completed with error \(error?.localizedDescription)")
        if error != nil {
            self.delegate?.download(manager: self, didFailedWithError: error!)
        }
        
    }
    public func urlSessionDidFinishEvents(forBackgroundURLSession session: URLSession) {
    
    }
    
    /**
     decrypts file at the URL specified using the  Key & IV, AES256 decryption is used
     */
    class func decrytFile(pathURL: URL?) -> Data?{
        
        var pathString = "file://" + "\((pathURL?.absoluteString)!)"
        if (pathURL?.absoluteString.contains("file://"))! {
            pathString = (pathURL?.absoluteString)!
        }
        
        if !FileManager.default.fileExists(atPath: pathString) {
        
        do{
            
            let ud = UserDefaults.standard
            
            var key = ""
            var initializationVector = ""
            
            if ud.value(forKey: kEncryptionKey) != nil {
                key = ud.value(forKey: kEncryptionKey) as! String
            }
            else{
                key = kdefaultKeyForEncrytion
            }
            if ud.value(forKey: kEncryptionIV) != nil {
                initializationVector = ud.value(forKey: kEncryptionIV) as! String
            }
            else{
                initializationVector = kdefaultIVForEncryption
            }
            // key = kdefaultKeyForEncrytion
            //initializationVector = kdefaultIVForEncryption
            
            let data = try Data.init(contentsOf: URL.init(string: pathString)!)
            let aes = try AES(key: key, iv: initializationVector)
            let deCipherText = try aes.decrypt(Array(data))
            let deCryptedData = Data(deCipherText)
            return deCryptedData
            
        }catch let error as NSError {
            
             debugPrint("Decrypting data failed \(error.localizedDescription)")
            print(error)
            
            return nil
        }
           
        }else {
            return nil
        }
    }
    
/**
  encrypts file at the URL specified using the random generated Key & IV, AES256 encryption is used
 */
   class func encyptFile(pathURL: URL?){
    
    var pathString = "file://" + "\((pathURL?.absoluteString)!)"
    if (pathURL?.absoluteString.contains("file://"))! {
        pathString = (pathURL?.absoluteString)!
    }
        if !FileManager.default.fileExists(atPath: pathString) {
            
            do{
                let ud = UserDefaults.standard
                
                var key = ""
                var initializationVector = ""
                
                if ud.value(forKey: kEncryptionKey) != nil {
                    key = ud.value(forKey: kEncryptionKey) as! String
                }
                else{
                    key = kdefaultKeyForEncrytion
                }
                if ud.value(forKey: kEncryptionIV) != nil {
                     initializationVector = ud.value(forKey: kEncryptionIV) as! String
                }
                else{
                    initializationVector = kdefaultIVForEncryption
                }
                 //key = kdefaultKeyForEncrytion
                //initializationVector = kdefaultIVForEncryption
                let data = try Data.init(contentsOf: URL.init(string: pathString)!)
                let aes = try AES(key: key, iv: initializationVector) // aes128
                
                let ciphertext = try aes.encrypt(Array(data))
                
                let encryptedData =  Data(ciphertext)
                
                do{
                    try encryptedData.write(to: URL.init(string: pathString)!, options: Data.WritingOptions.atomic)
                    
                }catch let error as NSError{
                    print(error)
                    debugPrint("Writing encrypted data to path failed \(error.localizedDescription)")
                }
            }catch let error as NSError {
                print(error)
                debugPrint("Encryting data failed \(error.localizedDescription)")
            }
        }
    }
}
