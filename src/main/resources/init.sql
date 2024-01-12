start transaction;
create fulltext index mentoring_title_idx on mentoring (title) with parser ngram;
create fulltext index mentoring_contents_idx on mentoring_detail (contents) with parser ngram;
commit;

#### 회원정보 등록
start transaction;
insert into user (id, email, nickname, create_date, update_date, image)
values (1, 'testMentor@test.com', '테스트멘토', now(), now(),
        'https://anchor-image-stroage.s3.ap-northeast-2.amazonaws.com/%EA%B3%A0%EC%8A%B4%EB%8F%84%EC%B9%98_1704431943574.jpg'),
       (2, 'testUser@test.com', '테스트유저', now(), now(),
        'https://anchor-image-stroage.s3.ap-northeast-2.amazonaws.com/%EA%B3%A0%EC%8A%B4%EB%8F%84%EC%B9%98_1704431943574.jpg');
commit;

#### 멘토정보 등록
start transaction;
insert into mentor(id, company_email, account_name, account_number, bank_name, career, create_date,
                   update_date, user_id)
values (1, 'testCompany@test.com', '테스트계좌명', '12345678', '테스트은행', 'MIDDLE', now(), now(), 1);
commit;

### 멘토 스케줄 등록
start transaction;
insert into mentor_schedule(id, mentor_id, open_time, close_time, active_status, day_of_week, create_date, update_date)
values (1, 1, '09:00', '12:00', 'OPEN', 'MONDAY', now(), now()),
       (2, 1, '19:00', '23:00', 'OPEN', 'MONDAY', now(), now()),
       (3, 1, '19:00', '23:00', 'OPEN', 'TUESDAY', now(), now()),
       (4, 1, '19:00', '23:00', 'OPEN', 'WEDNESDAY', now(), now()),
       (5, 1, '19:00', '23:00', 'CLOSE', 'THURSDAY', now(), now()),
       (6, 1, '19:00', '23:00', 'OPEN', 'FRIDAY', now(), now()),
       (7, 1, '09:00', '12:00', 'OPEN', 'SATURDAY', now(), now()),
       (8, 1, '14:00', '21:00', 'OPEN', 'SATURDAY', now(), now()),
       (9, 1, '09:00', '12:00', 'OPEN', 'SUNDAY', now(), now()),
       (10, 1, '14:00', '21:00', 'OPEN', 'SUNDAY', now(), now());
commit;

#### 멘토링 상세내용 등록
start transaction;
insert into mentoring_detail (id, contents, create_date, update_date)
values (1, '저는 IT 대기업이라 불리는 회사에서 총 10년 이상 재직하며 여러 팀에 소속되어 많은 프로젝트 개발에 참여 했고 최근 4년간은 스타트업 2개 회사에서 리더 포지션으로 업무를 진행한 경험을 가지고 있습니다.

제 경험을 나누고 솔직한 이야기나 고민을 나눌 사람이 필요하다면 저를 찾아 주세요.

● 멘토링 분야
     -  Java 개발 Backend
     -  개발자 커리어에 대한 고민, 이직에 대한 준비/고민
     -  조직 운영, 매니징에 대한 경험 공유
     -  프로젝트 진행, IT 직무 업무 진행에 대한 부분
     -  모의 면접과 이력서 검토도 해드릴 수 있습니다.

● 진행방식
     -  Google Meet을 이용한 1:1(1:N가능) 화상회의 방식
     -  예약이 되면 기재하신 연락처를 통해 접속주소를 알려드립니다.
     -  상호 원활한 대화를 위해 " 이어폰과 마이크 혹은 헤드셋" 을 준비해 주세요!
     -  마이크가 없을 경우 채팅창으로 대화를 나눠야하는데 시간 Loss가 발생할 수 있습니다.

● 준 비 물
     -  질문할 내용 list
     -  솔직하게 이야기를 나눌 마음 :)
     -  마이크+스피커 혹은 헤드셋 (상호 원활한 커뮤니케이션을 위해 필수)

저와 이런 저런 이야기를 나눠보고 싶은 분들 누구나 환영합니다!
정답은 아니겠지만 한 사람의 경험과 생각을 듣고 참고와 도움이 되시길 바라겠습니다.', now(), now()),

       (2, '👋🏼 자기소개
저도 최근 취업 시장을 경험 했고, 비전공자였고, 부트캠프(국비)를 다녔으며, 나의 실력에 대해 아직도 부족하다고 생각합니다.

취업 준비를 할 때, 많이 고민되고 개발자하려고 했던 것을 후회하기도 했습니다.

혹시나 제가 했던 고민들을 똑같이 하고 계신 분들이 있을 것 같아서 이렇게 멘토링을 시작해보려합니다.

🔎 가능한 멘토링 분야

이력서 피드백, 모의 면접, 코드 리뷰 등을 해드릴 수 있으나 저도 실력이 많이 부족하기 때문에 이런 것보다는 현재 갖고 있는 고민에 대해서 상담하는 시간이 됐으면 좋겠습니다.
🏃🏻‍♀️ 진행 방식

Zoom 으로 1대1 진행할 예정입니다.
30분이라 했지만, 추가 진행 가능 합니다. (추가 납입 x)
📚 멘토링에 필요한 준비

만약에 신청하실 분은 무한의 시간이 아니기에 미리 고민거리를 정리해서 오시면 더 알찬 시간이 될 것 같습니다.
저의 도움이 여러분의 취준시간을 하루라도 줄이고, 조금이라도 더 좋을 곳에 합격할 수 있도록 도움이 되면 좋을 것 같습니다.', now(), now()),

       (3, 'ㅎㅇㅎㅇ 테스트용3', now(), now()),
       (4, 'ㅎㅇㅎㅇ 테스트용4', now(), now()),
       (5, 'ㅎㅇㅎㅇ 테스트용4', now(), now()),
       (6, 'ㅎㅇㅎㅇ 테스트용4', now(), now());
commit;

#### 멘토링정보 등록
start transaction;
insert into mentoring (id, title, duration_time, cost, create_date, update_date, mentor_id,
                       mentoring_detail_id, total_application_number)
values (1, '개발자 커리어, IT 업무, 커리어, 매니징, 백엔드개발, 이력서, 모의면접 등 멘토링', '1h30m', 10000, now(), now(), 1, 1, 20),
       (2, '단테의 프론트엔드 / 개발자 커리어 / 취업, 이직 / 학습 멘토링', '1h30m', 10000, now(), now(), 1, 2, 18),
       (3, '금융권 개발자 취업 / 물경력 이직 멘토링', '1h30m', 10000, now(), now(), 1, 3, 19),
       (4, '실리콘밸리와 한국을 넘나드는 29년 개발자/리더/CTO', '1h30m', 10000, now(), now(), 1, 4, 25),
       (5, 'SI에서 유명 스타트업까지 2년 만에 성장한 백엔드 엔지니어의 멘토링', '1h30m', 10000, now(), now(), 1, 5, 14),
       (6, '대학교 생활 멘토링 / 대외활동 / AI 관련분야 / 대학원 진로 관련', '1h30m', 10000, now(), now(), 1, 6, 5);
commit;

#### 멘토링 태그 등록
start transaction;
insert into mentoring_tag (id, tag, create_date, update_date, mentoring_id)
values (1, '백엔드', now(), now(), 1),
       (2, '자바', now(), now(), 1),
       (3, '프론트엔드', now(), now(), 2),
       (4, '리액트', now(), now(), 2),
       (5, 'DBA', now(), now(), 3),
       (6, '데브옵스', now(), now(), 3),
       (7, '자바', now(), now(), 4),
       (8, '백엔드', now(), now(), 4),
       (9, 'AI', now(), now(), 5),
       (10, '파이썬', now(), now(), 5);
commit;

#### 멘토링 결제내역 등록
start transaction;
insert into payment(id, create_date, update_date, amount, cancel_amount, payment_status, order_uid, imp_uid,
                    merchant_uid)
values (1, now(), now(), 10000, 0, 'SUCCESS', 'anchor_12344567', 'imp_253462345', 'toss_12344567'),
       (2, now(), now(), 10000, 0, 'SUCCESS', 'anchor_34562346', 'imp_4563244', 'toss_34562346'),
       (3, now(), now(), 10000, 0, 'SUCCESS', 'anchor_34562347', 'imp_4563245', 'toss_34562347'),
       (4, now(), now(), 10000, 0, 'SUCCESS', 'anchor_34562348', 'imp_4563246', 'toss_34562348'),
       (5, now(), now(), 10000, 0, 'SUCCESS', 'anchor_34562349', 'imp_4563247', 'toss_34562349'),
       (6, now(), now(), 10000, 0, 'SUCCESS', 'anchor_34562350', 'imp_4563248', 'toss_34562350'),
       (7, now(), now(), 10000, 0, 'SUCCESS', 'anchor_34562351', 'imp_4563249', 'toss_34562351'),
       (8, now(), now(), 10000, 0, 'SUCCESS', 'anchor_34562352', 'imp_4563250', 'toss_34562352'),
       (9, now(), now(), 10000, 0, 'SUCCESS', 'anchor_34562353', 'imp_4563251', 'toss_34562353'),
       (10, now(), now(), 10000, 0, 'SUCCESS', 'anchor_34562354', 'imp_4563252', 'toss_34562354'),
       (11, now(), now(), 10000, 0, 'SUCCESS', 'anchor_34562355', 'imp_4563253', 'toss_34562355'),
       (12, now(), now(), 10000, 0, 'SUCCESS', 'anchor_34562356', 'imp_4563254', 'toss_34562356'),
       (13, now(), now(), 10000, 0, 'SUCCESS', 'anchor_34562357', 'imp_4563255', 'toss_34562357');
commit;

#### 멘토링 신청내역 등록
start transaction;
insert into mentoring_application(id, start_date_time, end_date_time, create_date, update_date,
                                  mentoring_status, user_id, mentoring_id, payment_id)
values (1, '2024-01-03 15:00:00', '2024-01-03 16:30:00', now(), now(), 'COMPLETE', 2, 1, 1),
       (2, '2024-01-06 13:00:00', '2024-01-06 14:30:00', now(), now(), 'CANCELLED', 2, 1, 2),
       (3, '2024-01-07 13:00:00', '2024-01-07 14:30:00', now(), now(), 'CANCELLED', 2, 1, 3),
       (4, '2024-01-08 13:00:00', '2024-01-08 14:30:00', now(), now(), 'CANCELLED', 2, 1, 4),
       (5, '2024-01-09 13:00:00', '2024-01-09 14:30:00', now(), now(), 'APPROVAL', 2, 1, 5),
       (6, '2024-01-10 13:00:00', '2024-01-10 14:30:00', now(), now(), 'APPROVAL', 2, 1, 6),
       (7, '2024-01-11 13:00:00', '2024-01-11 14:30:00', now(), now(), 'APPROVAL', 2, 1, 7),
       (8, '2024-01-12 13:00:00', '2024-01-12 14:30:00', now(), now(), 'APPROVAL', 2, 1, 8),
       (9, '2024-01-13 13:00:00', '2024-01-13 14:30:00', now(), now(), 'APPROVAL', 2, 1, 9),
       (10, '2024-01-14 13:00:00', '2024-01-14 14:30:00', now(), now(), 'WAITING', 2, 1, 10),
       (11, '2024-01-15 13:00:00', '2024-01-15 14:30:00', now(), now(), 'WAITING', 2, 1, 11),
       (12, '2024-01-16 13:00:00', '2024-01-16 14:30:00', now(), now(), 'WAITING', 2, 1, 12),
       (13, '2024-01-17 13:00:00', '2024-01-17 14:30:00', now(), now(), 'WAITING', 2, 1, 13);
commit;