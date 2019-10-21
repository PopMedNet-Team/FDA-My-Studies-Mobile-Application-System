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

// MARK:- Alert Constants

let kTermsAndConditionLink = "http://www.google.com"
let kPrivacyPolicyLink = "http://www.facebook.com"
let kNavigationTitleTerms = "TERMS"
let kNavigationTitlePrivacyPolicy = "PRIVACY POLICY"


let kAlertPleaseEnterValidValue = "Please Enter Valid Value"

//Used for corner radius Color for sign in , sign up , forgot password etc screens
let kUicolorForButtonBackground = UIColor.init(red: 0/255.0, green: 124/255.0, blue: 186/255.0, alpha: 1.0).cgColor

let kUicolorForCancelBackground = UIColor.init(red: 140/255.0, green: 149/255.0, blue: 163/255.0, alpha: 1.0).cgColor

let kUIColorForSubmitButtonBackground = UIColor.init(red: 0/255.0, green: 124/255.0, blue: 186/255.0, alpha: 1.0)

let NoNetworkErrorCode = -101
let CouldNotConnectToServerCode = -1001

//Display Constants
let kTitleError = "Error"
let kTitleMessage = "Message"
let kImportantNoteMessage = "Important Note"
let kTitleOk = "Ok"
let kTitleCancel = "Cancel"
let kTitleDeleteAccount = "Delete Account"
let kRegistrationInfoMessage = "Registration (or sign up) for the app  is requested only to provide you with a seamless experience of using the app. Your registration information does not become part of the data collected for any study(ies) housed in the app. Each study has its own consent process and your data for the study will not be collected without you providing your informed consent prior to joining the study."

let kDeleteAccountConfirmationMessage = "Are you sure you wish to permanently delete your #APPNAME# account? You will need to register again if you wish to join a study."
let kMessageAccountDeletedSuccess = "Account has been deleted"
let kMessageAppNotificationOffRemainder = "Stay up-to-date! Turn ON notifications and reminders in app and phone settings to get notified about study activity in a timely manner."

// MARK:- Signin Constants
let kSignInTitleText = "SIGN IN"
let kSignInTableViewCellIdentifier = "DetailsCell"

// MARK:- ForgotPassword Constants
let kForgotPasswordTitleText = "PASSWORD HELP"
let kForgotPasswordResponseMessage = "We have sent a temporary password to your registered email. Please login with temporary password and change your password."

// MARK:- SignUp Constants
let kSignUpTitleText = "SIGN UP"
let kAgreeToTermsAndConditionsText = "I Agree to the Terms and Privacy Policy"
let kTermsText = "Terms"
let kPrivacyPolicyText = "Privacy Policy"
let kSignUpTableViewCellIdentifier = "CommonDetailsCell"

// MARK:- NOTIFICATIONS Constants
let kNotificationsTitleText = "NOTIFICATIONS"
let kNotificationTableViewCellIdentifier = "NotificationCell"


// MARK:- Validations Message during signup and sign in process

let kMessageFirstNameBlank = "Please enter your first name."
let kMessageLastNameBlank = "Please enter your last name."
let kMessageEmailBlank = "Please enter your email address."
let kMessagePasswordBlank = "Please enter your password."

let kMessageCurrentPasswordBlank = "Please enter your current password."

let kMessageProfileConfirmPasswordBlank = "Please confirm your password."
let kMessageConfirmPasswordBlank = "Please confirm the password."

let kMessagePasswordMatchingToOtherFeilds = "Your password should not match with email id"


let kMessageValidEmail = "Please enter valid email address."

let kMessageValidatePasswords = "The Password and Confirm password fields don't match."
let kMessageProfileValidatePasswords = "New password and confirm password fields don't match."


let kMessageValidatePasswordCharacters = "Password should have minimum of 8 characters."
let kMessageValidatePasswordComplexity = "Your password must contain: 8 to 64 characters, lower case letter, upper case letter, numeric,  special characters \\!  # $ % & ' () * + , - . : ; < > = ? @ [] ^ _  { } | ~"
let kMessageAgreeToTermsAndConditions = "Please agree to terms and conditions."

let kMessageNewPasswordBlank = "Please enter your new password."
let kMessageValidateChangePassword = "New password and old password are same."


// MARK:- ChangePassword Constants
let kChangePasswordTitleText = "CHANGE PASSWORD"
let kCreatePasswordTitleText = "CREATE PASSWORD"
let kChangePawwordCellIdentifer = "changePasswordCell"
let kChangePasswordResponseMessage = "Your password has been changed successfully"

let kMessageAllFieldsAreEmpty = "Please enter all the fields"
let kMessageValidFirstName = "Please enter valid first name. Please use letters(length:1 - 100 characters)."
let kMessageValidLastName = "Please enter valid last name. Please use letters(length:1 - 100 characters)."

let kMessageValidateOldAndNewPasswords = "Old password and New password should not be same."

// MARK:-VerificationController
let kMessageVerificationCodeEmpty = "Please enter valid Verification Code"

// MARK:- FeedbackviewController constants
let kFeedbackTableViewCellIdentifier1 = "FeedbackCellFirst"
let kFeedbackTableViewCellIdentifier2 = "FeedbackCellSecond"
let kMessageFeedbackSubmittedSuccessfuly = "Thank you for providing feedback. Your gesture is appreciated."

// MARK:- ContactUsviewController constants
let kContactUsTableViewCellIdentifier = "ContactUsCell"
let kMessageSubjectBlankCheck = "Please enter subject"
let kMessageMessageBlankCheck = "Please enter message"
let kMessageContactedSuccessfuly = "Thank you for contacting us. We will get back to you on your email address at the earliest."
let kMessageTextViewPlaceHolder = ""


// MARK:- ActivitiesViewController constants
let kBackgroundTableViewColor = UIColor.init(red: 216/255.0, green: 227/255.0, blue: 230/255.0, alpha: 1)
let kActivitiesTableViewCell = "ActivitiesCell"
let kActivitiesTableViewScheduledCell = "ActivitiesCellScheduled"

let kYellowColor = UIColor.init(red: 245/255.0, green: 175/255.0, blue: 55/255.0, alpha: 1.0)
let kBlueColor = UIColor.init(red: 0/255.0, green: 124/255.0, blue: 186/255.0, alpha: 1.0)
let kGreenColor = UIColor.init(red: 76/255.0, green: 175/255.0, blue: 80/255.0, alpha: 1.0)


let kResumeSpaces = "  Resume  "
let kStartSpaces = "  Start  "
let kCompletedSpaces = "  Completed  "
let kInCompletedSpaces = "  Incompleted  "

// MARK:- ResourcesViewController constants
let kResourcesTableViewCell = "ResourcesCell"


// MARK:- StudyDashboardViewController constants
let kWelcomeTableViewCell = "welcomeCell"
let kStudyActivityTableViewCell = "studyActivityCell"
let kPercentageTableViewCell = "percentageCell"
let kActivityTableViewCell = "ActivityCell"
let kStatisticsTableViewCell = "StatisticsCell"
let kTrendTableViewCell = "trendCell"

let kActivityCollectionViewCell = "ActivityCell"
let kStatisticsCollectionViewCell = "StatisticsCell"

let kDarkBlueColor = UIColor.init(red: 0/255.0, green: 124/255.0, blue: 186/255.0, alpha: 1.0)
let kGreyColor = UIColor.init(red: 140/255.0, green: 149/255.0, blue: 163/255.0, alpha: 1.0)

let kDaySpaces = "  DAY  "
let kDay = "Day"
let kMonthSpaces = "  MONTH  "
let kMonth = "MONTH"
let kWeekSpaces = "  WEEK  "
let kWeek = "WEEK"

// MARK:- Eligibility constants

let kMessageForInvalidToken = "Please enter valid enrollment token"

let kMessageValidToken = "Please enter valid token"
let kMessageForMissingStudyId = "Unable to Enroll, Please try again later."

let kMessageInvalidTokenOrIfStudyDoesNotExist = "Sorry, this token is invalid. Please enter a valid token to continue."


// MARK:- StudyHomeMessages
let kMessageForStudyUpcomingState = "This study is an upcoming one and isn't yet open for enrolling participants. Please check back later."
let kMessageForStudyPausedState = "This study has been temporarily paused. Please check back later."
let kMessageForStudyPausedAfterJoiningState = "The study has been temporarily paused. You can participate in activities once it is resumed. Please check back later."
let kMessageForStudyClosedState = "This study has been closed."
let kMessageForStudyWithdrawnState = "Sorry, this study currently does not allow previously enrolled participants to rejoin the study after they have withdrawn from the study. Please check back later or explore other studies"
let kMessageForStudyEnrollingNotAllowed = "Sorry, enrollment for this study has been closed for now. Please check back later or explore other studies you could join."


// MARK:- StudyDashboardViewController segues
let unwindToStudyListDashboard = "unwindToStudyListDashboardIdentifier"


// MARK:- FilterListViewController Segue
let filterListSegue = "filterscreenSegue"

// MARK:- Staging User Details

let kStagingUserEmailId = "aqibm@boston-technology.com"
let kIsStagingUser = "StagingUser"




struct BrandingConstant {
    
    
    static let JoinStudyButtonTitle = "JoinStudyButtonTitle"
    static let ViewConsentButtonTitle = "ViewConsentButtonTitle"
    static let VisitWebsiteButtonTitle = "VisitWebsiteButtonTitle"
    static let ConsentPDF = "ConsentPDF"
    static let LeaveStudy = "LeaveStudy"
    static let LeaveStudyConfirmationText = "LeaveStudyConfirmationText"
    static let WebsiteLink = "WebsiteLink"
    static let WebsiteButtonTitle = "WebsiteButtonTitle"
    static let TermsAndConditionURL = "TermsAndConditionURL"
    static let PrivacyPolicyURL = "PrivacyPolicyURL"
    static let ValidatedTitle = "ValidatedTitle" 
}
