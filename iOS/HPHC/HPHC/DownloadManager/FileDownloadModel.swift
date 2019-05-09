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

public enum TaskStatus: Int {
    case unknown, gettingInfo, downloading, paused, failed
    
    public func description() -> String {
        switch self {
        case .gettingInfo:
            return "GettingInfo"
        case .downloading:
            return "Downloading"
        case .paused:
            return "Paused"
        case .failed:
            return "Failed"
        default:
            return "Unknown"
        }
    }
}
class FileDownloadModel {
    
    open var fileName: String!
    open var fileURL: String!
    open var status: String = TaskStatus.gettingInfo.description()
    
    open var file: (size: Float, unit: String)?
    open var downloadedFile: (size: Float, unit: String)?
    
    open var remainingTime: (hours: Int, minutes: Int, seconds: Int)?
    
    open var speed: (speed: Float, unit: String)?
    
    open var progress: Float = 0
    
    open var task: URLSessionDownloadTask?
    
    open var startTime: Date?
    
    fileprivate(set) open var destinationPath: String = ""
    
    fileprivate convenience init(fileName: String, fileURL: String) {
        self.init()
        
        self.fileName = fileName
        self.fileURL = fileURL
    }
    
    convenience init(fileName: String, fileURL: String, destinationPath: String) {
        self.init(fileName: fileName, fileURL: fileURL)
        
        self.destinationPath = destinationPath
    }

}
