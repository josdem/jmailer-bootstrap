package com.jos.dem.jmailer.service.impl

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service

import com.jos.dem.jmailer.integration.MailService
import com.jos.dem.jmailer.service.NotificationService
import com.jos.dem.jmailer.command.MessageCommand
import com.jos.dem.jmailer.constant.ApplicationConstants

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory

@Service
class NotificationServiceImpl implements NotificationService {

  @Autowired
  MailService mailService
  @Autowired
  Properties properties

  private Log log = LogFactory.getLog(getClass())

  @Override
  void sendNotification(MessageCommand messageCommand) {
    log.info "messageCommand: ${messageCommand.dump()}"
    def (sender, subject, templateName) = obtainSubjectAndResourceToSendNotification(messageCommand)
    def data = [sender:sender, subject:subject, templateName:templateName, bean:messageCommand]
    sendNotificationWithData(data)
  }

  def obtainSubjectAndResourceToSendNotification(MessageCommand messageCommand){
    String templateKey = "${messageCommand.type.toString()}_PATH"
    String subjectKey = "${messageCommand.type.toString()}_SUBJECT"

    String templateName = properties.getProperty(templateKey)
    String sender = properties.getProperty(ApplicationConstants.SENDER)
    String subject = properties.getProperty(subjectKey)

    log.info("Sending email with subject: " + subject)

    [sender, subject, templateName]
  }

  void sendNotificationWithData(emailData){
    String templateName = emailData.templateName
    def bean = emailData.bean
    String sender = emailData.sender
    String subject = emailData.subject
    mailService.sendMailWithTemplate(bean.email, sender, bean.properties, subject, templateName)
  }
}
