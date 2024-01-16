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

commit;

#### 멘토링정보 등록
start transaction;

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