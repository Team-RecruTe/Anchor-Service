start transaction;
create fulltext index mentoring_title_idx on mentoring (title) with parser ngram;
create fulltext index mentoring_contents_idx on mentoring_detail (contents) with parser ngram;
commit;

#### 회원정보 등록
start transaction;
insert into user (id, email, nickname, create_date, update_date, role, image)
values (1, 'testMentor@test.com', '테스트멘토', now(), now(), 'MENTOR',
        'https://anchor-image-stroage.s3.ap-northeast-2.amazonaws.com/%EA%B3%A0%EC%8A%B4%EB%8F%84%EC%B9%98_1704431943574.jpg'),
       (2, 'testUser@test.com', '테스트유저', now(), now(), 'USER',
        'https://anchor-image-stroage.s3.ap-northeast-2.amazonaws.com/%EA%B3%A0%EC%8A%B4%EB%8F%84%EC%B9%98_1704431943574.jpg'),
       (3, 'kks4517@naver.com', '김기홍', now(), now(), 'MENTOR',
        'https://phinf.pstatic.net/contact/20200829_116/1598709994054RKSO7_JPEG/image.jpg');;
commit;

#### 멘토 소개글 등록
start transaction;
insert into mentor_introduction(id, contents, create_date, update_date)
values (1, '테스트 소개글입니다. 안녕안녕', now(), now()),
       (2, '테스트소개글 입니다. blanc is best beer', now(), now());
commit;

#### 멘토정보 등록
start transaction;
insert into mentor(id, company_email, account_name, account_number, bank_name, career, create_date,
                   update_date, user_id, mentor_introduction_id)
values (1, 'testCompany@test.com', '기홍', '1000002663004', '국민은행', 'MIDDLE', now(), now(), 1, 1),
       (2, 'team.recrute1602@gmail.com', '기홍', '3020000010088', '농협은행', 'JUNIOR', now(), now(), 3, 2);
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
       (10, 1, '14:00', '21:00', 'OPEN', 'SUNDAY', now(), now()),
       (11, 1, '13:00', '15:00', 'OPEN', 'THURSDAY', now(), now());
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
       (6, 'ㅎㅇㅎㅇ 테스트용4', now(), now()),
       (7, 'ㅎㅇㅎㅇ 테스트용4', now(), now()),
       (8, 'ㅎㅇㅎㅇ 테스트용4', now(), now()),
       (9, 'ㅎㅇㅎㅇ 테스트용4', now(), now()),
       (10, 'ㅎㅇㅎㅇ 테스트용4', now(), now()),
       (11, 'ㅎㅇㅎㅇ 테스트용4', now(), now()),
       (12, 'ㅎㅇㅎㅇ 테스트용4', now(), now()),
       (13, 'ㅎㅇㅎㅇ 테스트용4', now(), now()),
       (14, 'ㅎㅇㅎㅇ 테스트용4', now(), now()),
       (15, 'ㅎㅇㅎㅇ 테스트용4', now(), now()),
       (16, 'ㅎㅇㅎㅇ 테스트용4', now(), now()),
       (17, 'ㅎㅇㅎㅇ 테스트용4', now(), now()),
       (18, 'ㅎㅇㅎㅇ 테스트용4', now(), now()),
       (19, 'ㅎㅇㅎㅇ 테스트용4', now(), now()),
       (20, 'ㅎㅇㅎㅇ 테스트용4', now(), now()),
       (21, 'ㅎㅇㅎㅇ 테스트용4', now(), now()),
       (22, 'ㅎㅇㅎㅇ 테스트용4', now(), now());
commit;

#### 멘토링정보 등록
start transaction;
insert into mentoring (id, title, duration_time, cost, create_date, update_date, mentor_id,
                       mentoring_detail_id, total_application_number)
values (1, '개발자 커리어, IT 업무, 커리어, 매니징, 백엔드개발, 이력서, 모의면접 등 멘토링', '1h30m', 10000, now(), now(), 1, 1, 150),
       (2, '단테의 프론트엔드 / 개발자 커리어 / 취업, 이직 / 학습 멘토링', '1h30m', 10000, now(), now(), 1, 2, 120),
       (3, '금융권 개발자 취업 / 물경력 이직 멘토링', '1h30m', 10000, now(), now(), 1, 3, 32),
       (4, '실리콘밸리와 한국을 넘나드는 29년 개발자/리더/CTO', '1h30m', 10000, now(), now(), 1, 4, 25),
       (5, 'SI에서 유명 스타트업까지 2년 만에 성장한 백엔드 엔지니어의 멘토링', '1h30m', 10000, now(), now(), 1, 5, 14),
       (6, '대학교 생활 멘토링 / 대외활동 / AI 관련분야 / 대학원 진로 관련', '1h30m', 10000, now(), now(), 1, 6, 5),
       (7, '테스트제목1', '1h30m', 10000, now(), now(), 1, 7, 6),
       (8, '테스트제목2', '1h30m', 10000, now(), now(), 1, 8, 7),
       (9, '테스트제목3', '1h30m', 10000, now(), now(), 1, 9, 8),
       (10, '테스트제목4', '1h30m', 10000, now(), now(), 1, 10, 1),
       (11, '테스트제목5', '1h30m', 10000, now(), now(), 1, 11, 0),
       (12, '테스트제목6', '1h30m', 10000, now(), now(), 1, 12, 2),
       (13, '테스트제목7', '1h30m', 10000, now(), now(), 1, 13, 6),
       (14, '테스트제목8', '1h30m', 10000, now(), now(), 1, 14, 36),
       (15, '테스트제목9', '1h30m', 10000, now(), now(), 1, 15, 34),
       (16, '테스트제목10', '1h30m', 10000, now(), now(), 1, 16, 10),
       (17, '테스트제목11', '1h30m', 10000, now(), now(), 1, 17, 15),
       (18, '테스트제목12', '1h30m', 10000, now(), now(), 1, 18, 2),
       (19, '테스트제목13', '1h30m', 10000, now(), now(), 1, 19, 7),
       (20, '테스트제목14', '1h30m', 10000, now(), now(), 2, 20, 6),
       (21, '테스트제목15', '1h30m', 10000, now(), now(), 2, 21, 5),
       (22, '테스트제목16', '1h30m', 10000, now(), now(), 2, 22, 5);
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
       (10, '파이썬', now(), now(), 6),
       (11, '루비', now(), now(), 7),
       (12, '스칼라', now(), now(), 8),
       (13, '서버', now(), now(), 9),
       (14, '유니티', now(), now(), 10),
       (15, 'Vue', now(), now(), 11),
       (16, 'React', now(), now(), 12),
       (17, '풀스택', now(), now(), 13),
       (18, '테스트', now(), now(), 14),
       (19, '백엔드', now(), now(), 15),
       (20, '자바', now(), now(), 16),
       (21, '프론트엔드', now(), now(), 17),
       (22, 'AI', now(), now(), 18),
       (23, '해외취업', now(), now(), 19),
       (24, '유학', now(), now(), 20),
       (25, '미국', now(), now(), 21),
       (26, '미국', now(), now(), 22);
commit;

#### 멘토링 결제내역 등록
start transaction;
insert into payment(id, create_date, update_date, amount, cancel_amount, payment_status, order_uid, imp_uid,
                    merchant_uid)
values (1, '2023-12-01 11:00:00', now(), 10000, 0, 'SUCCESS', 'anchor_12347547', 'imp_253462234', 'toss_1234345'),
       (2, '2023-12-02 15:00:00', now(), 10000, 0, 'SUCCESS', 'anchor_12567857', 'imp_253462765', 'toss_123735467'),
       (3, '2023-12-04 10:00:00', now(), 10000, 0, 'SUCCESS', 'anchor_1224727', 'imp_253462867', 'toss_12312315'),
       (4, '2023-12-06 15:00:00', now(), 10000, 0, 'SUCCESS', 'anchor_127645767', 'imp_253462967', 'toss_14567432'),
       (5, '2023-12-08 15:00:00', now(), 10000, 0, 'SUCCESS', 'anchor_1234867457', 'imp_2534621234', 'toss_12334577'),
       (6, '2023-12-10 15:00:00', now(), 10000, 0, 'SUCCESS', 'anchor_12568247', 'imp_2534623523', 'toss_1286759'),
       (7, '2023-12-15 15:00:00', now(), 10000, 0, 'SUCCESS', 'anchor_876344567', 'imp_253124548', 'toss_1234435'),
       (8, '2023-12-19 12:00:00', now(), 10000, 0, 'SUCCESS', 'anchor_1856794', 'imp_256435341', 'toss_34754677'),
       (9, now(), now(), 10000, 0, 'SUCCESS', 'anchor_12344567', 'imp_253462345', 'toss_12344567'),
       (10, now(), now(), 10000, 0, 'SUCCESS', 'anchor_34562346', 'imp_4563244', 'toss_34562346'),
       (11, now(), now(), 10000, 0, 'SUCCESS', 'anchor_34562347', 'imp_4563245', 'toss_34562347'),
       (12, now(), now(), 10000, 0, 'SUCCESS', 'anchor_34562348', 'imp_4563246', 'toss_34562348'),
       (13, now(), now(), 10000, 0, 'SUCCESS', 'anchor_34562349', 'imp_4563247', 'toss_34562349'),
       (14, now(), now(), 10000, 0, 'SUCCESS', 'anchor_34562350', 'imp_4563248', 'toss_34562350'),
       (15, now(), now(), 10000, 0, 'SUCCESS', 'anchor_34562351', 'imp_4563249', 'toss_34562351'),
       (16, now(), now(), 10000, 0, 'SUCCESS', 'anchor_34562352', 'imp_4563250', 'toss_34562352'),
       (17, now(), now(), 10000, 0, 'SUCCESS', 'anchor_34562353', 'imp_4563251', 'toss_34562353'),
       (18, now(), now(), 10000, 0, 'SUCCESS', 'anchor_34562354', 'imp_4563252', 'toss_34562354'),
       (19, now(), now(), 10000, 0, 'SUCCESS', 'anchor_34562355', 'imp_4563253', 'toss_34562355'),
       (20, now(), now(), 10000, 0, 'SUCCESS', 'anchor_34562356', 'imp_4563254', 'toss_34562356'),
       (21, now(), now(), 10000, 0, 'SUCCESS', 'anchor_34562357', 'imp_4563255', 'toss_34562357'),
       (22, now(), now(), 10000, 0, 'SUCCESS', 'anchor_5678427', 'imp_7323455', 'toss_5678427'),
       (23, now(), now(), 10000, 0, 'SUCCESS', 'anchor_8546734', 'imp_56784343', 'toss_8546734');
commit;

#### 멘토링 신청내역 등록
start transaction;
insert into mentoring_application(id, start_date_time, end_date_time, create_date, update_date,
                                  mentoring_status, user_id, mentoring_id, payment_id)
values (1, '2023-12-01 11:00:00', '2023-12-01 12:30:00', now(), now(), 'COMPLETE', 2, 1, 1),
       (2, '2023-12-02 15:00:00', '2023-12-02 16:30:00', now(), now(), 'COMPLETE', 2, 1, 2),
       (3, '2023-12-04 10:00:00', '2023-12-04 11:30:00', now(), now(), 'COMPLETE', 2, 1, 3),
       (4, '2023-12-06 15:00:00', '2023-12-06 16:30:00', now(), now(), 'COMPLETE', 2, 1, 4),
       (5, '2023-12-08 15:00:00', '2023-12-08 16:30:00', now(), now(), 'COMPLETE', 2, 1, 5),
       (6, '2023-12-10 15:00:00', '2023-12-10 16:30:00', now(), now(), 'COMPLETE', 2, 1, 6),
       (7, '2023-12-15 15:00:00', '2023-12-15 16:30:00', now(), now(), 'COMPLETE', 2, 1, 7),
       (8, '2023-12-19 12:00:00', '2023-12-19 13:30:00', now(), now(), 'COMPLETE', 2, 1, 8),
       (9, '2023-12-21 15:00:00', '2023-12-21 16:30:00', now(), now(), 'COMPLETE', 2, 1, 9),
       (10, '2024-01-03 13:00:00', '2024-01-03 14:30:00', now(), now(), 'COMPLETE', 2, 1, 10),
       (11, '2024-01-07 13:00:00', '2024-01-07 14:30:00', now(), now(), 'COMPLETE', 2, 1, 11),
       (12, '2024-01-08 13:00:00', '2024-01-08 14:30:00', now(), now(), 'COMPLETE', 2, 1, 12),
       (13, '2024-01-10 16:00:00', '2024-01-10 17:30:00', now(), now(), 'COMPLETE', 2, 1, 13),
       (14, '2024-01-10 13:00:00', '2024-01-10 14:30:00', now(), now(), 'COMPLETE', 2, 1, 14),
       (15, '2024-01-11 13:00:00', '2024-01-11 14:30:00', now(), now(), 'COMPLETE', 2, 1, 15),
       (16, '2024-01-13 08:00:00', '2024-01-13 09:30:00', now(), now(), 'COMPLETE', 2, 1, 16),
       (17, '2024-01-13 13:00:00', '2024-01-13 14:30:00', now(), now(), 'COMPLETE', 2, 1, 17),
       (18, '2024-01-13 19:00:00', '2024-01-13 20:30:00', now(), now(), 'COMPLETE', 2, 1, 18),
       (19, '2024-01-15 13:00:00', '2024-01-15 14:30:00', now(), now(), 'COMPLETE', 2, 20, 19),
       (20, '2024-01-16 13:00:00', '2024-01-16 14:30:00', now(), now(), 'COMPLETE', 2, 21, 20),
       (21, '2024-02-07 13:00:00', '2024-02-07 14:30:00', now(), now(), 'COMPLETE', 2, 22, 21),
       (22, '2024-01-24 13:00:00', '2024-01-24 14:30:00', now(), now(), 'WAITING', 3, 1, 22),
       (23, '2024-01-25 13:00:00', '2024-01-25 14:30:00', now(), now(), 'CANCELLED', 3, 1, 23);
commit;

#### 정산 데이터 등록
start transaction;
insert into payup(id, create_date, update_date, amount, payup_status, payup_date_time, mentor_id, payment_id)
values (1, '2023-12-01 15:00:00', now(), 10000, 'WAITING', '2024-01-05 15:00:00', 1, 1),
       (2, '2023-12-03 13:00:00', now(), 10000, 'WAITING', '2024-01-05 15:00:00', 1, 2),
       (3, '2023-12-04 15:00:00', now(), 10000, 'WAITING', '2024-01-05 15:00:00', 1, 3),
       (4, '2023-12-06 13:00:00', now(), 10000, 'WAITING', '2024-01-05 15:00:00', 1, 4),
       (5, '2023-12-08 21:00:00', now(), 10000, 'WAITING', '2024-01-05 15:00:00', 1, 5),
       (6, '2023-12-10 23:00:00', now(), 10000, 'WAITING', '2024-01-05 15:00:00', 1, 6),
       (7, '2023-12-16 01:00:00', now(), 10000, 'WAITING', '2024-01-05 15:00:00', 1, 7),
       (8, '2023-12-19 14:00:00', now(), 10000, 'WAITING', '2024-01-05 15:00:00', 1, 8),
       (9, '2023-12-22 15:00:00', now(), 10000, 'WAITING', '2024-01-05 15:00:00', 1, 9),
       (10, now(), now(), 10000, 'WAITING', '2024-01-10 18:00:00', 1, 10),
       (11, now(), now(), 10000, 'WAITING', '2024-01-10 18:00:00', 1, 11),
       (12, now(), now(), 10000, 'WAITING', '2024-01-10 18:00:00', 1, 12),
       (13, now(), now(), 10000, 'WAITING', '2024-01-10 18:00:00', 1, 13),
       (14, now(), now(), 10000, 'WAITING', '2024-01-10 18:00:00', 1, 14),
       (15, now(), now(), 10000, 'WAITING', '2024-01-10 18:00:00', 1, 15),
       (16, now(), now(), 10000, 'WAITING', '2024-01-10 18:00:00', 1, 16),
       (17, now(), now(), 10000, 'WAITING', '2024-01-10 18:00:00', 1, 17),
       (18, now(), now(), 10000, 'WAITING', '2024-01-10 18:00:00', 1, 18),
       (19, now(), now(), 10000, 'WAITING', '2024-01-10 18:00:00', 2, 19),
       (20, now(), now(), 10000, 'WAITING', '2024-01-10 18:00:00', 2, 20),
       (21, now(), now(), 10000, 'WAITING', '2024-01-10 18:00:00', 2, 21);
commit;