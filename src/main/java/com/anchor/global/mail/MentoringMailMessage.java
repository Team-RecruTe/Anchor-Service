package com.anchor.global.mail;

import com.anchor.domain.notification.domain.ReceiverType;
import java.time.LocalDateTime;

public class MentoringMailMessage extends MailMessage {

  public MentoringMailMessage(String title, String content, String receiver) {
    super(title, content, receiver);
  }

  public static class MentoringMailMessageBuilder {

    private String title;
    private String mentoringTitle;
    private String receiverEmail;
    private String opponentEmail;
    private String opponentNickName;
    private LocalDateTime startDateTime;
    private ReceiverType receiverType;

    public MentoringMailMessageBuilder title(String title) {
      this.title = title;
      return this;
    }

    public MentoringMailMessageBuilder receiverEmail(String receiverEmail) {
      this.receiverEmail = receiverEmail;
      return this;
    }

    public MentoringMailMessageBuilder opponentEmail(String opponentEmail) {
      this.opponentEmail = opponentEmail;
      return this;
    }

    public MentoringMailMessageBuilder opponentNickName(String opponentNickName) {
      this.opponentNickName = opponentNickName;
      return this;
    }

    public MentoringMailMessageBuilder startDateTime(LocalDateTime startDateTime) {
      this.startDateTime = startDateTime;
      return this;
    }

    public MentoringMailMessageBuilder mentoringTitle(String mentoringTitle) {
      this.mentoringTitle = mentoringTitle;
      return this;
    }

    public MentoringMailMessageBuilder receiverType(ReceiverType receiverType) {
      this.receiverType = receiverType;
      return this;
    }

    public MentoringMailMessage build() {
      return new MentoringMailMessage(title, getContents(), receiverEmail);
    }

    private String getContents() {
      String subTitle = getSubTitle(opponentNickName);
      String reservationInfo = getReservationInfo(startDateTime);
      String link = getLink(receiverType);

      StringBuilder sb = new StringBuilder();
      return sb.append("<h1>" + subTitle + "</h1>\n")
          .append("<h3>상세 정보</h3")
          .append("<span>멘토링 제목: " + mentoringTitle + "<span>\n")
          .append("<span>멘티 이메일: " + opponentEmail + "</span>\n")
          .append("<span>예약일자: " + reservationInfo + "</span>\n")
          .append("<a href=\"" + link + "\"> [승인하러 가기] </a>")
          .toString();
    }

    private String getSubTitle(String nickName) {
      return nickName + " 멘티님께서 멘토링을 신청하셨습니다.";
    }

    private String getReservationInfo(LocalDateTime startDateTime) {
      int year = startDateTime.getYear();
      int month = startDateTime.getMonth()
          .getValue();
      int day = startDateTime.getDayOfMonth();
      int hour = startDateTime.getHour();
      int minute = startDateTime.getMinute();
      String dayOfWeek = startDateTime.getDayOfWeek()
          .toString();

      return year + "년 " + month + "월 " + day + "일 " + hour + "시 " + minute + "분 " + "(" + dayOfWeek + ")";
    }

    private String getLink(ReceiverType receiverType) {
      return receiverType.getPath() + "/me/applied-mentorings";
    }

  }

}
