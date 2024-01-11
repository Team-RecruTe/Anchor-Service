#### 회원정보 등록
start transaction;
insert into user (id, email, nickname, create_date, update_date)
values (1, 'testMentor@test.com', '테스트멘토', now(), now());
insert into user (id, email, nickname, create_date, update_date)
values (2, 'testUser@test.com', '테스트유저', now(), now());
commit;

#### 멘토정보 등록
start transaction;
insert into mentor(id, company_email, account_name, account_number, bank_name, career, create_date,
                   update_date, user_id)
values (1, 'testCompany@test.com', '테스트계좌명', '12345678', '테스트은행', 'MIDDLE', now(), now(), 1);
commit;

#### 멘토링 상세내용 등록
start transaction;
insert into mentoring_detail (id, contents, create_date, update_date)
values (1, '테스트내용1', now(), now());
commit;
start transaction;
insert into mentoring_detail (id, contents, create_date, update_date)
values (2, '테스트내용2', now(), now());
commit;
start transaction;
insert into mentoring_detail (id, contents, create_date, update_date)
values (3, '테스트내용3', now(), now());
commit;
start transaction;
insert into mentoring_detail (id, contents, create_date, update_date)
values (4, '테스트내용4', now(), now());
commit;
start transaction;
insert into mentoring_detail (id, contents, create_date, update_date)
values (5, '테스트내용5', now(), now());
commit;

#### 멘토링정보 등록
start transaction;
insert into mentoring (id, title, duration_time, cost, create_date, update_date, mentor_id,
                       total_application_number, mentoring_detail_id)
values (1, '테스트제목', '1시간', 10000, now(), now(), 1, 10, 1);
commit;
start transaction;
insert into mentoring (id, title, duration_time, cost, create_date, update_date, mentor_id,
                       total_application_number, mentoring_detail_id)
values (2, '테스트제목2', '1시간', 10000, now(), now(), 1, 12, 2);
commit;
start transaction;
insert into mentoring (id, title, duration_time, cost, create_date, update_date, mentor_id,
                       total_application_number, mentoring_detail_id)
values (3, '테스트제목3', '1시간', 10000, now(), now(), 1, 6, 3);
commit;
start transaction;
insert into mentoring (id, title, duration_time, cost, create_date, update_date, mentor_id,
                       total_application_number, mentoring_detail_id)
values (4, '테스트제목4', '1시간', 10000, now(), now(), 1, 4, 4);
commit;
start transaction;
insert into mentoring (id, title, duration_time, cost, create_date, update_date, mentor_id,
                       total_application_number, mentoring_detail_id)
values (5, '테스트제목5', '1시간', 10000, now(), now(), 1, 16, 5);
commit;

#### 멘토링 태그 등록
start transaction;
insert into mentoring_tag (id, tag, create_date, update_date, mentoring_id)
values (1, '백엔드', now(), now(), 1);

insert into mentoring_tag (id, tag, create_date, update_date, mentoring_id)
values (2, '자바', now(), now(), 1);
commit;

#### 멘토링 신청내역 등록
start transaction;
insert into mentoring_application(id, start_date_time, end_date_time, create_date, update_date,
                                  mentoring_status, user_id, mentoring_id)
values (1, '2024-01-01 00:00:00', '2024-01-01 23:59:59', now(), now(), 'APPROVAL', 2, 1);

insert into mentoring_application(id, start_date_time, end_date_time, create_date, update_date,
                                  mentoring_status, user_id, mentoring_id)
values (2, '2024-01-02 13:00:00', '2024-01-02 17:00:00', now(), now(), 'WAITING', 2, 1);
commit;