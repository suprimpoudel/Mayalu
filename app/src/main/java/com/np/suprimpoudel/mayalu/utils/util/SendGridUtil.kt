package com.np.suprimpoudel.mayalu.utils.util

import com.np.suprimpoudel.mayalu.features.shared.helper.EmailTemplate
import com.np.suprimpoudel.mayalu.utils.constants.APIConstants
import com.np.suprimpoudel.mayalu.utils.constants.Constants
import uk.co.jakebreen.sendgridandroid.SendGrid
import uk.co.jakebreen.sendgridandroid.SendGridMail
import uk.co.jakebreen.sendgridandroid.SendGridResponse
import uk.co.jakebreen.sendgridandroid.SendTask

//fun sendVerificationMail(recipientEmail: String?, otpCode: Int): Boolean {
//    val response = recipientEmail?.let {
//        sendMail(
//            it,
//            Constants.VERIFY_MAIL_SUBJECT,
//            EmailTemplate.getWelcomeUserTemplate(otpCode.toString())
//        )
//    }
//
//    if (response?.isSuccessful == true) {
//        return true
//    }
//    return false
//}

fun sendMail(recipient: String, subject: String, template: String): SendGridResponse {

    val sendGrid: SendGrid =
        SendGrid.create(APIConstants.SENDGRID_API)

    val mail = SendGridMail()

    mail.addRecipient(recipient, "")
    mail.setFrom(Constants.SENDER_EMAIL, Constants.NICKNAME)
    mail.setSubject(subject)
    mail.setHtmlContent(template)

    val task = SendTask(sendGrid, mail)
    return task.execute().get()
}